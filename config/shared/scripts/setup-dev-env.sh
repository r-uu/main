#!/bin/bash
# Setup-Skript für neue Entwicklungsumgebung
# Dieses Skript richtet die lokale Konfiguration ein

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
CONFIG_DIR="$PROJECT_ROOT/config"

echo "🚀 r-uu Entwicklungsumgebung Setup"
echo "=================================="
echo "Projekt-Root: $PROJECT_ROOT"
echo ""

# Erstelle config/local Verzeichnisse
echo "📁 Erstelle lokale Konfigurationsverzeichnisse..."
mkdir -p "$CONFIG_DIR/local/wsl"
mkdir -p "$CONFIG_DIR/local/docker/data/postgres"
mkdir -p "$CONFIG_DIR/local/docker/backups"

# Kopiere Templates (nur wenn Datei noch nicht existiert)
echo ""
echo "📋 Kopiere Konfigurations-Templates..."

if [ ! -f "$CONFIG_DIR/local/docker/.env.local" ]; then
    cp "$CONFIG_DIR/templates/.env.template" "$CONFIG_DIR/local/docker/.env.local"
    echo "  ✓ .env.local erstellt"
else
    echo "  ⊘ .env.local existiert bereits (übersprungen)"
fi

if [ ! -f "$CONFIG_DIR/local/wsl/aliases.sh" ]; then
    cp "$CONFIG_DIR/templates/wsl-aliases.template" "$CONFIG_DIR/local/wsl/aliases.sh"
    echo "  ✓ wsl aliases.sh erstellt"
else
    echo "  ⊘ wsl aliases.sh existiert bereits (übersprungen)"
fi

# Setze Berechtigungen
echo ""
echo "🔐 Setze Berechtigungen..."
chmod +x "$CONFIG_DIR/shared/scripts"/*.sh 2>/dev/null || true
chmod +x "$CONFIG_DIR/shared/wsl"/*.sh 2>/dev/null || true
chmod +x "$CONFIG_DIR/shared/docker/initdb"/*.sh 2>/dev/null || true
if [ -f "$CONFIG_DIR/local/wsl/aliases.sh" ]; then
    chmod +x "$CONFIG_DIR/local/wsl/aliases.sh"
fi

# Prüfe Maven Installation
echo ""
echo "🔍 Prüfe Abhängigkeiten..."
if command -v mvn &> /dev/null; then
    echo "  ✓ Maven $(mvn -version | head -n1 | cut -d' ' -f3)"
else
    echo "  ⚠ Maven nicht gefunden"
fi

if command -v docker &> /dev/null; then
    echo "  ✓ Docker $(docker --version | cut -d' ' -f3 | tr -d ',')"
else
    echo "  ⚠ Docker nicht gefunden"
fi

echo ""
echo "✅ Setup abgeschlossen!"
echo ""
echo "📝 Nächste Schritte:"
echo "  1. Bearbeite config/local/docker/.env.local mit deinen Credentials"
echo "  2. Füge zu deiner ~/.bashrc hinzu:"
echo "     source $CONFIG_DIR/shared/wsl/aliases.sh"
echo "     source $CONFIG_DIR/local/wsl/aliases.sh"
echo "  3. Lade deine Shell neu: source ~/.bashrc"
echo "  4. Starte Docker: ruu-docker-up"
echo ""

