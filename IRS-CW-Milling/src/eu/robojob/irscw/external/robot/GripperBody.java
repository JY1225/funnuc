package eu.robojob.irscw.external.robot;

import java.util.List;

public class GripperBody {

	int id;
	String description;
	
	private List<GripperHead> gripperHeads;
	
	public GripperBody (int id, String description, List<GripperHead> gripperHeads) {
		this.id = id;
		this.description = description;
		this.gripperHeads = gripperHeads;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getAmountOfGripperHeads() {
		return gripperHeads.size();
	}
	
	public void addGripperHead(GripperHead gripperHead) {
		if (getGripperHead(gripperHead.getId()) != null) {
			throw new IllegalArgumentException("A GripperHead with the same id already exists");
		} else {
			gripperHeads.add(gripperHead);
		}
	}
	
	public GripperHead getGripperHead(int id) {
		for (GripperHead gripperHead : gripperHeads) {
			if (gripperHead.getId() == id) {
				return gripperHead;
			}
		}
		return null;
	}
	
}
