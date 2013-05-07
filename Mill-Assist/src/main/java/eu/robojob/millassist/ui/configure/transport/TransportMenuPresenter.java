package eu.robojob.millassist.ui.configure.transport;

import eu.robojob.millassist.ui.configure.AbstractMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class TransportMenuPresenter extends AbstractMenuPresenter<TransportMenuView> {

	private TransportGripperPresenter transportGripperPresenter;
	private TransportInterventionPresenter transportInterventionPresenter;
	
	public TransportMenuPresenter(final TransportMenuView view, final TransportGripperPresenter transportGripperPresenter, final TransportInterventionPresenter transportInterventionPresenter) {
		super(view);
		this.transportGripperPresenter = transportGripperPresenter;
		this.transportInterventionPresenter = transportInterventionPresenter;
		transportGripperPresenter.setMenuPresenter(this);
		transportInterventionPresenter.setMenuPresenter(this);
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	public void configureGriper() {
		getView().setConfigureGripperActive();
		getParent().setBottomRightView(transportGripperPresenter.getView());
	}

	@Override
	public void openFirst() {
		configureGriper();
	}
	
	public void configureInterventions() {
		getView().setConfigureInterventionsActive();
		getParent().setBottomRightView(transportInterventionPresenter.getView());
	}

	@Override
	public void setBlocked(final boolean blocked) {
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		transportInterventionPresenter.setTextFieldListener(listener);
	}
	
	public void processFlowUpdated() {
		getParent().updateProcessFlow();
	}

	@Override
	public boolean isConfigured() {
		return transportGripperPresenter.isConfigured() && transportInterventionPresenter.isConfigured();
	}

	@Override
	public void unregisterListeners() { }
}
