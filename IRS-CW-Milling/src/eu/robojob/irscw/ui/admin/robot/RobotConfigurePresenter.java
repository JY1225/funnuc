package eu.robojob.irscw.ui.admin.robot;

import javafx.application.Platform;
import eu.robojob.irscw.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.RobotEvent;
import eu.robojob.irscw.external.robot.RobotListener;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class RobotConfigurePresenter extends AbstractFormPresenter<RobotConfigureView, RobotMenuPresenter> implements RobotListener {
	
	private FanucRobot robot;
	private RobotManager robotManager;
	
	public RobotConfigurePresenter(final RobotConfigureView view, final RobotManager robotManager) {
		super(view);
		robot = (FanucRobot) robotManager.getRobots().iterator().next();
		robot.addListener(this);
		this.robotManager = robotManager;
		view.setRobot(robot);
		view.refresh();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	@Override
	public void robotConnected(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refresh();
			}
		});
	}

	@Override
	public void robotDisconnected(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refresh();
			}
		});
	}

	@Override
	public void robotStatusChanged(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getView().refresh();
			}
		});
	}
	
	public void saveData(final String name, final String ip, final int port, final boolean gripperHeadA, final boolean gripperHeadB, final boolean gripperHeadC,
			final boolean gripperHeadD) {
		robotManager.updateRobotData(robot, name, ip, port, gripperHeadA, gripperHeadB, gripperHeadC, gripperHeadD);
		getView().refresh();
	}

	@Override
	public void robotZRestChanged(final RobotEvent event) { }

	@Override
	public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) { }

	@Override
	public void robotSpeedChanged(final RobotEvent event) { }

}
