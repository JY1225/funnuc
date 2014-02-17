package eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorLayout;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class ConveyorFinishedWorkPieceLayoutView extends AbstractWorkPieceLayoutView<ConveyorFinishedWorkPieceLayoutPresenter<? extends AbstractMenuPresenter<?>>> {
	
	private ConveyorLayout conveyorLayout;
		
	private static final float VISIBLE_AREA = 210;
		
	private static final String CSS_CLASS_TOTAL = "conveyor-eaton-bg";
	private static final String CSS_CLASS_SUPPORT = "conveyor-eaton-support";
	private static final String CSS_CLASS_TRACK = "conveyor-eaton-track";
	private static final String CSS_CLASS_SIDE1 = "conveyor-eaton-side1";
	private static final String CSS_CLASS_SIDE2 = "conveyor-eaton-side2";
	private static final String CSS_CLASS_STATUS_ICON = "status-icon";
	private static final String CSS_CLASS_STATUS_ICON_DISABLED = "status-icon-disabled";
	private static final String CSS_CLASS_STATUS_AREA = "status-area";
	private static final String CSS_CLASS_WORKPIECE_AREA = "workPiece-area";
	private static final String CSS_CLASS_WORKPIECE  = "workpiece-c-finished";
	
	private static final String PATH_MANUAL_MODE = "M 10 0 C 4.4771526 0 0 4.477153 0 10 C 0 15.522847 4.4771526 20 10 20 C 15.522847 20 20 15.522847 20 10 C 20 4.477153 15.522847 0 10 0 z M 10 2.5 C 14.142136 2.5 17.5 5.857864 17.5 10 C 17.5 14.142136 14.142136 17.5 10 17.5 C 5.8578645 17.5 2.5 14.142136 2.5 10 C 2.5 5.857864 5.8578645 2.5 10 2.5 z M 5.84375 5.6875 L 5.84375 14.3125 L 7.53125 14.3125 L 7.53125 7.09375 L 9.125 14.3125 L 10.90625 14.3125 L 12.5 7.09375 L 12.5 14.3125 L 14.1875 14.3125 L 14.1875 5.6875 L 11.5625 5.6875 L 10.0625 12.4375 L 8.46875 5.6875 L 5.84375 5.6875 z";
	private static final String PATH_LOCK = "M 9.78125 0 C 6.80264 0 4.40625 2.52069 4.40625 5.5 L 4.40625 7.90625 C 4.05894 8.00555 3.75 8.09375 3.75 8.09375 C 3.6724 8.11965 2.8125 8.37272 2.8125 8.90625 L 2.8125 19.3125 C 2.8125 19.75537 3.21949 20 3.75 20 L 16.25 20 C 16.77957 20 17.1875 19.75537 17.1875 19.3125 L 17.1875 8.90625 C 17.1875 8.46409 16.76162 8.2935 16.25 8.09375 C 16.25 8.09375 16.0203 8.01791 15.59375 7.90625 L 15.59375 5.5 C 15.59375 2.52069 13.19713 0 10.21875 0 L 9.78125 0 z M 10 1.96875 C 11.9218 1.96875 13.1875 3.54696 13.1875 5.46875 L 13.1875 7.46875 C 12.3125 7.36783 11.26263 7.3125 10 7.3125 C 8.7383 7.3125 7.61245 7.36787 6.8125 7.46875 L 6.8125 5.46875 C 6.8125 3.54765 8.07936 1.96875 10 1.96875 z M 10 10.28125 C 10.96404 10.28125 11.75 11.06721 11.75 12.03125 C 11.75 12.54871 11.51357 13.08716 11.15625 13.40625 L 11.75 16.90625 C 11.75 16.90625 11.23442 17.3125 10 17.3125 C 8.7665 17.3125 8.25 16.90625 8.25 16.90625 L 8.84375 13.40625 C 8.48503 13.08762 8.25 12.54941 8.25 12.03125 C 8.25 11.06698 9.03688 10.28125 10 10.28125 z";
	private static final String PATH_ROTATING = "M 9.21875 0 L 9.21875 1.65625 C 6.78895 1.88945 4.5416 3.1627 3.125 5.1875 L 4.90625 6.4375 C 5.91405 4.9975 7.49995 4.06635 9.21875 3.84375 L 9.21875 5.4375 L 13.90625 2.71875 L 9.21875 0 z M 14.8125 3.125 L 13.5625 4.90625 C 15.0027 5.91525 15.93225 7.50255 16.15625 9.21875 L 14.5625 9.21875 L 17.28125 13.90625 L 20 9.21875 L 18.34375 9.21875 C 18.10935 6.79035 16.8359 4.5442 14.8125 3.125 z M 2.71875 6.09375 L 0 10.78125 L 1.65625 10.78125 C 1.88805 13.21085 3.1615 15.4584 5.1875 16.875 L 6.4375 15.09375 C 4.9975 14.08715 4.06775 12.49885 3.84375 10.78125 L 5.4375 10.78125 L 2.71875 6.09375 z M 15.09375 13.5625 C 14.08595 15.0013 12.49485 15.93225 10.78125 16.15625 L 10.78125 14.5625 L 6.09375 17.28125 L 10.8125 20 L 10.8125 18.34375 C 13.2397 18.11075 15.4584 16.8345 16.875 14.8125 L 15.09375 13.5625 z";
	private static final String PATH_ROTATING_BACK = "M 10.78125 0 L 6.09375 2.71875 L 10.78125 5.4375 L 10.78125 3.84375 C 12.50005 4.06635 14.08595 4.9975 15.09375 6.4375 L 16.875 5.1875 C 15.4584 3.1627 13.21105 1.88945 10.78125 1.65625 L 10.78125 0 z M 5.1875 3.125 C 3.1641 4.5442 1.89065 6.79035 1.65625 9.21875 L 0 9.21875 L 2.71875 13.90625 L 5.4375 9.21875 L 3.84375 9.21875 C 4.06775 7.50255 4.9973 5.91525 6.4375 4.90625 L 5.1875 3.125 z M 17.28125 6.09375 L 14.5625 10.78125 L 16.15625 10.78125 C 15.93225 12.49885 15.0025 14.08715 13.5625 15.09375 L 14.8125 16.875 C 16.8385 15.4584 18.11195 13.21085 18.34375 10.78125 L 20 10.78125 L 17.28125 6.09375 z M 4.90625 13.5625 L 3.125 14.8125 C 4.5416 16.8345 6.7603 18.11075 9.1875 18.34375 L 9.1875 20 L 13.90625 17.28125 L 9.21875 14.5625 L 9.21875 16.15625 C 7.50515 15.93225 5.91405 15.0013 4.90625 13.5625 z";
	
	private Group conveyorGroupB;
	private TranslateTransition ttB;
	private TranslateTransition ttBSlow;
	private TranslateTransition ttBBack;
	private RotateTransition rtB;
	private RotateTransition rtBSlow;
	private RotateTransition rtBBack;
	private Pane paneTrackB;
	private Rectangle raSupportB1;
	private Rectangle raSupportB2;
	private HBox hBoxStatusB;
	private SVGPath iconRotatingB;
	private SVGPath iconLockedB;
	private SVGPath iconMode;
	private Rectangle workPieceWindowB;
	private Rectangle workPieceB;
	
	public static final int NOT_ROTATING = 0;
	public static final int ROTATING = 1;
	public static final int ROTATING_SLOW = 2;
	public static final int ROTATING_BACK = 3;
	
	public ConveyorFinishedWorkPieceLayoutView() {
		conveyorGroupB = new Group();
	}
	
	public void setConveyorLayout(final ConveyorLayout conveyorLayout) {
		this.conveyorLayout = conveyorLayout;
	}
	
	@Override
	public void build() {
		getContents().setPadding(new Insets(1, 0, 0, 0));
		getContents().getChildren().clear();
		getContents().add(conveyorGroupB, 1, 0);
		getContents().setAlignment(Pos.TOP_CENTER);
		
		getContents().setVgap(0);
		getContents().setHgap(0);
		
		hBoxStatusB = new HBox();
		hBoxStatusB.setAlignment(Pos.CENTER);
		hBoxStatusB.setSpacing(15);
		hBoxStatusB.getStyleClass().add(CSS_CLASS_STATUS_AREA);
		hBoxStatusB.setPrefSize(160, 40);
		hBoxStatusB.setMaxSize(160,  40);
		
		getContents().add(hBoxStatusB, 1, 1);
		GridPane.setHalignment(hBoxStatusB, HPos.CENTER);
		GridPane.setMargin(hBoxStatusB, new Insets(0, 3, 0, 0));
		
		createConveyorUsingShapes();
		
		iconRotatingB = new SVGPath();
		iconRotatingB.setContent(PATH_ROTATING);
		iconRotatingB.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		rtB = new RotateTransition(Duration.millis(7500), iconRotatingB);
		rtB.setByAngle(360);
		rtB.setInterpolator(Interpolator.LINEAR);
		rtB.setAutoReverse(false);
		rtB.setCycleCount(Transition.INDEFINITE);
		rtBSlow = new RotateTransition(Duration.millis(7500*2), iconRotatingB);
		rtBSlow.setByAngle(360);
		rtBSlow.setInterpolator(Interpolator.LINEAR);
		rtBSlow.setAutoReverse(false);
		rtBSlow.setCycleCount(Transition.INDEFINITE);
		rtBBack = new RotateTransition(Duration.millis(7500), iconRotatingB);
		rtBBack.setByAngle(-360);
		rtBBack.setInterpolator(Interpolator.LINEAR);
		rtBBack.setAutoReverse(false);
		rtBBack.setCycleCount(Transition.INDEFINITE);
		hBoxStatusB.getChildren().add(iconRotatingB);
		iconLockedB = new SVGPath();
		iconLockedB.setContent(PATH_LOCK);
		iconLockedB.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		hBoxStatusB.getChildren().add(iconLockedB);
		
		iconMode = new SVGPath();
		iconMode.setContent(PATH_MANUAL_MODE);
		iconMode.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		
		getContents().add(iconMode, 0, 0);
		
		refresh();	
	}

	@Override public void setTextFieldListener(final TextInputControlListener listener) { }
	
	private void createConveyorUsingShapes() {
		createTrackUsingShapes(true);
		conveyorGroupB.getChildren().clear();
		createTrackUsingShapes(false);
		conveyorGroupB.getChildren().add(paneTrackB);
	}
	
	private void createTrackUsingShapes(final boolean trackANotB) {
		Pane trackPane = new Pane();
		Rectangle total = new Rectangle(0, 0, VISIBLE_AREA, conveyorLayout.getSideWidth()*2 + conveyorLayout.getTrackWidth());
		Rectangle total2 = new Rectangle(-100, -50, VISIBLE_AREA + 100, conveyorLayout.getSideWidth()*2 + conveyorLayout.getTrackWidth() + 100);
		total.getStyleClass().add(CSS_CLASS_TOTAL);
		Rectangle side1 = new Rectangle(-2, 0, VISIBLE_AREA+4, conveyorLayout.getSideWidth());
		side1.getStyleClass().add(CSS_CLASS_SIDE1);
		Rectangle track = new Rectangle(0, conveyorLayout.getSideWidth(), VISIBLE_AREA, conveyorLayout.getTrackWidth());
		track.getStyleClass().add(CSS_CLASS_TRACK);
		Group patternGroup = new Group();
		Image img = new Image("/images/pattern2.png");
		Rectangle trackPattern = new Rectangle(-30, conveyorLayout.getSideWidth(), VISIBLE_AREA + 300, conveyorLayout.getTrackWidth());
		trackPattern.setFill(new ImagePattern(img, 10, 0, 15, 10, false));
		Rectangle trackPatternClip = new Rectangle(-2, conveyorLayout.getSideWidth(), VISIBLE_AREA+2, conveyorLayout.getTrackWidth());
		Rectangle shadow = new Rectangle(-2, conveyorLayout.getSideWidth(), VISIBLE_AREA+2, conveyorLayout.getTrackWidth());
		patternGroup.getChildren().add(trackPattern);
		patternGroup.getChildren().add(shadow);
		patternGroup.setClip(trackPatternClip);
		shadow.getStyleClass().add("tester");
		Rectangle side2 = new Rectangle(-2, (conveyorLayout.getSideWidth() + conveyorLayout.getTrackWidth()), VISIBLE_AREA+4, conveyorLayout.getSideWidth());
		side2.getStyleClass().add(CSS_CLASS_SIDE2);
		Rectangle support1 = new Rectangle(conveyorLayout.getXSupportStart(), conveyorLayout.getSideWidth() + 20, VISIBLE_AREA - conveyorLayout.getXSupportStart(), conveyorLayout.getSupportWidth());
		support1.getStyleClass().add(CSS_CLASS_SUPPORT);
		side1.setArcHeight(4);
		side1.setArcWidth(4);
		side2.setArcHeight(4);
		side2.setArcWidth(4);
		Rectangle support2 = new Rectangle(conveyorLayout.getXSupportStart(), conveyorLayout.getSideWidth() + conveyorLayout.getTrackWidth() - conveyorLayout.getSupportWidth() - 20, VISIBLE_AREA - conveyorLayout.getXSupportStart(), conveyorLayout.getSupportWidth());
		support2.getStyleClass().add(CSS_CLASS_SUPPORT);
		Rectangle workPieceWindow = new Rectangle(conveyorLayout.getxPosSensor1(), conveyorLayout.getSideWidth(), 0, conveyorLayout.getTrackWidth());
		workPieceWindow.getStyleClass().add(CSS_CLASS_WORKPIECE_AREA);
		Rectangle workPiece = new Rectangle(conveyorLayout.getxPosSensor1(), conveyorLayout.getSideWidth(), 0, conveyorLayout.getTrackWidth());
		workPiece.getStyleClass().add(CSS_CLASS_WORKPIECE);
		trackPane.getChildren().addAll(total, track, patternGroup, side1, side2, support1, support2, workPieceWindow, workPiece);
		trackPane.setClip(total2);
		trackPane.setRotate(-90);
		float speedNormal = getPresenter().getConveyor().getNomSpeedConveyorA();
		float speedSlow = getPresenter().getConveyor().getNomSpeedConveyorASlow();
		if (!trackANotB) {
			speedNormal = getPresenter().getConveyor().getNomSpeedConveyorB();
			speedSlow = getPresenter().getConveyor().getNomSpeedConveyorBSlow();
		}
		TranslateTransition tt = new TranslateTransition(Duration.millis((23 / speedNormal) * 60 * 1000), trackPattern);
		tt.setInterpolator(Interpolator.LINEAR);
		tt.setFromX(0);
		tt.setByX(-15);
		tt.setCycleCount(Transition.INDEFINITE);
		tt.setAutoReverse(false);
		tt.play();
		tt.pause();
		TranslateTransition ttSlow = new TranslateTransition(Duration.millis((23 / speedSlow) * 60 * 1000), trackPattern);
		ttSlow.setInterpolator(Interpolator.LINEAR);
		ttSlow.setFromX(0);
		ttSlow.setByX(-15);
		ttSlow.setCycleCount(Transition.INDEFINITE);
		ttSlow.setAutoReverse(false);
		ttSlow.play();
		ttSlow.pause();
		TranslateTransition ttBack = new TranslateTransition(Duration.millis((23 / speedNormal) * 60 * 1000), trackPattern);
		ttBack.setInterpolator(Interpolator.LINEAR);
		ttBack.setFromX(0);
		ttBack.setByX(15);
		ttBack.setCycleCount(Transition.INDEFINITE);
		ttBack.setAutoReverse(false);
		ttBack.play();
		ttBack.pause();
		paneTrackB = trackPane;
		ttB = tt;
		ttBSlow = ttSlow;
		ttBBack = ttBack;
		raSupportB1 = support1;
		raSupportB2 = support2;
		workPieceB = workPiece;
		workPieceWindowB = workPieceWindow;
	}
	
	public void setModeManual(final boolean modeManual) {
		iconMode.getStyleClass().removeAll(CSS_CLASS_STATUS_ICON, CSS_CLASS_STATUS_ICON_DISABLED);
		if (modeManual) {
			iconMode.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		} else {
			iconMode.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		}
	}
	
	public void setRotatingB(final int rotatingStatus) {
		rtB.stop();
		rtBSlow.stop();
		ttB.stop();
		ttBSlow.stop();
		rtBBack.stop();
		ttBBack.stop();
		iconRotatingB.getStyleClass().removeAll(CSS_CLASS_STATUS_ICON, CSS_CLASS_STATUS_ICON_DISABLED);
		switch (rotatingStatus) {
			case NOT_ROTATING:
				iconRotatingB.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
				break;
			case ROTATING:
				iconRotatingB.getStyleClass().add(CSS_CLASS_STATUS_ICON);
				rtB.play();
				ttB.play();
				break;
			case ROTATING_SLOW:
				iconRotatingB.getStyleClass().add(CSS_CLASS_STATUS_ICON);
				rtBSlow.play();
				ttBSlow.play();
				break;
			case ROTATING_BACK:
				iconRotatingB.getStyleClass().add(CSS_CLASS_STATUS_ICON);
				rtBBack.play();
				ttBBack.play();
				break;
			default:
				throw new IllegalArgumentException("Unknown rotating status: " + rotatingStatus);
		}
	}
	
	public void setLockedB(final boolean locked) {
		iconLockedB.getStyleClass().removeAll(CSS_CLASS_STATUS_ICON, CSS_CLASS_STATUS_ICON_DISABLED);
		if (locked) {
			iconLockedB.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		} else {
			iconLockedB.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		}
	}
	
	public void setTrackBModeLoad(final boolean trackBModeLoad) {
		if (trackBModeLoad) {
			iconRotatingB.setContent(PATH_ROTATING);
			paneTrackB.setOpacity(0.2);
			hBoxStatusB.setOpacity(0.2);
		} else {
			iconRotatingB.setContent(PATH_ROTATING_BACK);
			paneTrackB.setOpacity(1);
			hBoxStatusB.setOpacity(1);
		}
	}
	
	public void setConnected(final boolean connected) {
		if (connected) {
			this.setOpacity(1);
		} else {
			this.setOpacity(0.2);
			setRotatingB(NOT_ROTATING);
			setLockedB(false);
			setTrackBModeLoad(false);
			setModeManual(false);
			setSensorBActive(false);
		}
	}
	
	public void setSensorBActive(final boolean active) {
		if (active) {
			workPieceB.setVisible(true);
		} else {
			workPieceB.setVisible(false);
		}
	}
		
	@Override
	public void refresh() {
		setConnected(getPresenter().getConveyor().isConnected());
		WorkPieceDimensions wpDimB = conveyorLayout.getStackingPositionTrackB().getWorkPiece().getDimensions();
		workPieceB.setWidth(wpDimB.getLength());
		workPieceB.setHeight(wpDimB.getWidth());
		workPieceB.setY(conveyorLayout.getSideWidth() + (conveyorLayout.getTrackWidth()/2) - wpDimB.getWidth()/2);
		workPieceWindowB.setWidth(wpDimB.getLength());
		workPieceWindowB.setHeight(wpDimB.getWidth());
		workPieceWindowB.setY(conveyorLayout.getSideWidth() + (conveyorLayout.getTrackWidth()/2) - wpDimB.getWidth()/2);
		raSupportB1.setY(conveyorLayout.getSideWidth() + (conveyorLayout.getTrackWidth()/2) - wpDimB.getWidth()/2 - conveyorLayout.getSupportWidth());
		raSupportB2.setY(conveyorLayout.getSideWidth() + (conveyorLayout.getTrackWidth()/2) + wpDimB.getWidth()/2);
	}
	
}