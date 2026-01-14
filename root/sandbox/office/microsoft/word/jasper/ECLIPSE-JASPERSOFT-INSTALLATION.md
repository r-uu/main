# Eclipse & Jaspersoft Studio Installation Guide
## Windows 11 + WSL + IntelliJ Integration

Diese Anleitung zeigt die **optimale Installation** von Eclipse und Jaspersoft Studio für Ihre hybride Windows/WSL-Umgebung mit IntelliJ als primärer IDE.

---

## 🎯 Ziel-Setup

```
Windows 11
├── IntelliJ IDEA                    ← Primäre IDE (Java-Entwicklung)
├── Eclipse 2024-12                  ← Basis für Jaspersoft Studio
│   └── Jaspersoft Studio Plugin     ← Visual Report Designer
└── WSL (Ubuntu)
    └── Projekt: /home/r-uu/develop/github/main
```

**Workflow**:
1. **IntelliJ**: Java-Code schreiben, Maven builds
2. **Eclipse/Jaspersoft**: `.jrxml` Templates visuell designen
3. **Beide**: Greifen auf dieselben Dateien im WSL-Dateisystem zu

---

## Teil 1: Eclipse Installation (Windows)

### Warum Eclipse auf Windows statt WSL?

✅ **Bessere GUI-Performance**  
✅ **Direkter Zugriff auf WSL-Dateien** via `\\wsl.localhost\`  
✅ **Keine X11-Server nötig**  
✅ **Native Windows-Integration**  

### Schritt 1.1: Java Runtime prüfen

Eclipse benötigt Java 17+ (wird mit Eclipse Installer mitgeliefert, aber wir nutzen GraalVM).

**In Windows PowerShell**:
```powershell
# Prüfen, ob GraalVM in WSL installiert ist
wsl bash -c "java -version"

# Erwartetes Ergebnis:
# openjdk version "25.0.1" 2025-04-15
# OpenJDK Runtime Environment GraalVM CE 25.0.1+...
```

**Wenn noch nicht installiert** (sollte aber von früher vorhanden sein):
```powershell
# In WSL
wsl bash -c "
  cd /tmp && \
  wget https://download.oracle.com/graalvm/25/latest/graalvm-jdk-25_linux-x64_bin.tar.gz && \
  tar -xzf graalvm-jdk-25_linux-x64_bin.tar.gz && \
  sudo mv graalvm-jdk-25.* /usr/lib/jvm/graalvm-25 && \
  sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/graalvm-25/bin/java 1 && \
  sudo update-alternatives --config java
"
```

### Schritt 1.2: Eclipse Installer herunterladen

**Option A: Eclipse Installer (Empfohlen)**

1. **Download**: https://www.eclipse.org/downloads/
2. **Datei**: `eclipse-inst-jre-win64.exe` (~55 MB)
3. **Speichern unter**: `C:\Temp\eclipse-inst-jre-win64.exe`

**Option B: Direkter Download (Alternative)**

1. **Download**: https://www.eclipse.org/downloads/packages/
2. **Paket**: "Eclipse IDE for Java Developers" (Windows x86_64)
3. **Größe**: ~450 MB

### Schritt 1.3: Eclipse installieren

**Mit Eclipse Installer** (Empfohlen):

1. **Installer starten**: `C:\Temp\eclipse-inst-jre-win64.exe`
2. **Paket wählen**: 
   - **"Eclipse IDE for Java Developers"** 
   - NICHT "Eclipse IDE for Enterprise Java and Web Developers" (zu groß)
3. **Installation Folder**: 
   ```
   C:\Program Files\Eclipse\eclipse-java-2024-12
   ```
4. **Workspace**: 
   ```
   C:\Users\<YourUser>\eclipse-workspace
   ```
   (NICHT im WSL-Pfad! Wird später verlinkt)
5. **JVM**: Installer-eigene JVM verwenden (wird automatisch gewählt)
6. **Install** klicken
7. **Warten** (~5 Minuten)

**Installation verifizieren**:

1. Eclipse starten (Verknüpfung auf Desktop oder Start-Menü)
2. **Workspace** Dialog: `C:\Users\<YourUser>\eclipse-workspace` bestätigen
3. **Welcome** Screen → Close
4. Eclipse sollte nun laufen

### Schritt 1.4: Eclipse für WSL konfigurieren

**JDK aus WSL in Eclipse verfügbar machen**:

Eclipse kann direkt auf WSL-Tools zugreifen, aber für bessere Performance nutzen wir eine Windows-JDK-Installation.

**Option A: GraalVM für Windows installieren** (Empfohlen):

```powershell
# In PowerShell (Administrator)
cd C:\Temp
Invoke-WebRequest -Uri "https://download.oracle.com/graalvm/25/latest/graalvm-jdk-25_windows-x64_bin.zip" -OutFile "graalvm-25-windows.zip"

