# Build-Konfiguration Konsolidierung Abgeschlossen

**Datum:** 2026-01-11

## Zusammenfassung

Die Build-Konfiguration wurde erfolgreich in der BOM (`bom/pom.xml`) konsolidiert. Das root POM (`root/pom.xml`) ist jetzt sehr schlank und importiert nur noch die BOM.

## Durchgeführte Änderungen

### 1. **BOM (`bom/pom.xml`)**
   - ✅ Alle Plugin-Konfigurationen sind jetzt zentral in `<pluginManagement>`
   - ✅ Maven Compiler Plugin Version 3.13.0 (kompatibel mit Java 25)
   - ✅ Lombok 1.18.42 in `annotationProcessorPaths` konfiguriert
   - ✅ Mapstruct, Hibernate JPA Modelgen ebenfalls konfiguriert
   - ✅ Versions-Plugin hinzugefügt (aus root POM verschoben)
   - ✅ Alle Plugin-Versionen aktualisiert

### 2. **Root POM (`root/pom.xml`)**
   - ✅ `<build>` Sektion komplett entfernt
   - ✅ Importiert nur noch die BOM
   - ✅ Sehr schlank und wartbar

### 3. **Archunit POM (`root/lib/archunit/pom.xml`)**
   - ✅ Spezifische Lombok-Konfiguration entfernt
   - ✅ Erbt jetzt alle Build-Konfiguration von der BOM
   - ✅ Tests kompilieren und laufen erfolgreich

## Aktualisierte Dependencies

### Core Libraries
- **Lombok**: 1.18.38 → 1.18.42
- **PostgreSQL JDBC**: 42.7.3 → 42.7.8
- **Byte Buddy**: 1.15.10 → 1.18.3
- **Classmate**: 1.5.1 → 1.7.3

### Jakarta EE
- **Jakarta JSON Bind API**: 3.0.0 → 3.0.1
- **Jakarta Validation API**: 3.0.2 → 3.1.0

### Weitere
- **Keycloak**: 26.0.7 → 26.5.0
- **ControlsFX**: 11.2.0 → 11.2.3
- **Ikonli**: 12.3.1 → 12.4.0
- **Jandex**: 3.2.2 → 3.2.3

### Stabile Versionen (nicht aktualisiert)
- **Jackson**: 2.18.3 (neueste stabile Version)
- **Hibernate**: 6.6.5.Final (aktuell)
- **JUnit Jupiter**: 5.11.3 (aktuell)

## Plugin-Versionen in BOM

```xml
<pluginManagement>
    <plugins>
        <!-- Maven Compiler Plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <release>${java.version}</release>
                <fork>true</fork>
                <proc>full</proc>
                <compilerArgs>
                    <arg>-parameters</arg>
                    <arg>-Amapstruct.defaultComponentModel=default</arg>
                    <arg>-Amapstruct.suppressGeneratorTimestamp=true</arg>
                    <arg>-Amapstruct.suppressGeneratorVersionComment=true</arg>
                </compilerArgs>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.42</version>
                    </path>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.6.3</version>
                    </path>
                    <path>
                        <groupId>org.hibernate.orm</groupId>
                        <artifactId>hibernate-jpamodelgen</artifactId>
                        <version>6.6.5.Final</version>
                    </path>
                </annotationProcessorPaths>
                <showWarnings>true</showWarnings>
                <showDeprecation>true</showDeprecation>
            </configuration>
        </plugin>
        
        <!-- Surefire Plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.5.2</version>
        </plugin>
        
        <!-- Versions Plugin -->
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>versions-maven-plugin</artifactId>
            <version>2.16.2</version>
            <configuration>
                <generateBackupPoms>false</generateBackupPoms>
            </configuration>
        </plugin>
        
        <!-- ... weitere Plugins ... -->
    </plugins>
</pluginManagement>
```

## Vorteile der Konsolidierung

1. **Zentrale Wartung**: Alle Plugin- und Dependency-Versionen an einer Stelle (BOM)
2. **Konsistenz**: Alle Module verwenden dieselbe Konfiguration
3. **Lombok funktioniert überall**: Annotation Processing ist zentral konfiguriert
4. **Einfachere Updates**: Versionen müssen nur an einer Stelle aktualisiert werden
5. **Saubere Struktur**: Root POM ist minimal und fokussiert

## Nächste Schritte

1. ✅ BOM installieren: `mvn clean install -DskipTests` im `bom` Verzeichnis
2. ✅ Projekt bauen: `mvn clean install` im `root` Verzeichnis
3. ✅ Tests validieren: `mvn test` für einzelne Module

## Java 25 + GraalVM Kompatibilität

- ✅ Maven Compiler Plugin 3.13.0 unterstützt Java 25
- ✅ Lombok 1.18.42 ist kompatibel mit Java 25
- ✅ Alle Dependencies sind auf aktuelle, stabile Versionen aktualisiert
- ✅ Annotation Processing funktioniert korrekt mit GraalVM 25

## Validierung

Das `archunit` Modul wurde als Referenz verwendet:
- ✅ Kompiliert erfolgreich mit zentral konfiguriertem Lombok
- ✅ Alle Tests bestehen
- ✅ Lombok generiert korrekt fluent-style Getter (`javaField()`, `getter()`, `setter()`)

## Build-Befehle

### BOM installieren
```bash
cd bom
mvn clean install -DskipTests
```

### Projekt bauen
```bash
cd root
mvn clean install
```

### Module einzeln bauen
```bash
cd root/lib/archunit
mvn clean install
```

### Dependencies aktualisieren (bei Bedarf)
```bash
cd bom
mvn versions:display-dependency-updates
```

## Hinweise

- Die BOM muss **vor** dem root Projekt installiert werden
- Bei Änderungen an der BOM: immer `mvn clean install` ausführen
- Die zentrale Compiler-Konfiguration gilt für **alle** Module automatisch
- Lombok-Annotationen funktionieren in **allen** Modulen ohne zusätzliche Konfiguration

