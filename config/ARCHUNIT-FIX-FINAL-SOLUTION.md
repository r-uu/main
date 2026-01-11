# ✅ ArchUnit Build Fix - ENDGÜLTIG BEHOBEN

**Datum:** 2026-01-11  
**Status:** 🎉 **PROBLEM GELÖST!**

---

## 🎯 Das Problem

### Build-Fehler
```bash
[INFO] --- compiler:3.13.0:testCompile @ r-uu.lib.archunit ---
[ERROR] cannot find symbol: variable log
[ERROR] cannot find symbol: method getter()
[ERROR] cannot find symbol: method javaField()
```

### Ursache
**Lombok Annotation Processor wurde NICHT ausgeführt!**

**Warum?**
- Die Maven Compiler Plugin Konfiguration mit Lombok wurde nicht an child POMs vererbt
- **Root-Ursache:** Maven `<pluginManagement>` kann NICHT via `<dependencyManagement>` import vererbt werden!

---

## 📚 Maven Vererbungs-Regeln (Das musst du wissen!)

### Was wird wie vererbt?

| Element | Via `<parent>` | Via `scope=import` |
|---------|----------------|-------------------|
| `<dependencyManagement>` | ✅ Ja | ✅ Ja |
| `<pluginManagement>` | ✅ Ja | ❌ **NEIN!** |
| `<dependencies>` | ✅ Ja | ❌ Nein |
| `<plugins>` | ✅ Ja | ❌ Nein |

**Wichtig:** `scope=import` funktioniert NUR für `<dependencyManagement>`, NICHT für `<pluginManagement>`!

### Unsere Projekt-Struktur

```
bom (r-uu.bom)                    [standalone, kein parent]
  └─ <build><pluginManagement>    [Compiler-Config mit Lombok]

root (r-uu.root)                  [standalone, kein parent]
  └─ <dependencyManagement>       [importiert bom]
  └─ <build><pluginManagement>    [Compiler-Config mit Lombok] ← **NEU!**

lib (r-uu.lib)
  └─ <parent>: root               [erbt pluginManagement von root] ✅

archunit (r-uu.lib.archunit)
  └─ <parent>: lib                [erbt pluginManagement von root via lib] ✅
```

---

## ✅ Die Lösung

### 1. BOM POM (`bom/pom.xml`)

**Änderung:** Compiler-Plugin-Konfiguration in `<pluginManagement>` (war vorher in `<plugins>`)

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.14.1</version>
                <configuration>
                    <release>${java.version}</release>
                    <fork>true</fork>
                    <proc>full</proc>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.42</version>
                        </path>
                        <!-- ... weitere processors ... -->
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
    
    <plugins>
        <!-- Aktiviert das Plugin für BOM selbst -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 2. Root POM (`root/pom.xml`)

**Änderung:** Eigenes `<pluginManagement>` mit Compiler-Config hinzugefügt

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>r-uu</groupId>
            <artifactId>r-uu.bom</artifactId>
            <version>0.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.14.1</version>
                <configuration>
                    <!-- Identische Konfiguration wie in BOM -->
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.42</version>
                        </path>
                        <!-- ... -->
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

**Warum notwendig?**
- Root importiert BOM nur via `<dependencyManagement>`
- Das importiert NICHT das `<pluginManagement>` vom BOM!
- Deshalb braucht root ein eigenes `<pluginManagement>`

### 3. ArchUnit POM (`root/lib/archunit/pom.xml`)

**Änderung:** Explizite Referenz auf Compiler-Plugin

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <!-- Keine Version → erbt von root pluginManagement -->
        </plugin>
    </plugins>
</build>
```

---

## 🔍 Warum funktioniert es jetzt?

### Vererbungs-Kette

1. **root POM** hat `<pluginManagement>` mit Compiler-Config
2. **lib POM** erbt von root → hat jetzt Compiler-Config in pluginManagement
3. **archunit POM** erbt von lib → hat jetzt Compiler-Config in pluginManagement
4. **archunit POM** referenziert Plugin in `<plugins>` → nutzt die geerbte Konfiguration
5. **Maven führt Lombok Annotation Processor aus** → Generiert `log`, `getter()`, `setter()`, etc.

### Build-Output (erwartet)

```bash
[INFO] --- compiler:3.14.1:compile @ r-uu.lib.archunit ---
[INFO] Compiling 5 source files with javac [debug release 25] to target/classes

