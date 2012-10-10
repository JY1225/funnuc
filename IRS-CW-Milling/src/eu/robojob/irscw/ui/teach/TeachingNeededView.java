package eu.robojob.irscw.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class TeachingNeededView extends VBox {

	private Label lblTeachingMessage;
	
	private SVGPath arrowRight;
	private Label lblBtnTeachingFinished;
	private Button btnTeachingFinished;
	
	private Translator translator;
	
	private static final String arrowRightPath = "M 7.53125 -0.03125 L 7.53125 5.5625 L 0 5.5625 L 0 9.3125 L 7.53125 9.3125 L 7.53125 14.9375 L 15 7.4375 L 7.53125 -0.03125 z";
	private static final double BUTTON_WIDTH = UIConstants.BUTTON_HEIGHT * 4;

	private TeachPresenter presenter;
	
	public TeachingNeededView() {
		translator = Translator.getInstance();
		build();
	}
	
	private void build() {
		lblTeachingMessage = new Label(translator.getTranslation("teach-message"));
		
		arrowRight = new SVGPath();
		arrowRight.setContent(arrowRightPath);
		arrowRight.getStyleClass().add("btn-start-icon");
		btnTeachingFinished = new Button();
		HBox hboxStart = new HBox();
		lblBtnTeachingFinished = new Label(translator.getTranslation("start"));
		lblBtnTeachingFinished.getStyleClass().add("btn-start-label");
		hboxStart.getChildren().add(lblBtnTeachingFinished);
		lblBtnTeachingFinished.setPrefSize(BUTTON_WIDTH - 40, UIConstants.BUTTON_HEIGHT);
		hboxStart.getChildren().add(arrowRight);
		hboxStart.setPrefSize(BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT);
		hboxStart.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblBtnTeachingFinished, Priority.ALWAYS);
		btnTeachingFinished.setGraphic(hboxStart);
		btnTeachingFinished.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.continueFlow();
			}
		});
		btnTeachingFinished.setPrefSize(BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT);
		btnTeachingFinished.getStyleClass().add("btn-start");
	}
	
	public void setPresenter(TeachPresenter presenter) {
		this.presenter = presenter;
	}
}
