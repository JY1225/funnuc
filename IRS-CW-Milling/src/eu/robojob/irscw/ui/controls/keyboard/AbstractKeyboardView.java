package eu.robojob.irscw.ui.controls.keyboard;

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
	
	private GridPane gridPane;
	
	public void setPresenter(AbstractKeyboardPresenter presenter) {
		this.presenter = presenter;
	}
	
	protected void buildView() {
		this.getChildren().clear();
		
		gridPane = new GridPane();
		
		gridPane.setHgap(getSpacing());
		gridPane.setVgap(getSpacing());
		
		buildKeyboard();
		
		this.setPrefWidth(getPreferedWidth());
		this.getStyleClass().add("keyboard-background");
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
	
	protected void addKey(String text, KeyCode keyCode, int columnIndex, int rowIndex, int colspan, int rowspan, String id, String extraClassName) {
		Button btn = new Button();
		Text btnText = new Text(text);
		btn.setId(id);
		btn.setGraphic(btnText);
		btn.focusTraversableProperty().set(false);
		btn.setPrefSize((getButtonWidth()*colspan) + (colspan-1)*getSpacing(), (getButtonHeight()*rowspan) + (rowspan-1)*getSpacing());
		btn.getStyleClass().add("keyboard-button");
		if (extraClassName != null) {
			btn.getStyleClass().add(extraClassName);
		}
		btn.setOnAction(new KeyboardClickedEventHandler(keyCode));
		gridPane.add(btn, columnIndex, rowIndex, colspan, rowspan);
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
