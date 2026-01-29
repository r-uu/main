#!/bin/bash
# =============================================================================
# Fix MicroProfile Config Properties to use UPPERCASE .env variables
# =============================================================================

set -e

PROJECT_ROOT="/home/r-uu/develop/github/main"

echo "════════════════════════════════════════════════════════════════"
echo "🔧 Fixing MicroProfile Config Property Variables"
echo "════════════════════════════════════════════════════════════════"
echo ""

# Function to fix a single properties file
fix_properties_file() {
    local file="$1"
    echo "📝 Fixing: $file"

    # Create backup
    cp "$file" "$file.backup"

    # Replace lowercase env vars with UPPERCASE
    sed -i 's/${postgres_jeeeraaah_user:/${POSTGRES_JEEERAAAH_USER:/g' "$file"
    sed -i 's/${postgres_jeeeraaah_password:/${POSTGRES_JEEERAAAH_PASSWORD:/g' "$file"
    sed -i 's/${postgres_jeeeraaah_database:/${POSTGRES_JEEERAAAH_DATABASE:/g' "$file"
    sed -i 's/${postgres_lib_test_user:/${POSTGRES_LIB_TEST_USER:/g' "$file"
    sed -i 's/${postgres_lib_test_password:/${POSTGRES_LIB_TEST_PASSWORD:/g' "$file"
    sed -i 's/${postgres_lib_test_database:/${POSTGRES_LIB_TEST_DATABASE:/g' "$file"
    sed -i 's/${postgres_keycloak_user:/${POSTGRES_KEYCLOAK_USER:/g' "$file"
    sed -i 's/${postgres_keycloak_password:/${POSTGRES_KEYCLOAK_PASSWORD:/g' "$file"
    sed -i 's/${postgres_keycloak_database:/${POSTGRES_KEYCLOAK_DATABASE:/g' "$file"
    sed -i 's/${keycloak_admin_user:/${KEYCLOAK_ADMIN_USER:/g' "$file"
    sed -i 's/${keycloak_admin_password:/${KEYCLOAK_ADMIN_PASSWORD:/g' "$file"
    sed -i 's/${keycloak_realm:/${KEYCLOAK_REALM:/g' "$file"

    # Fix default values: Change "postgres_jeeeraaah_username" to "jeeeraaah"
    sed -i 's/:postgres_jeeeraaah_username}/:jeeeraaah}/g' "$file"
    sed -i 's/:postgres_jeeeraaah_password}/:jeeeraaah}/g' "$file"
    sed -i 's/:postgres_lib_test_username}/:lib_test}/g' "$file"
    sed -i 's/:postgres_lib_test_password}/:lib_test}/g' "$file"
    sed -i 's/:postgres_keycloak_username}/:keycloak}/g' "$file"
    sed -i 's/:postgres_keycloak_password}/:keycloak}/g' "$file"
    sed -i 's/:keycloak_admin_username}/:admin}/g' "$file"
    sed -i 's/:keycloak_admin_password}/:admin}/g' "$file"

    echo "   ✅ Fixed"
}

# Find and fix all microprofile-config.properties files
echo "🔍 Searching for microprofile-config.properties files..."
echo ""

while IFS= read -r -d '' file; do
    if [ -f "$file" ]; then
        fix_properties_file "$file"
    fi
done < <(find "$PROJECT_ROOT" -name "microprofile-config.properties" -type f -print0 2>/dev/null)

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "✅ All properties files fixed!"
echo "════════════════════════════════════════════════════════════════"
echo ""
echo "💡 Backups created with .backup extension"
echo "💡 To restore: find . -name '*.backup' -exec bash -c 'mv \"\$1\" \"\${1%.backup}\"' _ {} \\;"
