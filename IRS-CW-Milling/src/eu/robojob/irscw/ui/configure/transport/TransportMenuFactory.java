package eu.robojob.irscw.ui.configure.transport;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportMenuFactory {

	private ProcessFlowAdapter processFlowAdapter;
	
	private Map<TransportInformation, AbstractMenuPresenter<?>> presentersBuffer;
	
	public TransportMenuFactory(ProcessFlow processFlow) {
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		presentersBuffer = new HashMap<TransportInformation, AbstractMenuPresenter<?>>();
	}
	
	public AbstractMenuPresenter<?> getTransportMenu(TransportInformation transportInfo) {
		AbstractMenuPresenter<?> menuPresenter = presentersBuffer.get(transportInfo);
		if (menuPresenter == null) {
			menuPresenter =  getTransportMenuPresenter(transportInfo);
			presentersBuffer.put(transportInfo, menuPresenter);
		}
		return menuPresenter;
	}
	
	public TransportMenuPresenter getTransportMenuPresenter(TransportInformation transportInfo) {
		TransportMenuView view = new TransportMenuView(transportInfo);
		TransportMenuPresenter transportMenuPresenter = new TransportMenuPresenter(view, getTransportGripperPresenter(transportInfo), getTransportInterventionPresenter(transportInfo));
		return transportMenuPresenter;
	}
	
	public TransportGripperPresenter getTransportGripperPresenter(TransportInformation transportInfo) {
		TransportGripperView view = new TransportGripperView();
		TransportGripperPresenter transportGripperPresenter = new TransportGripperPresenter(view, transportInfo);
		return transportGripperPresenter;
	}
	
	public TransportInterventionPresenter getTransportInterventionPresenter(TransportInformation transportInfo) {
		TransportInterventionView view = new TransportInterventionView();
		TransportInterventionPresenter transportInterventionPresenter = new TransportInterventionPresenter(view, transportInfo, processFlowAdapter);
		return transportInterventionPresenter;
	}
	
	public void clearBuffer() {
		presentersBuffer.clear();
	}
}
