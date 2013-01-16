package eu.robojob.irscw.ui.general.status;

import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import eu.robojob.irscw.ui.teach.TeachView;
import eu.robojob.irscw.util.Translator;

public class DisconnectedDevicesView extends VBox {

	private Label lblDisconnectedDevicesTitle;
	private Label lblDisconnectedDevices;
	private Label lblDisconnectedList;
	private SVGPath warningBgIcon;
	private SVGPath warningIcon;
	
	private static final String DISCONNECTED_DEVICES_TITLE = "DisconnectedDevicesView.disconnectedDevicesTitle";
	private static final String DISCONNECTED_DEVICES = "DisconnectedDevicesView.disconnectedDevices";
	private static final String CSS_CLASS_DISCONNECTED = "lbl-disconnected";
	private static final String CSS_CLASS_DISCONNECTED_LIST = "lbl-disconnected-list";
	private static final String CSS_CLASS_WARNING_TITLE = "warning-title";
	
	private static final String WARNING_BG_PATH = "M 12.5,1.03125 C 11.993062,1.0311198 11.509776,1.3702678 11.125,2.0625 L 0.3125,21.46875 C -0.45731218,22.853735 0.22861858,24 1.8125,24 l 21.375,0 c 1.584142,0 2.268771,-1.145744 1.5,-2.53125 L 13.90625,2.0625 C 13.521995,1.3697471 13.006938,1.0313802 12.5,1.03125 z";
	private static final String WARNING_ICON = "m 10.9375,7.15625 0,2.59375 0.625,6.96875 1.875,0 0.625,-6.96875 0,-2.59375 z m 0.125,11.15625 0,2.875 2.875,0 0,-2.875 z";
	
	public DisconnectedDevicesView() {
		build();
	}
	
	private void build() {
		setPrefHeight(TeachView.HEIGHT_BOTTOM);
		this.setAlignment(Pos.CENTER);
		
		Pane warningIconPane = new Pane();
		warningBgIcon = new SVGPath();
		warningBgIcon.setContent(WARNING_BG_PATH);
		warningBgIcon.getStyleClass().add(StatusView.CSS_CLASS_WARNING_BG_ICON);
		warningIcon = new SVGPath();
		warningIcon.setContent(WARNING_ICON);
		warningIcon.getStyleClass().add(StatusView.CSS_CLASS_WARNING_ICON);
		warningIconPane.getChildren().addAll(warningBgIcon, warningIcon);
		
		lblDisconnectedDevicesTitle = new Label(Translator.getTranslation(DISCONNECTED_DEVICES_TITLE));
		lblDisconnectedDevicesTitle.getStyleClass().addAll(StatusView.CSS_CLASS_INFO_MESSAGE_TITLE, CSS_CLASS_WARNING_TITLE);
		
		lblDisconnectedDevices = new Label(Translator.getTranslation(DISCONNECTED_DEVICES));
		lblDisconnectedDevices.getStyleClass().addAll(StatusView.CSS_CLASS_INFO_MESSAGE, CSS_CLASS_DISCONNECTED);
		lblDisconnectedDevices.setPrefSize(470, 60);
		lblDisconnectedDevices.setWrapText(true);
		
		lblDisconnectedList = new Label("");
		lblDisconnectedList.getStyleClass().addAll(StatusView.CSS_CLASS_INFO_MESSAGE, CSS_CLASS_DISCONNECTED_LIST);
		lblDisconnectedList.setPrefSize(470, 70);
		
		HBox titleHBox = new HBox();
		titleHBox.getChildren().add(warningIconPane);
		titleHBox.getChildren().add(lblDisconnectedDevicesTitle);
		HBox.setMargin(warningIconPane, new Insets(0, 10, 0, 0));
		
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
