#!/bin/bash
# =============================================================================
# Clean Docker Environment - Removes old containers and starts fresh
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "════════════════════════════════════════════════════════════"
echo "🧹 Cleaning Docker Environment"
echo "════════════════════════════════════════════════════════════"
echo ""

# 1. Stop all compose services
echo "1️⃣  Stopping docker compose services..."
docker compose down 2>/dev/null || true
echo "   ✅ Done"
echo ""

# 2. Remove old/misnamed containers
echo "2️⃣  Removing old containers (if they exist)..."
OLD_CONTAINERS=(
    "keycloak-jeeeraaah"
    "ruu-keycloak"
    "ruu-postgres"
    "jasperreports-service"
)

for container in "${OLD_CONTAINERS[@]}"; do
    if docker ps -a --format '{{.Names}}' | grep -q "^${container}$"; then
        echo "   → Removing: $container"
        docker stop "$container" 2>/dev/null || true
        docker rm "$container" 2>/dev/null || true
    fi
done
echo "   ✅ Done"
echo ""

# 3. Start fresh
echo "3️⃣  Starting docker compose with correct container names..."
docker compose up -d
echo "   ✅ Done"
echo ""

echo "4️⃣  Waiting for containers to start..."
sleep 5
echo "   ✅ Done"
echo ""

# 4. Show status
echo "════════════════════════════════════════════════════════════"
echo "📦 Current Container Status:"
echo "════════════════════════════════════════════════════════════"
docker ps --format "table {{.Names}}\t{{.Status}}"
echo ""

echo "════════════════════════════════════════════════════════════"
echo "✅ Environment cleaned and restarted!"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "ℹ️  Expected containers:"
echo "   - keycloak"
echo "   - postgres-jeeeraaah"
echo "   - postgres-keycloak"
echo "   - jasperreports"
echo ""
echo "📝 Next steps:"
echo "   Wait for all containers to become healthy, then run:"
echo "   ./startup-and-setup.sh"
echo ""
