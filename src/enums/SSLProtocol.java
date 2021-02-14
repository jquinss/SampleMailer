package enums;

public enum SSLProtocol {
	SSLv3,
	TLSv1,
	TLSv1_1,
	TLSv1_2;
	
	@Override
	public String toString() {
		switch (this) {
		case SSLv3: return "SSLv3";
		case TLSv1: return "TLSv1";
		case TLSv1_1: return "TLSv1.1";
		case TLSv1_2: return "TLSv1.2";
		default:throw new IllegalArgumentException();
		}
	}
}
