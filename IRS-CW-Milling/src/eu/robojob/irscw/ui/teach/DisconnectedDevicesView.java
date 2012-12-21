package eu.robojob.irscw.ui.teach;

import java.util.Set;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.util.Translator;

public class DisconnectedDevicesView extends VBox {

	private Label lblDisconnectedDevices;
	private Label lblDisconnectedList;
		
	private static final String DISCONNECTED_DEVICES = "DisconnectedDevicesView.disconnected-devices";
	private static final String CSS_CLASS_DISCONNECTED = "lbl-disconnected";
	private static final String CSS_CLASS_DISCONNECTED_LIST = "lbl-disconnected-list";
	
	public DisconnectedDevicesView() {
		build();
	}
	
	private void build() {
		lblDisconnectedDevices = new Label(Translator.getTranslation(DISCONNECTED_DEVICES));
		lblDisconnectedDevices.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_DISCONNECTED);
		setMargin(lblDisconnectedDevices, new Insets(50, 0, 0, 0));
		lblDisconnectedDevices.setPrefSize(520, 100);
		lblDisconnectedDevices.setWrapText(true);
		
		lblDisconnectedList = new Label("");
		lblDisconnectedList.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_DISCONNECTED_LIST);
		lblDisconnectedList.setPrefWidth(520);
		
		this.getChildren().add(lblDisconnectedDevices);
		this.getChildren().add(lblDisconnectedList);
	}
	
	private void clearList() {
		lblDisconnectedList.setText("");
	}
	
	private void addDevice(final String deviceName) {
		lblDisconnectedList.setText(lblDisconnectedList.getText() + "\n" + deviceName);
	}
	
	public void setDisconnectedDevices(final Set<String> disconnectedDevices) {
		clearList();
		for (String name : disconnectedDevices) {
			addDevice(name);
		}
	}
}
