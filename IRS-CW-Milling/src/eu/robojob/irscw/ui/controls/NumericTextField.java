package eu.robojob.irscw.ui.controls;

public class NumericTextField extends TextField {

	public NumericTextField() {
	}

	@Override
	public String getMatchingExpression() {
		return "^[0-9]*\\.?[0-9]*$";
	}

}
