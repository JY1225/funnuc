package eu.robojob.irscw.ui;

import javafx.scene.input.KeyCode;

import org.apache.log4j.Logger;

public class KeyboardPresenter {

	private KeyboardView view;
	
	private static Logger logger = Logger.getLogger(KeyboardPresenter.class);
	
	public KeyboardPresenter(KeyboardView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void keyPressed(KeyCode keyCode) {
		logger.debug("Pressed key: " + keyCode);
	}
	
	public KeyboardView getView() {
		return view;
	}
}
