package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class InterventionStep extends AbstractProcessStep {

	private int frequency;
	
	private AbstractDevice.AbstractDeviceInterventionSettings interventionSettings;
	
	private AbstractRobot robot;
		
	public InterventionStep(ProcessFlow processFlow, AbstractDevice device, AbstractRobot robot, AbstractDevice.AbstractDeviceInterventionSettings interventionSettings, int frequency) {
		super(processFlow, device);
		this.robot = robot;
		this.frequency = frequency;
		setInterventionSettings(interventionSettings);
	}
	
	public InterventionStep(AbstractDevice device, AbstractRobot robot, AbstractDevice.AbstractDeviceInterventionSettings interventionSettings, int frequency) {
		this(null, device, robot, interventionSettings, frequency);
	}
	
	@Override
	public void executeStep() throws AbstractCommunicationException, DeviceActionException, RobotActionException {
		// check if the parent process has locked the device to be used
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.INTERVENTION_ROBOT_TO_HOME));
				robot.moveToHome();
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.INTERVENTION_PREPARE_DEVICE));
				device.prepareForIntervention(interventionSettings);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.INTERVENTION_READY));
			}
		}
	}

	public AbstractDevice.AbstractDeviceInterventionSettings getInterventionSettings() {
		return interventionSettings;
	}

	public void setInterventionSettings(
			AbstractDevice.AbstractDeviceInterventionSettings interventionSettings) {
		this.interventionSettings = interventionSettings;
		if (interventionSettings != null)
			interventionSettings.setStep(this);
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	@Override
	public String toString() {
		return "InterventionStep, " + "device: " + device;
	}

	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		return providers;
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.INTERVENTION_STEP;
	}

}
