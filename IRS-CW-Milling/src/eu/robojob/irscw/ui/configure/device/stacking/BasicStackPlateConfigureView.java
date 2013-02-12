package eu.robojob.irscw.ui.configure.device.stacking;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.model.DeviceInformation;
import eu.robojob.irscw.util.Translator;

public class BasicStackPlateConfigureView extends AbstractFormView<BasicStackPlateConfigurePresenter> {

	private Label lblStacker;
	private ComboBox<String> cbbStacker;
	private DeviceInformation deviceInfo;
	private Set<String> stackingDeviceIds;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final int COMBO_WIDTH = 200;
	private static final int COMBO_HEIGHT = 40;
	
	private static final String STACKER = "BasicStackPlateConfigureView.stacker";
	
	public void setDeviceInfo(final DeviceInformation deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public void setStackingDeviceIds(final Set<String> stackingDeviceIds) {
		this.stackingDeviceIds = stackingDeviceIds;
	}
	
	@Override
	protected void build() {
		setVgap(VGAP);
		setHgap(HGAP);
		getChildren().clear();
		lblStacker = new Label(Translator.getTranslation(STACKER));
		int column = 0;
		int row = 0;
		add(lblStacker, column++, row);
		cbbStacker = new ComboBox<String>();
		cbbStacker.setPrefSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbbStacker.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String oldValue, final String newValue) {
				if ((oldValue == null) || (!oldValue.equals(newValue))) {
					getPresenter().changedDevice(newValue);
				}
			}
		});
		add(cbbStacker, column++, row);
		refresh();
	}

	@Override
	public void refresh() {
		refreshStackers();
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
	}

	public void refreshStackers() {
		cbbStacker.getItems().clear();
		cbbStacker.getItems().addAll(stackingDeviceIds);
		lblStacker.setDisable(false);
		cbbStacker.setDisable(false);
		if (cbbStacker.getItems().size() == 1) {
			cbbStacker.setValue(cbbStacker.getItems().get(0));
			cbbStacker.setDisable(true);
			lblStacker.setDisable(true);
		} else if (deviceInfo.getDevice() != null) {
			cbbStacker.setValue(deviceInfo.getDevice().getName());
		}		
	}
	
}
