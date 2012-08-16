package eu.robojob.irscw.external.device;

import eu.robojob.irscw.positioning.UserFrame;

public class WorkArea {
	
	private String id;
	private Zone zone;
	private UserFrame userFrame;
	private Clamping clamping;
	
	public WorkArea (String id, Zone zone, UserFrame userFrame, Clamping clamping) {
		this.id = id;
		this.zone = zone;
		zone.addWorkArea(this);
		this.userFrame = userFrame;
		this.clamping = clamping;
	}
	
	public WorkArea (String id, Zone zone, UserFrame userFrame) {
		this(id, zone, userFrame, null);
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

	public Clamping getClamping() {
		return clamping;
	}

	public void setClamping(Clamping clamping) {
		this.clamping = clamping;
	}
	
	public String toString() {
		return "WorkArea " + id;
	}
	
}
