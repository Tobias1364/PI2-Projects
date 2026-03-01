//Gruppennummer: 109
import java.util.Scanner;

public class MiniFloatAdder { 
	public static void printAllMiniFloatValues() {
        for(int i = 255; i >= 0; i--) {
			System.out.println(toBinaryString((byte)i) + " " + toDouble((byte)i));
		}
	}

	public static String toBinaryString(byte miniF) {
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

		// Exponenten angleichen: die kleinere Zahl wird so verschoben, dass sie den größeren Exponenten hat
		if (exp1 > exp2) {
			int delta = exp1 - exp2;
			int d = 1;
			for (int i = 0; i < delta; i++) d *= 2;   

			int q = sig2 / d;
			int r = sig2 % d;

			
			if (2 * r < d) {
				// abrunden
				sig2 = (byte) q;
			} else if (2 * r > d) {
				sig2 = (byte) (q + 1); // aufrunden
			} else {
				// nur aufrunden, wenn q ungerade ist
				if (q % 2 != 0) sig2 = (byte) (q + 1);
				sig2 = (byte) q;
			}
			
			exp2 = exp1;
		} else if (exp2 > exp1) {
			int delta = exp2 - exp1;
			int d = 1;
			for (int i = 0; i < delta; i++) d *= 2;   // d = 2^delta, aber als int

			int q = sig1 / d;
			int r = sig1 % d;

			if (2 * r < d) {
				// abrunden
				sig1 = (byte) q;
			} else if (2 * r > d) {
				sig1 = (byte) (q + 1); // aufrunden
			} else {
				// exakt halb: nur aufrunden, wenn q ungerade ist
				if (q % 2 != 0) sig1 = (byte) (q + 1);
				sig1 = (byte) q;
			}
			exp1 = exp2;
		}

		byte sig = (byte) (sig1 + sig2);
		byte exp = exp1;
		if (sig >= 32) {
			int lost = sig % 2;     // verlorenes Bit beim /2
			sig = (byte)(sig / 2);
			if (lost == 1) sig = (byte)(sig + 1); 
			exp++;
		}

		byte frac;
		byte expBits;
		if (exp == 0 || (exp == 1 && sig < 16)) {
			expBits = 0;
			frac = sig;
		} else {
			expBits = exp;
			frac = (byte)(sig - 16);
		}

		return (byte) (expBits * 16 + frac);
	}

	// Hilfsmethode: konvertiert eine Binärzahl in ein MiniFloat-Byte
	private static byte binaryToMiniFloat(String bits) {
		return (byte) (Integer.parseInt(bits, 2) & 0xFF);
	}

	// Hilfsmethode: Testen der Addition 
	private static void aufgabe_3d(){
		String [] TESTS = {"00101000", "00101000", "00101000", "00101000", "01111001"};
		byte first = binaryToMiniFloat(TESTS[0]);
		byte second = binaryToMiniFloat(TESTS[1]);
		byte sumPrev = addMiniFloats(first, second);
		System.out.printf("%s + %s = %s     (%.2f + %.2f = %.2f)%n",
					TESTS[0], TESTS[1], toBinaryString(sumPrev),
					toDouble(first), toDouble(second), toDouble(sumPrev));
		for (int i = 2; i < TESTS.length; i++) {
			byte next = binaryToMiniFloat(TESTS[i]);
			byte sumNext = addMiniFloats(sumPrev, next);
			System.out.printf("%s + %s = %s     (%.2f + %.2f = %.2f)%n",
					toBinaryString(sumPrev), TESTS[i], toBinaryString(sumNext),
					toDouble(sumPrev), toDouble(next), toDouble(sumNext));
			sumPrev = sumNext;
		}

	}

	// Hilfsmethode: Testen der Addition
	private static void aufgabe_3e(){
		String [] TESTS = {"01111001", "00101000", "00101000", "00101000", "00101000"};
		byte first = binaryToMiniFloat(TESTS[0]);
		byte second = binaryToMiniFloat(TESTS[1]);
		byte sumPrev = addMiniFloats(first, second);
		System.out.printf("%s + %s = %s     (%.2f + %.2f = %.2f)%n",
					TESTS[0], TESTS[1], toBinaryString(sumPrev),
					toDouble(first), toDouble(second), toDouble(sumPrev));
		for (int i = 2; i < TESTS.length; i++) {
			byte next = binaryToMiniFloat(TESTS[i]);
			byte sumNext = addMiniFloats(sumPrev, next);
			System.out.printf("%s + %s = %s     (%.2f + %.2f = %.2f)%n",
					toBinaryString(sumPrev), TESTS[i], toBinaryString(sumNext),
					toDouble(sumPrev), toDouble(next), toDouble(sumNext));
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
		System.out.printf("%s + %s = %s     (%.2f + %.2f = %.2f)%n",
				firstBits, secondBits, toBinaryString(sum),
				toDouble(first), toDouble(second), toDouble(sum));
		scanner.close();

		System.out.println("\nAufgabe 3d:");
		aufgabe_3d();

		System.out.println("\nAufgabe 3e:");
		aufgabe_3e();
	}
}

