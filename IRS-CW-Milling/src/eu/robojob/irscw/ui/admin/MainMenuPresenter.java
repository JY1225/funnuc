package eu.robojob.irscw.ui.admin;

import eu.robojob.irscw.ui.admin.robot.RobotAdminPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class MainMenuPresenter extends AbstractMenuPresenter<MainMenuView> {
	
	private RobotAdminPresenter robotAdminPresenter;
	
	public MainMenuPresenter(final MainMenuView view, final RobotAdminPresenter robotAdminPresenter) {
		super(view);
		this.robotAdminPresenter = robotAdminPresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		robotAdminPresenter.setTextFieldListener(listener);
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		getView().setGeneralMenuActive();
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void generalMenuClicked() {
		getView().setGeneralMenuActive();
	}
	
	public void robotMenuClicked() {
		getView().setRobotMenuActive();
		getParent().setContentNode(robotAdminPresenter.getView());
	}
	
	public void deviceMenuClicked() {
		getView().setDeviceMenuActive();
	}

}
