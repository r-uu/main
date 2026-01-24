#!/bin/bash
# Optionale Bereinigung obsoleter Skripte
# ACHTUNG: Nur ausführen wenn sicher dass Skripte nicht mehr benötigt werden!

set -e

RUU_HOME="/home/r-uu/develop/github/main"
SCRIPTS="$RUU_HOME/config/shared/scripts"
DOCKER="$RUU_HOME/config/shared/docker"

echo "════════════════════════════════════════════════════════════"
echo "Obsolete Skripte bereinigen"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "⚠️  ACHTUNG: Diese Skripte werden gelöscht:"
echo ""
echo "Scripts:"
echo "  • install-graalvm.sh (GraalVM ist installiert)"
echo "  • setup-dev-env.sh (in anderen Skripten integriert)"
echo "  • docker-manager.sh (in Aliase integriert)"
echo "  • setup-postgresql.sh (in docker-compose.yml)"
echo ""
echo "Docker:"
echo "  • start-docker-services.sh (Alias: ruu-docker-up)"
echo "  • configure-docker-proxy.sh (einmalige Konfiguration)"
echo "  • setup-and-start.sh (ersetzt durch reset-all-containers.sh)"
echo "  • restart-with-fixes.sh (ersetzt durch reset-all-containers.sh)"
echo "  • docker-reset-with-keycloak-backup.sh (dupliziert)"
echo ""
echo "════════════════════════════════════════════════════════════"
echo "Fortfahren?"
echo "  [1] Ja, Skripte löschen"
echo "  [2] Nein, abbrechen"
echo "════════════════════════════════════════════════════════════"
read -p "Auswahl [1-2]: " choice

if [ "$choice" != "1" ]; then
    echo "Abgebrochen."
    exit 0
fi

echo ""
echo "🗑️  Lösche obsolete Skripte..."

# Scripts
if [ -f "$SCRIPTS/install-graalvm.sh" ]; then
    rm "$SCRIPTS/install-graalvm.sh"
    echo "  ✓ install-graalvm.sh gelöscht"
fi

if [ -f "$SCRIPTS/setup-dev-env.sh" ]; then
    rm "$SCRIPTS/setup-dev-env.sh"
    echo "  ✓ setup-dev-env.sh gelöscht"
fi

if [ -f "$SCRIPTS/docker-manager.sh" ]; then
    rm "$SCRIPTS/docker-manager.sh"
    echo "  ✓ docker-manager.sh gelöscht"
fi

if [ -f "$SCRIPTS/setup-postgresql.sh" ]; then
    rm "$SCRIPTS/setup-postgresql.sh"
    echo "  ✓ setup-postgresql.sh gelöscht"
fi

# Docker
if [ -f "$DOCKER/start-docker-services.sh" ]; then
    rm "$DOCKER/start-docker-services.sh"
    echo "  ✓ start-docker-services.sh gelöscht"
fi

if [ -f "$DOCKER/configure-docker-proxy.sh" ]; then
    rm "$DOCKER/configure-docker-proxy.sh"
    echo "  ✓ configure-docker-proxy.sh gelöscht"
fi

if [ -f "$DOCKER/setup-and-start.sh" ]; then
    rm "$DOCKER/setup-and-start.sh"
    echo "  ✓ setup-and-start.sh gelöscht"
fi

if [ -f "$DOCKER/restart-with-fixes.sh" ]; then
    rm "$DOCKER/restart-with-fixes.sh"
    echo "  ✓ restart-with-fixes.sh gelöscht"
fi

if [ -f "$DOCKER/docker-reset-with-keycloak-backup.sh" ]; then
    rm "$DOCKER/docker-reset-with-keycloak-backup.sh"
    echo "  ✓ docker-reset-with-keycloak-backup.sh gelöscht"
fi

echo ""
echo "✅ Bereinigung abgeschlossen!"
echo ""
echo "Verbleibende Skripte:"
echo "  Scripts:"
ls -1 "$SCRIPTS" | grep -v "readme.md" | sed 's/^/    • /'
echo ""
echo "  Docker:"
ls -1 "$DOCKER" | grep -E '\.sh$' | sed 's/^/    • /'
echo ""
echo "════════════════════════════════════════════════════════════"
echo "💡 Hinweis:"
echo "  Die meisten Funktionen sind jetzt als Aliase verfügbar!"
echo "  Auflisten: ruu-help"
echo "════════════════════════════════════════════════════════════"
