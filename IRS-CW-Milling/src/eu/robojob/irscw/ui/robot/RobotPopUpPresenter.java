package eu.robojob.irscw.ui.robot;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotEvent;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotListener;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotStatusChangedEvent;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.ui.AbstractPopUpPresenter;

public class RobotPopUpPresenter extends AbstractPopUpPresenter<RobotPopUpView> implements FanucRobotListener, ProcessFlowListener {

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
			} catch (AbstractCommunicationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toHomeClicked() {
		if (robot.isConnected()) {
			try {
				robot.moveToHome();
			} catch (AbstractCommunicationException | RobotActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toChangePointClicked() {
		if (robot.isConnected()) {
			try {
				robot.moveToChangePoint();
			} catch (AbstractCommunicationException | RobotActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setSpeedClicked(int speed) {
		if (robot.isConnected()) {
			try {
				robot.setSpeed(speed);
			} catch (AbstractCommunicationException e) {
				e.printStackTrace();
			}
		}
		view.refreshSpeed(robot.getSpeed());
	}

	@Override
	public void robotConnected(FanucRobotEvent event) {
		view.setRobotConnected(true);
		connected = true;
	}

	@Override
	public void robotDisconnected(FanucRobotEvent event) {
		view.setRobotConnected(false);
		connected = false;
	}

	@Override
	public void robotStatusChanged(FanucRobotStatusChangedEvent event) {
		view.refreshSpeed(event.getStatus().getSpeed());
	}

	@Override
	public void robotAlarmsOccured(FanucRobotAlarmsOccuredEvent event) {
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

	@Override public void activeStepChanged(ActiveStepChangedEvent e) {}
	@Override public void exceptionOccured(ExceptionOccuredEvent e) {}
	@Override public void dataChanged(ProcessFlowEvent e) {}
	@Override public void finishedAmountChanged(FinishedAmountChangedEvent e) {}
	
}
