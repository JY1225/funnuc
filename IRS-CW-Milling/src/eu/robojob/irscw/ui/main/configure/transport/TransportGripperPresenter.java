package eu.robojob.irscw.ui.main.configure.transport;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportGripperPresenter extends AbstractFormPresenter<TransportGripperView, TransportMenuPresenter> {

	private static Logger logger = Logger.getLogger(TransportGripperPresenter.class);
	
	private TransportInformation transportInfo;
	
	public TransportGripperPresenter(TransportGripperView view, TransportInformation transportInfo) {
		super(view);
		this.transportInfo = transportInfo;
		view.setTransportInfo(transportInfo);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	public void changedGripperHead(String id) {
		// for now this wil be impossible, since a fixed convention will be used! 
		logger.debug("changed gripper head to: " + id);
	}
	
	public void changedGripper(String id) {
		// TODO: make sure that a gripper is used with one head, and automatic gripper changed aren't possible!
		logger.debug("changed gripper to: " + id);
		for (Gripper gripper : transportInfo.getRobot().getGripperBody().getPossibleGrippers()) {
			if (gripper.getId().equals(id)) {
				transportInfo.getPickStep().getRobotSettings().setGripper(gripper);
				transportInfo.getPutStep().getRobotSettings().setGripper(gripper);
				view.setSelectedGripper();
			}
		}
	}

	@Override
	public boolean isConfigured() {
		if ((transportInfo.getPickStep().getRobotSettings().getGripper() != null) && (transportInfo.getPutStep().getRobotSettings().getGripper() != null)) {
			return true;
		} else {
			return false;
		}
	}
}
