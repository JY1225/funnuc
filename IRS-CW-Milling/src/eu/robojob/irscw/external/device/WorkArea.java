package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.positioning.UserFrame;

public class WorkArea {
	
	private String id;
	private Zone zone;
	private UserFrame userFrame;
	private Clamping activeClamping;
	private List<Clamping> clampings;
	
	public WorkArea (String id, UserFrame userFrame, Clamping activeClamping, List<Clamping> clampings) {
		this.id = id;
		this.userFrame = userFrame;
		this.activeClamping = activeClamping;
		this.clampings = clampings;
	}
	
	public WorkArea (String id, UserFrame userFrame, List<Clamping> clampings) {
		this(id, userFrame, null, clampings);
	}
	
	public WorkArea(String id, UserFrame userFrame) {
		this(id, userFrame, null, null);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public UserFrame getUserFrame() {
		return userFrame;
	}

	public void setUserFrame(UserFrame userFrame) {
		this.userFrame = userFrame;
	}

	public Clamping getActiveClamping() {
		return activeClamping;
	}

	public void setActiveClamping(Clamping activeClamping) {
		this.activeClamping = activeClamping;
	}
	
	public String toString() {
		return "WorkArea " + id;
	}

	public List<Clamping> getClampings() {
		return clampings;
	}

	public void setClampings(List<Clamping> clampings) {
		clampings = new ArrayList<Clamping>();
		for (Clamping clamping : clampings) {
			addClamping(clamping);
		}
	}
	
	public void addClamping(Clamping clamping) {
		clampings.add(clamping);
		clamping.setCorrespondingWorkArea(this);
	}
	
	public void removeClamping(Clamping clamping) {
		clampings.remove(clamping);
		clamping.setCorrespondingWorkArea(null);
	}
	
	public List<String> getClampingIds() {
		List<String> clampingIds = new ArrayList<String>();
		for (Clamping clamping: clampings) {
			clampingIds.add(clamping.getId());
		}
		return clampingIds;
	}
	
	public Clamping getClampingById(String id) {
		for (Clamping clamping : clampings) {
			if (clamping.getId().equals(id)) {
				return clamping;
			}
		}
		return null;
	}
}
