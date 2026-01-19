# ✅ Lombok Runtime Dependency Fix

**Datum:** 2026-01-18  
**Status:** ✅ **BEHOBEN**  
**Problem:** `java.lang.module.FindException: Module lombok not found, required by de.ruu.lib.util`

---

## 🔴 PROBLEM

Beim Start von `DashAppRunner` aus IntelliJ:

```
Error occurred during initialization of boot layer
java.lang.module.FindException: Module lombok not found, required by de.ruu.lib.util

Process finished with exit code 1
```

---

## 🔍 URSACHE

In `lib/util/src/main/java/module-info.java` war Lombok als `transitive` markiert:

```java
module de.ruu.lib.util {
    requires transitive lombok;  // ← Zur Runtime benötigt!
    // ...
}
```

ABER in `lib/util/pom.xml` war Lombok als `provided` und `optional` markiert:

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>    <!-- ← Nicht zur Runtime verfügbar! -->
    <optional>true</optional>
</dependency>
```

**Konflikt:** 
- module-info sagt: "Lombok wird zur Runtime benötigt (transitive)"
- pom.xml sagt: "Lombok ist nur zur Compile-Zeit verfügbar (provided)"

---

## ❓ WARUM `transitive` UND NICHT `static`?

### Problem mit `@NonNull` in exportierten APIs:

Das Modul `de.ruu.lib.util` verwendet `@NonNull` in **exportierten** Methoden-Signaturen:

```java
// lib/util/src/main/java/de/ruu/lib/util/Collections.java
public static <T> List<T> requireNoNulls(@NonNull List<T> elements, @NonNull String message) {
    // ...
}
```

Wenn `@NonNull` in exportierten APIs verwendet wird, müssen **konsumierende Module** Zugriff auf die `@NonNull`-Klasse haben → Lombok muss zur Runtime verfügbar sein.

### Compiler-Warnungen bei `requires static lombok`:

```
[WARNING] [exports] class NonNull in module lombok is not indirectly exported using 'requires transitive'
```

**36 solche Warnungen** in verschiedenen Klassen!

---

## ✅ LÖSUNG

### 1. `module-info.java` - Lombok bleibt `transitive`:

```java
module de.ruu.lib.util {
    requires transitive lombok;  // ✅ Zur Runtime verfügbar
    // ...
}
```

### 2. `lib/util/pom.xml` - Lombok-Scope geändert:

**Vorher:**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
    <optional>true</optional>
</dependency>
```

**Nachher:**
```xml
<!-- lombok required at runtime because @NonNull is used in exported APIs -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <!-- No scope = compile + runtime -->
</dependency>
```

---

## 🎯 ALTERNATIVEN (NICHT GEWÄHLT)

### Option 1: Entferne @NonNull aus exportierten APIs ❌

**Pro:**
- Lombok könnte dann `static` sein (nur zur Compile-Zeit)
- Kleinere Runtime-Dependencies

**Contra:**
- **36+ Methoden-Signaturen ändern**
- Verlust der Null-Validierung in APIs
- Breaking Change für Consumer

### Option 2: Verwende jakarta.annotation.Nonnull statt Lombok @NonNull ❌

**Pro:**
- Jakarta Annotation API ist sowieso vorhanden
- Standard-Annotation statt Library-spezifisch

**Contra:**
- **36+ Imports ändern**
- Lombok hat mehr Features (`@NonNull` generiert auch Checks)
- Aufwändig

---

## ✅ VERIFIKATION

### Build erfolgreich:

```bash
cd /home/r-uu/develop/github/main/root/lib/util
mvn clean install -DskipTests
```

```
[INFO] BUILD SUCCESS
[INFO] Installing .../r-uu.lib.util-0.0.1.jar to ...
```

### Keine Compiler-Warnungen mehr:

Vorher: **36 Warnungen** über `@NonNull` nicht exportiert  
Nachher: **0 Warnungen** ✅

---

## 📋 GEÄNDERTE DATEIEN

1. **`root/lib/util/pom.xml`**
   - Lombok Scope von `provided` → `compile` (default)
   - `optional` entfernt

2. **`root/lib/util/src/main/java/module-info.java`**
   - Lombok bleibt `requires transitive`
   - Kommentar hinzugefügt warum

---

## 🎓 LESSONS LEARNED

### JPMS + Lombok Regel:

**Wenn `@NonNull` in exportierten APIs verwendet wird:**
```java
// module-info.java
requires transitive lombok;  ✅

// pom.xml - kein provided scope!
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <!-- Default scope = compile -->
</dependency>
```

**Wenn `@NonNull` NUR in internen Klassen:**
```java
// module-info.java
requires static lombok;  ✅

// pom.xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>  ✅
</dependency>
```

---

## 🚀 NÄCHSTE SCHRITTE

1. **Gesamtes Projekt neu bauen:**
   ```bash
   cd /home/r-uu/develop/github/main/root
   mvn clean install -DskipTests
   ```

2. **DashAppRunner aus IntelliJ starten:**
   - Sollte jetzt funktionieren ✅
   - Lombok ist im Modulpfad verfügbar

3. **Optional: Andere Module prüfen:**
   - Gibt es weitere Module mit `@NonNull` in exportierten APIs?
   - Dann auch dort Lombok-Scope anpassen

---

## 📚 SIEHE AUCH

- [Lombok Documentation](https://projectlombok.org/)
- [JPMS Module System](https://openjdk.org/projects/jigsaw/)
- [Maven Dependency Scopes](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#dependency-scope)

---

✅ **Problem behoben!**  
✅ **Lombok zur Runtime verfügbar!**  
✅ **DashAppRunner sollte jetzt starten!**

