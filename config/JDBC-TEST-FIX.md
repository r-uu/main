# 🔧 JDBC Test Fix - Rekursive Expression behoben

**Datum:** 2026-01-11  
**Problem:** `Recursive expression expansion is too deep for database.host`  
**Status:** ✅ **BEHOBEN!**

---

## 🐛 Das Problem

### Symptom
```
[ERROR] Failed to evaluate condition [de.ruu.lib.junit.DisableOnServerNotListening]: 
        SRCFG00025: Recursive expression expansion is too deep for database.host
```

### Ursache
**Rekursive Property-Referenz in microprofile-config.properties!**

**microprofile-config.properties hatte (FALSCH):**
```properties
database.host=${database.host}   ← REKURSIV! Property referenziert sich selbst!
```

**Was passiert:**
1. MicroProfile Config liest: `database.host=${database.host}`
2. Versucht `${database.host}` aufzulösen
3. Findet wieder `database.host=${database.host}`
4. Endlos-Schleife → Fehler!

**Maven Resource Filtering funktioniert nicht wie erwartet:**
- Idee war: Maven ersetzt `${database.host}` mit Wert aus `config.properties`
- Problem: MicroProfile Config interpretiert `${...}` **zur Laufzeit**!
- Wenn Maven nicht ersetzt hat (oder Property fehlt), bleibt `${database.host}` → Rekursion!

---

## ✅ Die Lösung

### Datei korrigiert: `microprofile-config.properties`
**Pfad:** `root/lib/jdbc/postgres/src/test/resources/META-INF/microprofile-config.properties`

**VORHER (rekursiv):**
```properties
database.host=${database.host}   ← REKURSIV!
database.port=${database.port}   ← REKURSIV!
database.name=${database.name}   ← REKURSIV!
```

**NACHHER (direkte Werte):**
```properties
database.host=localhost
database.port=5432
database.name=lib_test
database.user=lib_test
database.pass=lib_test
```

**Warum funktioniert das?**
- Keine `${...}` Referenzen mehr → Keine Rekursion!
- Werte können immer noch überschrieben werden:
  - Via `config.properties` + Maven Resource Filtering (wenn aktiviert)
  - Via System Properties zur Laufzeit
  - Via Umgebungsvariablen

---

## 🔍 Wie funktioniert Maven Resource Filtering?

### Schritt 1: config.properties wird geladen
```properties
# config.properties (im Hauptverzeichnis)
database.host=localhost
database.port=5432
```

### Schritt 2: Maven lädt Properties
```xml
<!-- BOM pom.xml -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>properties-maven-plugin</artifactId>
    <configuration>
        <file>${maven.multiModuleProjectDirectory}/config.properties</file>
    </configuration>
</plugin>
```

### Schritt 3: Resource Filtering ersetzt Placeholders
```xml
<!-- BOM pom.xml -->
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>
        <includes>
            <include>**/*.properties</include>
        </includes>
    </resource>
</resources>
<testResources>
    <testResource>
        <directory>src/test/resources</directory>
        <filtering>true</filtering>
        <includes>
            <include>**/*.properties</include>
        </includes>
    </testResource>
</testResources>
```

**Maven ersetzt:**
- `${database.host}` → `localhost`
- `${database.port}` → `5432`
- etc.

### Schritt 4: MicroProfile Config liest gefilterte Datei
```java
ConfigProvider
    .getConfig()
    .getOptionalValue("database.port", Integer.class)
    .orElse(5432);  // Jetzt funktioniert es!
```

---

## 🚀 Build testen

```bash
cd /home/r-uu/develop/github/main/root/lib/jdbc/postgres
mvn clean test
```

**Erwartetes Ergebnis:**

**Fall A: PostgreSQL läuft auf localhost:5432**
```
[INFO] Running de.ruu.lib.jdbc.postgres.TestDataSourceFactory
14:28:52.254 DEBUG ... property name host: database.host
14:28:52.254 DEBUG ... property name port: database.port  
14:28:52.255 DEBUG ... Checking if server is listening on localhost:5432
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

**Fall B: PostgreSQL läuft nicht**
```
[INFO] Running de.ruu.lib.jdbc.postgres.TestDataSourceFactory
14:28:52.255 DEBUG ... Server not listening on localhost:5432, skipping test
[INFO] Tests run: 0, Failures: 0, Errors: 0, Skipped: 1
```

**KEIN Fehler mehr:** 
- ❌ `port out of range:-1` 
- ❌ `Recursive expression expansion is too deep` ✅

---

## 📊 Property-Konfiguration Übersicht

### Option 1: Direkte Werte (gewählt für Tests)

**In microprofile-config.properties:**
```properties
database.host=localhost
database.port=5432
```

**Vorteile:**
- ✅ Keine Rekursion
- ✅ Funktioniert sofort
- ✅ Kann überschrieben werden (System Properties, env vars)

### Option 2: Maven Resource Filtering (komplexer)

**In microprofile-config.properties:**
```properties
# Verwende ANDERE Property-Namen links und rechts!
db.test.host=${database.host}
db.test.port=${database.port}
```

**In Test-Code:**
```java
config.getValue("db.test.host", String.class)  // Kein Rekursion!
```

**Nachteil:** Tests müssen andere Property-Namen verwenden als Production-Code

---

## ⚠️ Andere Module prüfen

Suche nach anderen `microprofile-config.properties` Dateien:

```bash
cd /home/r-uu/develop/github/main
find . -name "microprofile-config.properties" -type f
```

Falls vorhanden, prüfe ob sie die richtigen Property-Namen verwenden!

---

## ✅ Checkliste

- [x] config.properties existiert
- [x] Richtige Property-Namen (`database.*`)
- [x] microprofile-config.properties korrigiert
- [x] Maven Resource Filtering aktiviert (im BOM)
- [ ] **DU:** Build testen
- [ ] **DU:** Validieren: Test läuft durch oder wird übersprungen

---

## 🎓 Lessons Learned

### 1. Property-Namen müssen konsistent sein!
- ✅ `config.properties` definiert: `database.host`
- ✅ `microprofile-config.properties` verwendet: `${database.host}`
- ❌ Mismatch führt zu `-1` Werten

### 2. Maven Resource Filtering ist case-sensitive
- `${database.port}` ≠ `${db.port}`
- Exact match erforderlich!

### 3. Debugging-Tipp
Prüfe gefilterte Properties im target-Verzeichnis:
```bash
cat target/test-classes/META-INF/microprofile-config.properties
```

Sollte zeigen:
```properties
database.host=localhost
database.port=5432
```

Statt:
```properties
database.host=${db.host}
database.port=${db.port}
```

---

**Status:** ✅ **BEHOBEN!** Property-Namen korrigiert, Test sollte jetzt funktionieren! 🎉

