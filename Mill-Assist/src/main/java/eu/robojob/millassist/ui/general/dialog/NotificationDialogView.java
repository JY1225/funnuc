package eu.robojob.millassist.ui.general.dialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class NotificationDialogView extends AbstractDialogView<NotificationDialogPresenter> {

	private VBox vboxContents;
	private Label lblMessage;
	private HBox hboxButtons;
	private Button btnOk;
	
	private String message;
	
	private static final String OK = "ConfirmationDialogView.ok";
	private static final String CSS_BUTTON_RIGHT = "right";
	private static final String CSS_BUTTON = "dialog-btn";
	private static final String CSS_MESSAGE = "dialog-msg";
	
	private static final double WIDTH = 420;
	private static final double HEIGHT = 170;
	
	public NotificationDialogView(String title, String message) {
		super(title);
		this.message = message;
		lblMessage.setText(message);
	}

	@Override
	protected Node getContents() {
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
		spMessage.setPrefHeight(HEIGHT - UIConstants.BUTTON_HEIGHT - TITLE_HEIGHT);
		hboxButtons = new HBox();
		btnOk = new Button();
		btnOk.setGraphic(new Text(Translator.getTranslation(OK)));
		btnOk.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_RIGHT);
		btnOk.setPrefHeight(UIConstants.BUTTON_HEIGHT);
		btnOk.setPrefWidth(WIDTH);
		btnOk.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getPresenter().setResult(true);
			}
		});
		hboxButtons.setSpacing(-1);
		hboxButtons.getChildren().add(btnOk);
		hboxButtons.setPrefSize(WIDTH, UIConstants.BUTTON_HEIGHT);
		HBox.setHgrow(btnOk, Priority.ALWAYS);
		vboxContents.getChildren().addAll(spMessage, hboxButtons);
		VBox.setVgrow(spMessage, Priority.ALWAYS);
		setDialogSize(WIDTH, HEIGHT);
		return vboxContents;
	}

}
