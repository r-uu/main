# JPMS Package-Hiding Strategie für bessere Kapselung

**Datum:** 2026-02-28  
**Ziel:** Public-Typen durch Package-Organisation verstecken

---

## 🎯 Grundprinzip

**JPMS ermöglicht:**
- Ein `public` Typ in einem **nicht-exportierten** Package ist **vollständig versteckt**
- Nur exportierte Packages sind von außen sichtbar
- Qualified Exports geben nur bestimmten Modulen Zugriff

### Beispiel:
```java
module my.module {
    // ✅ Öffentliche API
    exports com.example.api;
    
    // ❌ NICHT exportiert = vollständig versteckt
    // com.example.internal bleibt privat, auch wenn Klassen public sind!
    
    // ✅ Nur für spezifische Module
    exports com.example.impl to framework.module;
}
```

---

## 📦 Empfohlene Package-Struktur

### Pattern 1: API + Internal Pattern
```
my.module/
├── api/                    ← Exportiert (public API)
│   ├── MyService.java
│   └── MyDTO.java
├── internal/               ← NICHT exportiert (implementation)
│   ├── MyServiceImpl.java
│   └── MyHelper.java
└── spi/                    ← Qualified export (für Frameworks)
    └── MyExtension.java
```

**module-info.java:**
```java
module my.module {
    exports my.module.api;                    // Public API
    exports my.module.spi to framework;       // Nur für Framework
    // my.module.internal bleibt versteckt!
}
```

### Pattern 2: Facade Pattern (wie bei dir!)
```
mapping.module/
├── Mappings.java           ← Exportiert (Facade)
├── jpa.dto/               ← Qualified export (MapStruct)
│   └── MapperImpl.java
└── dto.jpa/               ← Qualified export (MapStruct)
    └── MapperImpl.java
```

**module-info.java:**
```java
module mapping.module {
    exports mapping.module;                           // Facade
    exports mapping.module.jpa.dto to org.mapstruct;  // Nur MapStruct
    exports mapping.module.dto.jpa to org.mapstruct;  // Nur MapStruct
}
```

---

## ✅ Bereits gut umgesetzt in deinem Projekt

### **backend.common.mapping.jpa.dto** ⭐ Best Practice!
```java
module de.ruu.app.jeeeraaah.backend.common.mapping.jpa.dto {
    // ✅ Nur Facade exportieren
    exports de.ruu.app.jeeeraaah.backend.common.mapping;
    
    // ✅ Mapper nur für MapStruct
    exports de.ruu.app.jeeeraaah.backend.common.mapping.jpa.dto 
        to org.mapstruct;
    
    // ✅ Reflection minimal
    opens de.ruu.app.jeeeraaah.backend.common.mapping 
        to weld.core.impl, weld.spi;
}
```

**Vorteile:**
- Clients nutzen nur `Mappings` (die Facade)
- Mapper-Implementierungen sind versteckt
- MapStruct kann generieren, aber niemand sonst sieht die Klassen

---

## 🔧 Verbesserungspotenzial

### 1. **backend.persistence.jpa** - Repositories verstecken

**Problem:**
```java
module de.ruu.app.jeeeraaah.backend.persistence.jpa {
    exports de.ruu.app.jeeeraaah.backend.persistence.jpa;  // ← Alles exportiert!
}
```

Aktuell exportiert:
- ✅ `TaskJPA.java` - braucht Export (für Mapping)
- ✅ `TaskGroupJPA.java` - braucht Export (für Mapping)
- ❌ `TaskRepositoryJPA.java` - sollte internal sein (nur via CDI)
- ❌ `TaskServiceJPA.java` - sollte internal sein (nur via CDI)

**Lösung: Package umstrukturieren**

#### Schritt 1: Neue Package-Struktur
```
backend.persistence.jpa/
├── TaskGroupJPA.java           ← API (Entity)
├── TaskJPA.java                ← API (Entity)
├── internal/                   ← NEU!
│   ├── TaskGroupRepositoryJPA.java
│   ├── TaskRepositoryJPA.java
│   ├── TaskGroupServiceJPA.java
│   └── TaskServiceJPA.java
└── ee/
    ├── TaskGroupServiceJPAEE.java
    └── ...
```

#### Schritt 2: module-info.java anpassen
```java
module de.ruu.app.jeeeraaah.backend.persistence.jpa {
    // Nur Entities exportieren
    exports de.ruu.app.jeeeraaah.backend.persistence.jpa;
    
    // Repositories bleiben versteckt!
    // (werden nur via @Inject genutzt)
    
    // Opens für CDI & JPA
    opens de.ruu.app.jeeeraaah.backend.persistence.jpa 
        to org.hibernate.orm.core, weld.se.shaded;
    opens de.ruu.app.jeeeraaah.backend.persistence.jpa.internal 
        to weld.se.shaded;
    opens de.ruu.app.jeeeraaah.backend.persistence.jpa.ee 
        to weld.se.shaded;
}
```

