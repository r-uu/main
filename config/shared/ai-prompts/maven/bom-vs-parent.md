# Maven Multi-Module Architektur: BOM vs. Root

## Ihre Frage

> Sollte das Root-Modul das BOM-Modul als Parent haben, damit es die Konfigurationen im BOM-Modul an seine Children weitergeben kann?

## Antwort: NEIN ❌

**Das Root-Modul sollte NICHT das BOM als Parent haben.**

## ✅ Die richtige Architektur (wie Sie es bereits haben!)

Ihre aktuelle Struktur ist korrekt:

```
main/
├── bom/                    # Bill of Materials
│   └── pom.xml            # Kein Parent, keine Children
│
└── root/                   # Parent POM
    ├── pom.xml            # Kein Parent, importiert BOM
    └── lib/
        └── pom.xml        # Parent: root
```

### Ihre aktuelle Konfiguration (KORREKT ✓)

**bom/pom.xml:**
```xml
<artifactId>r-uu.bom</artifactId>
<!-- Kein <parent> -->
<!-- Keine <modules> -->
```

**root/pom.xml:**
```xml
<artifactId>r-uu.root</artifactId>
<!-- Kein <parent> -->

<modules>
    <module>lib</module>
</modules>

<dependencyManagement>
    <dependencies>
        <!-- BOM importieren (nicht als Parent!) -->
        <dependency>
            <groupId>r-uu</groupId>
            <artifactId>r-uu.bom</artifactId>
            <version>0.0.1</version>
            <type>pom</type>
            <scope>import</scope>  <!-- ← Wichtig! -->
        </dependency>
    </dependencies>
</dependencyManagement>
```

**root/lib/pom.xml:**
```xml
<parent>
    <groupId>r-uu</groupId>
    <artifactId>r-uu.root</artifactId>
    <version>0.0.1</version>
</parent>
```

## 🎯 Warum diese Trennung?

### BOM (Bill of Materials)

**Zweck:**
- Zentrale Verwaltung von Dependency-Versionen
- Zentrale Plugin-Versionen und Konfigurationen
- Properties (Java-Version, Encoding, etc.)
- Wiederverwendbar über Projekt-Grenzen hinweg

**Eigenschaften:**
- ❌ Kein Parent
- ❌ Keine Children (keine `<modules>`)
- ✅ Nur `<dependencyManagement>` und `<pluginManagement>`
- ✅ Kann von mehreren Projekten importiert werden

### Root (Parent POM)

**Zweck:**
- Parent für Ihre konkreten Module (lib, app, etc.)
- Projekt-spezifische Konfiguration
- Orchestrierung der Child-Module
- Vererbt Konfiguration an Children

**Eigenschaften:**
- ❌ Kein Parent (ist selbst Root)
- ✅ Hat Children (`<modules>`)
- ✅ Importiert das BOM
- ✅ Kann zusätzliche projekt-spezifische Konfiguration haben

## 📚 Import vs. Parent - Der Unterschied

### Import (EMPFOHLEN für BOM)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>r-uu</groupId>
            <artifactId>r-uu.bom</artifactId>
            <type>pom</type>
            <scope>import</scope>  <!-- ← Import -->
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Vorteile:**
- ✅ Sie können mehrere BOMs importieren
- ✅ Flexibler - Sie behalten die Kontrolle
- ✅ Root kann eigenen Parent haben (falls gewünscht)
- ✅ Klare Trennung von Verantwortlichkeiten

**Was wird importiert:**
- `<dependencyManagement>` ✓
- `<pluginManagement>` **NEIN** ❌
- `<properties>` **NEIN** ❌

### Parent (NICHT empfohlen für BOM)

```xml
<parent>
    <groupId>r-uu</groupId>
    <artifactId>r-uu.bom</artifactId>
    <version>0.0.1</version>
</parent>
```

**Nachteile:**
- ❌ Nur EIN Parent möglich
- ❌ Zu starke Kopplung
- ❌ BOM-Änderungen betreffen direkt alle Children
- ❌ Nicht die intendierte Verwendung eines BOM

