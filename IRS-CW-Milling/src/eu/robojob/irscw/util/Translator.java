package eu.robojob.irscw.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class Translator {
	
	private static Translator instance;
	private Locale locale;
	private ResourceBundle messages;
	
	private Translator() {
		setLanguageNL();
	}
	
	public static Translator getInstance() {
		if (instance==null) {
			instance = new Translator();
		}
		return instance;
	}
	
	public void setLanguageNL() {
		locale = new Locale("nl");
		messages = ResourceBundle.getBundle("messages");
	}
	
	public void setLanguageEN() {
		locale = new Locale("nl");
		messages = ResourceBundle.getBundle("messages");
	}
	
	public String getTranslation(String key) {
		return messages.getString(key);
	}
	
	public Locale getLocale() {
		return locale;
	}
}
