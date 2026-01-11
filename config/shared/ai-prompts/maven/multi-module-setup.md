# AI-Prompt: Maven Multi-Module Setup

## Kontext
r-uu Projekt mit Maven Multi-Module Struktur:
- BOM (Bill of Materials) - keine Parent/Child Beziehungen
- Root Module (Parent für lib und app Module)
- Lib Modules - Bibliotheken
- App Modules - Anwendungen

## Prompt Template

```
Ich arbeite an einem Maven Multi-Module Projekt mit folgender Struktur:

- bom/ - Bill of Materials (keine Parent/Child Beziehungen)
- root/ - Parent POM für alle anderen Module
  - lib/ - Library Module
  - app/ - Application Module

Fragen:
1. [DEINE SPEZIFISCHE FRAGE]

Kontext:
- BOM sollte Dependency Management, Plugin Management und Properties definieren
- Root sollte die gemeinsame Parent-Konfiguration für lib und app bereitstellen
- Alle Module sollten das BOM über <dependencyManagement> importieren

Bitte berücksichtige Maven Best Practices für große Multi-Module Projekte.
```

## Typische Fragen

### Dependency Management
- Soll root das BOM als Parent haben?
- Wie importiere ich das BOM in Child-Modulen?
- Wie vermeide ich Versionskonflikte?

### Plugin Konfiguration
- Wo konfiguriere ich Maven Plugins?
- Wie vererbe ich Plugin-Konfiguration an Children?
- Wie überschreibe ich Plugin-Settings in Submodulen?

### Build-Reihenfolge
- Welches Modul sollte zuerst gebaut werden?
- Wie definiere ich die Build-Reihenfolge?
- Wie gehe ich mit zirkulären Abhängigkeiten um?

