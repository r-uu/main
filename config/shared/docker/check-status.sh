#!/bin/bash
echo "════════════════════════════════════════════════════════════"
echo "🔍 Docker Environment Status Check"
echo "════════════════════════════════════════════════════════════"
echo ""
cd "$(dirname "$0")"
# Load environment variables from .env file
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi
echo "📦 Container Status:"
echo "───────────────────────────────────────────────────────────��"
docker ps --all | grep -E "keycloak|postgres|jasper"
echo ""
echo "💚 Health Status:"
echo "────────────────────────────────────────────────────────────"
for container in keycloak postgres-jeeeraaah postgres-keycloak jasperreports; do
    status=$(docker inspect --format='{{.State.Health.Status}}' $container 2>/dev/null || echo "not found")
    if [ "$status" = "healthy" ]; then
        echo "✅ $container: $status"
    elif [ "$status" = "not found" ]; then
        echo "❌ $container: container not found"
    else
        echo "⚠️  $container: $status"
    fi
done
echo ""
echo "🗄️  PostgreSQL Databases:"
echo "────────────────────────────────────────────────────────────"
echo "postgres-jeeeraaah:"
docker exec postgres-jeeeraaah psql -U ${POSTGRES_JEEERAAAH_USER} -d ${POSTGRES_JEEERAAAH_DATABASE} -c "\l" 2>/dev/null | grep -E "${POSTGRES_JEEERAAAH_DATABASE}|${POSTGRES_LIB_TEST_DATABASE}" || echo "  ⚠️  Cannot connect yet"
echo ""
echo "postgres-keycloak:"
docker exec postgres-keycloak psql -U ${POSTGRES_KEYCLOAK_USER} -d ${POSTGRES_KEYCLOAK_DATABASE} -c "\l" 2>/dev/null | grep ${POSTGRES_KEYCLOAK_DATABASE} || echo "  ⚠️  Cannot connect yet"
echo ""
echo "🔐 Keycloak Realm Check:"
echo "────────────────────────────────────────────────────────────"
response=$(curl -s http://localhost:8080/realms/${KEYCLOAK_REALM} 2>/dev/null)
if echo "$response" | grep -q "${KEYCLOAK_REALM}"; then
    echo "✅ Realm '${KEYCLOAK_REALM}' exists"
else
    echo "❌ Realm '${KEYCLOAK_REALM}' not found"
fi
echo ""
echo "════════════════════════════════════════════════════════════"
echo "Status check complete"
echo "════════════════════════════════════════════════════════════"
