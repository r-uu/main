# 📝 Config Properties - Anleitung

**Datum:** 2026-01-11  
**Zweck:** Lokale Entwicklungsumgebung konfigurieren

---

## 🎯 Wo muss ich was konfigurieren?

### Datei: `config.properties`
**Pfad:** `\\wsl.localhost\Ubuntu\home\r-uu\develop\github\main\config.properties`

**Wichtig:** Diese Datei ist LOKAL und wird NICHT ins Git committed!

---

## 🔧 Erforderliche Properties

### 1. Datenbank-Konfiguration (für Tests)

```properties
# Neue Property-Namen (bevorzugt)
database.host=localhost
database.port=5432
database.name=lib_test
database.user=lib_test
database.pass=lib_test
```

**Verwendet von:**
- `TestDataSourceFactory` im `jdbc.postgres` Modul
- Andere JDBC/JPA Tests

**Standardwerte:** localhost:5432, DB: lib_test, User: lib_test, Pass: lib_test

---

### 2. Legacy DB Properties (Optional)

```properties
# Alte Property-Namen (für Backward Compatibility)
db.host=localhost
db.port=5432
db.database=db_database
db.username=db_username
db.password=db_password
```

**Verwendet von:**
- Ältere Tests und Konfigurationen

---

### 3. Backup/Restore Konfiguration (Optional)

```properties
db.backup.host=${db.host}
db.backup.port=${db.port}
db.backup.database=${db.database}
db.backup.username=${db.username}
db.backup.password=${db.password}
db.backup.directory=/tmp/db-backup
db.backup.executable=/usr/bin/pg_dump

db.restore.host=${db.host}
db.restore.port=${db.port}
db.restore.database=${db.database}
db.restore.username=${db.username}
db.restore.password=${db.password}
db.restore.executable=/usr/bin/pg_restore
```

---

### 4. REST API Konfiguration (Optional)

```properties
rest.api.host=localhost
rest.api.port=9080
```

---

### 5. Keycloak Konfiguration (Optional)

```properties
keycloak.host=localhost
keycloak.port=8080
keycloak.realm=jeeeraaah-realm
keycloak.client.id=keycloak_client_id
keycloak.client.secret=keycloak_client_secret
```

---

## 🚀 Setup-Prozess

### 1. Datei erstellen
```bash
cd /home/r-uu/develop/github/main
touch config.properties
```

### 2. Minimal-Konfiguration (für JDBC Tests)

```properties
# Minimal config für Tests
database.host=localhost
database.port=5432
database.name=lib_test
database.user=lib_test
database.pass=lib_test
```

### 3. PostgreSQL Docker Container starten (optional)

Wenn du keinen PostgreSQL Server hast:

```bash
cd /home/r-uu/develop/github/main/config/shared/docker
docker-compose up -d postgres
```

Die `docker-compose.yml` sollte einen Container mit:
- Port: 5432
- Database: lib_test
- User: lib_test
- Password: lib_test

erstellen.

---

## 🔍 Wie funktioniert es?

### Maven Properties Plugin

Das BOM hat ein Plugin konfiguriert:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>properties-maven-plugin</artifactId>
    <executions>
        <execution>
            <phase>initialize</phase>
            <goals>
                <goal>read-project-properties</goal>
            </goals>
            <configuration>
                <files>
                    <file>${maven.multiModuleProjectDirectory}/config.properties</file>
                </files>
                <quiet>true</quiet>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Was passiert:**
1. Maven liest `config.properties` beim Build
2. Properties werden als Maven-Properties verfügbar
3. Resource-Filtering ersetzt `${property}` in `.properties` Dateien
4. MicroProfile Config liest die gefilterten Properties zur Laufzeit

---

## ⚠️ Fehlerbehebung

### Problem: "port out of range:-1"

**Ursache:** `config.properties` existiert nicht oder fehlt `database.port`

**Lösung:**
1. Erstelle `config.properties` im Hauptverzeichnis
2. Füge mindestens hinzu:
   ```properties
   database.host=localhost
   database.port=5432
   ```

### Problem: Test wird übersprungen

**Ursache:** PostgreSQL Server läuft nicht auf `database.host:database.port`

**Lösung:**
1. Starte PostgreSQL Server
2. Oder: Ändere `database.host` und `database.port` in `config.properties`

### Problem: Connection refused

**Ursache:** 
- PostgreSQL läuft nicht
- Falsche Host/Port Konfiguration
- Firewall blockiert

**Lösung:**
```bash
# Prüfe ob PostgreSQL läuft
sudo systemctl status postgresql

# Oder via Docker
docker ps | grep postgres

# Teste Verbindung
psql -h localhost -p 5432 -U lib_test -d lib_test
```

---

## 📁 Git-Handling

### .gitignore

Die `config.properties` sollte in `.gitignore` sein:

```gitignore
# Local configuration (machine-specific)
config.properties
```

**Warum?**
- Jeder Entwickler hat andere lokale Settings
- Passwörter sollten nicht committed werden
- Verhindert Konflikte bei Team-Arbeit

---

## 📚 Template für neue Entwickler

Erstelle ein `config.properties.template`:

```properties
# Configuration Template
# Copy to config.properties and adjust values

# Database Configuration
database.host=localhost
database.port=5432
database.name=lib_test
database.user=lib_test
database.pass=lib_test

# ... weitere Properties ...
```

Dann kann jeder Entwickler:
```bash
cp config.properties.template config.properties
# ... Werte anpassen ...
```

---

## ✅ Checkliste

Nach dem Setup sollte gelten:

- [x] `config.properties` existiert im Hauptverzeichnis
- [x] `database.host` ist definiert
- [x] `database.port` ist definiert
- [x] PostgreSQL Server läuft (falls Tests ausgeführt werden sollen)
- [ ] `config.properties` ist in `.gitignore`
- [ ] Build funktioniert: `mvn clean install`
- [ ] JDBC Tests laufen durch (oder werden korrekt übersprungen)

---

**Status:** ✅ `config.properties` wurde erstellt mit Standard-Werten!

Falls du andere Werte brauchst, passe die Datei an deine lokale Umgebung an.

