#!/bin/bash
set -e

echo "════════════════════════════════════════════════════════════════════"
echo "PostgreSQL Initialisierung für JEEERAAAH"
echo "════════════════════════════════════════════════════════════════════"

# Create databases (as the postgres superuser which is $POSTGRES_USER)
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Test-Datenbank für Library-Tests
    CREATE DATABASE lib_test OWNER $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE lib_test TO $POSTGRES_USER;

    \echo '✓ Datenbank erstellt: lib_test'
EOSQL

# Grant schema permissions for jeeeraaah (main DB created by env var)
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    GRANT ALL ON SCHEMA public TO $POSTGRES_USER;

    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS pg_trgm;

    \echo '✓ Schema-Rechte und Extensions für jeeeraaah aktiviert'
EOSQL

# Grant schema permissions for lib_test
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "lib_test" <<-EOSQL
    GRANT ALL ON SCHEMA public TO $POSTGRES_USER;

    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS pg_trgm;

    \echo '✓ Schema-Rechte und Extensions für lib_test aktiviert'
EOSQL

echo "════════════════════════════════════════════════════════════════════"
echo "✅ PostgreSQL Initialisierung abgeschlossen"
echo "════════════════════════════════════════════════════════════════════"
echo "Verfügbare Datenbanken:"
echo "  - $POSTGRES_DB (Haupt-Anwendung, erstellt via POSTGRES_DB)"
echo "  - lib_test (Library-Tests)"
echo "════════════════════════════════════════════════════════════════════"
