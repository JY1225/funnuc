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
		cbbMachine.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String oldValue, String newValue) {
				if ((oldValue == null) || (!oldValue.equals(newValue))) {
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
				if ((oldValue == null) || (!oldValue.equals(newValue))) {
					if ((deviceInfo.getPickStep().getDeviceSettings().getWorkArea() == null) || (newValue != deviceInfo.getPickStep().getDeviceSettings().getWorkArea().getId())) {
						presenter.changedWorkArea(newValue);
					}
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
		cbbClamping.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if ((oldValue == null) || (!oldValue.equals(newValue))) {
					if ((deviceInfo.getPickStep().getDeviceSettings().getClamping() == null) || (newValue != deviceInfo.getPickStep().getDeviceSettings().getClamping().getId())) {
						presenter.changedClamping(newValue);
					}
				}
			}
		});
		lblDeltaX = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaX"));
		lblDeltaY = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaY"));
		lblDeltaZ = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaZ"));
		lblDeltaR = new Label(translator.getTranslation("CNCMillingMachineConfigureView.deltaR"));
		
		update();
	}
	
	public void update() {
		updateMachines();
		updateWorkAreas();
		updateClampings();
	}
	
	public void updateMachines() {
		cbbMachine.getItems().addAll(cncMillingMachineIds);
		cbbMachine.setDisable(false);
		if (cbbMachine.getItems().size() == 1) {
			cbbMachine.setValue(cbbMachine.getItems().get(0));
			cbbMachine.setDisable(true);
		} else if (deviceInfo.getDevice() != null) {
			cbbMachine.setValue(deviceInfo.getDevice().getId());
		}
	}
	
	public void updateWorkAreas() {
		if ((deviceInfo.getDevice() != null)&&(deviceInfo.getDevice().getWorkAreas() != null)) {
			cbbWorkArea.getItems().clear();
			cbbWorkArea.getItems().addAll(deviceInfo.getDevice().getWorkAreaIds());
			cbbWorkArea.setDisable(false);
			if (cbbWorkArea.getItems().size() == 1) {
				//presenter.changedWorkArea(cbbWorkArea.getItems().get(0));
				cbbWorkArea.setValue(cbbWorkArea.getItems().get(0));
				cbbWorkArea.setDisable(true);
			} else if ((deviceInfo.getPutStep() != null) && (deviceInfo.getPutStep().getDeviceSettings() != null) && 
					((deviceInfo.getPutStep().getDeviceSettings().getWorkArea() != null))) {
				cbbWorkArea.setValue(deviceInfo.getPutStep().getDeviceSettings().getWorkArea().getId());
			}
		}
	}

	public void updateClampings() {
		if ((deviceInfo.getPutStep().getDeviceSettings() != null) && (deviceInfo.getPutStep().getDeviceSettings().getWorkArea() != null)) {
			cbbClamping.getItems().clear();
			cbbClamping.getItems().addAll(deviceInfo.getPutStep().getDeviceSettings().getWorkArea().getClampingIds());
			cbbClamping.setDisable(false);
			if (cbbClamping.getItems().size() == 1) {
				//presenter.changedClamping(cbbWorkArea.getItems().get(0))
				cbbClamping.setValue(cbbClamping.getItems().get(0));
				cbbClamping.setDisable(true);
			} else if ((deviceInfo.getPutStep() != null) && (deviceInfo.getPutStep().getDeviceSettings() != null) && 
				(deviceInfo.getPutStep().getDeviceSettings().getClamping() != null)) {
				cbbClamping.setValue(deviceInfo.getPutStep().getDeviceSettings().getClamping().getId());
			}
		}
	}
	
	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		
	}

}
