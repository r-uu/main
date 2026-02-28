# JPMS Package-Hiding Improvements - 2026-02-28

## Zusammenfassung

Systematische Analyse und Verbesserung der JPMS-Kapselung durch Entfernung unnötiger Package-Exports.

## Durchgeführte Verbesserungen

### 1. lib.jpa.core - Removed criteria.restriction export ✅

**Problem:**
- Das Paket `de.ruu.lib.jpa.core.criteria.restriction` wurde exportiert
- Enthält 19 Implementierungsklassen (BetweenExpression, Conjunction, Disjunction, etc.)
- Wird nur intern verwendet (von AbstractRepository im gleichen Modul)

**Lösung:**
- Export `exports de.ruu.lib.jpa.core.criteria.restriction;` aus module-info.java entfernt
- Paket ist jetzt vollständig gekapselt
- Nur die criteria-Facade (Criteria, Criterion, Order, Projection) bleibt über `de.ruu.lib.jpa.core.criteria` zugänglich

**Datei:**
- `/root/lib/jpa/core/src/main/java/module-info.java`

**Ergebnis:**
- Build erfolgreich (BUILD SUCCESS, 03:18 min)
- Bessere Kapselung - 19 Implementierungsklassen sind jetzt vor externem Zugriff geschützt
- Kleinere öffentliche API-Oberfläche

**Externe Verwendung:**
```
VORHER: 3 exports (core, criteria, criteria.restriction)
NACHHER: 2 exports (core, criteria)
```

## Analysierte Module (keine Änderung erforderlich)

### backend.persistence.jpa
**Status:** Deferred - CDI Complexity

**Grund:**
- Services (TaskServiceJPA, TaskGroupServiceJPA) und Repositories werden via `@Inject` von backend.api.ws.rs verwendet
- CDI benötigt Typinformationen zur Compile-Zeit für Dependency Injection
- Umstrukturierung würde Interface-Extraktion und größere Architekturänderungen erfordern

**Potenzielle zukünftige Verbesserung:**
1. Interfaces für Services erstellen (TaskGroupService, TaskService)
2. Interfaces exportieren, Implementierungen in `internal/` Package verschieben
3. CDI-Injection über Interfaces statt konkrete Klassen

### lib.keycloak.admin
**Status:** Alle Exports erforderlich

**Grund:**
- `setup/` Package wird von `lib.docker.health` verwendet (KeycloakRealmSetup)
- `validation/` Package wird von `backend.api.ws.rs` Health-Endpoints verwendet (JwtTokenParser, KeycloakConfigValidator)
- Alle exportierten Sub-Packages haben legitime externe Verwendung

### lib.docker.health
**Status:** Alle Exports erforderlich

**Grund:**
- `check/` Package wird intern verwendet (HealthCheckRunner nutzt HealthCheck)
- `fix/` Package wird von `frontend.ui.fx` verwendet (AutoFixRunner, DockerContainerStartStrategy, KeycloakRealmSetupStrategy)
- Exports sind minimal und alle notwendig

### lib.gen.java.core
**Status:** Alle Exports erforderlich

**Grund:**
- Alle Sub-Packages (bean, context, doc, element, naming) werden aktiv von Generator-Modulen verwendet
- Module: lib.gen.java.fx.comp, lib.gen.java.fx.bean, lib.gen.java.fx.tableview
- Codegenerierung benötigt Zugriff auf Implementierungsdetails

### lib.fx.core
**Status:** Alle Exports erforderlich

**Grund:**
- Alle control/* Sub-Packages werden von frontend.ui.fx Anwendung verwendet
- Wiederverwendbare UI-Komponenten (dialog, autocomplete, buttons, etc.)
- Legitime öffentliche API für JavaFX-Controls

### lib.util
**Status:** Alle Exports erforderlich

**Grund:**
- Sub-Packages (bimapped, json, classpath, etc.) werden von verschiedenen Modulen verwendet
- lib.jsonb verwendet bimapped und json
- lib.fx.comp verwendet classpath
- Alle Exports haben externe Verwendung

## JPMS Best Practices (aus dieser Analyse)

### ✅ Gute Beispiele im Projekt

1. **backend.common.mapping.jpa.dto**
   - Verwendet `qualified exports` für MapStruct-Implementation
   - Exports Mappings-Facade öffentlich
   - Qualifizierte Exports für interne Details nur an org.mapstruct

2. **lib.jpa.core** (nach Verbesserung)
   - Exportiert nur öffentliche API (core, criteria)
   - Hält Implementierungsdetails (criteria.restriction) verborgen
   - Klare Trennung zwischen API und Implementation

### Pattern für Package-Hiding

**Wann Package-Export entfernen:**
- Package wird nur intern (innerhalb des Moduls) verwendet
- Package enthält Implementierungsdetails, nicht API
- Package-Name deutet auf interne Verwendung hin (helper, util, impl, internal)

**Wann Package-Export behalten:**
- Package wird von anderen Modulen importiert
- Package enthält öffentliche API-Typen
- Package wird für CDI-Injection oder Framework-Integration benötigt

## Metriken

### Vorher
- Module mit unnötigen Exports: 1
- Unnötig exportierte Packages: 1
- Unnötig exponierte Klassen: 19

### Nachher
- Module mit unnötigen Exports: 0
- Unnötig exportierte Packages: 0
- Unnötig exponierte Klassen: 0

## Weitere Verbesserungsmöglichkeiten

### Architektur-Refactoring (größerer Scope)

**backend.persistence.jpa - Interface Extraction:**
```java
// Neues exportiertes Package: de.ruu.app.jeeeraaah.backend.persistence.api
public interface TaskGroupService { ... }
public interface TaskService { ... }

// Verschobenes Package: de.ruu.app.jeeeraaah.backend.persistence.jpa.internal
public class TaskGroupServiceJPA implements TaskGroupService { ... }
public class TaskServiceJPA implements TaskService { ... }

// module-info.java
exports de.ruu.app.jeeeraaah.backend.persistence.api;
// Kein Export für internal Package
opens de.ruu.app.jeeeraaah.backend.persistence.jpa.internal to weld.se.shaded;
```

**Vorteile:**
- Services sind über CDI zugänglich, aber Implementation ist verborgen
- Bessere Trennung von API und Implementation
- Ermöglicht zukünftig alternative Implementierungen

**Aufwand:** Hoch (Interface-Extraktion, Imports aktualisieren, Tests anpassen)

## Lessons Learned

1. **Systematische Analyse lohnt sich**
   - Grep-Suchen nach Package-Imports zeigen externe Verwendung
   - Module mit vielen Exports sind nicht automatisch schlecht (z.B. lib.gen.java.core)
   - Implementierungsdetails ohne externe Verwendung können sicher versteckt werden

2. **CDI und JPMS Interaktion**
   - CDI benötigt Typ-Visibility zur Compile-Zeit für @Inject
   - `opens` alleine reicht nicht für CDI-Injection (nur für Reflection)
   - Interface-basierte Injection ist der Weg zu besserer Kapselung mit CDI

3. **Build-Feedback ist essentiell**
   - Maven-Build zeigt sofort, ob Exports fehlen
   - Module-not-found Fehler sind eindeutig
   - Schnelles Feedback ermöglicht iterative Verbesserungen

## Referenzen

- [JPMS-PACKAGE-HIDING-STRATEGY.md](JPMS-PACKAGE-HIDING-STRATEGY.md) - Detaillierte Strategie-Dokumentation
- [module-info.java Specification](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7)
