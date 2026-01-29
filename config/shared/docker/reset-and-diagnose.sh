#!/bin/bash
set -e  # Exit on error

cd ~/develop/github/main/config/shared/docker

echo "════════════════════════════════════════"
echo "🔄 Docker Environment Reset & Setup"
echo "════════════════════════════════════════"
echo ""

# 1. Stop everything
echo "1️⃣  Stopping all containers..."
docker compose down -v 2>/dev/null || true
docker stop $(docker ps -aq --filter name=keycloak --filter name=postgres --filter name=jasper) 2>/dev/null || true
docker rm $(docker ps -aq --filter name=keycloak --filter name=postgres --filter name=jasper) 2>/dev/null || true

# 2. Verify .env file
echo ""
echo "2️⃣  Checking .env file..."
if [ ! -f .env ]; then
    echo "❌ ERROR: .env file not found!"
    exit 1
fi
echo "✅ .env file exists"
echo "   Sample values:"
grep -E "^(POSTGRES_JEEERAAAH_USER|POSTGRES_KEYCLOAK_USER|KEYCLOAK_ADMIN)=" .env | head -3

# 3. Start containers
echo ""
echo "3️⃣  Starting containers..."
docker compose up -d

# 4. Wait and monitor
echo ""
echo "4️⃣  Waiting for containers to initialize (60s)..."
for i in {1..12}; do
    sleep 5
    echo "   ${i}x5s - Status:"
    docker ps --format "table {{.Names}}\t{{.Status}}" | grep -E "keycloak|postgres"
done

# 5. Final status
echo ""
echo "5️⃣  Final Status Check:"
echo "════════════════════════════════════════"
docker ps
echo ""

# 6. Check Keycloak specifically
echo "6️⃣  Keycloak Status:"
echo "════════════════════════════════════════"
KEYCLOAK_STATUS=$(docker inspect keycloak --format='{{.State.Health.Status}}' 2>/dev/null || echo "not found")
echo "   Health: $KEYCLOAK_STATUS"
if [ "$KEYCLOAK_STATUS" != "healthy" ]; then
    echo ""
    echo "⚠️  Keycloak logs (last 50 lines):"
    docker logs keycloak --tail 50
fi

echo ""
echo "════════════════════════════════════════"
echo "✅ Setup complete!"
echo "════════════════════════════════════════"
echo ""
echo "Next steps:"
echo "  1. If all containers are healthy, run:"
echo "     cd ~/develop/github/main/root/lib/keycloak.admin"
echo "     mvn exec:java -Dexec.mainClass=\"de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup\""
echo ""
echo "  2. Then start Liberty and DashAppRunner in IntelliJ"
