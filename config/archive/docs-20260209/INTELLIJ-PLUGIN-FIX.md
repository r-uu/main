# IntelliJ IDEA Plugin-Fehler Fix

## Problem
IntelliJ IDEA zeigt Fehler bei Lombok und MapStruct Annotations an, obwohl der Maven-Build erfolgreich ist.

## Ursache
IntelliJ verwendet seine eigene Annotation Processor Konfiguration in `.idea/compiler.xml`, die nicht automatisch mit den Maven `pom.xml` Konfigurationen synchronisiert wird.

Das fehlende `lombok-mapstruct-binding` JAR war nicht in den IntelliJ Annotation Processor Pfaden enthalten, obwohl es in der Maven-Konfiguration vorhanden ist.

## Lösung ✅

Die `.idea/compiler.xml` wurde aktualisiert:

### 1. Lombok Version aktualisiert
- **Alt:** `lombok-1.18.38.jar`
- **Neu:** `lombok-1.18.42.jar`

### 2. lombok-mapstruct-binding hinzugefügt
Folgendes JAR wurde zu den Annotation Processor Pfaden hinzugefügt:
```
/home/r-uu/.m2/repository/org/projectlombok/lombok-mapstruct-binding/0.2.0/lombok-mapstruct-binding-0.2.0.jar
```

### 3. Betroffene Profile
Die Änderungen wurden in folgenden Profilen vorgenommen:
- `Annotation profile for r-uu.app.jeeeraaah.backend.api.ws.rs`
- `Annotation profile for r-uu.app.jeeeraaah.backend.persistence.jpa`

## Nächste Schritte

### In IntelliJ IDEA ausführen:

1. **Projekt neu laden:**
   - File → Invalidate Caches... → Invalidate and Restart

2. **Maven Projekt reimportieren:**
   - Maven Tool Window → Reload All Maven Projects (kreisförmiger Pfeil)

3. **Build → Rebuild Project**

4. **Annotation Processing neu starten:**
   - Build → Build Project
   - Oder: Strg+F9 (Windows/Linux)

### Ergebnis
Nach dem Neustart sollten die Plugin-Fehler für Lombok und MapStruct verschwinden, da IntelliJ jetzt die korrekte Processor-Konfiguration verwendet.

## Technische Details

### Warum lombok-mapstruct-binding?
Das `lombok-mapstruct-binding` ermöglicht MapStruct, die von Lombok generierten Getter/Setter zu erkennen. Ohne dieses Binding:
- MapStruct sieht Lombok-generierte Methoden nicht
- IntelliJ zeigt Fehler wie "Cannot find getter" oder "Cannot find setter"
- Der Maven-Build funktioniert trotzdem (weil Maven die korrekte Konfiguration hat)

### Processor-Reihenfolge ist wichtig!
Die Processors werden in dieser Reihenfolge ausgeführt:
1. **Lombok** - generiert Getter/Setter/Konstruktoren
2. **lombok-mapstruct-binding** - macht Lombok-Code für MapStruct sichtbar
3. **MapStruct** - generiert Mapper-Implementierungen

## Zukünftige Wartung

### Bei Lombok-Updates
Wenn Lombok in `bom/pom.xml` aktualisiert wird, muss auch `.idea/compiler.xml` aktualisiert werden:

```xml
<entry name="/home/r-uu/.m2/repository/org/projectlombok/lombok/[NEUE_VERSION]/lombok-[NEUE_VERSION].jar" />
```

### Bei MapStruct-Updates
Analog für MapStruct-Updates in `bom/pom.xml`:

```xml
<entry name="/home/r-uu/.m2/repository/org/mapstruct/mapstruct-processor/[NEUE_VERSION]/mapstruct-processor-[NEUE_VERSION].jar" />
```

### Automatisierung (Optional)
Man könnte ein Maven-Plugin verwenden, das die IntelliJ-Konfiguration automatisch generiert:
- `maven-idea-plugin` (deprecated)
- Oder: IntelliJ IDEA's Maven Import automatisch die Processor-Pfade setzen lassen

**Empfehlung:** Da das Projekt JPMS (Java Platform Module System) nutzt, ist die manuelle Konfiguration stabiler.

## Warum nicht einfach "Maven Auto-Import" verwenden?

IntelliJ's Maven Auto-Import funktioniert **nicht perfekt** mit JPMS-Projekten, weil:
1. Module-Info.java Dateien spezielle Behandlung brauchen
2. Annotation Processor Konfigurationen komplex sind
3. Die Processor-Reihenfolge kritisch ist

Daher ist die manuelle Konfiguration in `.idea/compiler.xml` die zuverlässigste Lösung.

## Status
✅ **GELÖST** - Die IntelliJ Plugin-Fehler sollten nach einem IDE-Neustart verschwinden.

---
*Erstellt: 2026-02-09*
*Letzte Änderung: 2026-02-09*

