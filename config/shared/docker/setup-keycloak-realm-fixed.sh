#!/bin/bash
# Keycloak Realm Setup Script
# Erstellt realm_default, jeeeraaah-frontend Client und r_uu User

set -e

KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8080}"
ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-admin}"
REALM_NAME="${REALM_NAME:-realm_default}"
CLIENT_ID="${CLIENT_ID:-jeeeraaah-frontend}"
TEST_USER="${TEST_USER:-r_uu}"
TEST_PASSWORD="${TEST_PASSWORD:-r_uu_password}"

echo "=== Keycloak Realm Setup ==="
echo "Keycloak URL: $KEYCLOAK_URL"
echo "Realm: $REALM_NAME"
echo "Client: $CLIENT_ID"
echo ""

# Get Admin Token
echo "Requesting admin token..."
ADMIN_TOKEN=$(curl -s -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER" \
  -d "password=$ADMIN_PASSWORD" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

if [ "$ADMIN_TOKEN" == "null" ] || [ -z "$ADMIN_TOKEN" ]; then
  echo "❌ Failed to get admin token"
  exit 1
fi
echo "✅ Admin token obtained"

# Create or verify Realm
echo "Creating/verifying realm $REALM_NAME..."
REALM_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$KEYCLOAK_URL/admin/realms" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"realm\": \"$REALM_NAME\",
    \"enabled\": true,
    \"displayName\": \"JEEERAaH Default Realm\"
  }")

HTTP_CODE=$(echo "$REALM_RESPONSE" | tail -n1)
if [ "$HTTP_CODE" == "201" ]; then
  echo "✅ Realm '$REALM_NAME' created"
elif [ "$HTTP_CODE" == "409" ]; then
  echo "✓ Realm '$REALM_NAME' already exists"
else
  echo "❌ Failed to create realm (HTTP $HTTP_CODE)"
fi

# Get list of clients to check if jeeeraaah-frontend exists
echo "Checking if client '$CLIENT_ID' exists..."
CLIENTS=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

CLIENT_UUID=$(echo "$CLIENTS" | jq -r ".[] | select(.clientId==\"$CLIENT_ID\") | .id")

if [ -n "$CLIENT_UUID" ] && [ "$CLIENT_UUID" != "null" ]; then
  echo "✓ Client '$CLIENT_ID' exists (UUID: $CLIENT_UUID)"
  echo "Updating client to ensure Direct Access Grants is enabled..."

  # Update client
  curl -s -X PUT "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients/$CLIENT_UUID" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"clientId\": \"$CLIENT_ID\",
      \"enabled\": true,
      \"publicClient\": true,
      \"directAccessGrantsEnabled\": true,
      \"standardFlowEnabled\": true,
      \"redirectUris\": [\"*\"],
      \"webOrigins\": [\"*\"],
      \"protocol\": \"openid-connect\"
    }"
  echo "✅ Client '$CLIENT_ID' updated"
else
  echo "Creating client '$CLIENT_ID'..."
  CREATE_CLIENT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"clientId\": \"$CLIENT_ID\",
      \"enabled\": true,
      \"publicClient\": true,
      \"directAccessGrantsEnabled\": true,
      \"standardFlowEnabled\": true,
      \"redirectUris\": [\"*\"],
      \"webOrigins\": [\"*\"],
      \"protocol\": \"openid-connect\"
    }")

  HTTP_CODE=$(echo "$CREATE_CLIENT_RESPONSE" | tail -n1)
  if [ "$HTTP_CODE" == "201" ]; then
    echo "✅ Client '$CLIENT_ID' created"
  else
    echo "❌ Failed to create client (HTTP $HTTP_CODE)"
  fi
fi

# Get list of users to check if r_uu exists
echo "Checking if user '$TEST_USER' exists..."
USERS=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users?username=$TEST_USER" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

USER_ID=$(echo "$USERS" | jq -r ".[0].id")

if [ -n "$USER_ID" ] && [ "$USER_ID" != "null" ]; then
  echo "✓ User '$TEST_USER' exists (ID: $USER_ID)"
else
  echo "Creating user '$TEST_USER'..."
  CREATE_USER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"username\": \"$TEST_USER\",
      \"email\": \"${TEST_USER}@example.com\",
      \"enabled\": true,
      \"emailVerified\": true
    }")

  HTTP_CODE=$(echo "$CREATE_USER_RESPONSE" | tail -n1)
  if [ "$HTTP_CODE" == "201" ]; then
    echo "✅ User '$TEST_USER' created"

    # Get user ID for password reset
    USERS=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users?username=$TEST_USER" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    USER_ID=$(echo "$USERS" | jq -r ".[0].id")

    # Set password
    echo "Setting password for user '$TEST_USER'..."
    curl -s -X PUT "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users/$USER_ID/reset-password" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{
        \"type\": \"password\",
        \"value\": \"$TEST_PASSWORD\",
        \"temporary\": false
      }"
    echo "✅ Password set for user '$TEST_USER'"
  else
    echo "❌ Failed to create user (HTTP $HTTP_CODE)"
  fi
fi

echo ""
echo "=== Setup Complete ==="
echo "✅ Realm: $REALM_NAME"
echo "✅ Client: $CLIENT_ID (Direct Access Grants: enabled)"
echo "✅ User: $TEST_USER / $TEST_PASSWORD"
echo ""
echo "Test login:"
echo "curl -X POST '$KEYCLOAK_URL/realms/$REALM_NAME/protocol/openid-connect/token' \\"
echo "  -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "  -d 'username=$TEST_USER' \\"
echo "  -d 'password=$TEST_PASSWORD' \\"
echo "  -d 'grant_type=password' \\"
echo "  -d 'client_id=$CLIENT_ID'"
