package eu.robojob.simulators.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class ConnectionView extends GridPane {

	private Label lblPortNumber;
	private TextField tfPortNumber;
	private Label lblIpAddress;
	private TextField tfIpAddress;
	private Button btnAcceptConnections;
	
	private ComboBox<String> cbbType;
	
	private ConnectionPresenter presenter;
	
	private int portNumber;
	
	public ConnectionView() {
		build();
	}
	
	public void setPresenter(ConnectionPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		this.lblPortNumber = new Label("Poortnummer");
		lblPortNumber.getStyleClass().add("info-label");
		this.tfPortNumber = new TextField();
		tfPortNumber.getStyleClass().add("txt");
		tfPortNumber.setPrefWidth(75);
		tfPortNumber.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				String portString = tfPortNumber.getText();
				try {
					Integer i = Integer.parseInt(portString);
					setPortNumber(i);
					btnAcceptConnections.setDisable(false);
				} catch(NumberFormatException e) {
					setPortNumber(-1);
					btnAcceptConnections.setDisable(true);
				}
			}
		});
		lblIpAddress = new Label("IP-adres");
		lblIpAddress.getStyleClass().add("info-label");
		tfIpAddress = new TextField();
		tfIpAddress.getStyleClass().add("txt");
		tfIpAddress.setPrefWidth(75);
		cbbType = new ComboBox<String>();
		cbbType.getItems().addAll("Client", "Server");
		cbbType.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2.equals("Server")) {
					lblIpAddress.setDisable(true);
					tfIpAddress.setDisable(true);
				} else {
					lblIpAddress.setDisable(false);
					tfIpAddress.setDisable(false);
				}
			}
		});
		cbbType.valueProperty().set("Server");
		setHalignment(cbbType, HPos.CENTER);
		cbbType.setPrefWidth(150);
		btnAcceptConnections = new Button();
		btnAcceptConnections.setGraphic(new Text("Verbinden"));
		btnAcceptConnections.setPrefSize(150, 35);
		setMargin(btnAcceptConnections, new Insets(10, 10, 10, 10));
		setAlignment(Pos.CENTER);
		setHalignment(btnAcceptConnections, HPos.CENTER);
		btnAcceptConnections.setAlignment(Pos.CENTER);
		btnAcceptConnections.getStyleClass().add("button");
		btnAcceptConnections.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.connect();
			}
		});
		btnAcceptConnections.setDisable(true);
		btnAcceptConnections.setAlignment(Pos.CENTER);
		add(lblPortNumber, 0, 0);
		add(tfPortNumber, 1, 0);
		add(lblIpAddress, 0, 1);
		add(tfIpAddress, 1, 1);
		add(cbbType, 0, 2, 2, 1);
		add(btnAcceptConnections, 0, 3, 2, 1);
		/*ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(50);
		RowConstraints row1=new RowConstraints();
		row1.setPercentHeight(50);
		RowConstraints row2=new RowConstraints();
		row2.setPercentHeight(50);
		getColumnConstraints().addAll(column1, column2);
		getRowConstraints().addAll(row1, row2);*/
	}
	
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public String getType() {
		return cbbType.valueProperty().getValue();
	}
	
	public String getIpAddress() {
		return tfIpAddress.getText();
	}
}
