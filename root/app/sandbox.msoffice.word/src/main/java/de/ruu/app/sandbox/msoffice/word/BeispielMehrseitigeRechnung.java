package de.ruu.app.sandbox.msoffice.word;

import de.ruu.app.sandbox.msoffice.word.model.Adresse;
import de.ruu.app.sandbox.msoffice.word.model.Rechnung;
import de.ruu.app.sandbox.msoffice.word.model.Rechnungsposition;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Beispiel für mehrseitige Rechnungen mit vielen Positionen.
 *
 * <p>Dieses Beispiel demonstriert:
 * <ul>
 *   <li>Rechnung mit 50+ Positionen (verteilt auf mehrere Seiten)</li>
 *   <li>Automatische Seitenumbrüche</li>
 *   <li>Zwischensummen am Seitenende</li>
 *   <li>Überträge auf Folgeseiten</li>
 * </ul>
 *
 * <p><strong>Ausführung:</strong>
 * <pre>
 * mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielMehrseitigeRechnung"
 * </pre>
 */
@Slf4j
public class BeispielMehrseitigeRechnung
{
	public static void main(String[] args)
	{
		log.info("=".repeat(60));
		log.info("Mehrseitige Rechnung - Beispiel");
		log.info("=".repeat(60));

		try
		{
			// 1. Rechnung mit vielen Positionen erstellen
			Rechnung rechnung = erstelleRechnungMitVielenPositionen(50);

			log.info("Rechnung erstellt:");
			log.info("  Nummer: {}", rechnung.getRechnungsnummer());
			log.info("  Positionen: {}", rechnung.getPositionen().size());
			log.info("  Gesamt: {}", rechnung.getGesamtsumme());

			// 2. Generator konfigurieren
			MehrseitigeRechnungGenerator generator = new MehrseitigeRechnungGenerator();
			generator.setZeilenProSeite(15);  // 15 Positionen pro Seite
			generator.setZwischensummenAnzeigen(true);

			// 3. Rechnung generieren
			String ausgabeDatei = "beispiel-mehrseitige-rechnung.docx";
			generator.generiereRechnung(rechnung, ausgabeDatei);

			log.info("=".repeat(60));
			log.info("✅ Erfolgreich! Mehrseitige Rechnung erstellt:");
			log.info("   {}", ausgabeDatei);
			log.info("=".repeat(60));
			log.info("");
			log.info("💡 Das Dokument enthält:");
			log.info("   - {} Positionen verteilt auf ca. {} Seiten",
					rechnung.getPositionen().size(),
					(rechnung.getPositionen().size() + 14) / 15);
			log.info("   - Zwischensummen am Ende jeder Seite");
			log.info("   - Überträge am Anfang der Folgeseiten");
			log.info("");
			log.info("📝 Anpassungen möglich in MehrseitigeRechnungGenerator:");
			log.info("   - setZeilenProSeite(n)           - Anzahl Zeilen pro Seite");
			log.info("   - setZwischensummenAnzeigen(b)   - Zwischensummen ein/aus");
		}
		catch (Exception e)
		{
			log.error("❌ Fehler beim Generieren der Rechnung", e);
		}
	}

	/**
	 * Erstellt eine Beispiel-Rechnung mit vielen Positionen.
	 *
	 * @param anzahlPositionen Anzahl zu erstellender Positionen
	 * @return Rechnung mit generierten Positionen
	 */
	private static Rechnung erstelleRechnungMitVielenPositionen(int anzahlPositionen)
	{
		// Empfänger
		Adresse empfaenger = Adresse.builder()
				.firma("Großkunde AG")
				.vorname("Maria")
				.nachname("Großmann")
				.strasse("Industriestraße 456")
				.plz("54321")
				.ort("Großstadt")
				.land("Deutschland")
				.build();

		// Viele Positionen generieren
		List<Rechnungsposition> positionen = new ArrayList<>();

		String[] artikel = {
			"Software-Lizenz Professional",
			"Wartungsvertrag Standard",
			"Support-Paket Premium",
			"Schulung Basis",
			"Schulung Advanced",
			"Beratungsstunde",
			"Code-Review",
			"Deployment-Service",
			"Monitoring-Paket",
			"Backup-Service"
		};

		String[] einheiten = {
			"Stück",
			"Monat",
			"Stunden",
			"Pauschal",
			"Tag"
		};

		for (int i = 0; i < anzahlPositionen; i++)
		{
			// Variiere Artikel, Preise und Mengen
			String artikel_name = artikel[i % artikel.length];
			String einheit = einheiten[i % einheiten.length];
			double menge = 1.0 + (i % 10);  // 1-10
			BigDecimal preis = new BigDecimal(50 + (i * 7) % 200);  // 50-250€

			Rechnungsposition pos = Rechnungsposition.builder()
					.positionsnummer(i + 1)
					.beschreibung(String.format("%s #%d", artikel_name, (i / artikel.length) + 1))
					.menge(menge)
					.einheit(einheit)
					.einzelpreis(preis)
					.steuersatz(19.0)
					.build();

			positionen.add(pos);
		}

		// Rechnung zusammenbauen
		return Rechnung.builder()
				.rechnungsnummer("R-2026-MULTI-001")
				.datum(LocalDate.now())
				.empfaenger(empfaenger)
				.positionen(positionen)
				.bemerkungen(
					"Zahlbar innerhalb von 30 Tagen ohne Abzug.\n" +
					"Vielen Dank für Ihren umfangreichen Auftrag!\n\n" +
					"Bei Fragen zu einzelnen Positionen kontaktieren Sie uns gerne."
				)
				.build();
	}

	/**
	 * Erstellt eine Test-Rechnung mit unterschiedlichen Seitengrößen.
	 */
	@SuppressWarnings("unused")
	private static void erstelleVerschiedeneSeitengroessen() throws Exception
	{
		Rechnung rechnung = erstelleRechnungMitVielenPositionen(75);
		MehrseitigeRechnungGenerator generator = new MehrseitigeRechnungGenerator();

		// Test 1: 10 Zeilen pro Seite (viele Seiten)
		generator.setZeilenProSeite(10);
		generator.generiereRechnung(rechnung, "test-10-zeilen-pro-seite.docx");

		// Test 2: 25 Zeilen pro Seite (Standard)
		generator.setZeilenProSeite(25);
		generator.generiereRechnung(rechnung, "test-25-zeilen-pro-seite.docx");

		// Test 3: 50 Zeilen pro Seite (wenige Seiten)
		generator.setZeilenProSeite(50);
		generator.generiereRechnung(rechnung, "test-50-zeilen-pro-seite.docx");

		// Test 4: Ohne Zwischensummen
		generator.setZeilenProSeite(20);
		generator.setZwischensummenAnzeigen(false);
		generator.generiereRechnung(rechnung, "test-ohne-zwischensummen.docx");
	}
}

