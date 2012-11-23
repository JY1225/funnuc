package eu.robojob.irscw.ui.configure.device;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.model.DeviceInformation;
import eu.robojob.irscw.util.UIConstants;

public class CNCMillingMachineConfigureView extends AbstractFormView<CNCMillingMachineConfigurePresenter> {

	private static final String iconClampWidth = "M 7 0 L 7 5.5625 L 7 11.15625 L 9 11.15625 L 9 0 L 7 0 z M 7 5.5625 L 3.5 2.0625 L 2.28125 3.3125 L 3.65625 4.6875 L 0 4.6875 L 0 6.46875 L 3.65625 6.46875 L 2.28125 7.84375 L 3.5 9.0625 L 7 5.5625 z M 11 0 L 11 11.15625 L 23.75 11.15625 L 23.75 5.5625 L 23.75 0 L 11 0 z M 23.75 5.5625 L 27.25 9.0625 L 28.5 7.84375 L 27.125 6.46875 L 30.75 6.46875 L 30.75 4.6875 L 27.125 4.6875 L 28.5 3.3125 L 27.25 2.0625 L 23.75 5.5625 z";
	private static final String iconClampLength = "M 6.96875 0 L 6.96875 2 L 18.125 2 L 18.125 0 L 6.96875 0 z M 6.96875 4 L 6.96875 8.375 L 6.96875 16.75 L 18.125 16.75 L 18.125 8.375 L 18.125 4 L 6.96875 4 z M 18.125 8.375 L 21.625 11.875 L 22.875 10.65625 L 21.5 9.28125 L 25.125 9.28125 L 25.125 7.5 L 21.5 7.5 L 22.875 6.125 L 21.625 4.875 L 18.125 8.375 z M 6.96875 8.375 L 3.46875 4.875 L 2.25 6.125 L 3.625 7.5 L -0.03125 7.5 L -0.03125 9.28125 L 3.625 9.28125 L 2.25 10.65625 L 3.46875 11.875 L 6.96875 8.375 z";
	
	private Label lblMachine;
	private ComboBox<String> cbbMachine;
	private Label lblWorkArea;
	private ComboBox<String> cbbWorkArea;
	private Label lblClampingName;
	private ComboBox<String> cbbClamping;
	private Label lblClampingType;
	private Button btnLength;
	private Button btnWidth;
	
	private DeviceInformation deviceInfo;
	private Set<String> cncMillingMachineIds;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	private static final int COMBO_WIDTH = 200;
	private static final int COMBO_HEIGHT = 40;
	
	private static final double BTN_WIDTH = 100;
	private static final double BTN_HEIGHT= UIConstants.BUTTON_HEIGHT;

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
		column = 0;
		row++;
		lblClampingType = new Label(translator.getTranslation("CNCMillingMachineConfigureView.clampingType"));
		btnLength = createButton(iconClampLength, "btn-clamping", translator.getTranslation("CNCMillingMachineConfigureView.length"), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				presenter.changedClampingType(true);
			}
		});
		btnLength.getStyleClass().add("form-button-bar-left");
		btnWidth = createButton(iconClampWidth, "btn-clamping", translator.getTranslation("CNCMillingMachineConfigureView.width"), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				presenter.changedClampingType(false);
			}
		});
		btnWidth.getStyleClass().add("form-button-bar-right");
		add(lblClampingType, column++, row);
		HBox hboxBtns = new HBox();
		hboxBtns.getChildren().add(btnLength);
		hboxBtns.getChildren().add(btnWidth);
		add(hboxBtns, column++, row);
		
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
	
	public void refreshClampType() {
		btnLength.getStyleClass().remove("form-button-active");
		btnWidth.getStyleClass().remove("form-button-active");
		if (deviceInfo.getProcessingStep() != null) {
			if (deviceInfo.getProcessingStep().getProcessFlow().isClampLength()) {
				btnLength.getStyleClass().add("form-button-active");
			} else {
				btnWidth.getStyleClass().add("form-button-active");
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
		refreshClampType();
	}

}
