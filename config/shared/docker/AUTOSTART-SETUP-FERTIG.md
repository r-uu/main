# ✅ Docker Autostart erfolgreich eingerichtet!

## 🎉 Was wurde eingerichtet

### 1. Docker Proxy-Konfiguration
- **Systemd Override:** `/etc/systemd/system/docker.service.d/http-proxy.conf`
- **Docker Daemon:** `/etc/docker/daemon.json`
- **Proxy:** `172.16.28.3:8080` (GKD-RE Unternehmens-Proxy)

### 2. Docker Images
- ✅ PostgreSQL 16 Alpine heruntergeladen
- ✅ Keycloak Latest heruntergeladen

### 3. systemd Service
- **Service-Datei:** `/etc/systemd/system/jeeeraaah-docker.service`
- **Status:** Aktiviert und läuft
- **Autostart:** Aktiviert für Systemstart

### 4. Laufende Container
- ✅ `ruu-postgres` (PostgreSQL 16 Alpine)
  - Port: 5432
  - User: ruu
  - Password: changeme
  - Database: ruu_dev
  
- ✅ `ruu-keycloak` (Keycloak Latest)
  - Port: 8080
  - Admin: admin
  - Password: admin

## 🚀 Was passiert beim nächsten Systemstart

1. **Linux Mint startet** → VirtualBox VM bootet
2. **Docker-Dienst startet** → Automatisch durch systemd
3. **5 Sekunden Wartezeit** → Docker wird vollständig initialisiert
4. **JEEERAAAH-Service startet** → `docker compose up -d` wird ausgeführt
5. **Container starten** → PostgreSQL und Keycloak laufen automatisch

## 📋 Wichtige Befehle

