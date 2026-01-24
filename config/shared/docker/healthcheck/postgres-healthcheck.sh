#!/bin/bash
# ════════════════════════════════════════════════════════════════════
# PostgreSQL Healthcheck
# ════════════════════════════════════════════════════════════════════
# Prüft ob PostgreSQL bereit ist und alle Datenbanken existieren
# Datenbank-Erstellung erfolgt im Entrypoint-Wrapper!
# ════════════════════════════════════════════════════════════════════

set -e

# Verwende POSTGRES_USER aus Umgebungsvariablen (wird von docker-compose gesetzt)
# Für postgres-jeeeraaah: POSTGRES_USER=postgres_jeeeraaah_username
# Für postgres-keycloak: POSTGRES_USER=postgres_keycloak_username
: ${POSTGRES_USER:=postgres}

# Standard Health Check - prüfe ob PostgreSQL bereit ist
pg_isready -U "$POSTGRES_USER" >/dev/null 2>&1 || exit 1

# Prüfe ob die Hauptdatenbank existiert
# Für postgres-jeeeraaah: POSTGRES_DB=jeeeraaah
# Für postgres-keycloak: POSTGRES_DB=keycloak
if [ -n "$POSTGRES_DB" ] && [ "$POSTGRES_DB" != "postgres" ]; then
    if ! psql -U "$POSTGRES_USER" -d postgres -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw "$POSTGRES_DB"; then
        echo "⚠️  Hauptdatenbank fehlt: $POSTGRES_DB (wird beim nächsten Start erstellt)"
        exit 1
    fi
fi

# Für postgres-jeeeraaah: Prüfe zusätzlich ob lib_test existiert
if [ "$POSTGRES_DB" = "jeeeraaah" ]; then
    if ! psql -U "$POSTGRES_USER" -d postgres -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw "lib_test"; then
        echo "⚠️  Test-Datenbank fehlt: lib_test (wird beim nächsten Start erstellt)"
        exit 1
    fi
fi

exit 0
