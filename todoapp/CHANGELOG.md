# Changelog

## 1.0.5

- Leere Seite beim Öffnen der App behoben: `frontend/.env` legt
  `VITE_API_BASE_URL` fest auf `http://localhost:8080/api` für den lokalen
  `npm run dev`-Workflow fest. Das wurde beim Add-on-Build mit einkompiliert
  – im Browser des Endnutzers bedeutet „localhost" aber dessen eigenes
  Gerät, sodass jeder API-Aufruf ins Leere lief. Der Add-on-Build setzt
  `VITE_API_BASE_URL=/api` (relativ) jetzt explizit vor `npm run build`.

## 1.0.4

- Add-on-Dateien in den erforderlichen `todoapp/`-Unterordner verschoben, Build
  klont Backend/Frontend jetzt per `git clone` (Home Assistant beschränkt den
  Docker-Build-Kontext auf den Add-on-Ordner).
- `watchdog`-URL korrigiert (`[HOST]`-Platzhalter statt fester IP), sonst
  verwarf der Supervisor `config.yaml` beim Parsen kommentarlos.
- Datenbankrechte werden bei jedem Start idempotent per `GRANT` repariert,
  falls Tabellen aus einem vorherigen Lauf noch der Rolle `postgres` gehören
  ("permission denied for table flyway_schema_history").

## 1.0.0

- Erste Version des Home-Assistant-Add-ons: Backend, Frontend und PostgreSQL in
  einem Container, Zugriff per Ingress über die Home-Assistant-Seitenleiste.
