package eu.robojob.irscw.ui.controls.keyboard;

import javafx.scene.input.KeyCode;

public class FullKeyboardView extends AbstractKeyboardView {
	
	public enum KeyboardType {
		AZERTY, QWERTY, QWERTZ_DE
	}
	
	private KeyboardType type;
	
	public FullKeyboardView(final KeyboardType type) {
		this.type = type;
		buildView();
	}
	
	public void changeType(final KeyboardType type) {
		this.type = type;
		buildView();
	}
	
	@Override
	public double getPreferedWidth() {
		return 800;
	}
	
	@Override
	protected void buildKeyboard() {
		switch(type) {
			case AZERTY:
				buildViewAzerty();
				break;
			case QWERTY:
				buildViewQwerty();
				break;
			case QWERTZ_DE:
				buildViewQwertyDE();
				break;
			default:
				buildViewAzerty();
				break;
		}
	}
	
	private void buildViewQwertyDE() {
		int row = 0;
		int column = 0;
		
		addKey("Esc", KeyCode.ESCAPE, column++, row, 1, 1, "key-escape", null);
		addKey("1", KeyCode.DIGIT1, column++, row, 1, 1, "key-1", null);
		addKey("2", KeyCode.DIGIT2, column++, row, 1, 1, "key-2", null);
		addKey("3", KeyCode.DIGIT3, column++, row, 1, 1, "key-3", null);
		addKey("4", KeyCode.DIGIT4, column++, row, 1, 1, "key-4", null);
		addKey("5", KeyCode.DIGIT5, column++, row, 1, 1, "key-5", null);
		addKey("6", KeyCode.DIGIT6, column++, row, 1, 1, "key-6", null);
		addKey("7", KeyCode.DIGIT7, column++, row, 1, 1, "key-7", null);
		addKey("8", KeyCode.DIGIT8, column++, row, 1, 1, "key-8", null);
		addKey("9", KeyCode.DIGIT9, column++, row, 1, 1, "key-9", null);
		addKey("0", KeyCode.DIGIT0, column++, row, 1, 1, "key-0", null);
		addKey(".", KeyCode.DECIMAL, column++, row, 1, 1, "key-decimal", null);
		
		column = 0;
		row++;
		addKey("Q", KeyCode.Q, column++, row, 1, 1, "key-Q", null);
		addKey("W", KeyCode.W, column++, row, 1, 1, "key-W", null);
		addKey("E", KeyCode.E, column++, row, 1, 1, "key-E", null);
		addKey("R", KeyCode.R, column++, row, 1, 1, "key-R", null);
		addKey("T", KeyCode.T, column++, row, 1, 1, "key-T", null);
		addKey("Z", KeyCode.Z, column++, row, 1, 1, "key-Z", null);
		addKey("U", KeyCode.U, column++, row, 1, 1, "key-U", null);
		addKey("I", KeyCode.I, column++, row, 1, 1, "key-I", null);
		addKey("O", KeyCode.O, column++, row, 1, 1, "key-O", null);
		addKey("P", KeyCode.P, column++, row, 1, 1, "key-P", null);
		addKey("Ü", KeyCode.COLORED_KEY_0, column++, row, 1, 1, "key-U-special", null);
		addKey("Clr", KeyCode.DELETE, column++, row, 1, 1, "key-Clr", null);
		
		column = 0;
		row++;
		addKey("A", KeyCode.A, column++, row, 1, 1, "key-A", null);
		addKey("S", KeyCode.S, column++, row, 1, 1, "key-S", null);
		addKey("D", KeyCode.D, column++, row, 1, 1, "key-D", null);
		addKey("F", KeyCode.F, column++, row, 1, 1, "key-F", null);
		addKey("G", KeyCode.G, column++, row, 1, 1, "key-G", null);
		addKey("H", KeyCode.H, column++, row, 1, 1, "key-H", null);
		addKey("J", KeyCode.J, column++, row, 1, 1, "key-J", null);
		addKey("K", KeyCode.K, column++, row, 1, 1, "key-K", null);
		addKey("L", KeyCode.L, column++, row, 1, 1, "key-L", null);
		addKey("Ö", KeyCode.COLORED_KEY_1, column++, row, 1, 1, "key-O-special", null);
		addKey("Ä", KeyCode.COLORED_KEY_2, column++, row, 1, 1, "key-A-special", null);
		addKey("\u2190", KeyCode.BACK_SPACE, column++, row, 1, 1, "key-backspace", null);
		
		column = 0;
		row++;
		addKey("-", KeyCode.MINUS, column++, row, 1, 1, "key-minus", null);
		addKey("Y", KeyCode.Y, column++, row, 1, 1, "key-Y", null);
		addKey("X", KeyCode.X, column++, row, 1, 1, "key-X", null);
		addKey("C", KeyCode.C, column++, row, 1, 1, "key-C", null);
		addKey("V", KeyCode.V, column++, row, 1, 1, "key-V", null);
		addKey("B", KeyCode.B, column++, row, 1, 1, "key-B", null);
		addKey("N", KeyCode.N, column++, row, 1, 1, "key-N", null);
		addKey("M", KeyCode.M, column++, row, 1, 1, "key-M", null);
		addKey("_", KeyCode.UNDERSCORE, column++, row, 1, 1, "key-underscore", null);
		addKey("", KeyCode.SPACE, column++, row, 2, 1, "key-space", null);
		column++;
		addKey("OK", KeyCode.ENTER, column++, row, 1, 1, "key-OK", null);
	}
	
