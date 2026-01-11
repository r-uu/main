# 🚀 FINALE ANLEITUNG - Jetzt ausführen!

**Datum:** 2026-01-11  
**Status:** ✅ Bereit zum Ausführen

---

## ⚡ Quick Start (2 Befehle)

### Variante 1: Mit Aliasen (empfohlen)

```bash
# Aliase aktivieren
source ~/develop/github/main/config/shared/wsl/aliases.sh

# 1. PostgreSQL Setup (einmalig)
ruu-postgres-setup

# 2. Build ausführen
build-all
```

### Variante 2: Direkt (ohne Aliase)

```bash
cd /home/r-uu/develop/github/main

# 1. PostgreSQL Setup (einmalig)
chmod +x config/shared/scripts/setup-postgresql.sh
sudo config/shared/scripts/setup-postgresql.sh

# 2. Build ausführen
chmod +x config/shared/scripts/build-all.sh
./config/shared/scripts/build-all.sh
```

**Das war's!** 🎉

---

## 📝 Was passiert

### Schritt 1: PostgreSQL Setup
Das Skript `setup-postgresql.sh`:
1. ✅ Installiert PostgreSQL (falls nicht vorhanden)
2. ✅ Startet den Service
3. ✅ Erstellt User `r_uu` mit Password `r_uu_password`
4. ✅ Erstellt Datenbank `lib_test`
5. ✅ Vergibt alle Rechte
6. ✅ Testet die Verbindung

### Schritt 2: Build
Das Skript `build-all.sh`:
1. ✅ Prüft PostgreSQL Status (startet automatisch falls nötig)
2. ✅ Prüft `config.properties` (erstellt aus Template falls nötig)
3. ✅ Baut BOM
4. ✅ Baut Root (alle Module)
5. ✅ Prüft Maven Resource Filtering
6. ✅ Zeigt Test-Ergebnisse

---

## ✅ Erwartetes Ergebnis

### PostgreSQL Setup
```
========================================
PostgreSQL Setup
========================================

✅ PostgreSQL installiert
✅ PostgreSQL Service gestartet
✅ User r_uu erstellt
✅ Datenbank lib_test erstellt
✅ Verbindungstest erfolgreich

========================================
✅ PostgreSQL Setup erfolgreich!
========================================

Credentials:
  Host:     localhost
  Port:     5432
  Database: lib_test
  User:     r_uu
  Password: r_uu_password
```

### Build
```
========================================
Maven Build - Korrekte Reihenfolge
========================================

✅ PostgreSQL läuft bereits

1. BOM bauen...
[INFO] BUILD SUCCESS

2. Root bauen...
[INFO] Running de.ruu.lib.jdbc.postgres.TestDataSourceFactory
[INFO] Running de.ruu.lib.jpa.se.hibernate.postgres.demo.TestAbstractRepository
[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS

========================================
✅ BUILD ERFOLGREICH!
========================================

✅ Properties wurden korrekt ersetzt:
database.host=localhost
database.port=5432
```

---

## ⚠️ Falls Probleme auftreten

### Problem: "psql: error: connection to server"

**Lösung:** PostgreSQL läuft nicht
```bash
sudo service postgresql start
sudo service postgresql status
```

### Problem: "FATAL: Peer authentication failed"

**Lösung:** pg_hba.conf muss angepasst werden
```bash
sudo nano /etc/postgresql/14/main/pg_hba.conf
# Ändere "peer" zu "md5" für local connections
sudo service postgresql reload
```

### Problem: Maven Resource Filtering funktioniert nicht

**Symptom:** `${db.host}` wurde nicht ersetzt

**Lösung:** Build vom Root-Verzeichnis starten
```bash
cd /home/r-uu/develop/github/main
./config/shared/scripts/build-all.sh
```

---

## 📚 Dokumentation

Vollständige Anleitungen:
- **[POSTGRESQL-SETUP.md](POSTGRESQL-SETUP.md)** - PostgreSQL Details
- **[FINAL-PROPERTY-SYSTEM-FIX.md](FINAL-PROPERTY-SYSTEM-FIX.md)** - Property System
- **[BUILD-DOCS-INDEX.md](BUILD-DOCS-INDEX.md)** - Alle Dokumente

---

## 🎯 Nächste Schritte nach erfolgreichem Build

### 1. IntelliJ Maven Reload
```
Rechtsklick auf root/pom.xml → Maven → Reload Project
```

### 2. IntelliJ Build konfigurieren
```
File → Settings → Build, Execution, Deployment → Build Tools → Maven → Runner
☑️ Delegate IDE build/run actions to Maven
```

### 3. Validieren
- ✅ Build erfolgreich in IntelliJ
- ✅ Tests laufen durch (grün)
- ✅ Keine Compile-Fehler

---

## ✅ Checkliste

- [ ] PostgreSQL Setup ausgeführt: `sudo ./config/shared/scripts/setup-postgresql.sh`
- [ ] Verbindungstest erfolgreich
- [ ] Build ausgeführt: `./config/shared/scripts/build-all.sh`
- [ ] BUILD SUCCESS erhalten
- [ ] Tests laufen durch (keine `port out of range:-1` Fehler)
- [ ] IntelliJ Maven Reload
- [ ] IntelliJ Build funktioniert

---

**JETZT AUSFÜHREN:**

```bash
# Option 1: Mit Aliasen (kürzer)
source ~/develop/github/main/config/shared/wsl/aliases.sh
ruu-postgres-setup
build-all

# Option 2: Ohne Aliase (vollständige Pfade)
cd /home/r-uu/develop/github/main
sudo config/shared/scripts/setup-postgresql.sh
./config/shared/scripts/build-all.sh
```

🚀 Viel Erfolg!

