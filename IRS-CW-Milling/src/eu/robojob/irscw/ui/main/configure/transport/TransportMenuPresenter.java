package eu.robojob.irscw.ui.main.configure.transport;

import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class TransportMenuPresenter extends AbstractMenuPresenter<TransportMenuView> {

	private TransportGripperPresenter transportGripperPresenter;
	
	public TransportMenuPresenter(TransportMenuView view, TransportGripperPresenter transportGripperPresenter) {
		super(view);
		this.transportGripperPresenter = transportGripperPresenter;
	}

	@Override
	protected void setPresenter() {
		view.setPresenter(this);
	}

	public void configureGriper() {
		view.setConfigureGripperActive();
		parent.setBottomRightView(transportGripperPresenter.getView());
	}

	@Override
	public void openFirst() {
		configureGriper();
	}
	
	public void configureInterventions() {
		view.setConfigureInterventionsActive();
	}

	@Override
	public void setBlocked(boolean blocked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextFieldListener(ConfigurePresenter parent) {
		// TODO Auto-generated method stub
		
	}
}
