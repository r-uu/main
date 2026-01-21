# JEEERAAAH - SETUP UND START ANLEITUNG

**Projekt:** JEEERAAAH Task Management  
**Datum:** 2026-01-19  
**Java Version:** GraalVM 25  
**Build System:** Maven (JPMS-konform)

---

## 🚀 SCHNELLSTART

### 1. Docker Container starten

```bash
cd /home/r-uu/develop/github/main/config/shared/docker
docker compose up -d
```

**Warte bis alle Container healthy sind:**
```bash
docker ps
```

Erwartete Container:
- `keycloak-service` (Port 8080)
- `postgres-keycloak` (Port 5433)
- `postgres-jeeeraaah` (Port 5432)
- `jasperreports-service` (Port 8090)

### 2. Keycloak Realm initialisieren

```bash
cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

**Was wird erstellt:**
- Realm: `jeeeraaah-realm`
- Client: `jeeeraaah-frontend` (Direct Access Grants aktiviert)
- User: `r_uu` / `r_uu_password`

### 3. Backend starten

```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

**Warte auf:**
```
[INFO] CWWKF0011I: The defaultServer server is ready to run a smarter planet.
```

**Backend läuft auf:** http://localhost:9080/jeeeraaah/

### 4. Frontend (DashAppRunner) starten

**In IntelliJ:**
1. Run Configuration: `DashAppRunner`
2. **Run** klicken

**Oder Maven:**
```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/frontend/ui/fx
mvn exec:java -Dexec.mainClass="de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner"
```

---

## 🏗️ PROJEKT-STRUKTUR

```
main/
├── bom/                          # Bill of Materials (zentrale Dependency-Verwaltung)
├── root/
│   ├── app/
│   │   └── jeeeraaah/
│   │       ├── backend/          # OpenLiberty REST API (Port 9080)
│   │       │   ├── api/ws.rs/    # REST Endpoints
│   │       │   └── persistence/  # JPA Entities
│   │       └── frontend/         # JavaFX UI
│   │           ├── api.client/   # REST Client
│   │           └── ui/fx/        # DashAppRunner (JavaFX)
│   ├── lib/                      # Wiederverwendbare Libraries
│   │   ├── keycloak.admin/       # Keycloak Setup & Management
│   │   ├── jpa/                  # JPA Core
│   │   └── office/word/          # Dokument-Generierung
│   │       ├── docx4j/           # DOCX4J Implementation
│   │       └── jasperreports/    # JasperReports Service
│   └── sandbox/                  # Experimente & Prototypen
└── config/
    └── shared/
        ├── docker/               # Docker Compose Konfiguration
        ├── scripts/              # Build & Setup Skripte
        └── wsl/                  # WSL-spezifische Konfiguration
```

---

## 🔧 TECHNOLOGIE-STACK

### Backend
- **Application Server:** OpenLiberty
- **REST API:** JAX-RS (Jakarta EE)
- **Persistence:** JPA 3.2 (EclipseLink)
- **Database:** PostgreSQL 16
- **Authentication:** Keycloak (OAuth2/OpenID Connect)
- **API Documentation:** OpenAPI 3.x

### Frontend
- **UI Framework:** JavaFX 24
- **Dependency Injection:** CDI (Weld SE)
- **REST Client:** Jersey Client
- **Authentication:** Keycloak Direct Access Grants

### Build & Runtime
- **JDK:** GraalVM 25 (Oracle)
- **Build:** Maven 3.9.x
- **Module System:** JPMS (Java Platform Module System)
- **Logging:** Log4j2

---

## 📋 WICHTIGE BEFEHLE

### Build

```bash
# Gesamtprojekt bauen
cd /home/r-uu/develop/github/main/root
mvn clean install

# Nur Backend bauen
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn clean package
```

### Docker

```bash
# Container starten
docker compose up -d

# Container stoppen
docker compose down

# Container + Volumes löschen (Reset)
docker compose down -v

# Container Status
docker ps
```

### Keycloak

```bash
# Setup ausführen
cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"

# Admin Console
# URL: http://localhost:8080/admin
# Login: admin / changeme_in_local_env
```

### Backend (OpenLiberty)

```bash
# Dev-Modus (Hot Reload)
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev

# Nur starten
mvn liberty:run

# Stoppen
mvn liberty:stop
```

---

## 🐛 TROUBLESHOOTING

