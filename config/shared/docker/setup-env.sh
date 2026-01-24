#!/bin/bash

# ==============================================================================
# Setup Environment Variables (.env)
# ==============================================================================
# Dieses Script erstellt die .env Datei aus dem Template und setzt
# Entwicklungs-Credentials ein.
#
# ACHTUNG: Nur für Entwicklungsumgebung! In Produktion andere Werte verwenden!
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"
TEMPLATE_FILE="$SCRIPT_DIR/.env.template"

echo "🔧 Setup .env Datei"
echo "=================="

# Prüfe ob Template existiert
if [ ! -f "$TEMPLATE_FILE" ]; then
    echo "❌ Template nicht gefunden: $TEMPLATE_FILE"
    exit 1
fi

# Erstelle .env aus Template
cp "$TEMPLATE_FILE" "$ENV_FILE"

echo "✅ .env Datei erstellt aus Template"

# Setze Entwicklungs-Credentials (für lokales Testen)
# ACHTUNG: In Produktion NIEMALS diese einfachen Passwörter verwenden!

# PostgreSQL Admin
sed -i 's/YOUR_POSTGRES_ADMIN_PASSWORD/admin/' "$ENV_FILE"

# PostgreSQL Jeeeraaah Database
sed -i 's/YOUR_JEEERAAAH_DB_USER/jeeeraaah/' "$ENV_FILE"
sed -i 's/YOUR_JEEERAAAH_DB_PASSWORD/jeeeraaah/' "$ENV_FILE"

# PostgreSQL Keycloak Database
sed -i 's/YOUR_KEYCLOAK_DB_USER/keycloak/' "$ENV_FILE"
sed -i 's/YOUR_KEYCLOAK_DB_PASSWORD/keycloak/' "$ENV_FILE"

# Keycloak Admin (bleibt bei admin/admin)

# Test User
sed -i 's/YOUR_TEST_USER/test/' "$ENV_FILE"
sed -i 's/YOUR_TEST_PASSWORD/test/' "$ENV_FILE"

echo "✅ Entwicklungs-Credentials eingesetzt"
echo ""
echo "📝 Übersicht der gesetzten Credentials:"
echo "  PostgreSQL Admin:      postgres_admin / admin"
echo "  PostgreSQL Jeeeraaah:  jeeeraaah / jeeeraaah"
echo "  PostgreSQL Keycloak:   keycloak / keycloak"
echo "  Keycloak Admin:        admin / admin"
echo "  Test User:             test / test"
echo ""
echo "⚠️  WICHTIG:"
echo "   - Diese Credentials sind nur für Entwicklung!"
echo "   - .env ist in .gitignore und wird NICHT committed"
echo "   - Für Produktion: .env manuell mit sicheren Passwörtern erstellen"
echo ""
echo "🚀 Nächste Schritte:"
echo "   1. cd ~/develop/github/main/root"
echo "   2. mvn clean install"
echo "   3. ruu-docker-restart"
echo "   4. ruu-keycloak-setup"
echo ""
