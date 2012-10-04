package eu.robojob.irscw.ui.configure.transport;

import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;

public class TransportMenuPresenter extends AbstractMenuPresenter<TransportMenuView> {

	private TransportGripperPresenter transportGripperPresenter;
	private TransportInterventionPresenter transportInterventionPresenter;
	
	public TransportMenuPresenter(TransportMenuView view, TransportGripperPresenter transportGripperPresenter, TransportInterventionPresenter transportInterventionPresenter) {
		super(view);
		this.transportGripperPresenter = transportGripperPresenter;
		this.transportInterventionPresenter = transportInterventionPresenter;
		transportGripperPresenter.setMenuPresenter(this);
		transportInterventionPresenter.setMenuPresenter(this);
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
		parent.setBottomRightView(transportInterventionPresenter.getView());
	}

	@Override
	public void setBlocked(boolean blocked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextFieldListener(ConfigurePresenter parent) {
		transportInterventionPresenter.setTextFieldListener(parent);
	}
	
	public void processFlowUpdated() {
		parent.updateProcessFlow();
	}

	@Override
	public boolean isConfigured() {
		return transportGripperPresenter.isConfigured() && transportInterventionPresenter.isConfigured();
	}
}
