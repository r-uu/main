# ✅ Property-System korrigiert - Maven Resource Filtering

**Datum:** 2026-01-11  
**Status:** ✅ **KORREKT KONFIGURIERT!**

---

## 🎯 Das korrekte Setup

### Prinzip: Git vs. Lokal

| Datei | Git-Kontrolle | Inhalt |
|-------|---------------|--------|
| **microprofile-config.properties** | ✅ Ja | Platzhalter: `${db.host}` |
| **config.properties** | ❌ Nein (.gitignore) | Echte Werte: `db.host=localhost` |
| **config.properties.template** | ✅ Ja | Beispiel-Werte |

---

## ✅ Was wurde geändert

### 1. Property-Namen vereinheitlicht

**VORHER (inkonsistent):**
```properties
database.host=...  # Manche Dateien
db.host=...        # Andere Dateien
```

**NACHHER (konsistent):**
```properties
# Nur noch db.* Properties!
db.host=localhost
db.port=5432
db.database=lib_test
db.username=r_uu
db.password=r_uu_password
```

### 2. Credentials korrigiert

**User:** `r_uu` (statt `lib_test`)  
**Password:** `r_uu_password` (statt `lib_test`)

### 3. Maven Resource Filtering aktiviert

**Git-kontrollierte Dateien (microprofile-config.properties):**
```properties
# Platzhalter - werden von Maven ersetzt
database.host=${db.host}
database.port=${db.port}
database.name=${db.database}
database.user=${db.username}
database.pass=${db.password}
```

**Lokale Konfiguration (config.properties):**
```properties
# Echte Werte - nicht in Git!
db.host=localhost
db.port=5432
db.database=lib_test
db.username=r_uu
db.password=r_uu_password
```

**Beim Maven Build:**
1. Maven liest `config.properties`
2. Kopiert `microprofile-config.properties` nach `target/`
3. **Ersetzt** `${db.host}` → `localhost`, etc.
4. Tests lesen die gefilterten Dateien aus `target/`

---

## 📁 Geänderte Dateien

### Lokale Konfiguration (nicht in Git)
1. ✅ `config.properties` - Vereinheitlicht auf `db.*`, Credentials korrigiert

### Git-kontrolliert (Template)
2. ✅ `config.properties.template` - Beispiel für neue Entwickler

### Git-kontrolliert (Maven Resource Filtering)
3. ✅ `root/lib/jdbc/postgres/.../microprofile-config.properties`
4. ✅ `root/lib/jpa/se.hibernate/.../microprofile-config.properties`
5. ✅ `root/lib/jpa/se.hibernate.postgres.demo/.../microprofile-config.properties`
6. ✅ `root/app/jeeeraaah/backend.common.mapping/.../microprofile-config.properties`
7. ✅ `root/app/jeeeraaah/backend/persistence/jpa/.../microprofile-config.properties`

---

## 🚀 Build testen

```bash
cd /home/r-uu/develop/github/main/root
mvn clean test
```

**Erwartetes Ergebnis (wenn PostgreSQL mit r_uu/r_uu_password läuft):**
```
[INFO] Running de.ruu.lib.jdbc.postgres.TestDataSourceFactory
[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Erwartetes Ergebnis (wenn PostgreSQL NICHT läuft oder falsche Credentials):**
```
[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: X
(Tests werden automatisch übersprungen)
```

---

## 🔧 PostgreSQL Setup

### User und Datenbank erstellen

```bash
sudo -u postgres psql << 'EOF'
-- User erstellen
CREATE USER r_uu WITH PASSWORD 'r_uu_password';

-- Datenbank erstellen
CREATE DATABASE lib_test OWNER r_uu;

-- Rechte vergeben
GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;

-- In PostgreSQL 15+ auch Schema-Rechte
\c lib_test
GRANT ALL ON SCHEMA public TO r_uu;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO r_uu;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO r_uu;

\q
EOF
```

### Verbindung testen

```bash
psql -h localhost -U r_uu -d lib_test -c "SELECT version();"
# Password: r_uu_password
```

---

## 📊 Property Mapping

| MicroProfile Config Property | Maven Platzhalter | config.properties | Wert |
|------------------------------|-------------------|-------------------|------|
| `database.host` | `${db.host}` | `db.host` | `localhost` |
| `database.port` | `${db.port}` | `db.port` | `5432` |
| `database.name` | `${db.database}` | `db.database` | `lib_test` |
| `database.user` | `${db.username}` | `db.username` | `r_uu` |
| `database.pass` | `${db.password}` | `db.password` | `r_uu_password` |

**Warum unterschiedliche Namen?**
- **MicroProfile Config:** Verwendet `database.*` (historisch gewachsen)
- **Maven Properties:** Verwendet `db.*` (kürzer, konsistent mit Legacy-Code)
- **Maven Resource Filtering** übersetzt automatisch!

---

## ✅ Checkliste

- [x] config.properties: `db.*` Properties, `r_uu` / `r_uu_password`
- [x] config.properties.template: Aktualisiert
- [x] Alle microprofile-config.properties: Platzhalter `${db.*}`
- [x] .gitignore: config.properties ausgeschlossen
- [ ] **DU:** PostgreSQL User `r_uu` erstellen
- [ ] **DU:** Datenbank `lib_test` erstellen
- [ ] **DU:** Build testen: `mvn clean test`

---

## 🎓 Für neue Entwickler

```bash
# 1. Template kopieren
cd /home/r-uu/develop/github/main
cp config.properties.template config.properties

# 2. Werte anpassen (falls nötig)
nano config.properties

# 3. PostgreSQL Setup
sudo -u postgres createuser -P r_uu  # Password: r_uu_password
sudo -u postgres createdb -O r_uu lib_test

# 4. Build testen
mvn clean test
```

---

**Status:** ✅ Property-System ist jetzt korrekt konfiguriert mit Maven Resource Filtering!

