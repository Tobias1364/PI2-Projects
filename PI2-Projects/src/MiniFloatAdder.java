//Gruppennummer: 109
public class MiniFloatAdder {	
	/** prints all MiniFloat values in decreasing order */
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

	/** MiniFloat to double */
	public static final double toDouble(byte miniF) {
		// Byte als unsigned 8-Bit-Wert behandeln (0–255)
		//int raw = miniF & 0xFF;
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
        
		return 0;
	}	

	public static void main(String[] args) {
		printAllMiniFloatValues();
	}
}
