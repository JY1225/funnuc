package eu.robojob.irscw.ui.configure.device.pre;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class PrageDeviceConfigureView extends AbstractFormView<PrageDeviceConfigurePresenter> {

	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	private Label lblMachine;
	private ComboBox<String> cbbMachine;
	private DeviceInformation deviceInfo;
	private Set<String> preProcessingDeviceIds;
	
	private static final int COMBO_WIDTH = 200;
	private static final int COMBO_HEIGHT = 30;
	
	public PrageDeviceConfigureView(DeviceInformation deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public void setPreProcessingDeviceIds(Set<String> preProcessingDeviceIds) {
		this.preProcessingDeviceIds = preProcessingDeviceIds;
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
	}
	
	public void refreshMachines() {
		cbbMachine.getItems().clear();
		cbbMachine.getItems().addAll(preProcessingDeviceIds);
		cbbMachine.setDisable(false);
		if (cbbMachine.getItems().size() == 1) {
			cbbMachine.setValue(cbbMachine.getItems().get(0));
			cbbMachine.setDisable(true);
		} else if (deviceInfo.getDevice() != null) {
			cbbMachine.setValue(deviceInfo.getDevice().getId());
		}
	}

	@Override
	public void setTextFieldListener(TextFieldListener listener) {
	}

	@Override
	public void refresh() {
		refreshMachines();
	}

}
