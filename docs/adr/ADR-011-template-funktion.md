# ADR-011: Template-Funktion für Todo-Listen

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Nutzer sollen wiederkehrende Strukturen (z. B. eine Packliste mit festen Kategorien und Einträgen) einmalig anlegen und bei Bedarf als neue Liste instantiieren können. Eine Modellierungsentscheidung war nötig: Wie werden Templates von regulären Listen unterschieden?

## Entscheidung

Templates werden **nicht als separater Entitätstyp** modelliert, sondern als `TodoList` mit dem Flag `template = true`. Das Klonen übernimmt `TodoListService.createListFromTemplate()`.

**Datenmodell:**

```java
// TodoList.java
private boolean template = false;
```

**API-Endpunkte:**

| Methode | Pfad | Funktion |
|---|---|---|
| GET | `/api/lists/templates` | Alle Templates abrufen |
| POST | `/api/lists/from-template/{id}?newListName=...` | Neue Liste aus Template erzeugen |

**Klon-Logik:** Kategorien und ihre Todos werden tief geklont; alle Todos der neuen Liste starten mit `done = false`.

## Begründung

- **Einfachheit:** Ein Boolean-Flag vermeidet eine separate Tabelle und vereinfacht das Datenmodell erheblich.
- **Wiederverwendung:** Dieselbe Infrastruktur (Controller, Service, Repository) wird für reguläre Listen und Templates genutzt.
- **Tiefer Klon:** Das vollständige Klonen von Kategorien und Todos stellt sicher, dass Änderungen an der neuen Liste das Template nicht beeinflussen.

## Konsequenzen

**Positiv:**
- Minimale Datenbankschema-Erweiterung (ein `boolean`-Feld).
- Die Frontend-View (`ListDetailView.vue`) zeigt kontextuell einen "Liste erstellen"-Button an, wenn `list.template === true`.
- Template-Listen erscheinen in einer eigenen View (`TemplatesView.vue`) und sind von regulären Listen getrennt.

**Negativ / Trade-offs:**
- **Keine User-Zuordnung bei Templates:** `createListFromTemplate` setzt keinen `user` auf der neuen Liste — die neu erstellte Liste ist keinem Nutzer zugeordnet.
- **Endpunkt-Design:** `newListName` als Query-Parameter statt im Request-Body ist unüblich für REST-APIs.
- **Keine Template-Versionierung:** Wenn ein Template geändert wird, werden bestehende, daraus erstellte Listen nicht aktualisiert.
- `getAllLists()` filtert mit `findByTemplate(false)` — Templates erscheinen nicht in der Hauptliste, reguläre Listen nicht in der Template-Liste. Diese Trennung ist korrekt, aber im Frontend nicht durch Router Guards abgesichert.

## Update (2026-07-07)

Die fehlende User-Zuordnung wurde behoben: `TodoListService.createListFromTemplate()` und `createList()` ermitteln den aktuell authentifizierten Benutzer über den `SecurityContextHolder` und weisen ihn der neu erstellten `TodoList` zu. Damit sind neu erstellte Listen (aus Template oder manuell) nicht mehr ohne `user`.

Nicht behoben, da über den Scope dieses Punktes hinausgehend: Die Lese-Endpunkte (`getAllLists`, `getListById`, `getAllTemplates`) filtern weiterhin nicht nach dem eingeloggten User — jeder authentifizierte Nutzer sieht weiterhin alle Listen. Eine vollständige Mandantentrennung wäre ein separates Architekturthema.

## Alternativen, die verworfen wurden

- **Separate `Template`-Entität:** Sauberer, aber mehr Duplizierung von Modell und Service-Logik.
- **Kopieren im Frontend:** Unsicher und umgeht die serverseitige Geschäftslogik.
