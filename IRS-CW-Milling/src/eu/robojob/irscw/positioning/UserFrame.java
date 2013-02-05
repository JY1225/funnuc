package eu.robojob.irscw.positioning;

public class UserFrame {

	private int id; 
	private int number;
	private float zSafeDistance;
	
	public UserFrame(final int number, final float zSafeDistance) {
		this.number = number;
		this.zSafeDistance = zSafeDistance;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	public float getzSafeDistance() {
		return zSafeDistance;
	}

	public void setzSafeDistance(final float zSafeDistance) {
		this.zSafeDistance = zSafeDistance;
	}
		
}
