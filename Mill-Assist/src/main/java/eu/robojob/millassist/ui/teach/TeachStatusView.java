package eu.robojob.millassist.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
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
	private static final int ICON_MARGIN = 8;
	
	private static final String CSS_CLASS_FORM_BUTTON_ICON = "form-button-icon";
	private static final String CSS_CLASS_BUTTON_START_LABEL = "btn-start-label";
	private static final String CSS_CLASS_BUTTON = "form-button";
	private static final String CSS_CLASS_BUTTON_REMOVE = "delete-btn";
	
	private static final String STOP_ICON = "M 11.46875 0 C 5.1620208 0 0 5.1349468 0 11.5 C 0 17.865052 5.1620208 23 11.46875 23 C 17.775477 23 22.9375 17.865052 22.9375 11.5 C 22.9375 5.1349468 17.775478 0 11.46875 0 z M 11.46875 1.59375 C 17.003076 1.59375 21.40625 6.0239967 21.40625 11.5 C 21.40625 16.976002 17.003076 21.40625 11.46875 21.40625 C 5.9344209 21.40625 1.5 16.976002 1.5 11.5 C 1.5 6.0239967 5.9344209 1.59375 11.46875 1.59375 z M 6.40625 6.4375 L 6.40625 16.5625 L 16.53125 16.5625 L 16.53125 6.4375 L 6.40625 6.4375 z ";
	private static final String SAVE_ICON = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	
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
		
		btnCancel = createButton(STOP_ICON, Translator.getTranslation(STOP), CSS_CLASS_BUTTON_REMOVE, BTN_WIDTH, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.stopTeaching();
			}
		});
		
		btnSave = createButton(SAVE_ICON, Translator.getTranslation(SAVE), "", BTN_WIDTH, new EventHandler<ActionEvent>() {
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
	
	private static Button createButton(final String iconPath, final String text, final String cssClass, final double width, final EventHandler<ActionEvent> action) {
		Button button = new Button();
		HBox hbox = new HBox();
		StackPane iconPane = new StackPane();
		SVGPath icon = new SVGPath();
		icon.setContent(iconPath);
		icon.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_ICON);
		iconPane.getChildren().add(icon);
		iconPane.setPrefSize(20, 20);
		Label label = new Label(text);
		label.getStyleClass().add(CSS_CLASS_BUTTON_START_LABEL);
		label.setAlignment(Pos.CENTER);
		label.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		hbox.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.getChildren().add(iconPane);
		hbox.getChildren().add(label);
		HBox.setMargin(iconPane, new Insets(0, 0, 0, ICON_MARGIN));
		HBox.setHgrow(label, Priority.ALWAYS);
		button.setGraphic(hbox);
		button.setOnAction(action);
		button.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		button.getStyleClass().addAll(CSS_CLASS_BUTTON, cssClass);
		return button;
	}
}
