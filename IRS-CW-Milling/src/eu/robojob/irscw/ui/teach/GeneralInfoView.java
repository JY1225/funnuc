package eu.robojob.irscw.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

//TODO disable optimal when processflow has different structure
public class GeneralInfoView extends GridPane {

	private Label lblInfoMessageOptimalTitle;
	private Label lblInfoMessageOptimal;
	private Label lblInfoMessageAllTitle;
	private Label lblInfoMessageAll;
	private Button btnStartOptimal;
	private Label lblStartOptimal;
	private Button btnStartAll;
	private Label lblStartAll;			
	private TeachPresenter presenter;
	
	private static final double BUTTON_WIDTH = UIConstants.BUTTON_HEIGHT * 4;
	private static final double BUTTON_HEIGHT = 40;
	private static final int PREF_WIDTH = 700;
	private static final int PREF_HEIGHT = 500;
	private static final int PREF_LBL_WIDTH = 420;
	private static final int PREF_LBL_HEIGHT = 75;
	private static final int PREF_LBL_TITLE_HEIGHT = 30;
	
	private static final String CSS_CLASS_BUTTON_START_LABEL = "btn-start-label";
	private static final String CSS_CLASS_BUTTON_START = "btn-start";
	private static final String CSS_CLASS_GENERAL_INFO_MESSAGE = "general-info-msg";
	
	private static final String TEACH_OPTIMAL_TITLE = "GeneralInfoView.teachOptimalTitle";
	private static final String TEACH_OPTIMAL = "GeneralInfoView.teachOptimal";
	private static final String START_OPTIMAL = "GeneralInfoView.startOptimal";
	private static final String TEACH_ALL_TITLE = "GeneralInfoView.teachAllTitle";
	private static final String TEACH_ALL = "GeneralInfoView.teachAll";
	private static final String START_ALL = "GeneralInfoView.startTeachAll";
	
	public GeneralInfoView() {
		build();
	}
	
	public void setPresenter(final TeachPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		this.setAlignment(Pos.CENTER);
		this.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
		this.setVgap(10);
		
		lblInfoMessageOptimalTitle = new Label(Translator.getTranslation(TEACH_OPTIMAL_TITLE));
		lblInfoMessageOptimalTitle.setPrefSize(PREF_LBL_WIDTH, PREF_LBL_TITLE_HEIGHT);
		lblInfoMessageOptimalTitle.getStyleClass().add(TeachView.CSS_CLASS_INFO_MESSAGE_TITLE);
		
		lblInfoMessageOptimal = new Label(Translator.getTranslation(TEACH_OPTIMAL));
		lblInfoMessageOptimal.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_GENERAL_INFO_MESSAGE);
		lblInfoMessageOptimal.setPrefSize(PREF_LBL_WIDTH, PREF_LBL_HEIGHT);
		lblInfoMessageOptimal.setWrapText(true);
				
		btnStartOptimal = new Button();
		HBox hboxStart = new HBox();
		lblStartOptimal = new Label(Translator.getTranslation(START_OPTIMAL));
		lblStartOptimal.getStyleClass().add(CSS_CLASS_BUTTON_START_LABEL);
		hboxStart.getChildren().add(lblStartOptimal);
		lblStartOptimal.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStart.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStart.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblStartOptimal, Priority.ALWAYS);
		btnStartOptimal.setGraphic(hboxStart);
		btnStartOptimal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.startTeachOptimal();
			}
		});
		btnStartOptimal.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnStartOptimal.getStyleClass().add(CSS_CLASS_BUTTON_START);
		
		lblInfoMessageAllTitle = new Label(Translator.getTranslation(TEACH_ALL_TITLE));
		lblInfoMessageAllTitle.setPrefSize(PREF_LBL_WIDTH, PREF_LBL_TITLE_HEIGHT);
		lblInfoMessageAllTitle.getStyleClass().add(TeachView.CSS_CLASS_INFO_MESSAGE_TITLE);
		
		lblInfoMessageAll = new Label(Translator.getTranslation(TEACH_ALL));
		lblInfoMessageAll.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_GENERAL_INFO_MESSAGE);
		lblInfoMessageAll.setPrefSize(PREF_LBL_WIDTH, PREF_LBL_HEIGHT);
		lblInfoMessageAll.setWrapText(true);
				
		btnStartAll = new Button();
		HBox hboxStartTeachAll = new HBox();
		lblStartAll = new Label(Translator.getTranslation(START_ALL));
		lblStartAll.getStyleClass().add(CSS_CLASS_BUTTON_START_LABEL);
		hboxStartTeachAll.getChildren().add(lblStartAll);
		lblStartAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStartTeachAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStartTeachAll.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblStartAll, Priority.ALWAYS);
		btnStartAll.setGraphic(hboxStartTeachAll);
		btnStartAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.startTeachAll();
			}
		});
		btnStartAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnStartAll.getStyleClass().add(CSS_CLASS_BUTTON_START);
		
		VBox vBoxOptimalLabels = new VBox();
		vBoxOptimalLabels.getChildren().add(lblInfoMessageOptimalTitle);
		vBoxOptimalLabels.getChildren().add(lblInfoMessageOptimal);
		
		VBox vBoxAllLabels = new VBox();
		vBoxAllLabels.getChildren().add(lblInfoMessageAllTitle);
		vBoxAllLabels.getChildren().add(lblInfoMessageAll);
		
		add(vBoxOptimalLabels, 0, 0);
		add(btnStartOptimal, 1, 0);
		add(vBoxAllLabels, 0, 1);
		add(btnStartAll, 1, 1);
	}
}
