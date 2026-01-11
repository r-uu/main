package de.ruu.app.sandbox.msoffice.word;

import de.ruu.app.sandbox.msoffice.word.model.Adresse;
import de.ruu.app.sandbox.msoffice.word.model.Rechnung;
import de.ruu.app.sandbox.msoffice.word.model.Rechnungsposition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für {@link RechnungWordGenerator}.
 *
 * <p>Diese Tests prüfen:
 * <ul>
 *   <li>Erstellung von Word-Dokumenten</li>
 *   <li>Korrekte Berechnung von Summen</li>
 *   <li>Datei-Generierung</li>
 * </ul>
 */
class RechnungWordGeneratorTest
{
	@Test
	void testRechnungGenerierung(@TempDir Path tempDir) throws Exception
	{
		// Given: Eine vollständige Rechnung
		Adresse adresse = Adresse.builder()
				.firma("Test GmbH")
				.vorname("Test")
				.nachname("User")
				.strasse("Teststr. 1")
				.plz("12345")
				.ort("Teststadt")
				.build();

		Rechnungsposition pos1 = Rechnungsposition.builder()
				.positionsnummer(1)
				.beschreibung("Test-Artikel 1")
				.menge(2.0)
				.einheit("Stück")
				.einzelpreis(new BigDecimal("50.00"))
				.steuersatz(19.0)
				.build();

		Rechnungsposition pos2 = Rechnungsposition.builder()
				.positionsnummer(2)
				.beschreibung("Test-Artikel 2")
				.menge(1.0)
				.einheit("Pauschal")
				.einzelpreis(new BigDecimal("100.00"))
				.steuersatz(19.0)
				.build();

		Rechnung rechnung = Rechnung.builder()
				.rechnungsnummer("TEST-001")
				.datum(LocalDate.of(2026, 1, 4))
				.empfaenger(adresse)
				.positionen(Arrays.asList(pos1, pos2))
				.build();

		// When: Word-Dokument wird generiert
		File ausgabeDatei = tempDir.resolve("test-rechnung.docx").toFile();
		RechnungWordGenerator generator = new RechnungWordGenerator();
		generator.generiereRechnung(rechnung, ausgabeDatei.getAbsolutePath());

		// Then: Datei wurde erstellt
		assertTrue(ausgabeDatei.exists(), "Word-Dokument sollte erstellt worden sein");
		assertTrue(ausgabeDatei.length() > 0, "Word-Dokument sollte Inhalt haben");

		// And: Summen sind korrekt
		assertEquals(new BigDecimal("200.00"), rechnung.getNettoSumme());
		assertEquals(new BigDecimal("38.00"), rechnung.getMwStSumme());
		assertEquals(new BigDecimal("238.00"), rechnung.getGesamtsumme());
	}

	@Test
	void testRechnungsberechnungen()
	{
		// Given: Einfache Position
		Rechnungsposition position = Rechnungsposition.builder()
				.positionsnummer(1)
				.beschreibung("Test")
				.menge(5.0)
				.einheit("Stunden")
				.einzelpreis(new BigDecimal("80.00"))
				.steuersatz(19.0)
				.build();

		// When/Then: Berechnungen sind korrekt
		assertEquals(new BigDecimal("400.00"), position.getGesamtpreis());
		assertEquals(new BigDecimal("76.00"), position.getSteuerbetrag());
		assertEquals(new BigDecimal("476.00"), position.getBruttopreis());
	}

	@Test
	void testAdresseFormatierung()
	{
		// Given: Adresse mit allen Feldern
		Adresse adresse = Adresse.builder()
				.firma("Test GmbH")
				.vorname("Max")
				.nachname("Mustermann")
				.strasse("Teststr. 123")
				.plz("12345")
				.ort("Teststadt")
				.land("Deutschland")
				.build();

		// When: Vollständige Adresse wird abgerufen
		String vollstaendig = adresse.getVollstaendigeAdresse();

		// Then: Alle Felder sind enthalten
		assertTrue(vollstaendig.contains("Test GmbH"));
		assertTrue(vollstaendig.contains("Max Mustermann"));
		assertTrue(vollstaendig.contains("Teststr. 123"));
		assertTrue(vollstaendig.contains("12345 Teststadt"));
		assertTrue(vollstaendig.contains("Deutschland"));
	}
}

