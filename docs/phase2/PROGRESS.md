# Phase 2 - Progress Tracking

**Start**: 2026-02-14  
**Status**: 🟡 VORBEREITUNG ABGESCHLOSSEN - BEREIT FÜR DURCHFÜHRUNG  
**Build-Status**: ✅ (Phase 1 - stabil)

---

## 📋 Vorbereitung (ABGESCHLOSSEN)

- [x] Git Rollback zu Phase 1 (Commit `00ee37d`)
- [x] Mapper-Inventur durchgeführt (20 Mapper gefunden)
- [x] Dependency-Graph erstellt
- [x] Progress-Tracking eingerichtet
- [x] Plan reviewt und verstanden

**Zeitaufwand**: ~2h  
**Dokumentation**: 
- ✅ INVENTORY.md
- ✅ DEPENDENCIES.md
- ✅ PROGRESS.md (diese Datei)

---

## 🏗️ SCHRITT 1: Module erstellen (0/3)

### 1.1 Modul `common.api.mapping.flat.bean` erstellen
- [ ] Verzeichnis erstellen: `root/app/jeeeraaah/common/api/mapping.flat.bean/`
- [ ] pom.xml erstellen
- [ ] In parent POM registrieren (`common/api/pom.xml`)
- [ ] module-info.java erstellen
  - [ ] `module de.ruu.app.jeeeraaah.common.api.mapping.flat.bean`
  - [ ] `requires de.ruu.app.jeeeraaah.common.api.bean;`
  - [ ] `requires de.ruu.app.jeeeraaah.common.api.ws.rs;`
  - [ ] `requires org.mapstruct;`
  - [ ] `exports de.ruu.app.jeeeraaah.common.api.mapping.flat.bean;`
- [ ] In BOM registrieren (`bom/pom.xml`)
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Create mapping.flat.bean module"
- [ ] **Git Tag**: `phase2-module-flat-bean-created`

**Geschätzt**: 30 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

### 1.2 Modul `common.api.mapping.bean.lazy` erstellen
- [ ] Verzeichnis erstellen: `root/app/jeeeraaah/common/api/mapping.bean.lazy/`
- [ ] pom.xml erstellen
- [ ] In parent POM registrieren (`common/api/pom.xml`)
- [ ] module-info.java erstellen
  - [ ] `module de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy`
  - [ ] `requires de.ruu.app.jeeeraaah.common.api.bean;`
  - [ ] `requires de.ruu.app.jeeeraaah.common.api.ws.rs;`
  - [ ] `requires org.mapstruct;`
  - [ ] `exports de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy;`
  - [ ] `exports de.ruu.app.jeeeraaah.common.api.mapping.lazy.bean;`
- [ ] In BOM registrieren (`bom/pom.xml`)
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Create mapping.bean.lazy module"
- [ ] **Git Tag**: `phase2-module-bean-lazy-created`

**Geschätzt**: 30 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

### 1.3 Modul `backend.common.mapping.jpa.lazy` erstellen
- [ ] Verzeichnis erstellen: `root/app/jeeeraaah/backend/common/mapping.jpa.lazy/`
- [ ] pom.xml erstellen
- [ ] In parent POM registrieren (`backend/common/pom.xml`)
- [ ] module-info.java erstellen
  - [ ] `module de.ruu.app.jeeeraaah.backend.common.mapping.jpa.lazy`
  - [ ] `requires de.ruu.app.jeeeraaah.backend.jpa;`
  - [ ] `requires de.ruu.app.jeeeraaah.common.api.ws.rs;`
  - [ ] `requires org.mapstruct;`
  - [ ] `requires jakarta.cdi;`
  - [ ] `exports de.ruu.app.jeeeraaah.backend.common.mapping.jpa.lazy;`
  - [ ] `exports de.ruu.app.jeeeraaah.backend.common.mapping.lazy.jpa;`
- [ ] beans.xml erstellen (`src/main/resources/META-INF/beans.xml`)
- [ ] In BOM registrieren (`bom/pom.xml`)
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Create mapping.jpa.lazy module"
- [ ] **Git Tag**: `phase2-module-jpa-lazy-created`

**Geschätzt**: 35 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

**SCHRITT 1 GESAMT**:  
**Geschätzt**: 1h 35min  
**Tatsächlich**: ___ min  
**Status**: ⬜ NICHT GESTARTET

