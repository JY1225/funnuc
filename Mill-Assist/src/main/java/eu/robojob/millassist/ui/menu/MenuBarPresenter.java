package eu.robojob.millassist.ui.menu;

import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.ui.MainPresenter;
import eu.robojob.millassist.util.Translator;

public class MenuBarPresenter {
	
	private MenuBarView view;
	private MainPresenter parent;
	
	private boolean adminActive;
	private boolean robotActive;
	private boolean alarmsActive;
	
	private static final String UNSAVED_CHANGES_TITLE = "MenuBarPresenter.unsavedChangesTitle";
	private static final String UNSAVED_CHANGES = "MenuBarPresenter.unsavedChanges";
	
	public MenuBarPresenter(final MenuBarView processMenuBarView) {
		this.view = processMenuBarView;
		processMenuBarView.setPresenter(this);
		robotActive = false;
		alarmsActive = false;
		adminActive = false;
	}
	
	public void setParent(final MainPresenter parent) {
		this.parent = parent;
	}
	
	public MenuBarView getView() {
		return view;
	}
	
	public void clickedConfigure() {
		parent.closePopUps();
		parent.showConfigure();
	}
	
	public void showConfigureView() {
		view.setRobotButtonEnabled(true);
		view.setAlarmsButtonEnabled(true);
		view.setExitButtonEnabled(true);
		view.setConfigureButtonEnabled(true);
		view.setConfigureActive();
		adminActive = false;
	}
	
	public void clickedTeach() {
		parent.closePopUps();
		parent.showTeach();
	}
	
	public void showTeachView() {
		view.setTeachActive();
	}
	
	public void clickedAutomate() {
		parent.closePopUps();
		parent.showAutomate();
	}
	
	public void showAutomateView() {
		view.setAutomateActive();
	}
	
	public void clickedAdmin() {
		parent.closePopUps();
		if (adminActive) {
			parent.showConfigure();
		} else {
			parent.showAdmin();
		}
	}
	
	public void showAdminView() {
		view.setAdminActive();
		view.setTeachButtonEnabled(false);
		view.setAutomateButtonEnabled(false);
		view.setRobotButtonEnabled(false);
		view.setAlarmsButtonEnabled(false);
		view.setExitButtonEnabled(false);
		view.setConfigureButtonEnabled(false);
		adminActive = true;
	}
	
	public void clickedAlarms() {
		if (!alarmsActive) {
			parent.closePopUps();
			parent.showAlarms();
		} else {
			parent.closePopUps();
		}
	}
	
	public void disablePopUps() {
		view.setNonePopupActive();
		robotActive = false;
		alarmsActive = false;
	}
	
	public void clickedRobot() {
		if (!robotActive) {
			parent.closePopUps();
			parent.showRobot();
		} else {
			parent.closePopUps();
		}
	}
	
	public void robotActive() {
		robotActive = true;
		view.setRobotActive();
	}
	
	public void alarmsActive() {
		alarmsActive = true;
		view.setAlarmsActive();
	}
	
	public void indicateAlarmsPresent(final boolean alarmsPresent) {
		getView().indicateAlarmsPresent(alarmsPresent);
	}
	
	public void setConfigureButtonEnabled(final boolean enabled) {
		view.setConfigureButtonEnabled(enabled);
	}
	
	public void setTeachButtonEnabled(final boolean enabled) {
		view.setTeachButtonEnabled(enabled);
	}
	
	public void setAdminButtonEnabled(final boolean enabled) {
		view.setAdminButtonEnabled(enabled);
	}
	
	public void setAutomateButtonEnabled(final boolean enabled) {
		view.setAutomateButtonEnabled(enabled);
	}
	
	public void setMenuBarEnabled(final boolean enabled) {
		view.setEnabled(enabled);
	}
	
	public void clickedExit() {
		ThreadManager.submit(new Thread() {
			@Override
			public void run() {
				boolean doExit = true;
				if (parent.getProcessFlow().hasChangesSinceLastSave()) {
					if (!parent.askConfirmation(Translator.getTranslation(UNSAVED_CHANGES_TITLE), Translator.getTranslation(UNSAVED_CHANGES))) {
						doExit = false;
					}
				}
				if (doExit) {
					parent.exit();
				}
			}
		});
	}
	
}
