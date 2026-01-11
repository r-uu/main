#!/bin/bash
# Maven Build-Hilfsskript für r-uu Projekt
# Dieses Skript hilft beim Bauen einzelner Module oder des gesamten Projekts

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

# Farben für Output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🔨 r-uu Maven Build Helper${NC}"
echo "=============================="
echo ""

# Funktion: Build-Status anzeigen
build_status() {
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Build erfolgreich${NC}"
    else
        echo -e "${RED}✗ Build fehlgeschlagen${NC}"
        exit 1
    fi
}

# Wenn keine Parameter übergeben wurden, zeige Hilfe
if [ $# -eq 0 ]; then
    echo "Verwendung: ./maven-build.sh [OPTION]"
    echo ""
    echo "Optionen:"
    echo "  all           - Baue alle Module (BOM + Root)"
    echo "  bom           - Baue nur BOM"
    echo "  root          - Baue nur Root und Children"
    echo "  lib           - Baue nur lib Module"
    echo "  clean         - Maven Clean für alle Module"
    echo "  install-fast  - Baue alles ohne Tests"
    echo "  help          - Zeige diese Hilfe"
    echo ""
    echo "Beispiele:"
    echo "  ./maven-build.sh all"
    echo "  ./maven-build.sh bom"
    echo "  ./maven-build.sh install-fast"
    exit 0
fi

case "$1" in
    all)
        echo "📦 Baue alle Module..."
        echo ""

        echo -e "${YELLOW}Step 1/2: BOM${NC}"
        cd "$PROJECT_ROOT/bom"
        mvn clean install
        build_status
        echo ""

        echo -e "${YELLOW}Step 2/2: Root${NC}"
        cd "$PROJECT_ROOT/root"
        mvn clean install
        build_status
        ;;

    bom)
        echo "📦 Baue BOM..."
        cd "$PROJECT_ROOT/bom"
        mvn clean install
        build_status
        ;;

    root)
        echo "📦 Baue Root und Children..."
        cd "$PROJECT_ROOT/root"
        mvn clean install
        build_status
        ;;

    lib)
        echo "📦 Baue lib Module..."
        cd "$PROJECT_ROOT/root/lib"
        mvn clean install
        build_status
        ;;

    clean)
        echo "🧹 Maven Clean für alle Module..."
        cd "$PROJECT_ROOT/bom" && mvn clean
        cd "$PROJECT_ROOT/root" && mvn clean
        echo -e "${GREEN}✓ Clean abgeschlossen${NC}"
        ;;

    install-fast)
        echo "⚡ Schneller Build (ohne Tests)..."
        echo ""

        echo -e "${YELLOW}Step 1/2: BOM${NC}"
        cd "$PROJECT_ROOT/bom"
        mvn clean install -DskipTests
        build_status
        echo ""

        echo -e "${YELLOW}Step 2/2: Root${NC}"
        cd "$PROJECT_ROOT/root"
        mvn clean install -DskipTests
        build_status
        ;;

    help|--help|-h)
        $0
        ;;

    *)
        echo -e "${RED}Unbekannte Option: $1${NC}"
        echo "Verwende './maven-build.sh help' für Hilfe"
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}✅ Fertig!${NC}"

