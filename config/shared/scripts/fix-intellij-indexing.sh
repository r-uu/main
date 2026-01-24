#!/bin/bash
################################################################################
# IntelliJ Indexierung Fix
#
# Dieses Skript behebt Indexierungsprobleme in IntelliJ IDEA
# durch Neuaufbau des Maven-Projekts und Invalidierung der IntelliJ-Caches.
#
# Verwendung:
#   ./fix-intellij-indexing.sh
#
# Alternative: Manuell in IntelliJ IDEA:
#   1. File -> Invalidate Caches... -> Invalidate and Restart
#   2. Nach Neustart: Maven -> Reload All Maven Projects (Ctrl+Shift+O)
################################################################################

set -e  # Exit on error

echo "🔧 IntelliJ Indexierung wird repariert..."
echo ""

# Schritt 1: Maven Clean Install
echo "📦 Schritt 1/2: Maven Clean Install wird ausgeführt..."
cd /home/r-uu/develop/github/main/root
mvn clean install -DskipTests

echo ""
echo "✅ Maven Build erfolgreich abgeschlossen"
echo ""
echo "📋 Schritt 2/2: IntelliJ Cache manuell invalidieren"
echo ""
echo "Bitte führe in IntelliJ IDEA folgende Schritte aus:"
echo "  1. File → Invalidate Caches..."
echo "  2. Wähle: 'Invalidate and Restart'"
echo "  3. Nach Neustart: Maven → Reload All Maven Projects"
echo ""
echo "Alternative (schneller):"
echo "  1. Maven → Reload All Maven Projects (Strg+Shift+O)"
echo "  2. Build → Rebuild Project"
echo ""
