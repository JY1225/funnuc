package eu.robojob.irscw.external.robot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.communication.SocketConnection.Type;

public class RobotManager {
	
	private Map<String, AbstractRobot> robots;
	
	public RobotManager() {
		robots = new HashMap<String, AbstractRobot>();
		initialize();
	}
	
	private void initialize() {
		Gripper gripper = new Gripper("Vacuum grip", 200, "Vacuum grip, type 1", "img/grippers/gripper1.png");
		Gripper gripper2 = new Gripper("2P clamp grip", 250, "Clamp grip, two points", "img/grippers/gripper2.png");
		/*Gripper gripper3 = new Gripper("vacuum grip 2", 200, "vacuum grip, type 1", "img/grippers/gripper1.png");
		Gripper gripper4 = new Gripper("clamp grip 2", 250, "clamp grip, type 2", "img/grippers/gripper2.png");
		Gripper gripper5 = new Gripper("vacuum grip 3", 200, "vacuum grip, type 1", "img/grippers/gripper1.png");
		Gripper gripper6 = new Gripper("clamp grip 3", 250, "clamp grip, type 2", "img/grippers/gripper2.png");*/
		GripperHead head1 = new GripperHead("A", gripper);
		GripperHead head2 = new GripperHead("B", gripper2);
		List<GripperHead> gripperHeads = new ArrayList<GripperHead>();
		gripperHeads.add(head1);
		gripperHeads.add(head2);
		Set<Gripper> grippers = new HashSet<Gripper>();
		grippers.add(gripper);
		grippers.add(gripper2);
		/*grippers.add(gripper3);
		grippers.add(gripper4);
		grippers.add(gripper5);
		grippers.add(gripper6);*/
		GripperBody gripperBody = new GripperBody(2, "Standard Body", gripperHeads, grippers);
		Set<GripperBody> gripperBodies = new HashSet<GripperBody>();
		gripperBodies.add(gripperBody);
		//SocketConnection connection = new SocketConnection(Type.CLIENT, "Fanuc M20iA", "192.168.200.9", 2001);
		SocketConnection connection = new SocketConnection(Type.CLIENT, "Fanuc M20iA", "10.10.40.12", 2001);
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
