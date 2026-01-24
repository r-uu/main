#!/bin/bash
# Automatischer Start aller Docker-Container beim Systemstart
# Dieses Script startet alle benötigten Services für das JEEERAaH-Projekt

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/../docker"

echo "════════════════════════════════════════════════════════════════════"
echo "🚀 JEEERAaH Docker Services - Autostart"
echo "════════════════════════════════════════════════════════════════════"

# 1. Prüfe ob Docker läuft
if ! docker info >/dev/null 2>&1; then
    echo "⚠️  Docker Daemon läuft nicht - starte Docker..."
    sudo service docker start
    sleep 5
fi

echo "✓ Docker Daemon läuft"

# 2. Starte alle Container
cd "$DOCKER_DIR"
echo ""
echo "🐳 Starte alle Docker-Container..."
docker compose up -d

# 3. Warte auf Healthchecks
echo ""
echo "⏳ Warte auf Container-Healthchecks..."
sleep 10

# 4. Zeige Status
echo ""
echo "════════════════════════════════════════════════════════════════════"
echo "📊 Container Status:"
echo "════════════════════════════════════════════════════════════════════"
docker compose ps

echo ""
echo "════════════════════════════════════════════════════════════════════"
echo "✅ Alle Services gestartet!"
echo "════════════════════════════════════════════════════════════════════"
echo ""
echo "📝 Wichtige URLs:"
echo "   • Keycloak Admin:  http://localhost:8080/admin"
echo "   • Liberty Backend: http://localhost:9080/"
echo "   • JasperReports:   http://localhost:8090/"
echo ""
echo "💡 Tipp: Verwende 'ruu-docker-ps' um den Status zu prüfen"
echo "════════════════════════════════════════════════════════════════════"
