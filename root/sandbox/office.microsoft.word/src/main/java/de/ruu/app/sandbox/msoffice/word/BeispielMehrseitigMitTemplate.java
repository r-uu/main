package de.ruu.app.sandbox.msoffice.word;

import de.ruu.app.sandbox.msoffice.word.model.Adresse;
import de.ruu.app.sandbox.msoffice.word.model.Rechnung;
import de.ruu.app.sandbox.msoffice.word.model.Rechnungsposition;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Beispiel für mehrseitige Rechnungen MIT Template-Unterstützung.
 *
 * <p>Demonstriert die Kombination von:
 * <ul>
 *   <li>Template-basiertem Design (in Word anpassbar)</li>
 *   <li>Mehrseitiger Funktionalität (Zwischensummen, Überträge)</li>
 * </ul>
 *
 * <p><strong>Voraussetzung:</strong>
 * <pre>
 * Template erstellen:
 *   ./create-mehrseitig-template.sh
 * </pre>
 *
 * <p><strong>Ausführung:</strong>
 * <pre>
 * mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielMehrseitigMitTemplate"
 * </pre>
 */
@Slf4j
public class BeispielMehrseitigMitTemplate
{
	private static final String TEMPLATE_DATEI = "rechnung-mehrseitig-template.docx";
	private static final String AUSGABE_DATEI = "beispiel-mehrseitig-mit-template.docx";

	public static void main(String[] args)
	{
		log.info("=".repeat(60));
		log.info("Mehrseitige Rechnung MIT Template - Beispiel");
		log.info("=".repeat(60));

		try
		{
			// 1. Template prüfen
			if (!new File(TEMPLATE_DATEI).exists())
			{
				log.error("❌ Template nicht gefunden: {}", TEMPLATE_DATEI);
				log.info("");
				log.info("Bitte erstellen Sie zuerst das mehrseitige Template:");
				log.info("  ./create-mehrseitig-template.sh");
				log.info("");
				return;
			}

			log.info("✅ Template gefunden: {}", TEMPLATE_DATEI);

			// 2. Rechnung mit 50 Positionen erstellen
			// Template unterstützt bis zu 4 Seiten, also max. 60 Positionen (15 pro Seite)
			Rechnung rechnung = erstelleRechnungMitVielenPositionen(60);

			log.info("Rechnung erstellt:");
			log.info("  Nummer: {}", rechnung.getRechnungsnummer());
			log.info("  Positionen: {}", rechnung.getPositionen().size());
			log.info("  Gesamt: {}", rechnung.getGesamtsumme());

			// 3. Generator konfigurieren
			MehrseitigeRechnungGeneratorMitTemplate generator =
				new MehrseitigeRechnungGeneratorMitTemplate(TEMPLATE_DATEI);

			generator.setZeilenProSeite(15);  // 15 Positionen pro Seite = 4 Seiten
			generator.setZwischensummenAnzeigen(true);

			// 4. Rechnung generieren
			generator.generiereRechnung(rechnung, AUSGABE_DATEI);

			log.info("=".repeat(60));
			log.info("✅ Erfolgreich! Mehrseitige Rechnung mit Template erstellt:");
			log.info("   {}", AUSGABE_DATEI);
			log.info("=".repeat(60));
			log.info("");
			log.info("💡 Das Dokument enthält:");
			log.info("   - {} Positionen auf 4 Seiten", rechnung.getPositionen().size());
			log.info("   - Template-basiertes Design");
			log.info("   - Zwischensummen am Ende jeder Seite");
			log.info("   - Überträge am Anfang der Folgeseiten");
			log.info("");
			log.info("📝 Template anpassen:");
			log.info("   1. Öffnen Sie {} in Word", TEMPLATE_DATEI);
			log.info("   2. Fügen Sie Logo ein, ändern Sie Farben, etc.");
			log.info("   3. Führen Sie dieses Beispiel erneut aus");
		}
		catch (Exception e)
		{
			log.error("❌ Fehler beim Generieren der Rechnung", e);
		}
	}

	/**
	 * Erstellt eine Beispiel-Rechnung mit vielen Positionen.
	 */
	private static Rechnung erstelleRechnungMitVielenPositionen(int anzahlPositionen)
	{
		// Empfänger
		Adresse empfaenger = Adresse.builder()
				.firma("Großkunde International GmbH")
				.vorname("Anna")
				.nachname("Schmidt")
				.strasse("Hauptstraße 789")
				.plz("98765")
				.ort("Berlin")
				.land("Deutschland")
				.build();

		// Viele Positionen generieren
		List<Rechnungsposition> positionen = new ArrayList<>();

		String[] artikel = {
			"Enterprise Software Lizenz",
			"Premium Support Paket",
			"Consulting Services",
			"Training Workshop",
			"Custom Development",
			"Code Review Service",
			"Performance Optimization",
			"Security Audit",
			"Documentation Service",
			"Deployment Support"
		};

		for (int i = 0; i < anzahlPositionen; i++)
		{
			String artikelName = artikel[i % artikel.length];
			double menge = 1.0 + (i % 5);
			BigDecimal preis = new BigDecimal(75 + (i * 11) % 150);

			Rechnungsposition pos = Rechnungsposition.builder()
					.positionsnummer(i + 1)
					.beschreibung(String.format("%s (Set %d)", artikelName, (i / artikel.length) + 1))
					.menge(menge)
					.einheit(i % 2 == 0 ? "Stunden" : "Stück")
					.einzelpreis(preis)
					.steuersatz(19.0)
					.build();

			positionen.add(pos);
		}

		// Rechnung zusammenbauen
		return Rechnung.builder()
				.rechnungsnummer("R-2026-MULTI-TEMPLATE-001")
				.datum(LocalDate.now())
				.empfaenger(empfaenger)
				.positionen(positionen)
				.bemerkungen(
					"Zahlbar innerhalb von 30 Tagen ohne Abzug.\n" +
					"Vielen Dank für Ihre Bestellung!\n\n" +
					"Dieses Dokument wurde mit Template erstellt und kann in Word angepasst werden."
				)
				.build();
	}
}

