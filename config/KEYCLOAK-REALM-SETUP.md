# Keycloak Realm Setup - Problemlösung

## Problem

Die Anwendung `DashAppRunner` schlägt mit folgendem Fehler fehl:
```
{"error":"Realm does not exist"}
```

Der konfigurierte Realm `realm_default` existiert nicht auf dem Keycloak Server.

## Lösung

### 1. Docker Container starten

Stelle sicher, dass alle Docker Container laufen:

```bash
cd /home/r-uu/develop/github/main/config/shared/docker
docker compose up -d
```

Oder verwende den Alias:
```bash
ruu-docker-start
```

### 2. Keycloak Realm Setup ausführen

Führe das automatische Setup-Skript aus:

```bash
/home/r-uu/develop/github/main/config/shared/docker/setup-keycloak-realm.sh
```

Das Skript erstellt automatisch:
- ✅ Realm: `realm_default`
- ✅ Client: `jeeeraaah-frontend` (Public Client, Direct Access Grants aktiviert)
- ✅ Test User: `r_uu` mit Passwort `r_uu_password`

### 3. Anwendung starten

Starte nun die `DashAppRunner` Anwendung in IntelliJ.

Der automatische Login sollte jetzt funktionieren.

## Manuelle Keycloak-Konfiguration (Alternative)

Falls das Skript nicht funktioniert, kannst du Keycloak manuell konfigurieren:

### Admin Console öffnen
```
http://localhost:8080/admin
Username: admin
Password: admin
```

### 1. Realm erstellen
1. Klicke auf "Create Realm"
2. Realm name: `realm_default`
3. Enabled: ON
4. Klicke "Create"

### 2. Client erstellen
1. Wähle Realm `realm_default`
2. Clients → Create Client
3. Client ID: `jeeeraaah-frontend`
4. Client type: OpenID Connect
5. Next
6. Client authentication: OFF (Public Client!)
7. Direct access grants: ON ✓
8. Standard flow: ON ✓
9. Save

### 3. Test User erstellen
1. Users → Add user
2. Username: `r_uu`
3. Email verified: ON
4. Save
5. Credentials Tab
6. Password: `r_uu_password`
7. Temporary: OFF
8. Set Password

## Konfiguration in der Anwendung

Die Konfiguration ist bereits in den Properties-Dateien korrekt gesetzt:

**`microprofile-config.properties` (Frontend):**
```properties
keycloak.host=localhost
keycloak.port=8080
keycloak.realm=realm_default
keycloak.client-id=jeeeraaah-frontend
```

**`testing.properties`:**
```properties
testing.mode=true
testing.username=r_uu
testing.password=r_uu_password
```

## Test

Teste den Login manuell mit curl:

```bash
curl -X POST 'http://localhost:8080/realms/realm_default/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=r_uu' \
  -d 'password=r_uu_password' \
  -d 'grant_type=password' \
  -d 'client_id=jeeeraaah-frontend'
```

Bei Erfolg erhältst du ein JSON mit `access_token` und `refresh_token`.

## Realm-Backup und -Wiederherstellung

### Backup erstellen

Das Projekt hat ein Skript für Docker-Reset mit Keycloak-Backup:

```bash
/home/r-uu/develop/github/main/config/shared/docker/docker-reset-with-keycloak-backup.sh
```

Dieses Skript:
1. Exportiert den aktuellen Realm (falls vorhanden)
2. Speichert das Backup in `config/shared/local/keycloak-backup/export_<timestamp>/`
3. Resettet alle Docker Container
4. Bietet Anleitung zur Wiederherstellung

### Realm exportieren (manuell)

```bash
# Export in Container
docker exec keycloak-service /opt/keycloak/bin/kc.sh export \
    --dir /tmp/keycloak-export \
    --realm realm_default \
    --users realm_file

# Kopiere Export nach local
docker cp keycloak-service:/tmp/keycloak-export ./realm-backup/
```

### Realm importieren (manuell)

**Option 1: Via Admin Console**
1. Öffne http://localhost:8080/admin
2. Login: admin / admin
3. Realm Dropdown → Create Realm
4. "Browse" → Wähle `.json` Datei aus Backup
5. "Create"

**Option 2: Via CLI**
```bash
# Kopiere Backup in Container
docker cp ./realm-backup keycloak-service:/tmp/keycloak-import

# Import
docker exec keycloak-service /opt/keycloak/bin/kc.sh import \
    --dir /tmp/keycloak-import
```

### Hinweis zu alten Backups

**Aktuell sind keine Realm-Backups im Repository vorhanden.**

Wenn Sie früher einen funktionierenden Realm hatten, prüfen Sie:
- `config/shared/local/keycloak-backup/` (wird von `docker-reset-with-keycloak-backup.sh` erstellt)
- Lokale Backups außerhalb des Repositories

Falls kein Backup verfügbar ist, verwenden Sie das Java-Setup-Programm (Methode 1) oder die manuelle Konfiguration.

## Troubleshooting

### Keycloak läuft nicht
```bash
docker compose ps
# Wenn nicht laufend:
docker compose up -d
```

### Realm existiert nicht
```bash
# Setup-Skript erneut ausführen
/home/r-uu/develop/github/main/config/shared/docker/setup-keycloak-realm.sh
```

### Port 8080 belegt
```bash
# Prüfe welcher Prozess Port 8080 belegt
lsof -i :8080
# Oder ändere KEYCLOAK_PORT in .env
```

### Admin Credentials vergessen
Die Standard-Credentials sind in `docker-compose.yml` definiert:
- Username: `admin`
- Password: `admin`

## Nächste Schritte

Nach erfolgreichem Realm-Setup:

1. ✅ DashAppRunner startet ohne Fehler
2. ✅ Automatischer Login funktioniert im Testing-Modus
3. ✅ JWT Tokens werden vom Keycloak Server ausgestellt
4. ✅ REST API Calls verwenden Bearer Token Authentication
