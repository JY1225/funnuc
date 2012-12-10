package eu.robojob.irscw.external.device;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;

public class DeviceDisconnectedException extends AbstractCommunicationException {

	private static final long serialVersionUID = 1L;
	private AbstractDevice device;
	
	public DeviceDisconnectedException(AbstractDevice device) {
		this.device = device;
	}

	public AbstractDevice getDevice() {
		return device;
	}
	
	@Override
	public String getMessage() {
		return "Verbinding verbroken met " + device.getId();
	}

	@Override
	public String getLocalizedMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
