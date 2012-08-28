package eu.robojob.irscw.ui.controls;

public class FullTextField extends TextField {

	public FullTextField() {
	}

	@Override
	public String getMatchingExpression() {
		return "[A-Z0-9ÖÜÄ_ \\.\\r-]*$";
	}

	@Override
	public void replaceText(int start, int end, String text) {
		super.replaceText(start, end, text.toUpperCase());
	}
}
