package eu.robojob.millassist.external.device.stacking.stackplate;

import eu.robojob.millassist.util.Translator;

public class IncorrectWorkPieceDataException extends Exception {

	private static final long serialVersionUID = 1L;
	public static final String INCORRECT_DATA = "IncorrectWorkPieceDataException.incorrectData";
	public static final String LENGTH_SMALLER_WIDTH = "IncorrectWorkPieceDataException.lengthSmallerThanWidth";
	public static final String INCORRECT_AMOUNT = "IncorrectWorkPieceDataException.incorrectAmount";
	public static final String TOO_LARGE = "IncorrectWorkPieceDataException.tooLarge"; 
	
	private String key;

	public IncorrectWorkPieceDataException(final String key) {
		this.key = key;
	}
	
	@Override
	public String getMessage() {
		return "The provided workpiece data is incorrect";
	}
	
	@Override
	public String getLocalizedMessage() {
		return Translator.getTranslation(key) + ".";
	}
}
