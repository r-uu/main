#!/bin/bash
# =============================================================================
# Complete Docker Environment Reset & Setup
# =============================================================================
# Entfernt ALLE alten Container/Volumes/Networks und erstellt saubere Umgebung
# mit persistentem Keycloak Realm
# =============================================================================

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "════════════════════════════════════════════════════════════"
echo "🔄 Complete Docker Environment Reset & Setup"
echo "════════════════════════════════════════════════════════════"
echo ""

# 1. Stop ALLE Container (nicht nur compose)
echo "1️⃣  Stoppe ALLE Docker Container..."
RUNNING=$(docker ps -q)
if [ -n "$RUNNING" ]; then
    docker stop $(docker ps -q) 2>/dev/null || true
    echo "   ✅ Alle Container gestoppt"
else
    echo "   ℹ️  Keine laufenden Container"
fi
echo ""

# 2. Lösche ALLE alten r-uu/keycloak/postgres Container
echo "2️⃣  Lösche alte Container (inklusive verwaiste)..."
OLD_CONTAINERS="
keycloak-jeeeraaah
keycloak-service
keycloak
ruu-keycloak
postgres-jeeeraaah
postgres-keycloak
ruu-postgres
jasperreports-service
jasperreports
"

for container in $OLD_CONTAINERS; do
    if docker ps -a --format '{{.Names}}' | grep -q "^$container$"; then
        echo "   Entferne: $container"
        docker rm -f "$container" 2>/dev/null || true
    fi
done

# Entferne alle verwaisten Container
ORPHANED=$(docker ps -a --filter "status=exited" -q)
if [ -n "$ORPHANED" ]; then
    docker rm -f $ORPHANED 2>/dev/null || true
fi
echo "   ✅ Alte Container bereinigt"
echo ""

# 3. Lösche ALLE alten Volumes (außer keycloak-data für Persistenz!)
echo "3️⃣  Lösche alte Volumes (Keycloak-Daten bleiben erhalten)..."
OLD_VOLUMES="
ruu-postgres-data
ruu-postgres-backups
ruu-keycloak-data
postgres-data
postgres-backups
keycloak-data-old
"

for volume in $OLD_VOLUMES; do
    if docker volume ls --format '{{.Name}}' | grep -q "^$volume$"; then
        echo "   Entferne: $volume"
        docker volume rm "$volume" 2>/dev/null || true
    fi
done
echo "   ✅ Alte Volumes bereinigt"
echo ""

# 4. Bereinige verwaiste Networks
echo "4️⃣  Bereinige Docker Networks..."
docker network prune -f >/dev/null 2>&1 || true
echo "   ✅ Networks bereinigt"
echo ""

# 5. Prüfe .env Datei
echo "5️⃣  Prüfe .env Datei..."
if [ ! -f .env ]; then
    echo "   ❌ FEHLER: .env Datei nicht gefunden!"
    echo "   Führe aus: cd $SCRIPT_DIR && ./setup-env.sh"
    exit 1
fi
echo "   ✅ .env vorhanden"
echo ""

# 6. Starte Container mit Docker Compose
echo "6️⃣  Starte Container mit Docker Compose..."
docker compose up -d
echo "   ✅ Container gestartet"
echo ""

# 7. Warte auf gesunde Container
echo "7️⃣  Warte auf gesunde Container..."
echo "   PostgreSQL Jeeeraaah..."
for i in {1..30}; do
    if docker compose ps postgres-jeeeraaah | grep -q "healthy"; then
        echo "   ✅ PostgreSQL Jeeeraaah ist healthy"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "   ⚠️  PostgreSQL Jeeeraaah ist noch nicht healthy (fortfahren trotzdem)"
    fi
    sleep 2
done

echo "   PostgreSQL Keycloak..."
for i in {1..30}; do
    if docker compose ps postgres-keycloak | grep -q "healthy"; then
        echo "   ✅ PostgreSQL Keycloak ist healthy"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "   ❌ PostgreSQL Keycloak ist NICHT healthy!"
        docker logs postgres-keycloak --tail 50
        exit 1
    fi
    sleep 2
done

echo "   Keycloak Server..."
for i in {1..60}; do
    if docker compose ps keycloak | grep -q "healthy"; then
        echo "   ✅ Keycloak ist healthy"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "   ❌ Keycloak ist NICHT healthy!"
        docker logs keycloak --tail 100
        exit 1
    fi
    sleep 3
    if [ $((i % 10)) -eq 0 ]; then
        echo "   ⏳ Warte noch... (${i}s)"
    fi
done
echo ""

# 8. Prüfe ob Keycloak Realm existiert
echo "8️⃣  Prüfe Keycloak Realm..."
source .env
REALM_CHECK=$(curl -s "http://localhost:8080/realms/${KEYCLOAK_REALM}" 2>/dev/null || echo "")

if echo "$REALM_CHECK" | grep -q "realm_name"; then
    echo "   ✅ Realm '${KEYCLOAK_REALM}' existiert bereits (aus persistentem Volume)"
    echo "   ℹ️  Kein Setup erforderlich"
else
    echo "   ℹ️  Realm '${KEYCLOAK_REALM}' nicht gefunden - wird erstellt..."
    echo ""
    echo "9️⃣  Erstelle Keycloak Realm..."
    cd "$SCRIPT_DIR/../../../root/lib/keycloak.admin"
    mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup" -q
    if [ $? -eq 0 ]; then
        echo "   ✅ Keycloak Realm erstellt"
    else
        echo "   ❌ Fehler beim Erstellen des Keycloak Realms"
        exit 1
    fi
fi
echo ""

# Final Status
echo "════════════════════════════════════════════════════════════"
echo "✅ Docker Environment ist bereit!"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "📊 Container Status:"
docker compose ps
echo ""
echo "🔗 Services:"
echo "   PostgreSQL Jeeeraaah: localhost:5432 (user: jeeeraaah)"
echo "   PostgreSQL Keycloak:  localhost:5433 (user: keycloak)"
echo "   Keycloak Admin:       http://localhost:8080/admin"
echo "   JasperReports:        http://localhost:8090"
echo ""
echo "📝 Nächste Schritte:"
echo "   1. Teste: curl http://localhost:8080/realms/${KEYCLOAK_REALM}"
echo "   2. Starte: DashAppRunner (Maven) in IntelliJ"
echo ""
echo "💾 Persistenz:"
echo "   Keycloak Realm wird in Volume 'keycloak-data' gespeichert"
echo "   Nach Container-Neustart bleibt Realm erhalten!"
echo ""
