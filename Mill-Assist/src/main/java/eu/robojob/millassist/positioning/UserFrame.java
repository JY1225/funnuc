package eu.robojob.millassist.positioning;

public class UserFrame {

	private int id; 
	private String name;
	private int number;
	private float zSafeDistance;
	private Coordinates location;
	
	public UserFrame(final int number, final String name, final float zSafeDistance, final Coordinates location) {
		this.number = number;
		this.name = name;
		this.zSafeDistance = zSafeDistance;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
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
