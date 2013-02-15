package eu.robojob.irscw.ui.configure.device.stacking;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateLayout;
import eu.robojob.irscw.external.device.stacking.StackingPosition;
import eu.robojob.irscw.external.device.stacking.StudPosition;
import eu.robojob.irscw.external.device.stacking.StudPosition.StudType;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.workpiece.WorkPiece.Type;

public class BasicStackPlateLayoutView extends AbstractFormView<BasicStackPlateLayoutPresenter> {

	private BasicStackPlateLayout basicStackPlateLayout;
	
	private Group group;
	private Rectangle stackPlate;
	private Pane root;
	
	private ClampingManner clampingManner;
	
	private List<Circle> holes;
	private List<Circle> studs;
	private List<Text> horizontalLabels;
	private List<Text> verticalLabels;
	
	private static final float TXT_WIDTH = 40;
	private static final float TXT_HEIGHT = 15;
		
	private float width;
	
	private static final String CORNER_PATH = "m 2.75,0 -0.09375,0.03125 -0.125,0 L 2.40625,0.0625 2.3125,0.09375 2.1875,0.125 2.09375,0.15625 1.96875,0.1875 1.875,0.21875 1.75,0.25 1.65625,0.3125 1.5625,0.375 1.46875,0.40625 1.34375,0.46875 1.25,0.5625 1.1875,0.625 1.0625,0.6875 1,0.75 0.90625,0.84375 0.84375,0.9375 0.75,1 0.65625,1.09375 0.59375,1.1875 0.53125,1.28125 0.46875,1.375 0.40625,1.46875 0.34375,1.5625 0.3125,1.6875 0.25,1.78125 0.1875,1.875 0.15625,2 0.125,2.09375 0.09375,2.21875 0.0625,2.3125 l -0.03125,0.125 0,0.09375 L 0,2.65625 0,2.78125 0,2.875 0,3 l 0,38 0,0.28125 0,0.25 0.03125,0.28125 0.03125,0.25 0.0625,0.28125 0.03125,0.25 0.09375,0.28125 0.0625,0.25 0.09375,0.25 0.09375,0.25 0.09375,0.25 0.125,0.25 0.125,0.21875 0.125,0.25 0.15625,0.21875 0.125,0.21875 0.15625,0.21875 0.1875,0.21875 0.15625,0.1875 0.1875,0.21875 0.1875,0.1875 0.21875,0.15625 0.1875,0.1875 0.21875,0.15625 0.21875,0.1875 0.21875,0.125 0.21875,0.15625 0.25,0.125 0.21875,0.125 0.25,0.125 0.25,0.09375 0.25,0.09375 0.25,0.09375 0.25,0.0625 0.25,0.0625 0.28125,0.0625 0.25,0.0625 L 6.1875,47.96875 6.46875,48 6.71875,48 7,48 l 54.5,0 0.125,0 0.09375,0 0.125,0 0.125,-0.03125 0.09375,-0.03125 0.125,0 0.09375,-0.03125 0.125,-0.03125 0.09375,-0.03125 0.125,-0.0625 0.09375,-0.03125 0.09375,-0.0625 0.125,-0.03125 0.09375,-0.0625 0.09375,-0.0625 0.09375,-0.0625 0.09375,-0.0625 L 63.40625,47.3125 63.5,47.25 63.5625,47.15625 63.65625,47.09375 63.75,47 63.8125,46.90625 63.875,46.8125 63.9375,46.75 64,46.625 l 0.0625,-0.09375 0.0625,-0.09375 0.0625,-0.09375 0.03125,-0.125 0.0625,-0.09375 0.03125,-0.09375 0.03125,-0.125 0.03125,-0.09375 0.03125,-0.125 0.03125,-0.09375 0.03125,-0.125 0,-0.125 L 64.5,45.25 l 0,-0.125 0,-0.125 0,-11 0,-0.03125 0,-0.03125 0,-0.03125 0,-0.0625 -0.03125,-0.03125 0,-0.03125 0,-0.03125 -0.03125,-0.03125 0,-0.0625 0,-0.03125 -0.03125,-0.03125 -0.03125,-0.03125 0,-0.03125 -0.03125,-0.03125 0,-0.03125 L 64.3125,33.4375 64.28125,33.40625 64.25,33.375 64.25,33.34375 64.21875,33.3125 64.1875,33.28125 64.15625,33.25 64.125,33.21875 l -0.03125,0 -0.03125,-0.03125 -0.03125,-0.03125 -0.03125,0 -0.03125,-0.03125 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.0625,-0.03125 -0.03125,0 -0.03125,0 -0.03125,0 L 63.625,33 63.5625,33 63.53125,33 63.5,33 l -47,0 0,0.09375 -0.03125,0.0625 0,0.09375 0,0.0625 -0.03125,0.09375 -0.03125,0.0625 -0.03125,0.09375 -0.03125,0.0625 -0.03125,0.0625 -0.03125,0.09375 -0.03125,0.0625 -0.0625,0.0625 -0.03125,0.0625 -0.0625,0.0625 -0.0625,0.0625 -0.0625,0.03125 -0.0625,0.0625 -0.0625,0.0625 -0.0625,0.03125 -0.0625,0.03125 -0.09375,0.0625 -0.0625,0.03125 -0.0625,0 -0.09375,0.03125 -0.0625,0.03125 -0.09375,0 -0.0625,0.03125 -0.09375,0 -0.09375,0 -0.0625,0 -0.09375,0 -0.0625,0 -0.09375,-0.03125 -0.0625,0 L 14.53125,34.4375 14.46875,34.40625 14.375,34.375 14.3125,34.34375 14.25,34.3125 14.1875,34.25 14.125,34.21875 14.0625,34.1875 14,34.125 13.9375,34.0625 13.875,34 13.8125,33.9375 13.78125,33.875 13.71875,33.8125 13.6875,33.75 13.65625,33.6875 13.625,33.59375 13.59375,33.53125 13.5625,33.4375 13.53125,33.375 l 0,-0.0625 -0.03125,-0.09375 0,-0.0625 0,-0.09375 0,-0.09375 0,-0.0625 0,-0.09375 0,-0.0625 0.03125,-0.09375 0.03125,-0.09375 0,-0.0625 0.03125,-0.0625 0.03125,-0.09375 0.0625,-0.0625 0.03125,-0.0625 0.03125,-0.0625 0.0625,-0.0625 0.03125,-0.0625 0.0625,-0.0625 0.0625,-0.0625 0.0625,-0.0625 0.0625,-0.03125 0.0625,-0.0625 0.0625,-0.03125 0.09375,-0.03125 0.0625,-0.03125 0.0625,-0.0625 0.09375,0 0.0625,-0.03125 0.0625,-0.03125 0.09375,0 0.0625,-0.03125 0.09375,0 L 15,31.5 15,1 15,0.96875 15,0.90625 15,0.875 14.96875,0.84375 l 0,-0.03125 0,-0.03125 L 14.9375,0.75 l 0,-0.0625 0,-0.03125 -0.03125,-0.03125 0,-0.03125 L 14.875,0.5625 14.875,0.53125 14.84375,0.5 14.8125,0.46875 14.8125,0.4375 14.78125,0.40625 14.75,0.375 14.75,0.34375 14.71875,0.3125 14.6875,0.28125 14.65625,0.25 14.625,0.21875 l -0.03125,0 L 14.5625,0.1875 14.53125,0.15625 14.5,0.15625 14.46875,0.125 14.4375,0.09375 l -0.03125,0 -0.03125,-0.03125 -0.0625,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 L 14.125,0 14.09375,0 14.0625,0 14.03125,0 14,0 3,0 2.875,0 2.75,0 z m 4.59375,10.75 0.28125,0 0.25,0.03125 0.28125,0.03125 0.25,0.03125 0.25,0.0625 0.28125,0.0625 0.25,0.09375 0.25,0.09375 0.25,0.09375 0.21875,0.125 0.25,0.125 0.21875,0.15625 0.1875,0.15625 0.21875,0.1875 0.1875,0.15625 0.1875,0.21875 0.1875,0.1875 L 11.5,12.78125 11.65625,13 l 0.125,0.21875 0.15625,0.21875 0.09375,0.25 0.125,0.25 0.0625,0.25 0.09375,0.25 0.0625,0.25 0.0625,0.28125 0.03125,0.25 0,0.28125 0.03125,0.25 -0.03125,0.28125 0,0.25 -0.03125,0.28125 -0.0625,0.25 -0.0625,0.25 -0.09375,0.25 -0.0625,0.25 -0.125,0.25 -0.09375,0.25 -0.15625,0.21875 -0.125,0.25 L 11.5,18.75 l -0.15625,0.1875 -0.1875,0.21875 -0.1875,0.1875 L 10.78125,19.5 10.5625,19.6875 10.375,19.84375 10.15625,20 l -0.25,0.125 -0.21875,0.125 -0.25,0.125 -0.25,0.09375 -0.25,0.0625 -0.28125,0.0625 -0.25,0.0625 -0.25,0.0625 L 7.875,20.75 l -0.25,0 -0.28125,0 -0.25,0 -0.28125,-0.03125 -0.25,-0.0625 -0.25,-0.0625 -0.28125,-0.0625 -0.25,-0.0625 L 5.5625,20.375 5.3125,20.25 5.0625,20.125 4.84375,20 4.625,19.84375 4.40625,19.6875 4.1875,19.5 4,19.34375 3.8125,19.15625 3.65625,18.9375 3.46875,18.75 3.3125,18.53125 l -0.125,-0.25 -0.125,-0.21875 -0.125,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.03125,-0.25 -0.0625,-0.28125 0,-0.25 0,-0.28125 0,-0.25 0,-0.28125 0.0625,-0.25 0.03125,-0.28125 0.0625,-0.25 0.09375,-0.25 0.09375,-0.25 0.09375,-0.25 0.125,-0.25 0.125,-0.21875 L 3.3125,13 3.46875,12.78125 3.65625,12.5625 3.8125,12.375 4,12.15625 4.1875,12 4.40625,11.8125 4.625,11.65625 4.84375,11.5 5.0625,11.375 l 0.25,-0.125 0.25,-0.09375 0.21875,-0.09375 0.25,-0.09375 0.28125,-0.0625 0.25,-0.0625 0.25,-0.03125 0.28125,-0.03125 0.25,-0.03125 z m 0,24.75 0.28125,0 0.25,0.03125 0.28125,0.03125 0.25,0.03125 0.25,0.0625 0.28125,0.0625 0.25,0.09375 0.25,0.09375 0.25,0.09375 0.21875,0.125 0.25,0.125 0.21875,0.15625 0.1875,0.15625 0.21875,0.1875 0.1875,0.1875 0.1875,0.1875 0.1875,0.1875 0.15625,0.21875 0.15625,0.21875 0.125,0.21875 0.15625,0.21875 0.09375,0.25 0.125,0.25 0.0625,0.25 0.09375,0.25 0.0625,0.25 0.0625,0.28125 0.03125,0.25 0,0.28125 0.03125,0.25 -0.03125,0.28125 0,0.25 -0.03125,0.28125 -0.0625,0.25 -0.0625,0.25 -0.09375,0.25 -0.0625,0.25 -0.125,0.25 -0.09375,0.25 -0.15625,0.25 -0.125,0.21875 L 11.5,43.5 l -0.15625,0.1875 -0.1875,0.21875 -0.1875,0.1875 L 10.78125,44.25 10.5625,44.4375 10.375,44.59375 10.15625,44.75 9.90625,44.875 9.6875,45 l -0.25,0.125 -0.25,0.09375 -0.25,0.09375 -0.28125,0.0625 -0.25,0.03125 -0.25,0.0625 L 7.875,45.5 l -0.25,0 -0.28125,0 -0.25,0 L 6.8125,45.46875 6.5625,45.40625 6.3125,45.375 6.03125,45.3125 5.78125,45.21875 5.5625,45.125 5.3125,45 5.0625,44.875 4.84375,44.75 4.625,44.59375 4.40625,44.4375 4.1875,44.25 4,44.09375 3.8125,43.90625 3.65625,43.6875 3.46875,43.5 3.3125,43.28125 3.1875,43.0625 l -0.125,-0.25 -0.125,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.03125,-0.25 -0.0625,-0.28125 0,-0.25 0,-0.28125 0,-0.25 0,-0.28125 0.0625,-0.25 0.03125,-0.28125 0.0625,-0.25 0.09375,-0.25 0.09375,-0.25 0.09375,-0.25 0.125,-0.25 0.125,-0.21875 L 3.3125,37.75 3.46875,37.53125 3.65625,37.3125 3.8125,37.125 4,36.9375 4.1875,36.75 4.40625,36.5625 4.625,36.40625 4.84375,36.25 5.0625,36.125 5.3125,36 l 0.25,-0.09375 0.21875,-0.09375 0.25,-0.09375 0.28125,-0.0625 0.25,-0.0625 0.25,-0.03125 0.28125,-0.03125 0.25,-0.03125 z m 24.6875,0 0.25,0 0.28125,0 0.25,0.03125 0.25,0.03125 0.25,0.0625 0.28125,0.0625 0.21875,0.0625 0.25,0.09375 0.25,0.125 0.21875,0.09375 0.25,0.125 0.21875,0.125 0.1875,0.15625 0.21875,0.15625 0.1875,0.1875 0.1875,0.15625 0.1875,0.21875 0.03125,0 0,0.03125 0.03125,0.03125 0.03125,0 0,0.03125 0.03125,0 0.03125,0.03125 0.03125,0.03125 0.03125,0 0.03125,0.03125 0.03125,0 0,0.03125 0.03125,0 0.03125,0 0.03125,0.03125 0.03125,0 0.03125,0.03125 0.03125,0 0.03125,0 0.03125,0 0.03125,0.03125 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 1.3125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,-0.03125 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,-0.03125 0.03125,-0.03125 0.03125,0 0.03125,0 L 38.5,37.375 l 0.03125,0 0,-0.03125 0.03125,0 0.03125,-0.03125 0.03125,0 0.03125,-0.03125 0.03125,-0.03125 0.03125,0 0,-0.03125 0.03125,-0.03125 0.03125,0 0.15625,-0.21875 0.1875,-0.15625 0.21875,-0.1875 0.1875,-0.15625 0.21875,-0.15625 0.21875,-0.125 0.21875,-0.125 0.25,-0.09375 0.21875,-0.125 0.25,-0.09375 0.25,-0.0625 0.25,-0.0625 0.25,-0.0625 0.25,-0.03125 0.28125,-0.03125 0.25,0 0.25,0 0.28125,0.03125 0.25,0.03125 0.25,0.03125 0.25,0.0625 0.25,0.0625 0.25,0.09375 0.25,0.09375 0.21875,0.125 0.25,0.09375 0.21875,0.125 0.21875,0.15625 0.1875,0.15625 0.21875,0.15625 0.1875,0.1875 0.15625,0.1875 0.1875,0.1875 0.15625,0.1875 0.15625,0.21875 0.15625,0.21875 0.125,0.25 L 47,38.375 l 0.125,0.25 0.09375,0.21875 0.0625,0.25 0.0625,0.25 0.0625,0.25 0.0625,0.25 0,0.28125 0.03125,0.25 0,0.25 -0.03125,0.28125 0,0.25 -0.0625,0.25 -0.0625,0.25 -0.0625,0.25 -0.0625,0.25 -0.09375,0.25 -0.125,0.25 -0.09375,0.21875 -0.125,0.21875 -0.15625,0.21875 -0.15625,0.21875 -0.15625,0.1875 -0.1875,0.1875 -0.15625,0.1875 -0.1875,0.1875 -0.21875,0.15625 -0.1875,0.15625 -0.21875,0.15625 -0.21875,0.125 -0.25,0.125 -0.21875,0.09375 -0.25,0.09375 -0.25,0.09375 -0.25,0.0625 -0.25,0.0625 -0.25,0.0625 -0.25,0 -0.28125,0.03125 -0.25,0 -0.25,0 -0.28125,-0.03125 -0.25,-0.03125 -0.25,-0.0625 -0.25,-0.0625 -0.25,-0.0625 -0.25,-0.09375 -0.21875,-0.09375 -0.25,-0.125 -0.21875,-0.125 L 39.75,44.6875 39.53125,44.53125 39.34375,44.375 39.125,44.21875 38.9375,44.03125 38.78125,43.84375 38.75,43.8125 l -0.03125,0 0,-0.03125 -0.03125,-0.03125 -0.03125,0 0,-0.03125 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,-0.03125 0,-0.03125 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -1.3125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0.03125 -0.03125,0.03125 -0.03125,0 -0.03125,0.03125 L 36.0625,43.75 36.03125,43.78125 36,43.8125 l -0.03125,0.03125 -0.1875,0.1875 -0.1875,0.1875 L 35.40625,44.375 35.1875,44.53125 35,44.6875 l -0.21875,0.125 -0.25,0.125 -0.21875,0.125 -0.25,0.09375 -0.25,0.09375 -0.21875,0.0625 -0.28125,0.0625 -0.25,0.0625 -0.25,0.03125 -0.25,0.03125 -0.28125,0 -0.25,0 -0.25,-0.03125 -0.25,0 -0.25,-0.0625 L 31,45.34375 30.75,45.28125 30.53125,45.1875 30.28125,45.09375 30.03125,45 29.8125,44.875 29.59375,44.75 29.375,44.59375 29.15625,44.4375 l -0.1875,-0.15625 -0.1875,-0.1875 -0.1875,-0.1875 -0.1875,-0.1875 -0.15625,-0.1875 -0.15625,-0.21875 -0.125,-0.21875 -0.125,-0.21875 -0.125,-0.21875 -0.09375,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.0625,-0.25 -0.03125,-0.25 -0.03125,-0.25 0,-0.28125 0,-0.25 0,-0.25 0.03125,-0.28125 0.03125,-0.25 0.0625,-0.25 0.0625,-0.25 0.09375,-0.25 0.09375,-0.21875 0.09375,-0.25 0.125,-0.21875 0.125,-0.25 0.125,-0.21875 0.15625,-0.21875 0.15625,-0.1875 0.1875,-0.1875 0.1875,-0.1875 0.1875,-0.1875 0.1875,-0.15625 0.21875,-0.15625 0.21875,-0.15625 0.21875,-0.125 0.21875,-0.09375 0.25,-0.125 0.25,-0.09375 0.21875,-0.09375 0.25,-0.0625 0.28125,-0.0625 0.25,-0.03125 0.25,-0.03125 0.25,-0.03125 z";
	
