package eu.robojob.irscw.external.device;

import eu.robojob.irscw.util.Translator;

public class DeviceActionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private static final String EXCEPTION_DURING_DEVICE_ACTION = "DeviceActionException.exceptionDuringDeviceAction";
	
	private AbstractDevice device;
	private String errorId;
	private Translator translator;
	
	public DeviceActionException(AbstractDevice device, String errorId) {
		this.errorId = errorId;
		this.device = device;
		this.translator = Translator.getInstance();
	}

	@Override
	public String getMessage() {
		return "Error during the executing of an action of: " + device.getId() + ", " + super.getMessage();
	}
	
	@Override
	public String getLocalizedMessage() {
		return translator.getTranslation(EXCEPTION_DURING_DEVICE_ACTION) + ": " + device.getId() + ", " + translator.getTranslation(errorId);
	}
}
