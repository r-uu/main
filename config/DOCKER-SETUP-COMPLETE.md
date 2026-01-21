# ✅ Docker Setup - Erfolgreich konfiguriert

**Datum:** 2026-01-20

---

## 🐳 Laufende Container

Alle Container wurden neu gestartet gemäß `docker-compose.yml`:

| Container | Status | Port | Beschreibung |
|-----------|--------|------|--------------|
| **postgres-jeeeraaah** | ✅ healthy | 5432 | PostgreSQL für JEEERAaH & Tests |
| **postgres-keycloak** | ✅ healthy | 5433 | PostgreSQL für Keycloak |
| **keycloak** | ✅ healthy | 8080 | Identity & Access Management |

---

## 🗄️ Datenbank-Konfiguration

### PostgreSQL Container: postgres-jeeeraaah

**Port:** 5432

#### Datenbank 1: jeeeraaah (Production)
```properties
Host:     localhost
Port:     5432
Database: jeeeraaah
User:     r_uu
Password: r_uu_password
```

**JDBC URL:**
```
jdbc:postgresql://localhost:5432/jeeeraaah?user=r_uu&password=r_uu_password
```

#### Datenbank 2: lib_test (Integration Tests)
```properties
Host:     localhost
Port:     5432
Database: lib_test
User:     lib_test
Password: lib_test
```

**JDBC URL:**
```
jdbc:postgresql://localhost:5432/lib_test?user=lib_test&password=lib_test
```

---

### PostgreSQL Container: postgres-keycloak

**Port:** 5433

```properties
Host:     localhost
Port:     5433
Database: keycloak
User:     r_uu
Password: r_uu_password
```

**JDBC URL:**
```
jdbc:postgresql://localhost:5433/keycloak?user=r_uu&password=r_uu_password
```

---

## 🔐 Keycloak

**Admin Console:** http://localhost:8080/admin

```properties
Username: admin
Password: admin
```

⚠️ **Wichtig:** Ändern Sie das Admin-Passwort in der Produktion!

---

## 📝 config.properties

Die Datei `/home/r-uu/develop/github/main/config.properties` wurde aktualisiert mit:

### Für Production (JEEERAaH App)
```properties
db.host=localhost
db.port=5432
db.database=jeeeraaah
db.username=r_uu
db.password=r_uu_password
```

### Für Integration Tests
```properties
# Database Configuration (new naming convention)
database.host=localhost
database.port=5432
database.name=lib_test
database.user=lib_test
database.pass=lib_test

# Hibernate Postgres EntityManager Producer
de.ruu.lib.jpa.se.hibernate.postgres.AbstractEntityManagerProducer.dbhost=localhost
de.ruu.lib.jpa.se.hibernate.postgres.AbstractEntityManagerProducer.dbport=5432
de.ruu.lib.jpa.se.hibernate.postgres.AbstractEntityManagerProducer.dbname=lib_test
de.ruu.lib.jpa.se.hibernate.postgres.AbstractEntityManagerProducer.dbuser=lib_test
de.ruu.lib.jpa.se.hibernate.postgres.AbstractEntityManagerProducer.dbpass=lib_test
de.ruu.lib.jpa.se.hibernate.postgres.AbstractEntityManagerProducer.puname=lib_test
```

---

## 🚀 Container-Befehle

### Status prüfen
```bash
cd /home/r-uu/develop/github/main/config/shared/docker
docker-compose ps
```

### Container starten
```bash
docker-compose up -d
```

### Nur PostgreSQL + Keycloak (ohne JasperReports)
```bash
docker-compose up -d postgres-jeeeraaah postgres-keycloak keycloak
```

### Container stoppen
```bash
docker-compose down
```

### Container stoppen und Daten löschen
```bash
docker-compose down -v
```

### Logs ansehen
```bash
docker-compose logs -f postgres-jeeeraaah
docker-compose logs -f keycloak
```

---

## 🧪 Datenbank-Verbindung testen

### Test lib_test Verbindung
```bash
docker exec postgres-jeeeraaah psql -U lib_test -d lib_test -c "SELECT current_database(), current_user;"
```

### Test jeeeraaah Verbindung
```bash
docker exec postgres-jeeeraaah psql -U r_uu -d jeeeraaah -c "SELECT current_database(), current_user;"
```

---

## 📂 Init-Skripte

### Automatische Initialisierung
Das Skript `initdb/jeeeraaah/01-create-lib-test.sh` erstellt automatisch beim Container-Start:
- Benutzer `lib_test` mit Passwort `lib_test`
- Datenbank `lib_test`
- Notwendige Berechtigungen

**Hinweis:** Init-Skripte werden nur beim **ersten Start** ausgeführt (wenn das Volume leer ist).

### Erneute Initialisierung erzwingen
```bash
cd /home/r-uu/develop/github/main/config/shared/docker
docker-compose down -v
docker volume rm postgres-jeeeraaah-data
docker-compose up -d postgres-jeeeraaah
```

---

## ✅ Problem gelöst

Der ursprüngliche Fehler:
```
FATAL: password authentication failed for user "r_uu"
```

wurde behoben durch:
1. ✅ Alte Container gestoppt und entfernt
2. ✅ Container gemäß docker-compose.yml neu gestartet
3. ✅ `lib_test` Datenbank und Benutzer erstellt
4. ✅ `config.properties` mit korrekten Credentials aktualisiert
5. ✅ Init-Skript für automatische Setup erstellt

---

## 🎯 Nächste Schritte

Die Integration Tests sollten jetzt funktionieren:

```bash
cd /home/r-uu/develop/github/main/root
mvn test -pl lib/jpa/se.hibernate.postgres.demo
```

---

✅ **Setup abgeschlossen!**
