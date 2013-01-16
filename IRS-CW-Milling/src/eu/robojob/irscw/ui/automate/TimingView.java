package eu.robojob.irscw.ui.automate;

import eu.robojob.irscw.util.Translator;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
	
	public TimingView() {
		build();
	}
	
	private void build() {
		vBoxCycleTime = new VBox();
		lblCycleTimeTitle = new Label();
		lblCycleTimeTitle.setText(Translator.getTranslation(CYCLE_TIME));
		lblCycleTime = new Label();
		vBoxCycleTime.getChildren().add(lblCycleTimeTitle);
		vBoxCycleTime.getChildren().add(lblCycleTime);
		
		vBoxTimeInCycle = new VBox();
		lblTimeInCycleTitle = new Label();
		lblTimeInCycleTitle.setText(Translator.getTranslation(TIME_IN_CYCLE));
		lblTimeInCycle = new Label();
		vBoxTimeInCycle.getChildren().add(lblTimeInCycleTitle);
		vBoxTimeInCycle.getChildren().add(lblTimeInCycle);

		vBoxTimeTillIntervention = new VBox();
		lblTimeTillInterventionTitle = new Label();
		lblTimeTillInterventionTitle.setText(Translator.getTranslation(TIME_TILL_INTERVENTION));
		lblTimeTillIntervention = new Label();
		vBoxTimeTillIntervention.getChildren().add(lblTimeTillInterventionTitle);
		vBoxTimeTillIntervention.getChildren().add(lblTimeTillIntervention);
		
		vBoxTimeTillFinished = new VBox();
		lblTimeTillFinishedTitle = new Label();
		lblTimeTillFinishedTitle.setText(Translator.getTranslation(TIME_TILL_FINISHED));
		lblTimeTillFinished = new Label();
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
}
