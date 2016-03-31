package eu.robojob.millassist.ui.automate;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.ui.general.status.StatusView;
import eu.robojob.millassist.util.SizeManager;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class AutomateStatusView extends HBox {


    private static final Logger logger = LogManager.getLogger(AutomateStatusView.class.getName());
    public static final int WIDTH = 800;
    public static final int WIDTH_BOTTOM_RIGHT = 230;
    public static final int HEIGHT_BOTTOM_RIGHT_TOP = 230;
    public static final int HEIGHT_BOTTOM_LEFT_TOP = 230;
    public static final int PROGRESS_RADIUS = 80;
    public static final int PROGRESS_RADIUS_INNER = 1;
    public static final int PROGRESS_RADIUS_INNER_CIRCLE = 74;
    private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
    private static final int TIMING_STATUS_WIDTH = 500;

    private static final int ICON_MARGIN = 8;

    private static final String CSS_CLASS_FORM_BUTTON_ICON = "form-button-icon";
    private static final String CSS_CLASS_BUTTON_START_LABEL = "btn-start-label";
    private static final String CSS_CLASS_BUTTON = "form-button";
    private static final String CSS_CLASS_BUTTON_STOP = "delete-btn";

    private static final String STOP_ICON = "M 11.46875 0 C 5.1620208 0 0 5.1349468 0 11.5 C 0 17.865052 5.1620208 23 11.46875 23 C 17.775477 23 22.9375 17.865052 22.9375 11.5 C 22.9375 5.1349468 17.775478 0 11.46875 0 z M 11.46875 1.59375 C 17.003076 1.59375 21.40625 6.0239967 21.40625 11.5 C 21.40625 16.976002 17.003076 21.40625 11.46875 21.40625 C 5.9344209 21.40625 1.5 16.976002 1.5 11.5 C 1.5 6.0239967 5.9344209 1.59375 11.46875 1.59375 z M 6.40625 6.4375 L 6.40625 16.5625 L 16.53125 16.5625 L 16.53125 6.4375 L 6.40625 6.4375 z ";
    private static final String START_ICON = "M 11.46875 0 C 5.1620208 0 0 5.1349468 0 11.5 C 0 17.865052 5.1620208 23 11.46875 23 C 17.775477 23 22.9375 17.865052 22.9375 11.5 C 22.9375 5.1349468 17.775478 0 11.46875 0 z M 11.46875 1.59375 C 17.003076 1.59375 21.40625 6.0239967 21.40625 11.5 C 21.40625 16.976002 17.003076 21.40625 11.46875 21.40625 C 5.9344209 21.40625 1.5 16.976002 1.5 11.5 C 1.5 6.0239967 5.9344209 1.59375 11.46875 1.59375 z M 6.875 5.34375 L 6.875 17.65625 L 19.125 11.5 L 6.875 5.34375 z";

    private int totalAmount;
    private int finishedAmount;
    private AutomateStatusPresenter presenter;

    private StackPane bottomRight;
    private StackPane bottomLeft;
    private VBox vboxBottomLeft;
    private VBox vboxBottomRight;
    private StatusView statusView;
    private TimingView timingView;
    private VBox vboxButtons;
    private Button btnCancel;
    private Button btnStart;
    private Button btnContinue;
    private Path piePiecePath;
    private Pane piePiecePane;
    private Label lblFinishedAmount;
    private Label lblTotalAmount;
    private Circle circleBack;
    private Circle circleFront;
    private Circle circleBackContinuous;
    private StackPane spAmountContents;

    private boolean continuousEnabled;

    private RotateTransition rtContinuous;

    private static final String CSS_CLASS_AUTOMATE_BOTTOM = "content-bottom";
    protected static final String CSS_CLASS_AUTOMATE_BUTTON_TEXT = "automate-btn-text";
    protected static final String CSS_CLASS_AUTOMATE_BUTTON = "form-button";
    protected static final String CSS_CLASS_ABORT_BUTTON = "abort-btn";
    private static final String CSS_CLASS_CIRCLE_BACK = "circle-back";
    private static final String CSS_CLASS_CIRCLE_BACK_CONTINUOUS = "circle-back-continuous";
    private static final String CSS_CLASS_CIRCLE_FRONT = "circle-front";
    private static final String CSS_CLASS_CIRCLE_FRONT_DARK = "circle-front-dark";
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
        getStyleClass().add(CSS_CLASS_AUTOMATE_BOTTOM);
        setAlignment(Pos.CENTER_LEFT);
        bottomRight = new StackPane();
        bottomLeft = new StackPane();
        getChildren().add(bottomLeft);
        getChildren().add(bottomRight);

        vboxBottomLeft = new VBox();
        vboxBottomLeft.setPrefSize(WIDTH - WIDTH_BOTTOM_RIGHT, SizeManager.HEIGHT_BOTTOM);
        vboxBottomLeft.getChildren().add(statusView);
        vboxBottomLeft.getChildren().add(timingView);
        vboxBottomLeft.setAlignment(Pos.TOP_CENTER);
        statusView.setWidth(TIMING_STATUS_WIDTH);
        statusView.setPrefHeight(HEIGHT_BOTTOM_LEFT_TOP);
        bottomLeft.getChildren().clear();
        bottomLeft.getChildren().add(vboxBottomLeft);
        bottomLeft.setAlignment(Pos.CENTER);
        timingView.setWidth(TIMING_STATUS_WIDTH);
        timingView.setPrefHeight(SizeManager.HEIGHT_BOTTOM - HEIGHT_BOTTOM_LEFT_TOP);
        timingView.setMinHeight(SizeManager.HEIGHT_BOTTOM - HEIGHT_BOTTOM_LEFT_TOP);

        StackPane spAmount = new StackPane();
        spAmountContents = new StackPane();
        circleBack = new Circle();
        circleBack.setCenterX(PROGRESS_RADIUS);
        circleBack.setCenterY(PROGRESS_RADIUS);
        circleBack.setRadius(PROGRESS_RADIUS);
        circleBack.getStyleClass().add(CSS_CLASS_CIRCLE_BACK);
        circleBackContinuous = new Circle();
        circleBackContinuous.setCenterX(PROGRESS_RADIUS);
        circleBackContinuous.setCenterY(PROGRESS_RADIUS);
        circleBackContinuous.setRadius(PROGRESS_RADIUS);
        circleBackContinuous.getStyleClass().add(CSS_CLASS_CIRCLE_BACK_CONTINUOUS);
        rtContinuous = new RotateTransition(Duration.millis(2000), circleBackContinuous);
        rtContinuous.setByAngle(360);
        rtContinuous.setInterpolator(Interpolator.LINEAR);
        rtContinuous.setCycleCount(Transition.INDEFINITE);
        circleFront = new Circle();
        circleFront.setCenterX(PROGRESS_RADIUS);
        circleFront.setCenterY(PROGRESS_RADIUS);
        circleFront.setRadius(PROGRESS_RADIUS_INNER_CIRCLE);
        circleFront.getStyleClass().add(CSS_CLASS_CIRCLE_FRONT);
        piePiecePath = new Path();
        piePiecePath.getStyleClass().add(CSS_CLASS_PROGRESS);
        piePiecePane = new Pane();
        piePiecePane.getChildren().add(circleBack);
        piePiecePane.getChildren().add(circleBackContinuous);
        piePiecePane.getChildren().add(piePiecePath);
        piePiecePane.getChildren().add(circleFront);
        piePiecePane.setPrefSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
        piePiecePane.setMinSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
        piePiecePane.setMaxSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
        spAmountContents.setPrefSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
        spAmountContents.setMaxSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
        spAmountContents.setMinSize(PROGRESS_RADIUS * 2, PROGRESS_RADIUS * 2);
        spAmountContents.setAlignment(Pos.CENTER);
        spAmount.setPrefSize(WIDTH_BOTTOM_RIGHT, HEIGHT_BOTTOM_RIGHT_TOP*0.80);
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

        vboxButtons = new VBox();
        vboxButtons.setPrefSize(WIDTH_BOTTOM_RIGHT, SizeManager.HEIGHT_BOTTOM - (HEIGHT_BOTTOM_RIGHT_TOP * 0.80));
        btnCancel = createButton(STOP_ICON, Translator.getTranslation(STOP), CSS_CLASS_BUTTON_STOP, BTN_WIDTH, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                presenter.stopRunning();
            }
        });
        btnStart = createButton(START_ICON, Translator.getTranslation(START), "", BTN_WIDTH, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                presenter.startAutomate();
            }
        });
        btnContinue = createButton(START_ICON, Translator.getTranslation(CONTINUE), "", BTN_WIDTH, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                presenter.continueAutomate();
            }
        });
        vboxButtons.setAlignment(Pos.CENTER);
        vboxButtons.getChildren().addAll(btnStart, btnCancel);
        activateStartButton();

        vboxBottomRight = new VBox();
        vboxBottomRight.getChildren().add(spAmount);
        VBox.setMargin(spAmount, new Insets(10, 0, 0, 0));
        vboxBottomRight.getChildren().add(vboxButtons);
        bottomRight.getChildren().add(vboxBottomRight);
        vboxButtons.setSpacing(10);

        setPercentage(0);
        enableContinuousAnimation(false);
    }

    public void activateStartButton() {
        vboxButtons.getChildren().clear();
        vboxButtons.getChildren().addAll(btnStart, btnCancel);
        btnStart.setDisable(false);
        btnContinue.setDisable(true);
        btnCancel.setDisable(true);
    }

    public void activateContinueButton() {
        vboxButtons.getChildren().clear();
        vboxButtons.getChildren().addAll(btnContinue, btnCancel);
        btnStart.setDisable(true);
        btnContinue.setDisable(false);
        btnCancel.setDisable(false);
    }

    public void activateStopButton() {
        vboxButtons.getChildren().clear();
        vboxButtons.getChildren().addAll(btnStart, btnCancel);
        btnStart.setDisable(true);
        btnContinue.setDisable(true);
        btnCancel.setDisable(false);
    }

    public void setTotalAmount(final int amount) {
        if (amount == -1) {
            totalAmount = -1;
            lblTotalAmount.setText("");
            setPercentage(0);
        } else {
            totalAmount = amount;
            lblTotalAmount.setText("/" + amount);
            if ((totalAmount >= 0) && (finishedAmount >= 0)) {
                setPercentage((int) Math.floor(((double) finishedAmount / (double) totalAmount) * 100));
            }
        }
        enableContinuousAnimation(continuousEnabled);
    }

    public synchronized void enableContinuousAnimation(final boolean enable) {
        this.continuousEnabled = enable;
        circleFront.getStyleClass().remove(CSS_CLASS_CIRCLE_FRONT_DARK);
        if (enable && (totalAmount == -1)) {
            piePiecePath.setVisible(false);
            circleBackContinuous.setVisible(true);
            circleFront.getStyleClass().add(CSS_CLASS_CIRCLE_FRONT_DARK);
            rtContinuous.play();
        } else {
            piePiecePath.setVisible(true);
            circleBackContinuous.setVisible(false);
            rtContinuous.pause();
        }
    }

    public void setFinishedAmount(final int amount) {
        finishedAmount = amount;
        lblFinishedAmount.setText("" + amount);
        if ((totalAmount >= 0) && (finishedAmount >= 0)) {
            setPercentage((int) Math.floor(((double) finishedAmount / (double) totalAmount) * 100));
        } else {
            setPercentage(0);
        }
    }

    private void setPercentage(final int percentage) {

        double percentaged = percentage;

        if (percentage == 100) {
            percentaged = 99.999;
        }

        if ((percentaged < 0) || (percentaged > 100)) {
            logger.debug("Tried to set the percentage, but failed: Illegal percentage value: [" + percentage + "]");
            return;
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

    private static Button createButton(final String iconPath, final String text, final String cssClass, final double width, final EventHandler<ActionEvent> action) {
        Button button = new Button();
        HBox hbox = new HBox();
        StackPane iconPane = new StackPane();
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_ICON);
        iconPane.getChildren().add(icon);
        iconPane.setPrefSize(20, 20);
        Label label = new Label(text);
        label.getStyleClass().add(CSS_CLASS_BUTTON_START_LABEL);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
        hbox.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(iconPane);
        hbox.getChildren().add(label);
        HBox.setMargin(iconPane, new Insets(0, 0, 0, ICON_MARGIN));
        HBox.setHgrow(label, Priority.ALWAYS);
        button.setGraphic(hbox);
        button.setOnAction(action);
        button.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
        button.getStyleClass().addAll(CSS_CLASS_BUTTON, cssClass);
        return button;
    }
}
