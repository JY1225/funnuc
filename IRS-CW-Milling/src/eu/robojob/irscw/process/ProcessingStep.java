package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class ProcessingStep extends AbstractProcessStep {

	private AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings;
	
	private static final Logger logger = Logger.getLogger(ProcessingStep.class);
	
	public ProcessingStep(ProcessFlow processFlow, AbstractProcessingDevice processingDevice,
			AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		super(processFlow, processingDevice);
		setStartCyclusSettings(startCyclusSettings);
	}
	
	public ProcessingStep(AbstractProcessingDevice processingDevice, AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		this(null, processingDevice, startCyclusSettings);
	}
	
	@Override
	public void executeStep() throws CommunicationException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the device to be used
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			logger.debug("About to execute processing by " + device.getId());
			processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PROCESSING_PREPARE_DEVICE));
			logger.debug("Preparing device...");
			((AbstractProcessingDevice) device).prepareForStartCyclus(startCyclusSettings);
			logger.debug("Device prepared, starting processing");
			processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PROCESSING_IN_PROGRESS));
			((AbstractProcessingDevice) device).startCyclus(startCyclusSettings);
			logger.debug("Processing finished!");
			device.release(processFlow);
			processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PROCESSING_FINISHED));
		}
	}

	public AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings getStartCyclusSettings() {
		return startCyclusSettings;
	}

	public void setStartCyclusSettings(
			AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		this.startCyclusSettings = startCyclusSettings;
		if (startCyclusSettings!= null)
			startCyclusSettings.setStep(this);
	}

	@Override
	public String toString() {
		return "Processing step, " + "device: " + device; 
	}
	
	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		return providers;
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
