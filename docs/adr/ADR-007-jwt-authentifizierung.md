# ADR-007: JWT-basierte Authentifizierung

**Status:** Accepted  
**Datum:** 2025  
**Autor:** Viktor Riediger

---

## Kontext

Die API muss vor unbefugtem Zugriff geschützt werden. Da das Frontend eine SPA ist und das Backend zustandslos sein soll, muss eine token-basierte Authentifizierungsstrategie gewählt werden.

## Entscheidung

**JWT (JSON Web Token)** wird für die Authentifizierung eingesetzt, implementiert über `jjwt` (v0.11.5) und Spring Security.

**Ablauf:**
1. POST `/api/auth/register` → Nutzer registrieren
2. POST `/api/auth/login` → JWT-Token erhalten
3. Alle übrigen Endpoints → `Authorization: Bearer <token>` erforderlich

**Beteiligte Klassen:**
- `SecurityConfig.java` — Filter-Chain, BCrypt, JWT-Filter
- `JwtFilter.java` — Token-Validierung pro Request (im `security`-Package)
- `LoginRequest.java` / `LoginResponse.java` / `RegisterRequest.java` — Auth-DTOs

## Begründung

- **Zustandslosigkeit:** JWTs tragen alle nötigen Informationen im Token selbst — der Server muss keine Session im Speicher halten.
- **SPA-Kompatibilität:** Token lassen sich leicht im Browser (localStorage/Cookie) speichern und bei jedem Request mitsenden.
- **Spring Security Integration:** Der `JwtFilter` wird über `addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)` eingebunden.
- **BCrypt:** Passwörter werden sicher gehasht (`BCryptPasswordEncoder`).

## Konsequenzen

**Positiv:**
- Horizontal skalierbar — kein Session-Sharing zwischen Server-Instanzen nötig.
- `/api/auth/**` ist öffentlich zugänglich; alle anderen Routen sind automatisch geschützt.
- CSRF ist deaktiviert (`.csrf().disable()`), was bei stateless JWT-APIs korrekt ist.

**Negativ / Trade-offs:**
- **Token-Invalidierung:** JWTs können vor Ablauf nicht serverseitig invalidiert werden (kein Logout-Blacklisting implementiert).
- **Token-Speicherung im Frontend:** Es ist nicht erkennbar, ob das Token im `localStorage` (XSS-Risiko) oder als `httpOnly`-Cookie gespeichert wird.
- **CORS:** `@CrossOrigin(origins = "*")` in allen Controllern ist für Produktion zu permissiv — sollte auf bekannte Origins eingeschränkt werden.
- `spring-security-jwt` (`1.1.0.RELEASE`) ist eine ältere Bibliothek; `jjwt` (`0.11.5`) ist der eigentliche JWT-Anbieter.

## Update (2026-07-07)

Zwei der genannten Trade-offs wurden behoben:

- **Token-Invalidierung:** `TokenBlacklistService` hält widerrufene Tokens serverseitig in einem In-Memory-Set vor; `JwtFilter` lehnt Requests mit geblacklisteten Tokens ab. Ein neuer Endpoint `POST /api/auth/logout` blacklistet den mitgesendeten Token. Einschränkung: Der Blacklist-Speicher ist pro Instanz — bei mehreren Backend-Instanzen wäre ein geteilter Store (z. B. Redis) nötig.
- **CORS:** Die redundanten `@CrossOrigin(origins = "*")`-Annotationen wurden aus allen Controllern entfernt. Die zentrale `CorsConfigurationSource` in `SecurityConfig` nutzt jetzt eine konfigurierbare Origin-Liste (`app.cors.allowed-origins`, per `CORS_ALLOWED_ORIGINS`-Umgebungsvariable überschreibbar) statt eines Wildcards.

Weiterhin offen: Wo das Frontend den Token speichert (localStorage vs. httpOnly-Cookie) ist unverändert nicht erkennbar, da das Frontend aktuell noch keine Login-UI/Token-Handling besitzt.

## Alternativen, die verworfen wurden

- **Session-basierte Authentifizierung:** Erfordert serverseitigen Session-Speicher, was Zustandslosigkeit bricht.
- **OAuth2 / Keycloak:** Zu viel Overhead für eine selbst gehostete Einzel-Anwendung.
- **Spring Session:** Gute Alternative für zustandsbehaftete Szenarien, aber nicht sinnvoll bei reinen SPAs.
