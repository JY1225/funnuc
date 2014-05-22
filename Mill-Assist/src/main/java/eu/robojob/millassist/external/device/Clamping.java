package eu.robojob.millassist.external.device;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.positioning.Coordinates;

public class Clamping {
	
	public static enum Type {
		CENTRUM, FIXED_XP, FIXED_XM, FIXED_YP, FIXED_YM, NONE
	} 
	
	private int id;
	private String name;
	private Coordinates relativePosition;
	private Coordinates smoothToPoint;
	private Coordinates smoothFromPoint;
	private float height;
	private float defaultHeight;
	private String imageURL;
	private Type type;
	
	private Set<Clamping> relatedClampings;
	
	public Clamping(final Type type, final String name, final float defaultHeight, final Coordinates relativePosition, final Coordinates smoothToPoint,
				final Coordinates smoothFromPoint, final String imageURL) {
		this.name = name;
		this.height = defaultHeight;
		this.defaultHeight = defaultHeight;
		this.relativePosition = relativePosition;
		this.smoothToPoint = smoothToPoint;
		this.smoothFromPoint = smoothFromPoint;
		this.imageURL = imageURL;
		this.relatedClampings = new HashSet<Clamping>();
		this.type = type;
	}
	
	public Clamping(final Type type, final String name, final float defaultHeight, final Coordinates relativePosition, final Coordinates smoothPoint, final String imageURL) {
		this(type, name, defaultHeight, relativePosition, smoothPoint, smoothPoint, imageURL);
	}
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public void addRelatedClamping(final Clamping clamping) {
		relatedClampings.add(clamping);
	}
	
	public void removeRelatedClamping(final Clamping clamping) {
		relatedClampings.remove(clamping);
	}
	
	public Set<Clamping> getRelatedClampings() {
		return relatedClampings;
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public Coordinates getRelativePosition() {
		return relativePosition;
	}

	public void setRelativePosition(final Coordinates relativePosition) {
		this.relativePosition = relativePosition;
	}

	public String getImageUrl() {
		return imageURL;
	}

	public void setImageUrl(final String imageURL) {
		this.imageURL = imageURL;
	}

	public Coordinates getSmoothToPoint() {
		return smoothToPoint;
	}

	public void setSmoothToPoint(final Coordinates smoothToPoint) {
		this.smoothToPoint = smoothToPoint;
	}

	public Coordinates getSmoothFromPoint() {
		return smoothFromPoint;
	}

	public void setSmoothFromPoint(final Coordinates smoothFromPoint) {
		this.smoothFromPoint = smoothFromPoint;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}
	
	public float getDefaultHeight() {
		return defaultHeight;
	}

	public void setDefaultHeight(final float defaultHeight) {
		this.defaultHeight = defaultHeight;
	}
	
	public void resetHeightToDefault() {
		this.height = defaultHeight;
	}
}