	private static final String CSS_CLASS_STACKER_TEXT = "stacker-text";
	private static final String CSS_CLASS_STACKPLATE = "stackplate"; 
	private static final String CSS_CLASS_LINE = "line";
	private static final String CSS_CLASS_HOLE = "hole";
	private static final String CSS_CLASS_NORMALSTUD = "normal-stud";
	private static final String CSS_CLASS_CORNERSHAPE = "corner-shape";
	private static final String CSS_CLASS_WORKPIECE  = "workpiece";
	private static final String CSS_CLASS_WORKPIECE_MARK = "workpiece-mark";
	private static final String CSS_CLASS_FINISHED = "finished";
	private static final String CSS_CLASS_FINISHED_MARK = "workpiece-finished-mark";
	
	public BasicStackPlateLayoutView() {
		super();
		this.holes = new ArrayList<Circle>();
		this.studs = new ArrayList<Circle>();
		this.horizontalLabels = new ArrayList<Text>();
		this.verticalLabels = new ArrayList<Text>();
	}
	
	public void setBasicStackPlate(final BasicStackPlate basicStackPlate) {
		this.basicStackPlateLayout = basicStackPlate.getLayout();
		this.width = basicStackPlateLayout.getWidth();
	}
	
	public void setClampingType(final ClampingManner clampingManner) {
		this.clampingManner = clampingManner;
	}
	
