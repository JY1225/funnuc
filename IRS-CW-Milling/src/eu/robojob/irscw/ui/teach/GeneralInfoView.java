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
	
	private Translator translator;
			
	private TeachPresenter presenter;
	
	private static final double BUTTON_WIDTH = UIConstants.BUTTON_HEIGHT * 5;
	private static final double BUTTON_HEIGHT = 40;
	
	public GeneralInfoView() {
		translator = Translator.getInstance();
		build();
	}
	
	public void setPresenter(TeachPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		this.setFillWidth(true);
		this.setAlignment(Pos.CENTER);
		this.setPrefSize(520, 300);
		lblInfoMessageTeachStacker = new Label(translator.getTranslation("teach-info"));
		lblInfoMessageTeachStacker.getStyleClass().add("teach-msg");
		lblInfoMessageTeachStacker.setPrefSize(600, 50);
		lblInfoMessageTeachStacker.setWrapText(true);
		
		setMargin(lblInfoMessageTeachStacker, new Insets(0, 0, 10, 0));
		
		btnStart = new Button();
		HBox hboxStart = new HBox();
		lblStart = new Label(translator.getTranslation("start"));
		lblStart.getStyleClass().add("btn-start-label");
		hboxStart.getChildren().add(lblStart);
		lblStart.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		//hboxStart.getChildren().add(arrowRight);
		hboxStart.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStart.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblStart, Priority.ALWAYS);
		btnStart.setGraphic(hboxStart);
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				//presenter.startFlow();
				presenter.startOptimized();
			}
		});
		btnStart.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnStart.getStyleClass().add("btn-start");
		
		lblInfoMessageTeachAll= new Label(translator.getTranslation("teach-info-all"));
		lblInfoMessageTeachAll.getStyleClass().add("teach-msg");
		lblInfoMessageTeachAll.setPrefSize(600, 60);
		lblInfoMessageTeachAll.setWrapText(true);
		
		setMargin(lblInfoMessageTeachAll, new Insets(30, 0, 10, 0));
		
		btnStartTeachAll = new Button();
		HBox hboxStartTeachAll = new HBox();
		lblStartTeachAll = new Label(translator.getTranslation("start-teach-all"));
		lblStartTeachAll.getStyleClass().add("btn-start-label");
		hboxStartTeachAll.getChildren().add(lblStartTeachAll);
		lblStartTeachAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStartTeachAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hboxStartTeachAll.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblStartTeachAll, Priority.ALWAYS);
		btnStartTeachAll.setGraphic(hboxStartTeachAll);
		btnStartTeachAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.startFlowTeachAll();
			}
		});
		btnStartTeachAll.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnStartTeachAll.getStyleClass().add("btn-start");
		
		getChildren().add(lblInfoMessageTeachStacker);
		getChildren().add(btnStart);
		getChildren().add(lblInfoMessageTeachAll);
		getChildren().add(btnStartTeachAll);
	}
}
