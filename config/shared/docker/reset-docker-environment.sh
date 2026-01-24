#!/bin/bash

# ==============================================================================
# Docker Environment Reset Script
# ==============================================================================
#
# Löscht alle Container und Volumes und erstellt die gesamte Docker-Umgebung
# neu mit den Credentials aus der .env Datei.
#
# Verwendung:
#   ./reset-docker-environment.sh
#
# ==============================================================================

set -e  # Exit bei Fehler

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "════════════════════════════════════════════════════════════════"
echo "🔧 Docker Environment Reset"
echo "════════════════════════════════════════════════════════════════"
echo ""

# Prüfe ob .env existiert
if [ ! -f .env ]; then
    echo "❌ ERROR: .env Datei nicht gefunden!"
    echo "Bitte erstelle zuerst .env aus .env.template:"
    echo "  cp .env.template .env"
    echo "  # Bearbeite .env und setze die Credentials"
    exit 1
fi

echo "1️⃣  Stoppe alle Container..."
docker compose down || true

echo ""
echo "2️⃣  Lösche alle Volumes..."
docker volume rm postgres-jeeeraaah-data 2>/dev/null || echo "   Volume postgres-jeeeraaah-data existiert nicht"
docker volume rm postgres-keycloak-data 2>/dev/null || echo "   Volume postgres-keycloak-data existiert nicht"

echo ""
echo "3️⃣  Starte Container neu..."
docker compose up -d

echo ""
echo "4️⃣  Warte auf PostgreSQL Container..."
sleep 5

# Warte bis postgres-jeeeraaah healthy ist
echo "   Warte auf postgres-jeeeraaah..."
for i in {1..30}; do
    if docker exec postgres-jeeeraaah pg_isready -U postgres_jeeeraaah_username -d postgres_jeeeraaah_database 2>/dev/null; then
        echo "   ✅ postgres-jeeeraaah ist bereit"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "   ❌ Timeout: postgres-jeeeraaah nicht bereit"
        exit 1
    fi
    sleep 1
done

# Warte bis postgres-keycloak healthy ist
echo "   Warte auf postgres-keycloak..."
for i in {1..30}; do
    if docker exec postgres-keycloak pg_isready -U postgres_keycloak_username -d postgres_keycloak_database 2>/dev/null; then
        echo "   ✅ postgres-keycloak ist bereit"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "   ❌ Timeout: postgres-keycloak nicht bereit"
        exit 1
    fi
    sleep 1
done

echo ""
echo "5️⃣  Warte auf Keycloak..."
for i in {1..60}; do
    if docker inspect keycloak --format='{{.State.Health.Status}}' 2>/dev/null | grep -q "healthy"; then
        echo "   ✅ Keycloak ist healthy"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "   ❌ Timeout: Keycloak nicht healthy"
        exit 1
    fi
    sleep 2
done

echo ""
echo "6️⃣  Erstelle Keycloak Realm..."
cd ../../../root/lib/keycloak.admin

# Lade .env
source ../../../config/shared/docker/.env

# Debug: Zeige ob Variablen gesetzt sind
echo "   Debug: keycloak_admin_username=${keycloak_admin_username}"
echo "   Debug: test_username=${test_username}"

# Übergebe Credentials als System Properties
mvn exec:java \
  -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup" \
  -Dkeycloak.admin.user="${keycloak_admin_username}" \
  -Dkeycloak.admin.password="${keycloak_admin_password}" \
  -Dkeycloak.test.user="${test_username}" \
  -Dkeycloak.test.password="${test_password}" \
  -q

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "✅ Docker Environment erfolgreich neu erstellt!"
echo "════════════════════════════════════════════════════════════════"
echo ""
echo "📊 Container Status:"
docker compose ps
echo ""
echo "💾 Volumes:"
docker volume ls | grep postgres
echo ""
echo "🔐 Credentials (aus .env):"
echo "   PostgreSQL Jeeeraaah:"
echo "     - User: \$postgres_jeeeraaah_username"
echo "     - Password: \$postgres_jeeeraaah_password"
echo "     - Database: \$postgres_jeeeraaah_database"
echo ""
echo "   PostgreSQL Keycloak:"
echo "     - User: \$postgres_keycloak_username"
echo "     - Password: \$postgres_keycloak_password"
echo "     - Database: \$postgres_keycloak_database"
echo ""
echo "   Keycloak Admin:"
echo "     - User: \$keycloak_admin_username"
echo "     - Password: \$keycloak_admin_password"
echo ""
echo "   Test User:"
echo "     - User: \$test_username"
echo "     - Password: \$test_password"
echo ""
