package eu.robojob.millassist.ui.configure.transport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.robot.Gripper;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.RobotSettings;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.TransportInformation;

public class TransportGripperPresenter extends AbstractFormPresenter<TransportGripperView, TransportMenuPresenter> {

	private static Logger logger = LogManager.getLogger(TransportGripperPresenter.class.getName());
	
	private TransportInformation transportInfo;
	private RobotSettings robotSettings;
	
	public TransportGripperPresenter(final TransportGripperView view, final TransportInformation transportInfo) {
		super(view);
		this.transportInfo = transportInfo;
		this.robotSettings = transportInfo.getRobotSettings();
		view.setTransportInfo(transportInfo);
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	public void changedGripperHead(final String gripperHeadName) {
		if ((transportInfo.getPickStep().getRobotSettings().getGripperHead() == null) || (!gripperHeadName.equals(transportInfo.getPickStep().getRobotSettings().getGripperHead().getName()))) {
			logger.debug("Changed gripper head: " + gripperHeadName);
			transportInfo.getPickStep().getRobotSettings().setGripperHead(transportInfo.getRobot().getGripperBody().getGripperHeadByName(gripperHeadName));
			transportInfo.getPutStep().getRobotSettings().setGripperHead(transportInfo.getRobot().getGripperBody().getGripperHeadByName(gripperHeadName));
			transportInfo.getRobot().loadRobotSettings(robotSettings);
			transportInfo.getPickStep().setRelativeTeachedOffset(null);
			transportInfo.getPutStep().setRelativeTeachedOffset(null);
			transportInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(transportInfo.getPickStep().getProcessFlow(), transportInfo.getPickStep(), true));
			transportInfo.getPutStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(transportInfo.getPutStep().getProcessFlow(), transportInfo.getPutStep(), true));
			getView().refreshGrippers();
		}
	}
	
	public void changedGripper(final Gripper gripper) {
		//FIXME REVIEW
		boolean found = false;
		for (GripperHead head : transportInfo.getRobot().getGripperBody().getGripperHeads()) {
			if ((head.getGripper() != null) && (head.getGripper().equals(gripper)) && (!head.equals(transportInfo.getPickStep().getRobotSettings().getGripperHead()))) {
				found = true;
			}
		}
		if (!found) {
			if ((transportInfo.getPickStep().getRobotSettings().getGripperHead().getGripper() != null) && transportInfo.getPickStep().getRobotSettings().getGripperHead().getGripper().equals(gripper)) {
				robotSettings.setGripper(transportInfo.getPickStep().getRobotSettings().getGripperHead(), null);
			} else {
				robotSettings.setGripper(transportInfo.getPickStep().getRobotSettings().getGripperHead(), gripper);
			}
			transportInfo.getRobot().loadRobotSettings(robotSettings);
			transportInfo.getPickStep().setRelativeTeachedOffset(null);
			transportInfo.getPutStep().setRelativeTeachedOffset(null);
			transportInfo.getPickStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(transportInfo.getPickStep().getProcessFlow(), transportInfo.getPickStep(), true));
			transportInfo.getPutStep().getProcessFlow().processProcessFlowEvent(new DataChangedEvent(transportInfo.getPutStep().getProcessFlow(), transportInfo.getPutStep(), true));
			getView().setSelectedGripper();
		} else {
			// TODO handle this error (warning dialog...)
			logger.debug("Duplicate gripper usage!");
		}
	}

	@Override
	public boolean isConfigured() {
		if (robotSettings.getGripper(transportInfo.getPickStep().getRobotSettings().getGripperHead()) != null) {
			return true;
		}
		return false;
	}
}
