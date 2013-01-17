package eu.robojob.irscw.ui.automate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import eu.robojob.irscw.ui.general.flow.ProcessFlowView;
import eu.robojob.irscw.ui.general.status.StatusView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class AutomateView extends VBox {

	public static final int HEIGHT_TOP = 245;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH = 800;
	public static final int WIDTH_BOTTOM_RIGHT = 230;
	public static final int HEIGHT_BOTTOM_RIGHT_TOP = 230;
	public static final int HEIGHT_BOTTOM_LEFT_TOP = 230;
	public static final int PROGRESS_RADIUS = 70;
	public static final int PROGRESS_RADIUS_INNER = 66;
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3;
	private static final double BTN_HEIGHT = 40;
	private static final int TIMING_STATUS_WIDTH = 500;
	
	private int totalAmount;
	private int finishedAmount;
	
	private StackPane top;
	private HBox bottom;
	private StackPane bottomRight;
	private StackPane bottomLeft;
	private VBox vboxBottomLeft;
	private VBox vboxBottomRight;
	private StatusView statusView;
	private TimingView timingView;
	private StackPane spButton;
	private Button btnCancel;
	private Button btnStart;
	private ProcessFlowView processFlowView;
	private Label lblFinishedAmount;
	private Label lblTotalAmount;
	private Region circleBack;
	private Region circleFront;
	private Path piePiecePath;
	private AutomatePresenter presenter;
	
	private static final String CSS_CLASS_AUTOMATE_BOTTOM = "automate-bottom";
	protected static final String CSS_CLASS_AUTOMATE_BUTTON_TEXT = "automate-btn-text";
	protected static final String CSS_CLASS_AUTOMATE_BUTTON = "automate-btn";
	private static final String CSS_CLASS_CIRCLE_BACK = "circle-back";
	private static final String CSS_CLASS_CIRCLE_FRONT = "circle-front";
	private static final String CSS_CLASS_PROGRESS = "progress";
	private static final String CSS_CLASS_TOTAL_AMOUNT = "total-amount";
	private static final String CSS_CLASS_FINISHED_AMOUNT = "finished-amount";
	
	private static final String STOP = "StatusView.stop";
	private static final String START = "AutomateView.start";
	
	public AutomateView() {
	}
	
	public void setPresenter(final AutomatePresenter presenter) {
		this.presenter = presenter;
	}
	
	public void setProcessFlowView(final ProcessFlowView processFlowView) {
		this.processFlowView = processFlowView;
	}
	
	public void setStatusView(final StatusView statusView) {
		this.statusView = statusView;
	}
	
	public void setTimingView(final TimingView timingView) {
		this.timingView = timingView;
	}
	
	public void build() {
		top = new StackPane();
		top.setPrefSize(WIDTH, HEIGHT_TOP);
		top.getChildren().add(processFlowView);
		bottom = new HBox();
		bottom.setPrefSize(WIDTH, HEIGHT_BOTTOM);
		bottom.getStyleClass().add(CSS_CLASS_AUTOMATE_BOTTOM);
		bottom.setAlignment(Pos.CENTER_LEFT);
		bottomRight = new StackPane();
		bottomRight.setPrefSize(WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM);
		bottomLeft = new StackPane();
		bottomLeft.setPrefSize(WIDTH - WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM);
		bottom.getChildren().add(bottomLeft);
		bottom.getChildren().add(bottomRight);
		
		getChildren().add(top);
		getChildren().add(bottom);
		
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
		StackPane spAmountContents = new StackPane();
		circleBack = new Region();
		circleBack.setPrefSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		circleBack.setMinSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		circleBack.setMaxSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
		circleBack.getStyleClass().add(CSS_CLASS_CIRCLE_BACK);
		circleFront = new Region();
		circleFront.setPrefSize(PROGRESS_RADIUS_INNER * 2, PROGRESS_RADIUS_INNER * 2);
		circleFront.setMinSize(PROGRESS_RADIUS_INNER * 2, PROGRESS_RADIUS_INNER * 2);
		circleFront.setMaxSize(PROGRESS_RADIUS_INNER * 2, PROGRESS_RADIUS_INNER * 2);
		circleFront.getStyleClass().add(CSS_CLASS_CIRCLE_FRONT);
		piePiecePath = new Path();
		piePiecePath.getStyleClass().add(CSS_CLASS_PROGRESS);
		StackPane.setAlignment(piePiecePath, Pos.TOP_RIGHT);
		StackPane.setAlignment(circleFront, Pos.TOP_RIGHT);
		StackPane.setAlignment(circleBack, Pos.TOP_RIGHT);
		StackPane.setMargin(circleFront, new Insets((PROGRESS_RADIUS - PROGRESS_RADIUS_INNER), (PROGRESS_RADIUS - PROGRESS_RADIUS_INNER), 0, 0));
		spAmountContents.setAlignment(Pos.CENTER);
		spAmountContents.setPrefSize(PROGRESS_RADIUS_INNER * 2, PROGRESS_RADIUS_INNER * 2);
		spAmountContents.setMaxSize(PROGRESS_RADIUS_INNER * 2, PROGRESS_RADIUS_INNER * 2);
		spAmountContents.setMinSize(PROGRESS_RADIUS_INNER * 2, PROGRESS_RADIUS_INNER * 2);
		spAmount.setPrefSize(WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM_RIGHT_TOP);
		spAmount.setAlignment(Pos.CENTER);
		spAmountContents.getChildren().add(circleBack);
		spAmountContents.getChildren().add(circleFront);
		spAmountContents.getChildren().add(piePiecePath);
		spAmount.getChildren().add(spAmountContents);
		lblTotalAmount = new Label();
		spAmount.getChildren().add(lblTotalAmount);
		lblTotalAmount.getStyleClass().add(CSS_CLASS_TOTAL_AMOUNT);
		lblFinishedAmount = new Label();
		lblFinishedAmount.getStyleClass().add(CSS_CLASS_FINISHED_AMOUNT);
		spAmount.getChildren().add(lblFinishedAmount);
		StackPane.setMargin(lblTotalAmount, new Insets(75, 0, 0, 50));
		StackPane.setMargin(lblFinishedAmount, new Insets(10, 0, 0, 0));
		
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
		spButton.setAlignment(Pos.CENTER);
		activateStartButton();
		
		vboxBottomRight = new VBox();
		vboxBottomRight.getChildren().add(spAmount);
		vboxBottomRight.getChildren().add(spButton);
		bottomRight.getChildren().add(vboxBottomRight);
	}
	
	public void activateStartButton() {
		spButton.getChildren().clear();
		spButton.getChildren().add(btnStart);
	}
	
	public void activeStopButton() {
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
			throw new IllegalArgumentException("Illegal percentage value");
		}
		
		piePiecePath.getElements().clear();
		
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
	}
	
}
