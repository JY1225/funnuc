package eu.robojob.millassist.ui.shape;

import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectanglePieceRepresentation implements IDrawableObject {

	private WorkPiece workPiece;
	private static final String CSS_CLASS_WORKPIECE  = "workpiece";
	private static final String CSS_CLASS_FINISHED = "finished";
	private static final String CSS_CLASS_WORKPIECE_MARK = "workpiece-mark";
	private static final String CSS_CLASS_FINISHED_MARK = "workpiece-finished-mark";
	
	public RectanglePieceRepresentation(WorkPiece workPiece) {
		this.workPiece = workPiece;
	}
	
	@Override
	public Shape createShape() {
		Rectangle drawnWP =  new Rectangle(0,0, workPiece.getDimensions().getDimension(Dimensions.LENGTH), 
				workPiece.getDimensions().getDimension(Dimensions.WIDTH));
		//no rounded corners
		drawnWP.setArcHeight(0);
		drawnWP.setArcWidth(0);
		drawnWP.getStyleClass().add(CSS_CLASS_WORKPIECE);
		if (workPiece.getType().equals(Type.FINISHED)) {
			drawnWP.getStyleClass().add(CSS_CLASS_FINISHED);
		}
		return drawnWP;
	}

	@Override
	public boolean needsMarkers() {
		return true;
	}

	@Override
	public float getXCorrection() {
		return workPiece.getDimensions().getDimension(Dimensions.LENGTH) / 2;
	}

	@Override
	public float getYCorrection() {
		return workPiece.getDimensions().getDimension(Dimensions.WIDTH) / 2;
	}

	@Override
	public Rectangle createMarker(boolean length) {
		Rectangle marker = null;
		if (length) {
			marker = new Rectangle (0,0, workPiece.getDimensions().getDimension(Dimensions.LENGTH),5);
		} else {
			marker = new Rectangle (0,0,5, workPiece.getDimensions().getDimension(Dimensions.WIDTH));
		}
		marker.getStyleClass().add(CSS_CLASS_WORKPIECE_MARK);
		if (workPiece.getType().equals(Type.FINISHED)) {
			marker.getStyleClass().add(CSS_CLASS_FINISHED_MARK);
		}
		return marker;
	}
	
	public float getXTranslationMarker() {
		return workPiece.getDimensions().getDimension(Dimensions.LENGTH) - 10;
	}
	
	public float getYTranslationMarker() {
		return workPiece.getDimensions().getDimension(Dimensions.WIDTH) - 10;
	}

}
