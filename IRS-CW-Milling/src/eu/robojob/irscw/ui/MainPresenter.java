package eu.robojob.irscw.ui;

import javafx.application.Platform;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.event.DataChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.ui.admin.AdminPresenter;
import eu.robojob.irscw.ui.automate.AutomatePresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.menu.MenuBarPresenter;
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
	private AdminPresenter adminPresenter;
	
	private MainContentPresenter activeContentPresenter;
		
	public MainPresenter(final MainView view, final MenuBarPresenter menuBarPresenter, final ConfigurePresenter configurePresenter, final TeachPresenter teachPresenter, 
			final AutomatePresenter automatePresenter, final RobotPopUpPresenter robotPopUpPresenter, final AdminPresenter adminPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.menuBarPresenter = menuBarPresenter;
		menuBarPresenter.setParent(this);
		this.configurePresenter = configurePresenter;
		configurePresenter.setParent(this);
		this.teachPresenter = teachPresenter;
		teachPresenter.setParent(this);
		this.automatePresenter = automatePresenter;
		automatePresenter.setParent(this);
		this.robotPopUpPresenter = robotPopUpPresenter;
		robotPopUpPresenter.setParent(this);
		this.adminPresenter = adminPresenter;
		adminPresenter.setParent(this);
		view.setMenuBarView(menuBarPresenter.getView());
		this.process = null;
	}

	private void setActiveMainContentPresenter(final MainContentPresenter presenter) {
		if (activeContentPresenter != null) {
			activeContentPresenter.setActive(false);
		}
		activeContentPresenter = presenter;
		view.setContentView(activeContentPresenter.getView());
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
		//TODO show alarms content
	}

	public void showRobot() {
		menuBarPresenter.robotActive();
		view.addPopUpView(robotPopUpPresenter.getView());
	}
	
	public void showAdmin() {
		menuBarPresenter.showAdminView();
		setActiveMainContentPresenter(adminPresenter);
	}
	
	public void closePopUps() {
		menuBarPresenter.disablePopUps();
		view.closePopup();
	}
	
	public void refreshStatus() {
		menuBarPresenter.setConfigureButtonEnabled(true);
		menuBarPresenter.setTeachButtonEnabled(false);
		menuBarPresenter.setAutomateButtonEnabled(false);
		if (configurePresenter.isConfigured()) {
			if (process.isConfigured()) {
				menuBarPresenter.setTeachButtonEnabled(true);
				if (process.isTeached()) {
					menuBarPresenter.setAutomateButtonEnabled(true);
				}
			} else {
				throw new IllegalStateException("Configuration UI says ok, but there is still data missing.");
			}
		} 
	}
	
	public MainView getView() {
		return view;
	}
	
	public void setChangeContentEnabled(final boolean enabled) {
		menuBarPresenter.setAutomateButtonEnabled(enabled);
		menuBarPresenter.setConfigureButtonEnabled(enabled);
		menuBarPresenter.setTeachButtonEnabled(enabled);
		menuBarPresenter.setAdminButtonEnabled(enabled);
		if (enabled) {
			refreshStatus();
		}
	}
	
	public void loadProcessFlow(final ProcessFlow process) {
		if (this.process != null) {
			this.process.removeListener(this);
		}
		this.process = process;
		configurePresenter.loadProcessFlow(process);
		teachPresenter.loadProcessFlow(process);
		automatePresenter.loadProcessFlow(process);
		this.process.addListener(this);
	}
	
	public ProcessFlow getProcessFlow() {
		return process;
	}
	
	public void exit() {
		Platform.exit();
	}

	@Override public void modeChanged(final ModeChangedEvent e) {
		refreshStatus();
		switch(e.getMode()) {
			case AUTO:
			case TEACH:
				setChangeContentEnabled(false);
				break;
			default:
				setChangeContentEnabled(true);
				break;
		}
	}
	
	@Override public void statusChanged(final StatusChangedEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }

	@Override
	public void dataChanged(final DataChangedEvent e) {
		if (e instanceof ProcessChangedEvent) {
			configurePresenter.loadProcessFlow(e.getSource());
			teachPresenter.loadProcessFlow(e.getSource());
			automatePresenter.loadProcessFlow(e.getSource());
 		} else {
 			refreshStatus();
 		}
	}
	
}
