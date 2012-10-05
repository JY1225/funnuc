package eu.robojob.irscw.external.robot;

public class Gripper {
	
	private String id;
	private float height;
	private boolean fixedHeight;
	private String description;
	private String imageUrl;
	
	public Gripper(String id, float height, String description, String imageUrl) {
		this.id = id;
		this.height = height;
		this.description = description;
		this.imageUrl = imageUrl;
		this.fixedHeight = false;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isFixedHeight() {
		return fixedHeight;
	}

	public void setFixedHeight(boolean fixedHeight) {
		this.fixedHeight = fixedHeight;
	}

}
