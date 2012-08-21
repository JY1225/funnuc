package eu.robojob.irscw.process;

import java.io.IOException;

import eu.robojob.irscw.external.device.AbstractDevice;

public abstract class AbstractTransportStep extends AbstractProcessStep {

	public AbstractTransportStep(ProcessFlow processFlow, AbstractDevice device) {
		super(processFlow, device);
	}
	
	public AbstractTransportStep(AbstractDevice device) {
		this(null, device);
	}
	
	public abstract void finalize() throws IOException;

}