# Entpacken
Expand-Archive -Path "graalvm-25-windows.zip" -DestinationPath "C:\Program Files\Java\"

# Umbenennen für einfachere Pfade
Rename-Item "C:\Program Files\Java\graalvm-jdk-25.*" "graalvm-25"
```

**In Eclipse konfigurieren**:

1. **Window → Preferences**
2. **Java → Installed JREs**
3. **Add... → Standard VM**
   - **JRE home**: `C:\Program Files\Java\graalvm-25`
   - **JRE name**: `GraalVM 25`
   - **Finish**
4. **☑ GraalVM 25** als Default markieren
5. **Apply and Close**

**Option B: Nur WSL-JDK nutzen** (Langsamer, aber keine doppelte Installation):

Eclipse kann via WSL auf das JDK zugreifen, aber das ist langsamer. Für reine Template-Bearbeitung ausreichend.

### Schritt 1.5: Eclipse Encoding auf UTF-8 setzen

**Wichtig für deutsche Umlaute in Templates!**

1. **Window → Preferences**
2. **General → Workspace**
3. **Text file encoding**: 
   - ⚪ Default (wird oft zu Windows-1252)
   - ⚫ **Other: UTF-8** ← Auswählen!
4. **Apply and Close**

---

## Teil 2: Jaspersoft Studio Plugin installieren

> **⚠️ Hinweis**: Beim ersten Start zeigt Jaspersoft Studio möglicherweise einen "Community Login" Dialog. Dies ist optional - klicken Sie einfach **Cancel** oder aktivieren Sie "Don't ask again". Siehe [Troubleshooting](#problem-jaspersoft-studio---connection-timeout-zu-heartbeatcloudjaspersoftcom) für Details.

### Schritt 2.1: Marketplace öffnen

1. **Help → Eclipse Marketplace**
2. **Search**: `Jaspersoft Studio`
3. **Find**: "Jaspersoft® Studio" (nicht "TIBCO Jaspersoft Studio")
4. **Install** klicken

**Alternative (bei Marketplace-Problemen)**:

1. **Help → Install New Software**
2. **Add...**
   - **Name**: `Jaspersoft Studio`
   - **Location**: `https://jasperstudio.sourceforge.net/eclipse/`
3. **OK**
4. **Select All** (alle Komponenten)
5. **Next → Next → Accept → Finish**

### Schritt 2.2: Installation durchführen

1. **Komponenten auswählen**:
   - ☑ Jaspersoft Studio
   - ☑ Jaspersoft Studio BIRT Integration (optional)
   - ☑ Jaspersoft Studio RHTML Support (optional)
   - ☑ All available drivers (JDBC, etc.)

2. **Security Warning**: "Unsigned content" → **Install anyway**

3. **Restart Eclipse** → Yes

### Schritt 2.3: Jaspersoft Studio Perspektive

Nach Neustart:

1. **Window → Perspective → Open Perspective → Other**
2. **Jaspersoft Studio** auswählen
3. **Open**

