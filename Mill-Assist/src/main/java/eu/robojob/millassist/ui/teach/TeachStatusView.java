package eu.robojob.millassist.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.robojob.millassist.ui.general.MainContentView;
import eu.robojob.millassist.ui.general.status.StatusView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class TeachStatusView extends VBox {

	private TeachStatusPresenter presenter;
	private eu.robojob.millassist.ui.general.status.StatusView statusView;
	private Button btnCancel;
	private Button btnSave;
	
	private static final double WIDTH = 500;
	private static final double STATUS_HEIGHT = 200;
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
	private static final String CSS_CLASS_TEACH_BUTTON = "form-button";
	private static final String CSS_CLASS_ABORT_BUTTON = "abort-btn";
	private static final String CSS_CLASS_TEACH_BUTTON_TEXT = "form-btn-text";
	
	private static final String STOP = "StatusView.stop";
	private static final String SAVE = "StatusView.save";
	
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
		btnCancel.getStyleClass().addAll(CSS_CLASS_TEACH_BUTTON, CSS_CLASS_ABORT_BUTTON);
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.stopTeaching();
			}
		});
		
		btnSave = new Button();
		btnSave.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		Text txtSave = new Text(Translator.getTranslation(SAVE));
		txtSave.getStyleClass().add(CSS_CLASS_TEACH_BUTTON_TEXT);
		btnSave.setGraphic(txtSave);
		btnSave.getStyleClass().addAll(CSS_CLASS_TEACH_BUTTON);
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.saveProcess();
			}
		});
		
		getChildren().add(statusView);
		getChildren().add(btnCancel);
		getChildren().add(btnSave);
		showSaveButton(false);
	}
	
	public void setPresenter(final TeachStatusPresenter presenter) {
		this.presenter = presenter;
	}
	
	public void showSaveButton(final boolean show) {
		btnCancel.setVisible(!show);
		btnCancel.setManaged(!show);
		btnSave.setVisible(show);
		btnSave.setManaged(show);
	}
}
