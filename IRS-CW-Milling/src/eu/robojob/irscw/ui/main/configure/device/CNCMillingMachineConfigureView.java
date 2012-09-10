package eu.robojob.irscw.ui.main.configure.device;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.configure.AbstractFormView;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineConfigureView extends AbstractFormView<CNCMillingMachineConfigurePresenter> {

	private Label lblMachine;
	private ComboBox<String> cbbMachine;
	private Label lblWorkArea;
	private ComboBox<String> cbbWorkArea;
	private Label lblClampingName;
	private ComboBox<String> cbbClamping;
	private Label lblDeltaX;
	private Label lblDeltaY;
	private Label lblDeltaZ;
	private Label lblDeltaR;
	
	private DeviceInformation deviceInfo;
	private Set<String> cncMillingMachineIds;

	public void setDeviceInfo(DeviceInformation deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public void setCNCMillingMachineIds(Set<String> cncMillingMachineIds) {
		this.cncMillingMachineIds = cncMillingMachineIds;
	}
	
	@Override
	protected void build() {
		lblMachine = new Label(translator.getTranslation("CNCMillingMachineConfigureView.machine"));
		int column = 0;
		int row = 0;
		add(lblMachine, column++, row);
		cbbMachine = new ComboBox<String>();
		cbbMachine.getItems().addAll(cncMillingMachineIds);
		cbbMachine.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String oldValue, String newValue) {
				if (!oldValue.equals(newValue)) {
					presenter.changedDevice(newValue);
				}
			}
			
		});
		add(cbbMachine, column++, row);
		column = 0;
		row++;
		lblWorkArea = new Label(translator.getTranslation("CNCMillingMachineConfigureView.workArea"));
		add(lblWorkArea, column++, row);
		cbbWorkArea = new ComboBox<String>();
		cbbWorkArea.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!oldValue.equals(newValue)) {
					presenter.changedWorkArea(newValue);
				}
			}
		});
		add(cbbWorkArea, column++, row);
		column = 0;
		row++;
		lblClampingName = new Label(translator.getTranslation("CNCMillingMachineConfigureView.clampingName"));
		add(lblClampingName, column++, row);
		cbbClamping = new ComboBox<String>();
		add(cbbClamping, column++, row);
		
		lblDeltaX = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaX"));
		lblDeltaY = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaY"));
		lblDeltaZ = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaZ"));
		lblDeltaR = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaR"));
	}
	
	public void updateWorkAreas() {
		if ((deviceInfo.getDevice() != null)&&(deviceInfo.getDevice().getWorkAreas() != null)) {
			cbbWorkArea.getItems().addAll(deviceInfo.getDevice().getWorkAreaIds());
		}
	}

	public void updateClampings() {
		if ((deviceInfo.getPutStep().getDeviceSettings() != null) && (deviceInfo.getPutStep().getDeviceSettings().getWorkArea() != null)) {
			cbbClamping.getItems().addAll(deviceInfo.getPutStep().getDeviceSettings().getWorkArea().getClampingIds());
		}
	}
	
	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		
	}

}
