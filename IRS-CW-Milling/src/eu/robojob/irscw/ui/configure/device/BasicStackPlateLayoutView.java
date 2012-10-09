package eu.robojob.irscw.ui.configure.device;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import eu.robojob.irscw.external.device.BasicStackPlate;
import eu.robojob.irscw.external.device.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.device.BasicStackPlateLayout;
import eu.robojob.irscw.external.device.StackingPosition;
import eu.robojob.irscw.external.device.StudPosition;
import eu.robojob.irscw.external.device.StudPosition.StudType;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.TextFieldListener;

public class BasicStackPlateLayoutView extends AbstractFormView<BasicStackPlateLayoutPresenter> {

	private BasicStackPlate basicStackPlate;
	private BasicStackPlateLayout basicStackPlateLayout;
	
	private Group group;
	private Rectangle stackPlate;
	private Pane root;
	
	private List<Circle> holes;
	private List<Circle> studs;
	private List<Text> horizontalLabels;
	private List<Text> verticalLabels;
	
	private static final float TXT_WIDTH = 40;
	private static final float TXT_HEIGHT = 15;
	
	public BasicStackPlateLayoutView() {
		super();
		this.holes = new ArrayList<Circle>();
		this.studs = new ArrayList<Circle>();
		this.horizontalLabels = new ArrayList<Text>();
		this.verticalLabels = new ArrayList<Text>();
	}
	
	public void setBasicStackPlate(BasicStackPlate basicStackPlate) {
		this.basicStackPlate = basicStackPlate;
		this.basicStackPlateLayout = basicStackPlate.getLayout();
	}
	
