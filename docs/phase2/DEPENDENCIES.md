# Dependency-Graph - Mapper-Abhängigkeiten

**Datum**: 2026-02-14  
**Status**: ✅ VOLLSTÄNDIG  
**Zweck**: Reihenfolge für Phase 2 Module-Erstellung festlegen

---

## 📊 Modul-Abhängigkeiten (aktuell)

```
┌─────────────────────────────────────────┐
│  common.api.bean (TaskBean)             │
│  common.api.domain (TaskDTO)            │
│  common.api.ws.rs (TaskLazy, TaskFlat)  │
└────────────┬────────────────────────────┘
             │ depends on
             ▼
┌─────────────────────────────────────────┐
│  common.api.mapping.bean.dto            │◄─── ❌ MIXED!
│  ┌───────────────────────────────────┐  │     (Bean↔DTO + Lazy + Flat)
│  │ Bean ↔ DTO   (2 Mapper) ✅       │  │
│  │ Bean ↔ Lazy  (4 Mapper) ⚠️       │  │
│  │ Flat → Bean  (1 Mapper) ⚠️       │  │
│  └───────────────────────────────────┘  │
└────────────┬────────────────────────────┘
             │ used by
             ▼
┌─────────────────────────────────────────┐
│  frontend.api.client.ws.rs              │
│  (TaskServiceClient,                    │
│   TaskGroupServiceClient)               │
└────────────┬────────────────────────────┘
             │ depends on
             ▼
┌─────────────────────────────────────────┐
│  frontend.common.mapping.bean.fxbean    │
│  ┌───────────────────────────────────┐  │
│  │ Bean ↔ FXBean   (4 Mapper) ✅    │  │
│  │ Bean → FlatBean (1 Mapper) ⚠️    │  │
│  └───────────────────────────────────┘  │
└────────────┬────────────────────────────┘
             │ used by
             ▼
┌─────────────────────────────────────────┐
│  frontend.ui.fx                         │
│  (DashController, TaskController, etc.) │
└─────────────────────────────────────────┘
```

```
┌─────────────────────────────────────────┐
│  backend.jpa (TaskJPA)                  │
└────────────┬────────────────────────────┘
             │ depends on
             ▼
┌─────────────────────────────────────────┐
│  backend.common.mapping.jpa.dto         │◄─── ❌ MIXED!
│  ┌───────────────────────────────────┐  │     (JPA↔DTO + Lazy)
│  │ JPA ↔ DTO  (4 Mapper) ✅         │  │
│  │ JPA ↔ Lazy (4 Mapper) ⚠️         │  │
│  └───────────────────────────────────┘  │
└────────────┬────────────────────────────┘
             │ used by
             ▼
┌─────────────────────────────────────────┐
│  backend.api.ws.rs                      │
│  (TaskResource, TaskGroupResource)      │
└─────────────────────────────────────────┘
```

---

## 🎯 Modul-Abhängigkeiten (NACH Phase 2)

### Common API Mappings

```
┌─────────────────────────────────────────┐
│  common.api.bean (TaskBean)             │
│  common.api.domain (TaskDTO)            │
│  common.api.ws.rs (TaskLazy, TaskFlat)  │
└────┬─────────┬──────────┬───────────────┘
     │         │          │
     ▼         ▼          ▼
┌────────┐ ┌────────┐ ┌────────────┐
│ bean.  │ │ bean.  │ │ flat.bean  │
│ dto    │ │ lazy   │ │ (NEW)      │
│ (2)    │ │ (NEW)  │ │ (1)        │
│        │ │ (4)    │ │            │
└───┬────┘ └───┬────┘ └─────┬──────┘
    │          │            │
    └──────────┴────────────┘
             │ used by
             ▼
┌─────────────────────────────────────────┐
│  frontend.api.client.ws.rs              │
└─────────────────────────────────────────┘
```

### Backend Mappings

```
┌─────────────────────────────────────────┐
│  backend.jpa (TaskJPA)                  │
└────────────┬────────────┬───────────────┘
             │            │
             ▼            ▼
      ┌────────────┐ ┌─────────────┐
      │ jpa.dto    │ │ jpa.lazy    │
      │ (4)        │ │ (NEW)       │
      │            │ │ (4)         │
      └─────┬──────┘ └──────┬──────┘
            │               │
            └───────┬───────┘
                    │ used by
                    ▼
         ┌─────────────────────┐
         │ backend.api.ws.rs   │
         └─────────────────────┘
```

