package eu.robojob.irscw.ui.admin.robot;

import javafx.application.Platform;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.RobotEvent;
import eu.robojob.irscw.external.robot.RobotListener;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;

public class RobotConfigurePresenter extends AbstractFormPresenter<RobotConfigureView, RobotMenuPresenter> implements RobotListener {
	
	public RobotConfigurePresenter(final RobotConfigureView view, final RobotManager robotManager) {
		super(view);
		AbstractRobot robot = robotManager.getRobots().iterator().next();
		robot.addListener(this);
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

	@Override
	public void robotZRestChanged(final RobotEvent event) { }

	@Override
	public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) { }

	@Override
	public void robotSpeedChanged(final RobotEvent event) { }

}
