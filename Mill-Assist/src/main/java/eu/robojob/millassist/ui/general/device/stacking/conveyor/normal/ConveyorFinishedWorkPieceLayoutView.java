package eu.robojob.millassist.ui.general.device.stacking.conveyor.normal;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorLayout;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractMenuPresenter;

public class ConveyorFinishedWorkPieceLayoutView extends AbstractWorkPieceLayoutView<ConveyorFinishedWorkPieceLayoutPresenter<? extends AbstractMenuPresenter<?>>> {

	private VBox vboxStatusControls;
	private HBox hboxStatus;
	private SVGPath iconManual;
	private SVGPath iconLock;
	private SVGPath iconRotating;
	private RotateTransition rt;

	private ConveyorLayout conveyorLayout;
	
	private List<Rectangle> workPieceWindows;

	private TranslateTransition tt;
	private TranslateTransition ttShift;
	private FadeTransition ftShift;
	
	private Group gpWorkPieces;
	
	private List<Rectangle> workPieces;
	private Group conveyorGroup;
	private Pane p;
		
	private static final String CSS_CLASS_STATUS_ICON = "status-icon";
	private static final String CSS_CLASS_STATUS_ICON_DISABLED = "status-icon-disabled";
	private static final String CSS_CLASS_STATUS_AREA = "status-area";
	private static final String CSS_CLASS_TRACK = "track";	
	private static final String CSS_CLASS_CONVEYOR_BACKGROUND = "conveyor-background";
	private static final String CSS_CLASS_WORKPIECE_AREA = "workPiece-area";
	private static final String CSS_CLASS_WORKPIECE  = "workpiece-c-finished";
	
	private static final String ICON_MANUAL_MODE = "M 10 0 C 4.4771526 0 0 4.477153 0 10 C 0 15.522847 4.4771526 20 10 20 C 15.522847 20 20 15.522847 20 10 C 20 4.477153 15.522847 0 10 0 z M 10 2.5 C 14.142136 2.5 17.5 5.857864 17.5 10 C 17.5 14.142136 14.142136 17.5 10 17.5 C 5.8578645 17.5 2.5 14.142136 2.5 10 C 2.5 5.857864 5.8578645 2.5 10 2.5 z M 5.84375 5.6875 L 5.84375 14.3125 L 7.53125 14.3125 L 7.53125 7.09375 L 9.125 14.3125 L 10.90625 14.3125 L 12.5 7.09375 L 12.5 14.3125 L 14.1875 14.3125 L 14.1875 5.6875 L 11.5625 5.6875 L 10.0625 12.4375 L 8.46875 5.6875 L 5.84375 5.6875 z";
	private static final String ICON_LOCK = "M 9.78125 0 C 6.80264 0 4.40625 2.52069 4.40625 5.5 L 4.40625 7.90625 C 4.05894 8.00555 3.75 8.09375 3.75 8.09375 C 3.6724 8.11965 2.8125 8.37272 2.8125 8.90625 L 2.8125 19.3125 C 2.8125 19.75537 3.21949 20 3.75 20 L 16.25 20 C 16.77957 20 17.1875 19.75537 17.1875 19.3125 L 17.1875 8.90625 C 17.1875 8.46409 16.76162 8.2935 16.25 8.09375 C 16.25 8.09375 16.0203 8.01791 15.59375 7.90625 L 15.59375 5.5 C 15.59375 2.52069 13.19713 0 10.21875 0 L 9.78125 0 z M 10 1.96875 C 11.9218 1.96875 13.1875 3.54696 13.1875 5.46875 L 13.1875 7.46875 C 12.3125 7.36783 11.26263 7.3125 10 7.3125 C 8.7383 7.3125 7.61245 7.36787 6.8125 7.46875 L 6.8125 5.46875 C 6.8125 3.54765 8.07936 1.96875 10 1.96875 z M 10 10.28125 C 10.96404 10.28125 11.75 11.06721 11.75 12.03125 C 11.75 12.54871 11.51357 13.08716 11.15625 13.40625 L 11.75 16.90625 C 11.75 16.90625 11.23442 17.3125 10 17.3125 C 8.7665 17.3125 8.25 16.90625 8.25 16.90625 L 8.84375 13.40625 C 8.48503 13.08762 8.25 12.54941 8.25 12.03125 C 8.25 11.06698 9.03688 10.28125 10 10.28125 z";
	private static final String ICON_ROTATING_PATH = "M 9.21875 0 L 9.21875 1.65625 C 6.78895 1.88945 4.5416 3.1627 3.125 5.1875 L 4.90625 6.4375 C 5.91405 4.9975 7.49995 4.06635 9.21875 3.84375 L 9.21875 5.4375 L 13.90625 2.71875 L 9.21875 0 z M 14.8125 3.125 L 13.5625 4.90625 C 15.0027 5.91525 15.93225 7.50255 16.15625 9.21875 L 14.5625 9.21875 L 17.28125 13.90625 L 20 9.21875 L 18.34375 9.21875 C 18.10935 6.79035 16.8359 4.5442 14.8125 3.125 z M 2.71875 6.09375 L 0 10.78125 L 1.65625 10.78125 C 1.88805 13.21085 3.1615 15.4584 5.1875 16.875 L 6.4375 15.09375 C 4.9975 14.08715 4.06775 12.49885 3.84375 10.78125 L 5.4375 10.78125 L 2.71875 6.09375 z M 15.09375 13.5625 C 14.08595 15.0013 12.49485 15.93225 10.78125 16.15625 L 10.78125 14.5625 L 6.09375 17.28125 L 10.8125 20 L 10.8125 18.34375 C 13.2397 18.11075 15.4584 16.8345 16.875 14.8125 L 15.09375 13.5625 z";
	
