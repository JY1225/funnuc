package eu.robojob.irscw.external.device.stacking;

public class IncorrectWorkPieceDataException extends Exception {

	private static final long serialVersionUID = 1L;

	public IncorrectWorkPieceDataException() {
		super("Incorrect work piece data");
	}
	
	public IncorrectWorkPieceDataException(String message) {
		super(message);
	}
}
