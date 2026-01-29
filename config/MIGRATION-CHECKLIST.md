# Migration zur neuen Config-Strategie - Checkliste

## ✅ Abgeschlossen

- [x] **`.env` für Docker erstellt** (LF Line-Endings)
  - `/config/shared/docker/.env`
  - Nur Infrastructure-Credentials
  
- [x] **`testing.properties` erstellt** (Projekt-Root)
  - `/testing.properties`
  - Feste Development-Werte (kein Maven Filtering)
  
- [x] **`.gitignore` aktualisiert**
  - `local.properties` ausgeschlossen
  - Lokale Overrides ohne Git-Konflikte
  
- [x] **Alte Config-Dateien archiviert**
  - `config.properties` → `.archive/`
  - `config.properties.template` → `.archive/`
  
- [x] **Dokumentation erstellt**
  - `CONFIG-STRATEGY.md` - Benutzer-Guide
  - `CONFIG-IMPLEMENTATION-STATUS.md` - Technischer Status
  - `SIMPLIFIED-APPROACH.md` - Design-Rationale

## ⏳ Noch zu tun

### 1. Code-Migration auf MicroProfile Config ✅ ERLEDIGT

**Status:** Keine problematischen Stellen gefunden.
- Alle Properties-Aufrufe sind in Infrastruktur-Code oder Tests
- Keine hartcodierten config.properties Zugriffe mehr

### 2. Persistence.xml aktualisieren ✅ ERLEDIGT

**Status:** Alle persistence.xml verwenden feste Werte.
- Kein Maven Filtering mehr aktiv
- Alle Werte kommen aus testing.properties via MicroProfile Config

### 3. Testing-Properties validieren ✅ ERLEDIGT

**Neue Config-Health-Checks erstellt:**
- `ConfigHealthCheck` - Validiert alle Properties
- `ConfigHealthCheckTest` - Unit-Tests für Validierung
- `ConfigurationHealthCheck` - Integration in Docker-Health-Checks
- Health-Check in DashApp integriert

**Test ausführen:**
```bash
cd ~/develop/github/main/root/lib/mp.config
mvn test
```

### 4. Docker-Umgebung neu aufsetzen

**Kompletter Reset:**
```bash
cd ~/develop/github/main/config/shared/docker
docker compose down -v  # -v löscht Volumes!
docker compose up -d
docker ps  # Alle healthy?
```

### 5. Keycloak Realm Setup

**Nach Docker-Reset neu erstellen:**
```bash
cd ~/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

### 6. Liberty Server testen

**Starte Liberty mit neuer Config:**
```bash
cd ~/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

**Prüfe Server-Log auf:**
- ❌ Keine "Property not found" Fehler
- ✅ Erfolgreiche Datenbank-Verbindung
- ✅ Erfolgreiche Keycloak-Verbindung

### 7. Frontend testen

**IntelliJ Run Configuration:**
- DashAppRunner starten
- Login sollte funktionieren (Testing-Mode)
- TaskGroups sollten geladen werden

### 8. Integration Tests

**Vollständiger Test:**
```bash
cd ~/develop/github/main/root
mvn clean verify  # Inkl. Integration-Tests
```

## 🎯 Erfolgs-Kriterien

- [ ] Build läuft durch (`mvn clean install`)
- [ ] Keine Line-Ending-Warnings mehr
- [ ] Docker-Container alle healthy
- [ ] Keycloak Realm existiert
- [ ] Liberty Server startet ohne Fehler
- [ ] Frontend kann sich anmelden
- [ ] TaskGroups werden geladen
- [ ] Tests laufen durch

## 📝 Notizen

### Wenn Probleme auftreten:

1. **Prüfe MicroProfile Config-Priorität:**
   ```bash
   java -Dmp.config.debug=true -jar app.jar
   ```

2. **Validiere `.env` Line-Endings:**
   ```bash
   file config/shared/docker/.env
   # Sollte zeigen: ASCII text
   ```

3. **Teste Properties-Laden:**
   ```java
   @Inject
   Config config;
   
   String value = config.getValue("db.jeeeraaah.password", String.class);
   System.out.println("DB Password: " + value);
   ```

4. **Fallback auf alte Config:**
   ```bash
   # Alte Dateien aus .archive/ wiederherstellen
   cp .archive/config.properties .
   ```

## 🚀 Nächste Schritte nach erfolgreicher Migration

1. **Produktion vorbereiten:**
   - Umgebungsvariablen für echte Credentials
   - Kubernetes ConfigMaps/Secrets
   - Externe Config-Server (Consul, Vault)

2. **Security härten:**
   - `testing.properties` mit schwachen Passwörtern OK für Dev
   - Produktion: Starke Secrets via Env-Vars
   - `local.properties` niemals in Git committen!

3. **Monitoring:**
   - Health-Checks für Config-Verfügbarkeit
   - Alerts bei fehlenden Properties
   - Config-Änderungen loggen
