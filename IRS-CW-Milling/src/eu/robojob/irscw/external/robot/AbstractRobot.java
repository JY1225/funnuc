package eu.robojob.irscw.external.robot;

import java.io.IOException;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractRobot extends AbstractServiceProvider {
	
	private GripperBody gripperBody;
	
	public AbstractRobot(String id, GripperBody gripperBody) {
		super(id);
		this.gripperBody = gripperBody;
	}
	
	public AbstractRobot(String id) {
		this(id, null);
	}
	
	public abstract Coordinates getPosition() throws IOException;
	
	public abstract void pick(AbstractRobotPickSettings pickSettings) throws IOException;
	public abstract void put(AbstractRobotPutSettings putSettings) throws IOException;
	
	public abstract void releasePiece(AbstractRobotPutSettings putSettings) throws IOException;
	public abstract void grabPiece(AbstractRobotPickSettings pickSettings) throws IOException;
	
	public abstract void moveToSafePoint() throws IOException;
	
	public String toString() {
		return "Robot: " + id;
	}
	
	public static abstract class AbstractRobotActionSettings{
		final private WorkArea workArea;
		final private Gripper gripper;
		public AbstractRobotActionSettings(WorkArea workArea, Gripper gripper) {
			this.workArea = workArea;
			this.gripper = gripper;
		}
		public WorkArea getWorkArea() {
			return workArea;
		}
		public Gripper getGripper() {
			return gripper;
		}
	}
	
	public static abstract class AbstractRobotPickSettings extends AbstractRobotActionSettings {
		public AbstractRobotPickSettings(WorkArea workArea, Gripper gripper) {
			super(workArea, gripper);
		}
	}
	public static abstract class AbstractRobotPutSettings extends AbstractRobotActionSettings {
		public AbstractRobotPutSettings(WorkArea workArea, Gripper gripper) {
			super(workArea, gripper);
		}
	}
	
	public GripperBody getGripperBody() {
		return gripperBody;
	}

	public void setGripperBody(GripperBody gripperBody) {
		this.gripperBody = gripperBody;
	}
	
	public abstract AbstractRobotPickSettings getDefaultPickSettings();
	public abstract AbstractRobotPutSettings getDefaultPutSettings();
}
