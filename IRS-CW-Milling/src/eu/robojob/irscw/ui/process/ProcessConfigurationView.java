package eu.robojob.irscw.ui.process;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ProcessConfigurationView extends GridPane {

	private ProcessConfigurationPresenter presenter;
	
	private TextField name;
	private TextField test;
	
	public ProcessConfigurationView() {
		buildView();
	}
	
	private void buildView() {
		name = new TextField();
		name.focusedProperty().addListener(new TextFieldFocusListener(name));
		add(name, 0, 0);
		test = new TextField();
		test.focusedProperty().addListener(new TextFieldFocusListener(test));
		add(test, 0, 1);
	}
	
	public void setPresenter(ProcessConfigurationPresenter presenter) {
		this.presenter = presenter;
	}
	
	public ProcessConfigurationPresenter getPresenter() {
		return presenter;
	}

	
	private class TextFieldFocusListener implements ChangeListener<Boolean> {

		private TextField textField;
		
		public TextFieldFocusListener(TextField textField) {
			this.textField = textField;
		}
		
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) {
				presenter.textFieldFocussed(textField);
			} else {
				presenter.textFieldLostFocus();
			}
		}
		
	}

}
