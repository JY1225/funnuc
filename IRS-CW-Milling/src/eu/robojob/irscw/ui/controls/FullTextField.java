package eu.robojob.irscw.ui.controls;

public class FullTextField extends AbstractTextField {

	public FullTextField() {
	}

	@Override
	public String getMatchingExpression() {
		return "[A-Z0-9���_ \\.\\r-]*$";
	}

	@Override
	public void replaceText(int start, int end, String text) {
		super.replaceText(start, end, text.toUpperCase());
	}

	@Override
	public void cleanText() {
		// not necessary
	}
	
}
