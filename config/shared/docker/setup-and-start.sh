#!/bin/bash

# Setup-Skript für JEEERAAAH Docker-Container
# Lädt Images herunter und startet die Container

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🚀 JEEERAAAH Docker Setup"
echo "========================="
echo ""

# Schritt 1: Images herunterladen
echo "📦 Lade Docker-Images herunter..."
echo ""

echo "⬇️  PostgreSQL 16 Alpine..."
if docker pull postgres:16-alpine; then
    echo "✅ PostgreSQL-Image erfolgreich heruntergeladen"
else
    echo "❌ Fehler beim Herunterladen des PostgreSQL-Images"
    echo ""
    echo "💡 Tipp: Führe zuerst ./configure-docker-proxy.sh aus!"
    exit 1
fi

echo ""
echo "⬇️  Keycloak Latest..."
if docker pull quay.io/keycloak/keycloak:latest; then
    echo "✅ Keycloak-Image erfolgreich heruntergeladen"
else
    echo "❌ Fehler beim Herunterladen des Keycloak-Images"
    exit 1
fi

echo ""
echo "✅ Alle Images erfolgreich heruntergeladen!"
echo ""

# Schritt 2: Docker Compose starten
echo "🔧 Starte Docker-Container..."
echo ""

if docker compose up -d; then
    echo ""
    echo "✅ Container erfolgreich gestartet!"
else
    echo ""
    echo "❌ Fehler beim Starten der Container"
    exit 1
fi

# Schritt 3: Status prüfen
echo ""
echo "🔍 Container-Status:"
docker compose ps

echo ""
echo "📋 Laufende Container:"
docker ps --filter "name=ruu-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "✅ Setup abgeschlossen!"
echo ""
echo "🔌 Zugriff auf die Services:"
echo "   PostgreSQL: localhost:5432 (User: ruu, Password: changeme)"
echo "   Keycloak:   http://localhost:8080/admin (User: admin, Password: admin)"
echo ""
echo "📝 Logs anzeigen:"
echo "   docker compose logs -f"
echo "   docker compose logs -f postgres"
echo "   docker compose logs -f keycloak"
echo ""
echo "🛑 Services stoppen:"
echo "   docker compose down"

