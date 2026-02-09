# Projekt Dokumentation - Index

**📌 Haupteinstieg: [README.md](README.md)**

---

## 🚀 Schnellstart

| Dokument | Beschreibung |
|----------|--------------|
| [README.md](README.md) | Hauptdokumentation mit Schnellstart |
| [STARTUP-QUICK-GUIDE.md](STARTUP-QUICK-GUIDE.md) | Kurzanleitung für täglichen Start |
| [SCRIPTS-OVERVIEW.md](SCRIPTS-OVERVIEW.md) | ⭐ Übersicht aller Skripte & Aliase |

---

## 🐳 Docker & Infrastruktur

| Dokument | Beschreibung |
|----------|--------------|
| [config/shared/docker/LIB-TEST-FIX.md](config/shared/docker/LIB-TEST-FIX.md) | Warum lib_test Datenbank automatisch funktioniert |
| [config/shared/docker/MULTI-DB-SOLUTION.md](config/shared/docker/MULTI-DB-SOLUTION.md) | Multi-Datenbank Setup (jeeeraaah + lib_test) |
| [config/shared/docker/initdb/README.md](config/shared/docker/initdb/README.md) | Datenbank-Initialisierung |
| [config/DOCKER-AUTO-FIX.md](config/DOCKER-AUTO-FIX.md) | Health Check & Auto-Fix System |

---

## 🔐 Security & Keycloak

| Dokument | Beschreibung |
|----------|--------------|
| [root/lib/keycloak.admin/README.md](root/lib/keycloak.admin/README.md) | Keycloak Admin Library & Setup |
| [config/SECURITY-ARCHITEKTUR.md](config/SECURITY-ARCHITEKTUR.md) | Security Architektur |

---

## 🏗️ Architektur & Entwicklung

### Allgemein
| Dokument | Beschreibung |
|----------|--------------|
| [root/app/jeeeraaah/doc/md/architecture/requirements.md](root/app/jeeeraaah/doc/md/architecture/requirements.md) | Requirements |
| [root/app/jeeeraaah/doc/md/article - entwicklung modularer software in java/developing modular software in java.md](root/app/jeeeraaah/doc/md/article%20-%20entwicklung%20modularer%20software%20in%20java/developing%20modular%20software%20in%20java.md) | Artikel: Modulare Software in Java |

### Backend
| Dokument | Beschreibung |
|----------|--------------|
| [root/app/jeeeraaah/backend/api/ws.rs/README.md](root/app/jeeeraaah/backend/api/ws.rs/README.md) | Backend REST API Dokumentation |

### Frontend
| Dokument | Beschreibung |
|----------|--------------|
| [root/lib/fx/comp/readme.md](root/lib/fx/comp/readme.md) | JavaFX Component Framework |
| [root/lib/fx/comp/doc/fx-comp-architecture.md](root/lib/fx/comp/doc/fx-comp-architecture.md) | FX Component Architektur |

### Libraries
| Dokument | Beschreibung |
|----------|--------------|
| [root/lib/docker.health/README.md](root/lib/docker.health/README.md) | Docker Health Check Library |
| [root/lib/mp.config/README.md](root/lib/mp.config/README.md) | MicroProfile Config Integration |

---

## 📝 Datenmodell

| Dokument | Beschreibung |
|----------|--------------|
| [root/app/jeeeraaah/doc/md/datamodel/datamodel.md](root/app/jeeeraaah/doc/md/datamodel/datamodel.md) | Datenmodell Basis |
| [root/app/jeeeraaah/doc/md/datamodel/datamodel-extended.md](root/app/jeeeraaah/doc/md/datamodel/datamodel-extended.md) | Erweitertes Datenmodell |

---

## 🔧 Build & Tools

| Dokument | Beschreibung |
|----------|--------------|
| [bom/readme.md](bom/readme.md) | Bill of Materials (BOM) |
| [config/shared/scripts/readme.md](config/shared/scripts/readme.md) | Build & Setup Scripts |
| [config/QUICK-COMMANDS.md](config/QUICK-COMMANDS.md) | Häufig verwendete Befehle |

---

## 🐛 Troubleshooting

| Dokument | Beschreibung |
|----------|--------------|
| [config/TROUBLESHOOTING.md](config/TROUBLESHOOTING.md) | Allgemeine Problemlösungen |
| [root/app/jeeeraaah/backend/api/ws.rs/src/main/liberty/config/TROUBLESHOOTING-DATABASE.md](root/app/jeeeraaah/backend/api/ws.rs/src/main/liberty/config/TROUBLESHOOTING-DATABASE.md) | Datenbank Probleme |
| [INTELLIJ-PLUGIN-FIX-QUICKSTART.md](INTELLIJ-PLUGIN-FIX-QUICKSTART.md) | ⭐ IntelliJ Plugin Fehler beheben (Lombok/MapStruct) |
| [INTELLIJ-PLUGIN-FIX.md](INTELLIJ-PLUGIN-FIX.md) | Detaillierte Erklärung des IntelliJ Plugin Fixes |

---

## 📚 Spezial-Themen

### Office Dokumente
| Dokument | Beschreibung |
|----------|--------------|
| [root/sandbox/office/microsoft/word/README.md](root/sandbox/office/microsoft/word/README.md) | Word Dokument-Generierung |
| [root/sandbox/office/microsoft/word/docx4j/README.md](root/sandbox/office/microsoft/word/docx4j/README.md) | DOCX4J Implementierung |

### MapStruct & Bidirektionale Relationen
| Dokument | Beschreibung |
|----------|--------------|
| [root/lib/jpa/core.mapstruct.demo.bidirectional/bidirectional-relations-with-mapstruct.md](root/lib/jpa/core.mapstruct.demo.bidirectional/bidirectional-relations-with-mapstruct.md) | Bidirektionale Relationen mit MapStruct |

---

## 🗄️ Archivierte Dokumentation

**Hinweis:** Veraltete Dokumentation liegt in `config/archive/` und sollte nur bei historischem Interesse konsultiert werden.

Wichtige archivierte Themen:
- GraalVM Migration
- IntelliJ Setup
- JPMS Migration
- Alte Build-Probleme

---

## 📋 Maintenance

| Dokument | Beschreibung |
|----------|--------------|
| [todo.md](todo.md) | Offene Aufgaben |

---

## 💡 Tipps

### Dokumentation suchen

```bash
# Alle Markdown-Dateien finden
find . -name "*.md" -type f | grep -v node_modules | grep -v target | sort

# In Dokumentation suchen
grep -r "suchbegriff" --include="*.md" .
```

### Neue Dokumentation erstellen

1. **Allgemeine Themen** → `config/`
2. **Modul-spezifisch** → Im jeweiligen Modul-Verzeichnis
3. **Archivieren** → Nach `config/archive/` verschieben wenn veraltet

---

**Letzte Aktualisierung:** 2026-02-09