	private void buildViewQwerty() {
		int row = 0;
		int column = 0;
		
		addKey("Esc", KeyCode.ESCAPE, column++, row, 1, 1, "key-escape", null);
		addKey("1", KeyCode.DIGIT1, column++, row, 1, 1, "key-1", null);
		addKey("2", KeyCode.DIGIT2, column++, row, 1, 1, "key-2", null);
		addKey("3", KeyCode.DIGIT3, column++, row, 1, 1, "key-3", null);
		addKey("4", KeyCode.DIGIT4, column++, row, 1, 1, "key-4", null);
		addKey("5", KeyCode.DIGIT5, column++, row, 1, 1, "key-5", null);
		addKey("6", KeyCode.DIGIT6, column++, row, 1, 1, "key-6", null);
		addKey("7", KeyCode.DIGIT7, column++, row, 1, 1, "key-7", null);
		addKey("8", KeyCode.DIGIT8, column++, row, 1, 1, "key-8", null);
		addKey("9", KeyCode.DIGIT9, column++, row, 1, 1, "key-9", null);
		addKey("0", KeyCode.DIGIT0, column++, row, 1, 1, "key-0", null);
		addKey(".", KeyCode.DECIMAL, column++, row, 1, 1, "key-decimal", null);
		
		column = 0;
		row++;
		addKey("Q", KeyCode.Q, column++, row, 1, 1, "key-Q", null);
		addKey("W", KeyCode.W, column++, row, 1, 1, "key-W", null);
		addKey("E", KeyCode.E, column++, row, 1, 1, "key-E", null);
		addKey("R", KeyCode.R, column++, row, 1, 1, "key-R", null);
		addKey("T", KeyCode.T, column++, row, 1, 1, "key-T", null);
		addKey("Y", KeyCode.Y, column++, row, 1, 1, "key-Y", null);
		addKey("U", KeyCode.U, column++, row, 1, 1, "key-U", null);
		addKey("I", KeyCode.I, column++, row, 1, 1, "key-I", null);
		addKey("O", KeyCode.O, column++, row, 1, 1, "key-O", null);
		addKey("P", KeyCode.P, column++, row, 1, 1, "key-P", null);
		column++;
		addKey("Clr", KeyCode.DELETE, column++, row, 1, 1, "key-Clr", null);
		
		column = 0;
		row++;
		addKey("A", KeyCode.A, column++, row, 1, 1, "key-A", null);
		addKey("S", KeyCode.S, column++, row, 1, 1, "key-S", null);
		addKey("D", KeyCode.D, column++, row, 1, 1, "key-D", null);
		addKey("F", KeyCode.F, column++, row, 1, 1, "key-F", null);
		addKey("G", KeyCode.G, column++, row, 1, 1, "key-G", null);
		addKey("H", KeyCode.H, column++, row, 1, 1, "key-H", null);
		addKey("J", KeyCode.J, column++, row, 1, 1, "key-J", null);
		addKey("K", KeyCode.K, column++, row, 1, 1, "key-K", null);
		addKey("L", KeyCode.L, column++, row, 1, 1, "key-L", null);
		column++;
		column++;
		addKey("\u2190", KeyCode.BACK_SPACE, column++, row, 1, 1, "key-backspace", null);
		
		column = 0;
		row++;
		addKey("-", KeyCode.MINUS, column++, row, 1, 1, "key-minus", null);
		addKey("Z", KeyCode.Z, column++, row, 1, 1, "key-Z", null);
		addKey("X", KeyCode.X, column++, row, 1, 1, "key-X", null);
		addKey("C", KeyCode.C, column++, row, 1, 1, "key-C", null);
		addKey("V", KeyCode.V, column++, row, 1, 1, "key-V", null);
		addKey("B", KeyCode.B, column++, row, 1, 1, "key-B", null);
		addKey("N", KeyCode.N, column++, row, 1, 1, "key-N", null);
		addKey("M", KeyCode.M, column++, row, 1, 1, "key-M", null);
		addKey("_", KeyCode.UNDERSCORE, column++, row, 1, 1, "key-underscore", null);
		addKey("", KeyCode.SPACE, column++, row, 2, 1, "key-space", null);
		column++;
		addKey("OK", KeyCode.ENTER, column++, row, 1, 1, "key-OK", null);
	}
	
