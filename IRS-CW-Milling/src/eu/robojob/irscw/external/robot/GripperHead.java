package eu.robojob.irscw.external.robot;

import java.util.HashSet;
import java.util.Set;

public class GripperHead {
	
	private int id;
	private String name;
	private Gripper gripper;
	
	private Set<Gripper> possibleGrippers;

	public GripperHead(final String name, final Set<Gripper> possibleGrippers, final Gripper gripper) {
		this.name = name;
		if (possibleGrippers != null) {
			this.possibleGrippers = possibleGrippers;
		} else {
			this.possibleGrippers = new HashSet<Gripper>();
		}
		setGripper(gripper);
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Gripper getGripper() {
		return gripper;
	}

	public void setGripper(final Gripper gripper) {
		this.gripper = gripper;
	}
	
	public Set<Gripper> getPossibleGrippers() {
		return possibleGrippers;
	}

	public Gripper getGripper(final String id) {
		for (Gripper gripper : possibleGrippers) {
			if (gripper.getName().equals(id)) {
				return gripper;
			}
		}
		return null;
	}
	
	public void setPossibleGrippers(final Set<Gripper> possibleGrippers) {
		this.possibleGrippers = possibleGrippers;
	}
	
	public void addPossibleGripper(final Gripper gripper) {
		possibleGrippers.add(gripper);
	}
	
}
