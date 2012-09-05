package eu.robojob.irscw.ui.main.model;

import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.PutStep;

public class TransportInformation {

	private AbstractRobot robot;
	
	private InterventionStep interventionBeforePick;
	private PickStep pickStep;
	private PutStep putStep;
	private InterventionStep interventionAfterPut;
	
	public TransportInformation(AbstractRobot robot, InterventionStep interventionBeforePick, PickStep pickStep, PutStep putStep, 
			InterventionStep interventionAfterPut) {
		this.robot = robot;
		this.interventionBeforePick = interventionBeforePick;
		this.pickStep = pickStep;
		this.putStep = putStep;
		this.interventionAfterPut = interventionAfterPut;
	}
	
	public TransportInformation() {
		this(null, null, null, null, null);
	}

	public AbstractRobot getRobot() {
		return robot;
	}

	public void setRobot(AbstractRobot robot) {
		this.robot = robot;
	}

	public InterventionStep getInterventionBeforePick() {
		return interventionBeforePick;
	}

	public void setInterventionBeforePick(InterventionStep interventionBeforePick) {
		this.interventionBeforePick = interventionBeforePick;
	}

	public PickStep getPickStep() {
		return pickStep;
	}

	public void setPickStep(PickStep pickStep) {
		this.pickStep = pickStep;
	}

	public PutStep getPutStep() {
		return putStep;
	}

	public void setPutStep(PutStep putStep) {
		this.putStep = putStep;
	}

	public InterventionStep getInterventionAfterPut() {
		return interventionAfterPut;
	}

	public void setInterventionAfterPut(InterventionStep interventionAfterPut) {
		this.interventionAfterPut = interventionAfterPut;
	}
	
	public boolean hasInterventionBeforePick() {
		if (interventionBeforePick != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasInterventionAfterPut() {
		if (interventionAfterPut != null) {
			return true;
		} else {
			return false;
		}
	}
	
}
