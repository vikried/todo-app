# ADR-005: Spring Boot 3 als Backend-Framework

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Das Backend muss eine REST-API bereitstellen, die von einem Vue-Frontend konsumiert wird. Es verwaltet Daten in einer relationalen Datenbank und erfordert Authentifizierung.

## Entscheidung

**Spring Boot 3.3** mit Java 17 wird als Backend-Framework eingesetzt. Der Aufbau folgt einem klassischen Schichtenmodell:

```
Controller → Service → Repository (JPA) → PostgreSQL
```

## Begründung

- **Produktionsreife:** Spring Boot ist das de-facto-Standard-Framework für Java-Backends; großes Ökosystem, aktive Weiterentwicklung.
- **Spring Boot 3 / Jakarta EE:** Nutzt `jakarta.*`-Namespaces (statt `javax.*`), d. h. es ist zukunftssicher für Java 17+.
- **Auto-Konfiguration:** Datenbankverbindung, Security, JPA — alles wird per `application.properties` ohne XML-Konfiguration eingerichtet.
- **Spring Security:** Authentifizierung und Autorisierung sind durch das bestehende Security-Ökosystem gut integrierbar.
- **Lombok:** Reduziert Boilerplate-Code in Modellen und Services erheblich.

## Konsequenzen

**Positiv:**
- Schnelle Entwicklung durch Spring Boot Starter-Dependencies.
- `spring.jpa.hibernate.ddl-auto=update` ermöglicht automatisches Schema-Update ohne Migrations-Skripte.
- OpenAPI/Swagger-Dokumentation mit minimalem Aufwand (springdoc-openapi).

**Negativ / Trade-offs:**
- Längere Startzeit im Vergleich zu Micronaut oder Quarkus.
- `ddl-auto=update` ist für Produktion riskant — fehlende Daten-Migrations-Strategie (z. B. Flyway/Liquibase).
- `@CrossOrigin(origins = "*")` in allen Controllern ist für Produktion zu permissiv.

## Alternativen, die verworfen wurden

- **Quarkus:** Bessere GraalVM-Native-Image-Unterstützung, aber kleineres Ökosystem und höhere Einstiegshürde.
- **Node.js / Express:** Würde eine zweite Sprache im Stack einführen; Java-Expertise war ausschlaggebend.
- **Micronaut:** Schneller Start, aber weniger verbreitet und weniger Ressourcen für Einsteiger.