### Frontend Mappings

```
┌─────────────────────────────────────────┐
│  frontend.ui.fx.model (TaskFXBean)      │
└────────────┬────────────────────────────┘
             │ depends on
             ▼
┌─────────────────────────────────────────┐
│  frontend.common.mapping.bean.fxbean    │
│  ┌───────────────────────────────────┐  │
│  │ Bean ↔ FXBean   (4 Mapper) ✅    │  │
│  │ Bean → FlatBean (1 Mapper)       │  │
│  └───────────────────────────────────┘  │
└────────────┬────────────────────────────┘
             │ used by
             ▼
┌─────────────────────────────────────────┐
│  frontend.ui.fx (Controllers)           │
└─────────────────────────────────────────┘
```

---

## 🔗 Mapper-zu-Mapper Abhängigkeiten

### Keine direkten Abhängigkeiten!
✅ **Gut**: Kein Mapper ruft einen anderen Mapper auf (außer OptionalMapper aus lib.mapstruct)

### Verwendung in Business-Code

#### Frontend (frontend.api.client.ws.rs)
**TaskServiceClient.java**:
- Verwendet: Map_Task_DTO_Bean
- Verwendet: Map_Task_Bean_DTO (indirekt über toDTO)
- Verwendet: Map_Task_Bean_Lazy (für toLazy)

**TaskGroupServiceClient.java**:
- Verwendet: Map_TaskGroup_DTO_Bean
- Verwendet: Map_TaskGroup_Bean_DTO (indirekt über toDTO)

#### Frontend UI (frontend.ui.fx)
**DashController.java**:
- Verwendet: Map_TaskGroup_DTO_Bean
- Verwendet: Map_Task_DTO_Bean
- Verwendet: Map_TaskGroup_Bean_FXBean

**TaskManagementController.java**:
- Verwendet: Map_Task_Bean_FXBean
- Verwendet: Map_Task_FXBean_Bean

**TaskGroupManagementController.java**:
- Verwendet: Map_TaskGroup_Bean_FXBean
- Verwendet: Map_TaskGroup_FXBean_Bean

---

## 📋 Module-Erstellungs-Reihenfolge (Bottom-Up)

### Phase 2 - Schritt 1: Neue Module erstellen

**Reihenfolge** (keine Abhängigkeiten untereinander!):

1. **`common.api.mapping.flat.bean`** (ZUERST)
   - ✅ Keine Abhängigkeiten zu anderen Mappern
   - ✅ Nur: Bean + ws.rs (Flat)
   - ✅ Empfängt: 1 Mapper (Map_TaskGroup_Flat_Bean)

2. **`common.api.mapping.bean.lazy`** (PARALLEL möglich)
   - ✅ Keine Abhängigkeiten zu anderen Mappern
   - ✅ Nur: Bean + ws.rs (Lazy)
   - ✅ Empfängt: 4 Mapper (Map_*_Bean_Lazy, Map_*_Lazy_Bean)

3. **`backend.common.mapping.jpa.lazy`** (PARALLEL möglich)
   - ✅ Keine Abhängigkeiten zu anderen Mappern
   - ✅ Nur: JPA + ws.rs (Lazy)
   - ✅ Empfängt: 4 Mapper (Map_*_JPA_Lazy, Map_*_Lazy_JPA)

**Vorteil**: Alle 3 Module können parallel erstellt werden!

---

## ⚠️ Kritische Pfade

### 1. **Mappings.java Facade**
**Datei**: `common.api.mapping.bean.dto/src/main/java/.../Mappings.java`

**Imports (aktuell)**:
```java
import de.ruu.app.jeeeraaah.common.api.mapping.bean.dto.Map_TaskGroup_Bean_DTO;
import de.ruu.app.jeeeraaah.common.api.mapping.bean.dto.Map_Task_Bean_DTO;
import de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy.Map_TaskGroup_Bean_Lazy; // ⚠️ wird verschoben
import de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy.Map_Task_Bean_Lazy;     // ⚠️ wird verschoben
import de.ruu.app.jeeeraaah.common.api.mapping.dto.bean.Map_TaskGroup_DTO_Bean;
import de.ruu.app.jeeeraaah.common.api.mapping.dto.bean.Map_Task_DTO_Bean;
import de.ruu.app.jeeeraaah.common.api.mapping.flat.bean.Map_TaskGroup_Flat_Bean; // ⚠️ wird verschoben
import de.ruu.app.jeeeraaah.common.api.mapping.lazy.bean.Map_TaskGroup_Lazy_Bean; // ⚠️ wird verschoben
import de.ruu.app.jeeeraaah.common.api.mapping.lazy.bean.Map_Task_Lazy_Bean;      // ⚠️ wird verschoben
```

