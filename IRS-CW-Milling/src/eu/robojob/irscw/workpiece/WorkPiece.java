package eu.robojob.irscw.workpiece;

public class WorkPiece {

	public enum Type {
		RAW, FINISHED
	}
	
	private Type type;
	private WorkPieceDimensions dimensions;
	
	public WorkPiece(Type type, WorkPieceDimensions dimensions) {
		this.type = type;
		this.dimensions = dimensions;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public WorkPieceDimensions getDimensions() {
		return dimensions;
	}

	public void setDimensions(WorkPieceDimensions dimensions) {
		this.dimensions = dimensions;
	}
}
