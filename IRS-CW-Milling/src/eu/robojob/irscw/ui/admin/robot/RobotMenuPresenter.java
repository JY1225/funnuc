package eu.robojob.irscw.ui.admin.robot;

import eu.robojob.irscw.ui.admin.AbstractSubMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class RobotMenuPresenter extends AbstractSubMenuPresenter<RobotMenuView, RobotAdminPresenter> {

	private RobotConfigurePresenter configurePresenter;
	
	public RobotMenuPresenter(final RobotMenuView view, final RobotConfigurePresenter configurePresenter) {
		super(view);
		this.configurePresenter = configurePresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		configurePresenter.setTextFieldListener(listener);
	}
	
	public void configureGeneral() {
		getView().setConfigureGeneralActive();
		getParent().setContentView(configurePresenter.getView());
	}
	
	public void configureGrippers() {
		getView().setConfigureGrippersActive();
		
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
