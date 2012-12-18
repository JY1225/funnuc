package eu.robojob.irscw.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class ProcessingStep extends AbstractProcessStep implements DeviceStep {

	private ProcessingDeviceStartCyclusSettings startCyclusSettings;
	private AbstractProcessingDevice device;
	
	private static Logger logger = LogManager.getLogger(ProcessingStep.class.getName());
	
	public ProcessingStep(final ProcessFlow processFlow, final AbstractProcessingDevice processingDevice, final ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		super(processFlow);
		this.device = processingDevice;
		setDeviceSettings(startCyclusSettings);
	}
	
	public ProcessingStep(final AbstractProcessingDevice processingDevice, final ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		this(null, processingDevice, startCyclusSettings);
	}
	
	@Override
	public void executeStep(final int workPieceId) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the device to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device [" + getDevice() + "] was already locked by [" + getDevice().getLockingProcess() + "].");
		} else {
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.STARTED, workPieceId));
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
			logger.debug("Preparing device [" + getDevice() + "] for processing.");
			getDevice().prepareForStartCyclus(startCyclusSettings);
			logger.debug("Device [" + getDevice() + "] prepared, starting processing.");
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PROCESSING_STARTED, workPieceId));
			getDevice().startCyclus(startCyclusSettings);
			logger.debug("Finished processing in [" + getDevice() + "].");
			getDevice().release(getProcessFlow());
			getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, workPieceId));
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
		return device;
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
