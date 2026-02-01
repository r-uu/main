#!/bin/bash
# ============================================================================
# Start Keycloak and JasperReports Containers
# Verwendung: ./start-keycloak-jasper.sh
# Oder via Alias: ruu-keycloak-start && ruu-jasper-start
# ============================================================================

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=================================="
echo "Starting Keycloak & JasperReports"
echo "=================================="
echo ""

# Prüfe ob Docker läuft
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker daemon is not running!"
    echo "   Start with: sudo service docker start"
    exit 1
fi

# Starte Keycloak
echo "Starting Keycloak..."
docker compose up -d keycloak

# Starte JasperReports
echo "Starting JasperReports..."
docker compose up -d jasperreports

echo ""
echo "Waiting for containers to start (5 seconds)..."
sleep 5

echo ""
echo "=================================="
echo "Container Status:"
echo "=================================="
docker compose ps

echo ""
echo "✅ Done!"
echo ""
echo "Check health:"
echo "  Keycloak:      curl -f http://localhost:8080/health/ready || echo 'Not ready yet'"
echo "  JasperReports: curl -f http://localhost:8090/health || echo 'Not ready yet'"

