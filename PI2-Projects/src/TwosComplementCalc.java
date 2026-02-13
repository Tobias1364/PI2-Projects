import java.util.Scanner;

public class TwosComplementCalc {

	/**
	 * Addiert zwei Zahlen im 8Bit-Zweierkomplement. Die Zahlen werden im 8-Bit-Zweierkomplement uebergeben. 
	 */
	public static TwosComplementResult add2(String bin1, String bin2) {
		// Fuege deinen Code hier ein.
		StringBuilder binary = new StringBuilder();
		boolean carry = false;
		for(int i = 7; i >= 0; i--) {
			int bit1 = (bin1.charAt(i) == '0') ? 0 : 1;
			int bit2 = (bin2.charAt(i) == '0') ? 0 : 1;
			int sum = bit1 + bit2 + ((carry) ? 1 : 0);
			
			binary.append(sum % 2);
			carry = (sum >= 2);
			
		}
		binary.reverse();
		
		boolean overflow = carry;
		
		int decimal = 0;
		if(binary.charAt(0) == '0') { //positiv
			decimal = binaryToDecimalPositiv(binary.toString());
		} else { //negativ
			StringBuilder zK = new StringBuilder();
			for(int i = 0; i < 8; i++) {
				if(binary.charAt(i) == '0') {
					zK.append('1');
				} else {
					zK.append('0');
				}
			}
			decimal = binaryToDecimalPositiv(zK.toString());
			decimal++;
			decimal *= -1;
		}
		
		
		return new TwosComplementResult(Integer.toString(decimal),binary.toString(),overflow);
	}

	public static int binaryToDecimalPositiv(String bin) {
		int decimal = 0;
		for(int i = 1; i < 8; i++) {
			if(bin.charAt(i) == '1') {
				decimal += Math.pow(2, 7-i);
			}
		}
		return decimal;
	}
	/**
	 * Uebersetzt eine Dezimalzahl zwischen -128 und 127 in das 8-Bit-Zweierkomplement.
	 */
	public static String toBinaryString(String decimal) {
		// Fuege deinen Code hier ein
		boolean negativ = false;
		StringBuilder binary = new StringBuilder();
		if(decimal.charAt(0) == '-') {
			negativ = true;
		}
		byte dec = Byte.parseByte(decimal);
		
		if(negativ) {
			StringBuilder zK = new StringBuilder();
			dec *=-1;
			dec--;
			while(dec > 0) { // Dec in Binaer
				binary.append(dec % 2);
				dec /= 2;
			}
			for(int i = binary.length(); i < 8; i++) {
				binary.append('0');
			}
			binary.reverse();
			for(int i = 0; i < binary.length(); i++) {
				if(binary.charAt(i) == '1') {
					zK.append('0');
				} else {
					zK.append('1');
				}
			}
			return zK.toString();
			
		} else {
			while(dec > 0) { //Dec in Binaer
				binary.append(dec % 2);
				dec /= 2;
			}
			for(int i = binary.length(); i < 8; i++) {
				binary.append('0');
			}
			binary.reverse();
			
		}
		return binary.toString();
	}
 
	// Teste deine Methoden mit zwei Dezimalzahlen, die du in der Konsole
	// eintippst
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.print("1. Dezimalzahl\t\t : ");
		String inputA = s.next("-?[0-9]*");
		System.out.println("Im Zweierkomplement\t : " + toBinaryString(inputA));
		System.out.print("2. Dezimalzahl:\t\t : ");
		String inputB = s.next("-?[0-9]*");
		System.out.println("Im Zweierkomplement\t : " + toBinaryString(inputB));
		s.close();
		TwosComplementResult result = add2(toBinaryString(inputA), toBinaryString(inputB));
		System.out.println("Summe (ZK)\t\t : " + result.binary);
		System.out.println("Summe (Dezimal)\t\t : " + result.decimal);
		System.out.println("Overflow?\t\t : " + result.overflow);
	}

}
