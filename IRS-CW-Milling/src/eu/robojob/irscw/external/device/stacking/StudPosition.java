package eu.robojob.irscw.external.device.stacking;

import eu.robojob.irscw.positioning.Coordinates;

public class StudPosition {
	
	public enum StudType {
		NONE, NORMAL, HORIZONTAL_CORNER, TILTED_CORNER
	}

	private Coordinates centerPosition;
	
	private int columnIndex;
	private int rowIndex;
	
	private StudType studType;
	
	public StudPosition(int columnIndex, int rowIndex, Coordinates centerPosition, StudType studType) {
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
		this.centerPosition = centerPosition;
		this.studType = studType;
	}
	
	public StudPosition(int columnIndex, int rowIndex, float x, float y, StudType studType) {
		this(columnIndex, rowIndex, new Coordinates(x, y, 0, 0, 0, 0), studType);
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

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
}
