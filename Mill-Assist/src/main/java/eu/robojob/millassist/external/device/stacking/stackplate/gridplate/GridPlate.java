package eu.robojob.millassist.external.device.stacking.stackplate.gridplate;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

public class GridPlate {
	
	private String name;
	private SortedSet<GridHole> gridHoles;
	private float width, height;
	private float depth;
	private float offsetX, offsetY;
	private float holeLength, holeWidth;
	private int id;
	
	private static final String CSS_CLASS_GRIDPLATE = "gridplate";
	private static final String CSS_CLASS_STACKPLATE = "stackplate"; 
	
	public GridPlate(String name, float width, float height) {
		this(name, width, height, new TreeSet<GridHole>());
	}
	
	public GridPlate(String name, float width, float height, SortedSet<GridHole> gridHoles) {
		this.gridHoles = gridHoles;
		this.width = width;
		this.height = height;
		this.name = name;
	}
	
	public void addHole(GridHole hole) {
		gridHoles.add(hole);
	}
	
	public void setGridHoles(SortedSet<GridHole> gridHoleList) {
		this.gridHoles = gridHoleList;
	}
	
	public SortedSet<GridHole> getGridHoles() {
		return this.gridHoles;
	}
	
	public Shape createShape() {
		Shape gridPlate = getGridPlate();
		for (Rectangle hole: getGridHoleShapes()) {
			gridPlate = Shape.subtract(gridPlate, hole);
		}
		gridPlate.getStyleClass().add(CSS_CLASS_GRIDPLATE);
		return gridPlate;
	}
	
	private Rectangle getGridPlate() {
		Rectangle r = new Rectangle(0, 0, width, height);
		r.setArcHeight(10);
		r.setArcWidth(10);
		r.setStrokeWidth(0);
		return r;
	}
	
	private List<Rectangle> getGridHoleShapes() {
		List<Rectangle> gridHoleShapes = new ArrayList<Rectangle>();
		for (GridHole hole: gridHoles) {
			double xPos = hole.getX();
			double yPos = height - hole.getY() - holeWidth;
			Rectangle newHole = new Rectangle(xPos, yPos, holeLength, holeWidth);
			if (hole.getAngle() != 0) {
				Rotate rotate = new Rotate(hole.getAngle()*-1, xPos, yPos + holeWidth);
				newHole.getTransforms().add(rotate);
			}
			newHole.getStyleClass().add(CSS_CLASS_STACKPLATE);
			gridHoleShapes.add(newHole);
		}
		return gridHoleShapes;
	}
	
	public String getName() {
		return this.name;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}
	
	public float getDepth() {
		return this.depth;
	}
	
	public void setDepth(float depth) {
		this.depth = depth;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public float getHoleLength() {
		return this.holeLength;
	}
	
	public float getHoleWidth() {
		return this.holeWidth;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setHoleLength(float holeLength) {
		this.holeLength = holeLength;
	}

	public void setHoleWidth(float holeWidth) {
		this.holeWidth = holeWidth;
	}

	@Override
	public String toString() {
		return name;
	}
}
