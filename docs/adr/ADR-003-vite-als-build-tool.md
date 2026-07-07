# ADR-003: Vite als Build-Tool

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Das Frontend-Projekt benötigt ein Build-Tool, das lokale Entwicklung (Hot Module Replacement) und Produktions-Builds unterstützt. Klassisch wäre webpack (z. B. über Vue CLI), modern wäre Vite.

## Entscheidung

**Vite** (v7) wird als Build-Tool und Dev-Server eingesetzt, konfiguriert über `vite.config.js`.

```js
// vite.config.js (Auszug)
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: { alias: { '@': '/src' } },
  server: { port: 5173 }
})
```

## Begründung

- **Geschwindigkeit:** Vite nutzt native ES-Module im Dev-Modus — kein vollständiges Bundle beim Start.
- **HMR:** Hot Module Replacement ist ohne Konfigurationsaufwand sofort verfügbar.
- **Modernes Tooling:** Vite ist der de-facto Standard für neue Vue-3-Projekte.
- **`@`-Alias:** Der konfigurierte Alias `@` → `/src` verbessert die Import-Lesbarkeit durchgehend im Frontend-Code.

## Konsequenzen

**Positiv:**
- Deutlich schnellerer Dev-Server-Start im Vergleich zu webpack.
- Produktions-Builds via `vite build` erzeugen optimiertes, getrenntes JS/CSS in `dist/`.
- Das Dockerfile nutzt das `dist/`-Verzeichnis und served es via nginx.

**Negativ / Trade-offs:**
- Vite ist auf moderne Browser ausgerichtet; Legacy-Browser-Support erfordert zusätzliche Konfiguration (`@vitejs/plugin-legacy`).
- `VITE_API_BASE_URL` wird per `.env`-Datei injiziert, ist aber im Store nicht konsequent genutzt (hardcodierte localhost-URLs in den Stores).

## Alternativen, die verworfen wurden

- **Vue CLI / webpack:** Langsamer Dev-Start, weniger zeitgemäß.
- **Nuxt.js:** Zu viel Framework-Overhead für eine SPA ohne SSR-Anforderungen.
