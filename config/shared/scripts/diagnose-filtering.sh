#!/bin/bash

# ============================================================================
# Diagnose - Warum werden Properties nicht ersetzt?
# ============================================================================

set -e

echo "========================================"
echo "Maven Resource Filtering Diagnose"
echo "========================================"
echo ""

cd /home/r-uu/develop/github/main

echo "1. Prüfe config.properties..."
if [ -f "config.properties" ]; then
    echo "✅ config.properties existiert"
    echo ""
    echo "Relevante Properties:"
    grep "^db\." config.properties | head -5
else
    echo "❌ config.properties NICHT gefunden!"
    exit 1
fi

echo ""
echo "2. Baue JDBC Postgres Modul..."
cd root/lib/jdbc/postgres
mvn clean test-compile -q

echo ""
echo "3. Prüfe gefilterte microprofile-config.properties..."
FILTERED="target/test-classes/META-INF/microprofile-config.properties"

if [ -f "$FILTERED" ]; then
    echo "✅ Gefilterte Datei existiert"
    echo ""
    echo "Inhalt:"
    cat "$FILTERED"
    echo ""
    echo "========================================"
    echo "Prüfe Ersetzung:"
    echo "========================================"

    if grep -q '\${db\.host}' "$FILTERED"; then
        echo "❌ FEHLER: \${db.host} wurde NICHT ersetzt!"
        echo ""
        echo "URSACHE: Maven Resource Filtering funktioniert nicht!"
        echo ""
        echo "Mögliche Gründe:"
        echo "1. properties-maven-plugin läuft nicht in 'initialize' Phase"
        echo "2. Resource Filtering ist nicht aktiviert"
        echo "3. Build wurde nicht vom Root-Verzeichnis gestartet"
        echo ""
        echo "LÖSUNG:"
        echo "Baue vom Hauptverzeichnis:"
        echo "  cd /home/r-uu/develop/github/main"
        echo "  mvn -f root/pom.xml clean test"
    else
        echo "✅ Properties wurden ersetzt!"
        echo ""
        grep "database.host=" "$FILTERED"
        grep "database.port=" "$FILTERED"
    fi
else
    echo "❌ Gefilterte Datei nicht gefunden!"
fi

echo ""
echo "========================================"
echo "Diagnose abgeschlossen"
echo "========================================"

