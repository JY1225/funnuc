package eu.robojob.irscw.external.robot;

import java.io.IOException;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

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
		protected WorkArea workArea;
		protected Gripper gripper;
		protected Coordinates smoothPoint;
		protected Coordinates location;
		protected WorkPieceDimensions workPieceDimensions;
		
		public AbstractRobotActionSettings(WorkArea workArea, Gripper gripper, Coordinates smoothPoint, Coordinates location, WorkPieceDimensions workPieceDimensions) {
			this.workArea = workArea;
			this.gripper = gripper;
			this.smoothPoint = smoothPoint;
			this.location = location;
			this.workPieceDimensions = workPieceDimensions;
		}
		public WorkArea getWorkArea() {
			return workArea;
		}
		public Gripper getGripper() {
			return gripper;
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
		public void setGripper(Gripper gripper) {
			this.gripper = gripper;
		}
		public WorkPieceDimensions getWorkPieceDimensions() {
			return workPieceDimensions;
		}
		public void setWorkPieceDimensions(WorkPieceDimensions workPieceDimensions) {
			this.workPieceDimensions = workPieceDimensions;
		}
	}
	
	public static abstract class AbstractRobotPickSettings extends AbstractRobotActionSettings {
		public AbstractRobotPickSettings(WorkArea workArea, Gripper gripper, Coordinates smoothPoint, Coordinates location, WorkPieceDimensions workPieceDimensions) {
			super(workArea, gripper, smoothPoint, location, workPieceDimensions);
		}
	}
	public static abstract class AbstractRobotPutSettings extends AbstractRobotActionSettings {
		public AbstractRobotPutSettings(WorkArea workArea, Gripper gripper, Coordinates smoothPoint, Coordinates location, WorkPieceDimensions workPieceDimensions) {
			super(workArea, gripper, smoothPoint, location, workPieceDimensions);
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
