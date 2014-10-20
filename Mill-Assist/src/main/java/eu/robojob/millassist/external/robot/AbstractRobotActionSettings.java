package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.AbstractProcessStep;

public abstract class AbstractRobotActionSettings<T extends AbstractProcessStep> {
	
	private int id;
	private T step;
	private AbstractRobot robot;
	private WorkArea workArea;
	private GripperHead gripperHead;
	private Coordinates smoothPoint;
	private Coordinates location;
	private boolean freeAfter;
	private boolean teachingNeeded;
	private boolean gripInner;
	
	public enum ApproachType {
		TOP(1), BOTTOM(2);
		
		private int id;
		
		private ApproachType(final int id) {
			this.id = id;
		}
		
		public int getId() {
			return this.id;
		}
		
		public static ApproachType getById(int id) {
		    for(ApproachType type : values()) {
		        if(type.id == id) 
		        	return type;
		    }
		    return null;
		 }	
	}
	
	public AbstractRobotActionSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, final boolean gripInner) {
		this.robot = robot;
		this.workArea = workArea;
		this.gripperHead = gripperHead;
		this.smoothPoint = smoothPoint;
		this.location = location;
		this.freeAfter = false;
		this.teachingNeeded = false;
		this.gripInner = gripInner;
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}
	
	public AbstractRobot getRobot() {
		return robot;
	}

	public void setRobot(final AbstractRobot robot) {
		this.robot = robot;
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
	
	/**
	 * Set the freeAfter flag. This flag indicates that the robot can go to home after the performed action.
	 *  
	 * @param freeAfter
	 */
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
	
	public boolean isGripInner() {
		return gripInner;
	}
	
	public void setGripInner(final boolean gripInner) {
		this.gripInner = gripInner;
	}
	
	public boolean isTeachingNeeded() {
		return teachingNeeded;
	}
	
	public void setTeachingNeeded(final boolean teachingNeeded) {
		this.teachingNeeded = teachingNeeded;
	}
}
