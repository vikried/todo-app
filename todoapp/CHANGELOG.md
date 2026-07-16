# Changelog

## 1.0.21

- Kategorien ohne sichtbare Todos werden jetzt ausgeblendet, statt leer
  angezeigt zu werden: Wirkt beim Ausblenden erledigter Todos (Kategorie
  verschwindet, wenn alle ihre Todos erledigt sind) und bei der Suche
  (Kategorie verschwindet, wenn nichts zum Suchbegriff passt). Im
  Bearbeiten-Modus bleiben weiterhin alle Kategorien sichtbar, damit
  z. B. Todos zu einer aktuell leeren Kategorie hinzugefügt werden können.

## 1.0.20

- JWT-Gültigkeit von 24 Stunden auf 90 Tage erhöht (konfigurierbar über
  `JWT_EXPIRATION_MS`), damit man sich nicht mehr täglich neu einloggen
  muss, nachdem man bereits in Home Assistant angemeldet ist. Abmelden
  und Login als anderer Nutzer funktionieren weiterhin wie gewohnt.

## 1.0.19

- Neues Suchfeld in der Listenansicht: Filtert Todos nach Titel oder
  Kategoriename. Passt der Suchbegriff auf eine Kategorie, bleiben alle
  ihre Todos sichtbar; Kategorien selbst verschwinden beim Filtern nie,
  nur die enthaltenen Todos werden ein-/ausgeblendet.
- Erledigte Todos werden jetzt standardmäßig ausgeblendet. Über einen
  neuen Button in der Bearbeiten-Leiste lassen sie sich wieder
  ein-/ausblenden (gilt nicht für Templates).

## 1.0.18

- Neuer Button „Als Vorlage speichern" in der Listenansicht: Der aktuelle
  Stand einer bestehenden Liste (Kategorien und Todos) lässt sich als
  neue Vorlage sichern, ohne die Ursprungsliste zu verändern – z. B. um
  eine bearbeitete Liste als wiederverwendbares Template festzuhalten.
  Der Erledigt-Status wird in der neuen Vorlage zurückgesetzt.

## 1.0.17

- Änderungen an einer geteilten Liste durch andere Nutzer (z. B. Familien-
  mitglieder) erschienen erst nach manuellem Neuladen der Seite. Die
  Listenansicht fragt jetzt alle 5 Sekunden im Hintergrund still nach
  Änderungen und aktualisiert sich automatisch, solange sie geöffnet ist.

## 1.0.16

- Buttons in der Bearbeiten-Leiste ragten auf schmalen Bildschirmen
  (z. B. iPhone) rechts über den Viewport hinaus bzw. brachen zweizeilig
  um, was beim Scrollen zu sichtbarem horizontalem Ruckeln führte.
  Auf mobilen Screens werden jetzt nur noch die Icons angezeigt (mit
  Tooltip/Screenreader-Label), Beschriftungen erscheinen erst ab
  Tablet-/Desktop-Breite.

## 1.0.15

- Beim Bearbeiten einer Liste blieb der „Fertig"-Button beim Scrollen
  ganz oben und war auf langen Listen erst nach Zurückscrollen wieder
  erreichbar. Titel- und Buttonzeile sind jetzt fixiert (sticky), nur
  die Kategorien scrollen darunter.
- Kategorien lassen sich einzeln per Klick auf den Titel ein-/ausklappen
  (Akkordion, standardmäßig alle ausgeklappt) sowie über einen neuen
  Button „Alle ein-/ausklappen" gesammelt umschalten.
- Todos lassen sich jetzt ebenfalls umbenennen (Stift-Icon im
  Bearbeiten-Modus, analog zu Listen und Kategorien).

## 1.0.14

- Seitenleisten-Panel war bisher nur für Home-Assistant-Admins sichtbar
  (`panel_admin` defaultet auf `true`). Jetzt `panel_admin: false`, damit
  auch Nicht-Admin-Nutzer (z. B. Familienmitglieder) das Panel sehen.
  Zugriff auf die App selbst bleibt weiterhin über den eigenen
  Todo-App-Login (Benutzername/Passwort) geregelt, unabhängig vom
  Home-Assistant-Nutzerkonto.

## 1.0.13

