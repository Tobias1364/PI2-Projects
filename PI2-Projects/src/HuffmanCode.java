import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

//Gruppennummer: 109

public class HuffmanCode {
	
	/** list of all chars in the file as Character */
	private char[] tokens;

	/** reads a file byte by byte and returns the file as a char[] */
	private static char[] scanFile(String filename) {
		ArrayList<Character> tokens = new ArrayList<Character>();
		try (FileInputStream fis = new FileInputStream(filename)) {
			int input;
			while ((input = fis.read()) != -1) {
				tokens.add((char) input);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Die angegebene Datei konnte nicht gefunden werden.");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		char[] result = new char[tokens.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = tokens.get(i);
		return result;
	}

	public static void main(String[] args) {
		try {
			HuffmanCode huffman = new HuffmanCode();
			huffman.tokens = scanFile(args[0]);

			// Berechnung der Haeufigkeit jedes Zeichens
			int[] freq = new int[256];

			for(char c : huffman.tokens){
				freq[(int) c]++;
			}

			//(i)
			System.out.println("Anzahl Zeichen\t\t\t  : " + huffman.tokens.length); // Wie viele Zeichen enthält die Datei?
			

			// (ii)
			int count = 0;
			for(int f : freq){
				if(f > 0) count++;
			}
			System.out.println("Anzahl verschiedener Zeichen\t  : " + count); // Nur die Zeichen, die mehr als 0-mal vorkommen, zählen

			// (iii)
			// für n verschiedene Zeichen braucht man mindestens log2(n) Bits pro Zeichen (b^2 >= n)
			int bitsProZeichen = 0;
			int moeglicheCodes = 1;

			while (moeglicheCodes < count) {
				bitsProZeichen++;
				moeglicheCodes = moeglicheCodes * 2;
			} // feststellen, wie viele Bits minimal pro Zeichen nötig sind, um alle verschiedenen Zeichen zu kodieren
			long festeLaenge = (long) huffman.tokens.length * bitsProZeichen;
			System.out.println("Kodierung mit fester Bitlaenge\t  : " + festeLaenge + " ("
					+ bitsProZeichen + " Bits pro Zeichen)");

			// (iv)
			ArrayList<Integer> liste = new ArrayList<Integer>(); // Liste der Haeufigkeiten, um den Huffman-Baum zu bauen
			for (int f : freq) {
				if (f > 0) {
					liste.add(f);
				}
			}

			long huffmanBits = 0;

			while (liste.size() > 1) {
				Collections.sort(liste); // sortieren, damit die beiden kleinsten Werte am Anfang stehen

				int a = liste.remove(0);
				int b = liste.remove(0); 

				int summe = a + b;
				huffmanBits += summe;

				liste.add(summe); // die Summe wird als neuer Knoten in die Liste eingefügt
			}		

			double bitsProZeichenHuffman = 0.0;
			if (huffman.tokens.length > 0) {
				bitsProZeichenHuffman = (double) huffmanBits / huffman.tokens.length;
			}

			System.out.println("Kodierung mit Huffman-Code\t  : " + huffmanBits + " ("
					+ String.format("%.2f", bitsProZeichenHuffman) + " Bits pro Zeichen)");

			// (v) i. und ii.
			if (huffman.tokens.length > 0) { // verhindert Division durch Null
				double basisBits = huffman.tokens.length * 8.0;
				double ersparnisFesteLaenge = (1.0 - festeLaenge / basisBits) * 100.0;
				double ersparnisHuffman    = (1.0 - huffmanBits / basisBits) * 100.0;

				System.out.println("Ersparnis (optimale feste Laenge) : "
						+ String.format("%.2f", ersparnisFesteLaenge) + "%");
				System.out.println("Ersparnis (Huffman-Code)\t  : "
						+ String.format("%.2f", ersparnisHuffman) + "%");
			} else {
				System.out.println("Ersparnis (optimale feste Laenge) : 0,00%");
				System.out.println("Ersparnis (Huffman-Code)\t  : 0,00%");
			}

			// (vi)		
			double entropie = 0.0; 
			for(int f : freq){
				if(f > 0){
					double p_i = (double) f / huffman.tokens.length;
					entropie -= p_i * Math.log(p_i) / Math.log(2);
				}
			}
			System.out.println("Entropie\t\t\t  : " + String.format("%.2f", entropie));

			// (vii)

			// Liste der Zeichen-Objekte
			ArrayList<Zeichen> zeichenListe = new ArrayList<Zeichen>();
			for (int i = 0; i < 256; i++) {
				if (freq[i] > 0) {
					zeichenListe.add(new Zeichen((char) i, huffman.tokens, freq));
				}
			}

			// Huffman-Wortlaengen berechnen
			ArrayList<ArrayList<Integer>> gruppen = new ArrayList<ArrayList<Integer>>(); // Liste der Gruppen von Zeichen-Indizes
			ArrayList<Integer> gruppenFreq = new ArrayList<Integer>(); // Liste der Haeufigkeiten der Gruppen

			for (int i = 0; i < zeichenListe.size(); i++) {
				ArrayList<Integer> gruppe = new ArrayList<Integer>();
				gruppe.add(i);

				gruppen.add(gruppe);
				gruppenFreq.add(zeichenListe.get(i).haeufigkeit);
			}

			
			while (gruppen.size() > 1) {
				int min1 = 0;
				int min2 = 1;

				// Gruppen mit den kleinsten Haeufigkeiten finden
				if (gruppenFreq.get(min2) < gruppenFreq.get(min1)) {
					int temp = min1;
					min1 = min2;
					min2 = temp;
				}

				
				for (int i = 2; i < gruppenFreq.size(); i++) {
					if (gruppenFreq.get(i) < gruppenFreq.get(min1)) {
						min2 = min1;
						min1 = i;
					} else if (gruppenFreq.get(i) < gruppenFreq.get(min2)) {
						min2 = i;
					}
				}

				// erhöhen der Wortlänge für alle Zeichen in den beiden Gruppen
				for (int idx : gruppen.get(min1)) {
					zeichenListe.get(idx).wortLaenge++;
				}
				for (int idx : gruppen.get(min2)) {
					zeichenListe.get(idx).wortLaenge++;
				}

				// neue Gruppe aus den beiden Gruppen bilden
				ArrayList<Integer> neueGruppe = new ArrayList<Integer>();
				neueGruppe.addAll(gruppen.get(min1));
				neueGruppe.addAll(gruppen.get(min2));

				int neueFreq = gruppenFreq.get(min1) + gruppenFreq.get(min2);

				if (min1 > min2) {
					gruppen.remove(min1);
					gruppen.remove(min2);
					gruppenFreq.remove(min1);
					gruppenFreq.remove(min2);
				} else {
					gruppen.remove(min2);
					gruppen.remove(min1);
					gruppenFreq.remove(min2);
					gruppenFreq.remove(min1);
				}

				gruppen.add(neueGruppe);
				gruppenFreq.add(neueFreq);
			}

			// Nach Haeufigkeit absteigend sortieren
			Collections.sort(zeichenListe, (a, b) -> b.haeufigkeit - a.haeufigkeit);

			System.out.println("Haeufigste Zeichen:");
			int limit = Math.min(10, zeichenListe.size());

			for (int i = 0; i < limit; i++) {
				Zeichen z = zeichenListe.get(i);
				System.out.println(z.toString() + ", Codewortlaenge: " + z.getWortLaenge());
			}
		} catch (ArrayIndexOutOfBoundsException aiobe) {
			System.out.println("Gueltiger Aufruf: java HuffmanCode datei");
		}

		
	}
	private static class Zeichen {
		char zeichen;
		int haeufigkeit;
		double relativeHaeufigkeit;
		int wortLaenge;

		public Zeichen(char zeichen, char[] tokens, int[] freq) {
			this.zeichen = zeichen;
			this.haeufigkeit = freq[(int) zeichen];
			this.relativeHaeufigkeit = (double) this.haeufigkeit / tokens.length * 100;
			this.wortLaenge = 0;
		}

		public int getWortLaenge() {
			return wortLaenge;
		}

		@Override
		public String toString() {
			
			String hex = String.format("0x%02x", (int) zeichen);

			// druckbares Zeichen: Wert >= 33 -> Zeichen ausgeben, sonst genau ein Leerzeichen
			String chPart = (zeichen >= 33) ? " " + zeichen : "  ";

			String percent = String.format(Locale.GERMAN, "%.1f", relativeHaeufigkeit);

			// Vorderer Teil des Formats (ohne Codewortlänge)
			return String.format("%s%s, Häufigkeit: %d (%s%%)",
					hex, chPart, haeufigkeit, percent);
		}
	}
}
