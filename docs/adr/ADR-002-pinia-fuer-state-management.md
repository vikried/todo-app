# ADR-002: Pinia für State Management

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Die Todo-App verwaltet Zustand über mehrere Views hinweg: Todo-Listen, Todos selbst und Kategorien. Ein zentrales State-Management verhindert Prop-Drilling und ermöglicht eine saubere Datenschicht.

## Entscheidung

**Pinia** wird als State-Management-Bibliothek eingesetzt. Es gibt drei Stores:

| Store | Datei | Verantwortung |
|---|---|---|
| `todoListStore` | `store/todoListStore.js` | Todo-Listen und Template-Verwaltung |
| `todoStore` | `store/todoStore.js` | Einzelne Todo-Einträge |
| `categoryStore` | `store/categoryStore.js` | Kategorien einer Liste |

## Begründung

- **Offizieller Nachfolger von Vuex:** Pinia ist die von Vue offiziell empfohlene State-Management-Lösung ab Vue 3.
- **Kein Mutations-Boilerplate:** Im Gegensatz zu Vuex entfallen Mutations — Actions können den State direkt verändern.
- **DevTools-Integration:** Pinia ist nahtlos in die Vue DevTools integriert.
- **Modular:** Jede Domäne (Listen, Todos, Kategorien) bekommt einen eigenen Store, was die Trennung der Verantwortlichkeiten unterstützt.

## Konsequenzen

**Positiv:**
- Saubere Trennung zwischen UI-Komponenten und Datenzugriffslogik.
- API-Calls sind in den Store-Actions gekapselt, Views bleiben schlank.
- Einfaches reaktives Teilen von Zustand über Views hinweg (z. B. `selectedList`).

**Negativ / Trade-offs:**
- Die Stores enthalten aktuell hardcodierte `localhost`-URLs statt den konfigurierten `api`-Client aus `api.js` zu nutzen — ein Konsistenzproblem (→ siehe auch ADR-003).
- Kein persistenter Zustand (kein `pinia-plugin-persistedstate`), d. h. nach Reload ist der Store leer.

## Alternativen, die verworfen wurden

- **Vuex 4:** Offiziell als veraltet markiert für Vue 3.
- **Globales `provide`/`inject`:** Für eine Anwendung dieser Größe zu unstrukturiert.
- **Lokaler Komponenten-State:** Würde Prop-Drilling zwischen Views erfordern.
