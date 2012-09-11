package eu.robojob.irscw.ui.controls;

public class FullTextField extends AbstractTextField<String> {

	public FullTextField(int maxLength) {
		super(maxLength);
	}

	@Override
	public String getMatchingExpression() {
		return "[A-Z0-9ÖÜÄ_ \\.\\r-]*$";
	}

	@Override
	public void replaceText(int start, int end, String text) {
		super.replaceText(start, end, text.toUpperCase());
	}

	@Override
	public void cleanText() {
		// not necessary
	}

	@Override
	public String convertString(String text) {
		return text;
	}

	@Override
	public int calculateLength(String string) {
		return string.length();
	}
	
}
