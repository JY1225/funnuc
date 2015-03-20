package eu.robojob.millassist.workpiece;

import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;

public interface IWorkPieceDimensions extends Comparable<IWorkPieceDimensions>, Cloneable {
	
	public float getZSafe();
	public String toString();
	public float getVolume();
	public boolean isValidDimension();
	public void rotateDimensionsAroundX();
	public void rotateDimensionsAroundY();
	public IWorkPieceDimensions clone();
	public boolean hasSameDimensions(IWorkPieceDimensions dimensions);
	public void setDimension(Dimensions dimension, float value);
	public float getDimension(Dimensions dimension);

}
