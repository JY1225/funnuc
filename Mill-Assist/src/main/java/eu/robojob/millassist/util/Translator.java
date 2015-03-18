package eu.robojob.millassist.util;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Translator {
	
	private static Locale locale = new Locale("nl");
	private static ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
	
	private Translator() {
		//setLanguageNL();
	}
	
	//TODO - remove this if we decide to use language files outside the program
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
	//TODO - stop remove here
	
	//TODO - uncomment in case we want to use the language files outside the program
//	public static void setLanguage(final String language, final ClassLoader loader) {
//		locale = new Locale(language);
//		messages = ResourceBundle.getBundle("messages", locale, loader);
//	}
	
	public static String getTranslation(final String key) {
		return messages.getString(key);
	}
	
	public static Locale getLocale() {
		return locale;
	}
}
