package eu.robojob.millassist.ui.controls;

import eu.robojob.millassist.util.UIConstants;

public class FullTextField extends AbstractTextField<String> {

	public FullTextField(final int maxLength) {
		super(maxLength);
		setPrefWidth(UIConstants.TEXT_FIELD_HEIGHT * 6);
	}

	@Override
	public String getMatchingExpression() {
		return "[A-Z0-9���_ \\.\\r-]*$";
	}

	@Override
	public void replaceText(final int start, final int end, final String text) {
		super.replaceText(start, end, text.toUpperCase());
	}

	@Override
	public void cleanText() {
		// not necessary
	}

	@Override
	public String convertString(final String text) {
		return text;
	}

	@Override
	public int calculateLength(final String string) {
		return string.length();
	}
	
}
