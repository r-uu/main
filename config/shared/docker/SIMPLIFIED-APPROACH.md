# Vereinfachte Credential-Strategie

## Problem
- Zu viele Konfigurationsebenen (.env, .properties, Maven, Docker)
- Line-Ending-Probleme (Windows ↔ WSL)
- Schwer zu debuggen

## Lösung: 2-Schicht-Ansatz

### Schicht 1: Docker-Umgebung (`.env` in WSL)
**Nur für Docker Compose!**
```bash
# In WSL erstellen: ~/develop/github/main/config/shared/docker/.env
# WICHTIG: Mit LF Line-Endings!

# PostgreSQL JEEERAaH
postgres_jeeeraaah_database=jeeeraaah
postgres_jeeeraaah_user=jeeeraaah
postgres_jeeeraaah_password=jeeeraaah

# PostgreSQL lib_test
postgres_lib_test_database=lib_test
postgres_lib_test_user=lib_test
postgres_lib_test_password=lib_test

# PostgreSQL Keycloak
postgres_keycloak_database=keycloak
postgres_keycloak_user=keycloak
postgres_keycloak_password=keycloak

# Keycloak Admin
keycloak_admin_user=admin
keycloak_admin_password=admin
```

### Schicht 2: Java-Anwendung (Properties)
**Hardcoded Development Defaults!**

Für Development KEIN Variablen-Ersatz, sondern:

#### `testing.properties` (im Git, für Tests)
```properties
# PostgreSQL JEEERAaH
db.jeeeraaah.host=localhost
db.jeeeraaah.port=5432
db.jeeeraaah.database=jeeeraaah
db.jeeeraaah.user=jeeeraaah
db.jeeeraaah.password=jeeeraaah

# PostgreSQL lib_test
db.lib_test.host=localhost
db.lib_test.port=5432
db.lib_test.database=lib_test
db.lib_test.user=lib_test
db.lib_test.password=lib_test

# Keycloak
keycloak.url=http://localhost:8080
keycloak.realm=jeeeraaah-realm
keycloak.admin.user=admin
keycloak.admin.password=admin

# Testing Mode
testing.enabled=true
```

#### `local.properties` (nicht im Git, für echte Systeme)
```properties
# Wenn Sie echte Credentials brauchen, überschreiben Sie hier
# Diese Datei wird von .gitignore ignoriert
db.jeeeraaah.password=geheim
keycloak.admin.password=sicheresPasswort
```

## Vorteile
✅ **Keine Line-Ending-Probleme** - `.properties` sind robust
✅ **Kein Maven-Filtering** - Werte direkt lesbar, kein Build-Schritt nötig
✅ **Einfach zu debuggen** - Man sieht sofort was geladen wird
✅ **Getrennte Concerns** - Docker ≠ Java
✅ **Funktioniert in WSL** - Keine Windows-Pfad-Probleme
✅ **MicroProfile Config** - Standard-konform, funktioniert überall

## Migration
1. ✅ `.env` nur für Docker Compose (mit LF!) → `/config/shared/docker/.env`
2. ✅ `testing.properties` mit festen Werten → `/testing.properties` (im Root)
3. ✅ **KEIN** Maven Resource Filtering mehr!
4. ✅ Keine verschachtelten Property-Referenzen mehr!
5. ✅ `local.properties` in `.gitignore` für lokale Overrides

## Für Produktion
- Umgebungsvariablen (höchste Priorität in MicroProfile Config)
- Externe Config-Server (z.B. Spring Cloud Config, Consul)
- Kubernetes ConfigMaps/Secrets

## MicroProfile Config - Prioritätsreihenfolge
1. **System Properties** (-Dkey=value beim Java-Start)
2. **Umgebungsvariablen** (KEYCLOAK_URL, DB_PASSWORD, etc.)
3. **`local.properties`** (nicht im Git, für lokale Entwicklung)
4. **`testing.properties`** (im Git, Development-Defaults)
5. **`microprofile-config.properties`** (in META-INF, selten benötigt)
