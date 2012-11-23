package eu.robojob.irscw.ui;

import javafx.application.Platform;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.ui.automate.AutomatePresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.robot.RobotPopUpPresenter;
import eu.robojob.irscw.ui.teach.TeachPresenter;

public class MainPresenter implements ProcessFlowListener {

	private MainView view;
	
	private ProcessFlow process;
	
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private TeachPresenter teachPresenter;
	private AutomatePresenter automatePresenter;
	private RobotPopUpPresenter robotPopUpPresenter;
	
	private MainContentPresenter activeContentPresenter;
		
	public MainPresenter(MainView view, MenuBarPresenter menuBarPresenter, ConfigurePresenter configurePresenter, TeachPresenter teachPresenter, AutomatePresenter automatePresenter,
			RobotPopUpPresenter robotPopUpPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.menuBarPresenter = menuBarPresenter;
		menuBarPresenter.setParent(this);
		this.configurePresenter = configurePresenter;
		configurePresenter.setParent(this);
		this.teachPresenter = teachPresenter;
		this.automatePresenter = automatePresenter;
		this.robotPopUpPresenter = robotPopUpPresenter;
		robotPopUpPresenter.setParent(this);
		this.process = null;
		view.setHeader(menuBarPresenter.getView());
	}
	
	public void setProcessMenuBarPresenter(MenuBarPresenter processMenuBarPresenter) {
		this.menuBarPresenter = processMenuBarPresenter;
	}
	
	public void setProcessMainContentPresenter(ConfigurePresenter configurePresenter) {
		this.configurePresenter = configurePresenter;
	}

	private void setActiveMainContentPresenter(MainContentPresenter presenter) {
		if (activeContentPresenter != null) {
			activeContentPresenter.setActive(false);
		}
		activeContentPresenter = presenter;
		view.setContent(activeContentPresenter.getView());
		activeContentPresenter.setActive(true);
	}
	
	public void showConfigure() {
		menuBarPresenter.showConfigureView();
		setActiveMainContentPresenter(configurePresenter);
		refreshStatus();
	}
	
	public void showTeach() {
		menuBarPresenter.showTeachView();
		setActiveMainContentPresenter(teachPresenter);
		refreshStatus();
	}
	
	public void showAutomate() {
		menuBarPresenter.showAutomateView();
		setActiveMainContentPresenter(automatePresenter);
		refreshStatus();
	}
	
	public void showAlarms() {
		menuBarPresenter.alarmsActive();
	}
	
	//TODO refresh based on process flow status
	public void refreshStatus() {
		menuBarPresenter.setConfigureButtonEnabled(true);
		menuBarPresenter.setTeachButtonEnabled(false);
		menuBarPresenter.setAutomateButtonEnabled(false);
		if (configurePresenter.isConfigured()) {
			if (process.isConfigured()) {
				menuBarPresenter.setTeachButtonEnabled(true);
			} else {
				throw new IllegalStateException("Configuration UI says ok, but there is still data missing");
			}
		} 
		if (process.isTeached()) {
			menuBarPresenter.setAutomateButtonEnabled(true);
		}
	}
	
	public void showRobot() {
		menuBarPresenter.robotActive();
		view.addPopup(robotPopUpPresenter.getView());
	}
	
	public void closePopUp(AbstractPopUpPresenter<?> presenter) {
		closePopUps();
	}
	
	public void closePopUps() {
		menuBarPresenter.disablePopUp();
		view.closePopup();
	}
	
	public void showAdmin() {
		menuBarPresenter.showAdminView();
	}
	
	public MainView getView() {
		return view;
	}
	
	public void setChangeContentEnabled(boolean enabled) {
		if (!enabled) {
			menuBarPresenter.setAutomateButtonEnabled(enabled);
			menuBarPresenter.setConfigureButtonEnabled(enabled);
			menuBarPresenter.setTeachButtonEnabled(enabled);
		}
		//menuBarPresenter.setAdminButtonEnabled(enabled);
	}
	
	public void loadProcessFlow(ProcessFlow process) {
		if (this.process != null) {
			this.process.removeListener(this);
		}
		this.process = process;
		configurePresenter.loadProcessFlow(process);
		teachPresenter.loadProcessFlow(process);
		automatePresenter.loadProcessFlow(process);
		this.process.addListener(this);
	}
	
	public void showMessage(String message) {
		
	}
	
	public ProcessFlow getProcessFlow() {
		return process;
	}
	
	public void updateProcessConfiguredStatus(boolean configured) {
		
	}
	
	public void exit() {
		Platform.exit();
	}

	@Override public void modeChanged(ModeChangedEvent e) {
		refreshStatus();
		switch(e.getMode()) {
			case AUTO:
			case TEACH:
				setChangeContentEnabled(false);
				break;
			default:
				break;
		}
	}
	@Override public void activeStepChanged(ActiveStepChangedEvent e) {}
	@Override public void exceptionOccured(ExceptionOccuredEvent e) {}
	@Override public void finishedAmountChanged(FinishedAmountChangedEvent e) {}
	
	@Override
	public void dataChanged(ProcessFlowEvent e) {
		refreshStatus();
	}

	
}
