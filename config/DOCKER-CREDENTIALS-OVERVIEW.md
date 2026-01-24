# Docker Umgebung - Credentials Übersicht

## 📋 Gesamtübersicht

Die Docker-Umgebung verwendet **4 verschiedene Benutzer** für unterschiedliche Zwecke:

### 🔑 Credential-Matrix

| System | Container | Zweck | Username | Password | DB/Service |
|--------|-----------|-------|----------|----------|------------|
| **PostgreSQL JEEERAaH** | `postgres-jeeeraaah` | App-Datenbank | `r_uu` | `r_uu_password` | `jeeeraaah` |
| **PostgreSQL Keycloak** | `postgres-keycloak` | Keycloak-Datenbank | `keycloak` | `keycloak_password` | `keycloak` |
| **Keycloak Admin** | `keycloak` | Admin-Zugang | `admin` | `admin` | - |
| **Application Test** | - | Testing | `r_uu` | `r_uu_password` | - |

## 🗂️ .env Datei Struktur

### Lokale .env Datei (wird NICHT committed!)

```bash
# postgres-jeeeraaah
POSTGRES_JEEERAAAH_DB=jeeeraaah
POSTGRES_JEEERAAAH_USER=r_uu
POSTGRES_JEEERAAAH_PASSWORD=r_uu_password

# postgres-keycloak
POSTGRES_KEYCLOAK_DB=keycloak
POSTGRES_KEYCLOAK_USER=keycloak
POSTGRES_KEYCLOAK_PASSWORD=keycloak_password

# keycloak admin
KC_BOOTSTRAP_ADMIN_USERNAME=admin
KC_BOOTSTRAP_ADMIN_PASSWORD=admin

# application test
TEST_USERNAME=r_uu
TEST_PASSWORD=r_uu_password
```

### .env.template Datei (wird committed!)

Siehe `config/shared/docker/.env.template` - enthält nur `YOUR_*` Platzhalter.

## 🎯 Verwendungszwecke

### 1. PostgreSQL JEEERAaH (Port 5432)

**Zweck**: Hauptdatenbank für die JEEERAaH Application

**Verwendung in:**
- Application Server (Liberty)
- JPA/Hibernate Persistence
- Testing (via `testing.properties`)

**Verbindung:**
```properties
db.host=localhost
db.port=5432
db.name=jeeeraaah
db.user=r_uu
db.password=r_uu_password
```

### 2. PostgreSQL Keycloak (Port 5433)

**Zweck**: Dedizierte Datenbank für Keycloak

**Verwendung in:**
- Keycloak Container (intern)
- Speichert Realms, Users, Roles, etc.

**Verbindung (nur intern im Container):**
```
jdbc:postgresql://postgres-keycloak:5432/keycloak
User: keycloak
Password: keycloak_password
```

### 3. Keycloak Admin

**Zweck**: Admin-Zugang zur Keycloak Console

**Verwendung:**
- Browser Login: http://localhost:8080/admin
- Username: `admin`
- Password: `admin`

**Wichtig:** Dieser User existiert NUR im Keycloak System, NICHT in PostgreSQL!

### 4. Application Test User

**Zweck**: Test-User für automatische Tests

**Verwendung in:**
- `testing.properties`
- `DashApp` im Test-Modus
- Automatische Health-Checks

**Keycloak Setup:**
- Wird im `jeeeraaah-realm` angelegt
- Hat alle erforderlichen Rollen
- Erlaubt Direct Access Grants

## 🔧 Wie .env Dateien funktionieren

### Setup

1. **Template kopieren:**
   ```bash
   cd ~/develop/github/main/config/shared/docker
   cp .env.template .env
   ```

2. **Platzhalter ersetzen:**
   - Öffne `.env`
   - Ersetze alle `YOUR_*` mit echten Werten
   - Speichern

3. **Docker Compose liest .env automatisch:**
   ```bash
   docker compose up -d
   ```

### Docker Compose Syntax

In `docker-compose.yml`:
```yaml
environment:
  POSTGRES_USER: ${POSTGRES_JEEERAAAH_USER}
  POSTGRES_PASSWORD: ${POSTGRES_JEEERAAAH_PASSWORD}
```

Docker Compose ersetzt `${VAR}` automatisch mit Werten aus `.env`.

## 🚨 Sicherheit

### ✅ Best Practices

1. **Nie echte Credentials committen!**
   - `.env` ist in `.gitignore`
   - Nur `.env.template` mit Platzhaltern wird committed

2. **Verschiedene Credentials verwenden:**
   - JEEERAaH DB: `r_uu` / `r_uu_password`
   - Keycloak DB: `keycloak` / `keycloak_password`
   - Keycloak Admin: `admin` / `admin`

3. **Production:**
   - Starke Passwörter verwenden!
   - Umgebungsvariablen aus Secret Management
   - Keine `.env` Dateien in Production

### ❌ Häufige Fehler

1. **`.env` Datei fehlt**
   ```bash
   # Fehler: docker compose kann Container nicht starten
   # Lösung: .env aus .env.template erstellen
   ```

2. **Platzhalter nicht ersetzt**
   ```bash
   # Fehler: PostgreSQL User "YOUR_USERNAME" existiert nicht
   # Lösung: Alle YOUR_* in .env ersetzen
   ```

3. **Alte hardcodierte Werte**
   ```yaml
   # ❌ Falsch:
   POSTGRES_USER: r_uu
   
   # ✅ Richtig:
   POSTGRES_USER: ${POSTGRES_JEEERAAAH_USER}
   ```

## 📝 Checkliste: Neue Umgebung einrichten

- [ ] `.env.template` nach `.env` kopieren
- [ ] Alle `YOUR_*` Platzhalter mit echten Werten ersetzen
- [ ] `.env` ist in `.gitignore` (nicht committen!)
- [ ] `docker compose up -d` startet alle Container
- [ ] PostgreSQL JEEERAaH läuft auf Port 5432
- [ ] PostgreSQL Keycloak läuft auf Port 5433
- [ ] Keycloak läuft auf Port 8080
- [ ] Keycloak Admin Login funktioniert (admin/admin)
- [ ] Realm Setup ausführen (`KeycloakRealmSetup.java`)
- [ ] Test-User `r_uu` existiert im Realm
- [ ] Application startet und verbindet sich erfolgreich

## 🔍 Troubleshooting

### Container startet nicht

```bash
# Prüfe Logs
docker logs postgres-jeeeraaah
docker logs keycloak

# Häufiger Fehler: .env Variablen nicht gesetzt
docker compose config  # Zeigt aufgelöste Werte
```

### Falsche Credentials

```bash
# PostgreSQL: Prüfe Umgebungsvariablen im Container
docker exec postgres-jeeeraaah env | grep POSTGRES

# Keycloak: Prüfe Umgebungsvariablen
docker exec keycloak env | grep KC
```

### Alles neu aufsetzen

```bash
# Kompletter Reset
cd ~/develop/github/main/config/shared/docker
docker compose down -v
docker volume rm postgres-jeeeraaah-data postgres-keycloak-data keycloak-data
docker compose up -d

# Warten bis Container healthy sind
docker ps

# Realm neu erstellen
cd ~/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

## 📚 Weitere Dokumentation

- [Keycloak Admin Console Anleitung](../KEYCLOAK-ADMIN-CONSOLE.md)
- [Docker Compose Dokumentation](https://docs.docker.com/compose/environment-variables/)
- [.gitignore Dokumentation](../.gitignore)
