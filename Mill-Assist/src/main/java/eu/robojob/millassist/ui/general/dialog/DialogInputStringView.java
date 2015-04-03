package eu.robojob.millassist.ui.general.dialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class DialogInputStringView extends AbstractDialogView<DialogInputStringPresenter> {

	private VBox vboxContents;
	private Label lblMessage;
	private HBox hboxButtons, hboxInputConfirmation;
	private Button btnCancel;
	private Button btnSave;
	private Label lblInput;
	private FullTextField fullTxtInput;
	private String message;
	private String inputLabelText;
	private StackPane keyboardPane;
	private static final String SAVE = "ConfirmationDialogView.save";
	private static final String CANCEL = "ConfirmationDialogView.cancel";
	private static final String CSS_BUTTON_LEFT = "left";
	private static final String CSS_BUTTON_RIGHT = "right";
	private static final String CSS_BUTTON = "dialog-btn";
	private static final String CSS_MESSAGE = "dialog-msg";
	private static final double WIDTH = 420;
	private static final double HEIGHT = 190;

	public DialogInputStringView(String title, String message, String inputLabelText) {
		super(title, HEIGHT);
		this.message = message;
		lblMessage.setText(message);
		this.inputLabelText = inputLabelText;
		lblInput.setText(inputLabelText);
		setPosition(Pos.TOP_CENTER, new Insets(50, 0, 0, 0));
	}

	@Override
	protected Node getContents(double height) {
		vboxContents = new VBox();
		vboxContents.setPrefWidth(WIDTH);
		StackPane spMessage = new StackPane();
		lblMessage = new Label();
		lblMessage.setText(message);
		lblMessage.setAlignment(Pos.CENTER);
		lblMessage.setPrefWidth(WIDTH);
		lblMessage.setMaxWidth(WIDTH);
		lblMessage.setMinWidth(WIDTH);
		lblMessage.setPrefHeight(Double.MAX_VALUE);
		lblMessage.getStyleClass().add(CSS_MESSAGE);
		lblMessage.setWrapText(true);
		spMessage.getChildren().add(lblMessage);
		spMessage.setAlignment(Pos.CENTER);
		spMessage.setPrefWidth(WIDTH);
		spMessage.setPrefHeight(height - UIConstants.BUTTON_HEIGHT - TITLE_HEIGHT);
		lblInput = new Label(inputLabelText);
		fullTxtInput = new FullTextField(100);
		fullTxtInput.setPrefWidth(280);
		fullTxtInput.setMinWidth(280);
		fullTxtInput.setMaxWidth(280);
		fullTxtInput.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtInput.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtInput.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtInput.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
				//
			}
		});
		hboxButtons = new HBox();
		hboxInputConfirmation = new HBox();
		btnCancel = new Button();
		btnCancel.setGraphic(new Text(Translator.getTranslation(CANCEL)));
		btnCancel.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_LEFT);
		btnCancel.setMinHeight(UIConstants.BUTTON_HEIGHT);
		btnCancel.setPrefHeight(UIConstants.BUTTON_HEIGHT);
		btnCancel.setPrefWidth(WIDTH/2 + 1);
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getPresenter().setResult("");
			}
		});
		btnSave = new Button();
		btnSave.setGraphic(new Text(Translator.getTranslation(SAVE)));
		btnSave.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_RIGHT);
		btnSave.setPrefHeight(UIConstants.BUTTON_HEIGHT);
		btnSave.setMinHeight(UIConstants.BUTTON_HEIGHT);
		btnSave.setPrefWidth(WIDTH/2);
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getPresenter().setResult(fullTxtInput.getText());
			}
		});
		hboxButtons.setSpacing(-1);
		hboxButtons.getChildren().addAll(btnCancel, btnSave);
		hboxButtons.setPrefSize(WIDTH, UIConstants.BUTTON_HEIGHT);
		HBox.setHgrow(btnCancel, Priority.ALWAYS);
		HBox.setHgrow(btnSave, Priority.ALWAYS);
		hboxInputConfirmation.setPrefSize(WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		hboxInputConfirmation.getChildren().addAll(lblInput, fullTxtInput);
		hboxInputConfirmation.setAlignment(Pos.CENTER);
		hboxInputConfirmation.setPadding(new Insets(0, 0, 10, 0));
		HBox.setHgrow(lblInput, Priority.ALWAYS);	
		HBox.setHgrow(fullTxtInput, Priority.ALWAYS);	
		vboxContents.getChildren().addAll(spMessage, hboxInputConfirmation, hboxButtons);
		VBox.setVgrow(spMessage, Priority.ALWAYS);
		setDialogSize(WIDTH, height);
		keyboardPane = new StackPane();
		keyboardPane.setMaxHeight(300);
		StackPane.setAlignment(keyboardPane, Pos.BOTTOM_LEFT);
		return vboxContents;
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtInput.setFocusListener(listener);
	}
	
	public void showKeyboardPane(final Node keyboardNode) {
		StackPane.setAlignment(keyboardPane, Pos.BOTTOM_LEFT);
		getChildren().remove(keyboardPane);
		keyboardPane.getChildren().clear();
		keyboardPane.getChildren().add(keyboardNode);
		getChildren().add(keyboardPane);
		keyboardPane.setMaxWidth(USE_PREF_SIZE);
		keyboardPane.setMaxHeight(250);
	}
	
	public void closeKeyboard() {
		getChildren().remove(keyboardPane);
		requestFocus();
	}
}