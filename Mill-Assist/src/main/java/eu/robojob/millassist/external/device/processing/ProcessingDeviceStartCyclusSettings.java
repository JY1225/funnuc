package eu.robojob.millassist.external.device.processing;

import eu.robojob.millassist.external.device.AbstractDeviceActionSettings;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.process.ProcessingStep;

public class ProcessingDeviceStartCyclusSettings extends AbstractDeviceActionSettings<ProcessingStep> {

	private int workNumber;
    
    public ProcessingDeviceStartCyclusSettings(final AbstractProcessingDevice device, final SimpleWorkArea workArea) {
		super(device, workArea);
	}
	
	public AbstractProcessingDevice getDevice() {
		return (AbstractProcessingDevice) super.getDevice();
	}

	public int getWorkNumber() {
	    if (getDevice() instanceof AbstractCNCMachine && ((AbstractCNCMachine) getDevice()).hasWorkNumberSearch()) {
	        return this.workNumber;
	    }
	    return -1;
	}
	
	public void setWorkNumber(int workNumber) {
	    this.workNumber = workNumber;
	}
}