	@Override
	protected void build() {
		group = new Group();
		
		group.getChildren().clear();
		
		// add plate
		stackPlate = new Rectangle(0, 0, basicStackPlateLayout.getLength(), basicStackPlateLayout.getWidth());
		stackPlate.getStyleClass().add("stackplate");
		//stackPlate.setArcHeight(25);
		//stackPlate.setArcWidth(25);
		
		group.getChildren().add(stackPlate);
		
		// add holes
		int index = 1;
		for (StudPosition[] horizontalPositions : basicStackPlateLayout.getStudPositions()) {
			Text txt = new Text(""  + (char)('A' + (index - 1)));
			txt.setX(0);
			txt.setY(horizontalPositions[0].getCenterPosition().getY() + TXT_HEIGHT/2);
			txt.setWrappingWidth(TXT_WIDTH);
			txt.getStyleClass().add("stacker-text");
			group.getChildren().add(txt);
			verticalLabels.add(txt);
			int index2 = 1;
			for (StudPosition pos : horizontalPositions) {
				if (index == 1) {
					if (index2 % 2 == 0) {
						Text txt2 = new Text("" + index2);
						txt2.setX(pos.getCenterPosition().getX() - TXT_WIDTH/2);
						txt2.setY(basicStackPlateLayout.getWidth() - TXT_HEIGHT/2);
						txt2.setWrappingWidth(TXT_WIDTH);
						txt2.getStyleClass().add("stacker-text");
						group.getChildren().add(txt2);
						horizontalLabels.add(txt2);
					} else {
						Text txt2 = new Text("\u00B7");
						txt2.setX(pos.getCenterPosition().getX() - TXT_WIDTH/2);
						txt2.setY(basicStackPlateLayout.getWidth() - TXT_HEIGHT/2);
						txt2.setWrappingWidth(TXT_WIDTH);
						txt2.getStyleClass().add("stacker-text");
						group.getChildren().add(txt2);
						horizontalLabels.add(txt2);
						// draw line
						Path path = new Path();
						MoveTo moveTo = new MoveTo();
						moveTo.setX(pos.getCenterPosition().getX());
						moveTo.setY(pos.getCenterPosition().getY());
						LineTo lineTo = new LineTo();
						lineTo.setX(pos.getCenterPosition().getX());
						lineTo.setY(basicStackPlateLayout.getStudPositions()[basicStackPlateLayout.getStudPositions().length-1][0].getCenterPosition().getY());
						path.getElements().add(moveTo);
						path.getElements().add(lineTo);
						path.getStyleClass().add("line");
						group.getChildren().add(path);
					}
					index2++;
				}
				Circle hole = new Circle(pos.getCenterPosition().getX(), pos.getCenterPosition().getY(), basicStackPlateLayout.getHoleDiameter()/2);
				holes.add(hole);
				hole.getStyleClass().add("hole");
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
		//Scale t = new Scale(550 / group.getBoundsInParent().getWidth(),  280 / group.getBoundsInParent().getHeight());
		//group.getTransforms().add(t);
				
		//group.setTranslateX((570 - group.getBoundsInParent().getWidth())/2);
		//group.setTranslateY((300 - group.getBoundsInParent().getHeight())/2);
		
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
					Circle circle = new Circle(pos.getCenterPosition().getX(), pos.getCenterPosition().getY(), basicStackPlateLayout.getStudDiameter()/2);
					circle.getStyleClass().add("normal-stud");
					studs.add(circle);
					group.getChildren().add(circle);
				} else if (pos.getStudType() == StudType.HORIZONTAL_CORNER) {
					// draw line
					Path path = new Path();
					MoveTo moveTo = new MoveTo();
					moveTo.setX(pos.getCenterPosition().getX() + basicStackPlateLayout.getHorizontalStudLength());
					moveTo.setY(pos.getCenterPosition().getY());
					LineTo lineTo = new LineTo();
					lineTo.setX(pos.getCenterPosition().getX());
					lineTo.setY(pos.getCenterPosition().getY());
					LineTo lineTo2 = new LineTo();
					lineTo2.setY(pos.getCenterPosition().getY() - basicStackPlateLayout.getHorizontalStudWidth());
					lineTo2.setX(pos.getCenterPosition().getX());
					path.getElements().add(moveTo);
					path.getElements().add(lineTo);
					path.getElements().add(lineTo2);
					path.getStyleClass().add("corner-stud-lines");
					path.setStrokeWidth(basicStackPlateLayout.getStudDiameter());
					group.getChildren().add(path);
					path.toFront();
				}
			}
		}
	}
	
	private void configureWorkPieces() {
		for (StackingPosition stackingPosition : basicStackPlateLayout.getStackingPositions()) {
			if (stackingPosition.getWorkPiece() != null) {
				if (stackingPosition.getOrientation() == WorkPieceOrientation.HORIZONTAL) {
					Rectangle rp = new Rectangle(stackingPosition.getPosition().getX() - stackingPosition.getWorkPiece().getDimensions().getLength()/2, 
							stackingPosition.getPosition().getY()- stackingPosition.getWorkPiece().getDimensions().getWidth()/2, 
							stackingPosition.getWorkPiece().getDimensions().getLength(), stackingPosition.getWorkPiece().getDimensions().getWidth());
					rp.getStyleClass().add("workpiece");
					rp.setArcHeight(10);
					rp.setArcWidth(10);
					group.getChildren().add(rp);
				} else if (stackingPosition.getOrientation() == WorkPieceOrientation.TILTED){
					// TILTED
					Rectangle rp = new Rectangle(stackingPosition.getPosition().getX() - stackingPosition.getWorkPiece().getDimensions().getLength()/2, 
							stackingPosition.getPosition().getY()- stackingPosition.getWorkPiece().getDimensions().getWidth()/2, 
							stackingPosition.getWorkPiece().getDimensions().getLength(), stackingPosition.getWorkPiece().getDimensions().getWidth());
					Rotate rotate = new Rotate(-45, stackingPosition.getPosition().getX(), stackingPosition.getPosition().getY());
					rp.getTransforms().add(rotate);
					rp.getStyleClass().add("workpiece");
					rp.setArcHeight(10);
					rp.setArcWidth(10);
					group.getChildren().add(rp);
				} else {
					throw new IllegalArgumentException("Unknown orientation");
				}
			}
		}
	}

	@Override
	public void setTextFieldListener(TextFieldListener listener) {
	}

	@Override
	public void refresh() {
		build();
	}

}
