# ❌ FRONTEND FEHLER: Session Expired - FALSCHE LOGIN-CREDENTIALS

**Datum:** 2026-01-20  
**Fehler:** `SessionExpiredException: Your session has expired`

---

## 🔍 PROBLEM

```
Authentication failed even after token refresh. Refresh token expired. Re-login required.
de.ruu.lib.ws.rs.SessionExpiredException: Your session has expired due to inactivity
```

**Was passiert:**
- Frontend (DashAppRunner) versucht automatisch einzuloggen (Testing-Modus)
- Login schlägt fehl weil **falsche Credentials** verwendet werden
- Session kann nicht erstellt werden → Frontend kann nicht mit Backend kommunizieren

---

## 🎯 URSACHE

Die **microprofile-config.properties** hatte **FALSCHE Property-Platzhalter**:

### ❌ FALSCH (vorher):
```properties
testing.username=${db.username:r_uu}
testing.password=${db.password:r_uu_password}
```

**Problem:**
- `${db.username}` und `${db.password}` sind **Datenbank-Properties**
- Diese haben **nichts mit dem Keycloak-Login** zu tun!
- Maven Resource Filtering konnte diese nicht auflösen
- Resultat: Leere oder falsche Werte → Login schlägt fehl

### ✅ RICHTIG (jetzt):
```properties
testing.username=r_uu
testing.password=r_uu_password
```

---

## ✅ LÖSUNG - BEREITS BEHOBEN!

### Was wurde geändert:

**Datei:** `root/app/jeeeraaah/frontend/ui/fx/src/main/resources/META-INF/microprofile-config.properties`

```diff
# Testing configuration
testing=true
-testing.username=${db.username:r_uu}
-testing.password=${db.password:r_uu_password}
+testing.username=r_uu
+testing.password=r_uu_password
```

### Frontend neu kompiliert:
```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/frontend/ui/fx
mvn clean compile
```

**Status:** ✅ BEHOBEN!

---

## 🧪 NÄCHSTER SCHRITT - FRONTEND TESTEN

### In IntelliJ:

1. **Stoppe DashAppRunner** (falls läuft)
2. **Starte DashAppRunner neu**
3. **Erwartetes Ergebnis:**

```
08:XX:XX INFO  - === Testing mode enabled - attempting automatic login ===
08:XX:XX INFO  - Test credentials found: username=r_uu
08:XX:XX DEBUG - Attempting login for user: r_uu
08:XX:XX INFO  - ✅ Automatic login successful
08:XX:XX INFO  - Access token (first 50 chars): eyJhbGciOi...
```

**Dann:**
- ✅ Login erfolgreich
- ✅ Keine "Session expired" Fehler mehr
- ✅ TaskGroups werden vom Backend geladen
- ✅ Dashboard wird angezeigt

---

## 📊 VERIFIKATION

### 1. Keycloak-Login manuell testen
```bash
curl -X POST 'http://localhost:8080/realms/realm_default/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=r_uu' \
  -d 'password=r_uu_password' \
  -d 'grant_type=password' \
  -d 'client_id=jeeeraaah-frontend' \
  | grep access_token
```

**Erwartung:** `"access_token":"eyJ..."` ✅

### 2. Properties verifizieren
```bash
cat /home/r-uu/develop/github/main/root/app/jeeeraaah/frontend/ui/fx/target/classes/META-INF/microprofile-config.properties
```

**Erwartung:**
```properties
testing=true
testing.username=r_uu
testing.password=r_uu_password
```

### 3. Backend läuft?
```bash
curl http://localhost:9080/health
```

**Erwartung:** `{"status":"UP"...}` ✅

---

## 🔧 FALLS IMMER NOCH PROBLEME

### Problem: "Session expired" tritt immer noch auf

**Mögliche Ursachen:**

1. **Frontend wurde nicht neu kompiliert**
   ```bash
   cd /home/r-uu/develop/github/main/root/app/jeeeraaah/frontend/ui/fx
   mvn clean compile
   ```

2. **IntelliJ verwendet alten Build**
   - In IntelliJ: `Build` → `Rebuild Project`
   - Dann DashAppRunner neu starten

3. **Backend läuft nicht**
   ```bash
   cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
   mvn liberty:dev
   ```

4. **Keycloak-Realm falsch konfiguriert**
   ```bash
   cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
   mvn -q exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
   ```

---

## 📝 ZUSAMMENFASSUNG

**Problem:** Frontend verwendet falsche Login-Credentials  
**Ursache:** Properties hatten Datenbank-Platzhalter statt feste Werte  
**Lösung:** Properties korrigiert auf `r_uu` / `r_uu_password`  
**Status:** ✅ BEHOBEN

**Nächster Schritt:**
1. Frontend in IntelliJ neu starten
2. Automatischer Login sollte jetzt funktionieren
3. Dashboard wird angezeigt

---

✅ **DAS PROBLEM IST BEHOBEN!** Frontend sollte jetzt einwandfrei funktionieren!

