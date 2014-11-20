package eu.robojob.millassist.external.device;

public class ClampingInUseException extends Exception {

	private static final long serialVersionUID = 1L;
	private String clampingName;
	private String processName;
	
	public ClampingInUseException(final String clampingName, final String processName) {
		this.clampingName = clampingName;
		this.processName = processName;
	}
	
	@Override
	public String getMessage() {
		return "Clamping " + clampingName + " is currently in use by process " + processName + ".";
	}

}
