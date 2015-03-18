package eu.robojob.millassist.ui.shape;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public interface IDrawableObject {

	public Shape createShape();
	public boolean needsMarkers();
	public float getXCorrection();
	public float getYCorrection();
	//TODO - use Decorator pattern for marker (return Group)
	public Rectangle createMarker(boolean length);
	public float getXTranslationMarker();
	public float getYTranslationMarker();
	
}
