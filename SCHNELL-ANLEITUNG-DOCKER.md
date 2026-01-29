# SCHNELL-ANLEITUNG: Docker-Umgebung reparieren

## Problem
Keycloak Container startet nicht wegen Passwort-Authentifizierungsfehler.

## Lösung (5 Minuten)

### Option A: Automatisch (Empfohlen)

Öffne ein **normales WSL-Terminal** (nicht IntelliJ Terminal!) und führe aus:

```bash
# 1. Lade Aliase neu
source ~/.bashrc

# 2. Starte automatisches Setup
ruu-docker-setup
```

Das Skript führt **automatisch** alle Schritte durch:
- ✅ Stoppt Container
- ✅ Löscht Volumes  
- ✅ Startet Container neu
- ✅ Wartet bis Keycloak healthy ist
- ✅ Erstellt Realm
- ✅ Zeigt Status

**Dauer: ~2-3 Minuten**

### Option B: Manuell

Falls das automatische Skript fehlschlägt:

```bash
# 1. Kompletter Reset
cd ~/develop/github/main/config/shared/docker
docker compose down -v
docker volume prune -f

# 2. Neustart
docker compose up -d

# 3. Warte 60 Sekunden
sleep 60

# 4. Prüfe ob Keycloak healthy ist
docker ps

# Sollte zeigen: keycloak ... Up XX seconds (healthy)

# 5. Erstelle Realm
cd ~/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"

# 6. Prüfe Status
cd ~/develop/github/main/config/shared/docker
./check-status.sh
```

## Nach erfolgreichem Setup

### 1. Starte Liberty Server

In einem WSL-Terminal:
```bash
cd ~/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

### 2. Starte DashAppRunner

In IntelliJ: Run-Configuration "DashAppRunner" starten

## Erwartete Services

Nach erfolgreichem Setup sollten laufen:

| Service | URL | Credentials |
|---------|-----|-------------|
| Keycloak Admin | http://localhost:8080/admin | admin/admin |
| Keycloak Realm | http://localhost:8080/realms/jeeeraaah-realm | - |
| PostgreSQL (jeeeraaah) | localhost:5432 | jeeeraaah/jeeeraaah |
| PostgreSQL (keycloak) | localhost:5433 | keycloak/keycloak |
| JasperReports | http://localhost:8090 | - |

## Troubleshooting

### Keycloak bleibt "unhealthy"

```bash
# Prüfe Logs:
docker logs keycloak --tail 50

# Häufige Fehler:
# - "password authentication failed" → .env Datei falsch
# - "Connection refused" → PostgreSQL noch nicht bereit
```

### Realm-Setup schlägt fehl

```bash
# Warte länger (Keycloak braucht manchmal 2 Minuten):
sleep 60

# Versuche nochmal:
cd ~/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

### Terminal zeigt keine Outputs

Das ist ein bekanntes Problem in deiner Umgebung. Deshalb:
- Verwende **WSL-Terminal** (nicht IntelliJ)
- Nutze `ruu-docker-setup` (schreibt Log-Datei)

## Nächste Schritte nach Setup

1. ✅ Docker-Umgebung läuft
2. ✅ Realm existiert
3. ▶️  Liberty Server starten
4. ▶️  DashAppRunner starten
5. ▶️  Testen ob Task Groups abgerufen werden

---
**Erstellt:** 2026-01-25
**Status:** Aktiv
