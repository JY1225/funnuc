package eu.robojob.irscw.ui.process.flow;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProcessFlowDeviceView extends VBox {
	
	public enum DeviceType {
		STACKING_FROM("stacking-from"), 
		PRE_PROCESSING("pre-processing"), 
		CNC_MACHINE("cnc-machine"), 
		POST_PROCESSING("post-processing"), 
		STACKING_TO("stacking-to");
		
		private String className;
		
		DeviceType(String className) {
			this.className = className;
		}

		public String getClassName() {
			return className;
		}
	}
	
	private int id;
	
	private Button deviceButton;
	private Label deviceLabel;
	
	private String deviceName;
	private DeviceType type;
	
	public ProcessFlowDeviceView(int viewId, String deviceName, DeviceType type) {
		this.id = viewId;
		this.deviceName = deviceName;
		this.type = type;
		buildView();
	}
	
	private void buildView() {
		deviceButton = new Button();
		deviceButton.getStyleClass().add(type.getClassName());
		deviceLabel = new Label(deviceName);
		this.getChildren().add(deviceButton);
		this.getChildren().add(deviceLabel);
	}
	
	public void updateName(String name) {
		this.deviceName = name;
		deviceLabel.setText(deviceName);
	}

	public int getViewId() {
		return id;
	}
	
}
