package eu.robojob.irscw.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.process.execution.ProcessExecutor;

public class InterventionStep extends AbstractProcessStep implements DeviceStep {

	private int frequency;
	private DeviceInterventionSettings interventionSettings;
	
	private static Logger logger = LogManager.getLogger(InterventionStep.class.getName());
		
	public InterventionStep(final ProcessFlow processFlow, final DeviceInterventionSettings interventionSettings, final int frequency) {
		super(processFlow);
		this.frequency = frequency;
		setDeviceSettings(interventionSettings);
	}
	
	public InterventionStep(final DeviceInterventionSettings interventionSettings, final int frequency) {
		this(null, interventionSettings, frequency);
	}
	
	//TODO check implementation intervention step!!
	@Override
	public void executeStep(final int workPieceId, final ProcessExecutor executor) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException {
		// check if the parent process has locked the device to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device [" + getDevice() + "] was already locked by: [" + getDevice().getLockingProcess() + "].");
		} else {
			try {
				if (isInterventionNeeded(getProcessFlow().getFinishedAmount())) {	// check if the the amount of finished pieces corresponds to the frequency
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
					logger.debug("About to prepare device: [" + getDevice() + "] for intervention.");
					checkProcessExecutorStatus(executor);
					getDevice().prepareForIntervention(interventionSettings);
					for (AbstractDevice device : getProcessFlow().getDevices()) {
						if (device instanceof AbstractCNCMachine) {
							checkProcessExecutorStatus(executor);
							((AbstractCNCMachine) device).indicateOperatorRequested(true);
						}
					}
					logger.debug("Device: [" + getDevice() + "] prepared for intervention.");
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.INTERVENTION_READY, workPieceId));
				}
			} catch(AbstractCommunicationException | DeviceActionException | InterruptedException e) {
				throw e;
			} finally {
				getDevice().release();
			}
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
		return interventionSettings.getDevice();
	}

}
