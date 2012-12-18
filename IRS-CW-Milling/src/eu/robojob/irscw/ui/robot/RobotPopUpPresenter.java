package eu.robojob.irscw.ui.robot;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.RobotEvent;
import eu.robojob.irscw.external.robot.RobotListener;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.ui.AbstractPopUpPresenter;

public class RobotPopUpPresenter extends AbstractPopUpPresenter<RobotPopUpView> implements RobotListener, ProcessFlowListener {

	private FanucRobot robot;
	private boolean connected;
	
	public RobotPopUpPresenter(RobotPopUpView view, FanucRobot robot, ProcessFlow processFlow) {
		super(view);
		this.robot = robot;
		connected = false;
		processFlow.addListener(this);
		robot.addListener(this);
		if (robot.isConnected()) {
			connected = true;
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
			} catch (AbstractCommunicationException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toHomeClicked() {
		if (robot.isConnected()) {
			try {
				robot.moveToHome();
			} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toChangePointClicked() {
		if (robot.isConnected()) {
			try {
				robot.moveToChangePoint();
			} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setSpeedClicked(int speed) {
		if (robot.isConnected()) {
			try {
				robot.setSpeed(speed);
			} catch (AbstractCommunicationException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		view.refreshSpeed(robot.getSpeed());
	}

	@Override
	public void robotConnected(RobotEvent event) {
		view.setRobotConnected(true);
		connected = true;
	}

	@Override
	public void robotDisconnected(RobotEvent event) {
		view.setRobotConnected(false);
		connected = false;
	}

	@Override
	public void robotStatusChanged(RobotEvent event) {
		view.refreshSpeed(event.getSource().getSpeed());
	}

	@Override
	public void robotAlarmsOccured(RobotAlarmsOccuredEvent event) {
	}

	@Override
	public void modeChanged(ModeChangedEvent e) {
		switch (e.getMode()) {
			case AUTO:
				if (connected) {
					view.setProcessActive(true);
				}
				break;
			case TEACH: 
				if (connected) {
					view.setProcessActive(true);
				}
				break;
			default:
				if (connected) {
					view.setProcessActive(false);
				}
				break;
		}
	}

	@Override public void statusChanged(StatusChangedEvent e) {}
	@Override public void dataChanged(ProcessFlowEvent e) {}
	@Override public void finishedAmountChanged(FinishedAmountChangedEvent e) {}

	@Override
	public void robotZRestChanged(RobotEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void robotSpeedChanged(RobotEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
