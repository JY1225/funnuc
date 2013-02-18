package eu.robojob.irscw.ui.admin.robot;

import eu.robojob.irscw.ui.admin.AbstractMenuPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public class RobotMenuPresenter extends AbstractMenuPresenter<RobotMenuView> {

	public RobotMenuPresenter(final RobotMenuView view) {
		super(view);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
	}
	
	public void configureGeneral() {
		
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		getView().setConfigureGeneralActive();
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

}
