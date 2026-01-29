# Keycloak Problem - Manuelle Lösung

## Problem
Keycloak Container hängt in einer Restart-Schleife und wird nie "healthy".

## Ursache
Wahrscheinlich beschädigtes Volume oder Konfigurations-Konflikt nach mehrfachem Reset.

## ⚡ SCHNELLSTE LÖSUNG (Neu!)

**Öffne ein WSL-Terminal und führe aus:**

```bash
ruu-docker-setup
```

Das Skript führt automatisch durch:
1. ✅ Stoppt alle Container
2. ✅ Löscht alle Volumes (Fresh Start)
3. ✅ Startet alle Container neu
4. ✅ Wartet bis Keycloak healthy ist (max 120s)
5. ✅ Erstellt automatisch den Realm
6. ✅ Zeigt finale Status-Übersicht

**Dauer:** ca. 2-3 Minuten

Falls Fehler auftreten, siehe Details unten.

---

## Lösung

Da die Terminal-Outputs in deiner Umgebung nicht angezeigt werden, musst du die Befehle **manuell in einem WSL-Terminal** ausführen (nicht über IntelliJ/Copilot):

### Option 1: Automatisches Skript (Empfohlen)

```bash
# 1. Öffne ein WSL-Terminal
# 2. Führe das Reset-Skript aus:
cd ~/develop/github/main/config/shared/docker
bash reset-keycloak.sh

# 3. Prüfe das Log (falls Probleme auftreten):
cat ~/develop/github/main/config/shared/docker/reset-keycloak.log
```

### Option 2: Manuell Schritt für Schritt

Wenn du genau sehen willst, was passiert:

```bash
# 1. Wechsle ins Docker-Verzeichnis
cd ~/develop/github/main/config/shared/docker

# 2. Stoppe Keycloak
docker compose stop keycloak
docker compose rm -f keycloak

# 3. Lösche das Keycloak-Volume (enthält möglicherweise beschädigte Daten)
docker volume rm keycloak-data

# 4. Starte Keycloak neu
docker compose up -d keycloak

# 5. Beobachte die Logs in Echtzeit
docker logs -f keycloak

# Du solltest sehen:
# - Keycloak startet
# - Verbindet sich mit postgres-keycloak Datenbank
# - "Keycloak X.X.X started" nach ca. 30-60 Sekunden

# 6. Prüfe ob healthy (in neuem Terminal)
docker ps

# Keycloak sollte nach ca. 60s als "healthy" angezeigt werden

# 7. Erstelle den Realm
cd ~/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"

# 8. Prüfe Status
cd ~/develop/github/main/config/shared/docker
./check-status.sh
```

### Option 3: Verwende die neuen Aliase

Nach dem Neuladen der Aliase (`source ~/.bashrc`):

```bash
# Komplett-Reset (Container + Volume + Realm):
ruu-keycloak-reset

# Nur Realm neu erstellen (wenn Container läuft):
ruu-keycloak-realm-reset

# Status prüfen:
ruu-docker-status
```

## Häufige Probleme

### Keycloak zeigt "Connection refused" in Logs
- PostgreSQL-Keycloak Container läuft noch nicht
- Warte 10 Sekunden, Keycloak versucht automatisch erneut

### Keycloak startet nicht (Exit Code 1)
```bash
# Prüfe Logs für genaue Fehlermeldung:
docker logs keycloak 2>&1 | grep -i error

# Häufige Ursachen:
# - Falscher DB Password in .env
# - Port 8080 bereits belegt
# - Nicht genug RAM (Docker Desktop: min 4GB empfohlen)
```

### Realm Setup schlägt fehl: "Realm does not exist"
Das ist normal beim ersten Start! 
- Keycloak ist zwar healthy, aber Realm existiert noch nicht
- Führe einfach das Realm-Setup aus (Schritt 7)

### "Unable to invoke request: Connection reset"
- Keycloak ist noch nicht vollständig gestartet
- Warte 20-30 Sekunden länger
- Prüfe: `docker logs keycloak | grep "started"`

## Nach erfolgreichem Reset

Wenn alles funktioniert:

1. ✅ `docker ps` zeigt alle Container als "healthy"
2. ✅ `ruu-docker-status` zeigt keine Fehler
3. ✅ Keycloak Admin Console erreichbar: http://localhost:8080/admin
   - User: `admin`
   - Password: `admin`
4. ✅ Realm `jeeeraaah-realm` existiert
5. ✅ Client `jeeeraaah-frontend` existiert

Dann kannst du:
- Liberty Server starten
- DashAppRunner in IntelliJ starten
- Die Anwendung sollte sich erfolgreich mit Keycloak verbinden

## Debug-Befehle

```bash
# Alle Container Status:
docker ps

# Keycloak Logs live:
docker logs -f keycloak

# Keycloak Gesundheitsstatus:
docker inspect --format='{{.State.Health.Status}}' keycloak

# Realm prüfen (wenn Keycloak läuft):
curl http://localhost:8080/realms/jeeeraaah-realm

# Datenbank-Verbindung testen:
docker exec postgres-keycloak psql -U keycloak -d keycloak -c "\l"
```

## Letzte Rettung: Alles zurücksetzen

Wenn gar nichts funktioniert:

```bash
cd ~/develop/github/main/config/shared/docker

# WARNUNG: Löscht ALLE Daten!
docker compose down -v
docker volume prune -f

# Neu starten:
docker compose up -d

# Warten bis alle healthy:
watch -n 2 docker ps

# Realm erstellen:
cd ~/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup"
```

---

**Stand:** 2026-01-25
**Erstellt:** Automatisch nach Keycloak-Reset-Problem
