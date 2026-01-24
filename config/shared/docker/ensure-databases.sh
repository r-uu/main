#!/bin/bash
# ════════════════════════════════════════════════════════════════════
# Stellt sicher, dass ALLE benötigten Datenbanken existieren
# Kann jederzeit ausgeführt werden (idempotent)
# ════════════════════════════════════════════════════════════════════

set -e

echo "════════════════════════════════════════════════════════════════════"
echo "Prüfe und erstelle fehlende PostgreSQL-Datenbanken"
echo "════════════════════════════════════════════════════════════════════"

# Warte bis PostgreSQL bereit ist
echo "⏳ Warte auf PostgreSQL (postgres-jeeeraaah)..."
timeout 60 bash -c 'until docker exec postgres-jeeeraaah pg_isready -U r_uu -d postgres > /dev/null 2>&1; do sleep 2; done'
echo "✓ PostgreSQL ist bereit"

# Funktion zum Erstellen einer Datenbank (falls nicht vorhanden)
create_db_if_not_exists() {
    local DB_NAME=$1
    local CONTAINER=$2
    local OWNER=${3:-r_uu}

    echo ""
    echo "Prüfe Datenbank: $DB_NAME in Container $CONTAINER..."

    # Prüfe ob DB existiert (als postgres Superuser!)
    local DB_EXISTS=$(docker exec $CONTAINER psql -U postgres -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" 2>/dev/null || echo "")

    if [ "$DB_EXISTS" = "1" ]; then
        echo "  ✓ Datenbank '$DB_NAME' existiert bereits"
    else
        echo "  ➕ Erstelle Datenbank '$DB_NAME'..."

        # Erstelle Datenbank (als postgres Superuser!)
        docker exec $CONTAINER psql -U postgres -d postgres -c "CREATE DATABASE $DB_NAME OWNER $OWNER;" 2>&1 | grep -v "already exists" || true
        docker exec $CONTAINER psql -U postgres -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $OWNER;" 2>&1 | grep -v "already exists" || true

        # Extensions hinzufügen (als Owner)
        docker exec $CONTAINER psql -U $OWNER -d $DB_NAME -c "GRANT ALL ON SCHEMA public TO $OWNER;" 2>/dev/null || true
        docker exec $CONTAINER psql -U $OWNER -d $DB_NAME -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";" 2>/dev/null || true
        docker exec $CONTAINER psql -U $OWNER -d $DB_NAME -c "CREATE EXTENSION IF NOT EXISTS pg_trgm;" 2>/dev/null || true

        echo "  ✓ Datenbank '$DB_NAME' wurde erstellt und konfiguriert"
    fi
}

# Erstelle alle benötigten Datenbanken für JEEERAaH
create_db_if_not_exists "jeeeraaah" "postgres-jeeeraaah"
create_db_if_not_exists "lib_test" "postgres-jeeeraaah"

# Erstelle Keycloak Datenbank
echo ""
echo "⏳ Warte auf PostgreSQL (postgres-keycloak)..."
timeout 60 bash -c 'until docker exec postgres-keycloak pg_isready -U r_uu -d postgres > /dev/null 2>&1; do sleep 2; done'
echo "✓ PostgreSQL (Keycloak) ist bereit"

create_db_if_not_exists "keycloak" "postgres-keycloak"

echo ""
echo "════════════════════════════════════════════════════════════════════"
echo "✅ Alle Datenbanken sind vorhanden und konfiguriert"
echo "════════════════════════════════════════════════════════════════════"
echo ""
echo "Überblick der Datenbanken:"
echo ""
echo "📊 Container: postgres-jeeeraaah (Port 5432)"
docker exec postgres-jeeeraaah psql -U r_uu -d postgres -c "\l" | grep -E "jeeeraaah|lib_test|Name" || true
echo ""
echo "📊 Container: postgres-keycloak (Port 5433)"
docker exec postgres-keycloak psql -U r_uu -d postgres -c "\l" | grep -E "keycloak|Name" || true
echo ""
echo "════════════════════════════════════════════════════════════════════"
