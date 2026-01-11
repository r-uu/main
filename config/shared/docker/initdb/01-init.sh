#!/bin/bash
echo "[init] ═══════════════════════════════════════════════════════════════════"
echo "[init]   - keycloak (Keycloak Auth)"
echo "[init]   - $POSTGRES_DB (Haupt-Datenbank)"
echo "[init] Verfügbare Datenbanken:"
echo "[init] ═══════════════════════════════════════════════════════════════════"
echo "[init] ✅ Initialisierung abgeschlossen!"
echo "[init] ═══════════════════════════════════════════════════════════════════"

echo "[init] ✓ Extensions aktiviert"

EOSQL
    CREATE EXTENSION IF NOT EXISTS pg_trgm;
    -- pg_trgm für Textsuche

    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    -- UUID Extension
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

echo "[init] Aktiviere PostgreSQL Extensions..."
# ───────────────────────────────────────────────────────────────────
# Extensions aktivieren
# ───────────────────────────────────────────────────────────────────

echo "[init] ✓ Anwendungs-Datenbanken erstellt"

EOSQL
    -- GRANT ALL PRIVILEGES ON DATABASE my_app TO $POSTGRES_USER;
    -- CREATE DATABASE my_app;
    -- Beispiel:
    -- Weitere Datenbanken können hier hinzugefügt werden
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

echo "[init] Erstelle Anwendungs-Datenbanken..."
# ───────────────────────────────────────────────────────────────────
# Anwendungs-Datenbanken (Beispiel)
# ───────────────────────────────────────────────────────────────────

echo "[init] ✓ Keycloak Datenbank erstellt"

EOSQL
    GRANT ALL ON SCHEMA public TO $POSTGRES_USER;
    \c keycloak

    GRANT ALL PRIVILEGES ON DATABASE keycloak TO $POSTGRES_USER;
    CREATE DATABASE keycloak;
    -- Keycloak Datenbank
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

echo "[init] Erstelle Keycloak Datenbank..."
# ───────────────────────────────────────────────────────────────────
# Keycloak Datenbank erstellen
# ───────────────────────────────────────────────────────────────────

echo "[init] ═══════════════════════════════════════════════════════════════════"
echo "[init] r-uu PostgreSQL Initialisierung"
echo "[init] ═══════════════════════════════════════════════════════════════════"

set -e

# Wird automatisch beim ersten Start der Datenbank ausgeführt
# PostgreSQL Initialisierungs-Skript

