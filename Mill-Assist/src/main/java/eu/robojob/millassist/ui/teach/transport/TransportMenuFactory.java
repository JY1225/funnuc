package eu.robojob.millassist.ui.teach.transport;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.general.model.TransportInformation;
import eu.robojob.millassist.ui.teach.AbstractMenuPresenter;

public class TransportMenuFactory {

	private Map<Integer, AbstractMenuPresenter<?>> presentersBuffer;
	
	public TransportMenuFactory(final ProcessFlow processFlow) {
		presentersBuffer = new HashMap<Integer, AbstractMenuPresenter<?>>();
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
			TransportTeachedOffsetPresenter presenter = new TransportTeachedOffsetPresenter(view, transportInfo.getPickStep());
			return presenter;
		}
	}
	
	public TransportTeachedOffsetPresenter getPutPresenter(final TransportInformation transportInfo) {
		TransportTeachedOffsetView view = new TransportTeachedOffsetView();
		TransportTeachedOffsetPresenter presenter = new TransportTeachedOffsetPresenter(view, transportInfo.getPutStep());
		return presenter;
	}
	
	public void clearBuffer() {
		for (AbstractMenuPresenter<?> presenter : presentersBuffer.values()) {
			presenter.unregisterListeners();
		}
		presentersBuffer.clear();
	}
}