Sie sehen nun:
- **Palette** (rechts): Report-Elemente (Text Field, Image, etc.)
- **Outline** (links): Report-Struktur (Parameters, Variables, etc.)
- **Properties** (unten): Element-Eigenschaften

---

## Teil 3: WSL-Projekt in Eclipse einbinden

### Methode A: Direkter Zugriff auf WSL (Empfohlen)

Eclipse kann direkt auf `\\wsl.localhost\Ubuntu\...` zugreifen.

**Schritt 3.1: Workspace vorbereiten**

1. **File → Switch Workspace → Other**
2. **Workspace**: 
   ```
   \\wsl.localhost\Ubuntu\home\r-uu\eclipse-workspace
   ```
3. **Launch**

Eclipse erstellt jetzt Workspace-Metadaten direkt in WSL.

**Schritt 3.2: Projekt importieren**

1. **File → Import**
2. **Maven → Existing Maven Projects**
3. **Next**
4. **Root Directory**: 
   ```
   \\wsl.localhost\Ubuntu\home\r-uu\develop\github\main\root\sandbox\office\microsoft\word\jasper
   ```
5. **Browse** (oder direkt Pfad eintippen)
6. **Finish**

**Schritt 3.3: Maven konfigurieren**

Eclipse nutzt embedded Maven, aber für Konsistenz mit IntelliJ nutzen wir Maven Wrapper:

1. **Window → Preferences**
2. **Maven → Installations**
3. **Add...**
   - **Installation home**: `\\wsl.localhost\Ubuntu\home\r-uu\develop\github\main\root\.mvn\wrapper`
   - **Name**: `Maven Wrapper`
   - Funktioniert nur, wenn Maven Wrapper auf Windows zugreifen kann
   
**Alternative** (einfacher):

Eclipse nutzt embedded Maven nur für Dependency Resolution. Builds führen wir in IntelliJ oder Terminal aus.

### Methode B: Symbolischer Link (Alternative)

Falls `\\wsl.localhost\` langsam ist:

**In Windows Explorer**:

1. **Ordner erstellen**: `C:\Dev\Projects`
2. **In PowerShell (Administrator)**:
   ```powershell
   New-Item -ItemType SymbolicLink `
     -Path "C:\Dev\Projects\main" `
     -Target "\\wsl.localhost\Ubuntu\home\r-uu\develop\github\main"
   ```

**In Eclipse**:
- **Import** → Maven Project
- **Root**: `C:\Dev\Projects\main\root\sandbox\office\microsoft\word\jasper`

---

## Teil 4: JRXML-Template öffnen und bearbeiten

### Schritt 4.1: Template öffnen

**In Eclipse Project Explorer**:

1. Projekt `r-uu.sandbox.office.microsoft.word.jasper` aufklappen
2. `src/main/resources/templates/invoice_template.jrxml` **Doppelklicken**

Template öffnet sich im **Jaspersoft Studio Designer**:
- **Design-Tab**: Visueller Editor
- **Source-Tab**: XML-Code
- **Preview-Tab**: Vorschau mit Beispieldaten

### Schritt 4.2: Design-Ansicht erkunden

**Bands** (Bereiche):
- **Title**: Rechnungskopf (einmalig)
- **Page Header**: Wiederholung auf jeder Seite
- **Column Header**: Spaltenüberschriften
- **Detail**: Rechnungspositionen (Loop)
- **Page Footer**: Zwischensumme, Seitenzahl
- **Summary**: Gesamtsumme

**Elemente bearbeiten**:

1. **Static Text** doppelklicken → Text ändern
2. **Text Field** markieren → **Properties** (unten) → Expression ändern
3. **Neue Elemente**: **Palette** (rechts) → Element ziehen

### Schritt 4.3: Beispiel: Logo hinzufügen

1. **Logo-Datei vorbereiten**:
   - In WSL: `/home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/jasper/src/main/resources/logo.png`
   - Oder in Windows: `\\wsl.localhost\Ubuntu\home\r-uu\...\logo.png`

2. **In Jaspersoft Studio**:
   - **Palette → Image** 
   - In **Title-Band** ziehen (rechts oben)
   - **Properties → Image**:
     - **Image Expression**: `"logo.png"` (mit Anführungszeichen!)
     - **Scale Image**: `Retain Shape`

3. **Größe anpassen**: Element markieren → Anfasser ziehen

### Schritt 4.4: Beispiel: Zwischensumme anpassen

**Vorhandene Variable `PAGE_TOTAL` bearbeiten**:

1. **Outline (links) → Variables → PAGE_TOTAL** Rechtsklick → **Edit**
2. **Properties**:
   - **Class**: `java.math.BigDecimal`
   - **Calculation**: `Sum`
   - **Reset Type**: `Page` (bei jedem Seitenwechsel zurücksetzen)
   - **Variable Expression**: `$F{total}` (summiert Feld `total`)

**Variable im Page Footer anzeigen**:

1. **Page Footer Band** aufklappen
2. **Text Field** mit `$V{PAGE_TOTAL}` markieren
3. **Properties → Pattern**: `#,##0.00 €`

