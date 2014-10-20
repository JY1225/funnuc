package eu.robojob.millassist.ui;

import javafx.application.Platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.admin.AdminPresenter;
import eu.robojob.millassist.ui.alarms.AlarmsPopUpPresenter;
import eu.robojob.millassist.ui.automate.AutomatePresenter;
import eu.robojob.millassist.ui.configure.ConfigurePresenter;
import eu.robojob.millassist.ui.general.MainContentPresenter;
import eu.robojob.millassist.ui.general.dialog.ConfirmationDialogPresenter;
import eu.robojob.millassist.ui.general.dialog.ConfirmationDialogView;
import eu.robojob.millassist.ui.general.dialog.NotificationDialogPresenter;
import eu.robojob.millassist.ui.general.dialog.NotificationDialogView;
import eu.robojob.millassist.ui.menu.MenuBarPresenter;
import eu.robojob.millassist.ui.robot.RobotPopUpPresenter;
import eu.robojob.millassist.ui.teach.TeachPresenter;

public class MainPresenter implements ProcessFlowListener {
	
	private MainView view;
	
	private ProcessFlow process;
	
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private TeachPresenter teachPresenter;
	private AutomatePresenter automatePresenter;
	private AlarmsPopUpPresenter alarmsPopUpPresenter;
	private RobotPopUpPresenter robotPopUpPresenter;
	private AdminPresenter adminPresenter;
	
	private MainContentPresenter activeContentPresenter;
	
	private final Logger logger = LogManager.getLogger(MainPresenter.class.getName());
			
	public MainPresenter(final MainView view, final MenuBarPresenter menuBarPresenter, final ConfigurePresenter configurePresenter, final TeachPresenter teachPresenter, 
			final AutomatePresenter automatePresenter, final AlarmsPopUpPresenter alarmsPopUpPresenter, 
				final RobotPopUpPresenter robotPopUpPresenter, final AdminPresenter adminPresenter) {
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
		this.alarmsPopUpPresenter = alarmsPopUpPresenter;
		alarmsPopUpPresenter.setParent(this);
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
		configurePresenter.getProcessFlowPresenter().backgroundClicked();
		configurePresenter.refreshClearCache();
		setActiveMainContentPresenter(configurePresenter);
		refreshStatus();
	}
	
	public void indicateAlarmsPresent(final boolean alarmsPresent) {
		menuBarPresenter.indicateAlarmsPresent(alarmsPresent);
	}
	
	public void showTeach() {
		menuBarPresenter.showTeachView();
		teachPresenter.getProcessFlowPresenter().backgroundClicked();
		setActiveMainContentPresenter(teachPresenter);
		refreshStatus();
	}
	
	public void showAutomate() {
		menuBarPresenter.showAutomateView();
		automatePresenter.getProcessFlowPresenter().backgroundClicked();
		setActiveMainContentPresenter(automatePresenter);
		refreshStatus();
	}
	
	public void showAlarms() {
		menuBarPresenter.alarmsActive();
		view.addPopUpView(alarmsPopUpPresenter.getView());
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
				setChangeContentEnabled(false);
				break;
			case TEACH:
				setChangeContentEnabled(false);
				break;
			case PAUSED:
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
 			if (e.getSource().getMode() == Mode.CONFIG) {
 				refreshStatus();
 			}
 		}
	}

	@Override
	public void unregister() {
		process.removeListener(this);
	}
	
	public boolean askConfirmation(final String title, final String message) {
		final ConfirmationDialogView view = new ConfirmationDialogView(title, message);
		ConfirmationDialogPresenter confirmationDialogPresenter = new ConfirmationDialogPresenter(view);
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				getView().showDialog(view);
			}
		});
		boolean returnValue = false;
		try {
			returnValue = confirmationDialogPresenter.getResult();
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				getView().hideDialog();
			}
		});
		return returnValue;
	}
	
	public void showNotificationOverlay(final String title, final String message) {
		final NotificationDialogView view = new NotificationDialogView(title, message);
		NotificationDialogPresenter confirmationDialogPresenter = new NotificationDialogPresenter(view);
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				getView().showDialog(view);
			}
		});
		try {
			confirmationDialogPresenter.getResult();
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				getView().hideDialog();
			}
		});
	}
}
