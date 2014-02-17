package eu.robojob.millassist.ui.general.device.stacking.conveyor.normal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorLayout;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class ConveyorRawWorkPieceLayoutView extends AbstractWorkPieceLayoutView<ConveyorRawWorkPieceLayoutPresenter<? extends AbstractMenuPresenter<?>>> {

	private VBox vboxStatusControls;
	private HBox hboxStatus;
	private SVGPath iconNearby;
	private SVGPath iconManual;
	private SVGPath iconLock;
	private SVGPath iconRotating;
	private RotateTransition rt;
	private Button btnConfigureSupports;
	private Button btnAllSupportsDown;
	
	private ConveyorLayout conveyorLayout;
	
	private List<TranslateTransition> tTransitions;
	private List<Rectangle> supports;
	private List<Rectangle> workPieceWindows;
	private List<Rectangle> workPieces;
	private List<Text> texts;
	private Group conveyorGroup;
	private Pane p;
	
	private DecimalFormat df;
			
	private static final String CSS_CLASS_SUPPORT_DOWN = "support-down";
	private static final String CSS_CLASS_SUPPORT_UP = "support-up";
	private static final String CSS_CLASS_SUPPORT_DOWN_SHOULD_BE_UP = "support-down-should-be-up";
	private static final String CSS_CLASS_SUPPORT_UP_SHOULD_BE_DOWN = "support-up-should-be-down";
	private static final String CSS_CLASS_DISTANCE_TEXT = "distance-text";
	private static final String CSS_CLASS_STATUS_ICON = "status-icon";
	private static final String CSS_CLASS_STATUS_ICON_DISABLED = "status-icon-disabled";
	private static final String CSS_CLASS_WHITE_ICON = "white-icon";
	private static final String CSS_CLASS_STATUS_AREA = "status-area";
	private static final String CSS_CLASS_CONVEYOR_BACKGROUND = "conveyor-background";
	private static final String CSS_CLASS_DISTANCE_BETWEEN_TRACKS = "distance-between-tracks";
	private static final String CSS_CLASS_TRACK = "track";	
	private static final String CSS_CLASS_SUPPORT_FIXED = "support-fixed";
	private static final String CSS_CLASS_WORKPIECE_AREA = "workPiece-area";
	private static final String CSS_CLASS_WORKPIECE  = "workpiece-c";
	
	private static final String ICON_NEARBY_PATH = "M 14.567901,0 12.888889,1.6790123 C 15.004695,3.8197474 16.320988,6.7523235 16.320988,10 c 0,3.247677 -1.316293,6.180252 -3.432099,8.320988 L 14.567901,20 c 2.544762,-2.569711 4.123457,-6.097751 4.123457,-10 0,-3.9022489 -1.578695,-7.4302893 -4.123457,-10 z M 10.666667,3.9012346 8.9876543,5.5802469 C 10.102866,6.7200183 10.790124,8.279423 10.790124,10 c 0,1.720578 -0.687258,3.279982 -1.8024697,4.419753 l 1.6790127,1.679012 C 12.210749,14.529986 13.160494,12.375095 13.160494,10 c 0,-2.375095 -0.949745,-4.5299853 -2.493827,-6.0987654 z M 4.4691358,6.8395062 C 2.7236434,6.8395062 1.308642,8.254507 1.308642,10 c 0,1.745493 1.4150014,3.160494 3.1604938,3.160494 1.7454926,0 3.1604939,-1.415001 3.1604939,-3.160494 0,-1.745493 -1.4150013,-3.1604938 -3.1604939,-3.1604938 z";
	private static final String ICON_MANUAL_MODE = "M 10 0 C 4.4771526 0 0 4.477153 0 10 C 0 15.522847 4.4771526 20 10 20 C 15.522847 20 20 15.522847 20 10 C 20 4.477153 15.522847 0 10 0 z M 10 2.5 C 14.142136 2.5 17.5 5.857864 17.5 10 C 17.5 14.142136 14.142136 17.5 10 17.5 C 5.8578645 17.5 2.5 14.142136 2.5 10 C 2.5 5.857864 5.8578645 2.5 10 2.5 z M 5.84375 5.6875 L 5.84375 14.3125 L 7.53125 14.3125 L 7.53125 7.09375 L 9.125 14.3125 L 10.90625 14.3125 L 12.5 7.09375 L 12.5 14.3125 L 14.1875 14.3125 L 14.1875 5.6875 L 11.5625 5.6875 L 10.0625 12.4375 L 8.46875 5.6875 L 5.84375 5.6875 z";
	private static final String ICON_LOCK = "M 9.78125 0 C 6.80264 0 4.40625 2.52069 4.40625 5.5 L 4.40625 7.90625 C 4.05894 8.00555 3.75 8.09375 3.75 8.09375 C 3.6724 8.11965 2.8125 8.37272 2.8125 8.90625 L 2.8125 19.3125 C 2.8125 19.75537 3.21949 20 3.75 20 L 16.25 20 C 16.77957 20 17.1875 19.75537 17.1875 19.3125 L 17.1875 8.90625 C 17.1875 8.46409 16.76162 8.2935 16.25 8.09375 C 16.25 8.09375 16.0203 8.01791 15.59375 7.90625 L 15.59375 5.5 C 15.59375 2.52069 13.19713 0 10.21875 0 L 9.78125 0 z M 10 1.96875 C 11.9218 1.96875 13.1875 3.54696 13.1875 5.46875 L 13.1875 7.46875 C 12.3125 7.36783 11.26263 7.3125 10 7.3125 C 8.7383 7.3125 7.61245 7.36787 6.8125 7.46875 L 6.8125 5.46875 C 6.8125 3.54765 8.07936 1.96875 10 1.96875 z M 10 10.28125 C 10.96404 10.28125 11.75 11.06721 11.75 12.03125 C 11.75 12.54871 11.51357 13.08716 11.15625 13.40625 L 11.75 16.90625 C 11.75 16.90625 11.23442 17.3125 10 17.3125 C 8.7665 17.3125 8.25 16.90625 8.25 16.90625 L 8.84375 13.40625 C 8.48503 13.08762 8.25 12.54941 8.25 12.03125 C 8.25 11.06698 9.03688 10.28125 10 10.28125 z";
	private static final String ICON_ROTATING_PATH = "M 10.78125 0 L 6.09375 2.71875 L 10.78125 5.4375 L 10.78125 3.84375 C 12.50005 4.06635 14.08595 4.9975 15.09375 6.4375 L 16.875 5.1875 C 15.4584 3.1627 13.21105 1.88945 10.78125 1.65625 L 10.78125 0 z M 5.1875 3.125 C 3.1641 4.5442 1.89065 6.79035 1.65625 9.21875 L 0 9.21875 L 2.71875 13.90625 L 5.4375 9.21875 L 3.84375 9.21875 C 4.06775 7.50255 4.9973 5.91525 6.4375 4.90625 L 5.1875 3.125 z M 17.28125 6.09375 L 14.5625 10.78125 L 16.15625 10.78125 C 15.93225 12.49885 15.0025 14.08715 13.5625 15.09375 L 14.8125 16.875 C 16.8385 15.4584 18.11195 13.21085 18.34375 10.78125 L 20 10.78125 L 17.28125 6.09375 z M 4.90625 13.5625 L 3.125 14.8125 C 4.5416 16.8345 6.7603 18.11075 9.1875 18.34375 L 9.1875 20 L 13.90625 17.28125 L 9.21875 14.5625 L 9.21875 16.15625 C 7.50515 15.93225 5.91405 15.0013 4.90625 13.5625 z";
	private static final String ICON_ARROW_DOWN = "m 327.07812,581.72831 0,6.40625 -2.6875,0 2.28125,3.5625 1.90625,2.9375 1.9375,-2.9375 2.28125,-3.5625 -2.65625,0 0,-6.40625 -3.0625,0 z m 9.6875,0 0,6.40625 -2.6875,0 2.28125,3.5625 1.90625,2.9375 1.9375,-2.9375 2.28125,-3.5625 -2.65625,0 0,-6.40625 -3.0625,0 z";
	private static final String ICON_ARROW_BOTH = "M 6.09375 0 L 4.125 2.9375 L 1.875 6.5 L 4.53125 6.5 L 4.53125 12.90625 L 7.59375 12.90625 L 7.59375 6.5 L 10.28125 6.5 L 8 2.9375 L 6.09375 0 z M 12.40625 7.09375 L 12.40625 13.5 L 9.71875 13.5 L 12 17.0625 L 13.90625 20 L 15.84375 17.0625 L 18.125 13.5 L 15.46875 13.5 L 15.46875 7.09375 L 12.40625 7.09375 z";
	
	private static final String SETUP_SUPPORTS = "ConveyorRawWorkPieceLayoutView.setUpSupports";
	private static final String ALL_SUPPORTS_DOWN = "ConveyorRawWorkPieceLayoutView.allSupportsDown";
	
	private static final float MAX_CONV_HEIGHT = 300;
	
	private static final float VISIBLE_AREA = 275;
	
	private static Logger logger = LogManager.getLogger(ConveyorRawWorkPieceLayoutView.class.getName());
	
	public ConveyorRawWorkPieceLayoutView() {
		this.tTransitions = new ArrayList<TranslateTransition>();
		this.workPieceWindows = new ArrayList<Rectangle>();
		this.workPieces = new ArrayList<Rectangle>();
		this.supports = new ArrayList<Rectangle>();
		this.texts = new ArrayList<Text>();
		this.conveyorGroup = new Group();
		this.p = new Pane();
		p.getChildren().add(conveyorGroup);
		getContents().add(p, 1, 0);
		df = new DecimalFormat("#.00");
		df.setDecimalSeparatorAlwaysShown(true);
	}
	
	public void setConveyorLayout(final ConveyorLayout conveyorLayout) {
		this.conveyorLayout = conveyorLayout;
	}
	
	@Override
	public void build() {
				
		getContents().setPadding(new Insets(0, 0, 0, 0));
		getContents().setHgap(0);
		
		vboxStatusControls = new VBox();
		getContents().add(vboxStatusControls, 0, 0);
		vboxStatusControls.setPrefWidth(590 - VISIBLE_AREA);
		vboxStatusControls.setAlignment(Pos.CENTER);
		
		//GridPane.setHgrow(vboxStatusControls, Priority.ALWAYS);
		
		iconNearby = new SVGPath();
		iconNearby.setContent(ICON_NEARBY_PATH);
		iconNearby.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		iconManual = new SVGPath();
		iconManual.setContent(ICON_MANUAL_MODE);
		iconManual.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		iconLock = new SVGPath();
		iconLock.setContent(ICON_LOCK);
		iconLock.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		iconRotating = new SVGPath();
		iconRotating.setContent(ICON_ROTATING_PATH);
		iconRotating.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		rt = new RotateTransition(Duration.millis(7500), iconRotating);
		rt.setByAngle(-360);
		rt.setInterpolator(Interpolator.LINEAR);
		rt.setAutoReverse(false);
		rt.setCycleCount(Transition.INDEFINITE);
		hboxStatus = new HBox();
		hboxStatus.setAlignment(Pos.CENTER);
		hboxStatus.setSpacing(15);
		hboxStatus.getChildren().add(iconManual);
		hboxStatus.getChildren().add(iconLock);
		hboxStatus.getChildren().add(iconRotating);
		hboxStatus.getChildren().add(iconNearby);
		hboxStatus.setPrefSize(160, 40);
		hboxStatus.setMaxSize(160,  40);
		hboxStatus.getStyleClass().add(CSS_CLASS_STATUS_AREA);
		VBox.setMargin(hboxStatus, new Insets(0, 0, 15, 0));
		vboxStatusControls.getChildren().add(hboxStatus);
		vboxStatusControls.setSpacing(10);
		
		btnConfigureSupports = createButton(ICON_ARROW_BOTH, CSS_CLASS_WHITE_ICON, Translator.getTranslation(SETUP_SUPPORTS), UIConstants.BUTTON_HEIGHT*4, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureSupports();
			}
		});
		btnAllSupportsDown = createButton(ICON_ARROW_DOWN, CSS_CLASS_WHITE_ICON, Translator.getTranslation(ALL_SUPPORTS_DOWN), UIConstants.BUTTON_HEIGHT*4, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().allSupportsDown();
			}
		});
		
		vboxStatusControls.getChildren().add(btnConfigureSupports);
		vboxStatusControls.getChildren().add(btnAllSupportsDown);
		
		createConveyorUsingShapes();
		
		refresh();	
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	public void hideButtons() {
		btnAllSupportsDown.setVisible(false);
		btnConfigureSupports.setVisible(false);
		btnAllSupportsDown.setManaged(false);
		btnConfigureSupports.setManaged(false);
	}
	
	private void createConveyorUsingShapes() {
		tTransitions.clear();
		workPieceWindows.clear();
		workPieces.clear();
		supports.clear();
		texts.clear();
		
		conveyorGroup.getChildren().clear();
		Rectangle total = new Rectangle();
		total.setX(70);
		total.setY(- conveyorLayout.getWidthRawWorkPieceConveyorWithOverlap());
		total.setWidth(VISIBLE_AREA);
		total.setHeight(conveyorLayout.getWidthRawWorkPieceConveyorWithOverlap());
		total.setOpacity(0);
		Rectangle bg = new Rectangle();
		bg.setX(74);
		bg.setY(- conveyorLayout.getWidthRawWorkPieceConveyor());
		bg.setWidth(VISIBLE_AREA - 4);
		bg.setHeight(conveyorLayout.getWidthRawWorkPieceConveyor());
		bg.getStyleClass().add(CSS_CLASS_CONVEYOR_BACKGROUND);		
		conveyorGroup.getChildren().add(total);
		conveyorGroup.getChildren().add(bg);
		// add support and space
		float ySupportFirst = conveyorLayout.getSupportWidth();
		float ySpaceBetweenFirst = conveyorLayout.getSpaceBetweenTracks() - (conveyorLayout.getSpaceBetweenTracks() - 
				conveyorLayout.getSupportWidth())/2;
		float yTrackFirst = ySpaceBetweenFirst + conveyorLayout.getRawTrackWidth();
		for (int i = 0; i < conveyorLayout.getRawTrackAmount(); i++) {
			Rectangle support = new Rectangle();
			support.setX(74);
			support.setWidth(VISIBLE_AREA+10);
			support.setY(-(i * (conveyorLayout.getRawTrackWidth() + conveyorLayout.getSpaceBetweenTracks()) +
					ySupportFirst));
			support.setHeight(conveyorLayout.getSupportWidth());
			support.setArcHeight(3);
			support.setArcWidth(3);
			// add supports, except for first one
			if (i > 0) {
				supports.add(support);
				support.getStyleClass().add(CSS_CLASS_SUPPORT_DOWN);
			} else {
				support.getStyleClass().add(CSS_CLASS_SUPPORT_FIXED);
			}
			Rectangle spaceBetween = new Rectangle();
			if (i == 0) {
				spaceBetween.setX(74);
				spaceBetween.setWidth(VISIBLE_AREA-4 + 10);
				spaceBetween.setY(-ySpaceBetweenFirst);
				spaceBetween.setHeight(ySpaceBetweenFirst);
			} else {
				spaceBetween.setX(74);
				spaceBetween.setWidth(VISIBLE_AREA-4 + 10);
				spaceBetween.setY(-(i * (conveyorLayout.getSpaceBetweenTracks() + conveyorLayout.getRawTrackWidth()) + 
						ySpaceBetweenFirst));
				spaceBetween.setHeight(conveyorLayout.getSpaceBetweenTracks());
			}
			spaceBetween.getStyleClass().add(CSS_CLASS_DISTANCE_BETWEEN_TRACKS);
			// add track
			Rectangle track = new Rectangle();
			track.setX(70);
			track.setWidth(VISIBLE_AREA + 10);
			track.setY(-(i * (conveyorLayout.getRawTrackWidth() + conveyorLayout.getSpaceBetweenTracks()) + yTrackFirst));
			track.setHeight(conveyorLayout.getRawTrackWidth());
			track.getStyleClass().add(CSS_CLASS_TRACK);
			Image img = new Image("images/pattern.png");
			Rectangle trackPattern = new Rectangle(); 
			trackPattern.setX(70);
			trackPattern.setWidth(VISIBLE_AREA-5 + 10);
			trackPattern.setY(-(i * (conveyorLayout.getRawTrackWidth() + conveyorLayout.getSpaceBetweenTracks()) + yTrackFirst));
			trackPattern.setHeight(conveyorLayout.getRawTrackWidth());
			trackPattern.setFill(new ImagePattern(img, 10, 0, 23, 10, false));
			TranslateTransition tt = new TranslateTransition(Duration.millis((23 /getPresenter().getConveyor().getNomSpeedRawConveyor()) * 60 * 1000), trackPattern);
			tt.setInterpolator(Interpolator.LINEAR);
			tt.setFromX(23);
			tt.setByX(-23);
			tt.setCycleCount(Transition.INDEFINITE);
			tt.setAutoReverse(false);
			tt.play();
			tt.pause();
			tTransitions.add(tt);
			// add text
			Text txt = new Text("0000.00");
			txt.setX(0);
			txt.setY(-(i * (conveyorLayout.getRawTrackWidth() + conveyorLayout.getSpaceBetweenTracks()) + yTrackFirst - conveyorLayout.getRawTrackWidth()));
			txt.setWrappingWidth(60);
			txt.getStyleClass().add(CSS_CLASS_DISTANCE_TEXT);
			texts.add(txt);
			conveyorGroup.getChildren().add(spaceBetween);
			conveyorGroup.getChildren().add(support);
			conveyorGroup.getChildren().add(track);
			conveyorGroup.getChildren().add(trackPattern);
			track.toFront();
			trackPattern.toFront();
			support.toBack();
			spaceBetween.toBack();
			conveyorGroup.getChildren().add(txt);
		}
		bg.toBack();
		total.toBack();
		double scaleX = (VISIBLE_AREA+70) / (conveyorGroup.getBoundsInParent().getWidth() - 10);
		double scaleY = MAX_CONV_HEIGHT / conveyorGroup.getBoundsInParent().getHeight();
		
		
		if (scaleX < scaleY) {
			conveyorGroup.setScaleX(scaleX);
			conveyorGroup.setScaleY(scaleX);
			StackPane.setMargin(conveyorGroup, new Insets(0, 0, 0, 30*scaleX));
		} else {
			conveyorGroup.setScaleX(scaleY);
			conveyorGroup.setScaleY(scaleY);
			StackPane.setMargin(conveyorGroup, new Insets(0, 0, 0, 30*scaleY));
		}
		
		double width = conveyorGroup.getBoundsInParent().getWidth();
		if (width < (VISIBLE_AREA+70)) {
			conveyorGroup.setTranslateX(-conveyorGroup.getBoundsInParent().getMinX() + (VISIBLE_AREA+70) - width);
		} else {
			conveyorGroup.setTranslateX(-conveyorGroup.getBoundsInParent().getMinX());

		}

		conveyorGroup.setTranslateY(-conveyorGroup.getBoundsInParent().getMinY());
		
		p.setPrefWidth((VISIBLE_AREA+70));
		p.setMinWidth((VISIBLE_AREA+70));
		p.setMaxWidth((VISIBLE_AREA+70));
		p.setPrefHeight(conveyorGroup.getBoundsInParent().getHeight());
		
		setMoving(false);
	}
	
	@Override
	public void refresh() {
		updateSupportStatus();
		conveyorGroup.getChildren().removeAll(workPieceWindows);
		conveyorGroup.getChildren().removeAll(workPieces);
		workPieceWindows.clear();
		workPieces.clear();
		double scale = conveyorGroup.getScaleX();
		double translateX = conveyorGroup.getTranslateX();
		double translateY = conveyorGroup.getTranslateY();
		conveyorGroup.setTranslateY(-translateY);
		conveyorGroup.setTranslateX(-translateX);
		conveyorGroup.setScaleY(1);
		conveyorGroup.setScaleX(1);
		for (StackingPosition stPos : conveyorLayout.getStackingPositionsRawWorkPieces()) {
			Rectangle wp = new Rectangle();
			conveyorGroup.getChildren().add(wp);
			wp.setLayoutX(200);
			wp.setY(-(stPos.getPosition().getY() + stPos.getWorkPiece().getDimensions().getWidth()/2 + conveyorLayout.getSupportWidth()));
			wp.setWidth(stPos.getWorkPiece().getDimensions().getLength());
			wp.setHeight(stPos.getWorkPiece().getDimensions().getWidth());
			wp.getStyleClass().add(CSS_CLASS_WORKPIECE);
			wp.setVisible(false);
			this.workPieces.add(wp);			
			
			Rectangle ra = new Rectangle();
			conveyorGroup.getChildren().add(ra);
			ra.setX(stPos.getPosition().getX() + 70 - stPos.getWorkPiece().getDimensions().getLength()/2);
			ra.setY(-(stPos.getPosition().getY() + stPos.getWorkPiece().getDimensions().getWidth()/2 + conveyorLayout.getSupportWidth()));
			ra.setWidth(stPos.getWorkPiece().getDimensions().getLength());
			ra.setHeight(stPos.getWorkPiece().getDimensions().getWidth());
			ra.getStyleClass().add(CSS_CLASS_WORKPIECE_AREA);
			
			TranslateTransition tt = new TranslateTransition();
			tt.setNode(wp);
			tt.stop();
			tt.setInterpolator(Interpolator.LINEAR);
			this.workPieceWindows.add(ra);
		}
		conveyorGroup.setScaleX(scale);
		conveyorGroup.setScaleY(scale);
		conveyorGroup.setTranslateX(translateX);
		conveyorGroup.setTranslateY(translateY);
		
		setSensorValues(getPresenter().getConveyor().getSensorValues());
		setConnected(getPresenter().getConveyor().isConnected());
		setModeManual(!getPresenter().getConveyor().isModeAuto());
		setMoving(getPresenter().getConveyor().isMovingRaw());
		setLocked(getPresenter().getConveyor().isInterlockRaw());
	}
	
	public void updateSupportStatus() {
		// set supports
		for (int i = 0; i < conveyorLayout.getCurrentSupportStatus().length; i++) {
			setSupport(supports.get(i), conveyorLayout.getCurrentSupportStatus()[i], conveyorLayout.getRequestedSupportStatus()[i]);
			/*if (conveyorLayout.getRequestedSupportStatus()[i]) {
				texts.get(i+1).setVisible(true);
			} else {
				texts.get(i+1).setVisible(false);
			}*/
		}
	}
	
	private void setSupport(final Rectangle rectangle, final boolean currentState, final boolean requestedState) {
		rectangle.getStyleClass().remove(CSS_CLASS_SUPPORT_DOWN);
		rectangle.getStyleClass().remove(CSS_CLASS_SUPPORT_DOWN_SHOULD_BE_UP);
		rectangle.getStyleClass().remove(CSS_CLASS_SUPPORT_UP);
		rectangle.getStyleClass().remove(CSS_CLASS_SUPPORT_UP_SHOULD_BE_DOWN);
		if (requestedState) {
			if (currentState) {
				rectangle.getStyleClass().add(CSS_CLASS_SUPPORT_UP);
			} else {
				rectangle.getStyleClass().add(CSS_CLASS_SUPPORT_DOWN_SHOULD_BE_UP);
			}
		} else {
			if (currentState) {
				rectangle.getStyleClass().add(CSS_CLASS_SUPPORT_UP_SHOULD_BE_DOWN);
			} else {
				rectangle.getStyleClass().add(CSS_CLASS_SUPPORT_DOWN);
			}
		}
	}
	
	public void setMoving(final boolean moving) {
		iconRotating.getStyleClass().remove(CSS_CLASS_STATUS_ICON);
		iconRotating.getStyleClass().remove(CSS_CLASS_STATUS_ICON_DISABLED);
		if (moving) {
			for (TranslateTransition tt : tTransitions) {
				tt.play();
			}
			rt.play();
			iconRotating.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		} else {
			for (TranslateTransition tt : tTransitions) {
				tt.pause();
			}
			rt.pause();
			iconRotating.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		}
	}
	
	public void setConnected(final boolean connected) {
		if (connected) {
			hboxStatus.setDisable(false);
			conveyorGroup.setOpacity(1);
			hboxStatus.setOpacity(1);
			btnAllSupportsDown.setDisable(false);
			btnConfigureSupports.setDisable(false);
		} else {
			hboxStatus.setDisable(true);
			setMoving(false);
			setLocked(false);
			setModeManual(false);
			conveyorGroup.setOpacity(0.4);
			hboxStatus.setOpacity(0.4);
		}
	}
	
	public void setModeManual(final boolean modeManual) {
		iconManual.getStyleClass().remove(CSS_CLASS_STATUS_ICON);
		iconManual.getStyleClass().remove(CSS_CLASS_STATUS_ICON_DISABLED);
		if (modeManual) {
			iconManual.getStyleClass().add(CSS_CLASS_STATUS_ICON);
			if (getPresenter().getConveyor().isConnected()) {
				btnAllSupportsDown.setDisable(false);
				btnConfigureSupports.setDisable(false);
			}
		} else {
			iconManual.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
			btnAllSupportsDown.setDisable(true);
			btnConfigureSupports.setDisable(true);
		}
	}
	
	public void setLocked(final boolean locked) {
		iconLock.getStyleClass().remove(CSS_CLASS_STATUS_ICON);
		iconLock.getStyleClass().remove(CSS_CLASS_STATUS_ICON_DISABLED);
		if (locked) {
			iconLock.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		} else {
			iconLock.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		}
	}
	
	public void setSensorValues(final List<Integer> sensorValues) {
		boolean found = false;
		int wpIndex = 0;
		for (int i = 0; i < sensorValues.size(); i++) {
			if (sensorValues.get(i) > 0) {
				texts.get(i).setText(df.format(((float) sensorValues.get(i))/100));
			} else {
				texts.get(i).setText("--");
			}
			if ((i == 0)  || (conveyorLayout.getRequestedSupportStatus()[i-1])) {
				if (wpIndex < workPieces.size()) {	// prevents from updating when workpieces are not yet recalculated but supports are
					double dest = ((float) sensorValues.get(i))/100 + 70; 
					// check if other sensor indicates smaller value
					int j = 1;
					boolean foundThis = false;
					if (sensorValues.get(i) > 0) {
						logger.info("found: " + sensorValues.get(i));
						foundThis = true;
					}
					while ((i+j-1) < conveyorLayout.getRequestedSupportStatus().length && !conveyorLayout.getRequestedSupportStatus()[i-1+j]) {
						if (((sensorValues.get(j+i) < sensorValues.get(i)) || (sensorValues.get(i) == 0)) && (sensorValues.get(j+i) > 0)) {
							dest = ((float) sensorValues.get(i+j))/100 + 70;
							if (sensorValues.get(i+j) > 0) {
								foundThis = true;
								logger.info("found: " + sensorValues.get(i+j) + " - " + j + " - " + wpIndex);
							}
						}
						j++;
					}
					
					workPieces.get(wpIndex).setLayoutX(dest);
					if (foundThis) {
						workPieces.get(wpIndex).setVisible(true);
						found = true;
					} else {
						workPieces.get(wpIndex).setVisible(false);
					}
					wpIndex++;
				}
			}
		}
		iconNearby.getStyleClass().remove(CSS_CLASS_STATUS_ICON);
		iconNearby.getStyleClass().remove(CSS_CLASS_STATUS_ICON_DISABLED);
		if (found) {
			iconNearby.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		} else {
			iconNearby.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		}
	}
}
