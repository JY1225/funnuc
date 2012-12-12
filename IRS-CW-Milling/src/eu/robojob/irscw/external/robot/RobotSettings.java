package eu.robojob.irscw.external.robot;

import java.util.Map;

public class RobotSettings {
	
	protected GripperBody gripperBody;
	protected Map<GripperHead, Gripper> grippers;
	
	public RobotSettings(GripperBody gripperBody, Map<GripperHead, Gripper> grippers) {
		this.gripperBody = gripperBody;
		this.grippers = grippers;
	}

	public void setGripper(GripperHead head, Gripper gripper) {
		grippers.put(head, gripper);
	}
	
	public Gripper getGripper(GripperHead head) {
		return grippers.get(head);
	}

	public GripperBody getGripperBody() {
		return gripperBody;
	}

	public Map<GripperHead, Gripper> getGrippers() {
		return grippers;
	}
}
