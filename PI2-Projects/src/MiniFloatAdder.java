//Gruppennummer: 109
import java.util.Scanner;

public class MiniFloatAdder { 
	/** prints all MiniFloat values in decreasing order */
	public static void printAllMiniFloatValues() {
        for(int i = 255; i >= 0; i--) {
			System.out.println(toBinaryString((byte)i) + " " + toDouble((byte)i));
		}
	}

	/** MiniFloat to double */
	public static final double toDouble(byte miniF) {
		int raw = miniF;
		if (raw < 0) raw += 256;

		int vorzeichen = raw / 128;

		int e = (raw / 16) % 8;

		int fracBits = raw % 16;

		// Sonderfall: Exponent = 0; denormalisierte Zahl 
		if (e == 0) {
			if (fracBits == 0) {
				return 0.0;
			}
			double m = fracBits / 16.0;
			return Math.pow(-1, vorzeichen) * m * Math.pow(2, -2);
		}

		// Normalisierte Zahl:
		double mantissa = 1.0 + fracBits / 16.0;
		int exponent = e - 3;
		return Math.pow(-1, vorzeichen) * mantissa * Math.pow(2, exponent);
	}

	/** adds two non-negative MiniFloats */
	public static final byte addMiniFloats(byte miniF1, byte miniF2) {
		int raw1 = ((int) miniF1) < 0 ? miniF1 + 256 : miniF1;
		int raw2 = ((int) miniF2) < 0 ? miniF2 + 256 : miniF2;

		byte exp1 = (byte) ((raw1 / 16) % 8);
		byte exp2 = (byte) ((raw2 / 16) % 8);

		byte frac1 = (byte) (raw1 % 16);
		byte frac2 = (byte) (raw2 % 16);

		byte sig1 = (exp1 == 0) ? frac1 : (byte) (16 + frac1);
		byte sig2 = (exp2 == 0) ? frac2 : (byte) (16 + frac2);

		if (exp1 == 0) exp1 = 1;
		if (exp2 == 0) exp2 = 1;

		// Exponenten angleichen und dabei das Guard-Bit für die spätere Rundung merken
		int guard = 0;

		if (exp1 > exp2) {
			int delta = exp1 - exp2;
			int d = 1;
			for (int i = 0; i < delta; i++) d *= 2;
			int lost = sig2 % d;

			guard = (lost >= d / 2) ? 1 : 0; // Guard-Bit merken
			sig2 = (byte) (sig2 / d);
			exp2 = exp1;
		} else if (exp2 > exp1) {
			int delta = exp2 - exp1;
			int d = 1;
			for (int i = 0; i < delta; i++) d *= 2;
			int lost = sig1 % d;

			guard = (lost >= d / 2) ? 1 : 0; // Guard-Bit merken
			sig1 = (byte) (sig1 / d);
			exp1 = exp2;
		}

		int sig = sig1 + sig2;
		byte exp = exp1;
		if (sig >= 32) {
			guard = sig % 2; // Guard-Bit aus Normalisierung überschreibt das vorherige Guard-Bit
			sig = sig / 2;
			exp++;
		}

		// Rundung am Ende
		if (guard == 1) {
			sig++;
			if (sig >= 32) { // Overflow durch Rundung
				sig = sig / 2;
				exp++;
			}
		}

		byte frac;
		byte expBits;
		if (exp == 0 || (exp == 1 && sig < 16)) {
			expBits = 0;
			frac = (byte) sig;
		} else {
			expBits = exp;
			frac = (byte) (sig - 16);
		}

		return (byte) (expBits * 16 + frac);
	}

	// Hilfsmethode: konvertiert ein MiniFloat-Byte in eine Binärzahl
	private static String toBinaryString(byte miniF) {
		int raw = miniF;
		if (raw < 0) raw += 256; 

		StringBuilder builder = new StringBuilder();

		while(raw != 0) {
			builder.append(raw % 2);
			raw /= 2;
		}

		if(builder.length() == 8) {
			return builder.reverse().toString();
		} else {
			while(builder.length() < 8) {
				builder.append(0);
			}
		}
		return builder.reverse().toString();
	}


	// Hilfsmethode: konvertiert eine Binärzahl in ein MiniFloat-Byte
	private static byte binaryToMiniFloat(String bits) {
		return (byte) Integer.parseInt(bits, 2);
	}

	// Hilfsmethode: Testen der Addition 
	private static void aufgabe_3d(){
		String [] TESTS = {"00101000", "00101000", "00101000", "00101000", "01111001"};
		byte first = binaryToMiniFloat(TESTS[0]);
		byte second = binaryToMiniFloat(TESTS[1]);
		byte sumPrev = addMiniFloats(first, second);
		System.out.printf(TESTS[0] + " + " + TESTS[1] + " = " + toBinaryString(sumPrev) + "     (" + toDouble(first) + " + " + toDouble(second) + " = " + toDouble(sumPrev) + ")%n");
		for (int i = 2; i < TESTS.length; i++) {
			byte next = binaryToMiniFloat(TESTS[i]);
			byte sumNext = addMiniFloats(sumPrev, next);
			System.out.printf(toBinaryString(sumPrev) + " + " + TESTS[i] + " = " + toBinaryString(sumNext) + "     (" + toDouble(sumPrev) + " + " + toDouble(next) + " = " + toDouble(sumNext) + ")%n");
			sumPrev = sumNext;
		}

	}

	// Hilfsmethode: Testen der Addition
	private static void aufgabe_3e(){
		String [] TESTS = {"01111001", "00101000", "00101000", "00101000", "00101000"};
		byte first = binaryToMiniFloat(TESTS[0]);
		byte second = binaryToMiniFloat(TESTS[1]);
		byte sumPrev = addMiniFloats(first, second);
		System.out.printf(TESTS[0] + " + " + TESTS[1] + " = " + toBinaryString(sumPrev) + "     (" + toDouble(first) + " + " + toDouble(second) + " = " + toDouble(sumPrev) + ")%n");
		for (int i = 2; i < TESTS.length; i++) {
			byte next = binaryToMiniFloat(TESTS[i]);
			byte sumNext = addMiniFloats(sumPrev, next);
			System.out.printf(toBinaryString(sumPrev) + " + " + TESTS[i] + " = " + toBinaryString(sumNext) + "     (" + toDouble(sumPrev) + " + " + toDouble(next) + " = " + toDouble(sumNext) + ")%n");
			sumPrev = sumNext;
		}

	}

	public static void main(String[] args) {
		System.out.println("Aufgabe 3b:");
		printAllMiniFloatValues();

		System.out.println("\nAufgabe 3c:");
		Scanner scanner = new Scanner(System.in);
		System.out.print("Erste MiniFloat (8-Bit-Binär): ");
		String firstBits = scanner.nextLine().trim();
		System.out.print("Zweite MiniFloat (8-Bit-Binär): ");
		String secondBits = scanner.nextLine().trim();
		byte first = binaryToMiniFloat(firstBits);
		byte second = binaryToMiniFloat(secondBits);
		byte sum = addMiniFloats(first, second);
		System.out.printf(firstBits + " + " + secondBits + " = " + toBinaryString(sum) + "     (" + toDouble(first) + " + " + toDouble(second) + " = " + toDouble(sum) + ")%n");
		scanner.close();

		System.out.println("\nAufgabe 3d:");
		aufgabe_3d();

		System.out.println("\nAufgabe 3e:");
		aufgabe_3e();
	}
}

