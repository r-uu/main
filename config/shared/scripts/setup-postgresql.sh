#!/bin/bash

# ============================================================================
# PostgreSQL Setup für WSL/Ubuntu
# ============================================================================

set -e  # Exit on error

echo "=========================================="
echo "PostgreSQL Setup"
echo "=========================================="
echo ""

# Prüfe ob PostgreSQL installiert ist
if ! command -v psql &> /dev/null; then
    echo "PostgreSQL ist nicht installiert."
    echo "Installiere PostgreSQL..."
    echo ""

    sudo apt update
    sudo apt install -y postgresql postgresql-contrib

    echo "✅ PostgreSQL installiert"
else
    echo "✅ PostgreSQL ist bereits installiert: $(psql --version)"
fi

echo ""
echo "Starte PostgreSQL Service..."
sudo service postgresql start

# Warte kurz bis Service läuft
sleep 2

# Prüfe Status
echo ""
echo "PostgreSQL Status:"
sudo service postgresql status || true

echo ""
echo "=========================================="
echo "Erstelle Datenbank und User"
echo "=========================================="
echo ""

# Erstelle User (ignoriere Fehler falls schon existiert)
echo "Erstelle User r_uu..."
sudo -u postgres psql -c "CREATE USER r_uu WITH PASSWORD 'r_uu_password';" 2>/dev/null || \
sudo -u postgres psql -c "ALTER USER r_uu WITH PASSWORD 'r_uu_password';"

# Erstelle Datenbank (ignoriere Fehler falls schon existiert)
echo "Erstelle Datenbank lib_test..."
sudo -u postgres psql -c "CREATE DATABASE lib_test OWNER r_uu;" 2>/dev/null || \
echo "Datenbank lib_test existiert bereits"

# Rechte vergeben
echo "Vergebe Rechte..."
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE lib_test TO r_uu;"

# Schema-Rechte (PostgreSQL 15+)
echo "Vergebe Schema-Rechte..."
sudo -u postgres psql -d lib_test << 'EOF'
GRANT ALL ON SCHEMA public TO r_uu;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO r_uu;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO r_uu;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO r_uu;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO r_uu;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO r_uu;
EOF

echo ""
echo "=========================================="
echo "Teste Verbindung"
echo "=========================================="
echo ""

# Teste Verbindung
PGPASSWORD=r_uu_password psql -h localhost -U r_uu -d lib_test -c "SELECT version();" || {
    echo "❌ Verbindungstest fehlgeschlagen!"
    echo ""
    echo "Troubleshooting:"
    echo "1. Prüfe ob PostgreSQL läuft: sudo service postgresql status"
    echo "2. Prüfe Logs: sudo tail -20 /var/log/postgresql/postgresql-*-main.log"
    echo "3. Prüfe pg_hba.conf: sudo cat /etc/postgresql/*/main/pg_hba.conf | grep -v '^#'"
    exit 1
}

echo ""
echo "=========================================="
echo "✅ PostgreSQL Setup erfolgreich!"
echo "=========================================="
echo ""
echo "Credentials:"
echo "  Host:     localhost"
echo "  Port:     5432"
echo "  Database: lib_test"
echo "  User:     r_uu"
echo "  Password: r_uu_password"
echo ""
echo "Verbindungstest:"
echo "  PGPASSWORD=r_uu_password psql -h localhost -U r_uu -d lib_test"
echo ""

