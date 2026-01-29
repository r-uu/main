#!/bin/bash
set -e

# ════════════════════════════════════════════════════════════════════
# PostgreSQL Multi-Database Entrypoint Wrapper
# ════════════════════════════════════════════════════════════════════
# Warum: PostgreSQL unterstützt nur EINE Datenbank via POSTGRES_DB
# Lösung: Dieses Skript erstellt alle benötigten Datenbanken beim Start
# ════════════════════════════════════════════════════════════════════

echo "📦 PostgreSQL Entrypoint Wrapper gestartet"
echo "   POSTGRES_USER: ${POSTGRES_USER}"
echo "   POSTGRES_DB: ${POSTGRES_DB}"

# ═══════════════════════════════════════════════════════════════════
# Konfiguration: Zusätzliche Datenbanken (außer POSTGRES_DB)
# ═══════════════════════════════════════════════════════════════════
# Für postgres-jeeeraaah: POSTGRES_DB=jeeeraaah, zusätzlich lib_test
# Für postgres-keycloak: POSTGRES_DB=keycloak, keine zusätzlichen DBs
ADDITIONAL_DATABASES=()

if [ "$POSTGRES_DB" = "jeeeraaah" ]; then
    # Verwende POSTGRES_USER als Owner (jeeeraaah), da dieser garantiert existiert
    ADDITIONAL_DATABASES=("lib_test:${POSTGRES_USER}:Library test database")
fi

# ═══════════════════════════════════════════════════════════════════
# Funktion: Datenbanken erstellen (wird nach PostgreSQL-Start ausgeführt)
# ═══════════════════════════════════════════════════════════════════
ensure_databases() {
    echo "→ Prüfe und erstelle Datenbanken..."

    # Warte bis PostgreSQL bereit ist
    until pg_isready -U "$POSTGRES_USER" >/dev/null 2>&1; do
        echo "  Warte auf PostgreSQL..."
        sleep 1
    done

    # Zusätzliche Wartezeit für vollständige Initialisierung
    sleep 3

    echo "✅ PostgreSQL ist bereit"

    # Prüfe ob POSTGRES_USER existiert
    USER_EXISTS=$(psql -U postgres -tAc "SELECT 1 FROM pg_roles WHERE rolname='$POSTGRES_USER'" 2>/dev/null || echo "0")
    if [ "$USER_EXISTS" != "1" ]; then
        echo "  ⚠️  User $POSTGRES_USER existiert noch nicht, warte..."
        sleep 2
    fi

    # Hauptdatenbank (POSTGRES_DB) wird automatisch vom Docker-Entrypoint erstellt
    echo "  ✅ Hauptdatenbank: $POSTGRES_DB (wird automatisch erstellt)"

    # Erstelle zusätzliche Datenbanken
    if [ ${#ADDITIONAL_DATABASES[@]} -eq 0 ]; then
        echo "  → Keine zusätzlichen Datenbanken erforderlich"
    else
        for db_config in "${ADDITIONAL_DATABASES[@]}"; do
            IFS=':' read -r dbname owner description <<< "$db_config"

            echo "→ Prüfe Datenbank: $dbname"

            if psql -U "$POSTGRES_USER" -lqt | cut -d \| -f 1 | grep -qw "$dbname"; then
                echo "  ✅ $dbname existiert bereits"
            else
                echo "  → Erstelle $dbname ($description)..."
                psql -U "$POSTGRES_USER" <<-EOSQL
                    CREATE DATABASE $dbname OWNER $owner;
                    GRANT ALL PRIVILEGES ON DATABASE $dbname TO $owner;
EOSQL

                # Schema-Rechte und Extensions
                psql -U "$POSTGRES_USER" -d "$dbname" <<-EOSQL
                    GRANT ALL ON SCHEMA public TO $owner;
                    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
                    CREATE EXTENSION IF NOT EXISTS pg_trgm;
EOSQL

                echo "  ✅ $dbname erstellt ($description)"
            fi
        done
    fi

    echo "✅ Alle Datenbanken bereit!"
}

# ═══════════════════════════════════════════════════════════════════
# Starte Datenbank-Setup im Hintergrund (nach PostgreSQL-Start)
# ═══════════════════════════════════════════════════════════════════
(
    # Warte 10 Sekunden bis PostgreSQL vollständig gestartet und initialisiert ist
    sleep 10
    ensure_databases
) &

# ═══════════════════════════════════════════════════════════════════
# Führe den originalen PostgreSQL Entrypoint aus
# ═══════════════════════════════════════════════════════════════════
echo "→ Starte PostgreSQL Server..."
exec docker-entrypoint.sh "$@"
