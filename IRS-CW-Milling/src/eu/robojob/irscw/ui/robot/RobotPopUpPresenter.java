package eu.robojob.irscw.ui.robot;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.ui.AbstractPopUpPresenter;

public class RobotPopUpPresenter extends AbstractPopUpPresenter<RobotPopUpView> {

	private AbstractRobot robot;
	
	public RobotPopUpPresenter(RobotPopUpView view, AbstractRobot robot) {
		super(view);
		this.robot = robot;
		if (robot.isConnected()) {
			view.refreshSpeed(robot.getSpeed());
		} else {
			view.refreshSpeed(0);
		}
		view.setRobotConnected(robot.isConnected());
	}
	
	@Override
	protected void setViewPresenter() {
		view.setPresenter(this);
	}
	
	public void resetClicked() {
		if (robot.isConnected()) {
			try {
				robot.restartProgram();
			} catch (CommunicationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toHomeClicked() {
		if (robot.isConnected()) {
			try {
				robot.moveToHome();
			} catch (CommunicationException | RobotActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toChangePointClicked() {
		if (robot.isConnected()) {
			try {
				robot.moveToChangePoint();
			} catch (CommunicationException | RobotActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setSpeedClicked(int speed) {
		if (robot.isConnected()) {
			try {
				robot.setSpeed(speed);
			} catch (CommunicationException e) {
				e.printStackTrace();
			}
		}
		System.out.println("robot speed: " + robot.getSpeed());
		view.refreshSpeed(robot.getSpeed());
	}
	
}
