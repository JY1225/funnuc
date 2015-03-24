package eu.robojob.millassist.external.device;

public class ClampingInUseException extends Exception {

	private static final long serialVersionUID = 1L;
	private String clampingName;
	
	public ClampingInUseException(final String clampingName) {
		this.clampingName = clampingName;
	}
	
	@Override
	public String getMessage() {
		return "Clamping " + clampingName + " is currently in use.";
	}

}