**Problem**: 
- ❌ Facade in `mapping.bean.dto` aber verwendet Mapper aus anderen Modulen
- ❌ Nach Verschiebung: Zirkuläre Abhängigkeit!

**Lösung (Phase 2)**:
1. **Option A**: Facade ganz löschen (Mapper direkt verwenden) ✅ EMPFOHLEN
2. **Option B**: Facade in separates Modul `common.api.mapping` (Overhead)
3. **Option C**: Pro Modul eigene Facade (z.B. `BeanDTOMappings`, `BeanLazyMappings`)

**Entscheidung**: Option A (siehe PHASE-2-NEUSTART-PLAN.md)

### 2. **Frontend API Client**
**Dateien**:
- `frontend.api.client.ws.rs/...TaskServiceClient.java`
- `frontend.api.client.ws.rs/...TaskGroupServiceClient.java`

**Verwendung**:
```java
// Aktuell: Über Facade
Mappings.toDTO(bean)
Mappings.toBean(dto)
Mappings.toLazy(bean)

// Nach Phase 2: Direkt
Map_Task_Bean_DTO.INSTANCE.map(bean)
Map_Task_DTO_Bean.INSTANCE.map(dto)
Map_Task_Bean_Lazy.INSTANCE.map(bean)
```

**Abhängigkeiten nach Phase 2**:
```xml
<dependency>
  <groupId>r-uu</groupId>
  <artifactId>r-uu.app.jeeeraaah.common.api.mapping.bean.dto</artifactId>
</dependency>
<dependency>
  <groupId>r-uu</groupId>
  <artifactId>r-uu.app.jeeeraaah.common.api.mapping.bean.lazy</artifactId>
</dependency>
```

---

## ✅ Validierungs-Checkliste

### Nach Modul-Erstellung
- [ ] pom.xml erstellt und in parent registriert
- [ ] module-info.java erstellt mit korrekten exports
- [ ] beans.xml erstellt (falls CDI)
- [ ] BOM aktualisiert
- [ ] `mvn clean compile -DskipTests` ✅

### Nach Mapper-Verschiebung
- [ ] `git mv` durchgeführt
- [ ] Package-Deklaration angepasst
- [ ] Imports in abhängigen Dateien aktualisiert
- [ ] module-info.java exports aktualisiert
- [ ] `mvn clean compile -DskipTests` ✅

### Nach Facade-Entfernung
- [ ] Mappings.java gelöscht
- [ ] Alle Aufrufe ersetzt durch direkte Mapper
- [ ] Imports in Business-Code aktualisiert
- [ ] `mvn clean compile -DskipTests` ✅
- [ ] `mvn clean install` ✅
- [ ] Apps starten ✅

---

## 📊 Zeitschätzung pro Schritt

### Modul erstellen (3x)
- Verzeichnisse + POMs: 15 min × 3 = **45 min**
- module-info.java: 10 min × 3 = **30 min**
- BOM Updates: **15 min**
- Build-Tests: 5 min × 3 = **15 min**
- **Summe**: **1h 45min**

### Mapper verschieben (9x)
- git mv + Package: 5 min × 9 = **45 min**
- Import-Updates: 10 min × 9 = **1h 30min**
- Build-Tests: 5 min × 9 = **45 min**
- **Summe**: **3h**

### Facade ersetzen
- Mappings.java löschen: **5 min**
- Aufrufe ersetzen: 15 min × ~20 Dateien = **5h**
- Import-Updates: **30 min**
- Build-Tests: **30 min**
- **Summe**: **6h**

### Gesamt
- **Best Case**: 9h
- **Realistic**: 11-13h (mit Debugging)
- **Worst Case**: 15h

---

**Status**: ✅ DEPENDENCY-GRAPH ABGESCHLOSSEN  
**Nächster Schritt**: PROGRESS.md mit Checkboxen erstellen  
**Zeitaufwand**: ~45 Minuten

