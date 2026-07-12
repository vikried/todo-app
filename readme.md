# Docker starten
- docker-compose build
- docker-compose up

Docker Dateien nach lokalen Änderungen durchsuchen

# Als Home-Assistant-Add-on
Dieses Repository lässt sich zusätzlich als Home-Assistant-Add-on-Repository
einbinden (Backend, Frontend und PostgreSQL in einem Container, Zugriff über
die Home-Assistant-Seitenleiste/App per Ingress). Details siehe
[todoapp/DOCS.md](todoapp/DOCS.md).

# Sprachsteuerung über Home Assistant Assist
`custom_components/todoapp` stellt Listen/Templates als native
Home-Assistant-Todo-Entities bereit, nutzbar per Assist-Sprachsteuerung
(App-/Browser-Mikrofon, Voice-Hardware – nicht über Google-Home-Lautsprecher,
siehe README). Details und Installation siehe
[custom_components/todoapp/README.md](custom_components/todoapp/README.md).