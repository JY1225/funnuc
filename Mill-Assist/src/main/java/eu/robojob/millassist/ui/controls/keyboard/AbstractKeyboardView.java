package eu.robojob.millassist.ui.controls.keyboard;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public abstract class AbstractKeyboardView extends GridPane {

	private AbstractKeyboardPresenter presenter;
	
	private static final int BTN_WIDTH = 50;
	private static final int BTN_HEIGHT = 50;
	private static final int SPACING = 5;
	private static final int MARGIN = 15;
	
	private static final String CSS_CLASS_KEYBOARD_BACKGROUND = "keyboard-background";
	private static final String CSS_CLASS_KEYBOARD_BUTTON = "keyboard-button";
	
	private GridPane gridPane;
	
	public void setPresenter(final AbstractKeyboardPresenter presenter) {
		this.presenter = presenter;
	}
	
	protected void buildView() {
		this.getChildren().clear();
		
		gridPane = new GridPane();
		
		gridPane.setHgap(getSpacing());
		gridPane.setVgap(getSpacing());
		
		buildKeyboard();
		
		this.setPrefWidth(getPreferedWidth());
		this.getStyleClass().add(CSS_CLASS_KEYBOARD_BACKGROUND);
		setAlignment(Pos.CENTER);
		setMargin(gridPane, new Insets(getMargin(), 0, getMargin(), 0));
		
		this.getChildren().add(gridPane);
	}
	
	protected int getSpacing() {
		return SPACING;
	}
	
	protected int getButtonWidth() {
		return BTN_WIDTH;
	}
	
	protected int getButtonHeight() {
		return BTN_HEIGHT;
	}
	
	protected int getMargin() {
		return MARGIN;
	}
	
	protected abstract void buildKeyboard();
	protected abstract double getPreferedWidth();
	
	protected void addKey(final String text, final KeyCode keyCode, final int columnIndex, final int rowIndex, final int colspan, final int rowspan, final String id, final String extraClassName) {
		Button btn = new Button();
		Text btnText = new Text(text);
		btn.setId(id);
		btn.setGraphic(btnText);
		btn.focusTraversableProperty().set(false);
		btn.setPrefSize((getButtonWidth() * colspan) + (colspan - 1) * getSpacing(), (getButtonHeight() * rowspan) + (rowspan - 1) * getSpacing());
		btn.getStyleClass().add(CSS_CLASS_KEYBOARD_BUTTON);
		if (extraClassName != null) {
			btn.getStyleClass().add(extraClassName);
		}
		btn.setOnAction(new KeyboardClickedEventHandler(keyCode));
		gridPane.add(btn, columnIndex, rowIndex, colspan, rowspan);
	}
	
	private class KeyboardClickedEventHandler implements EventHandler<ActionEvent> {
		
		private KeyCode keyCode;

		public KeyboardClickedEventHandler(final KeyCode keyCode) {
			this.keyCode = keyCode;
		}
		
		@Override
		public void handle(final ActionEvent event) {
			presenter.keyPressed(keyCode);
		}
		
	}
}
