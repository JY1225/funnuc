package eu.robojob.millassist.workpiece;

import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;

public class RectangularDimensions implements IWorkPieceDimensions {
	
	private float height;
	private float length;
	private float width;
		
	public RectangularDimensions(final float length, final float width, final float height) {
		this.length = length;
		this.width = width;
		this.height = height;
	}
	
	public RectangularDimensions(final RectangularDimensions dimensions) {
		this(dimensions.getLength(), dimensions.getWidth(), dimensions.getHeight());
	}
	
	public RectangularDimensions() {
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
	
	public boolean isValidDimension() {
		return (width > 0 && length > 0 && height > 0);
	}

	public float getVolume() {
		return width*height*length;
	}

	public String toString() {
		return "(" + length + ", " + width + ", " + height + ")";
	}

	@Override
	public float getZSafe() {
		return getHeight();
	}
	
	//Occurs when reversalUnit.approachType = LEFT (X-as ligt naar voren)
	public void rotateDimensionsAroundX() {
		float newLength = getHeight();
		float newHeight = getLength();
		setLength(newLength);
		setHeight(newHeight);
	}
	
	//Occurs when reversalUnit.approachType = FRONT (Y-as ligt naar rechts)
	public void rotateDimensionsAroundY() {
		float newWidth = getHeight();
		float newHeight = getWidth();
		setWidth(newWidth);
		setHeight(newHeight);
	}
	
	public RectangularDimensions clone() {
		return new RectangularDimensions(this);
	}

	@Override
	public boolean hasSameDimensions(IWorkPieceDimensions dimensions) {
		if (this.getLength() == ((RectangularDimensions) dimensions).getLength() &&
				this.getWidth() == ((RectangularDimensions) dimensions).getWidth()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void setDimension(Dimensions dimension, float value) {
		switch (dimension) {
		case LENGTH:
			length = value;
		break;
		case WIDTH:
			width = value;
			break;
		case HEIGHT:
			height = value;
		break;
		default:
			break;
		}
	}

	@Override
	public float getDimension(Dimensions dimension) {
		switch (dimension) {
		case LENGTH:
			return length;
		case WIDTH:
			return width;
		case HEIGHT:
			return height;
		default:
			return -1;
		}
	}

	@Override
	public int compareTo(IWorkPieceDimensions otherDimensions) {		
		if (otherDimensions == null) {
			return 1;
		}
		if (!(otherDimensions instanceof RectangularDimensions)) {
			return -99;
		}
		RectangularDimensions other = (RectangularDimensions) otherDimensions;
		if (this.getWidth() > other.getWidth() || this.getLength() > other.getLength() || this.getHeight() > other.getHeight()) {
			// This object is bigger than the other
			return 1;
		} else if (this.getWidth() == other.getWidth() && this.getLength() == other.getLength() && this.getHeight() == other.getHeight()) {
			return 0;
		}
		return -1;
	}
}
