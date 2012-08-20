package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractRobot extends AbstractServiceProvider {
	
	private GripperBody gripperBody;
	
	public AbstractRobot(String id, GripperBody gripperBody) {
		super(id);
		this.gripperBody = gripperBody;
	}
	
	public abstract Coordinates getPosition();
	
	public abstract void pick(AbstractRobotPickSettings pickSettings);
	public abstract void put(AbstractRobotPutSettings putSettings);
	
	public abstract void releasePiece(AbstractRobotPutSettings putSettings);
	public abstract void grabPiece(AbstractRobotPickSettings pickSettings);
	
	public abstract void moveToSafePoint();
	
	public String toString() {
		return "Robot: " + id;
	}
	
	
	public abstract class AbstractRobotActionSettings{
		final private WorkArea workArea;
		final private Gripper gripper;
		final private GripperBody gripperBody;
		final private GripperHead gripperHead;
		public AbstractRobotActionSettings(WorkArea workArea, Gripper gripper, GripperBody gripperBody, GripperHead gripperHead) {
			this.workArea = workArea;
			this.gripper = gripper;
			this.gripperBody = gripperBody;
			this.gripperHead = gripperHead;
		}
		public WorkArea getWorkArea() {
			return workArea;
		}
		public Gripper getGripper() {
			return gripper;
		}
		public GripperBody getGripperBody() {
			return gripperBody;
		}
		public GripperHead getGripperHead() {
			return gripperHead;
		}
	}
	
	public abstract class AbstractRobotPickSettings extends AbstractRobotActionSettings {
		public AbstractRobotPickSettings(WorkArea workArea, Gripper gripper, GripperBody gripperBody, GripperHead gripperHead) {
			super(workArea, gripper, gripperBody, gripperHead);
		}
	}
	public abstract class AbstractRobotPutSettings extends AbstractRobotActionSettings {
		public AbstractRobotPutSettings(WorkArea workArea, Gripper gripper, GripperBody gripperBody, GripperHead gripperHead) {
			super(workArea, gripper, gripperBody, gripperHead);
		}
	}
	
	public GripperBody getGripperBody() {
		return gripperBody;
	}

	public void setGripperBody(GripperBody gripperBody) {
		this.gripperBody = gripperBody;
	}
	
}
