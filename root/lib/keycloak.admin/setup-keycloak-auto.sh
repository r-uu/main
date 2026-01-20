#!/bin/bash
# ==========================================
# VOLLAUTOMATISCHER KEYCLOAK SETUP WORKFLOW
# ==========================================
#
# Dieses Skript stellt die komplette Keycloak-Umgebung
# automatisch wieder her.
#
# VERWENDUNG:
#   cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
#   ./setup-keycloak-auto.sh
#
# WAS DAS SKRIPT MACHT:
#   1. Prüft ob Keycloak Container läuft
#   2. Löscht existierenden Realm (falls vorhanden)
#   3. Erstellt Realm neu via Java Setup-Programm
#   4. Prüft ob Setup erfolgreich war
#   5. Testet Login
#
# ==========================================

set -e  # Bei Fehler abbrechen

echo "=========================================="
echo "KEYCLOAK SETUP - VOLLAUTOMATISCH"
echo "=========================================="
echo ""

# Schritt 1: Prüfe Docker Container
echo "1. Prüfe Docker Container..."
if ! docker ps | grep -q "keycloak-service.*healthy"; then
    echo "   ❌ Keycloak Container läuft nicht oder ist unhealthy!"
    echo "   Starte Container..."
    cd /home/r-uu/develop/github/main/config/shared/docker
    docker compose up -d
    echo "   Warte 20 Sekunden auf Keycloak..."
    sleep 20
fi
echo "   ✅ Keycloak Container läuft"
echo ""

# Schritt 2: Lösche existierenden Realm
echo "2. Lösche existierenden Realm (falls vorhanden)..."
TOKEN=$(curl -s -X POST 'http://localhost:8080/realms/master/protocol/openid-connect/token' \
    -d 'username=admin&password=changeme_in_local_env&grant_type=password&client_id=admin-cli' \
    | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    curl -s -X DELETE "http://localhost:8080/admin/realms/realm_default" \
        -H "Authorization: Bearer $TOKEN" > /dev/null 2>&1 || true
    echo "   ✅ Realm gelöscht (falls vorhanden)"
    sleep 3
else
    echo "   ⚠ Konnte Token nicht abrufen - überspringe Realm-Löschung"
fi
echo ""

# Schritt 3: Führe Java Setup aus
echo "3. Erstelle Realm neu via Java Setup..."
echo "   (Dies kann 10-15 Sekunden dauern)"
echo ""
cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
mvn -q exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup" 2>&1 \
    | grep -E "(INFO|ERROR|✅|✓)" \
    | grep -v "WARNING:"
echo ""

# Schritt 4: Prüfe ob Setup erfolgreich war
echo "4. Prüfe Setup-Ergebnis..."
if mvn -q exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup" 2>&1 | grep -q "ERROR"; then
    echo "   ❌ Setup fehlgeschlagen!"
    echo "   Prüfe Logs oben für Details"
    exit 1
fi
echo "   ✅ Setup erfolgreich"
echo ""

# Schritt 5: Teste Login
echo "5. Teste Login..."
LOGIN_RESULT=$(curl -s -X POST 'http://localhost:8080/realms/realm_default/protocol/openid-connect/token' \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d 'username=r_uu&password=r_uu_password&grant_type=password&client_id=jeeeraaah-frontend')

if echo "$LOGIN_RESULT" | grep -q '"access_token"'; then
    echo "   ✅ Login erfolgreich!"
else
    echo "   ❌ Login fehlgeschlagen:"
    echo "   $LOGIN_RESULT"
    exit 1
fi
echo ""

echo "=========================================="
echo "✅ SETUP KOMPLETT ERFOLGREICH!"
echo "=========================================="
echo ""
echo "🚀 NÄCHSTER SCHRITT:"
echo "   Starte DashAppRunner in IntelliJ"
echo ""
echo "Keycloak Admin Console:"
echo "   http://localhost:8080/admin"
echo "   Login: admin / changeme_in_local_env"
echo ""
