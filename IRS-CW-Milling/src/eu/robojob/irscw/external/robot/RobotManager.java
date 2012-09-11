package eu.robojob.irscw.external.robot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
		Gripper gripper = new Gripper("vacuum grip", 200, "vacuum grip, type 1");
		Gripper gripper2 = new Gripper("clamp grip", 250, "clamp grip, type 2");
		GripperHead head1 = new GripperHead(1, gripper);
		GripperHead head2 = new GripperHead(2, gripper2);
		List<GripperHead> gripperHeads = new ArrayList<GripperHead>();
		gripperHeads.add(head1);
		gripperHeads.add(head2);
		GripperBody gripperBody = new GripperBody(2, "body 1", gripperHeads);
		FanucRobot fanucRobot = new FanucRobot("fanuc M110", gripperBody, null);
		addRobot(fanucRobot);
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
