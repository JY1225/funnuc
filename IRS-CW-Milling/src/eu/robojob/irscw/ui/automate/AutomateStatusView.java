package eu.robojob.irscw.ui.automate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import eu.robojob.irscw.ui.general.status.StatusView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class AutomateStatusView extends HBox {
	
	public static final int WIDTH = 800;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH_BOTTOM_RIGHT = 230;
	public static final int HEIGHT_BOTTOM_RIGHT_TOP = 230;
	public static final int HEIGHT_BOTTOM_LEFT_TOP = 230;
	public static final int PROGRESS_RADIUS = 80;
	public static final int PROGRESS_RADIUS_INNER = 1;
	public static final int PROGRESS_RADIUS_INNER_CIRCLE = 74;
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3;
	private static final double BTN_HEIGHT = 40;
	private static final int TIMING_STATUS_WIDTH = 500;
	
	private int totalAmount;
	private int finishedAmount;
	private AutomateStatusPresenter presenter;

	private StackPane bottomRight;
	private StackPane bottomLeft;
	private VBox vboxBottomLeft;
	private VBox vboxBottomRight;
	private StatusView statusView;
	private TimingView timingView;
	private StackPane spButton;
	private Button btnCancel;
	private Button btnStart;
	private Button btnContinue;
	private Path piePiecePath;
	private Pane piePiecePane;
	private Label lblFinishedAmount;
	private Label lblTotalAmount;
	private Circle circleBack;
	private Circle circleFront;
	private StackPane spAmountContents;
	
	private static final String CSS_CLASS_AUTOMATE_BOTTOM = "content-bottom";
	protected static final String CSS_CLASS_AUTOMATE_BUTTON_TEXT = "automate-btn-text";
	protected static final String CSS_CLASS_AUTOMATE_BUTTON = "form-button";
	private static final String CSS_CLASS_CIRCLE_BACK = "circle-back";
	private static final String CSS_CLASS_CIRCLE_FRONT = "circle-front";
	private static final String CSS_CLASS_PROGRESS = "progress";
	private static final String CSS_CLASS_TOTAL_AMOUNT = "total-amount";
	private static final String CSS_CLASS_FINISHED_AMOUNT = "finished-amount";
	
	private static final String STOP = "StatusView.stop";
	private static final String START = "AutomateView.start";
	private static final String CONTINUE = "AutomateView.continue";
	
	public AutomateStatusView() {
	}
	
	public void setStatusView(final StatusView statusView) {
		this.statusView = statusView;
	}
	
	public void setTimingView(final TimingView timingView) {
		this.timingView = timingView;
	}
	
	public void setPresenter(final AutomateStatusPresenter presenter) {
		this.presenter = presenter;
	}
	
	public void build() {
		setPrefSize(WIDTH, HEIGHT_BOTTOM);
		getStyleClass().add(CSS_CLASS_AUTOMATE_BOTTOM);
		setAlignment(Pos.CENTER_LEFT);
		bottomRight = new StackPane();
		bottomRight.setPrefSize(WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM);
		bottomLeft = new StackPane();
		bottomLeft.setPrefSize(WIDTH - WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM);
		getChildren().add(bottomLeft);
		getChildren().add(bottomRight);
		
		vboxBottomLeft = new VBox();
		vboxBottomLeft.setPrefSize(WIDTH - WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM);
		vboxBottomLeft.getChildren().add(statusView);
		vboxBottomLeft.getChildren().add(timingView);
		vboxBottomLeft.setAlignment(Pos.TOP_CENTER);
		statusView.setWidth(TIMING_STATUS_WIDTH);
		statusView.setPrefHeight(HEIGHT_BOTTOM_LEFT_TOP);
		statusView.setMinHeight(HEIGHT_BOTTOM_LEFT_TOP);
		bottomLeft.getChildren().clear();
		bottomLeft.getChildren().add(vboxBottomLeft);
		bottomLeft.setAlignment(Pos.CENTER);
		timingView.setWidth(TIMING_STATUS_WIDTH);
		timingView.setPrefHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_LEFT_TOP);
		timingView.setMinHeight(HEIGHT_BOTTOM - HEIGHT_BOTTOM_LEFT_TOP);
		
		StackPane spAmount = new StackPane();
		spAmountContents = new StackPane();
		circleBack = new Circle();
		circleBack.setCenterX(PROGRESS_RADIUS);
		circleBack.setCenterY(PROGRESS_RADIUS);
		circleBack.setRadius(PROGRESS_RADIUS);
		circleBack.getStyleClass().add(CSS_CLASS_CIRCLE_BACK);
		circleFront = new Circle();
		circleFront.setCenterX(PROGRESS_RADIUS);
		circleFront.setCenterY(PROGRESS_RADIUS);
		circleFront.setRadius(PROGRESS_RADIUS_INNER_CIRCLE);
		circleFront.getStyleClass().add(CSS_CLASS_CIRCLE_FRONT);
		piePiecePath = new Path();
		piePiecePath.getStyleClass().add(CSS_CLASS_PROGRESS);
		piePiecePane = new Pane();
		piePiecePane.getChildren().add(circleBack);
		piePiecePane.getChildren().add(piePiecePath);
		piePiecePane.getChildren().add(circleFront);
		piePiecePane.setPrefSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		piePiecePane.setMinSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		piePiecePane.setMaxSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		spAmountContents.setPrefSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		spAmountContents.setMaxSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		spAmountContents.setMinSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		spAmountContents.setAlignment(Pos.CENTER);
		spAmount.setPrefSize(WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM_RIGHT_TOP);
		spAmount.setAlignment(Pos.CENTER);
		spAmountContents.getChildren().add(piePiecePane);
		lblTotalAmount = new Label();
		lblTotalAmount.getStyleClass().add(CSS_CLASS_TOTAL_AMOUNT);
		lblFinishedAmount = new Label();
		lblFinishedAmount.getStyleClass().add(CSS_CLASS_FINISHED_AMOUNT);
		lblFinishedAmount.setPrefSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		lblFinishedAmount.setMinSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		lblFinishedAmount.setMaxSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		StackPane.setAlignment(lblFinishedAmount, Pos.TOP_RIGHT);
		StackPane.setAlignment(lblTotalAmount, Pos.CENTER);
		spAmountContents.getChildren().add(lblFinishedAmount);
		spAmountContents.getChildren().add(lblTotalAmount);
		spAmount.getChildren().add(spAmountContents);
		
		StackPane.setMargin(lblTotalAmount, new Insets(95, 0, 0, 30));
		
		spButton = new StackPane();
		spButton.setPrefSize(WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM - HEIGHT_BOTTOM_RIGHT_TOP);
		btnCancel = new Button();
		Text txtCancel = new Text(Translator.getTranslation(STOP));
		txtCancel.getStyleClass().add(CSS_CLASS_AUTOMATE_BUTTON_TEXT);
		btnCancel.setGraphic(txtCancel);
		btnCancel.getStyleClass().add(CSS_CLASS_AUTOMATE_BUTTON);
		btnCancel.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.stopRunning();
			}
		});
		btnStart = new Button();
		Text txtStart = new Text(Translator.getTranslation(START));
		txtStart.getStyleClass().add(CSS_CLASS_AUTOMATE_BUTTON_TEXT);
		btnStart.setGraphic(txtStart);
		btnStart.getStyleClass().add(CSS_CLASS_AUTOMATE_BUTTON);
		btnStart.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.startAutomate();
			}
		});
		btnContinue = new Button();
		Text txtContinue = new Text(Translator.getTranslation(CONTINUE));
		txtContinue.getStyleClass().add(CSS_CLASS_AUTOMATE_BUTTON_TEXT);
		btnContinue.setGraphic(txtContinue);
		btnContinue.getStyleClass().add(CSS_CLASS_AUTOMATE_BUTTON);
		btnContinue.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnContinue.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.continueAutomate();
			}
		});
		spButton.setAlignment(Pos.CENTER);
		activateStartButton();
		
		vboxBottomRight = new VBox();
		vboxBottomRight.getChildren().add(spAmount);
		vboxBottomRight.getChildren().add(spButton);
		bottomRight.getChildren().add(vboxBottomRight);
		
		setPercentage(0);
	}
	
	public void activateStartButton() {
		spButton.getChildren().clear();
		spButton.getChildren().add(btnStart);
	}
	
	public void activateContinueButton() {
		spButton.getChildren().clear();
		spButton.getChildren().add(btnContinue);
	}
	
	public void activateStopButton() {
		spButton.getChildren().clear();
		spButton.getChildren().add(btnCancel);
	}
	
	public void setTotalAmount(final int amount) {
		totalAmount = amount;
		lblTotalAmount.setText("/" + amount);
	}
	
	public void setFinishedAmount(final int amount) {
		finishedAmount = amount;
		lblFinishedAmount.setText("" + amount);
		if ((totalAmount >= 0) && (finishedAmount >= 0)) {
			setPercentage((int) Math.floor(((double) finishedAmount / (double) totalAmount) * 100));
		}
	}
	
	private void setPercentage(final int percentage) {
		
		double percentaged = percentage;
		
		if (percentage == 100) {
			percentaged = 99.999;
		}
		
		if ((percentaged < 0) || (percentaged > 100)) {
			throw new IllegalArgumentException("Illegal percentage value: [" + percentage + "]");
		}
		
		piePiecePath.getElements().clear();
		piePiecePath.getTransforms().clear();
		
		double endX = 0;
		double endY = 0;
		double endXInner = 0;
		double endYInner = 0;
		double corner = ((percentaged) / 100) * (Math.PI * 2);
		
		endX = PROGRESS_RADIUS * Math.sin(corner);
		endXInner = PROGRESS_RADIUS_INNER * Math.sin(corner);
		endY = PROGRESS_RADIUS * Math.cos(corner);
		endYInner = PROGRESS_RADIUS_INNER * Math.cos(corner);
		
		MoveTo moveTo = new MoveTo(0, -PROGRESS_RADIUS);
		LineTo vLine = new LineTo(0, -PROGRESS_RADIUS_INNER);
		ArcTo innerArc = new ArcTo();
		innerArc.setX(endXInner);
		innerArc.setY(-endYInner);
		innerArc.setRadiusX(PROGRESS_RADIUS_INNER);
		innerArc.setRadiusY(PROGRESS_RADIUS_INNER);
		if (percentage > 50) {
			innerArc.setLargeArcFlag(true);
		} else {
			innerArc.setLargeArcFlag(false);
		}
		innerArc.setSweepFlag(true);
		
		LineTo dLine = new LineTo(endX, -endY);
		MoveTo moveTo2 = new MoveTo(endX, -endY);
		ArcTo arc = new ArcTo();
		arc.setX(0);
		arc.setY(-PROGRESS_RADIUS);
		arc.setRadiusX(PROGRESS_RADIUS);
		arc.setRadiusY(PROGRESS_RADIUS);
		if (percentage > 50) {
			arc.setLargeArcFlag(true);
		} else {
			arc.setLargeArcFlag(false);
		}
		arc.setSweepFlag(false);
		
		piePiecePath.getElements().add(moveTo);
		piePiecePath.getElements().add(vLine);
		piePiecePath.getElements().add(innerArc);
		piePiecePath.getElements().add(dLine);
		piePiecePath.getElements().add(moveTo2);
		piePiecePath.getElements().add(arc);
		piePiecePath.getTransforms().add(new Translate(PROGRESS_RADIUS, PROGRESS_RADIUS));
	}
}
