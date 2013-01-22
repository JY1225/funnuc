package eu.robojob.irscw.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class InterventionStep extends AbstractProcessStep implements DeviceStep {

	private int frequency;
	
	private AbstractDevice device;
	private DeviceInterventionSettings interventionSettings;
	
	private static Logger logger = LogManager.getLogger(InterventionStep.class.getName());
		
	public InterventionStep(final ProcessFlow processFlow, final AbstractDevice device, final DeviceInterventionSettings interventionSettings, final int frequency) {
		super(processFlow);
		this.frequency = frequency;
		this.device = device;
		setDeviceSettings(interventionSettings);
	}
	
	public InterventionStep(final AbstractDevice device, final DeviceInterventionSettings interventionSettings, final int frequency) {
		this(null, device, interventionSettings, frequency);
	}
	
	@Override
	public void executeStep(final int workPieceId) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException {
		// check if the parent process has locked the device to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device [" + getDevice() + "] was already locked by: [" + getDevice().getLockingProcess() + "].");
		} else {
			if (isInterventionNeeded(getProcessFlow().getFinishedAmount())) {	// check if the the amount of finished pieces corresponds to the frequency
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
				logger.debug("About to prepare device: [" + getDevice() + "] for intervention.");
				getDevice().prepareForIntervention(interventionSettings);
				logger.debug("Device: [" + getDevice() + "] prepared for intervention.");
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.INTERVENTION_READY, workPieceId));
			}
			getDevice().release(getProcessFlow());
		}
	}
	
	public boolean isInterventionNeeded(final int finishedAmount) {
		return ((finishedAmount > 0) && (finishedAmount % frequency == 0));
	}

	public void setDeviceSettings(final DeviceInterventionSettings interventionSettings) {
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
		return "InterventionStep, device [" + getDevice() + "].";
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.INTERVENTION_STEP;
	}

	@Override
	public DeviceInterventionSettings getDeviceSettings() {
		return interventionSettings;
	}

	@Override
	public AbstractDevice getDevice() {
		return device;
	}

}
