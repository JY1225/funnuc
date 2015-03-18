package eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate;

import eu.robojob.millassist.positioning.Coordinates;

public class StudPosition {
	
	public enum StudType {
		NONE, NORMAL, HORIZONTAL_CORNER, HORIZONTAL_CORNER_LEFT, TILTED_CORNER
	}

	private Coordinates centerPosition;
	
	private int columnIndex;
	private int rowIndex;
	
	private StudType studType;
	
	public StudPosition(final int columnIndex, final int rowIndex, final Coordinates centerPosition, final StudType studType) {
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
		this.centerPosition = centerPosition;
		this.studType = studType;
	}
	
	public StudPosition(final int columnIndex, final int rowIndex, final float x, final float y, final StudType studType) {
		this(columnIndex, rowIndex, new Coordinates(x, y, 0, 0, 0, 0), studType);
	}

	public Coordinates getCenterPosition() {
		return centerPosition;
	}

	public void setCenterPosition(final Coordinates centerPosition) {
		this.centerPosition = centerPosition;
	}

	public StudType getStudType() {
		return studType;
	}

	public void setStudType(final StudType studType) {
		this.studType = studType;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(final int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(final int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	@Override
	public String toString() {
		return "Stud["+ studType.toString() + "] at " + centerPosition.toString();
	}
	
}
