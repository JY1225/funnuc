package eu.robojob.irscw.positioning;

public class Coordinates {
	
	private float x;
	private float y;
	private float z;
	private float w;
	private float p;
	private float r;
	
	public Coordinates(float x, float y, float z, float w, float p, float r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.p = p;
		this.r = r;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public float getP() {
		return p;
	}

	public void setP(float p) {
		this.p = p;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}
	
}
