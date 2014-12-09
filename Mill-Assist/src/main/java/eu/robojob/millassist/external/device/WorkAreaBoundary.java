package eu.robojob.millassist.external.device;

import eu.robojob.millassist.external.robot.AirblowSquare;

public final class WorkAreaBoundary {

	// Boundary of the zone - square (lower left corner/upper right corner)
	private AirblowSquare boundaries;
	private WorkArea workarea;
	
	public WorkAreaBoundary(final WorkArea workarea, final AirblowSquare boundary) {
		this.workarea = workarea;
		this.boundaries = boundary;
	}
	
	public WorkArea getWorkArea() {
		return this.workarea;
	}
	
	public AirblowSquare getBoundary() {
		return this.boundaries;
	}
	
	@Override
	public String toString() {
		return workarea.getName();
	}
}
