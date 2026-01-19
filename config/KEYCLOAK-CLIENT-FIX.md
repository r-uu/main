# ✅ KEYCLOAK CLIENT PROBLEM BEHOBEN

## Problem
DashAppRunner erhielt **401 Unauthorized** mit:
```
"error":"invalid_client","error_description":"Invalid client or Invalid client credentials"
```

## Ursache
Der Keycloak Client `jeeeraaah-frontend` hatte **Direct Access Grants nicht aktiviert**.

## Lösung

### Manuell in Keycloak Admin Console:

1. Öffne: http://localhost:8080/admin
2. Login: `admin` / `admin`
3. Wechsle zu Realm: `realm_default`
4. Gehe zu: **Clients** → `jeeeraaah-frontend`
5. Stelle sicher:
   - ✅ **Client authentication:** OFF (Public Client)
   - ✅ **Direct access grants:** ON
   - ✅ **Standard flow:** ON
   - ✅ **Valid redirect URIs:** `*`
   - ✅ **Web origins:** `*`
6. Klicke **Save**

### Per curl (Automatisch):

```bash
# 1. Admin Token holen
ADMIN_TOKEN=$(curl -s -X POST 'http://localhost:8080/realms/master/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=admin' \
  -d 'password=admin' \
  -d 'grant_type=password' \
  -d 'client_id=admin-cli' | jq -r '.access_token')

# 2. Client ID finden
CLIENT_UUID=$(curl -s "http://localhost:8080/admin/realms/realm_default/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[] | select(.clientId=="jeeeraaah-frontend") | .id')

# 3. Client aktualisieren
curl -X PUT "http://localhost:8080/admin/realms/realm_default/clients/$CLIENT_UUID" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "jeeeraaah-frontend",
    "enabled": true,
    "publicClient": true,
    "directAccessGrantsEnabled": true,
    "standardFlowEnabled": true,
    "redirectUris": ["*"],
    "webOrigins": ["*"],
    "protocol": "openid-connect"
  }'

echo "✅ Client updated"
```

### Test nach der Korrektur:

```bash
curl -X POST 'http://localhost:8080/realms/realm_default/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=r_uu' \
  -d 'password=r_uu_password' \
  -d 'grant_type=password' \
  -d 'client_id=jeeeraaah-frontend'
```

**Erwartete Antwort:** JSON mit `access_token`, `refresh_token`, etc.

## Nächster Schritt

Nach der Korrektur:
1. **Starte DashAppRunner in IntelliJ neu**
2. Der automatische Login sollte jetzt funktionieren

## Warum ist das passiert?

Das Java Setup-Programm setzt `setDirectAccessGrantsEnabled(true)`, aber:
- Container wurden mit falschem Admin-Passwort gestartet
- Setup schlug fehl (401 Unauthorized)
- Realm/Client wurden nie erstellt

**Fix:** Admin-Passwort in docker-compose.yml ist jetzt `admin` (nicht `changeme_in_local_env`)
