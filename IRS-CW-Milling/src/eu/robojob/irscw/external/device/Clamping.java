package eu.robojob.irscw.external.device;

import eu.robojob.irscw.positioning.Coordinates;

public class Clamping {
	
	private Coordinates relativePosition;
	private Coordinates smoothToPoint;
	private Coordinates smoothFromPoint;
	private String imageURL;
	
	public Clamping(Coordinates relativePosition, Coordinates smoothToPoint,
				Coordinates smoothFromPoint, String imageURL) {
		this.relativePosition = relativePosition;
		this.smoothToPoint = smoothToPoint;
		this.smoothFromPoint = smoothFromPoint;
		this.imageURL = imageURL;
	}
	
	public Clamping(Coordinates relativePosition, Coordinates smoothPoint,
			String imageURL) {
		this(relativePosition, smoothPoint, smoothPoint, imageURL);
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

}
