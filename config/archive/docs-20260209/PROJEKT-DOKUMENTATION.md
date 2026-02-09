# 📚 PROJEKT DOKUMENTATION - KONSOLIDIERT

**Letzte Aktualisierung:** 2026-01-22  
**Status:** ✅ Aufgeräumt & Konsolidiert

---

## 🎯 HAUPTDOKUMENTATION

### ✅ Für den täglichen Gebrauch:

| Dokument | Beschreibung |
|----------|--------------|
| **[README.md](../README.md)** | 🏠 Hauptdokumentation - Start hier! |
| **[config/README.md](README.md)** | ⚙️ Konfiguration & Setup |
| **[todo.md](../todo.md)** | 📝 Projektaufgaben |

---

## 📂 WICHTIGE VERZEICHNISSE

### Config-Struktur

```
config/
├── README.md                           # Diese Dokumentation
├── AUTOMATIC-MODULES-DOCUMENTATION.md  # JPMS & Automatic Modules
├── CONFIG-PROPERTIES-GUIDE.md          # Properties-Konfiguration
├── DOCKER-SETUP.md                     # Docker Container Setup
├── KEYCLOAK-SETUP.md                   # Keycloak Konfiguration
├── POSTGRESQL-SETUP.md                 # PostgreSQL Setup
├── STRUCTURE.md                        # Projektstruktur
├── TROUBLESHOOTING.md                  # Problemlösungen
├── WSL-AUTO-STARTUP.md                 # WSL Autostart
│
├── shared/                             # ✅ IM GIT
│   ├── wsl/
│   │   ├── aliases.sh                  # Bash-Aliase
│   │   └── startup.sh                  # Docker-Startup
│   ├── docker/
│   │   └── docker-compose.yml          # Docker Services
│   └── scripts/
│       └── *.sh                        # Setup-Skripte
│
├── local/                              # ❌ NICHT IM GIT  
│   └── (maschinenspezifische Config)
│
└── archive/                            # 📦 Historische Dokumente
    └── *.md                            # Falls benötigt
```

---

## 🚀 QUICK START

### Neu in diesem Projekt?

```bash
# 1. Projekt klonen (bereits erledigt ✓)

# 2. Docker Services starten
ruu-startup

# 3. Projekt bauen
build-all

# 4. Backend starten (in IntelliJ)
# → Run Configuration: "backend"

# 5. Frontend starten (in IntelliJ)  
# → Run Configuration: "DashAppRunner"
```

---

## 🛠️ WICHTIGE BEFEHLE (Aliase)

### Build

```bash
build-all              # Komplettes Projekt bauen
ruu-build              # Alias für build-all
```

### Docker

```bash
ruu-startup            # Alle Docker Services starten & prüfen
ruu-docker-ps          # Container-Status anzeigen
ruu-docker-restart     # Alle Container neu starten
```

### Keycloak

```bash
ruu-keycloak-setup     # Keycloak Realm automatisch einrichten
ruu-keycloak-logs      # Keycloak Container Logs
```

### PostgreSQL

```bash
ruu-postgres-setup     # Datenbanken erstellen
```

---

## 📖 DETAILLIERTE DOKUMENTATION

### Technische Themen

| Dokument | Wann lesen? |
|----------|------------|
| [AUTOMATIC-MODULES-DOCUMENTATION.md](AUTOMATIC-MODULES-DOCUMENTATION.md) | JPMS Warnings verstehen |
| [CONFIG-PROPERTIES-GUIDE.md](CONFIG-PROPERTIES-GUIDE.md) | Properties konfigurieren |
| [POSTGRESQL-SETUP.md](POSTGRESQL-SETUP.md) | Datenbank-Probleme |
| [DOCKER-SETUP.md](DOCKER-SETUP.md) | Docker Container Probleme |
| [KEYCLOAK-SETUP.md](KEYCLOAK-SETUP.md) | Keycloak Probleme |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | Allgemeine Probleme |

### Spezialthemen

| Modul | Dokumentation |
|-------|--------------|
| **JasperReports** | [root/sandbox/office/microsoft/word/README.md](../root/sandbox/office/microsoft/word/README.md) |
| **DocX4J** | [root/sandbox/office/microsoft/word/docx4j/README.md](../root/sandbox/office/microsoft/word/docx4j/README.md) |

---

## 🔍 PROJEKT-STATUS

| Aspekt | Status |
|--------|--------|
| **Build** | ✅ SUCCESS |
| **Tests** | ✅ Laufen |
| **JPMS** | ✅ Vollständig (47 Module) |
| **Docker** | ✅ Automatisiert |
| **Dokumentation** | ✅ Konsolidiert |

---

## 📦 ARCHIV

Alle alten Status-Meldungen, Fix-Dokumente und historischen Informationen wurden nach **config/archive/** verschoben.

Diese sind bei Bedarf verfügbar, werden aber nicht mehr für den normalen Betrieb benötigt.

---

## ❓ HÄUFIGE FRAGEN

### Wo finde ich...?

**...die Hauptdokumentation?**  
→ [../README.md](../README.md)

**...Build-Anweisungen?**  
→ [../README.md](../README.md) Abschnitt "Schnellstart"

**...Docker Container starten?**  
→ `ruu-startup` Alias

**...Keycloak einrichten?**  
→ `ruu-keycloak-setup` Alias (vollautomatisch!)

**...PostgreSQL Datenbanken?**  
→ `ruu-postgres-setup` Alias

**...IntelliJ Konfiguration?**  
→ Projekt öffnen, Maven Sync, fertig!

---

## 🎯 ENTWICKLUNGS-WORKFLOW

```bash
# Morgen:
ruu-startup              # Docker Services starten

# Code ändern...

build-all                # Testen

# Backend starten (IntelliJ)
# Frontend starten (IntelliJ)

# Abend:
# Docker Container laufen weiter (kein Stop nötig)
```

---

## 📞 SUPPORT

Bei Problemen:

1. **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** prüfen
2. **[config/archive/](archive/)** für historische Lösungen durchsuchen
3. Docker Container neu starten: `ruu-docker-restart`
4. Im Zweifelsfall: Kompletter Neustart mit `ruu-startup`

---

**🎉 Projekt ist bereit für produktive Entwicklung!**
