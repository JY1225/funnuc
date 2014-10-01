package eu.robojob.millassist.ui.configure.device.processing.reversal;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

//TODO - ReversalUnit review

public class ReversalUnitConfigureView extends AbstractFormView<ReversalUnitConfigurePresenter> {

	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	private Label lblMachine;
	private ComboBox<String> cbbMachine;
	private DeviceInformation deviceInfo;
	private Set<String> postProcessingDeviceIds;
	
	private static final int COMBO_WIDTH = 200;
	private static final int COMBO_HEIGHT = UIConstants.COMBO_HEIGHT;
	
	private static final String DEVICE = "PrageDeviceConfigureView.device";
	
	public ReversalUnitConfigureView(final DeviceInformation deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public void setPostProcessingDeviceIds(final Set<String> postProcessingDeviceIds) {
		this.postProcessingDeviceIds = postProcessingDeviceIds;
	}
	
	@Override
	protected void build() {
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
		getContents().getChildren().clear();
		
		lblMachine = new Label(Translator.getTranslation(DEVICE));
		int column = 0;
		int row = 0;
		getContents().add(lblMachine, column++, row);
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
		getContents().add(cbbMachine, column++, row);
	}
	
	public void refreshMachines() {
		cbbMachine.getItems().clear();
		cbbMachine.getItems().addAll(postProcessingDeviceIds);
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
