package eu.robojob.irscw.ui.main.configure.process;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.configure.AbstractFormView;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;

public class ProcessConfigureView extends AbstractFormView<ProcessConfigurePresenter> {
		
	private Label lblName;
	private FullTextField fulltxtName;

	private Button btnAddDeviceStep;
	private Button btnRemoveDeviceStep;
	
	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 40;
	
	private static final int TEXTFIELD_HEIGHT= 30;
	
	private static final int MAX_NAME_LENGTH = 20;
	
	private static final String addIconPath = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String deleteIconPath = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	private ProcessFlowAdapter processFlowAdapter;
	
	public ProcessConfigureView() {
		super();	
	}
	
	public void setProcessFlow(ProcessFlow processFlow) {
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
	}
	
	public ProcessConfigurePresenter getPresenter() {
		return presenter;
	}

	//TODO add id's, apply css naming conventions, ...
	@Override
	protected void build() {
		setHgap(HGAP);
		setVgap(VGAP);
		
		getChildren().clear();
		
		HBox hbox = new HBox();
		lblName = new Label(translator.getTranslation("Name"));
		lblName.getStyleClass().addAll("form-label", "form-label-name");
		hbox.getChildren().add(lblName);
		fulltxtName = new FullTextField(MAX_NAME_LENGTH);
		fulltxtName.setPrefHeight(TEXTFIELD_HEIGHT);
		fulltxtName.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(fulltxtName, Priority.ALWAYS);
		fulltxtName.getStyleClass().addAll("form-full-textfield", "form-full-textfield-name");
		hbox.getChildren().add(fulltxtName);
		hbox.setAlignment(Pos.CENTER_LEFT);
		add(hbox, 0, 0, 2, 1);
		
		btnAddDeviceStep = createButton(addIconPath, "add-icon", translator.getTranslation("Add"), BUTTON_WIDTH, BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.addDeviceStep();
			}
		});
		if (!processFlowAdapter.canAddDevice()) {
			btnAddDeviceStep.setDisable(true);
		}
		add(btnAddDeviceStep, 0, 1);
		btnRemoveDeviceStep = createButton(deleteIconPath, "remove-icon", translator.getTranslation("Remove"), BUTTON_WIDTH, BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				presenter.removeDeviceStep();
			}
		});
		if (!processFlowAdapter.canRemoveDevice()) {
			btnRemoveDeviceStep.setDisable(true);
		}
		add(btnRemoveDeviceStep, 1, 1);
	}
	
	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		fulltxtName.setFocusListener(listener);
	}
	
	public void setAddDeviceStepActive(boolean active) {
		btnAddDeviceStep.getStyleClass().remove("form-button-active");
		if (active) {
			btnAddDeviceStep.getStyleClass().add("form-button-active");
		}
	}
	
	public void setRemoveDeviceStepActive(boolean active) {
		btnRemoveDeviceStep.getStyleClass().remove("form-button-active");
		if (active) {
			btnRemoveDeviceStep.getStyleClass().add("form-button-active");
		}
	}
	
	public void setNameEnabled(boolean enabled) {
		lblName.setDisable(!enabled);
		fulltxtName.setDisable(!enabled);
	}

}
