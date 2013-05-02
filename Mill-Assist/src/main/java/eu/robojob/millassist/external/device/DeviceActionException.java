package eu.robojob.irscw.external.device;

import eu.robojob.irscw.util.Translator;

public class DeviceActionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private static final String EXCEPTION_DURING_DEVICE_ACTION = "DeviceActionException.exceptionDuringDeviceAction";
	
	private AbstractDevice device;
	private String errorId;
	
	public DeviceActionException(final AbstractDevice device, final String errorId) {
		this.errorId = errorId;
		this.device = device;
	}

	@Override
	public String getMessage() {
		return "Error during the executing of an action of: " + device.getName() + ", " + super.getMessage();
	}
	
	@Override
	public String getLocalizedMessage() {
		return Translator.getTranslation(EXCEPTION_DURING_DEVICE_ACTION) + ": " + device.getName() + ", " + Translator.getTranslation(errorId);
	}
}
