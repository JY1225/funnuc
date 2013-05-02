package eu.robojob.irscw.ui.menu;

import eu.robojob.irscw.ui.MainPresenter;

public class MenuBarPresenter {
	
	private MenuBarView view;
	private MainPresenter parent;
	
	private boolean adminActive;
	private boolean robotActive;
	private boolean alarmsActive;
	
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
		parent.showTeach();
	}
	
	public void showTeachView() {
		view.setTeachActive();
	}
	
	public void clickedAutomate() {
		parent.showAutomate();
	}
	
	public void showAutomateView() {
		view.setAutomateActive();
	}
	
	public void clickedAdmin() {
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
		parent.exit();
	}
	
}
