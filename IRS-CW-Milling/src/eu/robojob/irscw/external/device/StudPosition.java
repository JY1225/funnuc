package eu.robojob.irscw.external.device;

import eu.robojob.irscw.positioning.Coordinates;

public class StudPosition {
	
	public enum StudType {
		NONE, NORMAL, HORIZONTAL_CORNER
	}

	private Coordinates centerPosition;
	private StudType studType;
	
	public StudPosition(Coordinates centerPosition, StudType studType) {
		this.centerPosition = centerPosition;
		this.studType = studType;
	}
	
	public StudPosition(float x, float y, StudType studType) {
		this(new Coordinates(x, y, 0, 0, 0, 0), studType);
	}

	public Coordinates getCenterPosition() {
		return centerPosition;
	}

	public void setCenterPosition(Coordinates centerPosition) {
		this.centerPosition = centerPosition;
	}

	public StudType getStudType() {
		return studType;
	}

	public void setStudType(StudType studType) {
		this.studType = studType;
	}
	
}
