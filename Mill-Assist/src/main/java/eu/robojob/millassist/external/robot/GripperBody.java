package eu.robojob.irscw.external.robot;

import java.util.HashSet;
import java.util.Set;

public class GripperBody {

	private int id;
	private String name;
	private String description;
	
	private Set<GripperHead> gripperHeads;
	
	public GripperBody(final String name, final String description, final Set<GripperHead> gripperHeads) {
		this.name = name;
		this.description = description;
		this.gripperHeads = new HashSet<GripperHead>();
		for (GripperHead head : gripperHeads) {
			addGripperHead(head);
		}
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
		if (getGripperHeadByName(gripperHead.getName()) != null) {
			throw new IllegalArgumentException("A GripperHead with the same id already exists");
		} else {
			gripperHead.setGripperBody(this);
			gripperHeads.add(gripperHead);
		}
	}
	
	public GripperHead getGripperHeadByName(final String name) {
		for (GripperHead gripperHead : gripperHeads) {
			if (gripperHead.getName().equals(name)) {
				return gripperHead;
			}
		}
		return null;
	}
	
	public GripperHead getGripperHeadById(final int id) {
		for (GripperHead gripperHead : gripperHeads) {
			if (gripperHead.getId() == id) {
				return gripperHead;
			}
		}
		return null;
	}
	
	public Set<GripperHead> getGripperHeads() {
		return gripperHeads;
	}
	
	public void setActiveGripper(final GripperHead head, final Gripper gripper) {
		if (!gripperHeads.contains(head)) {
			throw new IllegalArgumentException("Wrong GripperHead value");
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
