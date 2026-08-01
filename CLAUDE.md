# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A family todo-list app (Vue 3 SPA + Spring Boot REST API + PostgreSQL), shipped in **three deployment forms from the same source**:

1. **`backend/` + `frontend/`** — the app itself, run locally via `docker-compose.yml` (three containers: `db`, `backend`, `frontend`).
2. **`todoapp/`** — packages backend+frontend+PostgreSQL into a single container as a **Home Assistant Add-on** (installed via the HA Supervisor add-on store, accessed through HA's Ingress proxy in the sidebar). Has its own `config.yaml` (version, options) and `CHANGELOG.md`.
3. **`custom_components/todoapp/`** — a separate **Home Assistant custom integration** that exposes todo lists as native `todo.*` HA entities (for Assist voice control). Talks to the backend's REST API directly (not through Ingress — Ingress is bound to an active browser session, so the add-on also publishes an optional direct port for this).

Changes to `backend/` or `frontend/` affect all three; changes to `todoapp/` or `custom_components/todoapp/` are deployment-specific and have their own versioning (see below).

## Commands

### Local dev (docker-compose)
```
docker-compose up --build      # all three services; needs a .env (copy .env.example)
```

### Backend (Spring Boot, Java 17, Maven)
```
cd backend
mvn spring-boot:run            # requires Postgres reachable at localhost:5432 (jdbc default), or start via `docker-compose up -d db`
mvn clean package -DskipTests  # what the Dockerfile does
mvn test                       # no tests currently exist in backend/src/test
```
DB schema is managed by **Flyway** migrations in `backend/src/main/resources/db/migration/` (`ddl-auto=validate`, never `update`) — schema changes go through a new `V{n}__description.sql` file, not entity annotation changes alone.

### Frontend (Vue 3, Vite)
```
cd frontend
npm run dev       # Vite dev server; proxies /api to http://localhost:8080 (see vite.config.js)
npm run build
```
No lint or test scripts are configured for the frontend.

### No automated test suite exists anywhere in this repo.
Verify changes by rebuilding the relevant docker-compose service(s) and exercising the app in a browser (see `<preview_tools>` workflow if using Claude Code's browser tools). For UI changes, always check both light and dark mode, and desktop + mobile viewport widths — this app is used on phones and gets frequent mobile-specific bug reports.

## Architecture

### Backend layering
Standard Spring Boot layering: `controller` → `service` → `repository`, with a separate `dto` + `mapper` (MapStruct, compile-time generated) layer so JPA entities (`model/`) never get serialized directly — see `docs/adr/ADR-008-dto-pattern-und-mapstruct.md`.

### Data model
`User` → owns `TodoList`s (and can be granted access via a `todo_list_shares` many-to-many, see `shareList`/`unshareList`). `TodoList` → has `Category`s and `Todo`s. **A `Todo` has independent foreign keys to both its `TodoList` and (optionally) its `Category`** — a todo not in any category still belongs directly to a list. When creating/moving todos, both `todoListId` and `categoryId` need to be set correctly; a past bug here (todos created via the category endpoint missing `todoListId`) needed a Flyway backfill migration (`V3__backfill_todo_list_id.sql`), so don't reintroduce that gap.

**Templates are not a separate entity type** — a "template" is just a `TodoList` with `template = true`. `TodoListService.createListFromTemplate()` deep-clones a template's categories/todos into a new real list; `saveAsTemplate()` does the reverse (snapshot a real list into a new template, resetting `done` status). See `docs/adr/ADR-011-template-funktion.md`.

### Auth
Stateless JWT (`JwtUtil`, `JwtFilter`), no server-side sessions. Token lifetime is `app.jwt.expiration-ms` (default 90 days, overridable via `JWT_EXPIRATION_MS`) — deliberately long so users don't have to re-login constantly (see ADR-007). Logout blacklists the token in `TokenBlacklistService` (in-memory `Set`, not persisted/shared across instances — fine for single-instance deployment, would need a shared store like Redis for horizontal scaling).

### Ingress/base-path handling (frontend)
Because the Home Assistant Add-on serves the app behind a dynamic Ingress path prefix (`/api/hassio_ingress/<token>/`), the frontend **never hardcodes absolute paths**. `vite.config.js` uses `base: './'` (relative asset paths), nginx injects a `<base href>` tag reflecting the actual prefix, and both `frontend/src/api.js` and `frontend/src/router/index.js` derive their base path at runtime from that `<base>` tag rather than from a compiled-in constant. If you add new absolute-path assumptions (fetch calls, redirects, router pushes with a leading `/`), they will break under Ingress even though they work fine in plain docker-compose — always route through `api.js`'s `api` instance or `appBasePath`.

### Frontend structure
Vue 3 Composition API (`<script setup>`), Pinia stores (`store/`, one per resource: `authStore`, `todoListStore`, `categoryStore`, `todoStore`), Vue Router with a global `beforeEach` auth guard, Tailwind CSS (`dark:` variants for dark mode, class-based). No component library — `components/BaseButton.vue`, `IconButton.vue`, `ConfirmDialog.vue` are the shared primitives; icons come from `lucide-vue-next`.

### Home Assistant Add-on (`todoapp/`)
Single container running PostgreSQL + backend + nginx, started/supervised by `run.sh` (idempotently grants DB privileges on every start — tables can end up owned by the `postgres` role across restarts otherwise). Config (`db_password`, `jwt_secret`, `cors_allowed_origins`, etc.) comes from HA's add-on options (`/data/options.json`), not `.env`. The Docker build context is limited to the `todoapp/` folder by the HA Supervisor, so its `Dockerfile` `git clone`s `backend/` and `frontend/` from this repo rather than using local build contexts like the top-level `docker-compose.yml` does.

**Versioning convention**: bump `version` in `todoapp/config.yaml` and add a matching dated entry to `todoapp/CHANGELOG.md` for every user-facing change to `backend/`, `frontend/`, or `todoapp/` itself — this is how HA users see "update available". Commits follow the pattern of a feature commit, then a separate `Version X.Y.Z` commit.

### Home Assistant integration (`custom_components/todoapp/`)
Polls the backend REST API (`DataUpdateCoordinator`, default 2 min interval) and exposes each accessible list as a `todo.*` entity via `TodoListEntity`. Categories don't exist in HA's todo-entity model, so they're encoded as an `"Kategorie: Titel"` prefix in the item text (see its `README.md`). Versioned independently via `manifest.json`.

## Architecture Decision Records
`docs/adr/` documents the reasoning behind major stack choices (Vue 3, Pinia, Vite, Tailwind, Spring Boot, PostgreSQL, JWT, DTO/MapStruct, Docker Compose, OpenAPI, the template feature). Check there before proposing to swap out a core piece of the stack.
