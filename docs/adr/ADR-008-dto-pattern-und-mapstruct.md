# ADR-008: DTO-Pattern und MapStruct für Objekt-Mapping

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Die JPA-Entitäten enthalten Datenbankdetails (Lazy-Loading, Bidirektionale Beziehungen), die nicht direkt als API-Response serialisiert werden sollten. Es braucht eine Trennung zwischen Datenbank-Modell und API-Modell.

## Entscheidung

**DTOs (Data Transfer Objects)** werden als separate Klassen für die API-Schicht definiert. Das Mapping zwischen Entität und DTO übernimmt **MapStruct** (v1.6.3), dessen Code zur Compile-Zeit generiert wird.

**DTO-Klassen:**

| DTO | Verwendung |
|---|---|
| `TodoListDto` | Todo-Listen in der API |
| `TodoDto` | Einzelne Todos |
| `CategoryDto` | Kategorien |
| `LoginRequest` / `LoginResponse` | Auth-Endpunkt |
| `RegisterRequest` | Registrierung |

**Mapper-Klassen:**
- `TodoListMapper`, `TodoMapper`, `CategoryMapper`

## Begründung

- **Entkopplung:** Entitäten können sich ändern, ohne die API-Schnittstelle zu brechen und umgekehrt.
- **Sicherheit:** Interne Felder (z. B. gehashte Passwörter) werden nicht versehentlich in der API exponiert.
- **Vermeidung von Lazy-Loading-Problemen:** JPA-Entitäten mit `FetchType.LAZY` können bei der JSON-Serialisierung `LazyInitializationException` werfen — DTOs umgehen das.
- **MapStruct:** Generiert typsicheren Mapping-Code zur Compile-Zeit (kein Reflection zur Laufzeit) → bessere Performance als ModelMapper.

## Konsequenzen

**Positiv:**
- Klare Schichtenarchitektur: Controller spricht nur DTOs, Service/Repository nur Entitäten.
- MapStruct-Code ist gut in IDEs sichtbar und debuggbar.
- Lombok + MapStruct + `lombok-mapstruct-binding` sind korrekt in der Compiler-Plugin-Konfiguration verknüpft.

**Negativ / Trade-offs:**
- Mehr Boilerplate: Jede Entität braucht ein korrespondierendes DTO und einen Mapper.
- Bei tief verschachtelten Objekten (z. B. `TodoList` → `Categories` → `Todos`) kann das Mapping komplex werden.
- **Input-Validierung fehlt:** Die DTOs nutzen aktuell keine `@Valid`/`@NotNull`-Annotationen — fehlerhafte Eingaben werden nicht serverseitig validiert.

## Update (2026-07-07)

Die fehlende Input-Validierung wurde behoben: `spring-boot-starter-validation` wurde ergänzt, die DTOs (`TodoListDto.name`, `TodoDto.title`, `CategoryDto.name`, `LoginRequest`, `RegisterRequest`) tragen jetzt `@NotBlank`/`@Size`-Annotationen, und die entsprechenden Controller-Methoden nehmen die Request-Bodies mit `@Valid` entgegen. Endpunkte, bei denen das DTO nur als ID-Träger dient (`addCategoryToTodoList`, `addTodoToCategory`), wurden bewusst ausgenommen, da dort nur die ID ausgewertet wird.

## Alternativen, die verworfen wurden

- **Direkte Entitäts-Serialisierung:** Einfacher, aber führt zu zirkulären Referenzen, Sicherheitsproblemen und starker Kopplung.
- **ModelMapper:** Reflection-basiert, daher langsamer und schwerer zu debuggen.
- **Record-Klassen (Java 16+):** Würden gut als immutable DTOs funktionieren — könnten in Zukunft evaluiert werden.