	private static final float MAX_CONV_HEIGHT = 300;
	
	private static final float VISIBLE_AREA = 275;
		
	public ConveyorFinishedWorkPieceLayoutView() {
		this.conveyorGroup = new Group();
		this.workPieces = new ArrayList<Rectangle>();
		this.workPieceWindows = new ArrayList<Rectangle>();
		this.gpWorkPieces = new Group();
		this.p = new Pane();
		p.getChildren().add(conveyorGroup);
		getContents().add(p, 1, 0);
	}
	
	public void setConveyorLayout(final ConveyorLayout conveyorLayout) {
		this.conveyorLayout = conveyorLayout;
	}
	
	@Override
	protected void build() {
		vboxStatusControls = new VBox();
		getContents().add(vboxStatusControls, 0, 0);
		vboxStatusControls.setPrefWidth(300);
		vboxStatusControls.setAlignment(Pos.CENTER);
		
		iconManual = new SVGPath();
		iconManual.setContent(ICON_MANUAL_MODE);
		iconManual.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		iconLock = new SVGPath();
		iconLock.setContent(ICON_LOCK);
		iconLock.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		iconRotating = new SVGPath();
		iconRotating.setContent(ICON_ROTATING_PATH);
		iconRotating.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		hboxStatus = new HBox();
		hboxStatus.setAlignment(Pos.CENTER);
		hboxStatus.setSpacing(15);
		hboxStatus.setPrefSize(160, 40);
		hboxStatus.setMaxSize(160,  40);
		hboxStatus.getStyleClass().add(CSS_CLASS_STATUS_AREA);
		hboxStatus.getChildren().add(iconManual);
		hboxStatus.getChildren().add(iconLock);
		hboxStatus.getChildren().add(iconRotating);
		
		VBox.setMargin(hboxStatus, new Insets(0, 0, 15, 0));
		vboxStatusControls.getChildren().add(hboxStatus);
		vboxStatusControls.setSpacing(10);
		
		createConveyorUsingShapes();
		
		rt = new RotateTransition(Duration.millis(6000), iconRotating);
		rt.setInterpolator(Interpolator.LINEAR);
		rt.setCycleCount(Animation.INDEFINITE);
		rt.setByAngle(360);
		
		setMoving(false);
		refresh();
	}
	
