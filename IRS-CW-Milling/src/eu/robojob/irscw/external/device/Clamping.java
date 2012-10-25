package eu.robojob.irscw.external.device;

import eu.robojob.irscw.positioning.Coordinates;

public class Clamping {
	
	private String id;
	private WorkArea correspondingWorkArea;
	private Coordinates relativePosition;
	private Coordinates smoothToPoint;
	private Coordinates smoothFromPoint;
	private float height;
	private String imageURL;
	
	public Clamping(String id, float height, Coordinates relativePosition, Coordinates smoothToPoint,
				Coordinates smoothFromPoint, String imageURL) {
		this.id = id;
		this.height = height;
		this.relativePosition = relativePosition;
		this.smoothToPoint = smoothToPoint;
		this.smoothFromPoint = smoothFromPoint;
		this.imageURL = imageURL;
	}
	
	public Clamping(String id, float height, Coordinates relativePosition, Coordinates smoothPoint,
			String imageURL) {
		this(id, height, relativePosition, smoothPoint, smoothPoint, imageURL);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Coordinates getRelativePosition() {
		return relativePosition;
	}

	public void setRelativePosition(Coordinates relativePosition) {
		this.relativePosition = relativePosition;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Coordinates getSmoothToPoint() {
		return smoothToPoint;
	}

	public void setSmoothToPoint(Coordinates smoothToPoint) {
		this.smoothToPoint = smoothToPoint;
	}

	public Coordinates getSmoothFromPoint() {
		return smoothFromPoint;
	}

	public void setSmoothFromPoint(Coordinates smoothFromPoint) {
		this.smoothFromPoint = smoothFromPoint;
	}

	public WorkArea getCorrespondingWorkArea() {
		return correspondingWorkArea;
	}

	public void setCorrespondingWorkArea(WorkArea correspondingWorkArea) {
		this.correspondingWorkArea = correspondingWorkArea;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
}
