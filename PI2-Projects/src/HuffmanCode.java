import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

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
			System.out.println("Anzahl Zeichen\t\t\t  : " + "YOUR RESULT HERE");
			System.out.println("Anzahl verschiedener Zeichen\t  : " + "YOUR RESULT HERE");
			System.out.println("Kodierung mit fester Bitlaenge\t  : " + "YOUR RESULT HERE" + " ("
					+ "YOUR RESULT HERE" + " Bits pro Zeichen)");
			System.out.println("Kodierung mit Huffman-Code\t  : " + "YOUR RESULT HERE" + " ("
					+ "YOUR RESULT HERE" + " Bits pro Zeichen)");
			System.out.println("Ersparnis (optimale feste Laenge) : "
					+ "YOUR RESULT HERE " + "%");
			System.out.println("Ersparnis (Huffman-Code)\t  : "
					+ "YOUR RESULT HERE " + "%");
			System.out.println("Entropie\t\t\t  : " + "YOUR RESULT HERE");
			System.out.println("Haeufigste Zeichen:");
			System.out.println("YOUR RESULT HERE");
		} catch (ArrayIndexOutOfBoundsException aiobe) {
			System.out.println("Gueltiger Aufruf: java HuffmanCode datei");
		}
	}
}
