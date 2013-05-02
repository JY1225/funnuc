package eu.robojob.millassist.ui.robot;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.millassist.external.robot.RobotEvent;
import eu.robojob.millassist.external.robot.RobotListener;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.general.AbstractPopUpPresenter;

public class RobotPopUpPresenter extends AbstractPopUpPresenter<RobotPopUpView> implements RobotListener, ProcessFlowListener {

	private FanucRobot robot;
	private boolean connected;
	
	public RobotPopUpPresenter(final RobotPopUpView view, final FanucRobot robot, final ProcessFlow processFlow) {
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
		getView().setPresenter(this);
	}
	
	public void resetClicked() {
		if (robot.isConnected()) {
			try {
				robot.reset();
			} catch (AbstractCommunicationException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void restartClicked() {
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
	
	public void setSpeedClicked(final int speed) {
		if (robot.isConnected()) {
			try {
				robot.setSpeed(speed);
			} catch (AbstractCommunicationException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		getView().refreshSpeed(robot.getSpeed());
	}

	@Override
	public void robotConnected(final RobotEvent event) {
		getView().setRobotConnected(true);
		connected = true;
	}

	@Override
	public void robotDisconnected(final RobotEvent event) {
		getView().setRobotConnected(false);
		connected = false;
	}

	@Override
	public void robotStatusChanged(final RobotEvent event) {
		getView().refreshSpeed(event.getSource().getSpeed());
	}

	@Override
	public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) {
	}

	@Override
	// this pop-up should be disabled when running in AUTO or TEACH mode
	public void modeChanged(final ModeChangedEvent e) {
		switch (e.getMode()) {
			case AUTO:
				if (connected) {
					getView().setProcessActive(true);
				}
				break;
			case TEACH: 
				if (connected) {
					getView().setProcessActive(true);
				}
				break;
			default:
				if (connected) {
					getView().setProcessActive(false);
				}
				break;
		}
	}
	
	@Override
	public void robotSpeedChanged(final RobotEvent event) {
		getView().refreshSpeed(event.getSource().getSpeed());
	}
	
	@Override public void statusChanged(final StatusChangedEvent e) { }
	@Override public void dataChanged(final DataChangedEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void robotZRestChanged(final RobotEvent event) { }
	@Override
	public void exceptionOccured(final ExceptionOccuredEvent e) { }
	
}
