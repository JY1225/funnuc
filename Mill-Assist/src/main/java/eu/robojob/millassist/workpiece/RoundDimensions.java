package eu.robojob.millassist.workpiece;

import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;

public class RoundDimensions implements IWorkPieceDimensions {
	
	private float height;
	private float diameter;
		
	public RoundDimensions(final float diameter, final float height) {
		this.diameter = diameter;
		this.height = height;
	}
	
	public RoundDimensions(final RoundDimensions dimensions) {
		this(dimensions.getDiameter(), dimensions.getHeight());
	}
	
	public RoundDimensions() {
		this(-1, -1);
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public float getDiameter() {
		return diameter;
	}

	public void setDiameter(final float diameter) {
		this.diameter = diameter;
	}
	
	public boolean isValidDimension() {
		return (diameter > 0 && height > 0);
	}

	public float getVolume() {
		return (diameter/2)*(diameter/2)* (float) Math.PI*height;
	}

	public String toString() {
		return "(" + diameter + ", " + height + ")";
	}

	@Override
	public float getZSafe() {
		return getHeight();
	}

	@Override
	public void rotateDimensionsAroundX() {
		//Do nothing
	}

	@Override
	public void rotateDimensionsAroundY() {
		//Do nothing
	}
	
	public RoundDimensions clone() {
		return new RoundDimensions(this);
	}
	
	@Override
	public boolean hasSameDimensions(IWorkPieceDimensions dimensions) {
		if (!(dimensions instanceof RoundDimensions)) {
			throw new IllegalArgumentException("Cannot compare round dimensions with other types");
		}
		if (this.getDiameter() == ((RoundDimensions) dimensions).getDiameter()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void setDimension(Dimensions dimension, float value) {
		switch (dimension) {
		case DIAMETER:
			diameter = value;
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
		case DIAMETER:
			return diameter;
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
		if (!(otherDimensions instanceof RoundDimensions)) {
			return -99;
		}
		RoundDimensions other = (RoundDimensions) otherDimensions;
		if (this.getDiameter() > other.getDiameter() || this.getHeight() > other.getHeight()) {
			// This object is bigger than the other
			return 1;
		} else if (this.getDiameter() == other.getDiameter() && this.getHeight() == other.getHeight()) {
			return 0;
		}
		return -1;
	}
}
