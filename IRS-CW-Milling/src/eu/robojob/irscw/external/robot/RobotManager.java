package eu.robojob.irscw.external.robot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.communication.SocketConnection.Type;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;

public class RobotManager {
	
	private Map<String, AbstractRobot> robots;
	private Properties properties;
	
	private static final String ROBOT_IP = "robot-ip";
	private static final String ROBOT_PORT = "robot-port";
	
	public RobotManager(Properties properties) {
		robots = new HashMap<String, AbstractRobot>();
		this.properties = properties;
		initialize();
	}
	
	private void initialize() {
		Gripper gripper = new Gripper("Vacuum grip", 270, "Vacuum grip, type 1", "img/grippers/gripper1.png");
		Gripper gripper2 = new Gripper("2P clamp grip A", 133, "Clamp grip, two points", "img/grippers/gripper2.png");
		Gripper gripper3 = new Gripper("2P clamp grip B", 133, "Clamp grip, two points", "img/grippers/gripper2.png");
		GripperHead head1 = new GripperHead("A", gripper);
		GripperHead head2 = new GripperHead("B", gripper2);
		List<GripperHead> gripperHeads = new ArrayList<GripperHead>();
		gripperHeads.add(head1);
		gripperHeads.add(head2);
		Set<Gripper> grippers = new HashSet<Gripper>();
		grippers.add(gripper);
		grippers.add(gripper2);
		grippers.add(gripper3);
		GripperBody gripperBody = new GripperBody("2", "Standard Body", gripperHeads, grippers);
		Set<GripperBody> gripperBodies = new HashSet<GripperBody>();
		gripperBodies.add(gripperBody);
		SocketConnection connection = new SocketConnection(Type.CLIENT, "Fanuc M20iA", properties.getProperty(ROBOT_IP), Integer.parseInt(properties.getProperty(ROBOT_PORT)));
		FanucRobot fanucRobot = new FanucRobot("Fanuc M20iA", gripperBodies, gripperBody, connection);
		addRobot(fanucRobot);
	}
	
	public List<GripperHead> getGripperHeads(String robotId) {
		return robots.get(robotId).getGripperBody().getGripperHeads();
	}
	
	public Set<Gripper> getGrippers(GripperBody gripperBody) {
		return gripperBody.getPossibleGrippers();
	}
	
	public Set<GripperBody> getGripperBodies(AbstractRobot robot) {
		return robot.getPossibleGripperBodies();
	}
	
	public Gripper getGripper(String id) {
		for (AbstractRobot robot : robots.values()) {
			for (GripperBody body : robot.getPossibleGripperBodies()) {
				for (Gripper gripper : body.getPossibleGrippers()) {
					if (gripper.getId().equals(id)) {
						return gripper;
					}
				}
			}
		}
		return null;
	}
	
	public void addRobot(AbstractRobot robot) {
		robots.put(robot.getId(), robot);
	}
	
	public Set<String> getRobotIds() {
		return robots.keySet();
	}

	public Collection<AbstractRobot> getRobots() {
		return robots.values();
	}
	
	public AbstractRobot getRobotById(String robotId) {
		return robots.get(robotId);
	}
}
