# config - Entwicklungsumgebung Konfiguration
```
fi
    source "$PROJECT_ROOT/config/local/wsl/aliases.sh"
if [ -f "$PROJECT_ROOT/config/local/wsl/aliases.sh" ]; then
# Lade lokale Aliase (falls vorhanden)

fi
    source "$PROJECT_ROOT/config/shared/wsl/aliases.sh"
if [ -f "$PROJECT_ROOT/config/shared/wsl/aliases.sh" ]; then
# Lade gemeinsame Aliase

PROJECT_ROOT="/home/r-uu/develop/github/main"
# r-uu projekt konfiguration
```bash

Füge in deine `~/.bashrc` oder `~/.bash_profile` ein:

## Einbindung in Shell

| Setup-Dokumentation                  | `shared/docs/`    | ✅      |
| KI-API Keys                          | `local/`          | ❌      |
| AI-Prompt Libraries                  | `shared/ai-prompts/` | ✅   |
| Persönliche WSL-Aliase               | `local/wsl/`      | ❌      |
| WSL-Aliase für Projekt-Commands      | `shared/wsl/`     | ✅      |
| Wiederverwendbare Skripte            | `shared/scripts/` | ✅      |
| Umgebungsvariablen mit Secrets       | `local/docker/`   | ❌      |
| Standard Docker Compose Files        | `shared/docker/`  | ✅      |
|--------------------------------------|-------------------|---------|
| Typ                                  | Speicherort       | Im Git? |

## Best Practices

- Lokale Datenbank-Konfigurationen
- Persönliche Aliase und Shortcuts
- Maschinen-spezifische Pfade
- Passwörter, API-Keys
Änderungen in `config/local/` bleiben auf deinem Rechner:

### Lokale Konfigurationen

```
git commit -m "Update shared configuration"
git add config/shared/
```bash
Änderungen in `config/shared/` werden versioniert und mit allen Entwicklern geteilt:

### Gemeinsame Konfigurationen aktualisieren

3. Die `config/local/` Dateien werden automatisch von Git ignoriert (siehe `.gitignore`)

2. Passe die lokalen Dateien an deine Entwicklungsumgebung an

   ```
   cp config/templates/wsl-aliases.template config/local/wsl/.bash_aliases
   cp config/templates/.env.template config/local/docker/.env.local
   ```bash
1. Kopiere Templates nach `config/local/`:

### Erstmalige Einrichtung

## Verwendung

```
    └── ...
    ├── wsl-aliases.template
    ├── .env.template
└── templates/          # ✅ Vorlagen für lokale Konfigurationen (im Git)
│
│   └── ...
│   ├── docker/        # Lokale Docker Übersteuerungen (.env.local, etc.)
│   ├── wsl/           # Lokale WSL Anpassungen
├── local/              # ❌ maschinenspezifische Konfigurationen (NICHT im Git)
│
│   └── scripts/        # Gemeinsame Build/Setup Skripte
│   ├── ai-prompts/     # KI-Prompt Templates
│   ├── docker/         # Docker Compose Templates
│   ├── wsl/            # WSL Aliase, Skripte, etc.
├── shared/              # ✅ gemeinsame Konfigurationen (im Git)
config/
```

## Struktur

Dieses Verzeichnis enthält gemeinsame und maschinenspezifische Konfigurationen für die Entwicklungsumgebung.


