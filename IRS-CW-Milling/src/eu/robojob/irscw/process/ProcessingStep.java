package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class ProcessingStep extends AbstractProcessStep {

	private ProcessingDeviceStartCyclusSettings startCyclusSettings;
	
	private static final Logger logger = LogManager.getLogger(ProcessingStep.class.getName());
	
	public ProcessingStep(ProcessFlow processFlow, AbstractProcessingDevice processingDevice,
			ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		super(processFlow, processingDevice);
		setStartCyclusSettings(startCyclusSettings);
	}
	
	public ProcessingStep(AbstractProcessingDevice processingDevice, ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		this(null, processingDevice, startCyclusSettings);
	}
	
	@Override
	public void executeStep() throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the device to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device " + getDevice() + " was already locked by: " + getDevice().getLockingProcess());
		} else {
			logger.debug("About to execute processing by " + getDevice().getId());
			getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PROCESSING_PREPARE_DEVICE));
			logger.debug("Preparing device...");
			((AbstractProcessingDevice) getDevice()).prepareForStartCyclus(startCyclusSettings);
			logger.debug("Device prepared, starting processing");
			getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PROCESSING_IN_PROGRESS));
			((AbstractProcessingDevice) getDevice()).startCyclus(startCyclusSettings);
			logger.debug("Processing finished!");
			getDevice().release(getProcessFlow());
			getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PROCESSING_FINISHED));
		}
	}

	public ProcessingDeviceStartCyclusSettings getStartCyclusSettings() {
		return startCyclusSettings;
	}

	public void setStartCyclusSettings(ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		this.startCyclusSettings = startCyclusSettings;
		if (startCyclusSettings!= null)
			startCyclusSettings.setStep(this);
	}

	@Override
	public String toString() {
		return "Processing step, " + "device: " + getDevice(); 
	}
	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PROCESSING_STEP;
	}
	
	@Override 
	public AbstractProcessingDevice getDevice() {
		return (AbstractProcessingDevice) super.getDevice();
	}
	
}
