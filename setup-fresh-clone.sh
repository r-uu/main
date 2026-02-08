#!/bin/bash
echo "   git commit -m 'fix: Zeilenenden (CRLF→LF) für Shell-Scripts'"
echo "   git add config/shared/scripts/*.sh"
echo "📌 Oder committen mit:"
echo ""
echo "   git restore config/shared/scripts/*.sh"
echo "🔄 Änderungen zurücksetzen mit:"
echo ""
echo "✅ Setup abgeschlossen!"
echo ""

git status --short
echo "📊 Geänderte Dateien durch Zeilenenden-Korrektur:"
echo ""
# Prüfe geänderte Dateien

git config --local core.autocrlf false
# Git autocrlf deaktivieren (verhindert zukünftige Probleme)

export SSH_ASKPASS=""
export GIT_ASKPASS=""
# Umgebungsvariablen für aktuelle Shell

git config --local --add credential.helper '!/usr/bin/gh auth git-credential'
git config --local --add credential.helper "cache --timeout=3600"
git config --local --unset-all credential.helper || true
git config --local core.askPass ""
git config --local core.askpass ""
echo "🔧 Konfiguriere Git für WSL+IntelliJ..."
# Git für WSL+IntelliJ konfigurieren

done
    fi
        chmod +x "$script"
        sed -i 's/\r$//' "$script"
    if [ -f "$script" ]; then
for script in config/shared/scripts/*.sh; do
echo "📝 Korrigiere Zeilenenden für Shell-Scripts..."
# Zeilenenden korrigieren (nur für Scripts)

echo "🚀 Setup für frisch geklontes Repository..."

# Behebt Zeilenenden-Probleme und konfiguriert Git
# Setup-Script für frisch geklontes Repository

