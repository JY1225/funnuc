package eu.robojob.irscw.ui.process;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.keyboard.KeyboardParentPresenter;
import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;

public class ConfigurePresenter implements KeyboardParentPresenter {

	private static Logger logger = Logger.getLogger(ConfigurePresenter.class);
		
	private ConfigureView view;
	
	private KeyboardPresenter keyboardPresenter;
	private ProcessConfigurationPresenter processConfigurationPresenter;
	
	private boolean keyboardActive;
	
	public ConfigurePresenter(ConfigureView view, KeyboardPresenter keyboardPresenter, ProcessConfigurationPresenter processConfigurationPresenter) {
		this.view = view;
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParentPresenter(this);
		this.processConfigurationPresenter = processConfigurationPresenter;
		processConfigurationPresenter.setParent(this);
		view.setPresenter(this);
		showConfigureView();
		keyboardActive = false;
	}
	
	public ConfigureView getView() {
		return view;
	}
	
	public void showAlarmsView() {
		
	}
	
	public void showConfigureView() {
		view.setBottomRight(processConfigurationPresenter.getView());
	}
	
	public void showTeachView() {
		
	}
	
	public void showAutomateView() {
		
	}
	
	public void textFieldFocussed(eu.robojob.irscw.ui.controls.TextField textField) {
		keyboardPresenter.setTargetTextInput(textField);
		if (!keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}

	@Override
	public synchronized void closeKeyboard() {
		if (keyboardActive) {
			keyboardActive = false;
			logger.debug("Close keyboard");
			// we assume the keyboard view is always on top
			view.removeNodeFromTop(keyboardPresenter.getView());
			view.requestFocus();
		} 
	}

}