### Schritt 4.5: Vorschau mit Beispieldaten

**Preview-Tab nutzen**:

1. **Preview-Tab** klicken
2. **Compile & Preview** (Play-Button)
3. **Problem**: "No data adapter selected"

**Data Adapter erstellen**:

Für komplexe Daten ist es einfacher, die Main-Methode in IntelliJ zu nutzen.

**Alternative**: Preview in IntelliJ:

```bash
# In IntelliJ Terminal
cd /home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/jasper
mvn compile exec:java

# Öffnen:
target/rechnung_beispiel.docx
target/rechnung_beispiel.pdf
```

---

## Teil 5: Workflow Eclipse ↔ IntelliJ

### 5.1: Optimaler Workflow

```
┌─────────────────────────────────────────────────────┐
│ 1. TEMPLATE DESIGN (Eclipse/Jaspersoft)            │
│    - invoice_template.jrxml öffnen                  │
│    - Visuell bearbeiten (Logo, Farben, Layout)     │
│    - Variablen konfigurieren                        │
│    - Speichern (Ctrl+S)                             │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│ 2. JAVA-CODE (IntelliJ)                            │
│    - InvoiceGenerator.java bearbeiten              │
│    - Datenmodell anpassen                           │
│    - Maven Build: mvn compile exec:java             │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│ 3. PREVIEW (IntelliJ)                              │
│    - target/rechnung_beispiel.docx öffnen          │
│    - Ergebnis prüfen                                │
└─────────────────────────────────────────────────────┘
                        ↓
                   Zufrieden?
                   ↓        ↓
                  Ja       Nein → Zurück zu Schritt 1
                   ↓
                 FERTIG!
```

### 5.2: Dateisynchronisation

**Kein Problem!** Beide IDEs arbeiten auf denselben Dateien:

- **Eclipse ändert**: `invoice_template.jrxml`
- **IntelliJ sieht**: Änderung sofort (Auto-Reload)
- **Kein Git-Push nötig**: Lokal synchron

**Tipp**: Git-Ignore für Eclipse-Metadaten:

```bash
# In WSL Terminal
cd /home/r-uu/develop/github/main
echo ".project" >> .gitignore
echo ".classpath" >> .gitignore
echo ".settings/" >> .gitignore
echo "bin/" >> .gitignore
```

### 5.3: Maven Build nur in IntelliJ

**Eclipse nutzen wir NUR für Template-Design**, nicht für Builds.

**Warum?**
- ✅ IntelliJ hat bessere Maven-Integration
- ✅ Konsistente Build-Umgebung
- ✅ Maven Wrapper wird korrekt genutzt

**In Eclipse**: Template bearbeiten → Speichern  
**In IntelliJ**: Build → Preview

---

## Teil 6: Tipps & Tricks

### 6.1: Eclipse Performance optimieren

**eclipse.ini** anpassen (für große Templates):

Datei: `C:\Program Files\Eclipse\eclipse-java-2024-12\eclipse.ini`

