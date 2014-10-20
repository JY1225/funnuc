package eu.robojob.millassist.ui.configure.process;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class ProcessSaveView extends AbstractFormView<ProcessSavePresenter> {

	private ProcessFlow processFlow;
	
	private Label lblName;
	private FullTextField fulltxtName;
	private Button btnOverwrite;
//	private Button btnDelete;

	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final int NAME_TEXTFIELD_WIDTH = 300;
	private static final int BUTTON_WIDTH = 150;
	private static final int MAX_NAME_LENGTH = 50;
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";

	private static final String CSS_CLASS_FORM_LABEL_NAME = "form-label-name";
	private static final String CSS_CLASS_FORM_BUTTON_ICON = "form-button-icon";
	private static final String NAME = "ProcessSaveView.name";
	private static final String OVERWRITE = "ProcessSaveView.overwrite";
	
	private static final String ONLY_SAVE_AS_NAME = "ProcessSaveView.onlySaveAsName";
	private static final String ONLY_SAVE_AS_CONFIGURED = "ProcessSaveView.onlySaveAsConfigured";
	
	
	public ProcessSaveView() {
		super();
	}
	
	public void setProcessFlow(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	@Override
	protected void build() {
		getContents().setHgap(HGAP);
		getContents().setVgap(VGAP);
		
		int row = 0;
		int column = 0;
		
		lblName = new Label(Translator.getTranslation(NAME));
		lblName.getStyleClass().addAll(CSS_CLASS_FORM_LABEL, CSS_CLASS_FORM_LABEL_NAME);
		fulltxtName = new FullTextField(MAX_NAME_LENGTH);
		fulltxtName.setPrefSize(NAME_TEXTFIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		fulltxtName.setAlignment(Pos.CENTER_LEFT);
		fulltxtName.setText(processFlow.getName());
		fulltxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String oldValue, final String newValue) {
				getPresenter().nameChanged(newValue);
			}
		});
		
		VBox vboxName = new VBox();
		vboxName.getChildren().add(lblName);
		vboxName.getChildren().add(fulltxtName);
		vboxName.setSpacing(VGAP / 2);
		vboxName.setAlignment(Pos.CENTER_LEFT);
		getContents().add(vboxName, column++, row);
	
		btnOverwrite = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON_ICON, Translator.getTranslation(OVERWRITE), BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().save(fulltxtName.getText());
			}
		});
		btnOverwrite.getStyleClass().add(CSS_CLASS_SAVE_BUTTON);
		
		row++;
		column = 0;
		getContents().add(btnOverwrite, column++, row);
		GridPane.setHalignment(btnOverwrite, HPos.CENTER);
		row++; column = 0;
		row++; column = 0;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		fulltxtName.setText(processFlow.getName());
		if (processFlow.isConfigured()) {
			if (processFlow.getName().equals("")) {
				showNotification(Translator.getTranslation(ONLY_SAVE_AS_NAME), Type.WARNING);
				btnOverwrite.setDisable(true);
			} else {
				hideNotification();		
				btnOverwrite.setDisable(false);
				if (processFlow.getId() > 0) {
					btnOverwrite.setDisable(false);
				} 
			}
		} else {
			showNotification(Translator.getTranslation(ONLY_SAVE_AS_CONFIGURED), Type.WARNING);
			btnOverwrite.setDisable(true);
		}
	}
	
}
