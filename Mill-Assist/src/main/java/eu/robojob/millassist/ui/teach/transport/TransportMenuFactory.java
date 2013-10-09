package eu.robojob.millassist.ui.teach.transport;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.general.model.ProcessFlowAdapter;
import eu.robojob.millassist.ui.general.model.TransportInformation;
import eu.robojob.millassist.ui.teach.AbstractMenuPresenter;

public class TransportMenuFactory {

	private Map<Integer, AbstractMenuPresenter<?>> presentersBuffer;
	private ProcessFlowAdapter processFlowAdapter;
	
	public TransportMenuFactory(final ProcessFlow processFlow, final ProcessFlowAdapter processFlowAdapter) {
		presentersBuffer = new HashMap<Integer, AbstractMenuPresenter<?>>();
		this.processFlowAdapter = processFlowAdapter;
	}
	
	public AbstractMenuPresenter<?> getTransportMenu(final TransportInformation transportInfo) {
		AbstractMenuPresenter<?> menuPresenter = presentersBuffer.get(transportInfo.getIndex());
		if (menuPresenter == null) {
			menuPresenter =  getTransportMenuPresenter(transportInfo);
			presentersBuffer.put(transportInfo.getIndex(), menuPresenter);
		}
		return menuPresenter;
	}
	
	public TransportMenuPresenter getTransportMenuPresenter(final TransportInformation transportInfo) {
		TransportMenuView view = new TransportMenuView(transportInfo);
		TransportMenuPresenter transportMenuPresenter = new TransportMenuPresenter(view, getPickPresenter(transportInfo), getPutPresenter(transportInfo));
		return transportMenuPresenter;
	}
	
	public TransportTeachedOffsetPresenter getPickPresenter(final TransportInformation transportInfo) {
		if (transportInfo.getPickStep() instanceof PickAfterWaitStep) {
			return null;
		} else {
			TransportTeachedOffsetView view = new TransportTeachedOffsetView();
			TransportTeachedOffsetPresenter presenter = new TransportTeachedOffsetPresenter(view, transportInfo.getPickStep(), transportInfo.getPutStep());
			return presenter;
		}
	}
	
	public TransportTeachedOffsetPresenter getPutPresenter(final TransportInformation transportInfo) {
		TransportTeachedOffsetView view = new TransportTeachedOffsetView();
		TransportInformation nextTransportInfo = processFlowAdapter.getTransportInformation(transportInfo.getIndex() + 1);
		TransportTeachedOffsetPresenter presenter = new TransportTeachedOffsetPresenter(view, transportInfo.getPutStep(), nextTransportInfo.getPickStep());
		return presenter;
	}
	
	public void clearBuffer() {
		for (AbstractMenuPresenter<?> presenter : presentersBuffer.values()) {
			presenter.unregisterListeners();
		}
		presentersBuffer.clear();
	}
}
