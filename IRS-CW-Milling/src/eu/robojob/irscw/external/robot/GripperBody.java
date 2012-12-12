package eu.robojob.irscw.external.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GripperBody {

	private String id;
	private String description;
	
	private List<GripperHead> gripperHeads;
	private Set<Gripper> possibleGrippers;
	
	public GripperBody(final String id, final String description, final List<GripperHead> gripperHeads, final Set<Gripper> possibleGrippers) {
		this.id = id;
		this.description = description;
		this.gripperHeads = gripperHeads;
		if (possibleGrippers != null) {
			this.possibleGrippers = possibleGrippers;
		} else {
			this.possibleGrippers = new HashSet<Gripper>();
		}
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}
	
	public int getAmountOfGripperHeads() {
		return gripperHeads.size();
	}
	
	public void addGripperHead(final GripperHead gripperHead) {
		if (getGripperHead(gripperHead.getId()) != null) {
			throw new IllegalArgumentException("A GripperHead with the same id already exists");
		} else {
			gripperHeads.add(gripperHead);
		}
	}
	
	public GripperHead getGripperHead(final String id) {
		for (GripperHead gripperHead : gripperHeads) {
			if (gripperHead.getId().equals(id)) {
				return gripperHead;
			}
		}
		return null;
	}
	
	public List<GripperHead> getGripperHeads() {
		return gripperHeads;
	}

	public Set<Gripper> getPossibleGrippers() {
		return possibleGrippers;
	}

	public Gripper getGripper(final String id) {
		for (Gripper gripper : possibleGrippers) {
			if (gripper.getId().equals(id)) {
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
	
	public void setActiveGripper(final GripperHead head, final Gripper gripper) {
		if (!gripperHeads.contains(head)) {
			throw new IllegalArgumentException("Wrong GripperHead value");
		}
		if (!possibleGrippers.contains(gripper)) {
			throw new IllegalArgumentException("Wrong gripper value");
		}
		for (GripperHead head2 : gripperHeads) {
			if ((head2.getGripper() == gripper) && (head2 == head)) {
				throw new IllegalArgumentException("The provided gripper was already activated on another head");
			}
		}
		head.setGripper(gripper);
	}
	
	public Gripper getActiveGripper(final GripperHead head) {
		if (!gripperHeads.contains(head)) {
			throw new IllegalArgumentException("Wrong GripperHead value");
		}
		return head.getGripper();
	}
}
