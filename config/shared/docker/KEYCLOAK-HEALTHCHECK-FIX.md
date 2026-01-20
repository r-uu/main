# ✅ Keycloak Healthcheck Fix

**Problem gelöst:** Keycloak-Container wurde als "unhealthy" angezeigt

---

## 🔍 PROBLEMANALYSE

### Symptome:
- Keycloak-Container hatte Status "unhealthy" trotz laufendem Server
- Container-Name war `keycloak-jeeeraaah` statt `keycloak-service`
- Healthcheck funktionierte nicht korrekt

### Ursachen:
1. **Falscher Container-Name**: In docker-compose.yml war Service `keycloak` aber Container-Name `keycloak-service` definiert - der laufende Container hatte aber den alten Namen `keycloak-jeeeraaah`
2. **Fehlerhafter Healthcheck**: 
   - Versuch mit `curl` scheiterte (nicht im Image verfügbar)
   - Healthcheck auf Port 8080 prüfte, aber Health-Endpoint ist auf Port 9000
3. **Falscher Endpoint**: `/health/ready` ist auf Management-Port 9000, nicht auf HTTP-Port 8080

---

## ✅ LÖSUNG

### 1. Service-Name korrigiert
```yaml
# In docker-compose.yml
keycloak-service:  # Service-Name jetzt identisch mit container_name
    image: quay.io/keycloak/keycloak:latest
    container_name: keycloak-service
```

### 2. Healthcheck optimiert
```yaml
healthcheck:
  test: ["CMD-SHELL", "exec 3<>/dev/tcp/localhost/9000 && echo -e 'GET /health/ready HTTP/1.1\\r\\nHost: localhost\\r\\nConnection: close\\r\\n\\r\\n' >&3 && grep -q '\"status\": \"UP\"' <&3"]
  interval: 30s
  timeout: 10s
  retries: 10
  start_period: 60s
```

**Wichtige Änderungen:**
- ✅ Verwendet Management-Port **9000** statt 8080
- ✅ Nutzt Shell-Befehle (keine externen Tools wie curl/wget nötig)
- ✅ Prüft auf JSON-Status `"status": "UP"`
- ✅ Reduzierter `start_period` von 120s auf 60s (Keycloak ist schnell bereit)

### 3. Container komplett neu gestartet
```bash
cd /home/r-uu/develop/github/main/config/shared/docker
docker compose down
docker compose up -d
```

---

## 🎯 ERGEBNIS

Nach 60-90 Sekunden:
```bash
$ docker ps
CONTAINER ID   IMAGE                              STATUS
f8fd14ea5cd9   quay.io/keycloak/keycloak:latest   Up (healthy)  ✅
```

Alle Container sind jetzt **healthy**:
- ✅ `postgres-jeeeraaah` - healthy
- ✅ `postgres-keycloak` - healthy  
- ✅ `keycloak-service` - **healthy** 🎉
- ✅ `jasperreports-service` - healthy

---

## 📝 AKTUALISIERTE DATEIEN

1. **docker-compose.yml**
   - Service-Name: `keycloak-service`
   - Healthcheck auf Port 9000
   - Prüft `/health/ready` mit Shell-Befehlen

2. **DOCKER-RESET-GUIDE.md**
   - Alle Referenzen zu `keycloak-jeeeraaah` entfernt
   - Aktualisiert auf `keycloak-service`

3. **root/lib/keycloak.admin/README.md**
   - Docker-Befehle aktualisiert auf `keycloak-service`

4. **docker-reset-with-keycloak-backup.sh**
   - Alter Container-Name entfernt

---

## 🔧 TECHNISCHE DETAILS

### Keycloak Health Endpoints

Keycloak stellt Health-Endpoints auf **Port 9000** (Management Interface) bereit:

```bash
# Ready Check (für Healthcheck geeignet)
GET http://localhost:9000/health/ready

# Live Check
GET http://localhost:9000/health/live

# Full Health
GET http://localhost:9000/health
```

**Response Format:**
```json
{
    "status": "UP",
    "checks": [
        {
            "name": "Keycloak database connections async health check",
            "status": "UP"
        }
    ]
}
```

### Warum Shell-basierter Healthcheck?

Keycloak-Image enthält:
- ❌ Kein `curl`
- ❌ Kein `wget`  
- ✅ Shell (`sh`)
- ✅ TCP-Device (`/dev/tcp`)

Daher nutzen wir Shell-Features für HTTP-Requests:
```bash
exec 3<>/dev/tcp/localhost/9000  # Öffne TCP-Verbindung
echo -e 'GET /health/ready HTTP/1.1\r\n...' >&3  # Sende HTTP-Request
grep -q '"status": "UP"' <&3  # Prüfe Response
```

---

## ✅ VERIFICATION

### Container-Status prüfen
```bash
docker compose ps
```

### Healthcheck manuell testen
```bash
# Im Container
docker exec keycloak-service sh -c \
  "exec 3<>/dev/tcp/localhost/9000 && \
   echo -e 'GET /health/ready HTTP/1.1\r\nHost: localhost\r\n\r\n' >&3 && \
   cat <&3"

# Erwartete Response: HTTP/1.1 200 OK mit "status": "UP"
```

### Von außen (Host)
```bash
curl -s http://localhost:8080/  # Keycloak Login-Page
curl -s http://localhost:8080/admin  # Admin Console
```

---

## 📚 REFERENZEN

- **Keycloak Health Checks**: https://www.keycloak.org/server/health
- **Docker Healthcheck**: https://docs.docker.com/engine/reference/builder/#healthcheck
- **Shell TCP Device**: `/dev/tcp` ist ein Bash-Feature für TCP-Verbindungen

---

✅ **Keycloak läuft stabil mit funktionierendem Health-Monitoring!**
