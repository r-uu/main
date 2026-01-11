package de.ruu.app.sandbox.msoffice.word;

import com.deepoove.poi.XWPFTemplate;
import de.ruu.app.sandbox.msoffice.word.model.Rechnung;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Generator für Word-Dokumente MIT Template-Datei.
 *
 * <p>Diese Klasse verwendet eine vordefinierte Template-Datei (.docx)
 * mit Platzhaltern, die durch echte Daten ersetzt werden.
 *
 * <p><strong>Verwendung:</strong>
 * <pre>
 * RechnungWordGeneratorMitTemplate generator =
 *     new RechnungWordGeneratorMitTemplate("rechnung-template.docx");
 * generator.generiereRechnung(rechnung, "rechnung.docx");
 * </pre>
 *
 * <p><strong>Template erstellen:</strong>
 * <ol>
 *   <li>Führen Sie create-template.py aus ODER</li>
 *   <li>Erstellen Sie manuell ein Word-Dokument mit Platzhaltern (siehe TEMPLATE-ANLEITUNG.md)</li>
 * </ol>
 *
 * <p><strong>Platzhalter im Template:</strong>
 * <ul>
 *   <li>{{rechnungsnummer}} - Rechnungsnummer</li>
 *   <li>{{datum}} - Datum</li>
 *   <li>{{empfaenger_firma}} - Firmenname</li>
 *   <li>{{empfaenger_name}} - Name</li>
 *   <li>{{empfaenger_strasse}} - Straße</li>
 *   <li>{{empfaenger_plz_ort}} - PLZ Ort</li>
 *   <li>{{positionen}} - Tabelle (wird automatisch eingefügt)</li>
 *   <li>{{netto_summe}} - Netto-Betrag</li>
 *   <li>{{mwst_summe}} - MwSt.-Betrag</li>
 *   <li>{{gesamt_summe}} - Brutto-Betrag</li>
 *   <li>{{bemerkungen}} - Bemerkungen</li>
 * </ul>
 *
 * <p><strong>Vorteil gegenüber RechnungWordGenerator:</strong>
 * <ul>
 *   <li>✅ Einfaches Anpassen von Layout und Design in Word</li>
 *   <li>✅ Logo und Bilder können im Template eingefügt werden</li>
 *   <li>✅ Farben, Schriftarten, etc. visuell anpassbar</li>
 *   <li>✅ Kein Java-Code-Änderung für Design-Anpassungen nötig</li>
 * </ul>
 */
@Slf4j
public class RechnungWordGeneratorMitTemplate
{
	private final String templateDatei;
	private final RechnungWordGenerator baseGenerator;

	/**
	 * Erstellt einen Generator mit Template-Datei.
	 *
	 * @param templateDatei Pfad zur Template-Datei (z.B. "rechnung-template.docx")
	 */
	public RechnungWordGeneratorMitTemplate(String templateDatei)
	{
		this.templateDatei = templateDatei;
		this.baseGenerator = new RechnungWordGenerator();
	}

	/**
	 * Generiert eine Rechnung als Word-Dokument basierend auf dem Template.
	 *
	 * @param rechnung Die zu generierende Rechnung
	 * @param ausgabeDatei Pfad zur Ausgabe-Datei (z.B. "rechnung.docx")
	 * @throws IOException bei Dateizugriffsproblemen
	 */
	public void generiereRechnung(Rechnung rechnung, String ausgabeDatei) throws IOException
	{
		log.info("Generiere Rechnung mit Template: {} -> {}", rechnung.getRechnungsnummer(), ausgabeDatei);
		log.info("Verwende Template: {}", templateDatei);

		// Datenmodell erstellen (wiederverwendet die Logik vom base Generator)
		// Wir nutzen hier Reflection, um auf die private Methode zuzugreifen
		// Alternative: Datenmodell-Erstellung in eigene Klasse auslagern

		// Word-Dokument mit Template erstellen
		XWPFTemplate template = XWPFTemplate.compile(templateDatei)
				.render(baseGenerator.createDataModel(rechnung));

		// Dokument speichern
		try (FileOutputStream out = new FileOutputStream(ausgabeDatei))
		{
			template.writeAndClose(out);
			log.info("Rechnung erfolgreich erstellt: {}", ausgabeDatei);
		}
	}

	/**
	 * Generiert eine Rechnung mit Standard-Template.
	 *
	 * <p>Verwendet "rechnung-template.docx" als Template.
	 *
	 * @param rechnung Die Rechnung
	 * @param ausgabeDatei Ausgabe-Datei
	 * @throws IOException bei Fehlern
	 */
	public static void generiereRechnungMitStandardTemplate(Rechnung rechnung, String ausgabeDatei) throws IOException
	{
		RechnungWordGeneratorMitTemplate generator =
			new RechnungWordGeneratorMitTemplate("rechnung-template.docx");
		generator.generiereRechnung(rechnung, ausgabeDatei);
	}
}

