package eu.robojob.millassist.ui.automate;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.util.Translator;

public class TimingView extends HBox {

	private VBox vBoxTotalTime;
	private Label lblTotalTimeTitle;
	private Label lblTotalTime;
	
	private VBox vBoxFinishedInterval;
	private Label lblFinishedIntervalTitle;
	private Label lblFinishedInterval;
	
	private VBox vBoxRemainingCurrent;
	private Label lblRemainingCurrentTitle;
	private Label lblRemainingCurrent;
	
	private VBox vBoxTimeTillFinished;
	private Label lblTimeTillFinishedTitle;
	private Label lblTimeTillFinished;
	
	private static final String TOTAL_TIME = "TimingView.totalTime";
	private static final String FINISHED_INTERVAL = "TimingView.finishedInterval";
	private static final String REMAINING_CURRENT = "TimingView.remainingCurrent";
	private static final String TIME_TILL_FINISHED = "TimingView.timeTillFinished";
	private static final String DEFAULT_VALUE = "00:00:00";
	
	private static final String CSS_TIMING_TITLE = "timing-title";
	private static final String CSS_TIMING_VALUE = "timing-value";
	
	public TimingView() {
		build();
	}
	
	private void build() {
		getChildren().clear();
		setAlignment(Pos.CENTER);
		
		vBoxTotalTime = new VBox();
		vBoxTotalTime.setAlignment(Pos.CENTER_LEFT);
		lblTotalTimeTitle = new Label();
		lblTotalTimeTitle.setText(Translator.getTranslation(TOTAL_TIME));
		lblTotalTimeTitle.getStyleClass().add(CSS_TIMING_TITLE);
		lblTotalTime = new Label();
		lblTotalTime.setText(DEFAULT_VALUE);
		lblTotalTime.getStyleClass().add(CSS_TIMING_VALUE);
		vBoxTotalTime.getChildren().add(lblTotalTimeTitle);
		vBoxTotalTime.getChildren().add(lblTotalTime);
		
		vBoxFinishedInterval = new VBox();
		vBoxFinishedInterval.setAlignment(Pos.CENTER_LEFT);
		lblFinishedIntervalTitle = new Label();
		lblFinishedIntervalTitle.setText(Translator.getTranslation(FINISHED_INTERVAL));
		lblFinishedIntervalTitle.getStyleClass().add(CSS_TIMING_TITLE);
		lblFinishedInterval = new Label();
		lblFinishedInterval.setText(DEFAULT_VALUE);
		lblFinishedInterval.getStyleClass().add(CSS_TIMING_VALUE);
		vBoxFinishedInterval.getChildren().add(lblFinishedIntervalTitle);
		vBoxFinishedInterval.getChildren().add(lblFinishedInterval);

		vBoxRemainingCurrent = new VBox();
		vBoxRemainingCurrent.setAlignment(Pos.CENTER_LEFT);
		lblRemainingCurrentTitle = new Label();
		lblRemainingCurrentTitle.setText(Translator.getTranslation(REMAINING_CURRENT));
		lblRemainingCurrentTitle.getStyleClass().add(CSS_TIMING_TITLE);
		lblRemainingCurrent = new Label();
		lblRemainingCurrent.setText(DEFAULT_VALUE);
		lblRemainingCurrent.getStyleClass().add(CSS_TIMING_VALUE);
		vBoxRemainingCurrent.getChildren().add(lblRemainingCurrentTitle);
		vBoxRemainingCurrent.getChildren().add(lblRemainingCurrent);

		vBoxTimeTillFinished = new VBox();
		vBoxTimeTillFinished.setAlignment(Pos.CENTER_LEFT);
		lblTimeTillFinishedTitle = new Label();
		lblTimeTillFinishedTitle.setText(Translator.getTranslation(TIME_TILL_FINISHED));
		lblTimeTillFinishedTitle.getStyleClass().add(CSS_TIMING_TITLE);
		lblTimeTillFinished = new Label();
		lblTimeTillFinished.setText(DEFAULT_VALUE);
		lblTimeTillFinished.getStyleClass().add(CSS_TIMING_VALUE);
		vBoxTimeTillFinished.getChildren().add(lblTimeTillFinishedTitle);
		vBoxTimeTillFinished.getChildren().add(lblTimeTillFinished);

		getChildren().add(vBoxTotalTime);
		getChildren().add(vBoxFinishedInterval);
		getChildren().add(vBoxRemainingCurrent);
		getChildren().add(vBoxTimeTillFinished);
	}
	
	public void setTotalTime(final String timeString) {
		lblTotalTime.setText(timeString);
	}
	
	public void setFinishedInterval(final String timeString) {
		lblFinishedInterval.setText(timeString);
	}
	
	public void setRemainingCurrent(final String timeString) {
		lblRemainingCurrent.setText(timeString);
	}
	
	public void setTimeTillFinished(final String timeString) {
		lblTimeTillFinished.setText(timeString);
	}
	
	public void setWidth(final double width) {
		setMinWidth(width);
		setPrefWidth(width);
		setMaxWidth(width);
		double vboxWidth = width / 4;
		vBoxTotalTime.setPrefWidth(vboxWidth);
		vBoxTotalTime.setMinWidth(vboxWidth);
		vBoxTotalTime.setMaxWidth(vboxWidth);
		vBoxFinishedInterval.setPrefWidth(vboxWidth);
		vBoxFinishedInterval.setMinWidth(vboxWidth);
		vBoxFinishedInterval.setMaxWidth(vboxWidth);
		vBoxRemainingCurrent.setPrefWidth(vboxWidth);
		vBoxRemainingCurrent.setMinWidth(vboxWidth);
		vBoxRemainingCurrent.setMaxWidth(vboxWidth);
		vBoxTimeTillFinished.setPrefWidth(vboxWidth);
		vBoxTimeTillFinished.setMinWidth(vboxWidth);
		vBoxTimeTillFinished.setMaxWidth(vboxWidth);
	}
}
