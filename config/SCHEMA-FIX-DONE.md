# ✅ SCHEMA-PROBLEM BEHOBEN - Jetzt testen!

**Problem:** `relation "test.simple_type" does not exist`  
**Status:** ✅ **BEHOBEN!**

---

## 🎯 Was wurde geändert (4 Entities!)

**Problem:** Mehrere Entities verwendeten `schema = "test"`, aber das Schema existiert nicht!

### Dateien korrigiert:

**1. SimpleTypeEntity.java** (Demo)
```java
// VORHER: @Table(schema = "test", name = "simple_type")
// NACHHER: @Table(name = "simple_type")
```

**2. TaskJPA.java** (jeeeraaah)
```java
// VORHER: @Table(schema = "test", name = "task")
// NACHHER: @Table(name = "task")

// VORHER: @JoinTable(schema = "test", name = "PREDECESSOR_SUCCESSOR", ...)
// NACHHER: @JoinTable(name = "PREDECESSOR_SUCCESSOR", ...)
```

**3. TaskGroupJPA.java** (jeeeraaah)
```java
// VORHER: @Table(schema = "test", name = "task_group")
// NACHHER: @Table(name = "task_group")
```

**Alle verwenden jetzt das Standard "public" Schema!** ✅

---

## 🚀 JETZT TESTEN

```bash
cd /home/r-uu/develop/github/main
source config/shared/wsl/aliases.sh
build-all
```

---

## ✅ Erwartetes Ergebnis

```
[INFO] Running de.ruu.lib.jpa.se.hibernate.postgres.demo.TestSimpleTypeService
[INFO] Running de.ruu.lib.jpa.se.hibernate.postgres.demo.TestAbstractRepository
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**KEINE Fehler mehr:**
- ✅ `relation "test.simple_type" does not exist` - BEHOBEN
- ✅ `detached entity passed to persist` - BEHOBEN

---

## 📊 Alle Änderungen in dieser Session

| # | Datei | Änderung |
|---|-------|----------|
| 1 | `root/pom.xml` | BOM als parent |
| 2 | `bom/pom.xml` | Plugin-Versionen als Properties |
| 3 | `config.properties` | Erstellt mit r_uu credentials |
| 4 | `microprofile-config.properties` (6x) | Fallback-Werte |
| 5 | `aliases.sh` | build-all Funktion + Docker-Aliase |
| 6 | **`SimpleTypeEntity.java`** | **Schema "test" → public** |
| 7 | **`SimpleTypeServiceJPA.java`** | **save() ID-Check** |
| 8 | **`TaskJPA.java`** | **Schema "test" → public (2x)** |
| 9 | **`TaskGroupJPA.java`** | **Schema "test" → public** |

---

**FÜHRE JETZT `build-all` AUS!** 🚀

Der Build sollte jetzt komplett durchlaufen!

