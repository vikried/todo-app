# Changelog

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