```ini
-Xms512m     ← Von 256m erhöhen
-Xmx2048m    ← Von 1024m erhöhen (bei 16+ GB RAM)
```

Eclipse neu starten.

### 6.2: Jaspersoft Studio Shortcuts

| Shortcut | Aktion |
|----------|--------|
| `Ctrl+S` | Speichern |
| `Ctrl+Z` | Undo |
| `Ctrl+Y` | Redo |
| `Ctrl+D` | Duplicate Element |
| `Delete` | Element löschen |
| `F5` | Preview aktualisieren |
| `Alt+←/→` | Zwischen Tabs wechseln |

### 6.3: Häufige Design-Tasks

**Schriftart ändern**:
1. Element markieren
2. **Properties → Font**
3. **Font Name**: Arial, Times, etc.
4. **Size**: 10, 12, 14, etc.

**Farben ändern**:
1. Element markieren
2. **Properties → Forecolor**: Textfarbe
3. **Properties → Backcolor**: Hintergrundfarbe

**Element ausrichten**:
1. Mehrere Elemente markieren (Ctrl+Klick)
2. **Rechtsklick → Align → Left/Right/Top/Bottom**

**Gitter/Raster nutzen**:
1. **Window → Preferences**
2. **Jaspersoft Studio → Defaults → Grid**
3. **Show Grid**: ☑
4. **Snap to Grid**: ☑

### 6.4: Template-Versionierung

**Vor größeren Änderungen**:

```bash
# Backup erstellen
cp src/main/resources/templates/invoice_template.jrxml \
   src/main/resources/templates/invoice_template.jrxml.backup
```

Oder Git nutzen:

```bash
git add src/main/resources/templates/invoice_template.jrxml
git commit -m "Template: Logo und Farben angepasst"
```

---

## Teil 7: Troubleshooting

### Problem: XML-Validierungsfehler beim Öffnen des Templates

**Symptom**: 
```
net.sf.jasperreports.engine.JRException: org.xml.sax.SAXParseException; lineNumber: 207; columnNumber: 26; 
cvc-complex-type.2.4.a: Invalid content was found starting with element 'printWhenExpression'. 
One of 'box, textElement, textFieldExpression, ...' is expected.
```

**Ursache**: Die XML-Elemente in einem `<textField>` müssen in einer **strikten Reihenfolge** stehen:
1. `<reportElement>` (immer zuerst)
2. `<printWhenExpression>` (optional, aber wenn vorhanden, vor anderen Elementen)
3. `<textElement>` (optional)
4. `<textFieldExpression>` (Pflicht)

**Falsch** ❌:
```xml
<textField>
    <reportElement x="470" y="10" width="10" height="20"/>
    <textElement><font isBold="true"/></textElement>
    <printWhenExpression><![CDATA[$V{PAGE_NUMBER} < $V{PAGE_COUNT}]]></printWhenExpression>
    <textFieldExpression><![CDATA[$V{PAGE_NUMBER}]]></textFieldExpression>
</textField>
```

**Richtig** ✅:
```xml
<textField>
    <reportElement x="470" y="10" width="10" height="20"/>
    <printWhenExpression><![CDATA[$V{PAGE_NUMBER} < $V{PAGE_COUNT}]]></printWhenExpression>
    <textElement><font isBold="true"/></textElement>
    <textFieldExpression><![CDATA[$V{PAGE_NUMBER}]]></textFieldExpression>
</textField>
```

**Lösung**: Öffnen Sie die `.jrxml` Datei im Text-Editor und verschieben Sie `<printWhenExpression>` direkt nach `<reportElement>`.

**Tipp**: Jaspersoft Studio korrigiert solche Fehler manchmal automatisch. Öffnen Sie die Datei im **Source-Tab**, speichern Sie (`Ctrl+S`), und wechseln Sie zum **Design-Tab**.

---

### Problem: Jaspersoft Studio - Connection Timeout zu heartbeat.cloud.jaspersoft.com

