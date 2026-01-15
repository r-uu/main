# Git Push Problem behoben

## Problem
IntelliJ versuchte, ein Windows-Java-Executable (`java.exe`) aus WSL heraus für Git-Authentifizierung aufzurufen, was fehlschlug.

## Ursache
- Mehrfache `core.askpass` Einträge in Git-Konfiguration
- IntelliJ-spezifische askpass-Helper zeigten auf Windows-Pfade
- Credential Helper waren inkonsistent konfiguriert

## Lösung
Bereinigte Git-Konfiguration:

```bash
# Entfernt alle problematischen askpass Einträge
git config --global --unset-all core.askpass

# Verwendet GitHub CLI als Credential Helper
git config --global credential.https://github.com.helper '!gh auth git-credential'

# HTTPS Remote-URL (wie vorher)
git remote set-url origin https://github.com/r-uu/main.git
```

## Aktuelle Konfiguration

```
credential.helper=store
credential.https://github.com.helper=!gh auth git-credential
```

## Status

✅ Git Push funktioniert wieder in IntelliJ
✅ Keine Passwort-Abfrage mehr
✅ Verwendet bestehende GitHub CLI Authentifizierung
✅ HTTPS wie vorher (keine SSH-Umstellung notwendig)

## Test

```bash
git push origin main
# Output: Everything up-to-date
```

**Alles funktioniert wieder wie gewohnt!**

