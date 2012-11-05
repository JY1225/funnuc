package eu.robojob.irscw.ui;

public class MenuBarPresenter {

	//private static Logger logger = Logger.getLogger(MenuBarPresenter.class);
	
	private MenuBarView view;
	private MainPresenter parent;
	
	private boolean robotActive;
	private boolean alarmsActive;
	
	public MenuBarPresenter(MenuBarView processMenuBarView) {
		this.view = processMenuBarView;
		processMenuBarView.setPresenter(this);
		robotActive = false;
		alarmsActive = false;
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
	public MenuBarView getView() {
		return view;
	}
	
	public void clickedConfigure() {
		parent.showConfigure();
	}
	
	public void showConfigureView() {
		view.setConfigureActive();
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
		parent.showAdmin();
	}
	
	public void showAdminView() {
		view.setAdminActive();
	}
	
	public void clickedAlarms() {
		if (!alarmsActive) {
			parent.showAlarms();
		}
	}
	
	public void disablePopUp() {
		view.setNonePopupActive();
		robotActive = false;
		alarmsActive = false;
	}
	
	public void clickedRobot() {
		if (!robotActive) {
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
	
	public void setConfigureButtonEnabled(boolean enabled) {
		view.setConfigureButtonEnabled(enabled);
	}
	
	public void setTeachButtonEnabled(boolean enabled) {
		view.setTeachButtonEnabled(enabled);
	}
	
	public void setAdminButtonEnabled(boolean enabled) {
		view.setAdminButtonEnabled(enabled);
	}
	
	public void setAutomateButtonEnabled(boolean enabled) {
		view.setAutomateButtonEnabled(enabled);
	}
	
	public void setMenuBarEnabled(boolean enabled) {
		view.setEnabled(enabled);
	}
	
	public void clickedExit() {
		parent.exit();
	}
	
}