**Symptom**: 
```
org.apache.http.conn.ConnectTimeoutException: Connect to heartbeat.cloud.jaspersoft.com:443 failed: Connection timed out
```

**Ursache**: Jaspersoft Studio versucht, sich mit der Jaspersoft Community Cloud zu verbinden (Telemetrie/Login-Feature). Dies ist **nicht erforderlich** für lokale Entwicklung.

**Lösung 1: Community Login Dialog schließen (Schnell)**

Beim Start von Jaspersoft Studio:
1. **Community Login Dialog** erscheint
2. **Cancel** oder **X** (Schließen) klicken
3. Optional: ☑ **"Don't ask me again"** aktivieren
4. Studio öffnet sich normal

**Lösung 2: Offline-Modus aktivieren (Dauerhaft)**

1. **Window → Preferences**
2. **Jaspersoft Studio → Properties**
3. ☑ **"Work Offline"** aktivieren
4. **Apply and Close**
5. Jaspersoft Studio neu starten

**Lösung 3: Heartbeat-Feature deaktivieren (Erweitert)**

Jaspersoft Studio Konfigurationsdatei bearbeiten:

**Linux/WSL**:
```bash
# Konfigurationsdatei öffnen
nano ~/.eclipse/com.jaspersoft.studio/configuration/config.ini

# Folgende Zeile hinzufügen:
jaspersoft.studio.heartbeat.enabled=false

# Speichern: Ctrl+O, Enter, Ctrl+X
```

**Windows**:
```
Datei: C:\Users\<YourUser>\.eclipse\com.jaspersoft.studio\configuration\config.ini

Zeile hinzufügen:
jaspersoft.studio.heartbeat.enabled=false
```

**Lösung 4: Proxy/Firewall umgehen (Wenn hinter Corporate Firewall)**

Falls Sie hinter einer Firewall sind:

1. **Window → Preferences**
2. **General → Network Connections**
3. **Active Provider**: Direct
4. **Apply and Close**

**Hinweis**: Diese Timeouts beeinträchtigen **nicht** die Funktionalität von Jaspersoft Studio für lokale Template-Entwicklung!

---

### Problem: Eclipse startet nicht

**Fehler**: "Failed to load the JNI shared library"

**Lösung**:
- Eclipse x64 benötigt JDK x64
- Prüfen: `java -version` (sollte 64-Bit sein)
- Falls 32-Bit JDK: Deinstallieren und 64-Bit installieren

### Problem: `\\wsl.localhost\` nicht erreichbar

**Symptom**: "Network path not found"

**Lösung**:
```powershell
# WSL neu starten
wsl --shutdown
wsl

# WSL-Integration prüfen
Get-Service -Name "LxssManager" | Restart-Service
```

### Problem: Jaspersoft Studio Plugin nicht gefunden

**Lösung**: Manuelle Installation

1. **Download**: https://sourceforge.net/projects/jasperstudio/files/
2. **Datei**: `com.jaspersoft.studio-<version>.zip`
3. **Eclipse → Help → Install New Software**
4. **Add → Archive** → `.zip` Datei auswählen

### Problem: Template-Änderungen werden nicht übernommen

**Ursache**: Maven cached kompilierte `.jasper` Datei

**Lösung**:
```bash
# In IntelliJ Terminal
cd /home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/jasper
mvn clean compile exec:java
```

### Problem: Umlaute (ä, ö, ü) werden nicht korrekt angezeigt

**Ursache**: Falsches Encoding

**Lösung in Eclipse**:
1. **Window → Preferences**
2. **General → Workspace → Text file encoding**
3. **Other: UTF-8** ← Sicherstellen!

