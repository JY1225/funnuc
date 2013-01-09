package eu.robojob.irscw.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class GeneralInfoView extends VBox {

	private Label lblInfoMessageTeachStacker;
	private Label lblInfoMessageTeachAll;
	private Button btnStart;
	private Label lblStart;
	private Button btnStartTeachAll;
	private Label lblStartTeachAll;			
	private TeachPresenter presenter;
	
	private static final double BUTTON_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BUTTON_HEIGHT = 40;
	private static final int PREF_WIDTH = 650;
	private static final int PREF_HEIGHT = 300;
	private static final int INFO_MESSAGE_HEIGHT = 50;
	
	private static final String CSS_CLASS_BUTTON_START_LABEL = "btn-start-label";
	private static final String CSS_CLASS_BUTTON_START = "btn-start";
	
	private static final String TEACH_INFO = "GeneralInfoView.teachInfo";
	private static final String START = "GeneralInfoView.start";
	private static final String TEACH_INFO_ALL = "GeneralInfoView.teachInfoAll";
	private static final String START_TEACH_ALL = "GeneralInfoView.startTeachAll";
	
	public GeneralInfoView() {
		build();
	}
	
	public void setPresenter(final TeachPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		this.setFillWidth(true);
		this.setAlignment(Pos.CENTER);
		this.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
		lblInfoMessageTeachStacker = new Label(Translator.getTranslation(TEACH_INFO));
		lblInfoMessageTeachStacker.getStyleClass().add(TeachView.CSS_CLASS_TEACH_MESSAGE);
		lblInfoMessageTeachStacker.setPrefSize(PREF_WIDTH, INFO_MESSAGE_HEIGHT);
		lblInfoMessageTeachStacker.setWrapText(true);
		
		setMargin(lblInfoMessageTeachStacker, new Insets(0, 0, 10, 0));
		
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
		
		lblInfoMessageTeachAll = new Label(Translator.getTranslation(TEACH_INFO_ALL));
		lblInfoMessageTeachAll.getStyleClass().add(TeachView.CSS_CLASS_TEACH_MESSAGE);
		lblInfoMessageTeachAll.setPrefSize(PREF_WIDTH, INFO_MESSAGE_HEIGHT);
		lblInfoMessageTeachAll.setWrapText(true);
		
		setMargin(lblInfoMessageTeachAll, new Insets(30, 0, 10, 0));
		
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
		
		getChildren().add(lblInfoMessageTeachStacker);
		getChildren().add(btnStart);
		getChildren().add(lblInfoMessageTeachAll);
		getChildren().add(btnStartTeachAll);
	}
}
