# ✅ QUICK FIX - Datenbank lib_test existiert nicht

**Problem:** `FATAL: database "lib_test" does not exist`

---

## 🚀 Schnelle Lösung

### Option 1: Mit Skript (empfohlen)

```bash
cd /home/r-uu/develop/github/main

# Aliase laden
source config/shared/wsl/aliases.sh

# PostgreSQL Setup nochmal ausführen (erstellt DB wenn nicht vorhanden)
ruu-postgres-setup

# Build ausführen
build-all
```

### Option 2: One-Liner (schnell!)

```bash
# Alles in einem Befehl
sudo service postgresql start && \
sudo -u postgres psql -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || \
sudo -u postgres psql -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';" && \
sudo -u postgres psql -c "CREATE DATABASE lib_test OWNER r_uu;" 2>/dev/null || true && \
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;" && \
sudo -u postgres psql -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;" && \
echo "✅ PostgreSQL Setup abgeschlossen!"

# Dann Build
cd /home/r-uu/develop/github/main && source config/shared/wsl/aliases.sh && build-all
```

**Das war's!** Der `build-all` Alias wechselt jetzt automatisch ins richtige Verzeichnis!

---

## 🔧 Was wurde korrigiert

### 1. PostgreSQL Setup verbessert
- ✅ Prüft ob Datenbank `lib_test` existiert
- ✅ Erstellt sie falls nicht vorhanden
- ✅ Setzt Rechte auch wenn DB schon existiert

### 2. build-all Alias verbessert
- ✅ Ist jetzt eine Funktion statt Alias
- ✅ Wechselt automatisch nach `$RUU_HOME`
- ✅ Kehrt zum ursprünglichen Verzeichnis zurück
- ✅ Funktioniert **egal von wo** du es aufrufst!

**Vorher:**
```bash
cd /home/r-uu/develop/github/main  # Musste manuell wechseln!
build-all
```

**Jetzt:**
```bash
build-all  # Funktioniert von überall!
```

---

## 📝 Manuelle Alternative

Falls das Skript nicht funktioniert:

```bash
# PostgreSQL starten
sudo service postgresql start

# Datenbank manuell erstellen
sudo -u postgres psql << 'EOF'
-- User erstellen/aktualisieren
CREATE USER r_uu WITH PASSWORD 'r_uu_password';

-- Datenbank erstellen (falls noch nicht vorhanden)
CREATE DATABASE lib_test OWNER r_uu;

-- Rechte vergeben
GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;
\q
EOF

# Schema-Rechte
sudo -u postgres psql -d lib_test << 'EOF'
GRANT ALL ON SCHEMA public TO r_uu;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO r_uu;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO r_uu;
\q
EOF

# Verbindung testen
PGPASSWORD=r_uu_password psql -h localhost -U r_uu -d lib_test -c "SELECT version();"
```

---

## ✅ Erwartetes Ergebnis

Nach `ruu-postgres-setup`:
```
========================================
PostgreSQL Setup
========================================

✅ PostgreSQL Service gestartet
User r_uu existiert bereits
Datenbank lib_test erstellt
✅ Rechte vergeben
✅ Verbindungstest erfolgreich
```

Nach `build-all`:
```
[INFO] Running de.ruu.lib.jpa.se.hibernate.postgres.demo.TestAbstractRepository
[INFO] Running de.ruu.lib.jpa.se.hibernate.postgres.demo.TestSimpleTypeService
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🎯 Zusammenfassung der Verbesserungen

| Aspekt | Vorher | Jetzt |
|--------|--------|-------|
| **build-all** | Muss von $RUU_HOME ausgeführt werden | Funktioniert von überall ✅ |
| **Datenbank** | Manuell erstellen | Automatisch mit Setup-Skript ✅ |
| **Fehler** | `database does not exist` | Funktioniert ✅ |

---

**JETZT AUSFÜHREN:**

```bash
source ~/develop/github/main/config/shared/wsl/aliases.sh
ruu-postgres-setup
build-all
```

Die Datenbank wird erstellt und der Build sollte durchlaufen! 🚀

