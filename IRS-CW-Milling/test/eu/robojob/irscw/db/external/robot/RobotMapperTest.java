package eu.robojob.irscw.db.external.robot;

import java.sql.SQLException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.irscw.external.robot.AbstractRobot;

public class RobotMapperTest {

	private RobotMapper robotMapper;
	
	@Before
	public void setup() {
		robotMapper = RobotMapper.getInstance();
	}
	
	@Test
	public void testGetAllDevices() {
		try {
			Set<AbstractRobot> robots = robotMapper.getAllRobots();
			for (AbstractRobot robot : robots) {
				System.out.println(robot.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}
