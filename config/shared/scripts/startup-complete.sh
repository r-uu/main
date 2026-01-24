#!/bin/bash
set -e

echo "════════════════════════════════════════════════════════════════════"
echo "🚀 r-uu Projekt - Kompletter Startup"
echo "════════════════════════════════════════════════════════════════════"

PROJECT_ROOT="/home/r-uu/develop/github/main"
DOCKER_DIR="$PROJECT_ROOT/config/shared/docker"

# ═══════════════════════════════════════════════════════════════════
# 1. Docker Daemon starten (falls nicht läuft)
# ═══════════════════════════════════════════════════════════════════
echo ""
echo "📌 Schritt 1: Docker Daemon prüfen..."
if ! docker info >/dev/null 2>&1; then
    echo "  → Docker Daemon wird gestartet..."
    sudo service docker start
    sleep 5
    echo "  ✅ Docker Daemon gestartet"
else
    echo "  ✅ Docker Daemon läuft bereits"
fi

# ═══════════════════════════════════════════════════════════════════
# 2. Docker Container starten
# ═══════════════════════════════════════════════════════════════════
echo ""
echo "📌 Schritt 2: Docker Container starten..."
cd "$DOCKER_DIR"
docker compose up -d

echo "  → Warte auf Container-Start (30 Sekunden)..."
sleep 30

# ═══════════════════════════════════════════════════════════════════
# 3. PostgreSQL Datenbanken sicherstellen
# ═══════════════════════════════════════════════════════════════════
echo ""
echo "📌 Schritt 3: PostgreSQL Datenbanken prüfen..."

# Warte bis postgres-jeeeraaah bereit ist
echo "  → Warte auf postgres-jeeeraaah..."
for i in {1..30}; do
    if docker exec postgres-jeeeraaah pg_isready -U r_uu -d jeeeraaah >/dev/null 2>&1; then
        echo "  ✅ postgres-jeeeraaah ist bereit"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "  ❌ postgres-jeeeraaah nicht bereit nach 60 Sekunden"
        exit 1
    fi
    sleep 2
done

# lib_test Datenbank erstellen (falls nicht existiert)
echo "  → Prüfe lib_test Datenbank..."
if docker exec postgres-jeeeraaah psql -U r_uu -d postgres -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw lib_test; then
    echo "  ✅ lib_test existiert bereits"
else
    echo "  → Erstelle lib_test Datenbank..."
    docker exec postgres-jeeeraaah psql -U r_uu -d postgres -c "CREATE DATABASE lib_test OWNER r_uu;" >/dev/null 2>&1
    docker exec postgres-jeeeraaah psql -U r_uu -d lib_test -c "GRANT ALL ON SCHEMA public TO r_uu;" >/dev/null 2>&1
    echo "  ✅ lib_test erstellt"
fi

# Zeige alle Datenbanken
echo ""
echo "  📊 Verfügbare Datenbanken in postgres-jeeeraaah:"
docker exec postgres-jeeeraaah psql -U r_uu -d postgres -c "\l" 2>/dev/null | grep -E "(jeeeraaah|lib_test)" || echo "  ⚠️  Keine Datenbanken gefunden"

# ═══════════════════════════════════════════════════════════════════
# 4. Keycloak Realm einrichten
# ═══════════════════════════════════════════════════════════════════
echo ""
echo "📌 Schritt 4: Keycloak Realm einrichten..."

# Warte bis Keycloak bereit ist
echo "  → Warte auf Keycloak (max 120 Sekunden)..."
KEYCLOAK_READY=false
for i in {1..60}; do
    if docker exec keycloak /opt/keycloak/bin/kcadm.sh config credentials \
        --server http://localhost:8080 \
        --realm master \
        --user admin \
        --password admin >/dev/null 2>&1; then
        KEYCLOAK_READY=true
        echo "  ✅ Keycloak ist bereit"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "  ⚠️  Keycloak nicht bereit nach 120 Sekunden - Realm-Setup wird übersprungen"
        echo "     Führe später manuell aus: ruu-keycloak-setup"
    fi
    sleep 2
done

# Keycloak Realm erstellen (falls Keycloak bereit ist)
if [ "$KEYCLOAK_READY" = true ]; then
    echo "  → Führe Keycloak Realm Setup aus..."
    cd "$PROJECT_ROOT/root/lib/keycloak.admin"

    if mvn -q exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup" 2>&1 | tail -5; then
        echo "  ✅ Keycloak Realm eingerichtet"
    else
        echo "  ⚠️  Keycloak Realm Setup fehlgeschlagen - führe später manuell aus: ruu-keycloak-setup"
    fi
fi

# ═══════════════════════════════════════════════════════════════════
# 5. Zusammenfassung
# ═══════════════════════════════════════════════════════════════════
echo ""
echo "════════════════════════════════════════════════════════════════════"
echo "📊 Container Status:"
echo "════════════════════════════════════════════════════════════════════"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(NAMES|postgres|keycloak|jasper)"

echo ""
echo "════════════════════════════════════════════════════════════════════"
echo "✅ Startup abgeschlossen!"
echo "════════════════════════════════════════════════════════════════════"
echo ""
echo "📚 Nächste Schritte:"
echo "  → Projekt bauen: cd $PROJECT_ROOT/root && mvn clean install"
echo "  → Frontend starten: (IntelliJ Run Configuration: DashAppRunner)"
echo "  → Backend starten: cd $PROJECT_ROOT/root/app/jeeeraaah/backend/api/ws.rs && mvn liberty:dev"
echo ""
echo "🔧 Hilfsbefehle:"
echo "  → ruu-docker-ps      - Container Status anzeigen"
echo "  → ruu-keycloak-setup - Keycloak Realm manuell einrichten"
echo "  → ruu-help           - Alle Aliase anzeigen"
echo ""
