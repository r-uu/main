# config - Entwicklungsumgebung Konfiguration
> 🔥 **Neu:** [INTELLIJ-WSL-SETUP.md](INTELLIJ-WSL-SETUP.md) - IntelliJ IDEA mit WSL und GraalVM 25  
> 🔧 **Problem gelöst:** [JAVA-HOME-FIX.md](JAVA-HOME-FIX.md) - "JAVA_HOME not defined" Fehler behoben
## 📋 Übersicht
Dieses Verzeichnis enthält gemeinsame und maschinenspezifische Konfigurationen für die Entwicklungsumgebung.
### Wichtigste Dokumentationen
| Datei | Beschreibung |
|-------|--------------|
| [INDEX.md](INDEX.md) | Vollständige Übersicht aller Konfigurationen |
| [QUICKSTART.md](QUICKSTART.md) | Schnellstart für neue Entwickler |
| [INTELLIJ-WSL-SETUP.md](INTELLIJ-WSL-SETUP.md) | IntelliJ IDEA mit WSL konfigurieren |
| [JAVA-HOME-FIX.md](JAVA-HOME-FIX.md) | JAVA_HOME Problem-Lösung |
| [GRAALVM-INSTALLATION.md](GRAALVM-INSTALLATION.md) | GraalVM 25 Installation |
| [GRAALVM-25-MIGRATION.md](GRAALVM-25-MIGRATION.md) | Migration zu GraalVM 25 |
## 🚀 Quick Start
### 1. Neu im Projekt?
➡️ Folge dem [QUICKSTART.md](QUICKSTART.md)
### 2. IntelliJ IDEA Setup Problem?
➡️ Folge dem [INTELLIJ-WSL-SETUP.md](INTELLIJ-WSL-SETUP.md)
**Häufigster Fehler:**
```
The JAVA_HOME environment variable is not defined correctly
```
**Lösung:** [JAVA-HOME-FIX.md](JAVA-HOME-FIX.md)
### 3. GraalVM installieren/aktualisieren?
```bash
cd ~/develop/github/main/config/shared/scripts
sudo ./install-graalvm.sh
source ~/.bashrc
ruu-graalvm-version
```
➡️ Dokumentation: [GRAALVM-INSTALLATION.md](GRAALVM-INSTALLATION.md)
## 📁 Struktur
```
config/
├── shared/              # ✅ gemeinsame Konfigurationen (im Git)
│   ├── wsl/            # WSL Aliase, Skripte
│   ├── docker/         # Docker Compose Templates
│   ├── scripts/        # Gemeinsame Build/Setup Skripte
│   └── ai-prompts/     # KI-Prompt Templates
├── local/              # ❌ maschinenspezifische Konfigurationen (NICHT im Git)
│   ├── wsl/           # Lokale WSL Anpassungen
│   └── docker/        # Lokale Docker Übersteuerungen (.env.local)
├── templates/          # ✅ Vorlagen für lokale Konfigurationen (im Git)
│   ├── .env.template
│   └── wsl-aliases.template
└── *.md               # Dokumentation
```
## 🔧 Verwendung
### Gemeinsame Konfigurationen aktualisieren
```bash
# Änderungen in config/shared/ werden mit allen Entwicklern geteilt
git add config/shared/
git commit -m "Update shared configuration"
git push
```
### Lokale Konfigurationen
```bash
# Änderungen in config/local/ bleiben auf deinem Rechner
# Diese Dateien werden automatisch von Git ignoriert
nano config/local/wsl/aliases.sh
source config/local/wsl/aliases.sh
```
## 🛠️ Shell Integration
Füge in deine `~/.bashrc` ein:
```bash
# r-uu projekt konfiguration
PROJECT_ROOT="/home/r-uu/develop/github/main"
# Lade gemeinsame Aliase
if [ -f "$PROJECT_ROOT/config/shared/wsl/aliases.sh" ]; then
    source "$PROJECT_ROOT/config/shared/wsl/aliases.sh"
fi
# Lade lokale Aliase (falls vorhanden)
if [ -f "$PROJECT_ROOT/config/local/wsl/aliases.sh" ]; then
    source "$PROJECT_ROOT/config/local/wsl/aliases.sh"
fi
```
Dann:
```bash
source ~/.bashrc
```
## 📚 Weiterführende Dokumentation
Siehe [INDEX.md](INDEX.md) für die vollständige Übersicht aller Konfigurationen und Dokumentationen.
