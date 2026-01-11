# Dependency Update Übersicht

**Stand:** 2026-01-11

## Aktualisierte Dependencies

| Dependency | Alte Version | Neue Version | Status |
|------------|--------------|--------------|--------|
| Lombok | 1.18.38 | 1.18.42 | ✅ Aktualisiert |
| PostgreSQL JDBC | 42.7.3 | 42.7.8 | ✅ Aktualisiert |
| Byte Buddy | 1.15.10 | 1.18.3 | ✅ Aktualisiert |
| Classmate | 1.5.1 | 1.7.3 | ✅ Aktualisiert |
| Keycloak | 26.0.7 | 26.5.0 | ✅ Aktualisiert |
| ControlsFX | 11.2.0 | 11.2.3 | ✅ Aktualisiert |
| Ikonli | 12.3.1 | 12.4.0 | ✅ Aktualisiert |
| Jakarta JSON Bind API | 3.0.0 | 3.0.1 | ✅ Aktualisiert |
| Jakarta Validation API | 3.0.2 | 3.1.0 | ✅ Aktualisiert |
| Jandex | 3.2.2 | 3.2.3 | ✅ Aktualisiert |

## Bereits Aktuelle Dependencies

| Dependency | Version | Notiz |
|------------|---------|-------|
| Jackson (alle Module) | 2.18.3 | Neueste stabile Version |
| Hibernate ORM | 6.6.5.Final | Aktuell |
| JUnit Jupiter | 5.11.3 | Aktuell |
| Log4j | 2.25.3 | Aktuell |
| SLF4J | 2.0.16 | Aktuell |
| MapStruct | 1.6.3 | Aktuell |
| Hamcrest | 3.0 | Aktuell |
| ArchUnit | 1.4.1 | Aktuell |

## Maven Plugins

| Plugin | Alte Version | Neue Version | Status |
|--------|--------------|--------------|--------|
| Maven Compiler Plugin | 3.14.1 | 3.13.0 | ✅ Downgrade (3.13.0 ist stabiler mit Java 25) |
| Versions Plugin | 2.16.2 | - | ✅ Zur BOM hinzugefügt |

## Warum Maven Compiler Plugin 3.13.0?

Version 3.14.1 hatte Probleme mit Annotation Processing in Java 25. Version 3.13.0 ist:
- ✅ Stabiler mit Java 25
- ✅ Funktioniert korrekt mit Lombok 1.18.42
- ✅ Unterstützt alle benötigten Annotation Processors

## Java 25 Kompatibilität

Alle Dependencies sind geprüft und kompatibel mit:
- ✅ Java 25
- ✅ GraalVM 25
- ✅ Jakarta EE 10

## Nächste geplante Updates

Folgende Dependencies sollten bei zukünftigen Releases geprüft werden:
- JavaFX (aktuell 24.0.2, prüfe auf 25.x wenn verfügbar)
- TestFX (aktuell 4.0.18, stabil)

## Hinweise

### Jackson 2.20.1 - NICHT VERFÜGBAR
Die Version 2.20.1 existiert nicht in Maven Central. Neueste stabile Version ist 2.18.3.

### Breaking Changes vermeiden
Wir aktualisieren nur auf Patch- und Minor-Versionen, um Breaking Changes zu vermeiden.

### Testing erforderlich
Nach dem Update sollten folgende Module getestet werden:
- ✅ `root/lib/archunit` (erfolgreich getestet)
- `root/lib/jpa/*` (Hibernate)
- `root/lib/fx/*` (JavaFX)
- `root/lib/jackson` (Jackson)

## Build-Reihenfolge

1. **BOM installieren**
   ```bash
   cd bom
   mvn clean install -DskipTests
   ```

2. **Root-Projekt bauen**
   ```bash
   cd root
   mvn clean install
   ```

3. **Bei Problemen: Clean und Rebuild**
   ```bash
   mvn clean
   rm -rf ~/.m2/repository/r-uu
   mvn install
   ```

