#!/bin/bash
# Installation von GraalVM 25 für WSL Ubuntu
# Dieses Skript ersetzt das existierende JDK durch GraalVM 25

set -e

echo "🔥 GraalVM 25 Installation"
echo "=========================="
echo ""

# Konfiguration
GRAALVM_VERSION="25"
GRAALVM_JAVA_VERSION="25"
GRAALVM_BUILD="13"
INSTALL_DIR="/opt"
GRAALVM_HOME="$INSTALL_DIR/graalvm-jdk-$GRAALVM_VERSION"

# Temporäres Verzeichnis
TMP_DIR="/tmp/graalvm-install"
mkdir -p "$TMP_DIR"

echo "📋 Konfiguration:"
echo "  Version: GraalVM for JDK $GRAALVM_VERSION"
echo "  Installationsverzeichnis: $GRAALVM_HOME"
echo ""

# Prüfe ob altes JDK existiert
echo "🔍 Prüfe existierende Java-Installation..."
if command -v java &> /dev/null; then
    echo "  Aktuell installiert:"
    java --version | head -n 1
    echo ""
fi

# Prüfe ob GraalVM bereits installiert ist
if [ -d "$GRAALVM_HOME" ]; then
    echo "⚠️  GraalVM ist bereits installiert unter $GRAALVM_HOME"
    read -p "Möchten Sie fortfahren und neu installieren? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "❌ Installation abgebrochen"
        exit 1
    fi
    echo "🗑️  Entferne existierende Installation..."
    sudo rm -rf "$GRAALVM_HOME"
fi

# Download GraalVM
echo "📥 Download GraalVM JDK $GRAALVM_VERSION..."
DOWNLOAD_URL="https://download.oracle.com/graalvm/$GRAALVM_VERSION/latest/graalvm-jdk-${GRAALVM_VERSION}_linux-x64_bin.tar.gz"
ARCHIVE="$TMP_DIR/graalvm-jdk-$GRAALVM_VERSION.tar.gz"

echo "  URL: $DOWNLOAD_URL"
if [ -f "$ARCHIVE" ]; then
    echo "  ⊘ Archiv bereits heruntergeladen"
else
    wget -O "$ARCHIVE" "$DOWNLOAD_URL" || {
        echo "❌ Download fehlgeschlagen"
        echo "Versuchen Sie manuell: $DOWNLOAD_URL"
        exit 1
    }
    echo "  ✓ Download abgeschlossen"
fi

# Entpacken
echo ""
echo "📦 Entpacke GraalVM..."
cd "$TMP_DIR"
tar -xzf "$ARCHIVE"

# Finde entpacktes Verzeichnis (kann graalvm-jdk-25.0.1+12.1 oder ähnlich heißen)
EXTRACTED_DIR=$(find "$TMP_DIR" -maxdepth 1 -type d -name "graalvm-jdk-*" ! -name "*.tar.gz" | head -n 1)

if [ -z "$EXTRACTED_DIR" ]; then
    echo "❌ Entpacktes Verzeichnis nicht gefunden"
    exit 1
fi

echo "  Gefundenes Verzeichnis: $EXTRACTED_DIR"

# Installation
echo ""
echo "📂 Installiere GraalVM nach $GRAALVM_HOME..."
sudo mkdir -p "$INSTALL_DIR"
sudo mv "$EXTRACTED_DIR" "$GRAALVM_HOME"
sudo chown -R root:root "$GRAALVM_HOME"
echo "  ✓ Installation abgeschlossen"

# Entferne alte JDK-Alternativen (optional)
echo ""
echo "🔧 Konfiguriere Java-Alternativen..."
if command -v update-alternatives &> /dev/null; then
    # Entferne alte Java-Einträge
    sudo update-alternatives --remove-all java 2>/dev/null || true
    sudo update-alternatives --remove-all javac 2>/dev/null || true

    # Registriere GraalVM
    sudo update-alternatives --install /usr/bin/java java "$GRAALVM_HOME/bin/java" 1100
    sudo update-alternatives --install /usr/bin/javac javac "$GRAALVM_HOME/bin/javac" 1100

    # Setze GraalVM als Standard
    sudo update-alternatives --set java "$GRAALVM_HOME/bin/java"
    sudo update-alternatives --set javac "$GRAALVM_HOME/bin/javac"
    echo "  ✓ GraalVM als Standard-JDK konfiguriert"
else
    echo "  ⊘ update-alternatives nicht verfügbar (übersprungen)"
fi

# Aufräumen
echo ""
echo "🧹 Räume temporäre Dateien auf..."
rm -rf "$TMP_DIR"
echo "  ✓ Aufräumen abgeschlossen"

# Verifizierung
echo ""
echo "✅ Installation erfolgreich!"
echo ""
echo "📊 Installierte Version:"
"$GRAALVM_HOME/bin/java" --version
echo ""

# Prüfe Native Image
if [ -f "$GRAALVM_HOME/bin/native-image" ]; then
    echo "  ✓ Native Image verfügbar"
else
    echo "  ℹ️  Native Image ist nicht enthalten"
    echo "     Installieren Sie es mit: gu install native-image"
fi

echo ""
echo "📝 Nächste Schritte:"
echo "  1. Laden Sie Ihre Shell neu:"
echo "     source ~/.bashrc"
echo "  2. Überprüfen Sie die Installation:"
echo "     ruu-graalvm-version"
echo "  3. (Optional) Installieren Sie Native Image:"
echo "     sudo $GRAALVM_HOME/bin/gu install native-image"
echo ""
echo "💡 JAVA_HOME ist bereits in config/shared/wsl/aliases.sh konfiguriert:"
echo "   export JAVA_HOME=$GRAALVM_HOME"
echo ""

