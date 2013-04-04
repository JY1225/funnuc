package eu.robojob.irscw.ui.configure.transport;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;
import eu.robojob.irscw.ui.general.model.TransportInformation;

public class TransportMenuFactory {

	private ProcessFlowAdapter processFlowAdapter;
	
	private Map<Integer, AbstractMenuPresenter<?>> presentersBuffer;
	
	public TransportMenuFactory(final ProcessFlow processFlow) {
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
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
		TransportMenuPresenter transportMenuPresenter = new TransportMenuPresenter(view, getTransportGripperPresenter(transportInfo), getTransportInterventionPresenter(transportInfo));
		return transportMenuPresenter;
	}
	
	public TransportGripperPresenter getTransportGripperPresenter(final TransportInformation transportInfo) {
		TransportGripperView view = new TransportGripperView();
		TransportGripperPresenter transportGripperPresenter = new TransportGripperPresenter(view, transportInfo);
		return transportGripperPresenter;
	}
	
	public TransportInterventionPresenter getTransportInterventionPresenter(final TransportInformation transportInfo) {
		TransportInterventionView view = new TransportInterventionView();
		TransportInterventionPresenter transportInterventionPresenter = new TransportInterventionPresenter(view, transportInfo, processFlowAdapter);
		return transportInterventionPresenter;
	}
	
	public void clearBuffer() {
		presentersBuffer.clear();
	}
}
