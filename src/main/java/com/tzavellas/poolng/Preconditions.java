package com.tzavellas.poolng;

/**
 * A helper class with methods that check common preconditions.
 *  
 * @author spiros
 */
abstract class Preconditions {
	
	private Preconditions() { /* prevent instantiation */ }
		
	static void assertHasText(String argName, String text) {
		if (! hasText(text)) {
			throw new IllegalArgumentException("The argument [" + argName + "] must not be null or whitespace!");
		}
	}
	
	static boolean hasText(String text) {
		return text != null && ! text.trim().equals("");
	}
}
