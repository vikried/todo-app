# Todo-App – Home-Assistant-Integration

Stellt die Listen (und Templates) der Todo-App als native Home-Assistant-
`todo.*`-Entities bereit. Damit lassen sich Items per Assist (Home
Assistants eigenes Sprachsystem – App-Mikrofon, Browser-Mikrofon,
ESPHome-Satelliten, Home Assistant Voice Preview Edition) frei sprechen,
z. B. „Füge Milch zur Einkaufsliste hinzu" oder „Markiere Milch auf der
Einkaufsliste als erledigt".

**Nicht** über echte Google-Home-Lautsprecher nutzbar – deren Mikrofon ist
für Drittsoftware technisch nicht zugänglich (siehe Home-Assistant-Doku).
Google-Home-Lautsprecher können aber weiterhin als reine Sprachausgabe für
Assist-Antworten verwendet werden (Google-Cast-Integration).

## Einschränkung: Kategorien

Home-Assistant-Todo-Listen kennen keine Unterkategorien. Die Kategorie
wird daher als Präfix in den Item-Text kodiert: `Kategorie: Titel`
(z. B. `Kühlware: Milch`). Ohne Doppelpunkt-Präfix landet das Item
unkategorisiert. Beim Ansagen per Sprache entsprechend mitsprechen, sonst
liest Assist beim Vorlesen auch den Kategorie-Teil vor.

## Voraussetzung: Backend muss direkt erreichbar sein

Diese Integration läuft in Home Assistant Core und braucht eine normale
HTTP-URL zum Backend – **nicht** die Ingress-URL (die ist an eine aktive
Browser-Session gebunden, nicht dauerhaft adressierbar).

- **Home-Assistant-Add-on**: Auf der Add-on-Seite unter „Netzwerk" den
  Port `80/tcp` (Default `8099`) veröffentlichen. URL dann z. B.
  `http://homeassistant.local:8099` oder `http://<HA-Host-IP>:8099`.
- **docker-compose**: Backend ist bereits auf Port `8080` exponiert, z. B.
  `http://<Docker-Host-IP>:8080`.

## Installation

1. Diesen Ordner (`custom_components/todoapp`) nach
   `<Home-Assistant-Config>/custom_components/todoapp` kopieren.
2. Home Assistant neu starten.
3. **Einstellungen → Geräte & Dienste → Integration hinzufügen** → „Todo-App"
   suchen.
4. Backend-URL, Benutzername und Passwort eingeben (dasselbe Konto wie in
   der Web-App).

Jede zugängliche Liste (eigene, geteilte, Templates) erscheint danach als
eigene `todo.*`-Entity. Neue Listen, die später in der App angelegt
werden, erscheinen automatisch nach dem nächsten Poll (alle 2 min – Änderungen
über Assist selbst lösen unabhängig davon sofort einen gezielten Refresh
aus). Wird eine Liste in der App gelöscht, wird die zugehörige Entity nicht
automatisch entfernt, sondern nur als „nicht verfügbar" markiert.

## Bekannte Einschränkungen

- Reine Fehlerdiagnose: Bei falschem Passwort/URL zeigt der Config-Flow
  einen Fehler; bei abgelaufener Sitzung meldet sich die Integration
  automatisch neu an (JWT-Tokens laufen standardmäßig nach 90 Tagen ab,
  konfigurierbar über `JWT_EXPIRATION_MS`).
- Kein Löschen/Umbenennen von Listen selbst über Assist, nur Items
  innerhalb einer Liste (Create/Update/Delete).
