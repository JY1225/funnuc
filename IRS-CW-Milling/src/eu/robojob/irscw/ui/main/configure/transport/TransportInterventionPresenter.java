package eu.robojob.irscw.ui.main.configure.transport;

import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportInterventionPresenter extends AbstractFormPresenter<TransportInterventionView, TransportMenuPresenter> {

	private TransportInformation transportInfo;
	
	public TransportInterventionPresenter(TransportInterventionView view, TransportInformation transportInfo) {
		super(view);
		this.transportInfo = transportInfo;
		view.setTransportInfo(transportInfo);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

}