	public ClampingManner getClampingManner() {
		return clampingManner;
	}
	
	@Override
	protected void build() {
		group = new Group();
		
		group.getChildren().clear();
		
		// add plate
		stackPlate = new Rectangle(0, 0, basicStackPlateLayout.getLength(), basicStackPlateLayout.getWidth());
		stackPlate.getStyleClass().add(CSS_CLASS_STACKPLATE);
		
		group.getChildren().add(stackPlate);
		
		// add holes
		int index = 1;
		for (StudPosition[] horizontalPositions : basicStackPlateLayout.getStudPositions()) {
			Text txt = new Text(""  + (char) ('A' + (index - 1)));
			txt.setX(0);
			txt.setY(width - horizontalPositions[0].getCenterPosition().getY() + TXT_HEIGHT / 2);
			txt.setWrappingWidth(TXT_WIDTH);
			txt.getStyleClass().add(CSS_CLASS_STACKER_TEXT);
			group.getChildren().add(txt);
			verticalLabels.add(txt);
			int index2 = 1;
			for (StudPosition pos : horizontalPositions) {
				if (index == 1) {
					if (index2 % 2 == 0) {
						Text txt2 = new Text("" + index2);
						txt2.setX(pos.getCenterPosition().getX() - TXT_WIDTH / 2);
						txt2.setY(basicStackPlateLayout.getWidth() - TXT_HEIGHT / 8);
						txt2.setWrappingWidth(TXT_WIDTH);
						txt2.getStyleClass().add(CSS_CLASS_STACKER_TEXT);
						group.getChildren().add(txt2);
						horizontalLabels.add(txt2);
					} else {
						Text txt2 = new Text("\u00B7");
						txt2.setX(pos.getCenterPosition().getX() - TXT_WIDTH / 2);
						txt2.setY(basicStackPlateLayout.getWidth() - TXT_HEIGHT / 8);
						txt2.setWrappingWidth(TXT_WIDTH);
						txt2.getStyleClass().add(CSS_CLASS_STACKER_TEXT);
						group.getChildren().add(txt2);
						horizontalLabels.add(txt2);
						// draw line
						Path path = new Path();
						MoveTo moveTo = new MoveTo();
						moveTo.setX(pos.getCenterPosition().getX());
						moveTo.setY(width - pos.getCenterPosition().getY());
						LineTo lineTo = new LineTo();
						lineTo.setX(pos.getCenterPosition().getX());
						lineTo.setY(width - basicStackPlateLayout.getStudPositions()[basicStackPlateLayout.getStudPositions().length - 1][0].getCenterPosition().getY());
						path.getElements().add(moveTo);
						path.getElements().add(lineTo);
						path.getStyleClass().add(CSS_CLASS_LINE);
						group.getChildren().add(path);
					}
					index2++;
				}
				Circle hole = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(), basicStackPlateLayout.getHoleDiameter() / 2);
				holes.add(hole);
				hole.getStyleClass().add(CSS_CLASS_HOLE);
				group.getChildren().add(hole);
			}
			index++;
		}
		
