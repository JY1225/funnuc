package eu.robojob.irscw.ui.process;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.KeyboardParentPresenter;
import eu.robojob.irscw.ui.KeyboardPresenter;

public class ProcessConfigurePresenter implements KeyboardParentPresenter {

	private static Logger logger = Logger.getLogger(ProcessConfigurePresenter.class);
	
	private ProcessConfigureView view;
	
	private KeyboardPresenter keyboardPresenter;
	
	private boolean keyboardActive;
	
	public ProcessConfigurePresenter(ProcessConfigureView view, KeyboardPresenter keyboardPresenter) {
		this.view = view;
		this.keyboardPresenter = keyboardPresenter;
		view.setPresenter(this);
		keyboardActive = false;
		activateKeyoard();
	}
	
	public ProcessConfigureView getView() {
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
	
	public void activateKeyoard() {
		view.addNodeToTop(keyboardPresenter.getView());
		keyboardActive = true;
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
