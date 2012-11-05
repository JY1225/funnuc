package eu.robojob.irscw.external.device;

import eu.robojob.irscw.external.communication.CommunicationException;

public class DeviceDisconnectedException extends CommunicationException {

	private static final long serialVersionUID = 1L;
	private AbstractDevice device;
	
	public DeviceDisconnectedException(AbstractDevice device) {
		this.device = device;
	}

	public AbstractDevice getDevice() {
		return device;
	}
}