		if (basicStackPlateLayout.getStackingPositions().size() > 0) {
			configureStuds();
			configureWorkPieces();
		}
		
		Scale s = new Scale(570 / group.getBoundsInParent().getWidth(), 300 / group.getBoundsInParent().getHeight());
		group.getTransforms().add(s);
		
		root = new Pane();
		root.setPrefSize(570, 300);
		root.getChildren().clear();
		root.getChildren().add(group);		
		
		group.setLayoutX(0 - group.getBoundsInParent().getMinX());
		group.setLayoutY(0 - group.getBoundsInParent().getMinY());
		
		this.getChildren().clear();
		
		this.add(root, 0, 0);
	}
	
	private void configureStuds() {
		for (StudPosition[] horizontalPositions : basicStackPlateLayout.getStudPositions()) {
			for (StudPosition pos : horizontalPositions) {
				if (pos.getStudType() == StudType.NORMAL) {
					Circle circle = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(), basicStackPlateLayout.getStudDiameter() / 2);
					circle.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle);
					group.getChildren().add(circle);
				} else if (pos.getStudType() == StudType.HORIZONTAL_CORNER) {
					SVGPath corner = new SVGPath();
					corner.setContent(CORNER_PATH);
					corner.getStyleClass().add(CSS_CLASS_CORNERSHAPE);
					Translate tr = new Translate();
					tr.setX(pos.getCenterPosition().getX() - 7.5);
					tr.setY(width - pos.getCenterPosition().getY() - 40.5);
					corner.getTransforms().add(tr);
					group.getChildren().add(corner);
					// add first stud
					Circle circle = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(), basicStackPlateLayout.getStudDiameter() / 2);
					circle.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle);
					group.getChildren().add(circle);
					// add second stud
					Circle circle2 = new Circle(pos.getCenterPosition().getX() + basicStackPlateLayout.getHorizontalHoleDistance(), width - pos.getCenterPosition().getY(), basicStackPlateLayout.getStudDiameter() / 2);
					circle2.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle2);
					group.getChildren().add(circle2);
				} else if (pos.getStudType() == StudType.TILTED_CORNER) {
					SVGPath corner = new SVGPath();
					corner.setContent(CORNER_PATH);
					corner.getStyleClass().add(CSS_CLASS_CORNERSHAPE);
					Rotate rt = new Rotate(-45, 7.5, 15.751);
					Translate tr = new Translate();
					tr.setX(pos.getCenterPosition().getX() - 7.5);
					tr.setY(width - pos.getCenterPosition().getY() - 40.5 + 24.749);
					corner.getTransforms().addAll(tr);
					corner.getTransforms().add(rt);
					group.getChildren().add(corner);
					// add first stud
					Circle circle = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(), basicStackPlateLayout.getStudDiameter() / 2);
					circle.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle);
					group.getChildren().add(circle);
					// add second stud
					Circle circle2 = new Circle(pos.getCenterPosition().getX() + basicStackPlateLayout.getHorizontalHoleDistance(), width - pos.getCenterPosition().getY(), basicStackPlateLayout.getStudDiameter() / 2);
					circle2.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle2);
					group.getChildren().add(circle2);
				}
			}
		}
	}
	
	private void configureWorkPieces() {
		for (StackingPosition stackingPosition : basicStackPlateLayout.getStackingPositions()) {
			if (stackingPosition.getWorkPiece() != null) {
				if (stackingPosition.getOrientation() == WorkPieceOrientation.HORIZONTAL) {
					Rectangle rp = new Rectangle(stackingPosition.getPosition().getX() - stackingPosition.getWorkPiece().getDimensions().getLength() / 2, 
							width - stackingPosition.getPosition().getY() - stackingPosition.getWorkPiece().getDimensions().getWidth() / 2, 
							stackingPosition.getWorkPiece().getDimensions().getLength(), stackingPosition.getWorkPiece().getDimensions().getWidth());
					Rectangle rp2 = new Rectangle(stackingPosition.getPosition().getX() - stackingPosition.getWorkPiece().getDimensions().getLength() / 2 + 5, 
							width - stackingPosition.getPosition().getY() - stackingPosition.getWorkPiece().getDimensions().getWidth() / 2, 
							5, stackingPosition.getWorkPiece().getDimensions().getWidth());
					rp.getStyleClass().add(CSS_CLASS_WORKPIECE);
					rp2.getStyleClass().add(CSS_CLASS_WORKPIECE_MARK);
					if (stackingPosition.getWorkPiece().getType() == Type.FINISHED) {
						rp.getStyleClass().add(CSS_CLASS_FINISHED);
						rp2.getStyleClass().add("workpiece-finished-mark");
						rp2.getStyleClass().add(CSS_CLASS_FINISHED_MARK);
					}
					rp.setArcHeight(0);
					rp.setArcWidth(0);
					group.getChildren().add(rp);
					group.getChildren().add(rp2);
				} else if (stackingPosition.getOrientation() == WorkPieceOrientation.TILTED) {
					// TILTED
					Rectangle rp = new Rectangle(stackingPosition.getPosition().getX() - stackingPosition.getWorkPiece().getDimensions().getLength() / 2, 
							width - stackingPosition.getPosition().getY() - stackingPosition.getWorkPiece().getDimensions().getWidth() / 2, 
							stackingPosition.getWorkPiece().getDimensions().getLength(), stackingPosition.getWorkPiece().getDimensions().getWidth());
					Rectangle rp2 = new Rectangle(stackingPosition.getPosition().getX() - stackingPosition.getWorkPiece().getDimensions().getLength() / 2 + 5, 
							width - stackingPosition.getPosition().getY() - stackingPosition.getWorkPiece().getDimensions().getWidth() / 2, 
							5, stackingPosition.getWorkPiece().getDimensions().getWidth());
					Rotate rotate = new Rotate(-45, stackingPosition.getPosition().getX(), width - stackingPosition.getPosition().getY());
					rp.getTransforms().add(rotate);
					rp.getStyleClass().add(CSS_CLASS_WORKPIECE);
					rp2.getTransforms().add(rotate);
					rp2.getStyleClass().add(CSS_CLASS_WORKPIECE_MARK);
					if (stackingPosition.getWorkPiece().getType() == Type.FINISHED) {
						rp.getStyleClass().add(CSS_CLASS_FINISHED);
						rp2.getStyleClass().add(CSS_CLASS_FINISHED_MARK);
					}
					rp.setArcHeight(0);
					rp.setArcWidth(0);
					group.getChildren().add(rp);
					group.getChildren().add(rp2);
				} else {
					throw new IllegalArgumentException("Unknown orientation");
				}
			}
		}
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
	}

	@Override
	public void refresh() {
		build();
	}

}
