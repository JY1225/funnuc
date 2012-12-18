package eu.robojob.irscw.util;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Translator {
	
	private static Locale locale = new Locale("nl");;
	private static ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
	
	private Translator() {
		setLanguageNL();
	}
	
	public static void setLanguageNL() {
		locale = new Locale("nl");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageEN() {
		locale = new Locale("nl");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static String getTranslation(final String key) {
		return messages.getString(key);
	}
	
	public static Locale getLocale() {
		return locale;
	}
}
