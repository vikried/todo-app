# ADR-009: Docker Compose für lokales Deployment

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Die Anwendung besteht aus drei Komponenten (Frontend, Backend, Datenbank), die koordiniert gestartet werden müssen. Eine einfache, reproduzierbare Entwicklungs- und Deploymentumgebung ist gewünscht.

## Entscheidung

**Docker Compose** (v3.9) wird als Orchestrierungswerkzeug für die lokale Umgebung eingesetzt. Alle drei Services werden über `docker-compose.yml` definiert.

**Service-Übersicht:**

| Service | Image / Build | Port | Abhängigkeit |
|---|---|---|---|
| `db` | `postgres:15` | 5432 | — |
| `backend` | `./backend` (Dockerfile) | 8080 | `db` |
| `frontend` | `./frontend` (Dockerfile) | 3000→80 | `backend` |

**Frontend-Dockerfile:** Multi-Stage-Build — `node`-Image für den Vite-Build, `nginx`-Image für das Serving.  
**Backend-Dockerfile:** Baut das Spring Boot JAR und startet es.

## Begründung

- **Reproduzierbarkeit:** Jeder Entwickler kann die gesamte Anwendung mit `docker-compose up` starten, ohne lokale Java/Node-Installationen (außer Docker) zu benötigen.
- **Service-Isolation:** Jeder Service läuft in seinem eigenen Container mit definierten Umgebungsvariablen.
- **Umgebungsvariablen:** Die Datenbankverbindung (`SPRING_DATASOURCE_URL`) und die API-URL (`VITE_API_BASE_URL`) werden per Compose injiziert.
- **Datenpersistenz:** Ein Named Volume (`db_data`) stellt sicher, dass Datenbankdaten Container-Neustarts überleben.

## Konsequenzen

**Positiv:**
- Einzeilige Startprozedur: `docker-compose up`.
- Klare Netzwerkisolation — Backend greift per Service-Name `db` auf die Datenbank zu.
- `depends_on` stellt die Startreihenfolge sicher.

**Negativ / Trade-offs:**
- `depends_on` prüft nur ob der Container läuft, nicht ob der Service bereit ist (z. B. PostgreSQL kann noch initialisieren, während das Backend startet) → ggf. Healthchecks nötig.
- Datenbankpasswörter stehen im Klartext in `docker-compose.yml` und `application.properties` — kein Secret Management (z. B. Docker Secrets, `.env`-Datei).
- Nicht für Produktions-Deployments auf Kubernetes/ECS ausgelegt (kein Helm-Chart, kein `docker-compose.prod.yml`).
- `VITE_API_BASE_URL=http://backend:8080/api` funktioniert im Container-Netzwerk, aber nicht für Browser-Clients — der Browser kann `backend` nicht auflösen. Nginx-Proxy-Konfiguration im Frontend-Container ist nötig.

## Update (2026-07-07)

Die Klartext-Zugangsdaten wurden aus den versionierten Dateien entfernt: `docker-compose.yml` und `application.properties` lesen Datenbank-Zugangsdaten, JWT-Secret und erlaubte CORS-Origins jetzt aus Umgebungsvariablen (`DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`). Eine lokale `.env`-Datei (per `.gitignore` ausgeschlossen) liefert die Werte für `docker-compose up`; `.env.example` dokumentiert die benötigten Variablen als Vorlage. Für Produktion sollte weiterhin ein echtes Secret-Management (Docker Secrets, Vault, …) eingesetzt werden — die `.env`-Datei ist nur ein erster Schritt weg vom Klartext im Repository.

## Alternativen, die verworfen wurden

- **Kubernetes (Minikube):** Für lokale Entwicklung zu komplex.
- **Manuelles Starten:** Fehleranfällig und nicht reproduzierbar.
- **Podman Compose:** Gleichwertige Alternative, aber Docker ist weiter verbreitet.
