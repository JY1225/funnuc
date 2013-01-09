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

public class GeneralInfoView extends GridPane {

	private Label lblInfoMessageOptimalTitle;
	private Label lblInfoMessageTeachStacker;
	private Label lblInfoMessageAllTitle;
	private Label lblInfoMessageTeachAll;
	private Button btnStart;
	private Label lblStart;
	private Button btnStartTeachAll;
	private Label lblStartTeachAll;			
	private TeachPresenter presenter;
	
	private static final double BUTTON_WIDTH = UIConstants.BUTTON_HEIGHT * 4;
	private static final double BUTTON_HEIGHT = 40;
	private static final int PREF_WIDTH = 700;
	private static final int PREF_HEIGHT = 500;
	private static final int PREF_LBL_WIDTH = 400;
	private static final int PREF_LBL_HEIGHT = 75;
	private static final int PREF_LBL_TITLE_HEIGHT = 30;
	
	private static final String CSS_CLASS_BUTTON_START_LABEL = "btn-start-label";
	private static final String CSS_CLASS_BUTTON_START = "btn-start";
	private static final String CSS_CLASS_GENERAL_INFO_MESSAGE = "general-info-msg";
	private static final String CSS_CLASS_GENERAL_INFO_MESSAGE_TITLE = "general-info-msg-title";
	
	private static final String TEACH_OPTIMAL_TITLE = "GeneralInfoView.teachOptimalTitle";
	private static final String TEACH_INFO = "GeneralInfoView.teachInfo";
	private static final String START = "GeneralInfoView.start";
	private static final String TEACH_ALL_TITLE = "GeneralInfoView.teachAllTitle";
	private static final String TEACH_INFO_ALL = "GeneralInfoView.teachInfoAll";
	private static final String START_TEACH_ALL = "GeneralInfoView.startTeachAll";
	
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
		lblInfoMessageOptimalTitle.getStyleClass().add(CSS_CLASS_GENERAL_INFO_MESSAGE_TITLE);
		
		lblInfoMessageTeachStacker = new Label(Translator.getTranslation(TEACH_INFO));
		lblInfoMessageTeachStacker.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_GENERAL_INFO_MESSAGE);
		lblInfoMessageTeachStacker.setPrefSize(PREF_LBL_WIDTH, PREF_LBL_HEIGHT);
		lblInfoMessageTeachStacker.setWrapText(true);
				
		btnStart = new Button();
		HBox hboxStart = new HBox();
		lblStart = new Label(Translator.getTranslation(START));
		lblStart.getStyleClass().add(CSS_CLASS_BUTTON_START_LABEL);
		hboxStart.getChildren().add(lblStart);
		lblStart.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStart.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStart.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblStart, Priority.ALWAYS);
		btnStart.setGraphic(hboxStart);
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.startOptimized();
			}
		});
		btnStart.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnStart.getStyleClass().add(CSS_CLASS_BUTTON_START);
		
		lblInfoMessageAllTitle = new Label(Translator.getTranslation(TEACH_ALL_TITLE));
		lblInfoMessageAllTitle.setPrefSize(PREF_LBL_WIDTH, PREF_LBL_TITLE_HEIGHT);
		lblInfoMessageAllTitle.getStyleClass().add(CSS_CLASS_GENERAL_INFO_MESSAGE_TITLE);
		
		lblInfoMessageTeachAll = new Label(Translator.getTranslation(TEACH_INFO_ALL));
		lblInfoMessageTeachAll.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_GENERAL_INFO_MESSAGE);
		lblInfoMessageTeachAll.setPrefSize(PREF_LBL_WIDTH, PREF_LBL_HEIGHT);
		lblInfoMessageTeachAll.setWrapText(true);
				
		btnStartTeachAll = new Button();
		HBox hboxStartTeachAll = new HBox();
		lblStartTeachAll = new Label(Translator.getTranslation(START_TEACH_ALL));
		lblStartTeachAll.getStyleClass().add(CSS_CLASS_BUTTON_START_LABEL);
		hboxStartTeachAll.getChildren().add(lblStartTeachAll);
		lblStartTeachAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStartTeachAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStartTeachAll.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblStartTeachAll, Priority.ALWAYS);
		btnStartTeachAll.setGraphic(hboxStartTeachAll);
		btnStartTeachAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.startFlowTeachAll();
			}
		});
		btnStartTeachAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnStartTeachAll.getStyleClass().add(CSS_CLASS_BUTTON_START);
		
		VBox vBoxOptimalLabels = new VBox();
		vBoxOptimalLabels.getChildren().add(lblInfoMessageOptimalTitle);
		vBoxOptimalLabels.getChildren().add(lblInfoMessageTeachStacker);
		
		VBox vBoxAllLabels = new VBox();
		vBoxAllLabels.getChildren().add(lblInfoMessageAllTitle);
		vBoxAllLabels.getChildren().add(lblInfoMessageTeachAll);
		
		add(vBoxOptimalLabels, 0, 0);
		add(btnStart, 1, 0);
		add(vBoxAllLabels, 0, 1);
		add(btnStartTeachAll, 1, 1);
	}
}
