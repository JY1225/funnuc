package eu.robojob.irscw.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class KeyboardView extends GridPane {
	
	private KeyboardPresenter presenter;
	private GridPane gridPane;
	
	private static final int BTN_WIDTH = 45;
	private static final int BTN_HEIGHT = 45;
	private static final int SPACING = 12;
	
	private static final int MARGIN = 15;
	
	public KeyboardView() {
		super();
		buildView();
	}
	
	private void buildView() {
			
		gridPane = new GridPane();
		
		gridPane.setHgap(SPACING);
		gridPane.setVgap(SPACING);
		
		addKey("Esc", KeyCode.ESCAPE, 0, 0, 1, 1, "key-escape", null);
		addKey("1", KeyCode.DIGIT1, 1, 0, 1, 1, "key-1", null);
		addKey("2", KeyCode.DIGIT2, 2, 0, 1, 1, "key-2", null);
		addKey("3", KeyCode.DIGIT3, 3, 0, 1, 1, "key-3", null);
		addKey("4", KeyCode.DIGIT4, 4, 0, 1, 1, "key-4", null);
		addKey("5", KeyCode.DIGIT5, 5, 0, 1, 1, "key-5", null);
		addKey("6", KeyCode.DIGIT6, 6, 0, 1, 1, "key-6", null);
		addKey("7", KeyCode.DIGIT7, 7, 0, 1, 1, "key-7", null);
		addKey("8", KeyCode.DIGIT8, 8, 0, 1, 1, "key-8", null);
		addKey("9", KeyCode.DIGIT9, 9, 0, 1, 1, "key-9", null);
		addKey("0", KeyCode.DIGIT0, 10, 0, 1, 1, "key-0", null);
		addKey(".", KeyCode.DECIMAL, 11, 0, 1, 1, "key-decimal", null);
		
		addKey("A", KeyCode.A, 1, 1, 1, 1, "key-A", null);
		addKey("Z", KeyCode.Z, 2, 1, 1, 1, "key-Z", null);
		addKey("E", KeyCode.E, 3, 1, 1, 1, "key-E", null);
		addKey("R", KeyCode.R, 4, 1, 1, 1, "key-R", null);
		addKey("T", KeyCode.T, 5, 1, 1, 1, "key-T", null);
		addKey("Y", KeyCode.Y, 6, 1, 1, 1, "key-Y", null);
		addKey("U", KeyCode.U, 7, 1, 1, 1, "key-U", null);
		addKey("I", KeyCode.I, 8, 1, 1, 1, "key-I", null);
		addKey("O", KeyCode.O, 9, 1, 1, 1, "key-O", null);
		addKey("P", KeyCode.P, 10, 1, 1, 1, "key-P", null);
		addKey("Clr", KeyCode.DELETE, 11, 1, 1, 1, "key-Clr", null);
		
		addKey("Q", KeyCode.Q, 1, 2, 1, 1, "key-Q", null);
		addKey("S", KeyCode.S, 2, 2, 1, 1, "key-S", null);
		addKey("D", KeyCode.D, 3, 2, 1, 1, "key-D", null);
		addKey("F", KeyCode.F, 4, 2, 1, 1, "key-F", null);
		addKey("G", KeyCode.G, 5, 2, 1, 1, "key-G", null);
		addKey("H", KeyCode.H, 6, 2, 1, 1, "key-H", null);
		addKey("J", KeyCode.J, 7, 2, 1, 1, "key-J", null);
		addKey("K", KeyCode.K, 8, 2, 1, 1, "key-K", null);
		addKey("L", KeyCode.L, 9, 2, 1, 1, "key-L", null);
		addKey("M", KeyCode.M, 10, 2, 1, 1, "key-M", null);
		addKey("←", KeyCode.BACK_SPACE, 11, 2, 1, 1, "key-back-space", null);
		
		addKey("-", KeyCode.MINUS, 1, 3, 1, 1, "key-minus", null);
		addKey("_", KeyCode.UNDERSCORE, 2, 3, 1, 1, "key-underscore", null);
		addKey("W", KeyCode.W, 3, 3, 1, 1, "key-W", null);
		addKey("X", KeyCode.X, 4, 3, 1, 1, "key-X", null);
		addKey("C", KeyCode.C, 5, 3, 1, 1, "key-C", null);
		addKey("V", KeyCode.V, 6, 3, 1, 1, "key-V", null);
		addKey("B", KeyCode.B, 7, 3, 1, 1, "key-B", null);
		addKey("N", KeyCode.N, 8, 3, 1, 1, "key-N", null);
		addKey("", KeyCode.SPACE, 9, 3, 2, 1, "key-space", null);
		addKey("OK", KeyCode.ENTER, 11, 3, 1, 1, "key-OK", null);
		
		this.setPrefWidth(800);
		this.getStyleClass().add("keyboard-background");
		setAlignment(Pos.CENTER);
		setMargin(gridPane, new Insets(MARGIN, 0, MARGIN, 0));
		
		this.getChildren().add(gridPane);
		
	}
	
	private void addKey(String text, KeyCode keyCode, int columnIndex, int rowIndex, int colspan, int rowspan, String id, String extraClassName) {
		Button btn = new Button();
		Text btnText = new Text(text);
		btn.setId(id);
		btn.setGraphic(btnText);
		btn.setPrefSize((BTN_WIDTH*colspan) + (colspan-1)*SPACING, (BTN_HEIGHT*rowspan) + (rowspan-1)*SPACING);
		btn.getStyleClass().add("keyboard-button");
		if (extraClassName != null) {
			btn.getStyleClass().add(extraClassName);
		}
		btn.setOnAction(new KeyboardClickedEventHandler(keyCode));
		gridPane.add(btn, columnIndex, rowIndex, colspan, rowspan);
	}
	
	public void setPresenter(KeyboardPresenter presenter) {
		this.presenter = presenter;
	}
	
	private class KeyboardClickedEventHandler implements EventHandler<ActionEvent> {
		
		private KeyCode keyCode;

		public KeyboardClickedEventHandler(KeyCode keyCode) {
			this.keyCode = keyCode;
		}
		
		@Override
		public void handle(ActionEvent event) {
			presenter.keyPressed(keyCode);
		}
		
	}
}
