# Docker Credentials - Einheitliche Dev-Umgebung

**Letzte Aktualisierung:** 2026-01-24

---

## 🎯 **DAS WICHTIGSTE:**

### Einheitliche Credentials überall:
- **Username:** `admin`
- **Password:** `admin`

**Warum?** Vereinfacht Security-Debugging massiv - keine Verwirrung mehr über verschiedene User/Passwörter!

---

## 📋 Übersicht

| System | Username | Password | Port | Database |
|--------|----------|----------|------|----------|
| **postgres-jeeeraaah** | `admin` | `admin` | 5432 | `jeeeraaah` |
| **postgres-keycloak** | `admin` | `admin` | 5433 | `keycloak` |
| **keycloak (Admin)** | `admin` | `admin` | 8080 | - |
| **Application (Test)** | `admin` | `admin` | - | - |

---

## 🔧 Verwendung

### PostgreSQL JEEERAaH
```bash
# Shell-Zugriff
docker exec -it postgres-jeeeraaah psql -U admin -d jeeeraaah

# JDBC URL
jdbc:postgresql://localhost:5432/jeeeraaah?user=admin&password=admin
```

### PostgreSQL Keycloak
```bash
# Shell-Zugriff  
docker exec -it postgres-keycloak psql -U admin -d keycloak

# JDBC URL
jdbc:postgresql://localhost:5433/keycloak?user=admin&password=admin
```

### Keycloak Admin Console
```bash
# URL öffnen
open http://localhost:8080/admin

# Login
Username: admin
Password: admin
```

---

## 📁 Konfiguration

### `.env` (lokal, NICHT in Git!)

```bash
# postgres-jeeeraaah
postgres_jeeeraaah_db=jeeeraaah
postgres_jeeeraaah_user=admin
postgres_jeeeraaah_password=admin

# postgres-keycloak
postgres_keycloak_db=keycloak
postgres_keycloak_user=admin
postgres_keycloak_password=admin

# keycloak admin
kc_bootstrap_admin_username=admin
kc_bootstrap_admin_password=admin

# application test
test_username=admin
test_password=admin
```

### `testing.properties`

```properties
test.username=admin
test.password=admin
```

---

## 🔒 Sicherheit

### ✅ OK für Dev:
- Einheitliche `admin/admin` Credentials
- Einfaches Debugging
- Keine Produktionsdaten

### ❌ NIEMALS in Produktion:
- Schwache Passwörter
- `.env` in Git
- Hardcoded Credentials

---

## 🚀 Neustart

```bash
# Alles löschen und neu starten
cd ~/develop/github/main/config/shared/docker
docker compose down -v
docker compose up -d

# Keycloak Realm Setup
cd ~/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

---

**Merke:** `admin` / `admin` - überall! 🎯
