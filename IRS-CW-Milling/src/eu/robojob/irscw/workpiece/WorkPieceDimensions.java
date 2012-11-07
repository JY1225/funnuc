package eu.robojob.irscw.workpiece;

public class WorkPieceDimensions {
	
	private float height;
	private float length;
	private float width;
	
	private boolean knownShape;
	
	public WorkPieceDimensions (float length, float width, float height) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.knownShape = false;
	}
	
	public WorkPieceDimensions clone() {
		return new WorkPieceDimensions(length, width, height);
	}
	
	public WorkPieceDimensions() {
		this(-1, -1, -1);
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public boolean isKnownShape() {
		return knownShape;
	}

	public void setKnownShape(boolean knownShape) {
		this.knownShape = knownShape;
	}

	public String toString() {
		return "(" + length + ", " + width + ", " + height + ")";
	}
}
