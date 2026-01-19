#!/bin/bash

# Keycloak Realm Setup Script
# Erstellt den benötigten Realm und Client für die jeeeraaah Anwendung

set -e

KEYCLOAK_URL="http://localhost:8080"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin"
REALM_NAME="realm_default"
CLIENT_ID="jeeeraaah-frontend"
TEST_USER="r_uu"
TEST_PASSWORD="r_uu_password"

echo "=== Keycloak Realm Setup ==="
echo "Keycloak URL: $KEYCLOAK_URL"
echo "Realm: $REALM_NAME"
echo "Client: $CLIENT_ID"
echo ""

# Warte bis Keycloak verfügbar ist
echo "Warte auf Keycloak Server..."
until curl -sf "$KEYCLOAK_URL/health/ready" > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo " ✓ Keycloak ist bereit"

# Hole Admin Access Token
echo "Authentifiziere als Admin..."
ADMIN_TOKEN=$(curl -sf -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER" \
  -d "password=$ADMIN_PASSWORD" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" \
  | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$ADMIN_TOKEN" ]; then
    echo "❌ Fehler: Konnte kein Admin Token erhalten"
    exit 1
fi
echo "✓ Admin Token erhalten"

# Prüfe ob Realm existiert
echo "Prüfe Realm $REALM_NAME..."
REALM_EXISTS=$(curl -sf -o /dev/null -w "%{http_code}" -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

if [ "$REALM_EXISTS" = "200" ]; then
    echo "✓ Realm $REALM_NAME existiert bereits"
else
    echo "Erstelle Realm $REALM_NAME..."
    curl -sf -X POST "$KEYCLOAK_URL/admin/realms" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "realm": "'"$REALM_NAME"'",
        "enabled": true,
        "displayName": "JEEERAaH Default Realm"
      }'
    echo "✓ Realm erstellt"
fi

# Prüfe ob Client existiert
echo "Prüfe Client $CLIENT_ID..."
CLIENT_ID_RESPONSE=$(curl -sf -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients?clientId=$CLIENT_ID" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

if echo "$CLIENT_ID_RESPONSE" | grep -q "\"clientId\":\"$CLIENT_ID\""; then
    echo "✓ Client $CLIENT_ID existiert bereits"
else
    echo "Erstelle Client $CLIENT_ID..."
    curl -sf -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "clientId": "'"$CLIENT_ID"'",
        "name": "JEEERAaH Frontend Client",
        "enabled": true,
        "publicClient": true,
        "directAccessGrantsEnabled": true,
        "standardFlowEnabled": true,
        "implicitFlowEnabled": false,
        "serviceAccountsEnabled": false,
        "redirectUris": ["*"],
        "webOrigins": ["*"]
      }'
    echo "✓ Client erstellt"
fi

# Prüfe ob User existiert
echo "Prüfe Testuser $TEST_USER..."
USER_EXISTS=$(curl -sf -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users?username=$TEST_USER" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

if echo "$USER_EXISTS" | grep -q "\"username\":\"$TEST_USER\""; then
    echo "✓ User $TEST_USER existiert bereits"
else
    echo "Erstelle Testuser $TEST_USER..."
    curl -sf -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "username": "'"$TEST_USER"'",
        "enabled": true,
        "emailVerified": true,
        "credentials": [{
          "type": "password",
          "value": "'"$TEST_PASSWORD"'",
          "temporary": false
        }]
      }'
    echo "✓ User erstellt"
fi

echo ""
echo "=== Setup abgeschlossen ==="
echo "Realm: $REALM_NAME"
echo "Client: $CLIENT_ID (Public Client, Direct Access Grants aktiviert)"
echo "Test User: $TEST_USER / $TEST_PASSWORD"
echo ""
echo "Test-Login-Command:"
echo "curl -X POST '$KEYCLOAK_URL/realms/$REALM_NAME/protocol/openid-connect/token' \\"
echo "  -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "  -d 'username=$TEST_USER' \\"
echo "  -d 'password=$TEST_PASSWORD' \\"
echo "  -d 'grant_type=password' \\"
echo "  -d 'client_id=$CLIENT_ID'"
