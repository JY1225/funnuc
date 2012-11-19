package eu.robojob.irscw.ui.configure.device;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineConfigureView extends AbstractFormView<CNCMillingMachineConfigurePresenter> {

	private Label lblMachine;
	private ComboBox<String> cbbMachine;
	private Label lblWorkArea;
	private ComboBox<String> cbbWorkArea;
	private Label lblClampingName;
	private ComboBox<String> cbbClamping;
	
	private DeviceInformation deviceInfo;
	private Set<String> cncMillingMachineIds;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	private static final int COMBO_WIDTH = 200;
	private static final int COMBO_HEIGHT = 30;

	public void setDeviceInfo(DeviceInformation deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public void setCNCMillingMachineIds(Set<String> cncMillingMachineIds) {
		this.cncMillingMachineIds = cncMillingMachineIds;
	}
	
	@Override
	protected void build() {
		setVgap(VGAP);
		setHgap(HGAP);
		getChildren().clear();
		
		lblMachine = new Label(translator.getTranslation("CNCMillingMachineConfigureView.machine"));
		int column = 0;
		int row = 0;
		add(lblMachine, column++, row);
		cbbMachine = new ComboBox<String>();
		cbbMachine.setPrefSize(COMBO_WIDTH, COMBO_HEIGHT);
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
		cbbWorkArea.setPrefSize(COMBO_WIDTH, COMBO_HEIGHT);
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
		cbbClamping.setPrefSize(COMBO_WIDTH, COMBO_HEIGHT);
		add(cbbClamping, column++, row);
		cbbClamping.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if ((oldValue == null) || (!oldValue.equals(newValue))) {
					CNCMillingMachineSettings deviceSettings = (CNCMillingMachineSettings) deviceInfo.getDeviceSettings();
					Clamping currentClamping = deviceSettings.getClamping(deviceInfo.getPickStep().getDeviceSettings().getWorkArea());
					if ((currentClamping == null) || (newValue != currentClamping.getId())) {
						presenter.changedClamping(newValue);
					}
				}
			}
		});
		refresh();
	}
	
	public void refreshMachines() {
		cbbMachine.getItems().clear();
		cbbMachine.getItems().addAll(cncMillingMachineIds);
		cbbMachine.setDisable(false);
		if (cbbMachine.getItems().size() == 1) {
			cbbMachine.setValue(cbbMachine.getItems().get(0));
			cbbMachine.setDisable(true);
		} else if (deviceInfo.getDevice() != null) {
			cbbMachine.setValue(deviceInfo.getDevice().getId());
		}
	}
	
	public void refreshWorkAreas() {
		if ((deviceInfo.getDevice() != null)&&(deviceInfo.getDevice().getWorkAreas() != null)) {
			cbbWorkArea.getItems().clear();
			cbbWorkArea.getItems().addAll(deviceInfo.getDevice().getWorkAreaIds());
			cbbWorkArea.setDisable(false);
			if (cbbWorkArea.getItems().size() == 1) {
				//presenter.changedWorkArea(cbbWorkArea.getItems().get(0));
				cbbWorkArea.setValue(cbbWorkArea.getItems().get(0));
				cbbWorkArea.setDisable(true);
			} else if ((deviceInfo.getPutStep() != null) && (deviceInfo.getPutStep().getDeviceSettings() != null) && 
					(deviceInfo.getPutStep().getDeviceSettings().getWorkArea() != null)) {
				cbbWorkArea.setValue(deviceInfo.getPutStep().getDeviceSettings().getWorkArea().getId());
			}
		}
	}

	public void refreshClampings() {
		if ((deviceInfo.getPutStep().getDeviceSettings() != null) && (deviceInfo.getPutStep().getDeviceSettings().getWorkArea() != null)) {
			cbbClamping.getItems().clear();
			cbbClamping.getItems().addAll(deviceInfo.getPutStep().getDeviceSettings().getWorkArea().getClampingIds());
			cbbClamping.setDisable(false);
			if (cbbClamping.getItems().size() == 1) {
				//presenter.changedClamping(cbbWorkArea.getItems().get(0))
				cbbClamping.setValue(cbbClamping.getItems().get(0));
				cbbClamping.setDisable(true);
			} else if ((deviceInfo.getPutStep() != null) && (deviceInfo.getPutStep().getDeviceSettings() != null) && 
				(deviceInfo.getPutStep().getDeviceSettings().getWorkArea() != null)) {
				if (deviceInfo.getPutStep().getDeviceSettings().getWorkArea().getActiveClamping() != null) {
					cbbClamping.setValue(deviceInfo.getPutStep().getDeviceSettings().getWorkArea().getActiveClamping().getId());
				} else {
					cbbClamping.setValue(cbbClamping.getItems().get(0));
				}
			}
		}
	}
	
	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		
	}

	@Override
	public void refresh() {
		refreshMachines();
		refreshWorkAreas();
		refreshClampings();
	}

}