---

## 📦 SCHRITT 2: Mapper verschieben (0/9)

### 2.1 Flat → Bean Mapper (1/1)
- [ ] **Map_TaskGroup_Flat_Bean** verschieben
  - [ ] `git mv` von `mapping.bean.dto/.../flat/bean/` nach `mapping.flat.bean/.../flat/bean/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` aktualisieren (temporär)
  - [ ] module-info.java in `mapping.bean.dto` aktualisieren (remove export)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_TaskGroup_Flat_Bean to mapping.flat.bean"

**Geschätzt**: 15 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

### 2.2 Bean ↔ Lazy Mapper (0/4)
- [ ] **Map_TaskGroup_Bean_Lazy** verschieben
  - [ ] `git mv` von `mapping.bean.dto/.../bean/lazy/` nach `mapping.bean.lazy/.../bean/lazy/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` aktualisieren (temporär)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_TaskGroup_Bean_Lazy to mapping.bean.lazy"

- [ ] **Map_Task_Bean_Lazy** verschieben
  - [ ] `git mv` von `mapping.bean.dto/.../bean/lazy/` nach `mapping.bean.lazy/.../bean/lazy/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` aktualisieren (temporär)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_Task_Bean_Lazy to mapping.bean.lazy"

- [ ] **Map_TaskGroup_Lazy_Bean** verschieben
  - [ ] `git mv` von `mapping.bean.dto/.../lazy/bean/` nach `mapping.bean.lazy/.../lazy/bean/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` aktualisieren (temporär)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_TaskGroup_Lazy_Bean to mapping.bean.lazy"

- [ ] **Map_Task_Lazy_Bean** verschieben
  - [ ] `git mv` von `mapping.bean.dto/.../lazy/bean/` nach `mapping.bean.lazy/.../lazy/bean/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` aktualisieren (temporär)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_Task_Lazy_Bean to mapping.bean.lazy"

**Geschätzt**: 1h  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

### 2.3 JPA ↔ Lazy Mapper (0/4)
- [ ] **Map_TaskGroup_JPA_Lazy** verschieben
  - [ ] `git mv` von `mapping.jpa.dto/.../jpa/lazy/` nach `mapping.jpa.lazy/.../jpa/lazy/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` (Backend) aktualisieren (falls vorhanden)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_TaskGroup_JPA_Lazy to mapping.jpa.lazy"

- [ ] **Map_Task_JPA_Lazy** verschieben
  - [ ] `git mv` von `mapping.jpa.dto/.../jpa/lazy/` nach `mapping.jpa.lazy/.../jpa/lazy/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` (Backend) aktualisieren (falls vorhanden)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_Task_JPA_Lazy to mapping.jpa.lazy"

- [ ] **Map_TaskGroup_Lazy_JPA** verschieben
  - [ ] `git mv` von `mapping.jpa.dto/.../lazy/jpa/` nach `mapping.jpa.lazy/.../lazy/jpa/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` (Backend) aktualisieren (falls vorhanden)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_TaskGroup_Lazy_JPA to mapping.jpa.lazy"

