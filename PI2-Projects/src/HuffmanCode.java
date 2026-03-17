import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

//Gruppennummer: 109

// ---------------------------------------------------------------------------
// Hilfsklasse: Knoten des Huffman-Baums
// ---------------------------------------------------------------------------
class HuffmanNode {
	char c;
	int freq;
	HuffmanNode left, right;

	HuffmanNode(char c, int freq) {
		this.c = c;
		this.freq = freq;
	}

	HuffmanNode(int freq, HuffmanNode left, HuffmanNode right) {
		this.freq = freq;
		this.left = left;
		this.right = right;
	}

	boolean isLeaf() {
		return left == null && right == null;
	}
}

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

	// Huffman-Baum aus den Haeufigkeiten aufbauen
	private static HuffmanNode buildTree(int[] freq) {
		PriorityQueue<HuffmanNode> pq = new PriorityQueue<HuffmanNode>(
				Comparator.comparingInt((HuffmanNode n) -> n.freq)
						.thenComparingInt(n -> (int) n.c));

		for (int i = 0; i < freq.length; i++) {
			if (freq[i] > 0) {
				pq.add(new HuffmanNode((char) i, freq[i]));
			}
		}

		if (pq.isEmpty()) {
			return null;
		}

		if (pq.size() == 1) {
			HuffmanNode only = pq.poll();
			return new HuffmanNode(only.freq, only, null);
		}

		while (pq.size() > 1) {
			HuffmanNode a = pq.poll();
			HuffmanNode b = pq.poll();
			pq.add(new HuffmanNode(a.freq + b.freq, a, b));
		}

		return pq.poll();
	}

	// rekursiv Codewortlaengen im Baum setzen
	private static void fillCodeLengths(HuffmanNode node, int depth, int[] codeLengths) {
		if (node == null) {
			return;
		}

		if (node.isLeaf()) {
			codeLengths[(int) node.c] = (depth == 0) ? 1 : depth;
			return;
		}

		fillCodeLengths(node.left, depth + 1, codeLengths);
		fillCodeLengths(node.right, depth + 1, codeLengths);
	}

	public static void main(String[] args) {
		try {
			if (args.length == 3 && args[0].equals("-e")) {
				encodeOptional(args[1], args[2]);
				return;
			}

			if (args.length == 3 && args[0].equals("-d")) {
				decodeOptional(args[1], args[2]);
				return;
			}

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
			int[] codeLengths = new int[256];
			HuffmanNode root = buildTree(freq);
			fillCodeLengths(root, 0, codeLengths);

			long huffmanBits = 0;
			for (int i = 0; i < 256; i++) {
				if (freq[i] > 0) {
					huffmanBits += (long) freq[i] * codeLengths[i];
				}
			}

			double bitsProZeichenHuffman = 0.0;
			if (huffman.tokens.length > 0) {
				bitsProZeichenHuffman = (double) huffmanBits / huffman.tokens.length;
			}

			System.out.println("Kodierung mit Huffman-Code\t  : " + huffmanBits + " ("
					+ String.format(Locale.GERMAN, "%.2f", bitsProZeichenHuffman) + " Bits pro Zeichen)");

			// (v) i. und ii.
			if (huffman.tokens.length > 0) { // verhindert Division durch Null
				double basisBits = huffman.tokens.length * 8.0;
				double ersparnisFesteLaenge = (1.0 - festeLaenge / basisBits) * 100.0;
				double ersparnisHuffman    = (1.0 - huffmanBits / basisBits) * 100.0;

				System.out.println("Ersparnis (optimale feste Laenge) : "
						+ String.format(Locale.GERMAN, "%.2f", ersparnisFesteLaenge) + "%");
				System.out.println("Ersparnis (Huffman-Code)\t  : "
						+ String.format(Locale.GERMAN, "%.2f", ersparnisHuffman) + "%");
			} else {
				System.out.println("Ersparnis (optimale feste Laenge) : 0,00%");
				System.out.println("Ersparnis (Huffman-Code)\t  : 0,00%");
			}

			// (vi)		
			double entropie = 0.0; 
			for(int f : freq){
				if(f > 0 && huffman.tokens.length > 0){
					double p_i = (double) f / huffman.tokens.length;
					entropie -= p_i * Math.log(p_i) / Math.log(2);
				}
			}
			System.out.println("Entropie\t\t\t  : " + String.format(Locale.GERMAN, "%.2f", entropie));

			// (vii)

			// Liste der Zeichen-Objekte
			ArrayList<Zeichen> zeichenListe = new ArrayList<Zeichen>();
			for (int i = 0; i < 256; i++) {
				if (freq[i] > 0) {
					zeichenListe.add(new Zeichen((char) i, huffman.tokens, freq, codeLengths));
				}
			}

			// Nach Haeufigkeit absteigend sortieren
			Collections.sort(zeichenListe, (a, b) -> b.haeufigkeit - a.haeufigkeit);

			System.out.println("Haeufigste Zeichen:");
			int limit = Math.min(10, zeichenListe.size());

			for (int i = 0; i < limit; i++) {
				Zeichen z = zeichenListe.get(i);
				System.out.println(z.toString() + ", Codewortlaenge: " + z.getWortLaenge());
			}

			// Optionale Teilaufgabe
			String autoOutput = args[0] + ".huf";
			System.out.println("\n[Zusatzaufgabe] Datei wird jetzt kodiert...");
			encodeOptional(args[0], autoOutput);
			System.out.println("Fertig: " + autoOutput);
		} catch (ArrayIndexOutOfBoundsException aiobe) {
			System.out.println("Gueltiger Aufruf: java HuffmanCode datei");
			System.out.println("Oder optional:");
			System.out.println("  java HuffmanCode -e eingabe ausgabe");
			System.out.println("  java HuffmanCode -d eingabe ausgabe");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		
	}
	private static class Zeichen {
		char zeichen;
		int haeufigkeit;
		double relativeHaeufigkeit;
		int wortLaenge;

		public Zeichen(char zeichen, char[] tokens, int[] freq, int[] codeLengths) {
			this.zeichen = zeichen;
			this.haeufigkeit = freq[(int) zeichen];
			this.relativeHaeufigkeit = (tokens.length == 0) ? 0.0
					: (double) this.haeufigkeit / tokens.length * 100;
			this.wortLaenge = codeLengths[(int) zeichen];
		}

		public int getWortLaenge() {
			return wortLaenge;
		}

		@Override
		public String toString() {
			
			String hex = String.format("0x%02x", (int) zeichen);

			// druckbares Zeichen: Wert >= 33 -> Zeichen ausgeben, sonst genau ein Leerzeichen
			String chPart = (zeichen >= 33) ? " " + zeichen : " ";

			String percent = String.format(Locale.GERMAN, "%.1f", relativeHaeufigkeit);

			// Vorderer Teil des Formats (ohne Codewortlänge)
			return String.format("%s%s, Häufigkeit: %d (%s%%)",
					hex, chPart, haeufigkeit, percent);
		}
	}

	
	// Optimale/optionale Loesung: Kodieren und Dekodieren mit Huffman-Baum

	// Zusaetzliche Informationen in der kodierten Datei:
	// 1) Anzahl der Original-Zeichen
	// 2) Laenge der Baumdarstellung in Bits
	// 3) Laenge der kodierten Daten in Bits
	// 4) Baumdarstellung
	// 5) Kodierte Nutzdaten

	private static void buildCodes(HuffmanNode node, String prefix, Map<Character, String> codes) {
		if (node == null) {
			return;
		}
		if (node.isLeaf()) {
			codes.put(node.c, prefix.isEmpty() ? "0" : prefix);
			return;
		}
		buildCodes(node.left, prefix + "0", codes);
		buildCodes(node.right, prefix + "1", codes);
	}

	private static void serializeTree(HuffmanNode node, StringBuilder sb) {
		if (node == null) {
			return;
		}
		if (node.isLeaf()) {
			sb.append('1');
			int val = (int) node.c;
			for (int i = 7; i >= 0; i--) {
				sb.append((val >> i) & 1);
			}
		} else {
			sb.append('0');
			serializeTree(node.left, sb);
			serializeTree(node.right, sb);
		}
	}

	private static HuffmanNode deserializeTree(String bits, int[] pos) {
		if (pos[0] >= bits.length()) {
			return null;
		}

		char bit = bits.charAt(pos[0]++);
		if (bit == '1') {
			int val = 0;
			for (int i = 0; i < 8; i++) {
				val = (val << 1) | (bits.charAt(pos[0]++) - '0');
			}
			return new HuffmanNode((char) val, 0);
		}

		HuffmanNode left = deserializeTree(bits, pos);
		HuffmanNode right = deserializeTree(bits, pos);
		return new HuffmanNode(0, left, right);
	}

	private static byte[] bitsToBytes(String bits) {
		int len = (bits.length() + 7) / 8;
		byte[] result = new byte[len];

		for (int i = 0; i < bits.length(); i++) {
			if (bits.charAt(i) == '1') {
				result[i / 8] |= (byte) (1 << (7 - (i % 8)));
			}
		}

		return result;
	}

	private static String bytesToBits(byte[] data, int totalBits) {
		StringBuilder sb = new StringBuilder(totalBits);
		int written = 0;

		for (byte b : data) {
			for (int i = 7; i >= 0 && written < totalBits; i--, written++) {
				sb.append((b >> i) & 1);
			}
		}

		return sb.toString();
	}

	private static void encodeOptional(String inputFile, String outputFile) throws IOException {
		char[] tokens = scanFile(inputFile);
		int n = tokens.length;

		int[] freq = new int[256];
		for (char c : tokens) {
			freq[(int) c]++;
		}

		HuffmanNode root = buildTree(freq);
		if (root == null) {
			try (DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(outputFile)))) {
				dos.writeInt(0);
				dos.writeInt(0);
				dos.writeInt(0);
			}
			System.out.println("Optionale Kodierung abgeschlossen (leere Datei).");
			return;
		}

		Map<Character, String> codes = new HashMap<Character, String>();
		buildCodes(root, "", codes);

		StringBuilder treeBits = new StringBuilder();
		serializeTree(root, treeBits);

		StringBuilder dataBits = new StringBuilder();
		for (char c : tokens) {
			dataBits.append(codes.get(c));
		}

		int treeBitLen = treeBits.length();
		int dataBitLen = dataBits.length();

		try (DataOutputStream dos = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(outputFile)))) {
			dos.writeInt(n);
			dos.writeInt(treeBitLen);
			dos.writeInt(dataBitLen);
			dos.write(bitsToBytes(treeBits.toString()));
			dos.write(bitsToBytes(dataBits.toString()));
		}

		System.out.println("Optionale Kodierung abgeschlossen.");
		System.out.println("  Original:    " + (n * 8) + " Bits  (" + n + " Bytes)");
		System.out.println("  Komprimiert: " + dataBitLen + " Bits Daten + " + treeBitLen + " Bits Baum");
		System.out.println("  Ausgabedatei: " + outputFile);
	}

	private static void decodeOptional(String inputFile, String outputFile) throws IOException {
		int n;
		int treeBitLen;
		int dataBitLen;
		byte[] treeBytes;
		byte[] dataBytes;

		try (DataInputStream dis = new DataInputStream(
				new BufferedInputStream(new FileInputStream(inputFile)))) {
			n = dis.readInt();
			treeBitLen = dis.readInt();
			dataBitLen = dis.readInt();

			int treeByteLen = (treeBitLen + 7) / 8;
			int dataByteLen = (dataBitLen + 7) / 8;

			treeBytes = new byte[treeByteLen];
			dataBytes = new byte[dataByteLen];
			dis.readFully(treeBytes);
			dis.readFully(dataBytes);
		}

		if (n == 0) {
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				// leere Datei schreiben
			}
			System.out.println("Optionale Dekodierung abgeschlossen: leere Datei.");
			return;
		}

		String treeBits = bytesToBits(treeBytes, treeBitLen);
		int[] pos = {0};
		HuffmanNode root = deserializeTree(treeBits, pos);

		String dataBits = bytesToBits(dataBytes, dataBitLen);
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			if (root != null && root.isLeaf()) {
				for (int i = 0; i < n; i++) {
					fos.write((int) root.c);
				}
			} else {
				HuffmanNode node = root;
				int decoded = 0;

				for (int i = 0; i < dataBits.length() && decoded < n; i++) {
					node = (dataBits.charAt(i) == '0') ? node.left : node.right;
					if (node == null) {
						System.err.println("Fehler beim Dekodieren!");
						return;
					}
					if (node.isLeaf()) {
						fos.write((int) node.c);
						decoded++;
						node = root;
					}
				}
			}
		}

		System.out.println("Optionale Dekodierung abgeschlossen: " + n + " Zeichen -> " + outputFile);
	}
}
