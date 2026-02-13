
public class TwosComplementResult {
	public String decimal;
	public String binary;
	public boolean overflow;

	public TwosComplementResult(String decimal, String binary, boolean overflow) {
		this.decimal = decimal;
		this.binary = binary;
		this.overflow = overflow;
	}

	public boolean equals(TwosComplementResult o) {
		return this.decimal.equals(o.decimal)
				&& this.binary.equals(o.binary) && this.overflow == o.overflow;
	}
	
	public String toString() {
		return this.decimal + "\t-> " + this.binary + " -> " + this.overflow;
	}

}
