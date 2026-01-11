#!/bin/bash

# Test Maven Resource Filtering
# Prüft ob Maven die Properties korrekt ersetzt

echo "======================================"
echo "Maven Resource Filtering Test"
echo "======================================"
echo ""

MODULE="root/lib/jpa/se.hibernate.postgres.demo"
cd /home/r-uu/develop/github/main/$MODULE

echo "1. Clean build..."
mvn clean compile test-compile

echo ""
echo "2. Prüfe gefilterte microprofile-config.properties..."
FILTERED_FILE="target/test-classes/META-INF/microprofile-config.properties"

if [ -f "$FILTERED_FILE" ]; then
    echo "✅ Datei existiert: $FILTERED_FILE"
    echo ""
    echo "Inhalt:"
    cat "$FILTERED_FILE"
    echo ""
    echo "======================================"
    echo "Prüfe Property-Ersetzung:"
    echo "======================================"

    if grep -q '${db.host}' "$FILTERED_FILE"; then
        echo "❌ FEHLER: ${db.host} wurde NICHT ersetzt!"
    else
        echo "✅ ${db.host} wurde ersetzt"
    fi

    if grep -q '${db.port}' "$FILTERED_FILE"; then
        echo "❌ FEHLER: ${db.port} wurde NICHT ersetzt!"
    else
        echo "✅ ${db.port} wurde ersetzt"
    fi

    echo ""
    echo "database.host=$(grep 'database.host=' "$FILTERED_FILE" || echo 'NICHT GEFUNDEN')"
    echo "database.port=$(grep 'database.port=' "$FILTERED_FILE" || echo 'NICHT GEFUNDEN')"
else
    echo "❌ FEHLER: Datei nicht gefunden: $FILTERED_FILE"
fi

