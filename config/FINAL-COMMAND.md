# 🚀 FINALE BEFEHLE - Einfach Copy & Paste

**Problem:** `database "lib_test" does not exist`

**⚠️ WICHTIG:** Führe **ZUERST** PostgreSQL Setup aus, **DANN** build-all!

---

## 🎯 SUPER-EINFACH (Copy & Paste diese 3 Zeilen nacheinander!)

```bash
# 1. PostgreSQL Setup (ZUERST!)
sudo service postgresql start && sudo -u postgres psql -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || sudo -u postgres psql -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';" && sudo -u postgres psql -c "CREATE DATABASE lib_test OWNER r_uu;" 2>/dev/null || true && sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;" && sudo -u postgres psql -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;"

# 2. Warte bis Schritt 1 fertig ist, dann:
cd /home/r-uu/develop/github/main && source config/shared/wsl/aliases.sh

# 3. Jetzt Build:
build-all
```

---

## ⚡ JETZT AUSFÜHREN (Copy & Paste - BEIDE Teile!)

**WICHTIG:** Du musst **BEIDE** Befehle ausführen - erst PostgreSQL Setup, dann Build!

```bash
# === TEIL 1: PostgreSQL starten und Datenbank erstellen ===
sudo service postgresql start && \
sudo -u postgres psql -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || \
sudo -u postgres psql -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';" && \
sudo -u postgres psql -c "CREATE DATABASE lib_test OWNER r_uu;" 2>/dev/null || true && \
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;" && \
sudo -u postgres psql -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;"

# === TEIL 2: Build ausführen ===
cd /home/r-uu/develop/github/main && \
source config/shared/wsl/aliases.sh && \
build-all
```

**ODER als 1 langer Befehl (am Ende dieses Dokuments):**

**Das war's!** ✅

---

## 📝 Schritt für Schritt (falls One-Liner nicht funktioniert)

```bash
# 1. PostgreSQL starten
sudo service postgresql start

# 2. User erstellen (ignoriert Fehler falls existiert)
sudo -u postgres psql -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" || \
sudo -u postgres psql -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';"

# 3. Datenbank erstellen (ignoriert Fehler falls existiert)
sudo -u postgres psql -c "CREATE DATABASE lib_test OWNER r_uu;" || echo "DB existiert bereits"

# 4. Rechte vergeben
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;"
sudo -u postgres psql -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;"

# 5. Testen
PGPASSWORD=r_uu_password psql -h localhost -U r_uu -d lib_test -c "\l"

# 6. Build
cd /home/r-uu/develop/github/main
source config/shared/wsl/aliases.sh
build-all
```

---

## ✅ Erwartetes Ergebnis

Nach PostgreSQL Setup:
```
PostgreSQL Service läuft
User r_uu erstellt/aktualisiert
Datenbank lib_test erstellt
Rechte vergeben
```

Nach Build:
```
[INFO] Running de.ruu.lib.jpa.se.hibernate.EntityManagerFactoryProducerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🔍 Diagnose (falls immer noch Fehler)

```bash
# Prüfe PostgreSQL Status
sudo service postgresql status

# Prüfe ob Datenbank existiert
sudo -u postgres psql -c "\l" | grep lib_test

# Prüfe ob User existiert
sudo -u postgres psql -c "\du" | grep r_uu

# Teste Verbindung
PGPASSWORD=r_uu_password psql -h localhost -U r_uu -d lib_test -c "SELECT version();"
```

Falls einer der Befehle fehlschlägt, zeige mir die Ausgabe!

---

**COPY & PASTE DIESEN BEFEHL:**

```bash
sudo service postgresql start && sudo -u postgres psql -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || sudo -u postgres psql -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';" && sudo -u postgres psql -c "CREATE DATABASE lib_test OWNER r_uu;" 2>/dev/null || true && sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;" && sudo -u postgres psql -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;" && cd /home/r-uu/develop/github/main && source config/shared/wsl/aliases.sh && build-all
```

🚀 Fertig!

