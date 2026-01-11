#!/bin/bash
# Beispiel-Daten für Entwicklung
# Dieses Skript lädt initiale Testdaten

set -e

echo "[init-data] ═══════════════════════════════════════════════════════════════"
echo "[init-data] Lade Beispiel-Daten..."
echo "[init-data] ═══════════════════════════════════════════════════════════════"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Beispiel: Demo-Tabelle erstellen
    -- CREATE TABLE IF NOT EXISTS demo (
    --     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    --     name VARCHAR(255) NOT NULL,
    --     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    -- );

    -- Beispiel: Demo-Daten einfügen
    -- INSERT INTO demo (name) VALUES
    --     ('Demo Entry 1'),
    --     ('Demo Entry 2'),
    --     ('Demo Entry 3');

    -- Hinweis: Entferne die Kommentare und passe an deine Bedürfnisse an
EOSQL

echo "[init-data] ✓ Beispiel-Daten geladen (aktuell deaktiviert)"
echo "[init-data] ═══════════════════════════════════════════════════════════════"

