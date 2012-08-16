package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractRobot extends AbstractServiceProvider {
	
	public AbstractRobot(String id) {
		super(id);
	}
	
	public abstract Coordinates getPosition();
	
	public abstract void pick(AbstractRobotPickSettings pickSettings);
	public abstract void put(AbstractRobotPutSettings putSettings);
	
	public abstract void releasePiece(AbstractRobotGriperSettings griperSettings);
	public abstract void grabPiece(AbstractRobotGriperSettings griperSettings);
	
	public String toString() {
		return "Robot: " + id;
	}
	
	public abstract class AbstractRobotPickSettings{}
	public abstract class AbstractRobotPutSettings{}
	public abstract class AbstractRobotGriperSettings{}

}
