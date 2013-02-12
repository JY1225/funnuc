package eu.robojob.irscw.ui.configure.process;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.TextArea;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class ProcessSaveView extends AbstractFormView<ProcessSavePresenter> {

	private ProcessFlow processFlow;
	
	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblDescription;
	private TextArea txtareaDescription;
	private Button btnOverwrite;
	private Button btnSaveAsNew;

	private static final int BUTTON_WIDTH = 150;
	private static final int MAX_NAME_LENGTH = 50;
	private static final int MAX_DESCRIPTION_LENGTH = 300;
	
	private static final String NAME = "ProcessSaveView.name";
	private static final String DESCRIPTION = "ProcessSaveView.description";

	public ProcessSaveView() {
		super();
	}
	
	public void setProcessFlow(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	@Override
	protected void build() {
		lblName = new Label(Translator.getTranslation(NAME));
		fulltxtName = new FullTextField(MAX_NAME_LENGTH);
		fulltxtName.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		fulltxtName.setAlignment(Pos.CENTER_LEFT);
		fulltxtName.setText(processFlow.getName());
		fulltxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String oldValue, final String newValue) {
				getPresenter().nameChanged(newValue);
			}
		});
		lblDescription = new Label(Translator.getTranslation(DESCRIPTION));
		txtareaDescription = new TextArea(MAX_DESCRIPTION_LENGTH);
		txtareaDescription.setPrefHeight(UIConstants.TEXT_AREA_HEIGHT);
		txtareaDescription.setText(processFlow.getDescription());
		txtareaDescription.setWrapText(true);
		txtareaDescription.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String oldValue, final String newValue) {
				getPresenter().descriptionChanged(newValue);
			}
		});
		
		HBox hboxName = new HBox();
		hboxName.getChildren().add(lblName);
		hboxName.getChildren().add(fulltxtName);
		HBox.setHgrow(fulltxtName, Priority.ALWAYS);
		int row = 0;
		int column = 0;
		this.add(hboxName, column++, row);
		
		VBox vboxDescription = new VBox();
		vboxDescription.getChildren().add(lblDescription);
		vboxDescription.getChildren().add(txtareaDescription);
		row++;
		column = 0;
		this.add(vboxDescription, column++, row);
	
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		txtareaDescription.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
	
}
