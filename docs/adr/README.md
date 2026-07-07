# Architecture Decision Records — todo-app

Dieses Verzeichnis enthält die Architecture Decision Records (ADRs) für das `todo-app`-Projekt (Vue 3 + Spring Boot + PostgreSQL).

ADRs dokumentieren wichtige Architekturentscheidungen: was entschieden wurde, warum, und welche Konsequenzen sich daraus ergeben.

## Index

| Nr. | Titel | Status |
|---|---|---|
| [ADR-001](ADR-001-vue3-als-frontend-framework.md) | Vue 3 als Frontend-Framework | Accepted |
| [ADR-002](ADR-002-pinia-fuer-state-management.md) | Pinia für State Management | Accepted |
| [ADR-003](ADR-003-vite-als-build-tool.md) | Vite als Build-Tool | Accepted |
| [ADR-004](ADR-004-tailwind-css.md) | Tailwind CSS als Styling-Ansatz | Accepted |
| [ADR-005](ADR-005-spring-boot-als-backend.md) | Spring Boot 3 als Backend-Framework | Accepted |
| [ADR-006](ADR-006-postgresql-als-datenbank.md) | PostgreSQL als Datenbank | Accepted |
| [ADR-007](ADR-007-jwt-authentifizierung.md) | JWT-basierte Authentifizierung | Accepted |
| [ADR-008](ADR-008-dto-pattern-und-mapstruct.md) | DTO-Pattern und MapStruct | Accepted |
| [ADR-009](ADR-009-docker-compose-deployment.md) | Docker Compose für lokales Deployment | Accepted |
| [ADR-010](ADR-010-openapi-dokumentation.md) | SpringDoc OpenAPI (Swagger) | Accepted |
| [ADR-011](ADR-011-template-funktion.md) | Template-Funktion für Todo-Listen | Accepted |

## Architektur-Überblick

```
┌─────────────────────────────────────────────────────┐
│                    Browser                          │
│  Vue 3 SPA · Pinia · Vue Router · Tailwind CSS     │
│  Build: Vite → dist/ → nginx (Port 3000)           │
└────────────────────┬────────────────────────────────┘
                     │ HTTP/REST (Axios)
                     ▼
┌─────────────────────────────────────────────────────┐
│              Spring Boot 3 (Port 8080)              │
│  Controller → Service → Repository (JPA)           │
│  Security: JWT · Docs: SpringDoc OpenAPI           │
│  Mapping: MapStruct · DTOs · Lombok                │
└────────────────────┬────────────────────────────────┘
                     │ JDBC
                     ▼
┌─────────────────────────────────────────────────────┐
│           PostgreSQL 15 (Port 5432)                 │
│  users · todo_lists · categories · todos           │
└─────────────────────────────────────────────────────┘
         Alle drei Services via Docker Compose
```

## Identifizierte Risiken / offene Punkte

Stand 2026-07-07: Die unten aufgeführten Punkte wurden behoben (siehe Update-Vermerke in den jeweiligen ADRs). Verbleibender offener Punkt: Das Frontend hat noch keine Login/Register-UI und sendet keinen JWT-Token mit — die Auth-Absicherung im Backend (ADR-007) ist damit vom Frontend aus noch nicht nutzbar.

- ~~`ddl-auto=update` sollte durch Flyway/Liquibase ersetzt werden~~ → behoben, Flyway-Migrationen unter `backend/src/main/resources/db/migration` (ADR-006)
- ~~Datenbankpasswörter stehen im Klartext~~ → behoben, Secrets über `.env` (nicht versioniert) (ADR-009)
- ~~JWT-Token-Invalidierung fehlt~~ → behoben, `TokenBlacklistService` + `POST /api/auth/logout` (ADR-007)
- ~~`@CrossOrigin(origins = "*")` ist zu permissiv für Produktion~~ → behoben, konfigurierbare Origins über `app.cors.allowed-origins` (ADR-007)
- Hardcodierte `localhost`-URLs in den Pinia-Stores → bereits über `api.js`/`VITE_API_BASE_URL` gelöst, kein Handlungsbedarf mehr (ADR-002)
- ~~Keine serverseitige Input-Validierung (`@Valid`)~~ → behoben, Bean-Validation-Annotationen auf DTOs + `@Valid` in Controllern (ADR-008)
- ~~Neue Liste aus Template bekommt keinen User zugewiesen~~ → behoben, `createList`/`createListFromTemplate` setzen den aktuell authentifizierten User (ADR-011)
- **Neu identifiziert:** Keine Endpunkte filtern Listen/Todos/Kategorien nach dem eingeloggten User — jeder authentifizierte Nutzer sieht alle Daten. Das `user`-Feld wird jetzt zwar gesetzt, aber nicht für Zugriffskontrolle ausgewertet.