	private void createConveyorUsingShapes() {
		workPieces.clear();
		conveyorGroup.getChildren().clear();

		Rectangle total = new Rectangle();
		total.setX(0);
		total.setY(- conveyorLayout.getWidthFinishedWorkPieceConveyorWithOverlap());
		total.setWidth(VISIBLE_AREA);
		total.setHeight(conveyorLayout.getWidthFinishedWorkPieceConveyorWithOverlap());
		total.setOpacity(0);
		Rectangle bg = new Rectangle();
		bg.setX(4);
		bg.setY(- conveyorLayout.getWidthFinishedWorkPieceConveyor());
		bg.setWidth(VISIBLE_AREA - 4);
		bg.setHeight(conveyorLayout.getWidthFinishedWorkPieceConveyor());
		bg.getStyleClass().add(CSS_CLASS_CONVEYOR_BACKGROUND);		
		conveyorGroup.getChildren().add(total);
		conveyorGroup.getChildren().add(bg);
		
		Rectangle track = new Rectangle();
		track.setX(0);
		track.setWidth(VISIBLE_AREA + 10);
		track.setY(- conveyorLayout.getFinishedConveyorWidth());
		track.setHeight(conveyorLayout.getFinishedConveyorWidth());
		track.getStyleClass().add(CSS_CLASS_TRACK);
		Image img = new Image("images/pattern.png");
		Rectangle trackPattern = new Rectangle(); 
		trackPattern.setX(0);
		trackPattern.setWidth(VISIBLE_AREA-5 + 10);
		trackPattern.setY(- conveyorLayout.getFinishedConveyorWidth());
		trackPattern.setHeight(conveyorLayout.getFinishedConveyorWidth());
		trackPattern.setFill(new ImagePattern(img, 10, 0, 23, 10, false));
		tt = new TranslateTransition(Duration.millis((23 /getPresenter().getConveyor().getNomSpeedFinishedConveyor()) * 60 * 1000), trackPattern);
		tt.setInterpolator(Interpolator.LINEAR);
		tt.setFromX(0);
		tt.setByX(23);
		tt.setCycleCount(Transition.INDEFINITE);
		tt.setAutoReverse(false);
		
		conveyorGroup.getChildren().add(track);
		conveyorGroup.getChildren().add(trackPattern);
		track.toFront();
		trackPattern.toFront();
		bg.toBack();
		total.toBack();
		
		double scaleX = (VISIBLE_AREA) / (conveyorGroup.getBoundsInParent().getWidth() - 10);
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
		if (width < (VISIBLE_AREA)) {
			conveyorGroup.setTranslateX((-conveyorGroup.getBoundsInParent().getMinX() + (VISIBLE_AREA) - width));
		} else {
			conveyorGroup.setTranslateX(-conveyorGroup.getBoundsInParent().getMinX());
		}
		
		conveyorGroup.setTranslateY(-conveyorGroup.getBoundsInParent().getMinY());
		
		p.setPrefWidth((VISIBLE_AREA));
		p.setMinWidth((VISIBLE_AREA));
		p.setMaxWidth((VISIBLE_AREA));
		p.setPrefHeight(conveyorGroup.getBoundsInParent().getHeight());
		
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		conveyorGroup.getChildren().removeAll(workPieceWindows);
		conveyorGroup.getChildren().removeAll(workPieces);
		conveyorGroup.getChildren().remove(gpWorkPieces);
		workPieceWindows.clear();
		workPieces.clear();
		gpWorkPieces.getChildren().clear();
		double scale = conveyorGroup.getScaleX();
		double translateX = conveyorGroup.getTranslateX();
		double translateY = conveyorGroup.getTranslateY();
		conveyorGroup.setTranslateY(-translateY);
		conveyorGroup.setTranslateX(-translateX);
		conveyorGroup.setScaleY(1);
		conveyorGroup.setScaleX(1);
		conveyorGroup.getChildren().add(gpWorkPieces);
		
		if (ttShift != null) {
			ttShift.stop();
		}
		if (ftShift != null) {
			ftShift.stop();
		}
		
		gpWorkPieces.setTranslateX(0.0);
		gpWorkPieces.setOpacity(1.0);
		
		for (StackingPosition stPos : conveyorLayout.getStackingPositionsFinishedWorkPieces()) {
			
			Rectangle wp = new Rectangle();
			gpWorkPieces.getChildren().add(wp);
			if (conveyorLayout.isLeftSetup()) {
				wp.setY(-(stPos.getPosition().getY() + stPos.getWorkPiece().getDimensions().getWidth()/2));
				wp.setLayoutX(stPos.getPosition().getX() - stPos.getWorkPiece().getDimensions().getLength()/2);
			} else {
				wp.setY(-(stPos.getPosition().getX() + stPos.getWorkPiece().getDimensions().getWidth()/2));
				wp.setLayoutX(stPos.getPosition().getY() - stPos.getWorkPiece().getDimensions().getLength()/2);
			}
			wp.setWidth(stPos.getWorkPiece().getDimensions().getLength());
			wp.setHeight(stPos.getWorkPiece().getDimensions().getWidth());
			wp.getStyleClass().add(CSS_CLASS_WORKPIECE);
			wp.setVisible(false);
			this.workPieces.add(wp);			
			
			Rectangle ra = new Rectangle();
			conveyorGroup.getChildren().add(ra);
			if (conveyorLayout.isLeftSetup()) {
				ra.setX(stPos.getPosition().getX() - stPos.getWorkPiece().getDimensions().getLength()/2);
				ra.setY(-(stPos.getPosition().getY() + stPos.getWorkPiece().getDimensions().getWidth()/2));
			} else {
				ra.setX(stPos.getPosition().getY() - stPos.getWorkPiece().getDimensions().getLength()/2);
				ra.setY(-(stPos.getPosition().getX() + stPos.getWorkPiece().getDimensions().getWidth()/2));
			}
			ra.setWidth(stPos.getWorkPiece().getDimensions().getLength());
			ra.setHeight(stPos.getWorkPiece().getDimensions().getWidth());
			ra.getStyleClass().add(CSS_CLASS_WORKPIECE_AREA);
			this.workPieceWindows.add(ra);
			
		}
		
		for (int i = 0; i < workPieces.size(); i++) {
			boolean present = conveyorLayout.getFinishedStackingPositionWorkPieces().get(i);
			if (present) {
				workPieces.get(i).setVisible(true);
			} else {
				workPieces.get(i).setVisible(false);
			}
		}
		
		conveyorGroup.setScaleX(scale);
		conveyorGroup.setScaleY(scale);
		conveyorGroup.setTranslateX(translateX);
		conveyorGroup.setTranslateY(translateY);
		setConnected(getPresenter().getConveyor().isConnected());
		setModeManual(!getPresenter().getConveyor().isModeAuto());
		setMoving(getPresenter().getConveyor().isMovingRaw());
		setLocked(getPresenter().getConveyor().isInterlockRaw());
	}
	
