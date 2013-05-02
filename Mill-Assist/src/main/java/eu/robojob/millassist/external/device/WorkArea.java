package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.robojob.irscw.positioning.UserFrame;

public class WorkArea {
	
	private int id;
	private String name;
	private Zone zone;
	private UserFrame userFrame;
	private Clamping activeClamping;
	private Set<Clamping> clampings;
	
	public WorkArea(final String name, final UserFrame userFrame, final Clamping activeClamping, final Set<Clamping> clampings) {
		this.name = name;
		this.userFrame = userFrame;
		this.activeClamping = activeClamping;
		this.clampings = clampings;
	}
	
	public WorkArea(final String name, final UserFrame userFrame, final Set<Clamping> clampings) {
		this(name, userFrame, null, clampings);
	}
	
	public WorkArea(final String name, final UserFrame userFrame) {
		this(name, userFrame, null, new HashSet<Clamping>());
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

	public Set<Clamping> getClampings() {
		return clampings;
	}

	public void setClampings(final Set<Clamping> clampings) {
		this.clampings = new HashSet<Clamping>();
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
		List<String> clampingNames = new ArrayList<String>();
		for (Clamping clamping: clampings) {
			clampingNames.add(clamping.getName());
		}
		return clampingNames;
	}
	
	public Clamping getClampingByName(final String name) {
		for (Clamping clamping : clampings) {
			if (clamping.getName().equals(name)) {
				return clamping;
			}
		}
		return null;
	}
	
	public Clamping getClampingById(final int id) {
		for (Clamping clamping : clampings) {
			if (clamping.getId() == id) {
				return clamping;
			}
		}
		return null;
	}
}
