# Changelog

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
