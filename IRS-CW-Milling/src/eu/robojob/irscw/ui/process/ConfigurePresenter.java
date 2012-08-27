package eu.robojob.irscw.ui.process;

import javafx.scene.control.TextField;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.KeyboardParentPresenter;
import eu.robojob.irscw.ui.KeyboardPresenter;

public class ConfigurePresenter implements KeyboardParentPresenter {

	private static Logger logger = Logger.getLogger(ConfigurePresenter.class);
	
	private ConfigureView view;
	
	private KeyboardPresenter keyboardPresenter;
	private ProcessConfigurationPresenter processConfigurationPresenter;
	
	private boolean keyboardActive;
	
	public ConfigurePresenter(ConfigureView view, KeyboardPresenter keyboardPresenter, ProcessConfigurationPresenter processConfigurationPresenter) {
		this.view = view;
		this.keyboardPresenter = keyboardPresenter;
		this.processConfigurationPresenter = processConfigurationPresenter;
		view.setPresenter(this);
		keyboardActive = false;
	}
	
	public ConfigureView getView() {
		return view;
	}
	
	public void showAlarmsView() {
		
	}
	
	public void showConfigureView() {
		
	}
	
	public void showTeachView() {
		
	}
	
	public void showAutomateView() {
		
	}
	
	public void activateKeyoard(TextField textfield) {
		keyboardPresenter.setTargetTextInput(textfield);
		if (!keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}
	
	@Override
	public void closeKeyboard() {
		if (keyboardActive) {
			// we assume the keyboard view is always on top
			view.removeNodeFromTop(keyboardPresenter.getView());
		} else {
			logger.error("Keyboard was already de-activated");
		}
		keyboardActive = false;
	}
}
