package eu.robojob.millassist.ui.admin.robot;

import eu.robojob.millassist.ui.admin.AbstractSubMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class RobotMenuPresenter extends AbstractSubMenuPresenter<RobotMenuView, RobotAdminPresenter> {

	private RobotConfigurePresenter configurePresenter;
	private RobotGripperPresenter gripperPresenter;
	private RobotDataPresenter dataPresenter;
	
	public RobotMenuPresenter(final RobotMenuView view, final RobotConfigurePresenter configurePresenter, 
	        final RobotGripperPresenter gripperPresenter, final RobotDataPresenter dataPresenter) {
		super(view);
		this.configurePresenter = configurePresenter;
		this.gripperPresenter = gripperPresenter;
		this.dataPresenter = dataPresenter;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		configurePresenter.setTextFieldListener(listener);
		gripperPresenter.setTextFieldListener(listener);
		dataPresenter.setTextFieldListener(listener);
	}
	
	public void configureGeneral() {
		getView().setConfigureGeneralActive();
		getParent().setContentView(configurePresenter.getView());
	}
	
	public void configureGrippers() {
		getView().setConfigureGrippersActive();
		getParent().setContentView(gripperPresenter.getView());
	}
	
	public void configureData() {
	    getView().setConfigureDataActive();
	    getParent().setContentView(dataPresenter.getView());
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

	@Override
	public void unregisterListeners() {
		configurePresenter.unregister();
	}

}
