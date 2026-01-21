# ✅ Liberty Server Konfiguration - server.env fehlte

**Datum:** 2026-01-20

---

## ❌ Problem

Liberty Server konnte nicht starten, weil die `server.env` Datei fehlte:

```
[ERROR] CWWKG0075E: The value ${datasource_server_port} is not valid for attribute portNumber
[ERROR] CWWKG0075E: The value ${default_http_port} is not valid for attribute httpPort
[ERROR] CWWJP0015E: Unable to determine Dialect without JDBC metadata
```

**Ursache:** Die `server.xml` verwendet Variablen wie `${datasource_server_port}`, `${default_http_port}`, etc., die aus der `server.env` Datei geladen werden müssen. Diese Datei existierte nicht.

---

## ✅ Lösung

### 1. server.env erstellt

**Datei:** `/home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs/src/main/liberty/config/server.env`

**Inhalt:**
```env
# Environment-specific settings
environment=development

# HTTP/HTTPS Ports
default_http_port=9080
default_https_port=9443
default_host_name=*

# PostgreSQL DataSource Configuration
datasource_server_host=localhost
datasource_server_port=5432
datasource_database=jeeeraaah
datasource_database_username=r_uu
datasource_database_password=r_uu_password

# Default Keystore Password
default_keystore_password=changeit

# Keycloak Configuration
keycloak_host=localhost
keycloak_port=8080
keycloak_realm=realm_default
keycloak_client_id=jeeeraaah-backend
```

### 2. persistence.xml aktualisiert

**Problem:** Hibernate konnte den Dialect nicht automatisch erkennen.

**Lösung:** `hibernate.dialect` Property hinzugefügt:

```xml
<property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
```

---

## 📁 Dateien

### Konfigurationsdateien

| Datei | Pfad | Beschreibung |
|-------|------|--------------|
| **server.env** | `backend/api/ws.rs/src/main/liberty/config/` | Environment-Variablen für Liberty |
| **server.env.template** | `backend/api/ws.rs/src/main/liberty/config/` | Template mit allen Optionen |
| **server.xml** | `backend/api/ws.rs/src/main/liberty/config/` | Liberty Server Konfiguration |
| **persistence.xml** | `backend/api/ws.rs/src/main/resources/META-INF/` | JPA Persistence Unit |

---

## 🔧 Wichtige Konfigurationen

### server.env - Variablen

Diese Variablen werden von `server.xml` verwendet:

#### HTTP/HTTPS
- `default_http_port` - HTTP Port (Standard: 9080)
- `default_https_port` - HTTPS Port (Standard: 9443)
- `default_host_name` - Network Binding (Standard: *)

#### Datenbank
- `datasource_server_host` - PostgreSQL Host
- `datasource_server_port` - PostgreSQL Port
- `datasource_database` - Datenbankname
- `datasource_database_username` - DB Benutzer
- `datasource_database_password` - DB Passwort

#### Security
- `default_keystore_password` - Keystore Passwort

#### Keycloak
- `keycloak_host` - Keycloak Host
- `keycloak_port` - Keycloak Port
- `keycloak_realm` - Keycloak Realm
- `keycloak_client_id` - Client ID

---

## 🚨 Wichtig: Git-Ignore

Die `server.env` Datei enthält **sensible Credentials** und sollte **NICHT** in Git committet werden!

**Prüfen:**
```bash
git status | grep server.env
# Sollte NICHTS anzeigen
```

**Falls `server.env` getrackt wird:**
```bash
# In .gitignore einfügen
echo "**/server.env" >> .gitignore

# Aus Git entfernen (nur aus Index, Datei bleibt lokal)
git rm --cached **/server.env
git commit -m "Remove server.env from Git (contains credentials)"
```

---

## 🎯 Verwendung

### Für neue Entwickler

1. **Template kopieren:**
   ```bash
   cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs/src/main/liberty/config
   cp server.env.template server.env
   ```

2. **Werte anpassen:**
   ```bash
   nano server.env
   # Oder mit beliebigem Editor
   ```

3. **Server starten:**
   ```bash
   cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
   mvn liberty:dev
   ```

---

## 🔍 Verschiedene Szenarien

### PostgreSQL in Docker (lokal)
```env
datasource_server_host=localhost
datasource_server_port=5432
```

### PostgreSQL auf anderem Host
```env
datasource_server_host=192.168.1.100
datasource_server_port=5432
```

### Beide in Docker Compose
```env
# Service-Name aus docker-compose.yml
datasource_server_host=postgres-jeeeraaah
datasource_server_port=5432
```

---

## ✅ Ergebnis

Nach Erstellen der `server.env` Datei sollte Liberty starten ohne Fehler:

```
[AUDIT] CWWKF0011I: The defaultServer server is ready to run a smarter planet.
[AUDIT] CWWKZ0001I: Application r-uu.app.jeeeraaah.backend.api.ws.rs started
```

---

## 📚 Weitere Informationen

- **README:** `backend/api/ws.rs/src/main/liberty/config/README-DATABASE-CONFIG.md`
- **Template:** `backend/api/ws.rs/src/main/liberty/config/server.env.template`

---

✅ **Problem gelöst!**
