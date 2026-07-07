# ADR-006: PostgreSQL als Datenbank

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Die Anwendung muss Nutzer, Todo-Listen, Kategorien und Todos persistent speichern. Die Daten sind relational strukturiert (Nutzer → Listen → Kategorien → Todos).

## Entscheidung

**PostgreSQL 15** wird als relationale Datenbank eingesetzt, betrieben als Docker-Container.

**Datenbankschema (abgeleitet aus den JPA-Entitäten):**

```
users          (id, username, password)
todo_lists     (id, name, template, user_id)
categories     (id, name, todo_list_id)
todos          (id, title, done, todo_list_id, category_id)
```

## Begründung

- **Relationale Struktur:** Die Daten haben klare Fremdschlüssel-Beziehungen (1:n Nutzer→Listen, 1:n Listen→Kategorien, 1:n Kategorien→Todos). Eine relationale DB bildet das direkt ab.
- **JPA-Kompatibilität:** Spring Data JPA mit Hibernate unterstützt PostgreSQL nativ; keine zusätzliche Konfiguration nötig.
- **PostgreSQL-Stärken:** ACID-konform, zuverlässig, kostenlos, weit verbreitet.
- **Docker-Integration:** Das offizielle `postgres:15`-Image ist minimal und gut dokumentiert.

## Konsequenzen

**Positiv:**
- Referenzielle Integrität durch Fremdschlüssel.
- `CascadeType.ALL` + `orphanRemoval = true` auf `TodoList.todos` und `TodoList.categories` stellt sicher, dass beim Löschen einer Liste alle abhängigen Einträge mitgelöscht werden.
- Daten werden über ein Docker Volume (`db_data`) persistiert.

**Negativ / Trade-offs:**
- Keine Datenbank-Migrations-Strategie (kein Flyway/Liquibase) — `ddl-auto=update` kann bei Schemaänderungen in Produktionsumgebungen zu Datenverlust führen.
- Passwörter in `docker-compose.yml` und `application.properties` sind im Klartext hinterlegt — kein Secret Management.

## Update (2026-07-07)

Die fehlende Migrations-Strategie wurde behoben: **Flyway** (`flyway-core`, `flyway-database-postgresql`) verwaltet das Schema jetzt über versionierte Migrationen unter `backend/src/main/resources/db/migration` (beginnend mit `V1__init.sql`). `spring.jpa.hibernate.ddl-auto` wurde von `update` auf `validate` umgestellt, damit Hibernate das Schema nur noch gegen die Migrationen prüft, statt es selbst zu verändern. `spring.flyway.baseline-on-migrate=true` erlaubt die Umstellung auf bereits laufenden Dev-Datenbanken ohne manuellen Eingriff.

## Alternativen, die verworfen wurden

- **H2 (In-Memory):** Gut für Tests, aber nicht für Produktion geeignet.
- **MySQL/MariaDB:** Gleichwertige Alternative; PostgreSQL wurde wegen seiner stärkeren Standard-Konformität bevorzugt.
- **MongoDB:** Dokumenten-DB würde keinen Mehrwert bieten, da die Daten klar relational sind.
