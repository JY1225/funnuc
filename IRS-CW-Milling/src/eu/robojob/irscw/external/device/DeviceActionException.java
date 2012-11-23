package eu.robojob.irscw.external.device;


public class DeviceActionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public DeviceActionException(String message) {
		super(message);
	}

	@Override
	public String getMessage() {
		return "Fout tijdens uitvoeren actie van device: " + super.getMessage();
	}
}
