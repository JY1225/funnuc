package eu.robojob.irscw.external.robot;

public class GripperHead {
	
	private String id;
	private Gripper gripper;
	
	public GripperHead(String id, Gripper gripper) {
		this.id = id;
		setGripper(gripper);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Gripper getGripper() {
		return gripper;
	}

	public void setGripper(Gripper gripper) {
		this.gripper = gripper;
	}
	
}
