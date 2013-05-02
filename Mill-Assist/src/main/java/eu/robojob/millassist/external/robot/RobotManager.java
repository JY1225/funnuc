package eu.robojob.millassist.external.robot;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.external.robot.RobotMapper;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlowManager;

public class RobotManager {
	
	private Map<String, AbstractRobot> robotsByName;
	private Map<Integer, AbstractRobot> robotsById;
	private RobotMapper robotMapper;
	private ProcessFlowManager processFlowManager;
	
	private static Logger logger = LogManager.getLogger(RobotManager.class.getName());
	
	public RobotManager(final RobotMapper robotMapper) {
		this.robotMapper = robotMapper;
		robotsByName = new HashMap<String, AbstractRobot>();
		robotsById = new HashMap<Integer, AbstractRobot>();
		initialize();
	}
	
	public void setProcessFlowManager(final ProcessFlowManager processFlowManager) {
		this.processFlowManager = processFlowManager;
	}
	
	private void initialize() {
		Set<AbstractRobot> allRobots;
		try {
			robotsById.clear();
			robotsByName.clear();
			allRobots = robotMapper.getAllRobots();
			for (AbstractRobot robot : allRobots) {
				robotsById.put(robot.getId(), robot);
				robotsByName.put(robot.getName(), robot);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	public Set<String> getRobotNames() {
		return robotsByName.keySet();
	}

	public Collection<AbstractRobot> getRobots() {
		return robotsByName.values();
	}
	
	public AbstractRobot getRobotByName(final String robotName) {
		return robotsByName.get(robotName);
	}
	
	public AbstractRobot getRobotById(final int id) {
		return robotsById.get(id);
	}
	
	public void updateRobotData(final FanucRobot robot, final String name, final String ip, final int port, 
			final boolean hasGripperHeadA, final boolean hasGripperHeadB, final boolean hasGripperHeadC, final boolean hasGripperHeadD) {
		try {
			logger.info("About to update robot [" + robot.toString() + "] with following data: name [" + name + "], ip [" + ip + 
					"], port [" + port + "], gripperHead A [" + hasGripperHeadA + "], gripperHead B [" + hasGripperHeadB + "], gripperHead C [" +
						hasGripperHeadC + "], gripperHeadD [" + hasGripperHeadD + "].");
			robotMapper.updateRobotData(robot, name, ip, port, hasGripperHeadA, hasGripperHeadB, hasGripperHeadC, hasGripperHeadD);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	public void updateGripper(final Gripper gripper, final String name, final String imgUrl, final float height, final boolean fixedHeight, 
			final boolean headA, final boolean headB, final boolean headC, final boolean headD) {
		try {
			robotMapper.updateGripperData(gripper, name, imgUrl, height, fixedHeight, headA, headB, headC, headD);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	public void deleteGripper(final Gripper gripper) {
		// delete all processes using this gripper!
		for (ProcessFlow processFlow : processFlowManager.getProcessFlows()) {
			for (RobotSettings robotSettings : processFlow.getRobotSettings().values()) {
				if (robotSettings.getGrippers().values().contains(gripper)) {
					processFlowManager.deleteProcessFlow(processFlow);
				}
			}
		}
		for (RobotSettings robotSettings : processFlowManager.getActiveProcessFlow().getRobotSettings().values()) {
			for (Entry<GripperHead, Gripper> entry : robotSettings.getGrippers().entrySet()) {
				if (entry.getValue().equals(gripper)) {
					entry.setValue(null);
				}
			}
		}
		for (AbstractRobot robot : getRobots()) {
			for (GripperBody body : robot.getPossibleGripperBodies()) {
				for (GripperHead head : body.getGripperHeads()) {
					head.getPossibleGrippers().remove(gripper);
				}
			}
		}
		try {
			robotMapper.deleteGripper(gripper);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void addGripper(final String name, final String imgUrl, final float height, final boolean fixedHeight, final boolean headA, 
			final boolean headB, final boolean headC, final boolean headD) {
		Gripper newGripper = new Gripper(name, height, null, imgUrl);
		try {
			robotMapper.saveGripper(newGripper, getRobots().iterator().next().getGripperBody().getGripperHeadByName("A"),
					getRobots().iterator().next().getGripperBody().getGripperHeadByName("B"));
			getRobots().iterator().next().getGripperBody().getGripperHeadByName("A").addPossibleGripper(newGripper);
			getRobots().iterator().next().getGripperBody().getGripperHeadByName("B").addPossibleGripper(newGripper);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
}
