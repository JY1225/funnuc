package eu.robojob.irscw.ui.admin.robot;

import eu.robojob.irscw.ui.admin.AbstractSubMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class RobotMenuPresenter extends AbstractSubMenuPresenter<RobotMenuView, RobotAdminPresenter> {

	private RobotConfigurePresenter configurePresenter;
	private RobotGripperPresenter gripperPresenter;
	
	public RobotMenuPresenter(final RobotMenuView view, final RobotConfigurePresenter configurePresenter, final RobotGripperPresenter gripperPresenter) {
		super(view);
		this.configurePresenter = configurePresenter;
		this.gripperPresenter = gripperPresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		configurePresenter.setTextFieldListener(listener);
		gripperPresenter.setTextFieldListener(listener);
	}
	
	public void configureGeneral() {
		getView().setConfigureGeneralActive();
		getParent().setContentView(configurePresenter.getView());
	}
	
	public void configureGrippers() {
		getView().setConfigureGrippersActive();
		getParent().setContentView(gripperPresenter.getView());
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		configureGeneral();
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

}
