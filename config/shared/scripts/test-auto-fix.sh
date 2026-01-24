#!/bin/bash
# Auto-Fix Logic Test Script
# Testet verschiedene Szenarien der automatischen Behebung

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "═══════════════════════════════════════════════════════════════"
echo "🧪 Auto-Fix Logic Test"
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Farben für bessere Lesbarkeit
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

function test_scenario_1() {
    echo -e "${YELLOW}Test 1: Keycloak Container gestoppt (Realm existiert)${NC}"
    echo "────────────────────────────────────────────────────────────"

    # Stoppe Keycloak
    docker stop keycloak 2>/dev/null || true

    echo "✓ Keycloak Container gestoppt"
    echo "✓ Realm-Datenbank bleibt erhalten (in postgres-keycloak)"
    echo ""
    echo "Erwartung:"
    echo "  1. Health Check erkennt: Keycloak Container fehlt"
    echo "  2. Auto-Fix startet Container"
    echo "  3. Kein Realm Setup (Realm existiert noch!)"
    echo "  4. App startet erfolgreich"
    echo ""
    echo -e "${GREEN}➜ Starte jetzt DashAppRunner in IntelliJ${NC}"
    echo -e "${GREEN}➜ Prüfe Console: 'Setting up Keycloak realm' sollte NICHT erscheinen!${NC}"
    echo ""
    read -p "Drücke Enter um fortzufahren..."
}

function test_scenario_2() {
    echo -e "${YELLOW}Test 2: Keycloak Container UND Realm fehlen${NC}"
    echo "────────────────────────────────────────────────────────────"

    # Stoppe und lösche Keycloak + Datenbank
    docker stop keycloak postgres-keycloak 2>/dev/null || true
    docker rm keycloak postgres-keycloak 2>/dev/null || true
    docker volume rm docker_postgres-keycloak-data 2>/dev/null || true

    # Starte nur Postgres neu (ohne Realm)
    cd "$PROJECT_ROOT/config/shared/docker"
    docker compose up -d postgres-keycloak
    sleep 5

    echo "✓ Keycloak Container gelöscht"
    echo "✓ Keycloak-Datenbank gelöscht (Realm ist WEG!)"
    echo "✓ PostgreSQL läuft (aber leer)"
    echo ""
    echo "Erwartung:"
    echo "  1. Health Check erkennt: Keycloak Container fehlt"
    echo "  2. Auto-Fix startet Container"
    echo "  3. Health Check erkennt: Keycloak Realm fehlt"
    echo "  4. Auto-Fix führt Realm Setup aus"
    echo "  5. App startet erfolgreich"
    echo ""
    echo -e "${GREEN}➜ Starte jetzt DashAppRunner in IntelliJ${NC}"
    echo -e "${GREEN}➜ Prüfe Console: 'Setting up Keycloak realm' sollte erscheinen!${NC}"
    echo ""
    read -p "Drücke Enter um fortzufahren..."
}

function test_scenario_3() {
    echo -e "${YELLOW}Test 3: Alles läuft bereits${NC}"
    echo "────────────────────────────────────────────────────────────"

    # Stelle sicher dass alles läuft
    cd "$PROJECT_ROOT/config/shared/docker"
    docker compose up -d

    echo "✓ Alle Container laufen"
    echo ""
    echo "Erwartung:"
    echo "  1. Health Check: Alles OK"
    echo "  2. Kein Auto-Fix"
    echo "  3. App startet direkt (~5 Sekunden)"
    echo ""
    echo -e "${GREEN}➜ Starte jetzt DashAppRunner in IntelliJ${NC}"
    echo -e "${GREEN}➜ Prüfe Console: Keine Auto-Fix Meldungen!${NC}"
    echo ""
    read -p "Drücke Enter um fortzufahren..."
}

function cleanup() {
    echo ""
    echo -e "${YELLOW}Cleanup: Stelle normale Umgebung wieder her${NC}"
    cd "$PROJECT_ROOT/config/shared/docker"
    docker compose up -d
    echo "✓ Alle Container laufen wieder"
}

# Main Menu
echo "Wähle Test-Szenario:"
echo ""
echo "1) Keycloak gestoppt (Realm existiert) → KEIN Realm Setup"
echo "2) Keycloak + Realm gelöscht → Realm Setup wird ausgeführt"
echo "3) Alles läuft → Kein Auto-Fix"
echo "4) Cleanup (stelle normale Umgebung wieder her)"
echo "q) Beenden"
echo ""

read -p "Auswahl (1-4, q): " choice

case $choice in
    1)
        test_scenario_1
        cleanup
        ;;
    2)
        test_scenario_2
        cleanup
        ;;
    3)
        test_scenario_3
        ;;
    4)
        cleanup
        ;;
    q|Q)
        echo "Abgebrochen"
        exit 0
        ;;
    *)
        echo -e "${RED}Ungültige Auswahl${NC}"
        exit 1
        ;;
esac

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "✅ Test abgeschlossen"
echo "═══════════════════════════════════════════════════════════════"
