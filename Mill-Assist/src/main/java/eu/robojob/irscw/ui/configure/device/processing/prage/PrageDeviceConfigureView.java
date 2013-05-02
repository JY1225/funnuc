package eu.robojob.irscw.ui.configure.device.processing.prage;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.ui.general.model.DeviceInformation;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class PrageDeviceConfigureView extends AbstractFormView<PrageDeviceConfigurePresenter> {

	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	private Label lblMachine;
	private ComboBox<String> cbbMachine;
	private DeviceInformation deviceInfo;
	private Set<String> preProcessingDeviceIds;
	
	private static final int COMBO_WIDTH = 200;
	private static final int COMBO_HEIGHT = UIConstants.COMBO_HEIGHT;
	
	private static final String DEVICE = "PrageDeviceConfigureView.device";
	
	public PrageDeviceConfigureView(final DeviceInformation deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public void setPreProcessingDeviceIds(final Set<String> preProcessingDeviceIds) {
		this.preProcessingDeviceIds = preProcessingDeviceIds;
	}
	
	@Override
	protected void build() {
		setVgap(VGAP);
		setHgap(HGAP);
		getChildren().clear();
		
		lblMachine = new Label(Translator.getTranslation(DEVICE));
		int column = 0;
		int row = 0;
		add(lblMachine, column++, row);
		cbbMachine = new ComboBox<String>();
		cbbMachine.setPrefSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbbMachine.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String oldValue, final String newValue) {
				if ((oldValue == null) || (!oldValue.equals(newValue))) {
					getPresenter().changedDevice(newValue);
				}
			}
		});
		add(cbbMachine, column++, row);
	}
	
	public void refreshMachines() {
		cbbMachine.getItems().clear();
		cbbMachine.getItems().addAll(preProcessingDeviceIds);
		lblMachine.setDisable(false);
		cbbMachine.setDisable(false);
		if (cbbMachine.getItems().size() == 1) {
			cbbMachine.setValue(cbbMachine.getItems().get(0));
			lblMachine.setDisable(true);
			cbbMachine.setDisable(true);
		} else if (deviceInfo.getDevice() != null) {
			cbbMachine.setValue(deviceInfo.getDevice().getName());
		}
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
	}

	@Override
	public void refresh() {
		refreshMachines();
	}

}