### Problem: Backend kompiliert nicht (Byte Buddy Fehler)

**Fehler:**
```
Java 25 is not supported by the current version of Byte Buddy
```

**Lösung:** ✅ **BEREITS BEHOBEN!** 

Byte Buddy wurde auf Version **1.18.4** aktualisiert in `bom/pom.xml`.

**Hinweis:** 1.18.4 ist die neueste stabile Version (Stand: Januar 2026) und unterstützt Java 25 vollständig.

### Problem: Keycloak Login fehlschlägt

**Fehler:**
```
invalid_client
```

**Lösung:**
1. Keycloak Setup erneut ausführen (siehe oben)
2. Prüfe dass Direct Access Grants aktiviert ist:
   - Admin Console → realm_default → Clients → jeeeraaah-frontend
   - "Direct Access Grants Enabled" = ON

### Problem: Frontend kann Backend nicht erreichen

**Fehler:**
```
Connection refused (Port 9080)
```

**Lösung:** Backend starten (siehe Schritt 3 oben)

### Problem: Datenbank-Verbindung fehlschlägt

**Lösung:**
```bash
# Container neustarten
cd /home/r-uu/develop/github/main/config/shared/docker
docker compose restart postgres-jeeeraaah

# Logs prüfen
docker logs postgres-jeeeraaah
```

### Problem: Port bereits belegt

```bash
# Prüfe welcher Prozess Port nutzt
sudo lsof -i:9080    # Backend
sudo lsof -i:8080    # Keycloak
sudo lsof -i:5432    # PostgreSQL
```

---

## 🔑 WICHTIGE URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| **Keycloak Admin** | http://localhost:8080/admin | admin / changeme_in_local_env |
| **Keycloak Realm** | http://localhost:8080/realms/jeeeraaah-realm | - |
| **Backend API** | http://localhost:9080/jeeeraaah/ | - |
| **Backend Health** | http://localhost:9080/health | - |
| **JasperReports** | http://localhost:8090/health | - |

---

## 📝 KONFIGURATION

### Lokale Konfigurationsdateien

**NICHT unter Git-Kontrolle:**
- `config.properties` (Root-Level)
- `testing.properties` (Frontend)
- Enthalten maschinenspezifische Werte (DB-Credentials, etc.)

### Wichtige Properties

**Datenbank (PostgreSQL):**
```properties
db.host=localhost
db.port=5432
db.name=lib_test
db.user=r_uu
db.password=r_uu_password
```

**Keycloak:**
```properties
keycloak.url=http://localhost:8080
keycloak.realm=jeeeraaah-realm
keycloak.client=jeeeraaah-frontend
```

**Backend REST API:**
```properties
jeeeraaah.rest-api.scheme=http
jeeeraaah.rest-api.host=localhost
jeeeraaah.rest-api.port=9080
```

---

## 🎯 ENTWICKLUNGS-WORKFLOW

### 1. Täglich

```bash
# 1. Docker Container starten (falls nicht laufen)
docker compose up -d

# 2. Backend starten
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev

# 3. Frontend in IntelliJ starten
# Run → DashAppRunner
```

### 2. Nach Git Pull

```bash
# Dependencies aktualisieren
cd /home/r-uu/develop/github/main/root
mvn clean install
```

### 3. Bei Container-Problemen

```bash
# Container komplett reset
cd /home/r-uu/develop/github/main/config/shared/docker
docker compose down -v
docker compose up -d

# Keycloak Realm neu erstellen
cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

---

## 📚 WEITERFÜHRENDE DOKUMENTATION

- **Backend Details:** `root/app/jeeeraaah/backend/api/ws.rs/README.md`
- **Keycloak Setup:** `KEYCLOAK-SETUP-VOLLAUTOMATISCH.md`
- **Docker Setup:** `config/shared/docker/README.md`

---

## ✅ CHECKLISTE: ALLES LÄUFT

- [ ] Docker Container: Alle 4 Container sind **healthy**
- [ ] Keycloak: Admin Console erreichbar (http://localhost:8080/admin)
- [ ] Backend: Health Check erfolgreich (http://localhost:9080/health)
- [ ] Frontend: DashAppRunner startet ohne Fehler
- [ ] Login: Automatischer Login funktioniert

**🎉 WENN ALLE PUNKTE ✅, DANN LÄUFT ALLES!**
