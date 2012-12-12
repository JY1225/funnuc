package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.AbstractProcessStep;

public abstract class AbstractRobotActionSettings<T extends AbstractProcessStep> {
	
	protected T step;
	protected WorkArea workArea;
	protected GripperHead gripperHead;
	protected Coordinates smoothPoint;
	protected Coordinates location;
	protected boolean freeAfter;
	
	public AbstractRobotActionSettings(WorkArea workArea, GripperHead gripperHead, Coordinates smoothPoint, Coordinates location) {
		this.workArea = workArea;
		this.gripperHead = gripperHead;
		this.smoothPoint = smoothPoint;
		this.location = location;
		this.freeAfter = false;
	}
	
	public void setStep(T step) {
		this.step = step;
	}
	
	public T getStep() {
		return step;
	}
	
	public boolean isFreeAfter() {
		return freeAfter;
	}

	public void setFreeAfter(boolean freeAfter) {
		this.freeAfter = freeAfter;
	}

	public WorkArea getWorkArea() {
		return workArea;
	}
	public GripperHead getGripperHead() {
		return gripperHead;
	}
	public void setGripperHead(GripperHead gripperHead) {
		this.gripperHead = gripperHead;
	}
	public Coordinates getSmoothPoint() {
		return smoothPoint;
	}
	public void setSmoothPoint(Coordinates smoothPoint) {
		this.smoothPoint = smoothPoint;
	}
	public Coordinates getLocation() {
		return location;
	}
	public void setLocation(Coordinates location) {
		this.location = location;
	}
	public void setWorkArea(WorkArea workArea) {
		this.workArea = workArea;
	}
}