**Was wird vererbt:**
- `<dependencyManagement>` ✓
- `<pluginManagement>` ✓
- `<properties>` ✓
- Alle anderen Konfigurationen ✓

## 🔧 Wie Sie Konfiguration weitergeben

### Szenario 1: Dependencies

**BOM definiert Versionen:**
```xml
<!-- bom/pom.xml -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.11.4</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Root importiert BOM:**
```xml
<!-- root/pom.xml -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>r-uu</groupId>
            <artifactId>r-uu.bom</artifactId>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Child verwendet Dependency OHNE Version:**
```xml
<!-- root/lib/pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <!-- Version kommt aus BOM via Root -->
    </dependency>
</dependencies>
```

### Szenario 2: Plugins (müssen in Root konfiguriert werden)

Da `<pluginManagement>` NICHT via Import übertragen wird, müssen Sie in Root konfigurieren:

```xml
<!-- root/pom.xml -->
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <release>25</release>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

**Alternative:** BOM kann Plugin-Versionen definieren, Root kann sie importieren:

```xml
<!-- bom/pom.xml -->
<properties>
    <maven-compiler-plugin.version>3.13.0</maven-compiler-plugin.version>
</properties>

<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven-compiler-plugin.version}</version>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

Aber: **Properties werden nicht via Import übertragen!**

**Lösung:** Definieren Sie Plugin-Management direkt in Root oder verwenden Sie einen separaten Parent für Plugins.

### Szenario 3: Properties

Properties aus dem BOM werden NICHT automatisch importiert. Optionen:

**Option A:** Properties direkt in Root duplizieren
```xml
<!-- root/pom.xml -->
<properties>
    <java.version>25</java.version>
    <maven.compiler.release>${java.version}</maven.compiler.release>
</properties>
```

**Option B:** BOM als Parent verwenden (nicht empfohlen)

**Option C (empfohlen):** Separate Parent-POM für gemeinsame Properties

## 🏗️ Erweiterte Architektur (bei Bedarf)

Wenn Sie Plugin-Management und Properties auch zentral verwalten möchten:

```
main/
├── bom/                    # Dependency-Versionen
│   └── pom.xml
│
├── parent/                 # Plugin-Management + Properties
│   └── pom.xml            # Importiert BOM
│
└── root/                   # Projekt-Root
    ├── pom.xml            # Parent: parent
    └── lib/
        └── pom.xml        # Parent: root
```

**Aber:** Für Ihr Projekt ist die aktuelle 2-stufige Struktur (BOM + Root) ausreichend!

## ✅ Zusammenfassung für Ihr Projekt

### Was Sie haben (KORREKT):
- ✅ BOM ohne Parent/Children
- ✅ Root importiert BOM via `<scope>import</scope>`
- ✅ lib hat Root als Parent

### Was Sie tun sollten:
1. ✅ **Behalten Sie die aktuelle Struktur bei**
2. ✅ BOM: Nur `<dependencyManagement>`
3. ✅ Root: Importiert BOM + definiert `<pluginManagement>` und `<properties>`
4. ✅ Children: Haben Root als Parent

### Was Sie ergänzen können (in root/pom.xml):

```xml
<!-- root/pom.xml -->
<properties>
    <!-- Übernehmen Sie wichtige Properties aus BOM -->
    <java.version>25</java.version>
    <maven.compiler.release>${java.version}</maven.compiler.release>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<build>
    <pluginManagement>
        <plugins>
            <!-- Zentrale Plugin-Konfiguration -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <release>${java.version}</release>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

## 📖 Weiterführende Ressourcen

Siehe auch:
- `config/shared/ai-prompts/maven/multi-module-setup.md`
- `bom/readme.md`
- Maven Best Practices Documentation

---

**Fazit:** Ihre aktuelle Architektur ist korrekt. BOM sollte NICHT als Parent verwendet werden, sondern via Import eingebunden werden. ✅

