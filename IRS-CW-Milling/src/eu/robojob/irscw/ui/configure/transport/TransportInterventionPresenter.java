package eu.robojob.irscw.ui.configure.transport;

import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;
import eu.robojob.irscw.ui.general.model.TransportInformation;

public class TransportInterventionPresenter extends AbstractFormPresenter<TransportInterventionView, TransportMenuPresenter> {

	private TransportInformation transportInfo;
	private ProcessFlowAdapter processFlowAdapter;
		
	public TransportInterventionPresenter(final TransportInterventionView view, final TransportInformation transportInfo, final ProcessFlowAdapter processFlowAdapter) {
		super(view);
		this.transportInfo = transportInfo;
		this.processFlowAdapter = processFlowAdapter;
		view.setTransportInfo(transportInfo);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	public void clickedInterventionBeforePick() {
		if (transportInfo.hasInterventionBeforePick()) {
			processFlowAdapter.getProcessFlow().removeStep(transportInfo.getInterventionBeforePick());
		} else {
			processFlowAdapter.addInterventionStepBeforePick(transportInfo);
		}
		transportInfo = processFlowAdapter.getTransportInformation(transportInfo.getIndex());
		getView().setTransportInfo(transportInfo);
		getMenuPresenter().processFlowUpdated();
		getView().refresh();
	}
	
	public void clickedInterventionAfterPut() {
		if (transportInfo.hasInterventionAfterPut()) {
			processFlowAdapter.getProcessFlow().removeStep(transportInfo.getInterventionAfterPut());
		} else {
			processFlowAdapter.addInterventionStepAfterPut(transportInfo);
		}
		transportInfo = processFlowAdapter.getTransportInformation(transportInfo.getIndex());
		getView().setTransportInfo(transportInfo);
		getMenuPresenter().processFlowUpdated();
		getView().refresh();
	}
	
	public void changedInterventionBeforePickInterval(final Integer interval)  {
		transportInfo.getInterventionBeforePick().setFrequency(interval);
		getMenuPresenter().processFlowUpdated();
		getView().refresh();
	}
	
	public void changedInterventionAfterPutInterval(final Integer interval) {
		transportInfo.getInterventionAfterPut().setFrequency(interval);
		getMenuPresenter().processFlowUpdated();
		getView().refresh();
	}

	@Override
	public boolean isConfigured() {
		return true;
	}
}