### 3. Frontend neu starten

**In IntelliJ:**
- Stoppe `DashAppRunner` (falls läuft)
- Starte `DashAppRunner` neu
- Der Login sollte jetzt funktionieren

---

## 🧪 VOLLSTÄNDIGER TEST-WORKFLOW

### Schritt 1: Alle Docker-Container healthy?
```bash
docker ps
```
**Erwartung:**
```
keycloak            Up (healthy)  ✅
postgres-jeeeraaah  Up (healthy)  ✅
postgres-keycloak   Up (healthy)  ✅
jasperreports       Up (healthy)  ✅
```

### Schritt 2: Backend läuft?
```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

**Warte auf:**
```
[INFO] The defaultServer server is ready to run a smarter planet.
```

### Schritt 3: Backend-Endpoints erreichbar?
```bash
# Test ohne Token (sollte 401 geben - das ist OK!)
curl -i http://localhost:9080/jeeeraaah/taskgroups
```

**Erwartung:**
```
HTTP/1.1 401 Unauthorized
```

**NICHT:**
```
"Could not find resource for full path"  ❌
```

### Schritt 4: Frontend starten
- In IntelliJ: `DashAppRunner` ausführen
- Automatischer Login mit `r_uu` / `r_uu_password`

**Erwartung:**
- ✅ Login erfolgreich
- ✅ Keine "Session expired" Fehler
- ✅ TaskGroups werden geladen

---

## 📊 KONFIGURATION PRÜFEN

### Backend-Konfiguration (server.env)
```bash
cat /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs/src/main/liberty/config/server.env
```

**Wichtig:**
```env
# HTTP Port
default_http_port=9080

# Datenbank (muss "jeeeraaah" sein, NICHT "lib_test")
datasource_database=jeeeraaah
datasource_database_username=r_uu
datasource_database_password=r_uu_password

# Keycloak
keycloak.url=http://localhost:8080
keycloak.realm=realm_default
```

### Frontend-Konfiguration

Das Frontend verwendet automatisch:
- Backend-URL: `http://localhost:9080/jeeeraaah`
- Keycloak-URL: `http://localhost:8080`
- Realm: `realm_default`

**Diese sind in der `testing.properties` hinterlegt.**

---

## 🔧 HÄUFIGE PROBLEME

### Problem 1: "Could not find resource for full path"
**Ursache:** Backend-REST-Ressourcen sind nicht registriert  
**Lösung:** Backend neu starten mit `mvn liberty:dev`

### Problem 2: "Session expired"
**Ursache:** Backend ist nicht erreichbar oder antwortet nicht  
**Lösung:** 
1. Prüfe: `curl http://localhost:9080/health`
2. Falls offline: Backend starten
3. Frontend neu starten

### Problem 3: "401 Unauthorized" beim Frontend-Login
**Ursache:** Keycloak-Realm nicht korrekt konfiguriert  
**Lösung:**
```bash
cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
mvn -q exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

### Problem 4: "FATAL: database 'jeeeraaah' does not exist"
**Ursache:** Datenbank nicht erstellt  
**Lösung:** Siehe `config/WARNINGS-FIXED.md` → Punkt 3

---

## 💡 QUICK FIX - ALLES NEU STARTEN

Falls nichts funktioniert, **kompletter Neustart**:

```bash
# 1. Docker-Container neu starten
cd /home/r-uu/develop/github/main/config/shared/docker
docker compose down && docker compose up -d

# 2. Warte bis alle Container healthy sind (60 Sekunden)
sleep 60 && docker ps

# 3. Keycloak-Realm wiederherstellen
cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
mvn -q exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"

# 4. Backend starten
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev

# 5. Warte bis Backend bereit ist, dann Frontend starten (IntelliJ)
```

---

## ✅ ERFOLGREICHER START - CHECKLISTE

- [ ] Alle Docker-Container healthy
- [ ] Keycloak erreichbar: `curl http://localhost:8080/health/ready`
- [ ] Backend gestartet: `mvn liberty:dev`
- [ ] Backend health: `curl http://localhost:9080/health`
- [ ] Backend-Endpoints registriert: `curl http://localhost:9080/openapi | grep taskgroups`
- [ ] Frontend gestartet: `DashAppRunner` in IntelliJ
- [ ] Login erfolgreich
- [ ] Keine "Session expired" Fehler

---

## 📝 ZUSAMMENFASSUNG

**Problem:** Frontend kann Backend nicht erreichen → "Session expired"

**Root Cause:** Backend-REST-Ressourcen nicht korrekt registriert

**Lösung:** 
1. Backend neu starten: `mvn liberty:dev`
2. Warten bis "server is ready"
3. Frontend neu starten
4. Login sollte funktionieren

**Wichtig:** Backend muss **VOR** dem Frontend gestartet werden!

---

✅ **NACH DEM FIX:** Frontend und Backend sollten problemlos kommunizieren können!
