package de.ruu.app.sandbox.msoffice.word;

import de.ruu.app.sandbox.msoffice.word.model.Adresse;
import de.ruu.app.sandbox.msoffice.word.model.Rechnung;
import de.ruu.app.sandbox.msoffice.word.model.Rechnungsposition;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Beispiel-Anwendung zur Demonstration der Word-Dokumentenerstellung.
 *
 * <p>Diese Klasse zeigt, wie man:
 * <ol>
 *   <li>Eine Adresse erstellt</li>
 *   <li>Rechnungspositionen definiert</li>
 *   <li>Eine vollständige Rechnung zusammenbaut</li>
 *   <li>Ein Word-Dokument generiert</li>
 * </ol>
 *
 * <p><strong>Ausführung:</strong>
 * <pre>
 * mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGenerator"
 * </pre>
 *
 * Oder in IntelliJ: Rechtsklick → Run 'BeispielRechnungGenerator.main()'
 */
@Slf4j
public class BeispielRechnungGenerator
{
	public static void main(String[] args)
	{
		log.info("=".repeat(60));
		log.info("Rechnung Word-Generator - Beispiel");
		log.info("=".repeat(60));

		try
		{
			// 1. Empfänger-Adresse erstellen
			Adresse empfaenger = erstelleBeispielAdresse();
			log.info("Adresse erstellt: {} {}", empfaenger.getVorname(), empfaenger.getNachname());

			// 2. Rechnungspositionen erstellen
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

			log.info("3 Rechnungspositionen erstellt");

			// 3. Rechnung zusammenbauen
			Rechnung rechnung = Rechnung.builder()
					.rechnungsnummer("R-2026-001")
					.datum(LocalDate.now())
					.empfaenger(empfaenger)
					.positionen(Arrays.asList(pos1, pos2, pos3))
					.bemerkungen("Zahlbar innerhalb von 14 Tagen ohne Abzug.\nVielen Dank für Ihren Auftrag!")
					.build();

			log.info("Rechnung erstellt: {}", rechnung.getRechnungsnummer());
			log.info("  Netto:  {}", rechnung.getNettoSumme());
			log.info("  MwSt.:  {}", rechnung.getMwStSumme());
			log.info("  Brutto: {}", rechnung.getGesamtsumme());

			// 4. Word-Dokument generieren
			String ausgabeDatei = "beispiel-rechnung.docx";
			RechnungWordGenerator generator = new RechnungWordGenerator();
			generator.generiereRechnung(rechnung, ausgabeDatei);

			log.info("=".repeat(60));
			log.info("✅ Erfolgreich! Word-Dokument erstellt: {}", ausgabeDatei);
			log.info("=".repeat(60));
		}
		catch (Exception e)
		{
			log.error("❌ Fehler beim Generieren der Rechnung", e);
		}
	}

	/**
	 * Erstellt eine Beispiel-Adresse für Demonstrations-Zwecke.
	 *
	 * @return Beispiel-Adresse
	 */
	private static Adresse erstelleBeispielAdresse()
	{
		return Adresse.builder()
				.firma("Musterfirma GmbH")
				.vorname("Max")
				.nachname("Mustermann")
				.strasse("Musterstraße 123")
				.plz("12345")
				.ort("Musterstadt")
				.land("Deutschland")
				.build();
	}
}

