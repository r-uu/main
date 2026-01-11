# ❌ PROBLEM: port out of range:-1 IMMER NOCH!

**Das bedeutet:** Maven Resource Filtering funktioniert NICHT!

---

## 🔍 Schnelle Diagnose

```bash
cd /home/r-uu/develop/github/main
chmod +x config/shared/scripts/diagnose-filtering.sh
./config/shared/scripts/diagnose-filtering.sh
```

---

## ✅ FINALE LÖSUNG: Build vom Root-Verzeichnis

Das Problem ist wahrscheinlich, dass der Build nicht vom `main/` Verzeichnis gestartet wurde, daher findet Maven `config.properties` nicht!

**WICHTIG:** Baue **IMMER** so:

```bash
cd /home/r-uu/develop/github/main

# Option 1: Mit Alias
source config/shared/wsl/aliases.sh
build-all

# Option 2: Direkt
mvn -f bom/pom.xml clean install
mvn -f root/pom.xml clean install

# Option 3: Build-Skript
./config/shared/scripts/build-all.sh
```

**NICHT so bauen:**
```bash
cd /home/r-uu/develop/github/main/root
mvn clean install  # ❌ FALSCH! Findet config.properties nicht!
```

---

## 🔧 Alternative: Hardcoded Defaults (FALLBACK)

Falls Maven Resource Filtering partout nicht funktioniert, können wir Default-Werte direkt in die microprofile-config.properties schreiben:

### Variante A: Mit Fallback-Werten

```properties
# Maven versucht ${db.host} zu ersetzen
# Falls nicht erfolgreich, verwendet MicroProfile Config "localhost" als Default
database.host=${db.host:localhost}
database.port=${db.port:5432}
database.name=${db.database:lib_test}
database.user=${db.username:r_uu}
database.pass=${db.password:r_uu_password}
```

Soll ich das implementieren?

---

## 📝 Nächste Schritte

**1. Diagnose ausführen:**
```bash
cd /home/r-uu/develop/github/main
./config/shared/scripts/diagnose-filtering.sh
```

**2. Ergebnis analysieren:**
- Falls `${db.host} wurde NICHT ersetzt` → Maven Filtering Problem
- Falls `Properties wurden ersetzt` → Anderes Problem

**3. Build nochmal vom richtigen Verzeichnis:**
```bash
cd /home/r-uu/develop/github/main
source config/shared/wsl/aliases.sh
build-all
```

---

**BITTE AUSFÜHREN:**

```bash
cd /home/r-uu/develop/github/main
chmod +x config/shared/scripts/diagnose-filtering.sh
./config/shared/scripts/diagnose-filtering.sh
```

Dann zeige mir die Ausgabe!

