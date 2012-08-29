package eu.robojob.irscw.ui.process.configure;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.keyboard.KeyboardParentPresenter;
import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.process.flow.ProcessFlowPresenter;

public class ConfigurePresenter implements KeyboardParentPresenter {

	private static Logger logger = Logger.getLogger(ConfigurePresenter.class);
		
	private ConfigureView view;
	
	private KeyboardPresenter keyboardPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	
	private ProcessFlowPresenter processFlowPresenter;
	private ProcessConfigurationPresenter processConfigurationPresenter;
	
	private boolean keyboardActive;
	private boolean numericKeyboardActive;
	
	public ConfigurePresenter(ConfigureView view, KeyboardPresenter keyboardPresenter, NumericKeyboardPresenter numericKeyboardPresenter,
			ProcessFlowPresenter processFlowPresenter, ProcessConfigurationPresenter processConfigurationPresenter) {
		this.view = view;
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParent(this);
		this.numericKeyboardPresenter = numericKeyboardPresenter;
		numericKeyboardPresenter.setParent(this);
		this.processConfigurationPresenter = processConfigurationPresenter;
		processConfigurationPresenter.setParent(this);
		this.processFlowPresenter = processFlowPresenter;
		processConfigurationPresenter.setParent(this);
		view.setPresenter(this);
		showConfigureView();
		keyboardActive = false;
		numericKeyboardActive = false;
	}
	
	public ConfigureView getView() {
		return view;
	}
	
	public void showAlarmsView() {
		
	}
	
	public void showConfigureView() {
		view.setTop(processFlowPresenter.getView());
		view.setBottomRight(processConfigurationPresenter.getView());
	}
	
	public void showTeachView() {
		
	}
	
	public void showAutomateView() {
		
	}
	
	public void textFieldFocussed(FullTextField textField) {
		keyboardPresenter.setTarget(textField);
		if (!keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}
	
	public void textFieldFocussed(NumericTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		if (!numericKeyboardActive) {
			logger.debug("Opening numeric keyboard");
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
			numericKeyboardActive = true;
		}
	}

	@Override
	public synchronized void closeKeyboard() {
		if (keyboardActive && numericKeyboardActive) {
			throw new IllegalStateException("Multiple keyboards are active!");
		}
		if (keyboardActive) {
			keyboardActive = false;
			logger.debug("Close keyboard");
			// we assume the keyboard view is always on top
			view.removeNodeFromTop(keyboardPresenter.getView());
			view.requestFocus();
		} else if (numericKeyboardActive) {
			numericKeyboardActive = false;
			logger.debug("Close numeric keyboard");
			view.removeNodeFromBottomLeft(numericKeyboardPresenter.getView());
			view.requestFocus();
		}
	}

}
