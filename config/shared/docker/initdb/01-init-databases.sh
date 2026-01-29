#!/bin/bash
set -e

echo "════════════════════════════════════════════════════════════════════"
echo "PostgreSQL Initialization for JEEERAAAH"
echo "════════════════════════════════════════════════════════════════════"
echo "Main DB:      $POSTGRES_DB (created automatically by Docker)"
echo "Main User:    $POSTGRES_USER"
echo "Test DB:      ${LIB_TEST_DB:-lib_test}"
echo "Test User:    ${LIB_TEST_USER:-lib_test}"
echo "════════════════════════════════════════════════════════════════════"

# ============================================================================
# Create lib_test database with dedicated user
# Uses environment variables from docker-compose.yml:
#   LIB_TEST_DB, LIB_TEST_USER, LIB_TEST_PASSWORD
# ============================================================================
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create dedicated user for lib_test database
    -- Uses credentials from .env (postgres_lib_test_*)
    CREATE USER ${LIB_TEST_USER} WITH PASSWORD '${LIB_TEST_PASSWORD}';

    -- Create lib_test database owned by lib_test user
    CREATE DATABASE ${LIB_TEST_DB} OWNER ${LIB_TEST_USER};

    -- Grant all privileges to lib_test user
    GRANT ALL PRIVILEGES ON DATABASE ${LIB_TEST_DB} TO ${LIB_TEST_USER};

    \echo '✓ Database created: ${LIB_TEST_DB}'
    \echo '✓ User created: ${LIB_TEST_USER}'
EOSQL

# ============================================================================
# Configure main jeeeraaah database
# Enable required extensions and grant schema permissions
# ============================================================================
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Grant permissions on public schema
    GRANT ALL ON SCHEMA public TO $POSTGRES_USER;

    -- Enable UUID generation extension
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

    -- Enable fuzzy text search extension
    CREATE EXTENSION IF NOT EXISTS pg_trgm;

    \echo '✓ Schema permissions and extensions enabled for: $POSTGRES_DB'
EOSQL

# ============================================================================
# Configure lib_test database
# Enable required extensions and grant schema permissions
# ============================================================================
psql -v ON_ERROR_STOP=1 --username "${LIB_TEST_USER}" --dbname "${LIB_TEST_DB}" <<-EOSQL
    -- Grant permissions on public schema to lib_test user
    GRANT ALL ON SCHEMA public TO ${LIB_TEST_USER};

    -- Enable UUID generation extension
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

    -- Enable fuzzy text search extension
    CREATE EXTENSION IF NOT EXISTS pg_trgm;

    \echo '✓ Schema permissions and extensions enabled for: ${LIB_TEST_DB}'
EOSQL

echo "════════════════════════════════════════════════════════════════════"
echo "✅ PostgreSQL Initialization Complete"
echo "════════════════════════════════════════════════════════════════════"
echo "Available databases:"
echo "  - $POSTGRES_DB (Main application, user: $POSTGRES_USER)"
echo "  - ${LIB_TEST_DB} (Library tests, user: ${LIB_TEST_USER})"
echo "════════════════════════════════════════════════════════════════════"


