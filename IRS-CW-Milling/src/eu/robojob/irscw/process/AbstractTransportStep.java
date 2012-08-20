package eu.robojob.irscw.process;

import eu.robojob.irscw.external.device.AbstractDevice;

public abstract class AbstractTransportStep extends AbstractProcessStep {

	public AbstractTransportStep(Process parentProcess, AbstractDevice device) {
		super(parentProcess, device);
	}
	
	public abstract void finalize();

}
