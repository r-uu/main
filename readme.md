# main

single repo for r-uu projects on github, the repo contains

- a maven module that serves as common bill of materials for other r-uu maven modules: **bom**
- a maven pom module that is the parent for several r-uu maven modules: **root**
- development environment configuration and scripts: **config**

## 📁 Struktur

```
main/
├── bom/              # Maven Bill of Materials
│   └── pom.xml      # Dependency & Plugin Versionen
│
├── root/             # Maven Parent POM mit Child-Modulen
│   ├── pom.xml      # Parent für alle Module
│   ├── lib/         # Library Module
│   └── app/         # Application Module (geplant)
│
└── config/          # Entwicklungsumgebung Konfiguration
    ├── shared/      # Gemeinsame Konfigurationen (versioniert)
    │   ├── wsl/    # Bash Aliase (100+ ruu-* Aliase)
    │   ├── docker/ # Docker Compose (Postgres + Keycloak)
    │   ├── scripts/# Setup & Build-Skripte
    │   └── ai-prompts/ # AI-Prompt Bibliothek
    ├── local/       # Maschinenspezifische Konfigurationen (nicht versioniert)
    └── templates/   # Vorlagen für lokale Konfigurationen
```

## 🚀 Schnellstart

### 1. Erstmalige Einrichtung

```bash
# Setup ausführen
cd config/shared/scripts
./setup-dev-env.sh

# Lokale Konfiguration anpassen
nano config/local/docker/.env.local
# → Passwörter ändern!

# Aliase aktivieren (in ~/.bashrc einfügen)
source ~/develop/github/main/config/shared/wsl/aliases.sh
source ~/develop/github/main/config/local/wsl/aliases.sh

# Shell neu laden
source ~/.bashrc
```

### 2. Docker Services starten

```bash
# Alle Services (PostgreSQL + Keycloak)
ruu-docker-up

# Status prüfen
ruu-docker-ps

# Einzelne Services
ruu-postgres-start
ruu-keycloak-start
```

### 3. Maven Build

```bash
# Alles bauen
ruu-install

# Einzelne Module
ruu-bom-install
ruu-root-install

# Schnell (ohne Tests)
ruu-install-fast
```

## 🐳 Docker Services

### PostgreSQL 16
- Port: **5432**
- User: `ruu` (konfigurierbar)
- Database: `ruu_dev` (konfigurierbar)
- Alias: `ruu-postgres-shell` für psql CLI

### Keycloak
- Port: **8080**
- Admin Console: http://localhost:8080/admin
- Login: `admin` / `admin` (in .env.local ändern!)
- Alias: `ruu-keycloak-admin` für URL

## 📚 Dokumentation

| Datei | Beschreibung |
|-------|--------------|
| [config/INDEX.md](config/INDEX.md) | 📋 Gesamtübersicht - **START HIER** |
| [config/QUICKSTART.md](config/QUICKSTART.md) | 🚀 Schnellstart (5 Minuten) |
| [config/MIGRATION.md](config/MIGRATION.md) | 🔄 Migration von space-02 |
| [config/shared/ai-prompts/](config/shared/ai-prompts/) | 🤖 AI-Prompt Bibliothek |
| [bom/readme.md](bom/readme.md) | 📦 BOM Dokumentation |
| [root/readme.md](root/readme.md) | 📦 Root POM Dokumentation |

## 🛠️ Nützliche Aliase

```bash
# Hilfe
ruu-help              # Alle Aliase anzeigen
ruu-help-docker       # Docker-Aliase
ruu-help-maven        # Maven-Aliase

# Navigation
cdruu                 # → /home/r-uu/develop/github/main
cdbom, cdroot, cdlib  # → Zu den Modulen springen

# Docker
ruu-docker-up         # Alle Services starten
ruu-postgres-logs     # PostgreSQL Logs
ruu-keycloak-admin    # Keycloak Admin URL

# Maven
ruu-install           # Clean Install (alle Module)
ruu-install-fast      # Ohne Tests

# Git
ruu-status            # Git Status
ruu-log               # Git Log (Graph)
```

Siehe [config/shared/wsl/aliases.sh](config/shared/wsl/aliases.sh) für alle 100+ Aliase!

## 🔧 Maven Architektur

### BOM (Bill of Materials)
- ❌ Kein Parent
- ❌ Keine Children
- ✅ Nur `<dependencyManagement>` und `<pluginManagement>`
- ✅ Wiederverwendbar über Projekt-Grenzen

### Root (Parent POM)
- ❌ Kein Parent
- ✅ Importiert BOM via `<scope>import</scope>`
- ✅ Children: lib, app
- ✅ Vererbt Konfiguration an Children

Siehe [config/shared/ai-prompts/maven/bom-vs-parent.md](config/shared/ai-prompts/maven/bom-vs-parent.md) für Details.

## 💡 Best Practices

1. **Niemals Secrets committen**
   - Passwörter nur in `config/local/docker/.env.local`
   - Diese Datei ist in .gitignore

2. **Aliase verwenden**
   - Alle Befehle haben `ruu-` Prefix
   - Schneller und konsistenter

3. **Dokumentation lesen**
   - Start mit `config/INDEX.md`
   - Jedes Modul hat eigene README

4. **Setup-Skript nutzen**
   - Automatisiert lokale Einrichtung
   - Kopiert Templates

## 🆘 Hilfe

```bash
# Dokumentation anzeigen
ruu-docs

# Alle Aliase anzeigen
ruu-help

# Docker Status
ruu-docker-ps

# PostgreSQL Shell
ruu-postgres-shell
```

Bei Fragen: Siehe [config/INDEX.md](config/INDEX.md) für vollständige Dokumentation.

---

**Status:** Migration Phase 1 abgeschlossen ✅  
**Datum:** 2026-01-11  
**Migriert von:** space-02 → main
