package eu.robojob.irscw.external.robot;

public class Gripper {
	
	private String id;
	private float height;
	private String description;
	
	public Gripper(String id, float height, String description) {
		this.id = id;
		this.height = height;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
