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
import javafx.scene.shape.SVGPath;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class GeneralInfoView extends VBox {

	private Label lblInfoMessage;
	private Button btnStart;
	private Label lblStart;
	
	private Translator translator;
	
	private SVGPath arrowRight;
	
	private static final String arrowRightPath = "M 7.53125 -0.03125 L 7.53125 5.5625 L 0 5.5625 L 0 9.3125 L 7.53125 9.3125 L 7.53125 14.9375 L 15 7.4375 L 7.53125 -0.03125 z";
	
	private TeachPresenter presenter;
	
	private static final double BUTTON_WIDTH = UIConstants.BUTTON_HEIGHT * 4;
	
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
		
		lblInfoMessage = new Label(translator.getTranslation("teach-info"));
		lblInfoMessage.getStyleClass().add("info-msg");
		lblInfoMessage.setPrefSize(520, 130);
		lblInfoMessage.setWrapText(true);
		
		setMargin(lblInfoMessage, new Insets(50, 0, 0, 0));
		
		btnStart = new Button();
		arrowRight = new SVGPath();
		arrowRight.setContent(arrowRightPath);
		arrowRight.getStyleClass().add("btn-start-icon");
		HBox hboxStart = new HBox();
		lblStart = new Label(translator.getTranslation("start"));
		lblStart.getStyleClass().add("btn-start-label");
		hboxStart.getChildren().add(lblStart);
		lblStart.setPrefSize(BUTTON_WIDTH - 40, UIConstants.BUTTON_HEIGHT);
		//hboxStart.getChildren().add(arrowRight);
		hboxStart.setPrefSize(BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT);
		hboxStart.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblStart, Priority.ALWAYS);
		btnStart.setGraphic(hboxStart);
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.startFlow();
			}
		});
		btnStart.setPrefSize(75, 75);
		btnStart.getStyleClass().add("btn-start");
		
		getChildren().add(lblInfoMessage);
		getChildren().add(btnStart);
	}
}
