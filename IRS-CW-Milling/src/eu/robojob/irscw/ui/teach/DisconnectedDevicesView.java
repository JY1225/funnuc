package eu.robojob.irscw.ui.teach;

import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import eu.robojob.irscw.util.Translator;

public class DisconnectedDevicesView extends VBox {

	private Label lblDisconnectedDevicesTitle;
	private Label lblDisconnectedDevices;
	private Label lblDisconnectedList;
	private SVGPath warningIcon;
	
	private static final String DISCONNECTED_DEVICES_TITLE = "DisconnectedDevicesView.disconnectedDevicesTitle";
	private static final String DISCONNECTED_DEVICES = "DisconnectedDevicesView.disconnectedDevices";
	private static final String CSS_CLASS_DISCONNECTED = "lbl-disconnected";
	private static final String CSS_CLASS_DISCONNECTED_LIST = "lbl-disconnected-list";
	
	private static final String WARNING_ICON_PATH = "M 12.53125 0 C 12.024848 -0.00011109284 11.509368 0.28555607 11.125 0.875 L 0.3125 17.40625 C -0.45649646 18.585582 0.23029693 19.59375 1.8125 19.59375 L 23.1875 19.59375 C 24.769964 19.59375 25.455456 18.586026 24.6875 17.40625 L 13.90625 0.875 C 13.522402 0.28511238 13.037652 0.00011075102 12.53125 0 z M 10.90625 5.21875 L 14.09375 5.21875 L 14.09375 7.40625 L 13.40625 13.375 L 11.59375 13.375 L 10.90625 7.40625 L 10.90625 5.21875 z M 11.0625 14.71875 L 13.9375 14.71875 L 13.9375 17.1875 L 11.0625 17.1875 L 11.0625 14.71875 z";
	
	public DisconnectedDevicesView() {
		build();
	}
	
	private void build() {
		setPrefHeight(TeachView.HEIGHT_BOTTOM);
		this.setAlignment(Pos.CENTER);
		
		warningIcon = new SVGPath();
		warningIcon.setContent(WARNING_ICON_PATH);
		warningIcon.getStyleClass().add(TeachView.CSS_CLASS_WARNING_ICON);
		
		lblDisconnectedDevicesTitle = new Label(Translator.getTranslation(DISCONNECTED_DEVICES_TITLE));
		lblDisconnectedDevicesTitle.getStyleClass().add(TeachView.CSS_CLASS_INFO_MESSAGE_TITLE);
		
		lblDisconnectedDevices = new Label(Translator.getTranslation(DISCONNECTED_DEVICES));
		lblDisconnectedDevices.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_DISCONNECTED);
		lblDisconnectedDevices.setPrefSize(470, 60);
		lblDisconnectedDevices.setWrapText(true);
		
		lblDisconnectedList = new Label("");
		lblDisconnectedList.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_DISCONNECTED_LIST);
		lblDisconnectedList.setPrefSize(470, 70);
		
		HBox titleHBox = new HBox();
		titleHBox.getChildren().add(warningIcon);
		titleHBox.getChildren().add(lblDisconnectedDevicesTitle);
		HBox.setMargin(warningIcon, new Insets(0, 10, 0, 0));
		
		this.getChildren().add(titleHBox);
		this.getChildren().add(lblDisconnectedDevices);
		this.getChildren().add(lblDisconnectedList);
	}
	
	private void clearList() {
		lblDisconnectedList.setText("");
	}
	
	private void addDevice(final String deviceName) {
		lblDisconnectedList.setText(lblDisconnectedList.getText() + deviceName + "\n");
	}
	
	public void setDisconnectedDevices(final Set<String> disconnectedDevices) {
		clearList();
		for (String name : disconnectedDevices) {
			addDevice(name);
		}
	}
}
