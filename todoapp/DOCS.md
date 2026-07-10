# Todo-App – Home-Assistant-Add-on

Verpackt Backend (Spring Boot, JWT-Auth), Frontend (Vue, über nginx) und PostgreSQL
in einem einzigen Add-on-Container. Nach der Installation erscheint „Todo-App" als
eigener Menüpunkt in der Home-Assistant-Seitenleiste (und damit auch in der
Home-Assistant-Companion-App) – dank Ingress ist dafür kein zusätzlicher Port und
kein Port-Forwarding nötig.

## Installation

1. **Einstellungen → Add-ons → Add-on Store → ⋮ (oben rechts) → Repositories**
   und `https://github.com/vikried/todo-app` hinzufügen.
2. „Todo-App" in der Liste suchen und installieren.
3. Unter dem Reiter **Konfiguration** die Optionen setzen (siehe unten) und speichern.
4. Add-on **starten**. Beim allerersten Start wird das Image lokal gebaut
   (Maven- + npm-Build) – das kann je nach Hardware (z. B. Raspberry Pi) einige
   Minuten dauern. Fortschritt ist im Reiter **Log** sichtbar.
5. Sobald der Log-Eintrag `[todoapp] Starte nginx...` erscheint, ist die App über
   „Open Web UI" bzw. den Seitenleisten-Eintrag erreichbar.

## Konfiguration

| Option | Beschreibung |
|---|---|
| `db_name`, `db_user`, `db_password` | Zugangsdaten für die interne PostgreSQL-Datenbank. `db_password` unbedingt ändern. |
| `jwt_secret` | Signaturschlüssel für JWT-Tokens, **mindestens 32 Zeichen**. Unbedingt ändern und geheim halten – jeder mit diesem Schlüssel kann gültige Tokens fälschen. |
| `cors_allowed_origins` | Nur nötig, wenn die App zusätzlich außerhalb von Ingress (z. B. per freigegebenem Port) unter einer eigenen Origin aufgerufen wird. Kommagetrennte Liste, sonst leer lassen. |

Nach dem Ändern von `db_password` das Add-on einmal **neu starten**, damit das
Passwort auch am bestehenden Datenbank-Benutzer aktualisiert wird.

## Daten & Persistenz

Alle Daten (PostgreSQL-Datenverzeichnis) liegen im add-on-eigenen, von Home
Assistant verwalteten `/data`-Verzeichnis und bleiben bei Updates/Neustarts
erhalten. Sie sind Teil der regulären Home-Assistant-Datensicherung, sofern das
Add-on beim Backup mit ausgewählt wird.

## Bekannte Einschränkungen

- Unterstützte Architekturen: `amd64`, `aarch64`. Kein `armv7` (32-Bit), da die
  benötigten Java-/PostgreSQL-Pakete dafür nicht zuverlässig verfügbar sind.
- Das Add-on baut das Image beim ersten Start selbst (kein vorgebautes Image aus
  einer Registry) – dadurch ist Internetzugriff des Host-Systems beim Bauen
  erforderlich, danach nicht mehr.
- Der Build-Kontext eines Add-ons ist laut Home-Assistant-Vorgabe auf den
  Add-on-Ordner (`todoapp/`) beschränkt. Das Dockerfile klont daher beim Bauen
  Backend und Frontend per `git clone` frisch aus dem öffentlichen GitHub-Repo
  (Branch `main`) – lokale, noch nicht gepushte Änderungen an `backend/` oder
  `frontend/` fließen erst nach einem Push in den nächsten Add-on-Build ein.
- Postgres, Backend und nginx laufen als einfache Hintergrundprozesse im selben
  Container (kein s6/Supervisor). Stirbt einer der drei Prozesse, beendet sich
  das Add-on kontrolliert, damit der Home-Assistant-Watchdog es neu starten kann.

## Für den normalen Docker-Betrieb (ohne Home Assistant)

Weiterhin einfach `docker-compose up` im Projekt-Root verwenden – das Add-on
ändert nichts an diesem bestehenden Setup, es kommt lediglich als zusätzlicher,
alternativer Weg (Ordner `todoapp/` im Repo, siehe `repository.yaml` im
Repo-Root) hinzu.
