package eu.robojob.irscw.external.device;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.positioning.Coordinates;

public class Clamping {
	
	enum Type {
		CENTRUM, FIXED
	}
	
	private String id;
	private Coordinates relativePosition;
	private Coordinates smoothToPoint;
	private Coordinates smoothFromPoint;
	private float height;
	private String imageURL;
	private Type type;
	
	private Set<Clamping> relatedClampings;
	
	public Clamping(Type type, String id, float height, Coordinates relativePosition, Coordinates smoothToPoint,
				Coordinates smoothFromPoint, String imageURL) {
		this.id = id;
		this.height = height;
		this.relativePosition = relativePosition;
		this.smoothToPoint = smoothToPoint;
		this.smoothFromPoint = smoothFromPoint;
		this.imageURL = imageURL;
		this.relatedClampings = new HashSet<Clamping>();
		this.type = type;
	}
	
	public Clamping(Type type, String id, float height, Coordinates relativePosition, Coordinates smoothPoint, String imageURL) {
		this(type, id, height, relativePosition, smoothPoint, smoothPoint, imageURL);
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void addRelatedClamping(Clamping clamping) {
		relatedClampings.add(clamping);
	}
	
	public void removeRelatedClamping(Clamping clamping) {
		relatedClampings.remove(clamping);
	}
	
	public Set<Clamping> getRelatedClampings() {
		return relatedClampings;
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

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
}
