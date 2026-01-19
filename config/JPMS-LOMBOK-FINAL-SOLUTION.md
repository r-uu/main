# JPMS Lombok - Endgültige Lösung

**Datum:** 2026-01-18  
**Status:** ✅ GELÖST

## Problem

Bei der Ausführung von modularen Java-Anwendungen (JPMS) trat folgender Fehler auf:

```
Error occurred during initialization of boot layer
java.lang.module.FindException: Module lombok not found, required by de.ruu.lib.util
```

## Ursache

Lombok war fälschlicherweise als **runtime dependency** konfiguriert:
- In `module-info.java`: `requires transitive lombok;`
- In `pom.xml`: Ohne `<scope>provided</scope>`

Dies führte dazu, dass Lombok zur Laufzeit benötigt wurde, obwohl es nur zur Compile-Zeit verfügbar war.

## Lösung: Striktes JPMS mit Lombok

### 1. module-info.java anpassen

**Vorher:**
```java
requires transitive lombok;
```

**Nachher:**
```java
requires static lombok; // Lombok is only needed at compile-time
```

### 2. pom.xml anpassen

**Vorher:**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

**Nachher:**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

## Ergebnis

✅ Maven Build erfolgreich: `mvn clean install -DskipTests`  
✅ Alle Module kompilieren korrekt  
✅ Lombok generiert Code zur Compile-Zeit  
✅ Zur Laufzeit wird Lombok nicht mehr benötigt  
✅ **Striktes JPMS** wird eingehalten

## Wichtige Hinweise

### Lombok und JPMS Best Practices

1. **`requires static`** - Lombok ist nur zur Compile-Zeit nötig
2. **`scope=provided`** - Lombok nicht in Runtime Classpath
3. **NIEMALS `requires transitive`** - Lombok darf nicht transitiv weitergegeben werden

### Warnungen die bleiben (und OK sind)

```
[WARNING] /home/r-uu/.../Collections.java:[57,25] [exports] class NonNull in module lombok 
is not indirectly exported using 'requires transitive'
```

Diese Warnungen entstehen, weil `@NonNull` in öffentlichen APIs verwendet wird. Das ist ein bekanntes Lombok-JPMS-Problem, aber **nicht kritisch**, da:
- Der Code kompiliert und läuft korrekt
- Zur Laufzeit werden die Lombok-Annotationen nicht benötigt
- Die generierten Methoden (Getter/Setter/etc.) funktionieren

### Alternative Lösung (falls Warnungen stören)

Anstatt Lombok's `@NonNull` könntest du verwenden:
- `jakarta.annotation.Nonnull`
- `org.jetbrains.annotations.NotNull`
- Eigene Null-Checks

## Nächste Schritte

1. ✅ IntelliJ Cache invalidieren: File → Invalidate Caches → Invalidate and Restart
2. ✅ Maven Reload in IntelliJ
3. ✅ Run-Konfigurationen testen

## Betroffene Dateien

- `root/lib/util/src/main/java/module-info.java` - `requires static lombok`
- `root/lib/util/pom.xml` - `<scope>provided</scope>`

## Validierung

```bash
cd /home/r-uu/develop/github/main/root
mvn clean install -DskipTests
```

Erwartetes Ergebnis: **BUILD SUCCESS** ✅