	public List<Rectangle> getWorkPieces() {
		return workPieces;
	}

	public void setConnected(final boolean connected) {
		if (connected) {
			hboxStatus.setDisable(false);
			conveyorGroup.setOpacity(1);
			hboxStatus.setOpacity(1);
		} else {
			hboxStatus.setDisable(true);
			setMoving(false);
			setLocked(false);
			setModeManual(false);
			conveyorGroup.setOpacity(0.4);
			hboxStatus.setOpacity(0.4);
		}
	}

	public void setMoving(final boolean moving) {
		iconRotating.getStyleClass().remove(CSS_CLASS_STATUS_ICON);
		iconRotating.getStyleClass().remove(CSS_CLASS_STATUS_ICON_DISABLED);
		if (moving) {
			rt.play();
			tt.play();
			iconRotating.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		} else {
			rt.pause();
			tt.pause();
			iconRotating.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
		}
	}
	
	public void shiftFinishedWorkPieces(final float distance) {
		ttShift = new TranslateTransition(Duration.millis((distance /getPresenter().getConveyor().getNomSpeedFinishedConveyor()) * 60 * 1000), gpWorkPieces);
		ttShift.setInterpolator(Interpolator.LINEAR);
		ttShift.setFromX(0);
		ttShift.setByX(distance);
		ttShift.setCycleCount(1);
		ftShift = new FadeTransition(Duration.millis((distance /getPresenter().getConveyor().getNomSpeedFinishedConveyor()) * 60 * 1000), gpWorkPieces);
		ftShift.setFromValue(1.0);
		ftShift.setToValue(0.0);
		ftShift.setCycleCount(1);
		ttShift.play();
		ftShift.play();
	}
	
	public void setModeManual(final boolean modeManual) {
		iconManual.getStyleClass().remove(CSS_CLASS_STATUS_ICON);
		iconManual.getStyleClass().remove(CSS_CLASS_STATUS_ICON_DISABLED);
		if (modeManual) {
			iconManual.getStyleClass().add(CSS_CLASS_STATUS_ICON);
		} else {
			iconManual.getStyleClass().add(CSS_CLASS_STATUS_ICON_DISABLED);
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
}
