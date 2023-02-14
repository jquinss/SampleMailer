package com.jquinss.samplemailer.enums;

public enum Charset implements Describable {
	UTF_8 {
		public String getDescription() {
			return "Universal Alphabet (UTF-8)";
		}
	},
	
	ISO_8859_1 {
		public String getDescription() {
			return "Western Alphabet";
		}
	},
	ISO_8859_2 {
		public String getDescription() {
			return "Central European Alphabet (ISO)";
		}
	}
	,
	ISO_8859_3 {
		public String getDescription() {
			return "Latin 3 Alphabet (ISO)";
		}
	},
	ISO_8859_4 {
		public String getDescription() {
			return "Baltic Alphabet (ISO)";
		}
	},
	ISO_8859_5 {
		public String getDescription() {
			return "Cyrillic Alphabet (ISO)";
		}
	},
	ISO_8859_6 {
		public String getDescription() {
			return "Arabic Alphabet (ISO)";
		}
	};
	
	@Override
	public String toString() {
		return switch (this) {
			case UTF_8 -> "utf-8";
			case ISO_8859_1 -> "iso-8859-1";
			case ISO_8859_2 -> "iso-8859-2";
			case ISO_8859_3 -> "iso-8859-3";
			case ISO_8859_4 -> "iso-8859-4";
			case ISO_8859_5 -> "iso-8859-5";
			case ISO_8859_6 -> "iso-8859-6";
			default -> throw new IllegalArgumentException();
		};
	}
}

interface Describable {
	String getDescription();
}