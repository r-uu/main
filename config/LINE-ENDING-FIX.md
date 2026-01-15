# ✅ Line Ending Probleme behoben

**Datum:** 2026-01-15

## 🔴 Das Problem

Shell-Skripte hatten Windows-Zeilenenden (CRLF) statt Unix-Zeilenenden (LF).

**Symptome:**
```bash
: command not found
: command not found
bash: line 50: syntax error near unexpected token `$'{\r''
```

Das `\r` zeigt ein Carriage Return (Windows CRLF).

---

## ✅ Die Lösung

### 1. Alle Shell-Skripte konvertiert (24 Dateien)

**Mit dos2unix:**
```bash
cd /home/r-uu/develop/github/main
bash fix-line-endings.sh
```

**Ergebnis:**
- ✓ 24 Shell-Skripte von CRLF → LF konvertiert
- ✓ aliases.sh funktioniert jetzt
- ✓ build-all funktioniert jetzt

---

### 2. .gitattributes erstellt

**Verhindert zukünftige CRLF-Probleme:**

Git konvertiert jetzt automatisch:
- `*.sh` → LF (Unix)
- `*.bat`, `*.ps1` → CRLF (Windows)
- Alle anderen Textdateien → LF bevorzugt

---

## 🎯 Jetzt funktioniert

### Aliases laden
```bash
cd /home/r-uu/develop/github/main
source config/shared/wsl/aliases.sh
```

**Output:**
```
✓ r-uu gemeinsame Aliase geladen
  Hilfe: ruu-help  Build: build-all  PostgreSQL Setup: ruu-postgres-setup
```

### Build-All ausführen
```bash
build-all
```

Funktioniert von jedem Verzeichnis aus!

---

## 📝 Warum passierte das?

**Windows vs. Linux Zeilenenden:**

| System | Zeilenende | Zeichen |
|--------|------------|---------|
| Windows | CRLF | `\r\n` |
| Linux/Unix | LF | `\n` |
| macOS | LF | `\n` |

**Problem:**
- IntelliJ/Git unter Windows erstellt manchmal CRLF
- WSL/Linux kann diese nicht interpretieren
- `\r` wird als ungültiges Zeichen gesehen

**Lösung:**
- Shell-Skripte MÜSSEN LF haben
- `.gitattributes` erzwingt dies automatisch

---

## 🔧 Verwendete Tools

| Tool | Zweck |
|------|-------|
| `dos2unix` | Konvertiert CRLF → LF |
| `.gitattributes` | Git-Konfiguration für Zeilenenden |
| `fix-line-endings.sh` | Batch-Konvertierung aller Skripte |

---

## 📚 Dateien erstellt/geändert

1. **fix-line-endings.sh** - Konvertierungs-Skript
2. **fix-line-endings.ps1** - PowerShell-Alternative (falls benötigt)
3. **.gitattributes** - Verhindert zukünftige Probleme
4. **LINE-ENDING-FIX.md** - Diese Dokumentation

---

## ✨ Nächste Schritte

### 1. PostgreSQL starten
```bash
sudo service postgresql start
# ODER
docker ps  # Prüfen ob Docker-PostgreSQL läuft
```

### 2. Build ausführen
```bash
cd /home/r-uu/develop/github/main
source config/shared/wsl/aliases.sh
build-all
```

### 3. docx4j-Beispiel testen
```bash
cd root/sandbox/office/microsoft/word/docx4j
./run-invoice-generator.sh
```

### 4. JasperReports-Beispiel testen
```bash
cd root/sandbox/office/microsoft/word/jasper
./run-invoice-generator.sh
```

---

## 🚨 Wichtig

**Nach Git Pull/Checkout:**

Falls nach einem `git pull` wieder CRLF-Probleme auftreten:

```bash
cd /home/r-uu/develop/github/main
bash fix-line-endings.sh
```

Die `.gitattributes` sollte dies aber verhindern!

---

**Problem gelöst! ✅**

