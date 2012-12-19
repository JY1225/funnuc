package eu.robojob.irscw.ui.controls;

import java.text.DecimalFormat;

public class IntegerTextField extends AbstractTextField<Integer> {

	private static final String CSS_CLASS_INTEGER_TEXTFIELD = "integer-textfield";

	private static final String DECIMAL_FORMAT = "#0";
	private static final String EMPTY_VALUE = "0";
	
	public IntegerTextField(final int maxLength) {
		super(maxLength);
		this.getStyleClass().add(CSS_CLASS_INTEGER_TEXTFIELD);
	}

	@Override
	public String getMatchingExpression() {
		return "^[0-9]*$";
	}

	@Override
	public int calculateLength(final String string) {
		return string.length();
	}

	@Override
	public void cleanText() {
		DecimalFormat formatter = new DecimalFormat(DECIMAL_FORMAT);
		formatter.setDecimalSeparatorAlwaysShown(false);
		if (!this.getText().equals("")) {
			setText(formatter.format(Float.valueOf(this.getText())));
		} else {
			setText(formatter.format(Float.valueOf(EMPTY_VALUE)));
		}
	}

	@Override
	public Integer convertString(final String text) {
		if (text.equals("")) {
			return 0;
		} else {
			return Integer.valueOf(text);
		}
	}

}
