package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.AbstractProcessStep;

public abstract class AbstractRobotActionSettings<T extends AbstractProcessStep> {
	
	private T step;
	private WorkArea workArea;
	private GripperHead gripperHead;
	private Coordinates smoothPoint;
	private Coordinates location;
	private boolean freeAfter;
	private boolean teachingNeeded;
	
	public AbstractRobotActionSettings(final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location) {
		this.workArea = workArea;
		this.gripperHead = gripperHead;
		this.smoothPoint = smoothPoint;
		this.location = location;
		this.freeAfter = true;
		this.teachingNeeded = false;
	}
	
	public void setStep(final T step) {
		this.step = step;
	}
	
	public T getStep() {
		return step;
	}
	
	public boolean isFreeAfter() {
		return freeAfter;
	}

	public void setFreeAfter(final boolean freeAfter) {
		this.freeAfter = freeAfter;
	}

	public WorkArea getWorkArea() {
		return workArea;
	}
	public GripperHead getGripperHead() {
		return gripperHead;
	}
	public void setGripperHead(final GripperHead gripperHead) {
		this.gripperHead = gripperHead;
	}
	public Coordinates getSmoothPoint() {
		return smoothPoint;
	}
	public void setSmoothPoint(final Coordinates smoothPoint) {
		this.smoothPoint = smoothPoint;
	}
	public Coordinates getLocation() {
		return location;
	}
	public void setLocation(final Coordinates location) {
		this.location = location;
	}
	public void setWorkArea(final WorkArea workArea) {
		this.workArea = workArea;
	}

	public boolean isTeachingNeeded() {
		return teachingNeeded;
	}
	public void setTeachingNeeded(final boolean teachingNeeded) {
		this.teachingNeeded = teachingNeeded;
	}
}