	private void buildViewAzerty() {
		
		int row = 0;
		int column = 0;
		
		addKey("Esc", KeyCode.ESCAPE, column++, row, 1, 1, "key-escape", null);
		addKey("1", KeyCode.DIGIT1, column++, row, 1, 1, "key-1", null);
		addKey("2", KeyCode.DIGIT2, column++, row, 1, 1, "key-2", null);
		addKey("3", KeyCode.DIGIT3, column++, row, 1, 1, "key-3", null);
		addKey("4", KeyCode.DIGIT4, column++, row, 1, 1, "key-4", null);
		addKey("5", KeyCode.DIGIT5, column++, row, 1, 1, "key-5", null);
		addKey("6", KeyCode.DIGIT6, column++, row, 1, 1, "key-6", null);
		addKey("7", KeyCode.DIGIT7, column++, row, 1, 1, "key-7", null);
		addKey("8", KeyCode.DIGIT8, column++, row, 1, 1, "key-8", null);
		addKey("9", KeyCode.DIGIT9, column++, row, 1, 1, "key-9", null);
		addKey("0", KeyCode.DIGIT0, column++, row, 1, 1, "key-0", null);
		addKey(".", KeyCode.DECIMAL, column++, row, 1, 1, "key-decimal", null);
		
		column = 1;
		row++;
		addKey("A", KeyCode.A, column++, row, 1, 1, "key-A", null);
		addKey("Z", KeyCode.Z, column++, row, 1, 1, "key-Z", null);
		addKey("E", KeyCode.E, column++, row, 1, 1, "key-E", null);
		addKey("R", KeyCode.R, column++, row, 1, 1, "key-R", null);
		addKey("T", KeyCode.T, column++, row, 1, 1, "key-T", null);
		addKey("Y", KeyCode.Y, column++, row, 1, 1, "key-Y", null);
		addKey("U", KeyCode.U, column++, row, 1, 1, "key-U", null);
		addKey("I", KeyCode.I, column++, row, 1, 1, "key-I", null);
		addKey("O", KeyCode.O, column++, row, 1, 1, "key-O", null);
		addKey("P", KeyCode.P, column++, row, 1, 1, "key-P", null);
		addKey("Clr", KeyCode.DELETE, column++, row, 1, 1, "key-Clr", null);
		
		column = 1;
		row++;
		addKey("Q", KeyCode.Q, column++, row, 1, 1, "key-Q", null);
		addKey("S", KeyCode.S, column++, row, 1, 1, "key-S", null);
		addKey("D", KeyCode.D, column++, row, 1, 1, "key-D", null);
		addKey("F", KeyCode.F, column++, row, 1, 1, "key-F", null);
		addKey("G", KeyCode.G, column++, row, 1, 1, "key-G", null);
		addKey("H", KeyCode.H, column++, row, 1, 1, "key-H", null);
		addKey("J", KeyCode.J, column++, row, 1, 1, "key-J", null);
		addKey("K", KeyCode.K, column++, row, 1, 1, "key-K", null);
		addKey("L", KeyCode.L, column++, row, 1, 1, "key-L", null);
		addKey("M", KeyCode.M, column++, row, 1, 1, "key-M", null);
		addKey("\u2190", KeyCode.BACK_SPACE, column++, row, 1, 1, "key-backspace", null);
		
		column = 1;
		row++;
		addKey("-", KeyCode.MINUS, column++, row, 1, 1, "key-minus", null);
		addKey("_", KeyCode.UNDERSCORE, column++, row, 1, 1, "key-underscore", null);
		addKey("W", KeyCode.W, column++, row, 1, 1, "key-W", null);
		addKey("X", KeyCode.X, column++, row, 1, 1, "key-X", null);
		addKey("C", KeyCode.C, column++, row, 1, 1, "key-C", null);
		addKey("V", KeyCode.V, column++, row, 1, 1, "key-V", null);
		addKey("B", KeyCode.B, column++, row, 1, 1, "key-B", null);
		addKey("N", KeyCode.N, column++, row, 1, 1, "key-N", null);
		addKey("", KeyCode.SPACE, column++, row, 2, 1, "key-space", null);
		column++;
		addKey("OK", KeyCode.ENTER, column++, row, 1, 1, "key-OK", null);
		
	}
	
	public void setPresenter(final FullKeyboardPresenter presenter) {
		super.setPresenter(presenter);
	}

}
