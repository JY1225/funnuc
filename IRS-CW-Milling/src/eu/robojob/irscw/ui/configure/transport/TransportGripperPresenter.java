package eu.robojob.irscw.ui.configure.transport;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotSettings;
import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportGripperPresenter extends AbstractFormPresenter<TransportGripperView, TransportMenuPresenter> {

	private static Logger logger = Logger.getLogger(TransportGripperPresenter.class);
	
	private TransportInformation transportInfo;
	private FanucRobotSettings robotSettings;
	
	public TransportGripperPresenter(TransportGripperView view, TransportInformation transportInfo) {
		super(view);
		this.transportInfo = transportInfo;
		this.robotSettings = (FanucRobotSettings) transportInfo.getRobotSettings();
		view.setTransportInfo(transportInfo);
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	public void changedGripperHead(String id) {
		// for now this wil be impossible, since a fixed convention will be used! 
		logger.debug("Gripper head not changed for now...");
	}
	
	public void changedGripper(String id) {
		// TODO: make sure that a gripper is used with one head, and automatic gripper changed aren't possible!
		logger.debug("Changed gripper to: " + id);
		Gripper gripper = transportInfo.getRobot().getGripperBody().getGripper(id);
		boolean found = false;
		for (GripperHead head : transportInfo.getRobot().getGripperBody().getGripperHeads()) {
			if ((head.getGripper() != null) && (head.getGripper().equals(gripper)) && (!head.equals(transportInfo.getPickStep().getRobotSettings().getGripperHead()))){
				found = true;
			}
		}
		if (!found) {
			if ((transportInfo.getPickStep().getRobotSettings().getGripper() != null) && transportInfo.getPickStep().getRobotSettings().getGripper().equals(gripper)) {
				// deselect gripper
				transportInfo.getPickStep().getRobotSettings().setGripper(null);
				transportInfo.getPutStep().getRobotSettings().setGripper(null);
				robotSettings.setGripper(transportInfo.getPickStep().getRobotSettings().getGripperHead(), null);
			} else {
				transportInfo.getPickStep().getRobotSettings().setGripper(gripper);
				transportInfo.getPutStep().getRobotSettings().setGripper(gripper);
				robotSettings.setGripper(transportInfo.getPickStep().getRobotSettings().getGripperHead(), gripper);
			}
			transportInfo.getRobot().loadRobotSettings(robotSettings);
			view.setSelectedGripper();
		} else {
			logger.debug("duplicate gripper usage!");
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