- Trennlinie zwischen Todo-Items einer Kategorie sichtbar gemacht: Im
  Light Mode fehlte ihr komplett die Farbe, im Dark Mode war sie exakt
  gleichfarbig mit dem Zeilen-Hintergrund – in beiden Fällen praktisch
  unsichtbar.

## 1.0.12

- Mobile-Ansicht überarbeitet: Dark-Mode-Hintergrund deckte bei kurzen
  Seiten nicht die volle Bildschirmhöhe ab (sichtbarer Farbbruch) – behoben.
- Lange Todo-Titel im Bearbeiten-Modus wurden durch die Verschieben-/
  Löschen-Icons in eine sehr schmale Spalte gequetscht (Wort-für-Wort-
  Umbruch, Icons vertikal mittig schwebend). Icons stehen jetzt in einer
  eigenen Zeile über dem Text, der Text bekommt die volle Breite.

## 1.0.11

- Listen und Kategorien lassen sich umbenennen (Stift-Icon im
  Bearbeiten-Modus, Speichern/Abbrechen per Button statt Enter/Blur).
- Todos lassen sich in eine andere Kategorie derselben Liste verschieben:
  per Icon → Popup mit Kategorie-Dropdown und OK/Abbrechen, oder direkt
  per Drag-and-Drop auf die Ziel-Kategorie (mit Bestätigungsabfrage vor
  dem eigentlichen Verschieben).
- Bug behoben: Todos, die über das Kategorie-Formular angelegt wurden,
  bekamen keine `todo_list_id` gesetzt (nur die Kategorie-Zuordnung).
  Eine Datenbank-Migration korrigiert davon betroffene Bestandsdaten
  automatisch beim ersten Start dieser Version.
- Repository ist jetzt zusätzlich über HACS als Custom Repository
  installierbar (`hacs.json` ergänzt).

## 1.0.10

- Optionalen Direkt-Port (80/tcp, Default 8099) ergänzt, damit die neue
  Home-Assistant-Integration (`custom_components/todoapp`, siehe
  [README](../custom_components/todoapp/README.md)) das Backend außerhalb
  von Ingress erreichen kann. Damit lassen sich Listen/Kategorien/Items
  per Assist-Sprachsteuerung (App-/Browser-Mikrofon, Voice-Hardware)
  pflegen. Backend-seitig: `POST /api/todos` erstellte bisher „verwaiste"
  Todos ohne Listen-/Kategorie-Verknüpfung (nie behobener Mapper-Bug) –
  jetzt korrekt verknüpft, inkl. neuer `categoryName`-Option (legt die
  Kategorie bei Bedarf an, statt nur per ID zu referenzieren).

## 1.0.9
- Import Funktion ein-/ausklappbar

## 1.0.8
- CSV und JSON Import der Templates

## 1.0.7

- Registrierung/Login schlugen nach 1.0.6 mit 404 bzw. „Invalid CORS
  request" (403) fehl: Die API-Basis-URL wurde als absoluter Pfad (`/api`)
  gebaut, der sich immer gegen die Domain-Wurzel statt gegen den
  Ingress-Präfix auflöst. `api.js` leitet die Basis jetzt zur Laufzeit aus
  demselben `<base href>`-Tag ab wie der Router. Zusätzlich entfernt nginx
  beim Proxy zum (nur intern erreichbaren) Backend den `Origin`-Header, da
  sich dieser je nach Home-Assistant-Hostname ändert und sich nicht in
  einer festen CORS-Allow-Liste pflegen lässt.
- `VITE_API_BASE_URL` (Build-Env-Var, `.env`-Datei) komplett entfernt und
  durch die Laufzeit-Ableitung ersetzt; `npm run dev` nutzt stattdessen
  einen Vite-Dev-Server-Proxy für `/api`.
- BUILD_VERSION (von Supervisor automatisch übergeben) im git-clone-Schritt
  referenziert, damit Docker diesen Layer bei jedem Versions-Bump neu
  ausführt statt einen alten Checkout zu cachen.

Lokal per Browser-Automatisierung verifiziert: Registrierung, Login und
Laden der Listen liefern 200, keine Konsolenfehler.

## 1.0.6
- Leere Seite weiterer Versuch

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
