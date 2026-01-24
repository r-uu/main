# Docker Environment Credentials - Status 2026-01-24

## ✅ Aktuelle Konfiguration

Alle Credentials sind in `.env` konsolidiert und folgen dem Muster:

```
variable_name=variable_name
```

**Beispiel:**
```
keycloak_admin_username=admin
keycloak_admin_password=admin
```

Dies bedeutet: Der **tatsächliche Username** ist "admin" und das **tatsächliche Passwort** ist "admin".

### Warum diese Konvention?

1. **Debugging-Freundlich**: Man sieht sofort, welche Variable verwendet wird
2. **Konsistenz**: Keine Verwirrung zwischen Variablennamen und Werten
3. **Entwicklungsumgebung**: Passwörter müssen nicht stark sein

## Container & Credentials

### postgres-jeeeraaah (Port 5432)

**Datenbanken:**
- `jeeeraaah` (Haupt-App)
- `lib_test` (Tests, via Init-Skript)

**Credentials:**
```bash
Username: postgres_jeeeraaah_username
Password: postgres_jeeeraaah_password
```

**Verbindung testen:**
```bash
docker exec -it postgres-jeeeraaah psql -U postgres_jeeeraaah_username -d jeeeraaah
```

### postgres-keycloak (Port 5433)

**Datenbank:**
- `keycloak`

**Credentials:**
```bash
Username: postgres_keycloak_username
Password: postgres_keycloak_password
```

**Verbindung testen:**
```bash
docker exec -it postgres-keycloak psql -U postgres_keycloak_username -d keycloak
```

### keycloak (Port 8080)

**Admin Console:** http://localhost:8080/admin

**Admin Credentials:**
```bash
Username: admin
Password: admin
```

**Test User (Application):**
```bash
Username: test_username
Password: test_password
```

### jasperreports (Port 8090)

**Health:** http://localhost:8090/health

Keine Credentials erforderlich.

## ✅ Vollautomatisches Setup

### 1. Kompletter Reset

```bash
ruu-docker-reset
```

Dies führt aus:
1. `docker compose down -v` - Stoppt alle Container & löscht Volumes
2. `docker compose up -d` - Startet alle Container neu
3. Init-Skripte laufen automatisch (lib_test DB wird erstellt)

### 2. Keycloak Realm erstellen

```bash
ruu-keycloak-setup
```

Dies führt aus:
1. Lädt `.env` in Shell Environment
2. Führt `KeycloakRealmSetup.java` aus
3. Erstellt Realm "jeeeraaah-realm" mit Client und Test-User

### 3. Verify Setup

```bash
docker ps  # Alle Container healthy?
ruu-keycloak-admin  # Zeigt Keycloak Admin Console URL
```

## Java Health Checks

Die Health Checks in `de.ruu.lib.docker.health.check.*` verwenden die gleichen Variablennamen:

```java
// PostgresDatabaseHealthCheck
if ("keycloak".equals(databaseName)) {
    user = "postgres_keycloak_username";
    pass = "postgres_keycloak_password";
} else {
    user = "postgres_jeeeraaah_username";
    pass = "postgres_jeeeraaah_password";
}
```

## Testing Properties

`testing.properties` verwendet:

```properties
test.username=test_username
test.password=test_password
```

Diese werden in die Environment-Variablen gemappt.

## Wichtige Befehle (Aliases)

```bash
# Docker
ruu-docker-up          # Starte alle Container
ruu-docker-down        # Stoppe alle Container
ruu-docker-reset       # Reset: down -v && up -d
ruu-docker-ps          # Status aller Container

# PostgreSQL
ruu-postgres-ensure-lib-test   # lib_test DB erstellen (falls nicht vorhanden)
ruu-postgres-reset-lib-test    # lib_test DB neu erstellen

# Keycloak
ruu-keycloak-start     # Keycloak Container starten
ruu-keycloak-setup     # Realm erstellen
ruu-keycloak-reset     # Realm neu erstellen
ruu-keycloak-admin     # Admin Console URL anzeigen
ruu-keycloak-logs      # Logs anzeigen

# JasperReports
ruu-jasper-start       # JasperReports starten
ruu-jasper-test        # Health Check
ruu-jasper-logs        # Logs anzeigen
```

## ⚠️ Bekannte Probleme & Lösungen

### Problem: "Database lib_test does not exist"

**Ursache:** Init-Skript nicht ausgeführt

**Lösung:**
```bash
docker compose restart postgres-jeeeraaah
# Oder:
ruu-postgres-ensure-lib-test
```

### Problem: "Keycloak 401 Unauthorized" bei Realm Setup

**Ursache:** Environment-Variablen nicht geladen

**Lösung:**
```bash
source ~/develop/github/main/config/shared/docker/.env
ruu-keycloak-setup
```

**Oder explizit:**
```bash
cd ~/develop/github/main/root/lib/keycloak.admin
mvn exec:java \
  -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup" \
  -Dkeycloak.admin.user=keycloak_admin_username \
  -Dkeycloak.admin.password=keycloak_admin_password
```

### Problem: "Keycloak Realm fehlt"

**Ursache:** Realm wurde nicht erstellt oder Container wurde zurückgesetzt

**Lösung:**
```bash
ruu-keycloak-setup
```

## Dateien

- **`.env`** - Lokale Credentials (nicht versioniert)
- **`.env.template`** - Template für .env (versioniert)
- **`docker-compose.yml`** - Container-Konfiguration
- **`initdb/01-init-databases.sh`** - PostgreSQL Init-Skript (lib_test)
- **`aliases.sh`** - Bash Aliases
- **`DOCKER-ENV-SETUP.md`** - Diese Datei

## Next Steps für Produktivumgebung

1. `.env` mit starken Passwörtern erstellen:
   ```
   keycloak_admin_username=admin_prod_xy123
   keycloak_admin_password=SuperSecure!Pa$$w0rd#2024
   ```

2. `.env` NIE ins Git committen (steht bereits in .gitignore)

3. Secrets Management System nutzen (z.B. HashiCorp Vault, AWS Secrets Manager)
