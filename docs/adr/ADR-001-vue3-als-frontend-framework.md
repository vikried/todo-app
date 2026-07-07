# ADR-001: Vue 3 als Frontend-Framework

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Für die Todo-App wird ein modernes JavaScript-Framework benötigt, das eine reaktive, komponentenbasierte UI ermöglicht. Die Wahl fiel zwischen React, Angular und Vue.js.

## Entscheidung

Vue 3 wird als Frontend-Framework eingesetzt. Die Komponenten nutzen die **Composition API** mit `<script setup>`-Syntax anstelle der Options API.

**Betroffene Dateien:**
- `frontend/src/App.vue`
- `frontend/src/views/*.vue`
- `frontend/src/components/*.vue`

## Begründung

- **Einsteigerfreundlichkeit:** Vue hat im Vergleich zu Angular eine flache Lernkurve und eine klarere Trennung von Template, Logik und Style in einer Single-File Component (SFC).
- **Composition API:** Ermöglicht bessere Code-Wiederverwendung über Composables und ist typischer für mittlere bis große Anwendungen geeignet.
- **`<script setup>`:** Reduziert Boilerplate gegenüber der Options API erheblich.
- **Ökosystem:** Vue Router und Pinia bieten eine vollständige, offiziell unterstützte Lösung für Routing und State Management.

## Konsequenzen

**Positiv:**
- Klare Komponentenstruktur mit SFCs (`.vue`-Dateien).
- Reaktives Rendering ohne manuelles DOM-Management.
- Gute Tooling-Unterstützung durch Vite + `@vitejs/plugin-vue`.

**Negativ / Trade-offs:**
- Kleineres Ökosystem als React; weniger Community-Bibliotheken verfügbar.
- Composition API erfordert ein Umdenken für Entwickler, die nur die Options API kennen.
- Keine eingebaute TypeScript-Erzwingung (das Projekt nutzt JS, nicht TS).

## Alternativen, die verworfen wurden

- **React:** Größeres Ökosystem, aber höherer Boilerplate und keine native SFC-Struktur.
- **Angular:** Zu viel Overhead und Komplexität für eine mittelgroße Anwendung.
