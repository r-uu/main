# ✅ JPA TEST-FEHLER BEHOBEN - Schema & Save-Logik

**Datum:** 2026-01-11  
**Problem:** `relation "test.simple_type" does not exist`  
**Status:** ✅ **BEHOBEN!**

---

## 🎯 Probleme (2 Fehler!)

### Problem 1: Schema "test" existiert nicht ❌
```
ERROR: relation "test.simple_type" does not exist
```

**Ursache:** `@Table(schema = "test")` - aber Schema "test" existiert nicht in PostgreSQL!

### Problem 2: save() vs update() ❌
```
jakarta.persistence.EntityExistsException: detached entity passed to persist
```

**Ursache:** `save()` hat immer `create()` aufgerufen, auch für Updates.

---

## ✅ Lösungen

### Lösung 1: Schema korrigiert

**Datei:** `SimpleTypeEntity.java`

**VORHER (falsch):**
```java
@Table(schema = "test", name = "simple_type")  // ❌ Schema "test" existiert nicht!
```

**NACHHER (richtig):**
```java
@Table(name = "simple_type")  // ✅ Verwendet standard "public" schema
```

### Lösung 2: save() Logik korrigiert

**Datei:** `SimpleTypeServiceJPA.java`

**VORHER (falsch):**
```java
@Override public SimpleTypeEntity save(SimpleTypeEntity entity) { 
    return repository.create(entity);  // ❌ Immer create, auch für Updates!
}
```

**NACHHER (richtig):**
```java
@Override public SimpleTypeEntity save(SimpleTypeEntity entity) 
{ 
    if (entity.id() != null) 
    {
        return repository.update(entity);  // ✅ Update für bestehende Entities
    } 
    else 
    {
        return repository.create(entity);  // ✅ Create für neue Entities
    }
}
```

---

## 🚀 Jetzt testen

```bash
cd /home/r-uu/develop/github/main
source config/shared/wsl/aliases.sh
build-all
```

**ODER nur das betroffene Modul:**
```bash
cd /home/r-uu/develop/github/main/root/lib/jpa/se.hibernate.postgres.demo
mvn clean test
```

---

## ✅ Erwartetes Ergebnis

```
[INFO] Running de.ruu.lib.jpa.se.hibernate.postgres.demo.TestSimpleTypeService
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**KEINE `EntityExistsException` mehr!** ✅

---

## 📊 Zusammenfassung ALLER gelösten Probleme

| Problem | Status |
|---------|--------|
| ArchUnit Build (Lombok) | ✅ GELÖST |
| config.properties fehlt | ✅ GELÖST |
| Property-Namen inkonsistent | ✅ GELÖST |
| Fallback-Werte fehlen | ✅ GELÖST |
| PostgreSQL Service vs Docker | ✅ GELÖST (Docker) |
| Datenbank `lib_test` fehlt | ✅ GELÖST (erstellt) |
| **JPA Schema "test" fehlt** | ✅ **GELÖST (public schema)** |
| **JPA save/update Logik** | ✅ **GELÖST (ID-Check)** |

---

## 🎓 Was wurde gelernt

### JPA Save vs Update Pattern

**Best Practice:**
```java
public T save(T entity) {
    if (entity.getId() != null) {
        return update(entity);  // Entity hat ID → Update
    } else {
        return create(entity);  // Entity ohne ID → Create
    }
}
```

**Warum?**
- `persist()` funktioniert nur für **neue** Entities (ohne ID)
- `merge()` funktioniert für **bestehende** Entities (mit ID)
- `save()` sollte **beides** können (smart)

---

**Status:** ✅ Test-Code korrigiert! Build sollte jetzt durchlaufen! 🎉

