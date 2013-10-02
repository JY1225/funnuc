package eu.robojob.millassist.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.general.MainContentView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class GeneralInfoView extends GridPane {
	
	private ProcessFlow processFlow;
	private Label lblInfoMessageOptimalTitle;
	private Label lblInfoMessageOptimal;
	private Label lblInfoMessageAllTitle;
	private Label lblInfoMessageAll;
	private Button btnStartOptimal;
	private Button btnStartAll;
	private Button btnSaveAll;
	private Button btnRemoveTeachedOffsets;
	private GeneralInfoPresenter presenter;
		
	private static final double BUTTON_WIDTH = UIConstants.BUTTON_HEIGHT * 3;
	private static final double BUTTON_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final int PREF_WIDTH = 700;
	private static final int PREF_HEIGHT = 400;
	private static final int PREF_LBL_WIDTH = 470;
	private static final int ICON_MARGIN = 8;
	
	private static final String CSS_CLASS_FORM_BUTTON_ICON = "form-button-icon";
	private static final String CSS_CLASS_BUTTON_START_LABEL = "btn-start-label";
	private static final String CSS_CLASS_BUTTON = "form-button";
	private static final String CSS_CLASS_BUTTON_REMOVE = "delete-btn";
	private static final String CSS_CLASS_GENERAL_INFO_MESSAGE = "general-info-msg";
	
	private static final String TEACH_OPTIMAL_TITLE = "GeneralInfoView.teachOptimalTitle";
	private static final String TEACH_OPTIMAL = "GeneralInfoView.teachOptimal";
	private static final String START_OPTIMAL = "GeneralInfoView.startOptimal";
	private static final String TEACH_ALL_TITLE = "GeneralInfoView.teachAllTitle";
	private static final String TEACH_ALL = "GeneralInfoView.teachAll";
	private static final String START_ALL = "GeneralInfoView.startTeachAll";
	private static final String REMOVE_OFFSETS = "GeneralInfoView.removeOffsets";
	private static final String SAVE = "GeneralInfoView.save";
	
	private static final String START_ICON = "M 11.46875 0 C 5.1620208 0 0 5.1349468 0 11.5 C 0 17.865052 5.1620208 23 11.46875 23 C 17.775477 23 22.9375 17.865052 22.9375 11.5 C 22.9375 5.1349468 17.775478 0 11.46875 0 z M 11.46875 1.59375 C 17.003076 1.59375 21.40625 6.0239967 21.40625 11.5 C 21.40625 16.976002 17.003076 21.40625 11.46875 21.40625 C 5.9344209 21.40625 1.5 16.976002 1.5 11.5 C 1.5 6.0239967 5.9344209 1.59375 11.46875 1.59375 z M 6.875 5.34375 L 6.875 17.65625 L 19.125 11.5 L 6.875 5.34375 z";
	private static final String SAVE_ICON = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final String DELETE_ICON = "M 3.6875 1.5 C 3.6321551 1.5180584 3.5743859 1.5512948 3.53125 1.59375 L 1.59375 3.53125 C 1.423759 3.7005604 1.4751389 4.0379792 1.6875 4.25 L 7.46875 10 L 1.6875 15.75 C 1.4751389 15.962191 1.423759 16.297908 1.59375 16.46875 L 3.53125 18.40625 C 3.7020918 18.577772 4.0386598 18.55526 4.25 18.34375 L 10 12.5625 L 15.75 18.34375 C 15.962021 18.55526 16.297908 18.577772 16.46875 18.40625 L 18.40625 16.46875 C 18.57556 16.297908 18.524691 15.962191 18.3125 15.75 L 12.53125 10 L 18.3125 4.25 C 18.524691 4.0379792 18.57556 3.7005604 18.40625 3.53125 L 16.46875 1.59375 C 16.297908 1.4244396 15.962021 1.4759897 15.75 1.6875 L 10 7.46875 L 4.25 1.6875 C 4.1439896 1.5809791 4.0004088 1.5136129 3.875 1.5 C 3.8124658 1.4931936 3.7428449 1.4819416 3.6875 1.5 z";
	
	public GeneralInfoView(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		build();
		refresh();
	}
	
	public void setPresenter(final GeneralInfoPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		this.setAlignment(Pos.CENTER);
		this.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
		this.setVgap(15);
		
		lblInfoMessageOptimalTitle = new Label(Translator.getTranslation(TEACH_OPTIMAL_TITLE));
		lblInfoMessageOptimalTitle.setPrefWidth(PREF_LBL_WIDTH);
		lblInfoMessageOptimalTitle.getStyleClass().add(MainContentView.CSS_CLASS_INFO_MESSAGE_TITLE);
		
		lblInfoMessageOptimal = new Label(Translator.getTranslation(TEACH_OPTIMAL));
		lblInfoMessageOptimal.getStyleClass().addAll(MainContentView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_GENERAL_INFO_MESSAGE);
		lblInfoMessageOptimal.setPrefWidth(PREF_LBL_WIDTH);
		lblInfoMessageOptimal.setWrapText(true);
				
		btnStartOptimal = createButton(START_ICON, Translator.getTranslation(START_OPTIMAL), "", BUTTON_WIDTH + 10, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.startTeachingOptimal();
			}
		});
		
		lblInfoMessageAllTitle = new Label(Translator.getTranslation(TEACH_ALL_TITLE));
		lblInfoMessageAllTitle.setPrefWidth(PREF_LBL_WIDTH);
		lblInfoMessageAllTitle.getStyleClass().add(MainContentView.CSS_CLASS_INFO_MESSAGE_TITLE);
		
		lblInfoMessageAll = new Label(Translator.getTranslation(TEACH_ALL));
		lblInfoMessageAll.getStyleClass().addAll(MainContentView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_GENERAL_INFO_MESSAGE);
		lblInfoMessageAll.setPrefWidth(PREF_LBL_WIDTH);
		lblInfoMessageAll.setWrapText(true);
				
		btnStartAll = createButton(START_ICON, Translator.getTranslation(START_ALL), "", BUTTON_WIDTH + 10, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.startTeachingAll();
			}
		});
		
		VBox vBoxOptimalLabels = new VBox();
		vBoxOptimalLabels.getChildren().add(lblInfoMessageOptimalTitle);
		vBoxOptimalLabels.getChildren().add(lblInfoMessageOptimal);
		
		VBox vBoxAllLabels = new VBox();
		vBoxAllLabels.getChildren().add(lblInfoMessageAllTitle);
		vBoxAllLabels.getChildren().add(lblInfoMessageAll);
		
		btnRemoveTeachedOffsets = createButton(DELETE_ICON, Translator.getTranslation(REMOVE_OFFSETS), CSS_CLASS_BUTTON_REMOVE, BUTTON_WIDTH, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.clearTeachedData();
			}
		});
		
		btnSaveAll = createButton(SAVE_ICON, Translator.getTranslation(SAVE), "", BUTTON_WIDTH, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.saveProcess();
			}
		});
		
		HBox hboxControls = new HBox();
		hboxControls.getChildren().addAll(btnRemoveTeachedOffsets, btnSaveAll);
		hboxControls.setAlignment(Pos.CENTER);
		hboxControls.setSpacing(10);
		
		setVgap(10);
		setHgap(10);
		int row = 0;
		int column = 0;
		add(vBoxOptimalLabels, column++, row);
		add(btnStartOptimal, column++, row);
		column = 0; row++;
		add(vBoxAllLabels, column++, row);
		add(btnStartAll, column++, row);
		column = 0; row++;
		add(hboxControls, column++, row, 2, 1);
		GridPane.setMargin(vBoxAllLabels, new Insets(10, 0, 0, 0));
		GridPane.setMargin(hboxControls, new Insets(15, 0, 0, 0));
	
	}
	
	public void refresh() {
		// get first Pick Step
		boolean disable = true;
		if (isOptimalPossible()) {
			disable = false;
		}
		lblInfoMessageOptimal.setDisable(disable);
		lblInfoMessageOptimalTitle.setDisable(disable);
		btnStartOptimal.setDisable(disable);

		if (processFlow.getId() <= 0) {
			btnSaveAll.setDisable(true);
		} else {
			btnSaveAll.setDisable(false);
		}
	}

	private boolean isOptimalPossible() {
		WorkPieceDimensions firstPickStepDimensions = null;
		WorkPieceDimensions lastPickStepDimensions = null;
		for (AbstractProcessStep step : processFlow.getProcessSteps()) {
			if ((step instanceof PickStep) && !(step instanceof PickAfterWaitStep)) {
				if (((PickStep) step).getRobotSettings().getWorkPiece() != null) {
					if (firstPickStepDimensions == null) {
						firstPickStepDimensions = ((PickStep) step).getRobotSettings().getWorkPiece().getDimensions();
					}
					lastPickStepDimensions = ((PickStep) step).getRobotSettings().getWorkPiece().getDimensions();
				}
			}
		}
		if ((firstPickStepDimensions.getWidth() != lastPickStepDimensions.getWidth()) || (firstPickStepDimensions.getLength() != lastPickStepDimensions.getLength())) {
			return false;
		} 
		return true;
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
		label.setPrefSize(width, BUTTON_HEIGHT);
		hbox.setPrefSize(width, BUTTON_HEIGHT);
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.getChildren().add(iconPane);
		hbox.getChildren().add(label);
		HBox.setMargin(iconPane, new Insets(0, 0, 0, ICON_MARGIN));
		HBox.setHgrow(label, Priority.ALWAYS);
		button.setGraphic(hbox);
		button.setOnAction(action);
		button.setPrefSize(width, BUTTON_HEIGHT);
		button.getStyleClass().addAll(CSS_CLASS_BUTTON, cssClass);
		return button;
	}
}
