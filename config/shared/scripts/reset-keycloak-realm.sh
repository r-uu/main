#!/bin/bash
################################################################################
# Keycloak Realm Reset - Behebt Session Expired Probleme
################################################################################
#
# Löscht und erstellt den jeeeraaah-realm neu mit optimierten Token-Lebensdauern.
#
# VERWENDUNG:
#   bash reset-keycloak-realm.sh
#
# WAS WIRD GEMACHT:
#   1. Alten Realm löschen
#   2. Neuen Realm erstellen mit:
#      - Access Token: 30 Minuten
#      - Refresh Token: 30 Minuten (Idle)
#      - SSO Session Max: 10 Stunden
#
################################################################################

set -e

PROJECT_ROOT="/home/r-uu/develop/github/main"
KEYCLOAK_ADMIN="$PROJECT_ROOT/root/lib/keycloak.admin"

echo "════════════════════════════════════════════════════════════"
echo "🔄 Keycloak Realm Reset"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "Dieser Vorgang wird:"
echo "  1. ❌ Alten Realm 'jeeeraaah-realm' LÖSCHEN"
echo "  2. ✅ Neuen Realm erstellen mit:"
echo "     • Access Token: 30 Minuten"
echo "     • Refresh Token: 30 Minuten (Idle)"
echo "     • SSO Session Max: 10 Stunden"
echo ""
echo "⚠️  WARNUNG: Alle Benutzer werden ausgeloggt!"
echo ""
read -p "Fortfahren? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    echo "❌ Abgebrochen"
    exit 1
fi

echo ""
echo "════════════════════════════════════════════════════════════"
echo "📦 SCHRITT 1: Alten Realm löschen"
echo "════════════════════════════════════════════════════════════"
cd "$KEYCLOAK_ADMIN"
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmCleanup" -q

echo ""
echo "════════════════════════════════════════════════════════════"
echo "🏗️  SCHRITT 2: Neuen Realm erstellen"
echo "════════════════════════════════════════════════════════════"
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup" -q

echo ""
echo "════════════════════════════════════════════════════════════"
echo "✅ FERTIG!"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "Der Realm 'jeeeraaah-realm' wurde neu erstellt mit:"
echo "  ✓ Access Token: 30 Minuten"
echo "  ✓ Refresh Token: 30 Minuten (Idle)"
echo "  ✓ SSO Session Max: 10 Stunden"
echo "  ✓ Test User: r_uu / r_uu_password"
echo ""
echo "🎉 Keine 'Session Expired'-Dialoge mehr direkt nach Login!"
echo ""
echo "════════════════════════════════════════════════════════════"
echo "🚀 Nächste Schritte"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "1. Backend neu starten (falls läuft):"
echo "   cd $PROJECT_ROOT/root/app/jeeeraaah/backend/api/ws.rs"
echo "   mvn liberty:dev"
echo ""
echo "2. Frontend testen:"
echo "   # In IntelliJ: Run DashAppRunner"
echo ""
echo "3. Verifizieren:"
echo "   - Login funktioniert ✓"
echo "   - KEINE 'Session Expired'-Dialoge ✓"
echo "   - Arbeiten für 30 Minuten ohne Dialog möglich ✓"
echo ""
