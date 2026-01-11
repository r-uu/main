#!/bin/bash

# ============================================================================
# Build Warning Analyzer
# ============================================================================
# Analysiert Maven Build Output auf Warnings und potenzielle Probleme
# ============================================================================

cd /home/r-uu/develop/github/main/root

echo "=========================================="
echo "Maven Build Warning Analyse"
echo "=========================================="
echo ""

# Build ausführen und Output speichern
echo "Führe Build aus..."
mvn clean install > /tmp/build-full.log 2>&1

BUILD_STATUS=$?

echo ""
echo "=========================================="
echo "Build Status: $BUILD_STATUS"
echo "=========================================="
echo ""

if [ $BUILD_STATUS -ne 0 ]; then
    echo "❌ Build fehlgeschlagen!"
    echo ""
    echo "Letzte Fehler:"
    tail -50 /tmp/build-full.log
    exit 1
fi

echo "✅ Build erfolgreich!"
echo ""

# Analysiere Warnings
echo "=========================================="
echo "Warning-Analyse"
echo "=========================================="
echo ""

# 1. Compilation Warnings
echo "1. Compilation Warnings:"
grep -i "\[WARNING\]" /tmp/build-full.log | grep -i "compil" | head -20
echo ""

# 2. Deprecated API Warnings
echo "2. Deprecated API Usage:"
grep -i "deprecated" /tmp/build-full.log | head -10
echo ""

# 3. Module System Warnings
echo "3. JPMS/Module Warnings:"
grep -i "module" /tmp/build-full.log | grep -i "\[WARNING\]" | head -10
echo ""

# 4. Test Warnings
echo "4. Test Execution:"
grep "Tests run:" /tmp/build-full.log | tail -20
echo ""

# 5. Skipped Tests
echo "5. Übersprungene Tests:"
grep "Skipped:" /tmp/build-full.log | grep -v "Skipped: 0" | head -10
echo ""

# 6. Zusammenfassung
echo "=========================================="
echo "Zusammenfassung"
echo "=========================================="
echo ""

TOTAL_WARNINGS=$(grep -c "\[WARNING\]" /tmp/build-full.log)
COMPILATION_WARNINGS=$(grep -i "\[WARNING\]" /tmp/build-full.log | grep -ic "compil")
MODULE_WARNINGS=$(grep -i "module" /tmp/build-full.log | grep -ic "\[WARNING\]")
DEPRECATED_WARNINGS=$(grep -ic "deprecated" /tmp/build-full.log)

echo "Total Warnings: $TOTAL_WARNINGS"
echo "  - Compilation: $COMPILATION_WARNINGS"
echo "  - Module/JPMS: $MODULE_WARNINGS"
echo "  - Deprecated: $DEPRECATED_WARNINGS"
echo ""

# Tests
TOTAL_TESTS=$(grep "Tests run:" /tmp/build-full.log | tail -1)
echo "Tests: $TOTAL_TESTS"
echo ""

echo "Vollständiger Log: /tmp/build-full.log"

