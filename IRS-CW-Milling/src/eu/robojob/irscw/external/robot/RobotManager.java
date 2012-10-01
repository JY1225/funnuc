package eu.robojob.irscw.external.robot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RobotManager {
	
	private Map<String, AbstractRobot> robots;
	
	public RobotManager() {
		robots = new HashMap<String, AbstractRobot>();
		initialize();
	}
	
	private void initialize() {
		Gripper gripper = new Gripper("vacuum grip", 200, "vacuum grip, type 1", "img/grippers/vacuum.png");
		Gripper gripper2 = new Gripper("clamp grip", 250, "clamp grip, type 2", "img/grippers/clamp.png");
		GripperHead head1 = new GripperHead("A", gripper);
		GripperHead head2 = new GripperHead("B", gripper2);
		List<GripperHead> gripperHeads = new ArrayList<GripperHead>();
		gripperHeads.add(head1);
		gripperHeads.add(head2);
		Set<Gripper> grippers = new HashSet<Gripper>();
		grippers.add(gripper);
		grippers.add(gripper2);
		GripperBody gripperBody = new GripperBody(2, "body 1", gripperHeads, grippers);
		Set<GripperBody> gripperBodies = new HashSet<GripperBody>();
		gripperBodies.add(gripperBody);
		FanucRobot fanucRobot = new FanucRobot("fanuc M110", gripperBodies, gripperBody, null);
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
