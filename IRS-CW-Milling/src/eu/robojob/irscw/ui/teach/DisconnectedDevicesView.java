package eu.robojob.irscw.ui.teach;

import java.util.Set;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.util.Translator;

public class DisconnectedDevicesView extends VBox {

	private Label lblDisconnectedDevices;
	private Label lblDisconnectedList;
		
	private Translator translator;
		
	public DisconnectedDevicesView() {
		translator = Translator.getInstance();
		build();
	}
	
	private void build() {
		lblDisconnectedDevices = new Label(translator.getTranslation("disconnected-devices"));
		lblDisconnectedDevices.getStyleClass().addAll("teach-msg", "lbl-disconnected");
		setMargin(lblDisconnectedDevices, new Insets(50, 0, 0, 0));
		lblDisconnectedDevices.setPrefSize(520, 100);
		lblDisconnectedDevices.setWrapText(true);
		
		lblDisconnectedList = new Label("");
		lblDisconnectedList.getStyleClass().addAll("teach-msg", "lbl-disconnected-list");
		lblDisconnectedList.setPrefWidth(520);
		
		this.getChildren().add(lblDisconnectedDevices);
		this.getChildren().add(lblDisconnectedList);
	}
	
	private void clearList() {
		lblDisconnectedList.setText("");
	}
	
	private void addDevice(String deviceName) {
		lblDisconnectedList.setText(lblDisconnectedList.getText() + "\n" + deviceName);
	}
	
	public void setDisconnectedDevices(Set<String> disconnectedDevices) {
		clearList();
		for (String name : disconnectedDevices) {
			addDevice(name);
		}
	}
}
