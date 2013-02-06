package eu.robojob.irscw.external.robot;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.db.external.robot.RobotMapper;

public class RobotManager {
	
	private Map<String, AbstractRobot> robotsByName;
	private Map<Integer, AbstractRobot> robotsById;
	private RobotMapper robotMapper;
	
	private static Logger logger = LogManager.getLogger(RobotManager.class.getName());
	
	public RobotManager(final RobotMapper robotMapper) {
		this.robotMapper = robotMapper;
		robotsByName = new HashMap<String, AbstractRobot>();
		robotsById = new HashMap<Integer, AbstractRobot>();
		initialize();
	}
	
	private void initialize() {
		Set<AbstractRobot> allRobots;
		try {
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
}
