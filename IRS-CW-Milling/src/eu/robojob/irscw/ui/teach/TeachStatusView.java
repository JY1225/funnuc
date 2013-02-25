package eu.robojob.irscw.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.robojob.irscw.ui.MainContentView;
import eu.robojob.irscw.ui.general.status.StatusView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class TeachStatusView extends VBox {

	private TeachStatusPresenter presenter;
	private eu.robojob.irscw.ui.general.status.StatusView statusView;
	private Button btnCancel;
	
	private static final double WIDTH = 500;
	private static final double STATUS_HEIGHT = 200;
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
	private static final String CSS_CLASS_TEACH_BUTTON = "form-button";
	private static final String CSS_CLASS_TEACH_BUTTON_TEXT = "form-btn-text";
	
	private static final String STOP = "StatusView.stop";
	
	public TeachStatusView() {
	}
	
	public void setStatusView(final StatusView statusView) {
		this.statusView = statusView;
	}
	
	public void build() {
		
		setPrefHeight(MainContentView.HEIGHT_BOTTOM);
		setAlignment(Pos.CENTER);
		getChildren().clear();
		statusView.setWidth(WIDTH);
		statusView.setPrefHeight(STATUS_HEIGHT);
		
		btnCancel = new Button();
		btnCancel.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		Text txtRestart = new Text(Translator.getTranslation(STOP));
		txtRestart.getStyleClass().add(CSS_CLASS_TEACH_BUTTON_TEXT);
		btnCancel.setGraphic(txtRestart);
		btnCancel.getStyleClass().addAll(CSS_CLASS_TEACH_BUTTON);
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.stopTeaching();
			}
		});
		
		getChildren().add(statusView);
		getChildren().add(btnCancel);
	}
	
	public void setPresenter(final TeachStatusPresenter presenter) {
		this.presenter = presenter;
	}
	
}
