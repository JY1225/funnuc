package eu.robojob.irscw.positioning;

public class UserFrame {

	private int id; 
	private int number;
	private float zSafeDistance;
	private Coordinates location;
	
	public UserFrame(final int number, final float zSafeDistance, final Coordinates location) {
		this.number = number;
		this.zSafeDistance = zSafeDistance;
		this.location = location;
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

	public Coordinates getLocation() {
		return location;
	}

	public void setLocation(final Coordinates location) {
		this.location = location;
	}

}
