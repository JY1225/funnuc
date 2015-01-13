package eu.robojob.millassist.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.ProcessExecutor;

public class ProcessingStep extends AbstractProcessStep implements DeviceStep {

	private ProcessingDeviceStartCyclusSettings startCyclusSettings;
	private int processing;
	
	private static Logger logger = LogManager.getLogger(ProcessingStep.class.getName());
	
	public ProcessingStep(final ProcessFlow processFlow, final ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		super(processFlow);
		this.processing = 0;
		setDeviceSettings(startCyclusSettings);
	}
	
	public ProcessingStep(final ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		this(null, startCyclusSettings);
	}
	
	public boolean isProcessing() {
		return processing > 0;
	}
	
	public void setNotProcessing() {
		processing = 0;
	}
	
	@Override
	public void executeStep(final int processId, final ProcessExecutor executor) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the device to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device [" + getDevice() + "] was already locked by [" + getDevice().getLockingProcess() + "].");
		} else {
			try {
				checkProcessExecutorStatus(executor);
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.STARTED, processId));
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, processId));
				logger.debug("Preparing device [" + getDevice() + "] for processing.");
				checkProcessExecutorStatus(executor);
				getDevice().prepareForStartCyclus(startCyclusSettings);
				logger.debug("Device [" + getDevice() + "] prepared, starting processing.");
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PROCESSING_STARTED, processId));
				checkProcessExecutorStatus(executor);
				processing++;
				logger.info("Running: " + processing);
				getDevice().startCyclus(startCyclusSettings, processId);
				processing--;
				logger.debug("Finished processing for PRC[" + processId + "] in [" + getDevice() + "].");
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, processId));
			} catch(Exception e) {
				throw e;
			} finally {
				getDevice().release();
			}
		}
	}

	public ProcessingDeviceStartCyclusSettings getDeviceSettings() {
		return startCyclusSettings;
	}

	public void setDeviceSettings(final ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		this.startCyclusSettings = startCyclusSettings;
		if (startCyclusSettings != null) {
			startCyclusSettings.setStep(this);
		}
	}
	
	@Override
	public AbstractProcessingDevice getDevice() {
		return startCyclusSettings.getDevice();
	}

	@Override
	public String toString() {
		return "Processing step in [" + getDevice() + "]."; 
	}
	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PROCESSING_STEP;
	}
	
}
