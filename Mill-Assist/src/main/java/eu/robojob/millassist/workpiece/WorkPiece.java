package eu.robojob.millassist.workpiece;

public class WorkPiece {

	public enum Type {
		RAW, FINISHED
	}
	
	private int id;
	
	private Type type;
	private WorkPieceDimensions dimensions;
	
	public WorkPiece(final Type type, final WorkPieceDimensions dimensions) {
		this.type = type;
		this.dimensions = dimensions;
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public WorkPiece(final WorkPiece wp) {
		this.type = wp.getType();
		this.dimensions = new WorkPieceDimensions(wp.getDimensions().getLength(), wp.getDimensions().getWidth(), wp.getDimensions().getHeight());
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public WorkPieceDimensions getDimensions() {
		return dimensions;
	}

	public void setDimensions(final WorkPieceDimensions dimensions) {
		this.dimensions = dimensions;
	}
	
	public String toString() {
		return "WorkPiece: " + type + " - " + dimensions;
	}
}
