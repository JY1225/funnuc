package eu.robojob.millassist.ui.admin;

import eu.robojob.millassist.ui.admin.device.DeviceAdminPresenter;
import eu.robojob.millassist.ui.admin.general.GeneralAdminPresenter;
import eu.robojob.millassist.ui.admin.robot.RobotAdminPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class MainMenuPresenter extends AbstractMenuPresenter<MainMenuView> {
	
	private GeneralAdminPresenter generalAdminPresenter;
	private RobotAdminPresenter robotAdminPresenter;
	private DeviceAdminPresenter deviceAdminPresenter;
	
	public MainMenuPresenter(final MainMenuView view, final GeneralAdminPresenter generalAdminPresenter,
			final RobotAdminPresenter robotAdminPresenter, final DeviceAdminPresenter deviceAdminPresenter) {
		super(view);
		this.generalAdminPresenter = generalAdminPresenter;
		this.robotAdminPresenter = robotAdminPresenter;
		this.deviceAdminPresenter = deviceAdminPresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		generalAdminPresenter.setTextFieldListener(listener);
		robotAdminPresenter.setTextFieldListener(listener);
		deviceAdminPresenter.setTextFieldListener(listener);
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		getView().setRobotMenuActive();
		getParent().setContentNode(robotAdminPresenter.getView());
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
		getParent().setContentNode(generalAdminPresenter.getView());
	}
	
	public void robotMenuClicked() {
		getView().setRobotMenuActive();
		getParent().setContentNode(robotAdminPresenter.getView());
	}
	
	public void deviceMenuClicked() {
		getView().setDeviceMenuActive();
		getParent().setContentNode(deviceAdminPresenter.getView());
	}

	@Override
	public void unregisterListeners() {
		//TODO pass to individual admin presenters?
	}

}
