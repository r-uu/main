# ✅ PROBLEM GELÖST - PostgreSQL läuft in Docker!

## 🎯 Du hast PostgreSQL in Docker, nicht als Service!

**Fehler:** `database "lib_test" does not exist`

**Ursache:** Die Datenbank fehlt im Docker Container `postgres-jeeeraaah`

---

## ⚡ LÖSUNG (1 One-Liner für Docker!)

Copy & Paste:

```bash
docker exec -it postgres-jeeeraaah psql -U postgres -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || docker exec -it postgres-jeeeraaah psql -U postgres -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';" && docker exec -it postgres-jeeeraaah psql -U postgres -c "CREATE DATABASE lib_test OWNER r_uu;" 2>/dev/null || true && docker exec -it postgres-jeeeraaah psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;" && docker exec -it postgres-jeeeraaah psql -U postgres -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;" && cd /home/r-uu/develop/github/main && source config/shared/wsl/aliases.sh && build-all
```

**Das war's!** ✅

---

## 📝 ODER Schritt für Schritt

### 1️⃣ Datenbank im Docker Container erstellen
```bash
# User erstellen
docker exec -it postgres-jeeeraaah psql -U postgres -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || \
docker exec -it postgres-jeeeraaah psql -U postgres -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';"

# Datenbank erstellen
docker exec -it postgres-jeeeraaah psql -U postgres -c "CREATE DATABASE lib_test OWNER r_uu;"

# Rechte vergeben
docker exec -it postgres-jeeeraaah psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;"
docker exec -it postgres-jeeeraaah psql -U postgres -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;"
```

### 2️⃣ Build ausführen
```bash
cd /home/r-uu/develop/github/main && source config/shared/wsl/aliases.sh && build-all
```

---

## ✅ Erwartetes Ergebnis

Nach Schritt 1:
```
CREATE ROLE
CREATE DATABASE
GRANT
```

Nach Schritt 3:
```
[INFO] Running de.ruu.lib.jpa.se.hibernate.EntityManagerFactoryProducerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**KEINE Fehler mehr:** `database "lib_test" does not exist` ✅

---

**FÜHRE JETZT AUS:**
1. PostgreSQL Setup Befehl (oben)
2. Warte bis fertig
3. cd & source Befehl
4. build-all

🚀 Fertig!

