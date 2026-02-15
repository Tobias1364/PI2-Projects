import java.util.Scanner;
//Gruppennummer: 109
public class BinaryMultiplication {

	public static String multiply(String bin1, String bin2) {
		String[] bins = new String[bin2.length()];
		int multi = bin2.length();
		for(int i = multi-1, j = 0; i >= 0; i--, j++) {
			StringBuilder builder = new StringBuilder();
			if(bin2.charAt(i) == '1') {
				builder.append(bin1);
			} else {
				builder.append('0');
			}
			for(int k = 0; k < j; k++) {
				builder.append('0');
			}
			bins[i] = builder.toString();
		}
		
		return addBinaryArray(bins);
	}
	
	
	public static String addBinaryArray(String[] bins) {
		
		for(int i = 0; i < bins.length - 1; i++) {
			StringBuilder builder = new StringBuilder();
			boolean carry = false;
			int shorter = bins[i].length() - 1;
			int longer = bins[i+1].length() - 1;
			
			while(shorter >= 0 || longer >= 0 || carry) {
				int bit1 = (shorter >= 0) ? bins[i].charAt(shorter) - '0' : 0;
				int bit2 = (longer >= 0) ? bins[i+1].charAt(longer) - '0' : 0;
				
				int sum = (bit1) + (bit2) + (carry ? 1 : 0);
				
				builder.append(sum % 2);
				carry = (sum >= 2);
				
				
				shorter--;
				longer--;
			}
			
			bins[i+1] = removeLeadingZeros(builder.reverse().toString());
		}
		
		return bins[bins.length - 1];
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
		Scanner s = new Scanner(System.in);
		System.out.print("1. Faktor:  ");
		String inputA = s.next("(0|1)*");
		System.out.print("2. Faktor:  ");
		String inputB = s.next("(0|1)*");
		s.close();
		System.out.println("Ergebnis: " + multiply(inputA, inputB));
	}
	
}
