package eu.robojob.millassist.ui.admin.robot;

import java.sql.SQLException;

import eu.robojob.millassist.db.external.robot.RobotMapper;
import eu.robojob.millassist.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.millassist.external.robot.RobotEvent;
import eu.robojob.millassist.external.robot.RobotListener;
import eu.robojob.millassist.external.robot.RobotManager;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class RobotDataPresenter extends AbstractFormPresenter<RobotDataView, RobotMenuPresenter> implements RobotListener {
	
	private FanucRobot robot;

	public RobotDataPresenter(final RobotDataView view, final RobotManager robotManager) {
		super(view);
		robot = (FanucRobot) robotManager.getRobots().iterator().next();
		robot.addListener(this);
		view.refresh();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	boolean isConnected() {
	    return robot.isConnected();
	}

	@Override
	public boolean isConfigured() {
		return false;
	}

	@Override
	public void robotConnected(final RobotEvent event) {
		getView().disableImportButton(false);
	}

	@Override
	public void robotDisconnected(final RobotEvent event) {
		getView().disableImportButton(true);
	}

	@Override
	public void robotStatusChanged(final RobotEvent event) {

	}

	@Override
	public void robotZRestChanged(final RobotEvent event) { }

	@Override
	public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) { }

	@Override
	public void robotSpeedChanged(final RobotEvent event) { }

	@Override
	public void unregister() {
		robot.removeListener(this);
	}

    void updateAcceptDataFlag() {
        try {
            RobotMapper.updateRobotAcceptDataFlag(robot, !robot.acceptsData());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    boolean isAcceptingData() {
        return robot.acceptsData();
    }
}