### Service verwalten
\`\`\`bash
# Service-Status anzeigen
sudo systemctl status jeeeraaah-docker.service

# Service manuell starten
sudo systemctl start jeeeraaah-docker.service

# Service manuell stoppen
sudo systemctl stop jeeeraaah-docker.service

# Service neu starten
sudo systemctl restart jeeeraaah-docker.service

# Service-Logs anzeigen
sudo journalctl -u jeeeraaah-docker.service -f

# Autostart deaktivieren (falls nötig)
sudo systemctl disable jeeeraaah-docker.service

# Autostart aktivieren
sudo systemctl enable jeeeraaah-docker.service
\`\`\`

### Container verwalten
\`\`\`bash
# Container-Status
docker compose ps

# Container-Logs live
docker compose logs -f

# Nur PostgreSQL Logs
docker compose logs -f postgres

# Nur Keycloak Logs
docker compose logs -f keycloak

# Container stoppen (ohne Service)
docker compose down

# Container starten (ohne Service)
docker compose up -d

# Container neu starten
docker compose restart
\`\`\`

### Direkter Zugriff
\`\`\`bash
# PostgreSQL Shell
docker exec -it ruu-postgres psql -U ruu -d ruu_dev

# Container Shell
docker exec -it ruu-postgres sh
docker exec -it ruu-keycloak bash

# Keycloak Admin Console
xdg-open http://localhost:8080/admin
\`\`\`

## 🔌 Zugriff auf die Services

### PostgreSQL
- **Host:** `localhost` oder `127.0.0.1`
- **Port:** `5432`
- **Datenbank:** `ruu_dev`
- **Benutzer:** `ruu`
- **Passwort:** `changeme`

**JDBC URL:**
\`\`\`
jdbc:postgresql://localhost:5432/ruu_dev?user=ruu&password=changeme
\`\`\`

### Keycloak Admin Console
- **URL:** http://localhost:8080/admin
- **Benutzer:** `admin`
- **Passwort:** `admin`

⚠️ **Sicherheitshinweis:** Ändere die Standard-Passwörter für Produktionsumgebungen!

## 🔧 Konfiguration anpassen

### Umgebungsvariablen ändern

Bearbeite die `.env` Datei oder erstelle eine `.env.local`:

\`\`\`bash
cd /home/roger/develop/github/main/config/shared/docker

# .env.local erstellen (überschreibt .env Werte)
nano .env.local

# Beispiel-Inhalt:
POSTGRES_PASSWORD=mein_sicheres_passwort
KEYCLOAK_ADMIN_PASSWORD=mein_admin_passwort

# Nach Änderungen Container neu starten
docker compose down
docker compose up -d
\`\`\`

### Proxy-Konfiguration ändern

Falls sich die Proxy-Einstellungen ändern:

\`\`\`bash
cd /home/roger/develop/github/main/config/shared/docker

# Proxy-Konfigurationsskript anpassen
nano configure-docker-proxy.sh

# Skript ausführen
./configure-docker-proxy.sh
\`\`\`

## 🛠️ Troubleshooting

### Problem: Container starten nicht beim Systemstart

\`\`\`bash
# 1. Prüfe Service-Status
sudo systemctl status jeeeraaah-docker.service

# 2. Prüfe Service-Logs
sudo journalctl -u jeeeraaah-docker.service -n 50

# 3. Prüfe Docker-Status
sudo systemctl status docker

# 4. Manuelle Prüfung
cd /home/roger/develop/github/main/config/shared/docker
docker compose up -d
\`\`\`

### Problem: Images können nicht heruntergeladen werden

\`\`\`bash
# 1. Proxy-Konfiguration neu anwenden
cd /home/roger/develop/github/main/config/shared/docker
./configure-docker-proxy.sh

# 2. Docker neu starten
sudo systemctl restart docker

# 3. Images manuell herunterladen
docker pull postgres:16-alpine
docker pull quay.io/keycloak/keycloak:latest
\`\`\`

### Problem: Port bereits belegt

\`\`\`bash
# Prüfe welcher Prozess Port 5432 oder 8080 nutzt
sudo netstat -tlnp | grep -E "5432|8080"

# Oder mit ss
sudo ss -tlnp | grep -E "5432|8080"

# Lösung: Ports in docker-compose.yml ändern
\`\`\`

### Problem: Container haben keine Daten mehr

Die Daten werden in Docker Volumes gespeichert:

\`\`\`bash
# Volumes anzeigen
docker volume ls | grep ruu

# Volume-Details
docker volume inspect ruu-postgres-data

# Volumes sichern (falls nötig)
docker run --rm -v ruu-postgres-data:/data -v $(pwd):/backup alpine tar czf /backup/postgres-backup.tar.gz /data
\`\`\`

## 📁 Skript-Übersicht

Alle wichtigen Skripte im Verzeichnis:

\`\`\`
/home/roger/develop/github/main/config/shared/docker/
├── configure-docker-proxy.sh      # Konfiguriert Docker-Proxy
├── setup-and-start.sh             # Lädt Images und startet Container
├── docker-compose.yml             # Docker Compose Konfiguration
├── .env                           # Umgebungsvariablen
└── README-AUTOSTART.md            # Diese Datei
\`\`\`

### Skripte ausführen

\`\`\`bash
cd /home/roger/develop/github/main/config/shared/docker

# Proxy konfigurieren (einmalig oder bei Änderungen)
./configure-docker-proxy.sh

# Images herunterladen und Container starten
./setup-and-start.sh
\`\`\`

## ✅ Test der Autostart-Funktion

Um zu testen, ob alles funktioniert:

\`\`\`bash
# 1. Container stoppen
docker compose down

# 2. Service stoppen
sudo systemctl stop jeeeraaah-docker.service

# 3. VM neu starten
sudo reboot

# 4. Nach dem Neustart - Container-Status prüfen (nach ca. 30 Sekunden)
docker ps

# Du solltest beide Container sehen:
# - ruu-postgres
# - ruu-keycloak
\`\`\`

## 🎯 Zusammenfassung

✅ **Proxy konfiguriert** → Docker kann Images herunterladen  
✅ **Images heruntergeladen** → PostgreSQL und Keycloak  
✅ **Container laufen** → Beide Container sind gestartet  
✅ **systemd Service** → Aktiviert und funktionsfähig  
✅ **Autostart** → Bei jedem VM-Start werden die Container automatisch gestartet  

**Die Einrichtung ist abgeschlossen! 🎉**

Bei jedem Start deiner VirtualBox VM werden PostgreSQL und Keycloak automatisch gestartet.

