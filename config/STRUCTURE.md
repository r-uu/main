# config/ - Verzeichnisstruktur

```
config/
│
├── QUICKSTART.md                          # 🚀 Schnellstart-Anleitung
├── readme.md                              # 📖 Vollständige Dokumentation
│
├── shared/                                # ✅ IM GIT - Gemeinsame Konfigurationen
│   ├── wsl/
│   │   └── aliases.sh                    # Gemeinsame WSL/Bash Aliase
│   │
│   ├── docker/
│   │   ├── docker-compose.yml            # Docker Services (Postgres, etc.)
│   │   └── .env                          # Standard Umgebungsvariablen (OHNE Secrets!)
│   │
│   ├── scripts/
│   │   └── setup-dev-env.sh              # Setup-Skript für neue Entwickler
│   │
│   └── ai-prompts/                       # KI-Prompt Bibliothek
│       ├── readme.md
│       ├── maven/
│       │   └── multi-module-setup.md     # Maven Multi-Module Prompts
│       ├── code-review/
│       ├── documentation/
│       ├── refactoring/
│       └── testing/
│
├── local/                                 # ❌ NICHT IM GIT - Maschinenspezifisch
│   │                                      # (wird durch setup-dev-env.sh erstellt)
│   ├── wsl/
│   │   └── aliases.sh                    # Persönliche Aliase
│   │
│   └── docker/
│       ├── .env.local                    # Lokale Overrides MIT Secrets
│       ├── data/                         # Postgres Daten
│       │   └── postgres/
│       └── backups/                      # Database Backups
│
└── templates/                             # ✅ IM GIT - Vorlagen
    ├── .env.template                     # Template für .env.local
    └── wsl-aliases.template              # Template für lokale Aliase
```

## Legende

| Symbol | Bedeutung                          |
|--------|------------------------------------|
| ✅     | Wird ins Git committed             |
| ❌     | Wird NICHT ins Git committed       |
| 🚀     | Schnellstart/Wichtig              |
| 📖     | Dokumentation                      |

## Git-Status der Verzeichnisse

### Versioniert (im Git)
- `config/shared/` - alle gemeinsamen Konfigurationen
- `config/templates/` - Vorlagen für lokale Configs
- `config/*.md` - Dokumentation

### Ignoriert (nicht im Git)
- `config/local/` - komplett ignoriert
- `config/**/.env.local` - alle .env.local Dateien
- Siehe `.gitignore` für Details

## Verwendungsmuster

### 1. Gemeinsame Konfiguration hinzufügen
```bash
# Neue gemeinsame Konfiguration erstellen
echo "alias my-alias='command'" >> config/shared/wsl/aliases.sh

# Committen und pushen
git add config/shared/
git commit -m "Add new shared alias"
git push
```

### 2. Lokale Konfiguration anpassen
```bash
# Lokale Datei bearbeiten (wird NICHT committed)
nano config/local/wsl/aliases.sh
```

### 3. Neues Template erstellen
```bash
# Template erstellen
cat > config/templates/my-config.template << 'EOF'
# Meine Konfiguration
KEY=VALUE
EOF

# Template committen
git add config/templates/my-config.template
git commit -m "Add my-config template"
```

### 4. Template zu lokal kopieren
```bash
cp config/templates/my-config.template config/local/my-config
# Dann config/local/my-config anpassen
```

