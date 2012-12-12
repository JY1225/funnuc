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
	
	public WorkArea(final String id, final UserFrame userFrame, final Clamping activeClamping, final List<Clamping> clampings) {
		this.id = id;
		this.userFrame = userFrame;
		this.activeClamping = activeClamping;
		this.clampings = clampings;
	}
	
	public WorkArea(final String id, final UserFrame userFrame, final List<Clamping> clampings) {
		this(id, userFrame, null, clampings);
	}
	
	public WorkArea(final String id, final UserFrame userFrame) {
		this(id, userFrame, null, new ArrayList<Clamping>());
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(final Zone zone) {
		this.zone = zone;
	}

	public UserFrame getUserFrame() {
		return userFrame;
	}

	public void setUserFrame(final UserFrame userFrame) {
		this.userFrame = userFrame;
	}

	public Clamping getActiveClamping() {
		return activeClamping;
	}

	public void setActiveClamping(final Clamping activeClamping) {
		this.activeClamping = activeClamping;
	}
	
	public String toString() {
		return "WorkArea " + id;
	}

	public List<Clamping> getClampings() {
		return clampings;
	}

	public void setClampings(final List<Clamping> clampings) {
		this.clampings = new ArrayList<Clamping>();
		for (Clamping clamping : clampings) {
			addClamping(clamping);
		}
	}
	
	public void addClamping(final Clamping clamping) {
		clampings.add(clamping);
	}
	
	public void removeClamping(final Clamping clamping) {
		clampings.remove(clamping);
	}
	
	public List<String> getClampingIds() {
		List<String> clampingIds = new ArrayList<String>();
		for (Clamping clamping: clampings) {
			clampingIds.add(clamping.getId());
		}
		return clampingIds;
	}
	
	public Clamping getClampingById(final String id) {
		for (Clamping clamping : clampings) {
			if (clamping.getId().equals(id)) {
				return clamping;
			}
		}
		return null;
	}
}
