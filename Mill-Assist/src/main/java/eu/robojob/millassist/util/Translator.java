package eu.robojob.millassist.util;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Translator {
	
	private static Locale locale = new Locale("nl");;
	private static ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
	
	private Translator() {
		//setLanguageNL();
	}
	
	public static void setLanguageNL() {
		locale = new Locale("nl");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageEN() {
		locale = new Locale("en");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageDE() {
		locale = new Locale("de");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageSE() {
		locale = new Locale("se");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static String getTranslation(final String key) {
		return messages.getString(key);
	}
	
	public static Locale getLocale() {
		return locale;
	}
}
