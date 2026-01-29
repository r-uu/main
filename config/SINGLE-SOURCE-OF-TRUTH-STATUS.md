# Single Source of Truth - Configuration Status

**Datum**: 2026-01-24  
**Status**: ✅ Implementiert

## Übersicht

Die `.env` Datei unter `config/shared/docker/` ist nun der **Single Source of Truth** für alle Credentials und Konfigurationswerte im gesamten Projekt.

## Konfigurationsdateien aktualisiert

### 1. ✅ Docker Compose (`config/shared/docker/docker-compose.yml`)
- Verwendet `.env` Variablen direkt
- Alle Postgres- und Keycloak-Container konfiguriert

### 2. ✅ Liberty Server (`root/app/jeeeraaah/backend/api/ws.rs/src/main/liberty/config/server.env`)
- Verwendet `.env` Variablen über Maven Resource Filtering
- Database-Credentials: `postgres_jeeeraaah_*`
- Keycloak-URL: `keycloak_admin_*`

### 3. ✅ Testing Properties (`testing.properties`)
- Test-User-Credentials: `app_test_user_*`
- Keycloak-Konfiguration

### 4. ✅ Config Properties (`config.properties`)
- Alle Datenbankverbindungen
- Keycloak-Konfiguration
- Liberty-Konfiguration

### 5. ✅ KeycloakRealmSetup.java
- Verwendet `.env` Variablen über Environment Variables
- Test-User-Credentials: `app_test_user_*`
- Admin-Credentials: `keycloak_admin_*`

## Verwendete .env Variablen

```bash
# PostgreSQL jeeeraaah Database
postgres_jeeeraaah_host=localhost
postgres_jeeeraaah_port=5432
postgres_jeeeraaah_database=jeeeraaah
postgres_jeeeraaah_user=jeeeraaah
postgres_jeeeraaah_password=jeeeraaah

# PostgreSQL lib_test Database
postgres_lib_test_host=localhost
postgres_lib_test_port=5432
postgres_lib_test_database=lib_test
postgres_lib_test_user=jeeeraaah
postgres_lib_test_password=jeeeraaah

# PostgreSQL Keycloak Database
postgres_keycloak_host=localhost
postgres_keycloak_port=5433
postgres_keycloak_database=keycloak
postgres_keycloak_user=keycloak
postgres_keycloak_password=keycloak

# Keycloak Admin
keycloak_admin_host=localhost
keycloak_admin_port=8080
keycloak_admin_user=admin
keycloak_admin_password=admin

# Application Test User
app_test_user_username=test
app_test_user_password=test
```

## Wie die Konfiguration funktioniert

### Maven Resource Filtering
- `pom.xml` aktiviert Resource Filtering für `.env` Datei
- Maven ersetzt `${variable_name}` in Konfigurationsdateien
- Build-Zeit-Ersetzung für `server.env`, `testing.properties`, etc.

### Environment Variables
- Java-Code liest direkt aus `System.getenv("variable_name")`
- KeycloakRealmSetup.java nutzt diesen Mechanismus
- Docker Compose lädt `.env` automatisch

## Vorteile

1. **Eine Quelle**: Alle Credentials in einer Datei
2. **Konsistenz**: Keine Diskrepanzen zwischen Konfigurationen
3. **Sicherheit**: `.env` ist in `.gitignore` (nicht versioniert)
4. **Template**: `.env.template` zeigt benötigte Variablen
5. **Debugging**: Einfacher zu finden wo Credentials definiert sind

## Nächste Schritte

1. ✅ Docker-Umgebung neu starten mit aktuellen Credentials
2. ✅ Keycloak Realm Setup ausführen
3. ✅ Liberty Server neu starten
4. ✅ DashAppRunner testen

## Wichtige Hinweise

### Credentials ändern
**NUR in `.env` ändern**, dann:
```bash
# Docker neu starten
cd ~/develop/github/main/config/shared/docker
docker compose down
docker compose up -d

# Maven neu builden (für filtered resources)
cd ~/develop/github/main/root
mvn clean install
```

### Neue Konfigurationswerte hinzufügen
1. In `.env` definieren
2. In `.env.template` dokumentieren  
3. In benötigten Konfigurationsdateien referenzieren
4. Maven neu builden

## Dateien die .env verwenden

- `config/shared/docker/docker-compose.yml` (direkt)
- `config/shared/docker/.env` (Source of Truth)
- `config.properties` (Maven filtered)
- `testing.properties` (Maven filtered)
- `server.env` (Maven filtered)
- `KeycloakRealmSetup.java` (Environment Variables)
- Alle Shell-Skripte die Docker starten

## Validierung

✅ Keine Hardcoded Credentials mehr im Projekt  
✅ Alle Konfigurationen verwenden `.env` Variablen  
✅ `.env.template` dokumentiert alle benötigten Variablen  
✅ `.env` ist in `.gitignore`
