package de.ruu.app.sandbox.msoffice.word;

import de.ruu.app.sandbox.msoffice.word.model.Adresse;
import de.ruu.app.sandbox.msoffice.word.model.Rechnung;
import de.ruu.app.sandbox.msoffice.word.model.Rechnungsposition;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Beispiel-Anwendung für Word-Dokumentenerstellung MIT Template.
 *
 * <p>Diese Klasse demonstriert die Verwendung eines Word-Templates
 * mit poi-tl Platzhaltern.
 *
 * <p><strong>Voraussetzung:</strong>
 * <pre>
 * 1. Erstellen Sie zuerst das Template:
 *    ./create-minimal-template.sh
 *
 * 2. Optional: Passen Sie rechnung-template.docx in Word an
 *    (Logo hinzufügen, Farben ändern, etc.)
 *
 * 3. Führen Sie dieses Beispiel aus
 * </pre>
 *
 * <p><strong>Ausführung:</strong>
 * <pre>
 * mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGeneratorMitTemplate"
 * </pre>
 */
@Slf4j
public class BeispielRechnungGeneratorMitTemplate
{
	private static final String TEMPLATE_DATEI = "rechnung-template.docx";
	private static final String AUSGABE_DATEI = "beispiel-rechnung-mit-template.docx";

	public static void main(String[] args)
	{
		log.info("=".repeat(60));
		log.info("Rechnung Word-Generator - MIT Template");
		log.info("=".repeat(60));

		try
		{
			// 1. Prüfen ob Template existiert
			if (!new File(TEMPLATE_DATEI).exists())
			{
				log.error("❌ Template nicht gefunden: {}", TEMPLATE_DATEI);
				log.info("");
				log.info("Bitte erstellen Sie zuerst das Template:");
				log.info("  ./create-minimal-template.sh");
				log.info("");
				log.info("Oder lesen Sie: TEMPLATE-ANLEITUNG.md");
				return;
			}

			log.info("✅ Template gefunden: {}", TEMPLATE_DATEI);

			// 2. Beispiel-Rechnung erstellen (gleiche Daten wie im anderen Beispiel)
			Rechnung rechnung = erstelleBeispielRechnung();
			log.info("Rechnung erstellt: {}", rechnung.getRechnungsnummer());
			log.info("  Positionen: {}", rechnung.getPositionen().size());
			log.info("  Gesamt: {}", rechnung.getGesamtsumme());

			// 3. Generator mit Template verwenden
			RechnungWordGeneratorMitTemplate generator =
				new RechnungWordGeneratorMitTemplate(TEMPLATE_DATEI);

			generator.generiereRechnung(rechnung, AUSGABE_DATEI);

			// 4. Erfolg
			log.info("=".repeat(60));
			log.info("✅ Erfolgreich! Word-Dokument mit Template erstellt:");
			log.info("   {}", AUSGABE_DATEI);
			log.info("=".repeat(60));
			log.info("");
			log.info("💡 Tipp: Öffnen Sie rechnung-template.docx in Word und passen");
			log.info("         Sie Layout, Logo, Farben etc. an. Die Änderungen werden");
			log.info("         bei der nächsten Generierung übernommen!");
		}
		catch (Exception e)
		{
			log.error("❌ Fehler beim Generieren der Rechnung", e);
		}
	}

	/**
	 * Erstellt eine Beispiel-Rechnung (identisch zum Beispiel ohne Template).
	 */
	private static Rechnung erstelleBeispielRechnung()
	{
		// Empfänger-Adresse
		Adresse empfaenger = Adresse.builder()
				.firma("Musterfirma GmbH")
				.vorname("Max")
				.nachname("Mustermann")
				.strasse("Musterstraße 123")
				.plz("12345")
				.ort("Musterstadt")
				.land("Deutschland")
				.build();

		// Rechnungspositionen
		Rechnungsposition pos1 = Rechnungsposition.builder()
				.positionsnummer(1)
				.beschreibung("Beratungsleistung Software-Entwicklung")
				.menge(8.0)
				.einheit("Stunden")
				.einzelpreis(new BigDecimal("95.00"))
				.steuersatz(19.0)
				.build();

		Rechnungsposition pos2 = Rechnungsposition.builder()
				.positionsnummer(2)
				.beschreibung("Code-Review und Qualitätssicherung")
				.menge(3.0)
				.einheit("Stunden")
				.einzelpreis(new BigDecimal("85.00"))
				.steuersatz(19.0)
				.build();

		Rechnungsposition pos3 = Rechnungsposition.builder()
				.positionsnummer(3)
				.beschreibung("Dokumentation (Technische Spezifikation)")
				.menge(1.0)
				.einheit("Pauschal")
				.einzelpreis(new BigDecimal("200.00"))
				.steuersatz(19.0)
				.build();

		// Rechnung zusammenbauen
		return Rechnung.builder()
				.rechnungsnummer("R-2026-001-TEMPLATE")
				.datum(LocalDate.now())
				.empfaenger(empfaenger)
				.positionen(Arrays.asList(pos1, pos2, pos3))
				.bemerkungen("Zahlbar innerhalb von 14 Tagen ohne Abzug.\nVielen Dank für Ihren Auftrag!")
				.build();
	}
}

