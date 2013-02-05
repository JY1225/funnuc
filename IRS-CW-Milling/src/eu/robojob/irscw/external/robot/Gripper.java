package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.workpiece.WorkPiece;

public class Gripper {
	
	private int id;
	private String name;
	private float height;
	private boolean fixedHeight;
	private String description;
	private String imageUrl;
	private WorkPiece workPiece;
	
	public Gripper(final String name, final float height, final String description, final String imageUrl) {
		this.name = name;
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
