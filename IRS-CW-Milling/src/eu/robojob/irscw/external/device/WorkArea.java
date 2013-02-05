package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.positioning.UserFrame;

public class WorkArea {
	
	private int id;
	private String name;
	private Zone zone;
	private UserFrame userFrame;
	private Clamping activeClamping;
	private List<Clamping> clampings;
	
	public WorkArea(final String name, final UserFrame userFrame, final Clamping activeClamping, final List<Clamping> clampings) {
		this.name = name;
		this.userFrame = userFrame;
		this.activeClamping = activeClamping;
		this.clampings = clampings;
	}
	
	public WorkArea(final String name, final UserFrame userFrame, final List<Clamping> clampings) {
		this(name, userFrame, null, clampings);
	}
	
	public WorkArea(final String name, final UserFrame userFrame) {
		this(name, userFrame, null, new ArrayList<Clamping>());
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
		return "WorkArea " + name;
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
	
	public List<String> getClampingNames() {
		List<String> clampingIds = new ArrayList<String>();
		for (Clamping clamping: clampings) {
			clampingIds.add(clamping.getName());
		}
		return clampingIds;
	}
	
	public Clamping getClampingByName(final String name) {
		for (Clamping clamping : clampings) {
			if (clamping.getName().equals(name)) {
				return clamping;
			}
		}
		return null;
	}
}
