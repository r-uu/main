#!/bin/bash
# =============================================================================
# Keycloak Reset Script
# Stops Keycloak, removes its volume, and restarts it with fresh configuration
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_FILE="$SCRIPT_DIR/reset-keycloak.log"

# Redirect all output to log file and console
exec > >(tee -a "$LOG_FILE") 2>&1

echo "════════════════════════════════════════════════════════════"
echo "🔄 Keycloak Reset Script"
echo "════════════════════════════════════════════════════════════"
echo "Started: $(date)"
echo ""

cd "$SCRIPT_DIR"

# Load environment variables
if [ -f .env ]; then
    echo "✅ Loading environment variables from .env"
    export $(grep -v '^#' .env | xargs)
else
    echo "❌ ERROR: .env file not found!"
    exit 1
fi

echo ""
echo "Step 1: Stopping Keycloak container..."
docker compose stop keycloak || true
docker compose rm -f keycloak || true

echo ""
echo "Step 2: Removing Keycloak data volume..."
docker volume rm keycloak-data || echo "  (Volume didn't exist or is in use)"

echo ""
echo "Step 3: Starting Keycloak..."
docker compose up -d keycloak

echo ""
echo "Step 4: Waiting for Keycloak to become healthy (max 120 seconds)..."
counter=0
while [ $counter -lt 120 ]; do
    status=$(docker inspect --format='{{.State.Health.Status}}' keycloak 2>/dev/null || echo "not found")
    if [ "$status" = "healthy" ]; then
        echo "✅ Keycloak is healthy!"
        break
    elif [ "$status" = "not found" ]; then
        echo "⚠️  Keycloak container not found yet..."
    else
        echo "⏳ Keycloak status: $status (${counter}s elapsed)"
    fi
    sleep 5
    counter=$((counter + 5))
done

if [ $counter -ge 120 ]; then
    echo "❌ ERROR: Keycloak did not become healthy within 120 seconds"
    echo ""
    echo "Keycloak logs:"
    docker logs keycloak 2>&1 | tail -50
    exit 1
fi

echo ""
echo "════════════════════════════════════════════════════════════"
echo "✅ Keycloak Reset Complete!"
echo "════════════════════════════════════════════════════════════"
echo "Completed: $(date)"
echo ""
echo "Next steps:"
echo "1. Run Keycloak Realm Setup:"
echo "   cd ~/develop/github/main/root/lib/keycloak.admin"
echo "   mvn exec:java -Dexec.mainClass=\"de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup\""
echo ""
echo "2. Check status:"
echo "   ~/develop/github/main/config/shared/docker/check-status.sh"
echo ""
