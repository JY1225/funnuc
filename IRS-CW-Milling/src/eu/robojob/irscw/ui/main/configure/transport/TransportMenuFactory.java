package eu.robojob.irscw.ui.main.configure.transport;

import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportMenuFactory {

	private RobotManager robotManager;
	private TransportMenuPresenter transportMenuPresenter;
	private TransportGripperPresenter transportGripperPresenter;
	private ProcessFlowAdapter processFlowAdapter;
	private TransportInterventionPresenter transportInterventionPresenter;
	
	public TransportMenuFactory(RobotManager robotManager, ProcessFlow processFlow) {
		this.robotManager = robotManager;
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
	}
	
	public AbstractMenuPresenter<?> getTransportMenu(TransportInformation transportInfo) {
		return getTransportMenuPresenter(transportInfo);
	}
	
	public TransportMenuPresenter getTransportMenuPresenter(TransportInformation transportInfo) {
		TransportMenuView view = new TransportMenuView(transportInfo);
		transportMenuPresenter = new TransportMenuPresenter(view, getTransportGripperPresenter(transportInfo), getTransportInterventionPresenter(transportInfo));
		return transportMenuPresenter;
	}
	
	public TransportGripperPresenter getTransportGripperPresenter(TransportInformation transportInfo) {
		TransportGripperView view = new TransportGripperView();
		transportGripperPresenter = new TransportGripperPresenter(view, transportInfo);
		return transportGripperPresenter;
	}
	
	public TransportInterventionPresenter getTransportInterventionPresenter(TransportInformation transportInfo) {
		TransportInterventionView view = new TransportInterventionView();
		transportInterventionPresenter = new TransportInterventionPresenter(view, transportInfo, processFlowAdapter);
		return transportInterventionPresenter;
	}
}
