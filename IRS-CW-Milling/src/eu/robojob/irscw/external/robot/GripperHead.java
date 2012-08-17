package eu.robojob.irscw.external.robot;

public class GripperHead {
	
	private int id;
	private Gripper gripper;
	
	public GripperHead(int id, Gripper gripper) {
		this.id = id;
		this.gripper = gripper;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Gripper getGripper() {
		return gripper;
	}

	public void setGripper(Gripper gripper) {
		this.gripper = gripper;
	}
	
}
