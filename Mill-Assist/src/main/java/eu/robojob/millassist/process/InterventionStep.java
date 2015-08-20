package eu.robojob.millassist.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.stacking.conveyor.AbstractConveyor;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.ProcessExecutor;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public class InterventionStep extends AbstractProcessStep implements DeviceStep {

	private int frequency;
	private DeviceInterventionSettings interventionSettings;
	private String customMessage="";
	
	private static Logger logger = LogManager.getLogger(InterventionStep.class.getName());
		
	public InterventionStep(final ProcessFlow processFlow, final DeviceInterventionSettings interventionSettings, final int frequency) {
		super(processFlow);
		this.frequency = frequency;
		setDeviceSettings(interventionSettings);
	}
	
	public InterventionStep(final DeviceInterventionSettings interventionSettings, final int frequency) {
		this(null, interventionSettings, frequency);
	}
	
	public InterventionStep(final ProcessFlow processFlow, final DeviceInterventionSettings interventionSettings, final int frequency, final String customMessage) {
        this(processFlow, interventionSettings, frequency);
        this.customMessage = customMessage;
    }
	
	//TODO check implementation intervention step!!
	@Override
	public void executeStep(final int workPieceId, final ProcessExecutor executor) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException {
		// check if the parent process has locked the device to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device [" + getDevice() + "] was already locked by: [" + getDevice().getLockingProcess() + "].");
		} else {
			try {
				if (isInterventionNeeded()) {	// check if the the amount of finished pieces corresponds to the frequency
					//TODO move robot to home!
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
					logger.debug("About to prepare device: [" + getDevice() + "] for intervention.");
					checkProcessExecutorStatus(executor);
					getDevice().prepareForIntervention(interventionSettings);
					for (AbstractDevice device : getProcessFlow().getDevices()) {
						if (device instanceof AbstractCNCMachine) {
							checkProcessExecutorStatus(executor);
							((AbstractCNCMachine) device).indicateOperatorRequested(true);
						}
						if (device instanceof AbstractConveyor) {
							checkProcessExecutorStatus(executor);
							((AbstractConveyor) device).indicateOperatorRequested(true);
						}
					}
					logger.debug("Device: [" + getDevice() + "] prepared for intervention.");
					
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.INTERVENTION_READY, workPieceId));
					getProcessFlow().setMode(Mode.PAUSED);
					
				}
			} catch(AbstractCommunicationException | DeviceActionException | InterruptedException e) {
				throw e;
			} finally {
				getDevice().release();
			}
		}
	}
	
	public void interventionFinished() throws AbstractCommunicationException, InterruptedException {
		for (AbstractDevice device : getProcessFlow().getDevices()) {
			if (device instanceof AbstractCNCMachine) {
				((AbstractCNCMachine) device).indicateOperatorRequested(false);
				logger.debug("OPERATOR NO LONGER REQUESTED");
			}
			if (device instanceof AbstractConveyor) {
				((AbstractConveyor) device).indicateOperatorRequested(false);
			}
		}
	}
	
	public boolean isInterventionNeeded() {
		int finishedAmount = getProcessFlow().getFinishedAmount();
		return isInterventionNeeded(finishedAmount);
	}
	
	public boolean isInterventionNeeded(final int finAmount) {
		int currentStepIndex = getProcessFlow().getStepIndex(this);
		int finishedAmount = finAmount;
		if(getDeviceSettings().getDevice() instanceof UnloadPallet) {
            finishedAmount += ((UnloadPallet)getDeviceSettings().getDevice()).getWorkPieceAmount(Type.FINISHED) - finAmount;
            return ((finishedAmount > 0) && (finishedAmount % frequency == 0));
        }
		finishedAmount++;
		if (currentStepIndex < getProcessFlow().getCurrentIndex(ProcessFlow.WORKPIECE_0_ID)) {
			finishedAmount++;
		}
		if (currentStepIndex < getProcessFlow().getCurrentIndex(ProcessFlow.WORKPIECE_1_ID)) {
			finishedAmount++;
		}
		
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

    public String getCustomMessage() {
        return this.customMessage;
    }

}
