package eu.robojob.irscw.ui.controls;

import java.text.DecimalFormat;

public class NumericTextField extends AbstractTextField {

	public NumericTextField(int maxLength) {
		super(maxLength);
	}

	@Override
	public String getMatchingExpression() {
		return "^[0-9]*\\.?[0-9]*$";
	}

	@Override
	public void cleanText() {
		if (!this.getText().equals("")) {
			DecimalFormat formatter = new DecimalFormat("#0.00");
			formatter.setDecimalSeparatorAlwaysShown(true);
			setText(formatter.format(Float.valueOf(this.getText())));
		}
	}
	
}
