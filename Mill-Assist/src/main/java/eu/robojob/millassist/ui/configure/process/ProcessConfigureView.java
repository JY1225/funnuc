package eu.robojob.millassist.ui.configure.process;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.model.ProcessFlowAdapter;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class ProcessConfigureView extends AbstractFormView<ProcessConfigurePresenter> {
		
	private Label lblName;
	private FullTextField fulltxtName;

	private Button btnAddDeviceStep;
	private Button btnRemoveDeviceStep;
	private CheckBox cbSingleCycle;
	private Label lblSingleCycle;
	
	private static final int BUTTON_WIDTH = 150;
		
	private static final int MAX_NAME_LENGTH = 25;
	
	private static final String ADD_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	private static final String CSS_CLASS_ADD_ICON = "add-icon";
	private static final String CSS_CLASS_REMOVE_ICON = "remove-icon";
	private static final String CSS_CLASS_FORM_LABEL_NAME = "form-label-name";
	private static final String CSS_CLASS_FORM_FULLTEXTFIELD_NAME = "form-full-textfield-name";
	
	private static final String NAME = "ProcessConfigureView.name";
	private static final String ADD = "ProcessConfigureView.add";
	private static final String REMOVE = "ProcessConfigureView.remove";
	private static final String SINGLE_CYCLE = "ProcessConfigureView.singleCycle";

	private ProcessFlowAdapter processFlowAdapter;
	
	public ProcessConfigureView() {
		super();	
	}
	
	public void setProcessFlow(final ProcessFlow processFlow) {
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
	}

	@Override
	protected void build() {
		getContents().setHgap(HGAP);
		getContents().setVgap(VGAP);
		
		getContents().getChildren().clear();
		
		HBox hbox = new HBox();
		lblName = new Label(Translator.getTranslation(NAME));
		lblName.getStyleClass().addAll(CSS_CLASS_FORM_LABEL, CSS_CLASS_FORM_LABEL_NAME);
		hbox.getChildren().add(lblName);
		fulltxtName = new FullTextField(MAX_NAME_LENGTH);
		fulltxtName.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		fulltxtName.setAlignment(Pos.CENTER_LEFT);
		fulltxtName.setText(processFlowAdapter.getProcessFlow().getName());
		fulltxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String oldValue, final String newValue) {
				getPresenter().nameChanged(newValue);
			}
		});
		HBox.setHgrow(fulltxtName, Priority.ALWAYS);
		fulltxtName.getStyleClass().addAll(CSS_CLASS_FORM_FULLTEXTFIELD, CSS_CLASS_FORM_FULLTEXTFIELD_NAME);
		hbox.getChildren().add(fulltxtName);
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.setSpacing(HGAP);
		getContents().add(hbox, 0, 0, 2, 1);
		
		btnAddDeviceStep = createButton(ADD_ICON_PATH, CSS_CLASS_ADD_ICON, Translator.getTranslation(ADD), BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().addDeviceStep();
			}
		});
		if (!processFlowAdapter.canAddDevice()) {
			btnAddDeviceStep.setDisable(true);
		}
		getContents().add(btnAddDeviceStep, 0, 1);
		btnRemoveDeviceStep = createButton(DELETE_ICON_PATH, CSS_CLASS_REMOVE_ICON, Translator.getTranslation(REMOVE), BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().removeDeviceStep();
			}
		});
		if (!processFlowAdapter.canRemoveDevice()) {
			btnRemoveDeviceStep.setDisable(true);
		}
		getContents().add(btnRemoveDeviceStep, 1, 1);
		cbSingleCycle = new CheckBox();
		cbSingleCycle.setSelected(processFlowAdapter.getProcessFlow().isSingleCycle());
		cbSingleCycle.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				getPresenter().setSingleCycle(newValue);
			}
		});
		lblSingleCycle = new Label(Translator.getTranslation(SINGLE_CYCLE));
		HBox singleCycleBox = new HBox();
		singleCycleBox.getChildren().addAll(cbSingleCycle, lblSingleCycle);
		singleCycleBox.setSpacing(10);
		getContents().add(singleCycleBox, 0, 2, 2, 1);
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
	}
	
	public void setAddDeviceStepActive(final boolean active) {
		btnAddDeviceStep.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		if (active) {
			btnAddDeviceStep.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		}
	}
	
	public void setRemoveDeviceStepActive(final boolean active) {
		btnRemoveDeviceStep.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		if (active) {
			btnRemoveDeviceStep.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		}
	}
	
	public void setNameEnabled(final boolean enabled) {
		lblName.setDisable(!enabled);
		fulltxtName.setDisable(!enabled);
	}

	@Override
	public void refresh() {
		if (!processFlowAdapter.canAddDevice()) {
			btnAddDeviceStep.setDisable(true);
		} else {
			btnAddDeviceStep.setDisable(false);
		}
		if (!processFlowAdapter.canRemoveDevice()) {
			btnRemoveDeviceStep.setDisable(true);
		} else {
			btnRemoveDeviceStep.setDisable(false);
		}
		boolean hasSingleCycleSetting = processFlowAdapter.getProcessFlow().hasSingleCycleSetting();
		disableSingleCycle(hasSingleCycleSetting);
		if (!hasSingleCycleSetting) {
			cbSingleCycle.setSelected(processFlowAdapter.getProcessFlow().isSingleCycle());
		} 
		fulltxtName.setText(processFlowAdapter.getProcessFlow().getName());
	}
	
	public void disableAddRemove() {
		btnAddDeviceStep.setVisible(false);
		btnAddDeviceStep.setManaged(false);
		btnRemoveDeviceStep.setVisible(false);
		btnRemoveDeviceStep.setManaged(false);
	}
	
	public void disableSingleCycle(boolean hasSingleCycleSetting) {
		cbSingleCycle.setVisible(!hasSingleCycleSetting);
		cbSingleCycle.setManaged(!hasSingleCycleSetting);
		lblSingleCycle.setVisible(!hasSingleCycleSetting);
		lblSingleCycle.setManaged(!hasSingleCycleSetting);
	}

}
