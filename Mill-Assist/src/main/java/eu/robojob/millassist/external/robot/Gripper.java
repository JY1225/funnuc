package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.workpiece.WorkPiece;

public class Gripper {
	
	public enum Type {
		TWOPOINT, VACUUM
	}
	
	private int id;
	private Type type;
	private String name;
	private float height;
	private boolean fixedHeight;
	private String description;
	private String imageUrl;
	private WorkPiece workPiece;
	
	public Gripper(final String name, final Type type, final float height, final String description, final String imageUrl) {
		this.name = name;
		this.type = type;
		this.height = height;
		this.description = description;
		this.imageUrl = imageUrl;
		this.fixedHeight = false;
		this.workPiece = null;
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public WorkPiece getWorkPiece() {
		return workPiece;
	}
	
	public void setWorkPiece(final WorkPiece workPiece) {
		this.workPiece = workPiece;
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

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isFixedHeight() {
		return fixedHeight;
	}

	public void setFixedHeight(final boolean fixedHeight) {
		this.fixedHeight = fixedHeight;
	}

}
