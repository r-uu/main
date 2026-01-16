#!/bin/bash
# Fix IntelliJ Run Configurations - Maven Delegation Problem

set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║  🔧 IntelliJ Run Configuration Fix                      ║${NC}"
echo -e "${GREEN}╚═══════════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${YELLOW}Problem:${NC}"
echo "Run Configurations schlagen fehl mit:"
echo "Cannot run program \"//wsl.localhost/Ubuntu/opt/graalvm-jdk-25/bin/java\""
echo ""

echo -e "${GREEN}Root Cause:${NC}"
echo "IntelliJ delegiert Build/Run Actions an Maven, aber das exec-plugin"
echo "kann Windows-Pfade nicht verarbeiten."
echo ""

echo -e "${GREEN}════════════════════════════════════════════════���══════════${NC}"
echo -e "${GREEN}Lösung 1: Maven Delegation deaktivieren (EMPFOHLEN)${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo ""
echo "1. Öffne IntelliJ IDEA"
echo "2. File → Settings (Strg+Alt+S)"
echo "3. Build, Execution, Deployment → Build Tools → Maven → Runner"
echo "4. DEAKTIVIERE: ☐ Delegate IDE build/run actions to Maven"
echo "5. Apply → OK"
echo "6. IntelliJ neu starten"
echo ""

echo -e "${GREEN}Dann:${NC}"
echo "- Run Configurations verwenden Java direkt (kein Maven exec)"
echo "- Schneller und zuverlässiger"
echo "- Funktioniert mit allen Java-Anwendungen"
echo ""

echo -e "${GREEN}═══════════��═══════════════════════════════════════════════${NC}"
echo -e "${GREEN}Lösung 2: Application Run Config verwenden${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo ""
echo "Bereits erstellt: InvoiceExample Run Configuration"
echo ""
echo "Verwenden:"
echo "1. Run → Edit Configurations"
echo "2. Wähle 'InvoiceExample'"
echo "3. Klicke Run (grüner Pfeil)"
echo ""

echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}Lösung 3: Maven im Terminal (funktioniert immer)${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo ""
echo "cd root/sandbox/office/microsoft/word/jasperreports"
echo "mvn exec:java"
echo ""

echo -e "${YELLOW}═══════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}Diagnose: Prüfe aktuelle Konfiguration${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════${NC}"
echo ""

# Prüfe Java
echo -n "Java Version: "
java -version 2>&1 | head -n1

# Prüfe JAVA_HOME
echo "JAVA_HOME: ${JAVA_HOME:-'❌ Nicht gesetzt'}"

# Prüfe Maven
echo -n "Maven: "
if command -v mvn &> /dev/null; then
    mvn -version 2>&1 | head -n1
else
    echo "❌ Nicht gefunden"
fi

echo ""
echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}Empfohlene Reihenfolge:${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo ""
echo "1. ✅ Maven Delegation deaktivieren (dauerhaft löst alle Probleme)"
echo "2. ✅ IntelliJ neu starten"
echo "3. ✅ Run Configuration testen"
echo ""
echo "Falls das nicht hilft:"
echo "4. File → Invalidate Caches → Invalidate and Restart"
echo "5. Schritte 1-3 wiederholen"
echo ""

echo -e "${YELLOW}Dokumentation:${NC}"
echo "- config/INTELLIJ-RUN-CONFIG-FIX.md"
echo "- config/INTELLIJ-WSL-SETUP.md"
echo ""

