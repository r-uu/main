# 📋 Quarterly Review Checklist - Dependencies & JPMS

**Frequenz:** Alle 3 Monate  
**Nächster Review:** 2026-04-11  
**Verantwortlich:** Tech Lead

---

## 🎯 Ziele

1. ✅ Automatic Modules minimieren
2. ✅ Dependencies aktuell halten
3. ✅ JPMS-Konformität sicherstellen
4. ✅ Security Updates einpflegen

---

## ✅ Checklist

### 1. Automatic Modules Review

- [ ] **MicroProfile Config API**
  - Aktuelle Version im Projekt: `3.2`
  - Neueste verfügbare Version: `_____`
  - JPMS Support verfügbar: ☐ Ja ☐ Nein
  - Action: ☐ Update ☐ Bleiben ☐ Alternative evaluieren

- [ ] **Jersey Client**
  - Aktuelle Version im Projekt: `3.1.6`
  - Neueste verfügbare Version: `_____`
  - JPMS Support verfügbar: ☐ Ja ☐ Nein
  - Action: ☐ Update ☐ Bleiben ☐ Alternative evaluieren

- [ ] **Keycloak Admin Client**
  - Aktuelle Version im Projekt: `26.0.7`
  - Neueste verfügbare Version: `_____`
  - JPMS Support verbessert: ☐ Ja ☐ Nein
  - Action: ☐ Update ☐ Bleiben ☐ Alternative evaluieren

### 2. Build Warnings Check

```bash
cd /home/r-uu/develop/github/main
mvn -f bom/pom.xml clean install -q
cd root && mvn clean compile 2>&1 | grep "\[WARNING\]" > /tmp/warnings-$(date +%Y%m%d).txt
```

- [ ] Warnings-Log generiert
- [ ] Neue Warnings identifiziert: ☐ Ja ☐ Nein
- [ ] Anzahl Warnings: Vorher `_____` → Jetzt `_____`
- [ ] Action Items erstellt: `_____`

### 3. Dependency Updates

**Kritische Dependencies:**

- [ ] **Lombok**
  - Aktuell: `1.18.42`
  - Neueste: `_____`
  - Breaking Changes: ☐ Ja ☐ Nein

- [ ] **MapStruct**
  - Aktuell: `1.6.3`
  - Neueste: `_____`
  - Breaking Changes: ☐ Ja ☐ Nein

- [ ] **Hibernate**
  - Aktuell: `6.6.5.Final`
  - Neueste: `_____`
  - Breaking Changes: ☐ Ja ☐ Nein

- [ ] **Jakarta EE**
  - Aktuell: `10.x` / `11.x`
  - Neueste: `_____`
  - Kompatibel: ☐ Ja ☐ Nein

### 4. Security Vulnerabilities

```bash
mvn org.owasp:dependency-check-maven:check
```

- [ ] Scan ausgeführt
- [ ] Kritische CVEs: `_____` (Anzahl)
- [ ] Hohe CVEs: `_____` (Anzahl)
- [ ] Alle behoben: ☐ Ja ☐ Nein ☐ Akzeptiert

### 5. JPMS Conformance

- [ ] **Module-Info Dateien:**
  - Anzahl: `47` (Baseline)
  - Neue Module: `_____`
  - Alle haben module-info.java: ☐ Ja ☐ Nein

- [ ] **Split Packages:**
  ```bash
  mvn clean compile 2>&1 | grep "split package"
  ```
  - Gefunden: ☐ Ja ☐ Nein
  - Anzahl: `_____`
  - Behoben: ☐ Ja ☐ In Arbeit ☐ Akzeptiert

- [ ] **Reflective Access Warnings:**
  - Gefunden: ☐ Ja ☐ Nein
  - Behoben/Dokumentiert: ☐ Ja ☐ Nein

### 6. Test Coverage

- [ ] **Build-Tests:**
  ```bash
  mvn clean install
  ```
  - Erfolgreich: ☐ Ja ☐ Nein
  - Übersprungene Tests: `_____`
  - Grund: `_____`

- [ ] **Integration Tests:**
  - PostgreSQL Tests: ☐ Laufen ☐ Übersprungen
  - Keycloak Tests: ☐ Laufen ☐ Übersprungen

### 7. Performance & Build Time

- [ ] **Build Duration:**
  - Vollständiger Build: `_____` min (Baseline: ~5-10 min)
  - Trend: ☐ Verbessert ☐ Gleich ☐ Verschlechtert

- [ ] **Artefakt Größen:**
  - Gesamt JAR Größe: `_____` MB
  - Größte Module: `_____`
  - Optimierungen möglich: ☐ Ja ☐ Nein

---

## 📊 Review Summary Template

```markdown
# Quarterly Dependency Review - Q[X] 2026

**Datum:** YYYY-MM-DD
**Reviewer:** [Name]

## Zusammenfassung

- **Automatic Modules:** [Anzahl] (Vorher: [Anzahl])
- **Updates durchgeführt:** [Anzahl]
- **Security Issues:** [Anzahl] behoben
- **Neue Warnings:** [Anzahl]

## Wichtigste Änderungen

1. [Dependency] updated: [Old] → [New]
2. [Problem] behoben
3. ...

## Action Items

- [ ] [Action 1]
- [ ] [Action 2]

## Nächster Review

**Datum:** YYYY-MM-DD
**Fokus:** [Special topics]
```

---

## 🔧 Tools & Scripts

### Dependency Analysis

```bash
# Dependency Tree
mvn dependency:tree > /tmp/dep-tree-$(date +%Y%m%d).txt

# Outdated Dependencies
mvn versions:display-dependency-updates

# Plugin Updates
mvn versions:display-plugin-updates

# Security Scan
mvn org.owasp:dependency-check-maven:check
```

### Automatic Module Detection

```bash
# Find all automatic modules
cd /home/r-uu/develop/github/main/root
mvn clean compile 2>&1 | grep "filename-based automodules" | sort | uniq
```

### JPMS Validation

```bash
# Check all module-info.java files exist
find . -name "pom.xml" -path "*/src/main/java/*" | while read pom; do
    dir=$(dirname "$pom")
    if [ ! -f "$dir/module-info.java" ]; then
        echo "Missing: $dir/module-info.java"
    fi
done
```

---

## 📅 Schedule

| Quarter | Start Date | Review Date | Focus |
|---------|------------|-------------|-------|
| Q1 2026 | 2026-01-01 | 2026-04-11 | Baseline + JPMS |
| Q2 2026 | 2026-04-01 | 2026-07-11 | Security + Updates |
| Q3 2026 | 2026-07-01 | 2026-10-11 | Performance |
| Q4 2026 | 2026-10-01 | 2027-01-11 | Year Review |

---

## ✅ Approval

**Reviewed by:** `_________________`  
**Date:** `_________________`  
**Next Review:** `_________________`  
**Status:** ☐ Approved ☐ Action Items ☐ Escalate

---

**Status:** Template bereit für Q2 2026 Review (2026-04-11)

