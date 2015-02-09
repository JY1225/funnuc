package eu.robojob.millassist.external.device.stacking.stackplate.gridplate;

public class GridHole implements Comparable<GridHole> {
	
	private float X, Y;
	private float angle;
	
	public GridHole(float X, float Y, float angle) {
		this.X = X;
		this.Y = Y;
		this.angle = angle;
	}

	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}

	public float getY() {
		return Y;
	}

	public void setY(float y) {
		Y = y;
	}
	
	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	@Override
	public int compareTo(GridHole hole) {
		if (this.getY() < hole.getY()) {
			return -1;
		} 
		if (this.getY() == hole.getY()) {
			if (this.getX() < hole.getX()) {
				return -1;
			} 
			if (this.getX() == hole.getX()) {
				return 0;
			}
			return 1;
		}
		return 1;
	}
	
}
