package eu.robojob.irscw.ui.controls;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumericTextField extends AbstractTextField<Float> {

	public NumericTextField(int maxLength) {
		super(maxLength-3);
		this.getStyleClass().add("numeric-text-field");
	}

	@Override
	public String getMatchingExpression() {
		return "^[0-9]*\\.?[0-9]*$";
	}

	@Override
	public void cleanText() {
		DecimalFormat formatter = new DecimalFormat("#0.00");
		formatter.setDecimalSeparatorAlwaysShown(true);
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator('.');
		formatter.setDecimalFormatSymbols(custom);
		if (!this.getText().equals("")) {
			setText(formatter.format(Float.valueOf(this.getText())));
		} else {
			setText(formatter.format(Float.valueOf("0.0")));
		}
	}

	@Override
	public Float convertString(String text) {
		if (text.equals("")) {
			return 0f;
		} else {
			return Float.valueOf(text);
		}
	}

	@Override
	public int calculateLength(String string) {
		String withoutDecimal = string;
		int decimalLocation = string.indexOf(".");
		if (decimalLocation != -1) {
			withoutDecimal = string.substring(0, decimalLocation);
		}
		return withoutDecimal.length();
	}
	
}
