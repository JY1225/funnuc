package eu.robojob.irscw.ui.main.configure.transport;

import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportMenuFactory {

	private RobotManager robotManager;
	private TransportMenuPresenter transportMenuPresenter;
	private TransportGripperPresenter transportGripperPresenter;
	
	public TransportMenuFactory(RobotManager robotManager) {
		this.robotManager = robotManager;
	}
	
	public AbstractMenuPresenter<?> getTransportMenu(TransportInformation transportInfo) {
		return getTransportMenuPresenter(transportInfo);
	}
	
	public TransportMenuPresenter getTransportMenuPresenter(TransportInformation transportInfo) {
		TransportMenuView view = new TransportMenuView(transportInfo);
		transportMenuPresenter = new TransportMenuPresenter(view, getTransportGripperPresenter(transportInfo));
		return transportMenuPresenter;
	}
	
	public TransportGripperPresenter getTransportGripperPresenter(TransportInformation transportInfo) {
		TransportGripperView view = new TransportGripperView();
		transportGripperPresenter = new TransportGripperPresenter(view, transportInfo);
		return transportGripperPresenter;
	}
}
