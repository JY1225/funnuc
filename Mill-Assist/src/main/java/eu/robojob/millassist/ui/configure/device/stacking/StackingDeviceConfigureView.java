package eu.robojob.millassist.ui.configure.device.stacking;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.model.DeviceInformation;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class StackingDeviceConfigureView extends AbstractFormView<StackingDeviceConfigurePresenter> {

	private Label lblStacker;
	private ComboBox<String> cbbStacker;
	private DeviceInformation deviceInfo;
	private Set<String> stackingDeviceIds;
	private Button btnChange;
	
	private Label lblGridPlate;
	private CheckBox cbGridPlate;
	private ComboBox<String> cbbGridPlates;

	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final int COMBO_WIDTH = 200;
	private static final int COMBO_HEIGHT = 40;
	
	private static final String STACKER = "StackingDeviceConfigureView.stacker";
	private static final String CHANGE = "StackingDeviceConfigureView.change";
	private static final String GRIDPLATE = "StackingDeviceConfigureView.gridplate";
	
	private static final String CHANGE_ICON = "M 11.5,0 C 10.123545,0 9,1.1240475 9,2.5 9,3.8754503 10.123545,5 11.5,5 12.311762,5 13.063476,4.5758969 13.53125,3.90625 L 13.625,3.8125 13.75,3.84375 c 3.37485,0.993046 5.71875,4.1438049 5.71875,7.65625 0,1.222539 -0.289352,2.412159 -0.84375,3.53125 l -0.125,0.25 -0.15625,-0.21875 -1.15625,-1.40625 -1.8125,6 6.21875,-0.5625 -1.625,-2 -0.09375,-0.125 0.09375,-0.09375 c 1.023678,-1.612727 1.5625,-3.474277 1.5625,-5.375 0,-4.5765484 -3.117772,-8.5505816 -7.5625,-9.6875 L 13.875,1.78125 13.84375,1.6875 C 13.49474,0.68240188 12.554813,0 11.5,0 z M 6.21875,3.34375 0,3.90625 l 1.625,2 0.09375,0.125 -0.0625,0.09375 c -1.02418067,1.6112202 -1.5625,3.4740263 -1.5625,5.375 0,4.5768 3.0862707,8.550582 7.53125,9.6875 L 7.71875,21.21875 7.75,21.3125 C 8.099512,22.317598 9.0391879,23 10.09375,23 c 1.376455,0 2.5,-1.123545 2.5,-2.5 0,-1.37545 -1.123545,-2.5 -2.5,-2.5 -0.8122639,0 -1.5634764,0.424103 -2.03125,1.09375 L 8,19.1875 7.875,19.15625 C 4.5004013,18.163957 2.125,15.013449 2.125,11.5 c 0,-1.222037 0.2898538,-2.4114055 0.84375,-3.53125 l 0.125,-0.25 0.1875,0.21875 1.125,1.40625 1.8125,-6 z";
	
	public void setDeviceInfo(final DeviceInformation deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public void setStackingDeviceIds(final Set<String> stackingDeviceIds) {
		this.stackingDeviceIds = stackingDeviceIds;
	}
	
	@Override
	protected void build() {
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
		getContents().getChildren().clear();
		lblStacker = new Label(Translator.getTranslation(STACKER));
		int column = 0;
		int row = 0;
		getContents().add(lblStacker, ++column, row);
		cbbStacker = new ComboBox<String>();
		cbbStacker.setPrefSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbbStacker.setMinSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbbStacker.setMaxSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbbStacker.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String oldValue, final String newValue) {
				if (newValue != null) {
					if (deviceInfo != null) {
						if (newValue.equals(deviceInfo.getDevice().getName())) {
							btnChange.setDisable(true);
						} else {
							btnChange.setDisable(false);
						}
						//Disable the selection of gridplates when device is not a basic stack plate
						if(getPresenter().getDeviceByName(newValue).getType() == EDeviceGroup.BASIC_STACK_PLATE) {
							cbGridPlate.setDisable(false);
							cbGridPlate.setSelected(false);
							cbbGridPlates.setDisable(true);
							//TODO - setManaged
						} else {
							cbGridPlate.setDisable(true);
							selectGridPlate(false);
						}
					}
				}
			}
		});
		getContents().add(cbbStacker, ++column, row);
		cbGridPlate = new CheckBox();
		cbGridPlate.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue,Boolean oldValue, Boolean newValue) {
				selectGridPlate(newValue);
			}
			
		});
		lblGridPlate = new Label(Translator.getTranslation(GRIDPLATE));
		cbbGridPlates = new ComboBox<String>();
		cbbGridPlates.setPrefSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbbGridPlates.setMinSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbbGridPlates.setMaxSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbbGridPlates.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {
				setGridPlate(cbbGridPlates.getSelectionModel().getSelectedItem());
				if(isNewGridPlateSelected())
					btnChange.setDisable(false);
				else 
					btnChange.setDisable(true);
			}
		});
		column = 0;
		getContents().add(cbGridPlate, column, ++row);
		getContents().add(lblGridPlate, ++column, row);
		getContents().add(cbbGridPlates, ++column, row);
		btnChange = createButton(CHANGE_ICON, "", Translator.getTranslation(CHANGE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().changedDevice(cbbStacker.getValue(), cbbGridPlates.getSelectionModel().getSelectedItem());
			}
		});
		btnChange.setDisable(true);
		column = 0; row++;
		GridPane.setHalignment(btnChange, HPos.CENTER);
		getContents().add(btnChange, ++column, row, 2, 1);
		refresh();
	}

	@Override
	public void refresh() {
		refreshStackers();
		resetGridPlates();
	}
	
	private boolean isNewGridPlateSelected() {
		if(cbGridPlate.isSelected() && cbbGridPlates.getValue() != null) {
			if(cbbGridPlates.getValue().equals(getPresenter().getGridPlateName())) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
	}

	private void refreshStackers() {
		cbbStacker.setValue(null);
		cbbStacker.getSelectionModel().clearSelection();
		cbbStacker.getItems().clear();
		cbbStacker.getItems().addAll(stackingDeviceIds);
		cbbStacker.getSelectionModel().clearSelection();
		lblStacker.setDisable(false);
		cbbStacker.setDisable(false);
		cbbStacker.setValue(null);
		if (cbbStacker.getItems().size() == 1) {
			cbbStacker.setValue(cbbStacker.getItems().get(0));
			cbbStacker.setDisable(true);
			lblStacker.setDisable(true);
		} else if (deviceInfo.getDevice() != null) {
			cbbStacker.setValue(deviceInfo.getDevice().getName());
		}		
	}
	
	/**
	 * This method makes the combobox which contains all the gridplates visible or not
	 * 
	 * @param flag
	 *		true - gridplate checkbox is selected and the combobox with the possible grids is enabled 
	 *     false - gridplate checkbox is not selected. The combobox is disabled.
	 */
	private void selectGridPlate(boolean flag) {
		cbGridPlate.setSelected(flag);
		cbbGridPlates.setDisable(!flag);
		lblGridPlate.setDisable(!flag);
		if(flag == false) {
			cbbGridPlates.setValue(null);
			btnChange.setDisable(false);
		}
	}
	
	private void resetGridPlates() {
		cbbGridPlates.setValue(null);
		cbbGridPlates.getSelectionModel().clearSelection();
		cbbGridPlates.getItems().clear();
		getPresenter().updateGridPlates();
		String plateName = getPresenter().getGridPlateName();
		if(plateName != null) {
			cbGridPlate.setSelected(true);
			setGridPlate(plateName);
			lblGridPlate.setDisable(false);
			cbbGridPlates.setDisable(false);
		} else {
			cbbGridPlates.setValue(null);
			cbbGridPlates.setDisable(true);
			lblGridPlate.setDisable(true);
		}
	}
	
	private void setGridPlate(String gridplateName) {
		cbbGridPlates.setValue(gridplateName);
		cbbGridPlates.setPromptText(cbbGridPlates.getConverter().toString(cbbGridPlates.getValue()));
	}
	
	public void setGridPlates(final Set<String> gridPlates) {
		cbbGridPlates.getItems().clear();
		cbbGridPlates.getItems().addAll(gridPlates);
	}
	
}
