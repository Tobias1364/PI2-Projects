import java.util.Scanner;
import java.lang.String;

//Gruppennummer: 109

public class BinaryAdder {
	public static String add(String binary1, String binary2) {
		if(binary1 == null || binary2 == null) {
			System.out.println("Inputs dürfen nicht null sein");
			return null;
		}
		
		boolean carry = false;
		StringBuilder builder = new StringBuilder("");
		String shorterString = "";
		String longerString = "";
		if(binary1.length() < binary2.length()) {
			shorterString = binary1;
			longerString = binary2;
		} else {
			shorterString = binary2;
			longerString = binary1;
		}
		
		int shorter = shorterString.length() - 1;
		int longer = longerString.length() - 1 ;
		
		while(shorter >= 0 || longer >= 0 || carry) {
			int bit1 = (shorter >= 0) ? shorterString.charAt(shorter) - '0' : 0;
			int bit2 = (longer >= 0) ? longerString.charAt(longer) - '0' : 0;
			
			int sum = (bit1) + (bit2) + (carry ? 1 : 0);
			
			builder.append(sum % 2);
			carry = (sum >= 2);
			
			
			shorter--;
			longer--;
		}
		String result = removeLeadingZeros(builder.reverse().toString());
		return result;
	}
	
	
	public static String removeLeadingZeros(String binary) {
		if(binary == null || binary.isEmpty()) {
			return "0";
		} 
		
		int i = 0;
			
		while(i < binary.length() - 1 && binary.charAt(i) == '0') {
	        i++;
	    }
	    
	    return binary.substring(i);
	}
	// Teste deine Methode mit zwei Binaerzahlen, die du in der Konsole eingibst
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Summand:  ");
		String inputA = scanner.next("(0|1)*");
		System.out.print("Summand:  ");
		String inputB = scanner.next("(0|1)*");
		scanner.close();
		System.out.println("Ergebnis: " + add(inputA, inputB));
	}

}
