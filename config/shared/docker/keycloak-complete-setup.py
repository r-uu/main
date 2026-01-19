#!/usr/bin/env python3
"""
Keycloak Complete Setup Script
Erstellt Realm, Client und User für JEEERAaH
"""
import json
import subprocess
import sys

def curl_json(method, url, headers=None, data=None):
    """Helper für curl Aufrufe"""
    cmd = ['curl', '-s', '-X', method, url]
    if headers:
        for key, value in headers.items():
            cmd.extend(['-H', f'{key}: {value}'])
    if data:
        cmd.extend(['-d', json.dumps(data)])

    result = subprocess.run(cmd, capture_output=True, text=True)
    try:
        return json.loads(result.stdout) if result.stdout else {}
    except:
        return {'error': result.stdout}

# Configuration
KEYCLOAK_URL = "http://localhost:8080"
ADMIN_USER = "admin"
ADMIN_PASSWORD = "admin"
REALM_NAME = "realm_default"
CLIENT_ID = "jeeeraaah-frontend"
TEST_USER = "r_uu"
TEST_PASSWORD = "r_uu_password"

print("=== Keycloak Setup für JEEERAaH ===\n")

# 1. Get Admin Token
print("1. Admin Token holen...")
token_data = curl_json('POST', f'{KEYCLOAK_URL}/realms/master/protocol/openid-connect/token',
    headers={'Content-Type': 'application/x-www-form-urlencoded'},
    data=f'username={ADMIN_USER}&password={ADMIN_PASSWORD}&grant_type=password&client_id=admin-cli')

if 'access_token' not in token_data:
    print(f"❌ Fehler beim Login: {token_data}")
    sys.exit(1)

token = token_data['access_token']
auth_header = {'Authorization': f'Bearer {token}'}
print(f"✅ Token erhalten\n")

# 2. Create/Verify Realm
print(f"2. Realm '{REALM_NAME}' erstellen/prüfen...")
realms = curl_json('GET', f'{KEYCLOAK_URL}/admin/realms', headers=auth_header)
realm_exists = any(r.get('realm') == REALM_NAME for r in realms)

if not realm_exists:
    realm_data = {
        "realm": REALM_NAME,
        "enabled": True,
        "displayName": "JEEERAaH Default Realm"
    }
    curl_json('POST', f'{KEYCLOAK_URL}/admin/realms',
        headers={**auth_header, 'Content-Type': 'application/json'},
        data=realm_data)
    print(f"✅ Realm '{REALM_NAME}' erstellt\n")
else:
    print(f"✓ Realm '{REALM_NAME}' existiert bereits\n")

# 3. Create/Update Client
print(f"3. Client '{CLIENT_ID}' erstellen/aktualisieren...")
clients = curl_json('GET', f'{KEYCLOAK_URL}/admin/realms/{REALM_NAME}/clients', headers=auth_header)
client = next((c for c in clients if c.get('clientId') == CLIENT_ID), None)

client_data = {
    "clientId": CLIENT_ID,
    "enabled": True,
    "publicClient": True,
    "directAccessGrantsEnabled": True,
    "standardFlowEnabled": True,
    "redirectUris": ["*"],
    "webOrigins": ["*"],
    "protocol": "openid-connect"
}

if client:
    # Update
    curl_json('PUT', f'{KEYCLOAK_URL}/admin/realms/{REALM_NAME}/clients/{client["id"]}',
        headers={**auth_header, 'Content-Type': 'application/json'},
        data=client_data)
    print(f"✅ Client '{CLIENT_ID}' aktualisiert (Direct Access Grants: ON)\n")
else:
    # Create
    curl_json('POST', f'{KEYCLOAK_URL}/admin/realms/{REALM_NAME}/clients',
        headers={**auth_header, 'Content-Type': 'application/json'},
        data=client_data)
    print(f"✅ Client '{CLIENT_ID}' erstellt (Direct Access Grants: ON)\n")

# 4. Create/Verify User
print(f"4. User '{TEST_USER}' erstellen/prüfen...")
users = curl_json('GET', f'{KEYCLOAK_URL}/admin/realms/{REALM_NAME}/users?username={TEST_USER}', headers=auth_header)

if users and len(users) > 0:
    user_id = users[0]['id']
    print(f"✓ User '{TEST_USER}' existiert bereits (ID: {user_id})")
    # Reset password to be sure
    pwd_data = {
        "type": "password",
        "value": TEST_PASSWORD,
        "temporary": False
    }
    curl_json('PUT', f'{KEYCLOAK_URL}/admin/realms/{REALM_NAME}/users/{user_id}/reset-password',
        headers={**auth_header, 'Content-Type': 'application/json'},
        data=pwd_data)
    print(f"✅ Passwort für '{TEST_USER}' zurückgesetzt\n")
else:
    # Create user
    user_data = {
        "username": TEST_USER,
        "email": f"{TEST_USER}@example.com",
        "enabled": True,
        "emailVerified": True
    }
    curl_json('POST', f'{KEYCLOAK_URL}/admin/realms/{REALM_NAME}/users',
        headers={**auth_header, 'Content-Type': 'application/json'},
        data=user_data)

    # Get user ID
    users = curl_json('GET', f'{KEYCLOAK_URL}/admin/realms/{REALM_NAME}/users?username={TEST_USER}', headers=auth_header)
    user_id = users[0]['id']

    # Set password
    pwd_data = {
        "type": "password",
        "value": TEST_PASSWORD,
        "temporary": False
    }
    curl_json('PUT', f'{KEYCLOAK_URL}/admin/realms/{REALM_NAME}/users/{user_id}/reset-password',
        headers={**auth_header, 'Content-Type': 'application/json'},
        data=pwd_data)
    print(f"✅ User '{TEST_USER}' erstellt mit Passwort\n")

# 5. Test Login
print("5. Login-Test...")
test_result = curl_json('POST', f'{KEYCLOAK_URL}/realms/{REALM_NAME}/protocol/openid-connect/token',
    headers={'Content-Type': 'application/x-www-form-urlencoded'},
    data=f'username={TEST_USER}&password={TEST_PASSWORD}&grant_type=password&client_id={CLIENT_ID}')

if 'access_token' in test_result:
    print("✅ LOGIN ERFOLGREICH!\n")
    print("="*50)
    print("✅ SETUP KOMPLETT!")
    print("="*50)
    print(f"Realm:    {REALM_NAME}")
    print(f"Client:   {CLIENT_ID}")
    print(f"User:     {TEST_USER} / {TEST_PASSWORD}")
    print(f"\n🚀 DashAppRunner kann jetzt gestartet werden!")
else:
    print(f"❌ Login fehlgeschlagen: {test_result}")
    sys.exit(1)
