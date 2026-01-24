# 🎯 POSTGRESQL DATENBANK-PROBLEM - ENDGÜLTIG GELÖST

**Datum:** 2026-01-22  
**Problem:** Die `lib_test` Datenbank wurde beim Container-Start nicht automatisch angelegt  
**Status:** ✅ **DEFINITIV GELÖST**

---

## 🔍 PROBLEM-ANALYSE

### Warum trat das Problem auf?

PostgreSQL-Container führen Init-Skripte (`/docker-entrypoint-initdb.d/`) **NUR beim allerersten Start** aus, wenn das Volume noch leer ist.

**Wenn:**
- Der Container schon mal lief
- Das Volume bereits Daten enthält  
- Der Container neu gestartet wurde

**Dann:** Init-Skripte werden NICHT mehr ausgeführt → `lib_test` fehlt!

---

## ✅ DIE LÖSUNG

### 1. Automatisches Setup-Skript

**Datei:** `config/shared/docker/ensure-databases.sh`

- **Idempotent:** Kann jederzeit ausgeführt werden
- **Prüft** ob Datenbanken existieren
- **Erstellt** fehlende Datenbanken automatisch
- **Konfiguriert** Extensions (uuid-ossp, pg_trgm)

**Wichtig:** Verwendet `postgres` Superuser für DB-Erstellung (nicht `r_uu`)!

### 2. Integration in Startup

**Geändert:** `config/shared/wsl/aliases.sh`

```bash
ruu-startup() {
    # 1. Docker Container starten
    bash "$RUU_CONFIG/shared/scripts/startup-docker-services.sh"
    
    # 2. ALLE Datenbanken sicherstellen
    bash "$RUU_DOCKER/ensure-databases.sh"
    
    # ✅ Fertig!
}
```

### 3. Manueller Befehl

```bash
# Datenbanken jederzeit manuell prüfen/erstellen
ruu-postgres-setup
```

---

## 🎯 GARANTIERTE DATENBANKEN

Nach `ruu-startup` oder `ruu-postgres-setup` existieren:

### Container: postgres-jeeeraaah (Port 5432)
- ✅ `jeeeraaah` - Hauptanwendung
- ✅ `lib_test` - Library-Tests

### Container: postgres-keycloak (Port 5433)
- ✅ `keycloak` - Identity Management

---

## 🧪 VALIDIERUNG

### Test durchgeführt

```bash
cd /home/r-uu/develop/github/main/root/lib/jpa/se.hibernate
mvn clean test
```

**Ergebnis:** ✅ BUILD SUCCESS
- `EntityManagerFactoryProducerTest` läuft erfolgreich
- Verbindung zu `lib_test` funktioniert
- Keine Fehler mehr!

### Kompletter Build

```bash
cd /home/r-uu/develop/github/main/root
mvn clean install -DskipTests
```

**Ergebnis:** ✅ BUILD SUCCESS (02:46 min)

---

## 📝 WORKFLOW AB JETZT

### Täglich morgens

```bash
ruu-startup
```

**Das war's!**
- Alle Docker Container laufen
- Alle Datenbanken existieren
- Kein manuelles Setup mehr nötig

### Bei Problemen

```bash
# Datenbanken prüfen/reparieren
ruu-postgres-setup
```

### Nach Docker-Reset

```bash
# Automatisch! ruu-startup erstellt alles neu
ruu-startup
```

---

## 🔧 TECHNISCHE DETAILS

### Warum als `postgres` Superuser?

```bash
# ❌ FALSCH (r_uu hat keine CREATEDB-Rechte)
docker exec postgres-jeeeraaah psql -U r_uu -d postgres -c "CREATE DATABASE ..."
# ERROR: permission denied to create database

# ✅ RICHTIG
docker exec postgres-jeeeraaah psql -U postgres -d postgres -c "CREATE DATABASE ..."
# CREATE DATABASE
```

### Idempotenz

Das Skript kann beliebig oft ausgeführt werden:

```bash
# 1. Mal: Erstellt lib_test
ruu-postgres-setup
# ➕ Erstelle Datenbank 'lib_test'...

# 2. Mal: Tut nichts
ruu-postgres-setup
# ✓ Datenbank 'lib_test' existiert bereits
```

---

## 📚 DOKUMENTATION AKTUALISIERT

### Geänderte Dateien

1. **`config/QUICK-COMMANDS.md`**
   - Morgens-Routine dokumentiert
   - PostgreSQL-Sektion aktualisiert

2. **`config/shared/wsl/aliases.sh`**
   - `ruu-startup` erweitert
   - `ruu-postgres-setup` hinzugefügt

3. **`config/shared/docker/ensure-databases.sh`** (NEU)
   - Automatisches Datenbank-Setup
   - Idempotent & robust

4. **`root/app/jeeeraaah/frontend/ui/fx/src/main/java/module-info.java`**
   - `requires de.ruu.lib.docker.health` hinzugefügt

---

## 🎉 FAZIT

**Problem:** Datenbank `lib_test` fehlte regelmäßig  
**Ursache:** PostgreSQL Init-Skripte laufen nur beim ersten Start  
**Lösung:** Automatisches, idempotentes Setup-Skript  
**Status:** ✅ **DEFINITIV GELÖST**

**Ab jetzt:**
- `ruu-startup` → Alles läuft
- Kein manuelles Setup mehr
- Keine Build-Fehler mehr durch fehlende Datenbanken

---

**✨ Die Umgebung ist jetzt stabil und reproduzierbar! ✨**
