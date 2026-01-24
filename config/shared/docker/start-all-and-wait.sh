#!/bin/bash
# Startet alle Docker Container für die JEEERAaH Anwendung
# und wartet, bis alle Container bereit sind

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="${SCRIPT_DIR}/../config/shared/docker"

echo "════════════════════════════════════════════════════════════════"
echo "🐳 Starting all Docker containers..."
echo "════════════════════════════════════════════════════════════════"

cd "$DOCKER_DIR"

# Starte alle Container
docker compose up -d

echo ""
echo "⏳ Waiting for containers to be healthy..."
echo ""

# Warte auf PostgreSQL (jeeeraaah)
echo -n "  📊 PostgreSQL (jeeeraaah)... "
until docker exec postgres-jeeeraaah pg_isready -U r_uu -d jeeeraaah > /dev/null 2>&1; do
  sleep 1
done
echo "✅"

# Warte auf PostgreSQL (keycloak)
echo -n "  📊 PostgreSQL (keycloak)... "
until docker exec postgres-keycloak pg_isready -U r_uu -d keycloak > /dev/null 2>&1; do
  sleep 1
done
echo "✅"

# Warte auf Keycloak (max 60 Sekunden)
echo -n "  🔐 Keycloak... "
for i in {1..60}; do
  if curl -s http://localhost:8080/health/ready > /dev/null 2>&1; then
    echo "✅"
    break
  fi
  if [ $i -eq 60 ]; then
    echo "⚠️ (timeout, aber Container läuft)"
  fi
  sleep 1
done

# Warte auf JasperReports
echo -n "  📄 JasperReports... "
for i in {1..30}; do
  if curl -s http://localhost:8090/health > /dev/null 2>&1; then
    echo "✅"
    break
  fi
  if [ $i -eq 30 ]; then
    echo "⚠️ (timeout, aber Container läuft)"
  fi
  sleep 1
done

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "✅ All containers are running!"
echo "════════════════════════════════════════════════════════════════"
echo ""
echo "📋 Container status:"
docker compose ps
echo ""
echo "💡 You can now start DashAppRunner in IntelliJ"
