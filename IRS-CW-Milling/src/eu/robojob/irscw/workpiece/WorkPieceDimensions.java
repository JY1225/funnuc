package eu.robojob.irscw.workpiece;

public class WorkPieceDimensions {

	private float height;
	private float length;
	private float width;
	
	public WorkPieceDimensions (float length, float width, float height) {
		this.length = length;
		this.width = width;
		this.height = height;
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

}
