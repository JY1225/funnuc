package eu.robojob.irscw.ui.controls;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumericTextField extends AbstractTextField<Float> {

	private static final String CSS_CLASS_NUMERIC_TEXTFIELD = "numeric-textfield";
	
	private static final String DECIMAL_FORMAT = "#0.00";
	private static final String EMPTY_VALUE = "0.00";
	private static final char DECIMAL_SEPERATOR = '.';
	
	public NumericTextField(final int maxLength) {
		super(maxLength);	// 
		this.getStyleClass().add(CSS_CLASS_NUMERIC_TEXTFIELD);
	}

	@Override
	public String getMatchingExpression() {
		return "^[0-9]*\\.?[0-9]*$";
	}

	@Override
	public void cleanText() {
		DecimalFormat formatter = new DecimalFormat(DECIMAL_FORMAT);
		formatter.setDecimalSeparatorAlwaysShown(true);
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator(DECIMAL_SEPERATOR);
		formatter.setDecimalFormatSymbols(custom);
		if (!this.getText().equals("")) {
			setText(formatter.format(Float.valueOf(this.getText())));
		} else {
			setText(formatter.format(Float.valueOf(EMPTY_VALUE)));
		}
	}

	@Override
	public Float convertString(final String text) {
		if (text.equals("")) {
			return 0f;
		} else {
			return Float.valueOf(text);
		}
	}

	@Override
	public int calculateLength(final String string) {
		return string.length();
	}
	
}
