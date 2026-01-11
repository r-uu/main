# ✅ PROBLEM GEFUNDEN - PostgreSQL läuft in Docker!

## 🎯 Die Datenbank läuft bereits in Docker, NICHT als Service!

**Du hast:** Docker Container `postgres-jeeeraaah` läuft bereits
**Problem:** Die Datenbank `lib_test` existiert im Docker Container nicht

---

## ⚡ LÖSUNG (Docker statt Service)

### 1️⃣ Datenbank im Docker Container erstellen

```bash
# User erstellen (im Docker Container!)
docker exec -it postgres-jeeeraaah psql -U postgres -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || \
docker exec -it postgres-jeeeraaah psql -U postgres -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';"

# Datenbank erstellen (im Docker Container!)
docker exec -it postgres-jeeeraaah psql -U postgres -c "CREATE DATABASE lib_test OWNER r_uu;" 2>/dev/null || echo "DB existiert bereits"

# Rechte vergeben
docker exec -it postgres-jeeeraaah psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;"
docker exec -it postgres-jeeeraaah psql -U postgres -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;"
```

### 2️⃣ Testen

```bash
# Verbindung testen
docker exec -it postgres-jeeeraaah psql -U r_uu -d lib_test -c "SELECT version();"
```

### 3️⃣ Build ausführen

```bash
cd /home/r-uu/develop/github/main && source config/shared/wsl/aliases.sh && build-all
```

---

## 🚀 ALLES IN EINEM (Copy & Paste)

```bash
docker exec -it postgres-jeeeraaah psql -U postgres -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || docker exec -it postgres-jeeeraaah psql -U postgres -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';" && docker exec -it postgres-jeeeraaah psql -U postgres -c "CREATE DATABASE lib_test OWNER r_uu;" 2>/dev/null || true && docker exec -it postgres-jeeeraaah psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;" && docker exec -it postgres-jeeeraaah psql -U postgres -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;" && cd /home/r-uu/develop/github/main && source config/shared/wsl/aliases.sh && build-all
```

---

## ✅ Erwartetes Ergebnis

Nach Datenbank-Erstellung:
```
CREATE ROLE (oder ALTER ROLE)
CREATE DATABASE
GRANT
GRANT
```

Nach Build:
```
[INFO] Running de.ruu.lib.jpa.se.hibernate.EntityManagerFactoryProducerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 📝 Docker Container Info

Deine PostgreSQL läuft in Docker:
- **Container:** `postgres-jeeeraaah`
- **Image:** `postgres:16-alpine`
- **Port:** 5432 (gemappt auf localhost:5432)
- **Status:** Healthy ✅

**Das ist GUT!** Docker ist besser als lokaler Service!

---

## 🔍 Nützliche Docker Befehle

```bash
# PostgreSQL Shell öffnen
docker exec -it postgres-jeeeraaah psql -U postgres

# Datenbanken anzeigen
docker exec -it postgres-jeeeraaah psql -U postgres -c "\l"

# Als r_uu einloggen
docker exec -it postgres-jeeeraaah psql -U r_uu -d lib_test

# Container Status
docker ps | grep postgres
```

---

**FÜHRE JETZT DEN ONE-LINER AUS (oben)!** 🚀

