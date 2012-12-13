package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class InterventionStep extends AbstractProcessStep {

	private int frequency;
	
	private DeviceInterventionSettings interventionSettings;
	
	private AbstractRobot robot;
		
	public InterventionStep(final ProcessFlow processFlow, final AbstractDevice device, final AbstractRobot robot, final DeviceInterventionSettings interventionSettings, final int frequency) {
		super(processFlow, device);
		this.robot = robot;
		this.frequency = frequency;
		setInterventionSettings(interventionSettings);
	}
	
	public InterventionStep(final AbstractDevice device, final AbstractRobot robot, final DeviceInterventionSettings interventionSettings, final int frequency) {
		this(null, device, robot, interventionSettings, frequency);
	}
	
	@Override
	public void executeStep() throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException {
		// check if the parent process has locked the device to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device " + getDevice() + " was already locked by: " + getDevice().getLockingProcess());
		} else {
			if (!robot.lock(getProcessFlow())) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.INTERVENTION_ROBOT_TO_HOME));
				robot.moveToHome();
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.INTERVENTION_PREPARE_DEVICE));
				getDevice().prepareForIntervention(interventionSettings);
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.INTERVENTION_READY));
			}
		}
	}

	public DeviceInterventionSettings getInterventionSettings() {
		return interventionSettings;
	}

	public void setInterventionSettings(final DeviceInterventionSettings interventionSettings) {
		this.interventionSettings = interventionSettings;
		if (interventionSettings != null) {
			interventionSettings.setStep(this);
		}
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(final int frequency) {
		this.frequency = frequency;
	}

	@Override
	public String toString() {
		return "InterventionStep, " + "device: " + getDevice();
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.INTERVENTION_STEP;
	}

}
