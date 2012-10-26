package eu.robojob.irscw.ui;

public class MenuBarPresenter {

	//private static Logger logger = Logger.getLogger(MenuBarPresenter.class);
	
	private MenuBarView view;
	private MainPresenter parent;
	
	public MenuBarPresenter(MenuBarView processMenuBarView) {
		this.view = processMenuBarView;
		processMenuBarView.setPresenter(this);
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
		parent.showAlarms();
	}
	
	public void showAlarmsView() {
		view.setAlarmsActive();
	}
	
	public void setConfigureButtonEnabled(boolean enabled) {
		view.setConfigureButtonEnabled(enabled);
	}
	
	public void setTeachButtonEnabled(boolean enabled) {
		view.setTeachButtonEnabled(enabled);
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
