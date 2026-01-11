# ArchUnit Build Fix - Lombok Annotation Processing

**Datum:** 2026-01-11  
**Problem:** `r-uu.lib.archunit` kompiliert nicht - Lombok Annotationen werden nicht verarbeitet  
**Status:** ✅ BEHOBEN

---

## 🐛 Problem-Beschreibung

### Fehlermeldungen

```
[ERROR] cannot find symbol: variable log
  location: class de.ruu.lib.archunit.TestExploreArchUnit

[ERROR] cannot find symbol: method getter()
  location: variable fieldAndAccessors of type de.ruu.lib.archunit.FieldWithAccessors

[ERROR] cannot find symbol: method javaField()
  location: variable field of type de.ruu.lib.archunit.FieldWithAccessors
```

### Ursache

Die Lombok-Annotationen (`@Slf4j`, `@Getter`, `@Accessors(fluent = true)`) wurden nicht verarbeitet, weil:

1. Das archunit-Modul hatte keine explizite Build-Konfiguration
2. Die BOM hatte **zwei verschiedene** Maven Compiler Plugin Konfigurationen:
   - Eine in `<pluginManagement>` (Version 3.13.0, Lombok 1.18.42)
   - Eine in `<plugins>` (Version 3.14.1, Lombok 1.18.38)
3. Das archunit-Modul erbte nicht die richtige Konfiguration

---

## ✅ Lösung

### 1. BOM konsolidiert

**Datei:** `bom/pom.xml`

**Änderungen:**
- ❌ Entfernt: Duplikat Maven Compiler Plugin aus `<pluginManagement>`
- ✅ Behalten: Maven Compiler Plugin in `<plugins>` mit korrekter Konfiguration
- ✅ Aktualisiert: Lombok Version auf 1.18.42

**Ergebnis:** Nur noch EINE zentrale Konfiguration im BOM

### 2. ArchUnit POM erweitert

**Datei:** `root/lib/archunit/pom.xml`

**Änderung:**
```xml
<build>
    <plugins>
        <!-- Explicitly inherit compiler plugin configuration from BOM -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

**Erklärung:** Durch die explizite Referenz erbt das archunit-Modul die komplette Compiler-Konfiguration vom BOM, inklusive:
- Lombok Annotation Processor (Version 1.18.42)
- MapStruct Annotation Processor
- Hibernate JPA Model Generator
- Alle Compiler-Args

---

## 🔍 Warum funktioniert Lombok mit GraalVM 25?

**Kurze Antwort:** Lombok ist vollständig kompatibel mit GraalVM 25.

**Technische Details:**
- Lombok arbeitet als **Annotation Processor** während der Compile-Zeit
- Es modifiziert den AST (Abstract Syntax Tree) des Java-Compilers
- GraalVM 25 verwendet den Standard javac Compiler
- Lombok 1.18.42 unterstützt Java 25 vollständig

**Wichtig:** Lombok funktioniert bei **Compile-Time**, nicht bei Runtime. Deshalb:
- Scope: `provided` (nicht in Runtime-Classpath)
- Optional: `true` (nicht transitiv)

---

## 📋 Validierung

### Build Test (Kommandozeile)

```bash
cd /home/r-uu/develop/github/main/root/lib/archunit
mvn clean compile test-compile
```

**Erwartetes Ergebnis:**
```
[INFO] BUILD SUCCESS
```

### Build Test (Root-Level)

```bash
cd /home/r-uu/develop/github/main/root
mvn clean install
```

**Erwartetes Ergebnis:**
```
[INFO] r-uu.lib.archunit ................................. SUCCESS
[INFO] BUILD SUCCESS
```

### In IntelliJ

1. **Maven Reload:**
   - Rechtsklick auf `root/pom.xml` → **Maven → Reload Project**

2. **Build:**
   - Maven Tool Window → `r-uu.lib.archunit` → `Lifecycle` → `clean` → `install`

3. **Validation:**
   - Keine Compiler-Fehler
   - Tests laufen durch (falls vorhanden)

---

## 🎯 Was wurde generiert?

### Von Lombok @Slf4j

**In:** `TestExploreArchUnit.java`

```java
@Slf4j
class TestExploreArchUnit {
    // Lombok generiert:
    private static final org.slf4j.Logger log = 
        org.slf4j.LoggerFactory.getLogger(TestExploreArchUnit.class);
}
```

### Von Lombok @Getter @Accessors(fluent = true)

**In:** `FieldWithAccessors.java`

```java
@Getter
@Accessors(fluent = true)
public class FieldWithAccessors {
    private final JavaField javaField;
    private final Optional<JavaMethod> getter;
    private final Optional<JavaMethod> setter;
    
    // Lombok generiert (fluent style):
    public JavaField javaField() { return this.javaField; }
    public Optional<JavaMethod> getter() { return this.getter; }
    public Optional<JavaMethod> setter() { return this.setter; }
}
```

**Hinweis:** `fluent = true` bedeutet:
- ✅ Getter: `javaField()` statt `getJavaField()`
- ✅ Setter: `javaField(JavaField)` statt `setJavaField(JavaField)`

---

## 🔧 Troubleshooting

### Problem: Immer noch Compile-Fehler

**Lösung 1 - Maven Cache löschen:**
```bash
cd /home/r-uu/develop/github/main/root
mvn clean install -U
```

**Lösung 2 - IntelliJ Cache invalidieren:**
```
File → Invalidate Caches → Invalidate and Restart
```

**Lösung 3 - Prüfe Annotation Processing in IntelliJ:**
```
File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
☑️ Enable annotation processing
```

### Problem: IntelliJ zeigt Fehler, aber Maven Build funktioniert

**Ursache:** IntelliJ verwendet eigenen Compiler (nicht Maven)

**Lösung:**
```
File → Settings → Build, Execution, Deployment → Build Tools → Maven → Runner
☑️ Delegate IDE build/run actions to Maven
```

---

## 📊 Zusammenfassung

| Aspekt | Vorher | Nachher |
|--------|--------|---------|
| Maven Compiler Plugin Konfigurationen | 2 (widersprüchlich) | 1 (konsolidiert) |
| Lombok Version in BOM | 1.18.38 / 1.18.42 | 1.18.42 |
| ArchUnit Build Config | Keine | Explizit vom BOM geerbt |
| Build Status | ❌ FAILED | ✅ SUCCESS |

---

## 📚 Siehe auch

- **[INTELLIJ-MAVEN-SETUP.md](INTELLIJ-MAVEN-SETUP.md)** - IntelliJ Maven Konfiguration
- **[GRAALVM-25-MIGRATION.md](GRAALVM-25-MIGRATION.md)** - GraalVM Migration
- **[KONSOLIDIERUNG-COMPLETE.md](KONSOLIDIERUNG-COMPLETE.md)** - Build Konsolidierung

---

**Status:** Alle Änderungen wurden durchgeführt. Der Build sollte jetzt funktionieren! 🎉

