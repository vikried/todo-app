# ADR-010: SpringDoc OpenAPI (Swagger) für API-Dokumentation

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Eine REST-API sollte dokumentiert sein, damit Frontend-Entwickler (oder zukünftige Konsumenten) die verfügbaren Endpunkte, Parameter und Response-Schemas ohne Codeinsicht verstehen können.

## Entscheidung

**springdoc-openapi-starter-webmvc-ui** (v2.6.0) wird für die automatische OpenAPI-3-Dokumentation eingesetzt. Die Swagger-UI ist unter `/swagger-ui.html` erreichbar.

Alle drei Controller sind mit OpenAPI-Annotationen dokumentiert:

```java
@Tag(name = "TodoList Controller", description = "Verwaltet Todo-Listen")
@Operation(summary = "...", description = "...")
@ApiResponse(responseCode = "200", ...)
```

## Begründung

- **Automatische Generierung:** springdoc leitet das Basis-Schema aus Spring-MVC-Annotationen ab — nur Beschreibungen müssen manuell hinzugefügt werden.
- **Swagger-UI:** Bietet eine interaktive Web-Oberfläche zum Testen der API ohne externes Tool (z. B. Postman).
- **OpenAPI 3:** Moderner Standard, kompatibel mit Code-Generatoren und API-Gateways.
- **Separater Config-Bean:** `OpenApiConfig.java` konfiguriert Metadaten (Titel, Version, Beschreibung) zentral.

## Konsequenzen

**Positiv:**
- API ist selbst-dokumentierend und für alle im Team zugänglich.
- Kein manueller Pflegeaufwand für ein separates API-Dokument.
- Request-/Response-Schemas werden aus den DTO-Klassen automatisch abgeleitet.

**Negativ / Trade-offs:**
- Die Swagger-UI ist in der aktuellen Konfiguration öffentlich zugänglich — in Produktion sollte sie hinter einem Auth-Gate oder komplett deaktiviert werden.
- Annotationen erhöhen den Code-Umfang in den Controllern, erschweren die Lesbarkeit bei vielen Endpunkten.

## Alternativen, die verworfen wurden

- **Manuell gepflegte OpenAPI YAML:** Mehr Kontrolle, aber hoher Pflegeaufwand und Gefahr der Inkonsistenz mit dem Code.
- **Postman-Collection:** Kein Standard, nicht im Code verankert.