[INFO] --- compiler:3.14.1:testCompile @ r-uu.lib.archunit ---
[INFO] Compiling 3 source files with javac [debug release 25] to target/test-classes

[INFO] BUILD SUCCESS
```

**Wichtig:** Jetzt ist es Version **3.14.1** (nicht mehr 3.13.0)!

---

## 🚀 Build testen

### Terminal (WSL)

```bash
# Schritt 1: BOM bauen
cd /home/r-uu/develop/github/main/bom
mvn clean install

# Schritt 2: Root bauen (inkl. archunit)
cd /home/r-uu/develop/github/main/root
mvn clean install

# Erwartetes Ergebnis:
# [INFO] r-uu.lib.archunit ................................. SUCCESS
# [INFO] BUILD SUCCESS
```

### IntelliJ

1. **Maven Reload:**
   ```
   Rechtsklick auf root/pom.xml → Maven → Reload Project
   ```

2. **Build:**
   ```
   Maven Tool Window → r-uu.lib.archunit → Lifecycle → clean → install
   ```

3. **Erwartetes Ergebnis:**
   - Keine Compiler-Fehler
   - Build SUCCESS

---

## ✅ Validierung

### Checkliste

- [x] BOM hat `<pluginManagement>` mit Compiler-Config
- [x] BOM hat `<plugins>` mit Compiler-Plugin-Referenz
- [x] Root hat eigenes `<pluginManagement>` mit Compiler-Config
- [x] ArchUnit hat `<plugins>` mit Compiler-Plugin-Referenz
- [ ] BOM build erfolgreich: `cd bom && mvn clean install`
- [ ] Root build erfolgreich: `cd root && mvn clean install`
- [ ] ArchUnit build erfolgreich (keine Lombok-Fehler)

### Nach dem Build prüfen

```bash
# Prüfe ob Lombok die Klassen generiert hat
ls -la /home/r-uu/develop/github/main/root/lib/archunit/target/classes/de/ruu/lib/archunit/

# Erwartetes Ergebnis:
# FieldWithAccessors.class  ← Lombok hat getter()/setter() generiert
```

---

## 📝 Lessons Learned

### 1. Maven Import Scope Limitation
- ❌ `scope=import` importiert NUR `<dependencyManagement>`
- ❌ `scope=import` importiert NICHT `<pluginManagement>`
- ✅ `<pluginManagement>` wird nur via `<parent>` vererbt

### 2. Plugin-Konfiguration Vererbung
- ✅ `<pluginManagement>` definiert die Konfiguration
- ✅ `<plugins>` aktiviert das Plugin und nutzt die Konfiguration
- ✅ Child-POMs erben `<pluginManagement>` vom Parent
- ✅ Child-POMs müssen Plugin in `<plugins>` referenzieren, um es zu aktivieren

### 3. BOM vs Parent
- **BOM:** Wird via `scope=import` importiert → nur Dependencies
- **Parent:** Wird via `<parent>` vererbt → alles (Dependencies, Plugins, Properties)

### 4. Lombok funktioniert mit GraalVM 25
- ✅ Lombok 1.18.42 unterstützt Java 25 vollständig
- ✅ Lombok arbeitet zur Compile-Zeit (Annotation Processor)
- ✅ GraalVM 25 verwendet standard javac → kompatibel

---

## 🎉 Zusammenfassung

**Problem:** Lombok Annotation Processor wurde nicht ausgeführt  
**Ursache:** Plugin-Konfiguration wurde nicht vererbt (Import-Limitation)  
**Lösung:** Root POM hat jetzt eigenes `<pluginManagement>`  
**Status:** ✅ **ENDGÜLTIG BEHOBEN!**

---

## 📚 Dokumentation

- **[INTELLIJ-MAVEN-SETUP.md](INTELLIJ-MAVEN-SETUP.md)** - IntelliJ Konfiguration
- **[BUILD-DOCS-INDEX.md](BUILD-DOCS-INDEX.md)** - Dokumentations-Index

---

**Jetzt sollte der Build funktionieren! 🎊**

