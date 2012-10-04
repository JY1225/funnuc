package eu.robojob.irscw.ui.configure.transport;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportInterventionPresenter extends AbstractFormPresenter<TransportInterventionView, TransportMenuPresenter> {

	private TransportInformation transportInfo;
	private ProcessFlowAdapter processFlowAdapter;
	
	private static Logger logger = Logger.getLogger(TransportInterventionPresenter.class);
	
	public TransportInterventionPresenter(TransportInterventionView view, TransportInformation transportInfo, ProcessFlowAdapter processFlowAdapter) {
		super(view);
		this.transportInfo = transportInfo;
		this.processFlowAdapter = processFlowAdapter;
		view.setTransportInfo(transportInfo);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	public void clickedInterventionBeforePick() {
		if (transportInfo.hasInterventionBeforePick()) {
			processFlowAdapter.removeInterventionStepBeforePick(transportInfo);
			logger.debug("removed interventionstep before pick");
		} else {
			processFlowAdapter.addInterventionStepBeforePick(transportInfo);
			logger.debug("added interventionstep before pick");
		}
		transportInfo = processFlowAdapter.getTransportInformation(transportInfo.getIndex());
		view.setTransportInfo(transportInfo);
		menuPresenter.processFlowUpdated();
		view.refresh();
	}
	
	public void clickedInterventionAfterPut() {
		if (transportInfo.hasInterventionAfterPut()) {
			processFlowAdapter.removeInterventionStepAfterPut(transportInfo);
			logger.debug("removed interventionstep before pick");
		} else {
			processFlowAdapter.addInterventionStepAfterPut(transportInfo);
			logger.debug("added interventionstep after put");
		}
		transportInfo = processFlowAdapter.getTransportInformation(transportInfo.getIndex());
		view.setTransportInfo(transportInfo);
		menuPresenter.processFlowUpdated();
		view.refresh();
	}
	
	public void changedInterventionBeforePickInterval(Integer interval)  {
		if (transportInfo.hasInterventionBeforePick()) {
			transportInfo.getInterventionBeforePick().setFrequency(interval);
			menuPresenter.processFlowUpdated();
			view.refresh();
		} else {
			throw new IllegalStateException("No intervention step found, so this value can't be set");
		}
	}
	
	public void changedInterventionAfterPutInterval(Integer interval) {
		if (transportInfo.hasInterventionAfterPut()) {
			transportInfo.getInterventionAfterPut().setFrequency(interval);
			menuPresenter.processFlowUpdated();
			view.refresh();
		} else {
			throw new IllegalStateException("No intervention step found, so this value can't be set");
		}
	}

	@Override
	public boolean isConfigured() {
		return true;
	}
}
