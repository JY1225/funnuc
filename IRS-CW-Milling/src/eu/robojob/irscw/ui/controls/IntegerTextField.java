package eu.robojob.irscw.ui.controls;

import java.text.DecimalFormat;

public class IntegerTextField extends AbstractTextField<Integer> {

	public IntegerTextField(int maxLength) {
		super(maxLength);
		this.getStyleClass().add("numeric-text-field");
	}

	@Override
	public String getMatchingExpression() {
		return "^[0-9]*$";
	}

	@Override
	public int calculateLength(String string) {
		return string.length();
	}

	@Override
	public void cleanText() {
		DecimalFormat formatter = new DecimalFormat("#0");
		formatter.setDecimalSeparatorAlwaysShown(false);
		if (!this.getText().equals("")) {
			setText(formatter.format(Float.valueOf(this.getText())));
		} else {
			setText(formatter.format(Float.valueOf("0")));
		}
	}

	@Override
	public Integer convertString(String text) {
		if (text.equals("")) {
			return 0;
		} else {
			return Integer.valueOf(text);
		}
	}

}