**Lösung im Template**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
```
Muss in erster Zeile stehen.

### Problem: Preview zeigt "No Data"

**Erklärung**: Jaspersoft Studio benötigt Data Adapter für Preview

**Lösung**: Nutzen Sie IntelliJ für Preview (einfacher):
```bash
mvn compile exec:java
# Dann: target/rechnung_beispiel.pdf öffnen
```

---

## Teil 8: Zusammenfassung Installation

### ✅ Checkliste

- [ ] **Eclipse IDE for Java Developers** installiert
- [ ] **GraalVM 25** in Eclipse konfiguriert (Windows oder WSL)
- [ ] **Jaspersoft Studio Plugin** installiert
- [ ] **Jaspersoft Studio Perspektive** aktiviert
- [ ] **WSL-Projekt** in Eclipse importiert
- [ ] **invoice_template.jrxml** öffnet sich im Designer
- [ ] **Encoding** auf UTF-8 gesetzt
- [ ] **Git-Ignore** für Eclipse-Metadaten konfiguriert

### 📁 Dateistruktur nach Installation

```
Windows:
C:\
├── Program Files\
│   ├── Eclipse\
│   │   └── eclipse-java-2024-12\     ← Eclipse IDE
│   └── Java\
│       └── graalvm-25\                ← GraalVM (optional)
└── Users\<YourUser>\
    ├── eclipse-workspace\             ← Eclipse Workspace (Metadaten)
    └── .m2\                           ← Maven Local Repo

WSL:
/home/r-uu/
├── develop/github/main/
│   └── root/sandbox/office/microsoft/word/jasper/
│       ├── src/main/resources/templates/
│       │   └── invoice_template.jrxml  ← Template (von Eclipse bearbeitet)
│       ├── src/main/java/
│       │   └── ...                     ← Java-Code (von IntelliJ bearbeitet)
│       ├── target/
│       │   ├── rechnung_beispiel.docx  ← Generierte Rechnung
│       │   └── rechnung_beispiel.pdf
│       ├── .project                    ← Eclipse-Metadaten (Git-ignoriert)
│       └── .settings/                  ← Eclipse-Metadaten (Git-ignoriert)
└── eclipse-workspace/                 ← Alternativ: WSL Workspace
```

---

## 🎓 Nächste Schritte

### 1. Erste Template-Änderung testen

**In Eclipse**:
1. `invoice_template.jrxml` öffnen
2. **Title-Band → Static Text "RECHNUNG"** doppelklicken
3. Ändern zu: **"RECHNUNG / INVOICE"**
4. **Ctrl+S** speichern

**In IntelliJ**:
```bash
mvn compile exec:java
```

**Ergebnis prüfen**:
`target/rechnung_beispiel.docx` → Sollte neue Überschrift zeigen

### 2. Eigenes Template erstellen

**In Eclipse**:
1. **File → New → Jasper Report**
2. **Template**: Blank A4
3. **Name**: `my_invoice.jrxml`
4. **Location**: `src/main/resources/templates/`
5. **Finish**

Jetzt können Sie von Grund auf designen!

### 3. Weitere Tutorials

- **Jaspersoft Studio Video**: https://www.youtube.com/watch?v=Ub1BhYN2-o8
- **Report Variables**: https://community.jaspersoft.com/wiki/jasperreports-library-variables
- **Group Headers/Footers**: https://community.jaspersoft.com/wiki/jasperreports-groups

---

## 📞 Support

Bei Problemen:

1. **Eclipse Community**: https://www.eclipse.org/forums/
2. **Jaspersoft Community**: https://community.jaspersoft.com/
3. **Stack Overflow**: Tag `jasper-reports` + `eclipse`

---

## ✨ Zusammenfassung

Sie haben jetzt ein **optimales Setup** für Report-Entwicklung:

1. ✅ **Eclipse** als Visual Designer für `.jrxml` Templates
2. ✅ **Jaspersoft Studio** für WYSIWYG-Editing
3. ✅ **IntelliJ** bleibt primäre IDE für Java-Code
4. ✅ **Nahtlose Integration** via WSL-Dateisystem
5. ✅ **Klarer Workflow**: Design in Eclipse → Build in IntelliJ → Preview

**Viel Erfolg mit Ihrem Report-Designer! 🎨📊📄**

