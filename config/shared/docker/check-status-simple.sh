#!/bin/bash
cd "$(dirname "$0")"
echo "=== Docker Container Status ==="
docker ps
echo ""
echo "=== PostgreSQL Connection Test ==="
echo "Testing postgres-jeeeraaah..."
docker exec postgres-jeeeraaah psql -U jeeeraaah -d jeeeraaah -c "SELECT 1" 2>&1 | head -5
echo ""
echo "Testing postgres-keycloak..."
docker exec postgres-keycloak psql -U keycloak -d keycloak -c "SELECT 1" 2>&1 | head -5
echo ""
echo "=== Keycloak Realm Check ==="
curl -s http://localhost:8080/realms/jeeeraaah-realm | head -5
