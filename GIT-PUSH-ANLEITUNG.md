# Git Push Anleitung - WSL/IntelliJ Fix

## ⚠️ Problem: IntelliJ Git Askpass funktioniert nicht in WSL

Fehler:
```
/mnt/c/Users/r-uu/AppData/Local/JetBrains/IntelliJIdea2025.3/tmp/intellij-git-askpass-wsl-Ubuntu.sh: 3: 
/mnt/c/Users/r-uu/AppData/Local/Programs/IntelliJ IDEA/jbr/bin/java.exe: Exec format error
```

**Ursache:** IntelliJ versucht ein Windows-Java-Programm aus WSL zu starten.

## ✅ Lösung: Git im WSL-Terminal verwenden

### Schritt 1: IntelliJ Git Askpass deaktivieren

Öffnen Sie ein **neues WSL-Terminal** (nicht in IntelliJ!) und führen Sie aus:

```bash
cd /home/r-uu/develop/github/main

# IntelliJ Askpass deaktivieren
unset GIT_ASKPASS
unset SSH_ASKPASS

# Git Credential Helper konfigurieren
git config --global credential.helper store
```

### Schritt 2: Git Befehle ausführen

Im **gleichen WSL-Terminal**:

```bash
# Status prüfen
git status

# Alle Änderungen hinzufügen
git add -A

# Commit erstellen
git commit -m "Clean up: Remove JasperReports, consolidate to single InvoiceGenerator

- Removed JasperReports module (incompatible with Java 25)
- Consolidated InvoiceGenerator and InvoiceGeneratorAdvanced into single class
- InvoiceGenerator now uses automatic page calculation (~24 rows/page)
- Updated all documentation (English + German)
- Cleaned up temporary files and old run configurations
- JPMS fully enabled for docx4j module"

# Push zum Remote (wird nach Username/Password fragen)
git push
```

### Schritt 3: GitHub Credentials eingeben

Beim ersten `git push` werden Sie nach Credentials gefragt:

**Username:** Ihr GitHub Username  
**Password:** Ihr GitHub Personal Access Token (NICHT Ihr Passwort!)

> **Hinweis:** Falls Sie noch keinen Personal Access Token haben:
> 1. Gehen Sie zu https://github.com/settings/tokens
> 2. Generate new token (classic)
> 3. Wählen Sie Scope: `repo` (full control)
> 4. Kopieren Sie den Token und verwenden Sie ihn als Passwort

### Alternative: SSH verwenden

Falls Sie SSH-Keys bevorzugen:

```bash
# SSH-Key generieren (falls noch nicht vorhanden)
ssh-keygen -t ed25519 -C "ihre-email@example.com"

# Public Key anzeigen
cat ~/.ssh/id_ed25519.pub
# Kopieren Sie diesen Key und fügen Sie ihn zu GitHub hinzu:
# https://github.com/settings/keys

# Remote auf SSH umstellen
git remote set-url origin git@github.com:IHR-USERNAME/IHR-REPO.git

# Push
git push
```

## Was wurde geändert?

### Gelöscht:
- Komplettes `jasper/` Modul
- JasperReports Dependencies aus BOM
- Alte `InvoiceGenerator.java` (feste Zeilenzahl)
- Temporäre Dokumentationsdateien
- Alte Run Configurations

### Umbenannt:
- `InvoiceGeneratorAdvanced.java` → `InvoiceGenerator.java`

### Aktualisiert:
- `README.md` Dateien (Englisch + Deutsch)
- `pom.xml` Dateien
- IntelliJ Run Configurations

## Verifikation

Nach dem Push:
```bash
git log --oneline -1
# Sollte zeigen: "Clean up: Remove JasperReports..."
```

---

**Wichtig:** Verwenden Sie für Git-Operationen das **WSL-Terminal**, nicht IntelliJ's Git-Integration (wegen WSL-Inkompatibilität).

