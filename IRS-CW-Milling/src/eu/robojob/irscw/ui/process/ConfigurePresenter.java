package eu.robojob.irscw.ui.process;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.KeyboardParentPresenter;
import eu.robojob.irscw.ui.KeyboardPresenter;
import eu.robojob.irscw.ui.controls.TextFieldFocussedListener;

public class ConfigurePresenter implements TextFieldFocussedListener, KeyboardParentPresenter {

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
	
	@Override
	public void textFieldFocussed(eu.robojob.irscw.ui.controls.TextField textField) {
		keyboardPresenter.setTargetTextInput(textField);
		if (!keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}

	@Override
	public void textFieldLostFocus(eu.robojob.irscw.ui.controls.TextField textField) {
		closeKeyboard();
	}

	@Override
	public void closeKeyboard() {
		logger.debug("Close keyboard");
		if (keyboardActive) {
			// we assume the keyboard view is always on top
			view.removeNodeFromTop(keyboardPresenter.getView());
			view.requestFocus();
		} else {
			logger.error("Keyboard was already de-activated");
		}
		keyboardActive = false;
	}

}