**Vorteile:**
- Repositories können nicht direkt importiert werden
- Nur CDI-Injection ist möglich
- Entities bleiben für Mapping verfügbar

---

### 2. **common.api.domain** - Exception Package gut!

**Status: ✅ Bereits sauber**

```java
module de.ruu.app.jeeeraaah.common.api.domain {
    exports de.ruu.app.jeeeraaah.common.api.domain;
    exports de.ruu.app.jeeeraaah.common.api.domain.exception;  // ✅ Gut
    exports de.ruu.app.jeeeraaah.common.api.domain.flat;
    exports de.ruu.app.jeeeraaah.common.api.domain.lazy;
}
```

Das Exception-Package ist korrekt exportiert, da es Teil der öffentlichen API ist.

---

### 3. **Weitere Kandidaten für Package-Hiding**

#### **lib.jpa.core** - criteria.restriction könnte internal sein

**Aktuell:**
```java
module de.ruu.lib.jpa.core {
    exports de.ruu.lib.jpa.core;
    exports de.ruu.lib.jpa.core.criteria;
    exports de.ruu.lib.jpa.core.criteria.restriction;  // Wirklich nötig?
}
```

**Prüfen:**
- Wird `criteria.restriction` direkt von außen genutzt?
- Oder nur intern via `criteria.Criteria`?

**Falls nur intern:**
```java
module de.ruu.lib.jpa.core {
    exports de.ruu.lib.jpa.core;
    exports de.ruu.lib.jpa.core.criteria;
    // criteria.restriction bleibt versteckt!
}
```

---

## 📋 Implementierungs-Checkliste

### **High Priority - Sofort umsetzbar**

- [ ] **backend.persistence.jpa**
  - [ ] Erstelle `internal/` Package
  - [ ] Verschiebe Repositories & Services
  - [ ] Update Imports in allen Service-Klassen
  - [ ] Update module-info.java
  - [ ] Test: Build muss durchlaufen

### **Medium Priority**

- [ ] **lib.jpa.core**
  - [ ] Prüfe externe Nutzung von `criteria.restriction`
  - [ ] Falls möglich: verstecke Package
  
- [ ] **Andere Module prüfen**
  - [ ] Suche nach weiteren `*Impl`, `*Internal`, `*Helper` Klassen
  - [ ] Verschiebe in `internal/` Packages

---

## 🎓 Best Practices Zusammenfassung

### ✅ DO
1. **Exportiere nur die minimal notwendige API**
   - Facades, Interfaces, DTOs
   
2. **Nutze qualified exports für Framework-Zugriff**
   ```java
   exports my.impl to org.mapstruct, weld.core;
   ```

3. **Organisiere Packages nach Sichtbarkeit**
   - `api/` → exportiert
   - `internal/` → nicht exportiert
   - `spi/` → qualified export

4. **Nutze `opens` statt `exports` für Reflection**
   ```java
   opens my.internal to lombok, jackson;  // Nicht exports!
   ```

### ❌ DON'T
1. **Nicht alles exportieren** "für alle Fälle"
2. **Nicht alle Subpackages exportieren** wenn die Parent-API ausreicht
3. **Nicht `opens ... to ALL-UNNAMED`** verwenden (schwächt Kapselung)

---

## 🔍 Analyse-Kommandos

### Finde alle exportierten Packages:
```bash
find . -name module-info.java -exec grep "exports" {} + | sort | uniq
```

### Finde public Klassen in einem Package:
```bash
grep -r "^public class" --include="*.java" src/main/java/com/example/internal/
```

### Prüfe wer ein Package nutzt:
```bash
grep -r "import com.example.internal" --include="*.java" .
```

---

## 📚 Weiterführende Ressourcen

- **Java Module System (JPMS) Spec:** https://openjdk.org/projects/jigsaw/spec/
- **Qualified Exports:** JEP 261
- **Strong Encapsulation:** https://www.oracle.com/java/technologies/javase/9-migration.html

---

## ✨ Zusammenfassung

**JPMS Package-Hiding ist mächtiger als `private`/`package-private`:**
- Funktioniert modulgrenzen-übergreifend
- Zwingt zu sauberer API-Design  
- Verhindert versehentliche Abhängigkeiten
- Ermöglicht sichere Refactorings

**Dein Projekt zeigt bereits gute Ansätze** (siehe `backend.common.mapping.jpa.dto`).
Die Hauptverbesserung wäre: **Repositories & Services in `internal/` verschieben**.
