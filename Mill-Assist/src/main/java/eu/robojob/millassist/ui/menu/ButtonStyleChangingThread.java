package eu.robojob.millassist.ui.menu;

import javafx.application.Platform;
import javafx.scene.control.Button;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ButtonStyleChangingThread implements Runnable {

	private Button button;
	
	private String style1;
	private String style2;
	
	private int duration;
	private boolean currentStyle1;
	
	private boolean running;
	private boolean alive;
	
	private Object syncObject;
	
	private static Logger logger = LogManager.getLogger(ButtonStyleChangingThread.class.getName());
	
	public ButtonStyleChangingThread(final Button button, final String style1, final String style2, final int duration) {
		super();
		this.button = button;
		this.style1 = style1;
		this.style2 = style2;
		this.alive = true;
		this.duration = duration;
		button.getStyleClass().add(style1);
		currentStyle1 = true;
		running = true;
		this.syncObject = new Object();
	}
	
	@Override 
	public void run() {
		while (alive) {
			if (running) {
				if (currentStyle1) {
					Platform.runLater(new Thread() {
						@Override
						public void run() {
							button.getStyleClass().remove(style1);
							button.getStyleClass().add(style2);
							currentStyle1 = false;
						}
					});
				} else {
					Platform.runLater(new Thread() {
						@Override
						public void run() {
							button.getStyleClass().add(style1);
							button.getStyleClass().remove(style2);
							currentStyle1 = true;
						}
					});
				}
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					interrupted();
				}
			} else {
				// always set style 1 when not running
				Platform.runLater(new Thread() {
					@Override
					public void run() {
						button.getStyleClass().add(style1);
						button.getStyleClass().remove(style2);
						currentStyle1 = true;
					}
				});
				synchronized (syncObject) {
					try {
						syncObject.wait();
					} catch (InterruptedException e) {
						interrupted();
					} catch (Exception e) {
						logger.error(e);
						alive = false;
					}
				}
			}
		}
		logger.info("ButtonStyleChangingThread ended...");
	}
	
	public void interrupted() {
		this.alive = false;
		synchronized (syncObject) {
			syncObject.notify();
		}
	}
	
	public void setRunning(final boolean running) {
		this.running = running;
		synchronized (syncObject) {
			syncObject.notify();
		}
	}
}
