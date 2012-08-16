package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractRobot extends AbstractServiceProvider {
	
	public AbstractRobot(String id) {
		super(id);
	}
	
	public abstract Coordinates getPosition();
	
	public abstract void pick(WorkArea workArea, AbstractRobotPickSettings pickSettings);
	public abstract void put(WorkArea workArea, AbstractRobotPutSettings putSettings);
	
	public abstract void releasePiece(WorkArea workArea, AbstractRobotGriperSettings griperSettings);
	public abstract void grabPiece(WorkArea workArea, AbstractRobotGriperSettings griperSettings);
	
	public String toString() {
		return "Robot: " + id;
	}
	
	public abstract class AbstractRobotPickSettings{}
	public abstract class AbstractRobotPutSettings{}
	public abstract class AbstractRobotGriperSettings{}

}
