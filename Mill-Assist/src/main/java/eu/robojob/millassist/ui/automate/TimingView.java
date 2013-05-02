package eu.robojob.millassist.ui.automate;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.util.Translator;

public class TimingView extends HBox {

	private VBox vBoxCycleTime;
	private Label lblCycleTimeTitle;
	private Label lblCycleTime;
	
	private VBox vBoxTimeInCycle;
	private Label lblTimeInCycleTitle;
	private Label lblTimeInCycle;
	
	private VBox vBoxTimeTillIntervention;
	private Label lblTimeTillInterventionTitle;
	private Label lblTimeTillIntervention;
	
	private VBox vBoxTimeTillFinished;
	private Label lblTimeTillFinishedTitle;
	private Label lblTimeTillFinished;
	
	private static final String CYCLE_TIME = "TimingView.cycleTime";
	private static final String TIME_IN_CYCLE = "TimingView.timeInCycle";
	private static final String TIME_TILL_INTERVENTION = "TimingView.timeTillIntervention";
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
		
		vBoxCycleTime = new VBox();
		vBoxCycleTime.setAlignment(Pos.CENTER_LEFT);
		lblCycleTimeTitle = new Label();
		lblCycleTimeTitle.setText(Translator.getTranslation(CYCLE_TIME));
		lblCycleTimeTitle.getStyleClass().add(CSS_TIMING_TITLE);
		lblCycleTime = new Label();
		lblCycleTime.setText(DEFAULT_VALUE);
		lblCycleTime.getStyleClass().add(CSS_TIMING_VALUE);
		vBoxCycleTime.getChildren().add(lblCycleTimeTitle);
		vBoxCycleTime.getChildren().add(lblCycleTime);
		
		vBoxTimeInCycle = new VBox();
		vBoxTimeInCycle.setAlignment(Pos.CENTER_LEFT);
		lblTimeInCycleTitle = new Label();
		lblTimeInCycleTitle.setText(Translator.getTranslation(TIME_IN_CYCLE));
		lblTimeInCycleTitle.getStyleClass().add(CSS_TIMING_TITLE);
		lblTimeInCycle = new Label();
		lblTimeInCycle.setText(DEFAULT_VALUE);
		lblTimeInCycle.getStyleClass().add(CSS_TIMING_VALUE);
		vBoxTimeInCycle.getChildren().add(lblTimeInCycleTitle);
		vBoxTimeInCycle.getChildren().add(lblTimeInCycle);

		vBoxTimeTillIntervention = new VBox();
		vBoxTimeTillIntervention.setAlignment(Pos.CENTER_LEFT);
		lblTimeTillInterventionTitle = new Label();
		lblTimeTillInterventionTitle.setText(Translator.getTranslation(TIME_TILL_INTERVENTION));
		lblTimeTillInterventionTitle.getStyleClass().add(CSS_TIMING_TITLE);
		lblTimeTillIntervention = new Label();
		lblTimeTillIntervention.setText(DEFAULT_VALUE);
		lblTimeTillIntervention.getStyleClass().add(CSS_TIMING_VALUE);
		vBoxTimeTillIntervention.getChildren().add(lblTimeTillInterventionTitle);
		vBoxTimeTillIntervention.getChildren().add(lblTimeTillIntervention);

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

		getChildren().add(vBoxCycleTime);
		getChildren().add(vBoxTimeInCycle);
		getChildren().add(vBoxTimeTillIntervention);
		getChildren().add(vBoxTimeTillFinished);
	}
	
	public void setCycleTime(final String timeString) {
		lblCycleTime.setText(timeString);
	}
	
	public void setTimeInCycle(final String timeString) {
		lblTimeInCycle.setText(timeString);
	}
	
	public void setTimeTillIntervention(final String timeString) {
		lblTimeTillIntervention.setText(timeString);
	}
	
	public void setTimeTillFinished(final String timeString) {
		lblTimeTillFinished.setText(timeString);
	}
	
	public void setWidth(final double width) {
		setMinWidth(width);
		setPrefWidth(width);
		setMaxWidth(width);
		double vboxWidth = width / 4;
		vBoxCycleTime.setPrefWidth(vboxWidth);
		vBoxCycleTime.setMinWidth(vboxWidth);
		vBoxCycleTime.setMaxWidth(vboxWidth);
		vBoxTimeInCycle.setPrefWidth(vboxWidth);
		vBoxTimeInCycle.setMinWidth(vboxWidth);
		vBoxTimeInCycle.setMaxWidth(vboxWidth);
		vBoxTimeTillIntervention.setPrefWidth(vboxWidth);
		vBoxTimeTillIntervention.setMinWidth(vboxWidth);
		vBoxTimeTillIntervention.setMaxWidth(vboxWidth);
		vBoxTimeTillFinished.setPrefWidth(vboxWidth);
		vBoxTimeTillFinished.setMinWidth(vboxWidth);
		vBoxTimeTillFinished.setMaxWidth(vboxWidth);
	}
}
