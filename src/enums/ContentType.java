package enums;

public enum ContentType {
	TEXT_PLAIN,
	TEXT_HTML;
	
	@Override
	public String toString() {
		switch (this) {
		case TEXT_PLAIN: return "text/plain";
		case TEXT_HTML: return "text/html";
		default:throw new IllegalArgumentException();
		}
	}
}