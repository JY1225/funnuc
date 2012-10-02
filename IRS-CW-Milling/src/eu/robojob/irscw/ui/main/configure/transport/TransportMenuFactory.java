package eu.robojob.irscw.ui.main.configure.transport;

import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportMenuFactory {

	private RobotManager robotManager;
	private TransportMenuPresenter transportMenuPresenter;
	private TransportGripperPresenter transportGripperPresenter;
	private TransportInterventionPresenter transportInterventionPresenter;
	
	public TransportMenuFactory(RobotManager robotManager) {
		this.robotManager = robotManager;
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
		transportInterventionPresenter = new TransportInterventionPresenter(view, transportInfo);
		return transportInterventionPresenter;
	}
}
