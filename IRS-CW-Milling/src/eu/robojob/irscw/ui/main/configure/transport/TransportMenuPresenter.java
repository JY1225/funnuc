package eu.robojob.irscw.ui.main.configure.transport;

import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;

public class TransportMenuPresenter extends AbstractMenuPresenter<TransportMenuView> {

	public TransportMenuPresenter(TransportMenuView view) {
		super(view);
	}

	@Override
	protected void setPresenter() {
		view.setPresenter(this);
	}

	public void configureGriper() {
		view.setConfigureGripperActive();
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
}
