# 🔧 Keycloak Client Fix - Manuelle Schritte

## Schnellste Lösung: Keycloak Admin Console (EMPFOHLEN)

1. **Öffne im Browser:** http://localhost:8080/admin
2. **Login:** `admin` / `admin`
3. **Wechsle Realm:** Oben links auf "master" klicken → "realm_default" wählen
4. **Gehe zu Clients:** Linkes Menü → "Clients"
5. **Öffne jeeeraaah-frontend:** In der Liste anklicken
6. **Aktiviere Direct Access Grants:**
   - Scrolle zum Abschnitt "Capability config"
   - **Direct access grants:** Schalter auf **ON**
7. **Speichern:** Klicke unten auf "Save"

## Alternative: One-Liner Bash Command

Kopiere diesen kompletten Befehl in deine WSL-Shell:

```bash
TOKEN=$(curl -s -X POST 'http://localhost:8080/realms/master/protocol/openid-connect/token' -d 'username=admin' -d 'password=admin' -d 'grant_type=password' -d 'client_id=admin-cli' | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])"); UUID=$(curl -s "http://localhost:8080/admin/realms/realm_default/clients" -H "Authorization: Bearer $TOKEN" | python3 -c "import sys, json; clients=json.load(sys.stdin); print([c['id'] for c in clients if c['clientId']=='jeeeraaah-frontend'][0])"); curl -X PUT "http://localhost:8080/admin/realms/realm_default/clients/$UUID" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"clientId":"jeeeraaah-frontend","enabled":true,"publicClient":true,"directAccessGrantsEnabled":true,"standardFlowEnabled":true,"redirectUris":["*"],"webOrigins":["*"],"protocol":"openid-connect"}'; echo "✅ Client updated - Direct Access Grants aktiviert"
```

## Verification Test

Nach der Korrektur führe aus:

```bash
curl -X POST 'http://localhost:8080/realms/realm_default/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=r_uu' \
  -d 'password=r_uu_password' \
  -d 'grant_type=password' \
  -d 'client_id=jeeeraaah-frontend'
```

**Erwartetes Ergebnis:** JSON mit `access_token`, `token_type`, `expires_in`, etc.

**Falls Fehler:** Überprüfe nochmal in der Admin Console.

## Danach: DashAppRunner starten

1. Öffne IntelliJ
2. Starte Run Configuration: **DashAppRunner**
3. Der automatische Login sollte nun funktionieren ✅
