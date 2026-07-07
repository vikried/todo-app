# ADR-004: Tailwind CSS als Styling-Ansatz

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Das Frontend benötigt eine konsistente, wartbare CSS-Strategie. Optionen reichen von klassischem BEM/SCSS über Komponentenbibliotheken (Vuetify, PrimeVue) bis hin zu Utility-First-CSS.

## Entscheidung

**Tailwind CSS v3** wird als Styling-Lösung eingesetzt. Dark-Mode-Unterstützung ist über die `class`-Strategie aktiviert.

```js
// tailwind.config.js
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{vue,js,ts}'],
  theme: { extend: {} },
  plugins: [],
}
```

## Begründung

- **Utility-First:** Klassen wie `flex`, `gap-2`, `rounded`, `hover:bg-blue-800` direkt im Template — kein Kontextwechsel in separate CSS-Dateien.
- **Dark Mode:** Die `class`-Strategie erlaubt es, den Dark Mode über eine CSS-Klasse am `<html>`-Element zu steuern, was mit dem `localStorage`-basierten Theme-Toggle in `main.js` harmoniert.
- **PurgeCSS integriert:** Tailwind entfernt ungenutzte Klassen im Produktions-Build automatisch.
- **Kein Design-System Overhead:** Im Gegensatz zu Vuetify oder PrimeVue gibt es keine vordefinierten Komponentenstile, die überschrieben werden müssten.

## Konsequenzen

**Positiv:**
- Schnelle UI-Entwicklung ohne CSS-Dateien anlegen zu müssen.
- Dark Mode funktioniert konsistent über alle Komponenten.
- Sehr kleines CSS-Bundle in der Produktion.

**Negativ / Trade-offs:**
- Templates können bei vielen Klassen unübersichtlich werden.
- Keine vorgefertigten UI-Komponenten — Buttons, Modals etc. müssen selbst gebaut werden (was im Projekt auch so gemacht wird, z. B. das Template-Popup in `ListDetailView.vue`).
- Tailwind v3 nutzt PostCSS; der Build ist an `postcss.config.js` gekoppelt.

## Alternativen, die verworfen wurden

- **Vuetify / PrimeVue:** Umfangreiche Komponentenbibliotheken, die mehr Overhead mitbringen und den Eigenentwicklungs-Lerneffekt reduzieren.
- **Plain CSS / SCSS:** Mehr Dateien, mehr Namenskonflikte, kein eingebautes Dark-Mode-Utility.
