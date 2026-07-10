#!/bin/bash
set -Eeuo pipefail

shutdown() {
    echo "[todoapp] Beende Dienste..."
    [ -n "${NGINX_PID:-}" ] && kill "$NGINX_PID" 2>/dev/null
    [ -n "${BACKEND_PID:-}" ] && kill "$BACKEND_PID" 2>/dev/null
    if [ -n "${PG_PID:-}" ]; then
        gosu postgres "$PGBIN/pg_ctl" -D "$PGDATA" -m fast stop 2>/dev/null
    fi
    exit 0
}
trap shutdown SIGTERM SIGINT

OPTIONS_FILE=/data/options.json
DB_NAME=$(jq -r '.db_name' "$OPTIONS_FILE")
DB_USER=$(jq -r '.db_user' "$OPTIONS_FILE")
DB_PASSWORD=$(jq -r '.db_password' "$OPTIONS_FILE")
JWT_SECRET=$(jq -r '.jwt_secret' "$OPTIONS_FILE")
CORS_ALLOWED_ORIGINS=$(jq -r '.cors_allowed_origins // ""' "$OPTIONS_FILE")

PGDATA=/data/pgdata
PGBIN=$(dirname "$(find /usr/lib/postgresql -maxdepth 3 -name initdb | head -n1)")

if [ ! -s "$PGDATA/PG_VERSION" ]; then
    echo "[todoapp] Initialisiere PostgreSQL-Datenverzeichnis..."
    install -d -o postgres -g postgres "$PGDATA"
    gosu postgres "$PGBIN/initdb" -D "$PGDATA" >/tmp/initdb.log 2>&1
fi

echo "[todoapp] Starte PostgreSQL..."
gosu postgres "$PGBIN/postgres" -D "$PGDATA" -c listen_addresses=127.0.0.1 -c logging_collector=off &
PG_PID=$!

echo "[todoapp] Warte auf PostgreSQL..."
for _ in $(seq 1 30); do
    gosu postgres "$PGBIN/pg_isready" -h 127.0.0.1 -q && break
    sleep 1
done

echo "[todoapp] Stelle sicher, dass Rolle & Datenbank existieren..."
gosu postgres "$PGBIN/psql" -h 127.0.0.1 -tc "SELECT 1 FROM pg_roles WHERE rolname='${DB_USER}'" | grep -q 1 \
    || gosu postgres "$PGBIN/psql" -h 127.0.0.1 -c "CREATE ROLE \"${DB_USER}\" LOGIN PASSWORD '${DB_PASSWORD}'"
gosu postgres "$PGBIN/psql" -h 127.0.0.1 -c "ALTER ROLE \"${DB_USER}\" WITH PASSWORD '${DB_PASSWORD}'"
gosu postgres "$PGBIN/psql" -h 127.0.0.1 -tc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'" | grep -q 1 \
    || gosu postgres "$PGBIN/psql" -h 127.0.0.1 -c "CREATE DATABASE \"${DB_NAME}\" OWNER \"${DB_USER}\""

echo "[todoapp] Stelle sicher, dass ${DB_USER} auf allen Objekten in ${DB_NAME} Rechte hat..."
# Tabellen können aus früheren Läufen (z. B. nach Wechsel von db_user oder einem
# unvollständigen vorherigen Start) noch der Rolle "postgres" gehören, wodurch
# ${DB_USER} keinen Zugriff mehr hat ("permission denied for table ..."). Statt
# Ownership umzuschreiben (scheitert bei "postgres" mit "required by the database
# system"), bei jedem Start idempotent Rechte gewähren – auch für Tabellen, die
# postgres künftig noch anlegt (ALTER DEFAULT PRIVILEGES).
gosu postgres "$PGBIN/psql" -h 127.0.0.1 -d "${DB_NAME}" -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"${DB_USER}\""
gosu postgres "$PGBIN/psql" -h 127.0.0.1 -d "${DB_NAME}" -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO \"${DB_USER}\""
gosu postgres "$PGBIN/psql" -h 127.0.0.1 -d "${DB_NAME}" -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO \"${DB_USER}\""
gosu postgres "$PGBIN/psql" -h 127.0.0.1 -d "${DB_NAME}" -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO \"${DB_USER}\""

echo "[todoapp] Starte Backend..."
export DB_NAME DB_USER DB_PASSWORD JWT_SECRET
if [ -n "$CORS_ALLOWED_ORIGINS" ] && [ "$CORS_ALLOWED_ORIGINS" != "null" ]; then
    export CORS_ALLOWED_ORIGINS
fi
java -jar /app/backend.jar &
BACKEND_PID=$!

echo "[todoapp] Warte auf Backend..."
for _ in $(seq 1 60); do
    (exec 3<>/dev/tcp/127.0.0.1/8080) 2>/dev/null && exec 3<&- && break
    sleep 1
done

echo "[todoapp] Starte nginx..."
nginx -g "daemon off;" &
NGINX_PID=$!

wait -n "$PG_PID" "$BACKEND_PID" "$NGINX_PID"
echo "[todoapp] Ein Dienst wurde beendet — stoppe Add-on, damit der Supervisor neu starten kann."
exit 1
