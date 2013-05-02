package eu.robojob.millassist.ui.general.model;

import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotSettings;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.PutStep;

public class TransportInformation {

	private AbstractRobot robot;
	
	private InterventionStep interventionBeforePick;
	private PickStep pickStep;
	private PutStep putStep;
	private InterventionStep interventionAfterPut;
	
	private RobotSettings robotSettings;
	
	private int index; 
	
	public TransportInformation(final int index, final AbstractRobot robot, final InterventionStep interventionBeforePick, final PickStep pickStep, final PutStep putStep, 
			final InterventionStep interventionAfterPut, final RobotSettings robotSettings) {
		this.index = index;
		this.robot = robot;
		this.interventionBeforePick = interventionBeforePick;
		this.pickStep = pickStep;
		this.putStep = putStep;
		this.interventionAfterPut = interventionAfterPut;
		this.robotSettings = robotSettings;
	}
	
	public TransportInformation() {
		this(-1, null, null, null, null, null, null);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public AbstractRobot getRobot() {
		return robot;
	}

	public void setRobot(final AbstractRobot robot) {
		this.robot = robot;
	}

	public InterventionStep getInterventionBeforePick() {
		return interventionBeforePick;
	}

	public void setInterventionBeforePick(final InterventionStep interventionBeforePick) {
		this.interventionBeforePick = interventionBeforePick;
	}

	public PickStep getPickStep() {
		return pickStep;
	}

	public void setPickStep(final PickStep pickStep) {
		this.pickStep = pickStep;
		setRobot(pickStep.getRobot());
	}

	public PutStep getPutStep() {
		return putStep;
	}

	public void setPutStep(final PutStep putStep) {
		this.putStep = putStep;
		setRobot(putStep.getRobot());
	}

	public InterventionStep getInterventionAfterPut() {
		return interventionAfterPut;
	}

	public void setInterventionAfterPut(final InterventionStep interventionAfterPut) {
		this.interventionAfterPut = interventionAfterPut;
	}
	
	public boolean hasInterventionBeforePick() {
		if (interventionBeforePick != null) {
			return true;
		}
		return false;
	}
	
	public boolean hasInterventionAfterPut() {
		if (interventionAfterPut != null) {
			return true;
		}
		return false;
	}

	public RobotSettings getRobotSettings() {
		return robotSettings;
	}

	public void setRobotSettings(final RobotSettings robotSettings) {
		this.robotSettings = robotSettings;
	}
}
