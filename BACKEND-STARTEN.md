# ✅ JEEERAAAH BACKEND STARTEN

**Datum:** 2026-01-19  
**Backend:** OpenLiberty auf Port 9080

## Problem: DashAppRunner kann sich nicht mit Backend verbinden

**Fehlermeldung:**
```
Connection refused
java.net.ConnectException: Connection refused
```

**Ursache:** Das Backend (REST API Server) läuft nicht.

## ✅ LÖSUNG: Backend starten

### Option 1: Maven Kommandozeile (EMPFOHLEN für Dev)

```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

**Was macht `liberty:dev`?**
- Startet OpenLiberty Server im Dev-Modus
- Hot-Reload: Änderungen werden automatisch übernommen
- Server läuft auf Port **9080**
- Endpoint: `http://localhost:9080/jeeeraaah/`

**Warte bis du siehst:**
```
[INFO] CWWKF0011I: The defaultServer server is ready to run a smarter planet.
[INFO] CWWKZ0001I: Application r-uu.app.jeeeraaah.backend.api.ws.rs started in X.XXX seconds.
```

**Dann ist das Backend bereit!**

### Option 2: IntelliJ Maven

1. Öffne Maven Tool Window (rechts in IntelliJ)
2. Navigiere zu: `root/app/jeeeraaah/backend/api/ws.rs`
3. Erweitere: `Plugins` → `liberty`
4. **Doppelklick auf:** `liberty:dev`

### Option 3: IntelliJ Run Configuration

Falls eine Run Configuration für das Backend existiert:
1. Run Configurations Dropdown
2. Wähle Backend/Liberty Configuration
3. **Run** klicken

## Backend prüfen

### Health Check:

```bash
curl http://localhost:9080/health
```

### OpenAPI Dokumentation:

```bash
curl http://localhost:9080/jeeeraaah/openapi
```

### Oder im Browser:

- Health: http://localhost:9080/health  
- API: http://localhost:9080/jeeeraaah/

## Vollständiger Workflow

### 1. Docker Container starten (falls nicht schon)

```bash
cd /home/r-uu/develop/github/main/config/shared/docker
docker compose up -d
```

Warte bis alle Container **healthy** sind.

### 2. Keycloak Realm initialisieren (falls nicht schon)

```bash
cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

### 3. Backend starten

```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

**Warte auf:** `The defaultServer server is ready`

### 4. DashAppRunner starten

**In IntelliJ:**
1. Run Configuration: `DashAppRunner`
2. **Run** klicken

## Erwartetes Ergebnis

**Wenn alles läuft:**

1. ✅ **Docker Container:** Alle healthy (Keycloak, PostgreSQL, JasperReports)
2. ✅ **Keycloak:** Realm initialisiert, Login funktioniert
3. ✅ **Backend:** OpenLiberty läuft auf Port 9080
4. ✅ **Frontend:** DashAppRunner zeigt UI an

**DashAppRunner Logs:**
```
✅ Keycloak auth service initialized
✅ Automatic login successful  
✅ Backend connection successful
✅ Task groups loaded
✅ Dashboard started
```

## Troubleshooting

### Backend kompiliert nicht (Byte Buddy / Java 25)

**Fehler:**
```
Java 25 (69) is not supported by the current version of Byte Buddy
which officially supports Java 24 (68)
```

**Lösung:** ✅ **BEREITS BEHOBEN!**

Byte Buddy wurde auf Version **1.18.4** aktualisiert (`bom/pom.xml`).

**Hinweis:** 1.18.4 ist die neueste stabile Version (Stand: Januar 2026) und unterstützt Java 25 vollständig.

Falls der Fehler weiterhin auftritt:
```bash
cd /home/r-uu/develop/github/main/root
mvn clean install
```

### Backend startet nicht

**Fehler:** Port 9080 bereits belegt
```bash
# Prüfe welcher Prozess Port 9080 nutzt
sudo lsof -i:9080
# Oder
sudo netstat -tulpn | grep 9080
```

**Lösung:** Stoppe den anderen Prozess oder ändere den Port in `server.xml`

### Backend startet, aber DashAppRunner kann sich nicht verbinden

**Prüfe:**
1. Backend läuft wirklich: `curl http://localhost:9080/health`
2. Firewall blockiert nicht Port 9080
3. DashAppRunner verwendet korrekten Port (9080)

### Datenbank-Fehler im Backend

**Fehler:** Kann nicht auf PostgreSQL zugreifen

**Lösung:**
```bash
# Prüfe ob postgres-jeeeraaah Container läuft
docker ps | grep postgres-jeeeraaah

# Falls nicht:
cd /home/r-uu/develop/github/main/config/shared/docker
docker compose up -d postgres-jeeeraaah
```

## Zusammenfassung

**Reihenfolge:**
1. Docker Container (Keycloak, PostgreSQL)
2. Keycloak Realm Setup
3. **Backend (OpenLiberty)** ← DAS HAT GEFEHLT!
4. Frontend (DashAppRunner)

**Backend-Start-Befehl:**
```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

**🎉 JETZT SOLLTE ALLES FUNKTIONIEREN!**
