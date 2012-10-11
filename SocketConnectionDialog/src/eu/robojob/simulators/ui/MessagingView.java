package eu.robojob.simulators.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class MessagingView extends GridPane {

	private MessagingPresenter presenter;

	private ScrollPane scrpLog;
	private Label lblLog;
	private TextField tfMessage;
	private Button btnSend;
	private Button btnDisconnect;
	
	public MessagingView() {
		build();
	}

	private void build() {
		scrpLog = new ScrollPane();
		lblLog = new Label();
		lblLog.getStyleClass().add("lbl-log");
		scrpLog.setContent(lblLog);
		scrpLog.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrpLog.getStyleClass().add("scrp-log");
		tfMessage = new TextField();
		tfMessage.getStyleClass().add("txt");
		tfMessage.setMinWidth(100);
		tfMessage.setMaxWidth(Double.MAX_VALUE);
		tfMessage.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.sendMessage(tfMessage.getText());
			}
		});
		setMargin(tfMessage, new Insets(10,10,10,10));
		btnSend = new Button();
		btnSend.setGraphic(new Text("Verstuur"));
		btnSend.setPrefSize(150, 35);
		setMargin(btnSend, new Insets(10, 10, 10, 10));
		btnSend.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.sendMessage(tfMessage.getText());
			}
		});
		setAlignment(Pos.CENTER);
		btnDisconnect = new Button();
		btnDisconnect.setGraphic(new Text("Verbreek verbinding"));
		btnDisconnect.setPrefSize(150, 35);
		setMargin(btnDisconnect, new Insets(10, 10, 10, 10));
		btnDisconnect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.disconnect();
			}
		});
		setHgrow(scrpLog, Priority.ALWAYS);
		setVgrow(scrpLog, Priority.ALWAYS);
		setHgrow(tfMessage, Priority.ALWAYS);
		add(scrpLog, 0, 0, 3, 1);
		add(tfMessage, 0, 1);
		add(btnSend, 1, 1);
		add(btnDisconnect, 2, 1);
	}
	
	public void setPresenter(MessagingPresenter presenter) {
		this.presenter = presenter;
	}
	
	public void setButtonEnabled(boolean enabled) {
		if (enabled) {
			btnSend.setDisable(!enabled);
		} else {
			btnSend.setDisable(enabled);
		}
	}
	
	public void setMessage(String message) {
		lblLog.setText(message);
		scrpLog.setVvalue(lblLog.getHeight() + 40);
	}
	
	public void setConnected(boolean connected) {
		tfMessage.setDisable(!connected);
		btnSend.setDisable(!connected);
		btnDisconnect.setDisable(!connected);
	}
	
	public void clearLog() {
		lblLog.setText("");
	}
	
	public void clearText() {
		tfMessage.setText("");
	}
}
