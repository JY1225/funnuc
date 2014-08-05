package eu.robojob.millassist.workpiece;

public class WorkPieceDimensions {
	
	private float height;
	private float length;
	private float width;
	
	private boolean knownShape;
	
	public WorkPieceDimensions(final float length, final float width, final float height) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.knownShape = false;
	}
	
	public WorkPieceDimensions() {
		this(-1, -1, -1);
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public float getLength() {
		return length;
	}

	public void setLength(final float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(final float width) {
		this.width = width;
	}

	public boolean isKnownShape() {
		return knownShape;
	}
	
	public boolean isValidDimension() {
		return (width > 0 && length > 0 && height > 0);
	}

	public float getVolume() {
		return width*height*length;
	}
	
	public void setKnownShape(final boolean knownShape) {
		this.knownShape = knownShape;
	}

	public String toString() {
		return "(" + length + ", " + width + ", " + height + ")";
	}
}