- [ ] **Map_Task_Lazy_JPA** verschieben
  - [ ] `git mv` von `mapping.jpa.dto/.../lazy/jpa/` nach `mapping.jpa.lazy/.../lazy/jpa/`
  - [ ] Package-Deklaration anpassen
  - [ ] Imports in `Mappings.java` (Backend) aktualisieren (falls vorhanden)
  - [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
  - [ ] Git Commit: "Move Map_Task_Lazy_JPA to mapping.jpa.lazy"

**Geschätzt**: 1h  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

**SCHRITT 2 GESAMT**:  
**Geschätzt**: 2h 15min  
**Tatsächlich**: ___ min  
**Status**: ⬜ NICHT GESTARTET

---

## 🔄 SCHRITT 3: Facade entfernen (0/1)

### 3.1 Mappings.java löschen
- [ ] Backend Mappings.java löschen (falls vorhanden)
- [ ] Common API Mappings.java löschen
- [ ] module-info.java aktualisieren (remove export für .mapping package)
- [ ] **BUILD TEST** (wird FEHLSCHLAGEN - erwartet!)
- [ ] Git Commit: "Remove Mappings facade - prepare for direct mapper usage"

**Geschätzt**: 10 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

### 3.2 Facade-Aufrufe ersetzen (Frontend API Client)

#### TaskServiceClient.java
- [ ] Imports aktualisieren
  - [ ] `import de.ruu.app.jeeeraaah.common.api.mapping.bean.dto.Map_Task_Bean_DTO;`
  - [ ] `import de.ruu.app.jeeeraaah.common.api.mapping.dto.bean.Map_Task_DTO_Bean;`
  - [ ] `import de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy.Map_Task_Bean_Lazy;`
- [ ] Ersetze `Mappings.toDTO(bean)` → `Map_Task_Bean_DTO.INSTANCE.map(bean)`
- [ ] Ersetze `Mappings.toBean(dto)` → `Map_Task_DTO_Bean.INSTANCE.map(dto)`
- [ ] Ersetze `Mappings.toLazy(bean)` → `Map_Task_Bean_Lazy.INSTANCE.map(bean)`
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Replace Mappings calls in TaskServiceClient"

**Geschätzt**: 30 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

#### TaskGroupServiceClient.java
- [ ] Imports aktualisieren
  - [ ] `import de.ruu.app.jeeeraaah.common.api.mapping.bean.dto.Map_TaskGroup_Bean_DTO;`
  - [ ] `import de.ruu.app.jeeeraaah.common.api.mapping.dto.bean.Map_TaskGroup_DTO_Bean;`
  - [ ] `import de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy.Map_TaskGroup_Bean_Lazy;`
- [ ] Ersetze `Mappings.toDTO(bean)` → `Map_TaskGroup_Bean_DTO.INSTANCE.map(bean)`
- [ ] Ersetze `Mappings.toBean(dto)` → `Map_TaskGroup_DTO_Bean.INSTANCE.map(dto)`
- [ ] Ersetze `Mappings.toLazy(bean)` → `Map_TaskGroup_Bean_Lazy.INSTANCE.map(bean)`
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Replace Mappings calls in TaskGroupServiceClient"

**Geschätzt**: 30 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

### 3.3 Facade-Aufrufe ersetzen (Frontend UI)

#### DashController.java
- [ ] Imports aktualisieren
- [ ] Ersetze Mappings-Aufrufe
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Replace Mappings calls in DashController"

**Geschätzt**: 20 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

#### TaskManagementController.java
- [ ] Imports aktualisieren
- [ ] Ersetze Mappings-Aufrufe
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Replace Mappings calls in TaskManagementController"

**Geschätzt**: 20 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

#### TaskGroupManagementController.java
- [ ] Imports aktualisieren
- [ ] Ersetze Mappings-Aufrufe
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Replace Mappings calls in TaskGroupManagementController"

**Geschätzt**: 20 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

#### Weitere Controller (falls vorhanden)
- [ ] Grep nach verbleibenden Mappings-Aufrufen
  ```bash
  grep -r "Mappings\." root/app/jeeeraaah --include="*.java" | grep -v "target/"
  ```
- [ ] Alle gefundenen Dateien aktualisieren
- [ ] **BUILD TEST**: `cd root && mvn clean compile -DskipTests`
- [ ] Git Commit: "Replace remaining Mappings calls"

**Geschätzt**: 30 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

**SCHRITT 3 GESAMT**:  
**Geschätzt**: 2h 30min  
**Tatsächlich**: ___ min  
**Status**: ⬜ NICHT GESTARTET

---

## ✅ SCHRITT 4: Validierung (0/5)

### 4.1 Build-Tests
- [ ] **Clean Compile**: `cd root && mvn clean compile -DskipTests`
- [ ] **Full Build**: `cd root && mvn clean install`
- [ ] **No Errors**: Keine Compile-Fehler
- [ ] **No Warnings**: Keine kritischen Warnungen

**Geschätzt**: 15 min  
**Tatsächlich**: ___ min  
**Build-Status**: ⬜

---

### 4.2 Funktionaler Test
- [ ] **DashAppRunner** starten
  - [ ] App startet ohne Fehler
  - [ ] Login funktioniert
  - [ ] TaskGroups werden geladen
  - [ ] Tasks werden angezeigt
  
- [ ] **GanttAppRunner** starten
  - [ ] App startet ohne Fehler
  - [ ] Login funktioniert
  - [ ] Gantt-Chart wird angezeigt
  - [ ] Filter funktionieren

**Geschätzt**: 30 min  
**Tatsächlich**: ___ min  
**Status**: ⬜

---

### 4.3 Code-Review
- [ ] Alle Mapper in korrekten Modulen
- [ ] Keine TemporaryHelper mehr vorhanden
- [ ] module-info.java exports korrekt
- [ ] POMs konsistent
- [ ] BOM vollständig

**Geschätzt**: 20 min  
**Tatsächlich**: ___ min  
**Status**: ⬜

---

### 4.4 Dokumentation
- [ ] INVENTORY.md aktualisieren (Status: ✅)
- [ ] DEPENDENCIES.md aktualisieren (Status: ✅)
- [ ] PROGRESS.md finalisieren (diese Datei)
- [ ] README.md aktualisieren (Modul-Struktur)

**Geschätzt**: 30 min  
**Tatsächlich**: ___ min  
**Status**: ⬜

---

### 4.5 Git Cleanup
- [ ] Alle Änderungen committed
- [ ] Aussagekräftige Commit-Messages
- [ ] **Final Tag**: `phase2-complete`
- [ ] Branch clean (kein uncommitted work)

**Geschätzt**: 10 min  
**Tatsächlich**: ___ min  
**Status**: ⬜

---

**SCHRITT 4 GESAMT**:  
**Geschätzt**: 1h 45min  
**Tatsächlich**: ___ min  
**Status**: ⬜ NICHT GESTARTET

---

## 📊 Gesamt-Status

### Zeitaufwand
| Phase | Geschätzt | Tatsächlich | Status |
|-------|-----------|-------------|--------|
| Vorbereitung | 2-3h | ~2h | ✅ ABGESCHLOSSEN |
| Schritt 1: Module | 1h 35min | ___ | ⬜ |
| Schritt 2: Verschieben | 2h 15min | ___ | ⬜ |
| Schritt 3: Facade | 2h 30min | ___ | ⬜ |
| Schritt 4: Validierung | 1h 45min | ___ | ⬜ |
| **GESAMT** | **10-12h** | **___** | ⬜ |

### Module-Status
- [x] common.api.mapping.bean.dto (existiert - Phase 1)
- [ ] common.api.mapping.bean.lazy (NEU - Phase 2)
- [ ] common.api.mapping.flat.bean (NEU - Phase 2)
- [x] backend.common.mapping.jpa.dto (existiert - Phase 1)
- [ ] backend.common.mapping.jpa.lazy (NEU - Phase 2)
- [x] frontend.common.mapping.bean.fxbean (existiert - Phase 1)

**Status**: 3/6 Module vorhanden

### Mapper-Status
- ✅ Bean ↔ DTO: 2/2 korrekt platziert
- ⬜ Bean ↔ Lazy: 0/4 verschoben
- ⬜ Flat → Bean: 0/1 verschoben
- ✅ JPA ↔ DTO: 4/4 korrekt platziert
- ⬜ JPA ↔ Lazy: 0/4 verschoben
- ✅ Bean ↔ FXBean: 4/4 korrekt platziert

**Status**: 10/20 Mapper korrekt platziert (50%)

---

## 🚨 Notfall-Prozedur

### Bei Build-Fehler
1. **STOPPEN** - nicht weitermachen
2. **Fehler kopieren** (vollständiger Stack Trace)
3. **Git Status prüfen**: `git status`
4. **Letzten funktionierenden Tag ermitteln**: `git tag`
5. **Rollback**: `git reset --hard TAG-NAME`
6. **Dokumentieren**: Fehler in ISSUES.md eintragen
7. **Hilfe holen**: Problem beschreiben

### Bei Unsicherheit
1. **Git Tag erstellen**: `git tag -a checkpoint-NAME -m "Beschreibung"`
2. **Build-Test**: `cd root && mvn clean compile -DskipTests`
3. **Pause machen** - 5-10 Minuten
4. **Review**: DEPENDENCIES.md nochmal lesen
5. **Weitermachen** oder **Hilfe holen**

---

**Erstellt**: 2026-02-14  
**Autor**: GitHub Copilot  
**Status**: 🟡 VORBEREITUNG KOMPLETT - BEREIT ZUM START  
**Nächster Schritt**: Schritt 1.1 (Modul `mapping.flat.bean` erstellen)

