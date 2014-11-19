package eu.robojob.millassist.ui.admin.device;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout.HoleOrientation;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class GridPlateConfigureView extends AbstractFormView<GridPlateConfigurePresenter> {
	
	private HBox orientationsBox, hboxSelectGridPlate;
	
	private Button btnHorizontal, btnTilted;
	private Button btnEdit, btnNew;
	private Button btnSave, btnDelete;
	
	private ComboBox<String> cbbGridPlates;
	
	private Label lblName, lblOrientation, lblFirstHole, lblX, lblY, lblOffset, lblOffsetX, lblOffsetY, lblNbRows, lblNbCols, lblCount, lblHeight, lblHoleLength, lblHoleWidth, lblLength, lblWidth, lblDimHoles, lblDimPlate, lblPos, lblPosX, lblPosY;
	private NumericTextField numtxtX, numtxtY, numtxtOffsetX, numtxtOffsetY, numtxtHeight, numtxtHoleLength, numtxtHoleWidth, numtxtLength, numtxtWidth, numtxtPosX, numtxtPosY;
	private IntegerTextField itxtNbRows, itxtNbCols;
	private FullTextField fullTxtName;
	private GridPane gpGridPlateConfig;
	
	private Label lblSmoothTo, lblSmoothToX, lblSmoothToY, lblSmoothToZ;
	private NumericTextField numtxtSmoothToX, numtxtSmoothToY, numtxtSmoothToZ;
	
	private Label lblSmoothFrom, lblSmoothFromX, lblSmoothFromY, lblSmoothFromZ;
	private NumericTextField numtxtSmoothFromX, numtxtSmoothFromY, numtxtSmoothFromZ;
	
	private static final String HORIZONTAL_ICON = "M 3.3125 3.28125 L 3.3125 14.4375 L 20.0625 14.4375 L 20.0625 3.28125 L 3.3125 3.28125 z ";
	private static final String TILTED_ICON = "M 11.90625 0.03125 L 0.0625 11.875 L 7.96875 19.78125 L 19.8125 7.9375 L 11.90625 0.03125 z ";
	private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";
	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";
	
	private static final String EDIT = "GridPlateConfigureView.edit";
	private static final String NEW = "GridPlateConfigureView.new";
	private static final String HORIZONTAL = "GridPlateConfigureView.horizontal";
	private static final String TILTED = "GridPlateConfigureView.tilted";
	private static final String NAME = "GridPlateConfigureView.name";
	private static final String SAVE = "GridPlateConfigureView.save";
	private static final String REMOVE = "GridPlateConfigureView.remove";
	private static final String ORIENTATION = "GridPlateConfigureView.orientation";
	private static final String FIRST_HOLE = "GridPlateConfigureView.firsthole";
	private static final String OFFSET = "GridPlateConfigureView.offset";
	private static final String COUNT = "GridPlateConfigureView.count";
	private static final String DIM_HOLES = "GridPlateConfigureView.dimhole";
	private static final String DIM_PLATE = "GridPlateConfigureView.dimplate";
	private static final String POSITION = "GridPlateConfigureView.pos";
	private static final String X = "GridPlateConfigureView.x";
	private static final String Y = "GridPlateConfigureView.y";
	private static final String SMOOTH_TO = "GridPlateConfigureView.smoothTo";
	private static final String SMOOTH_FROM = "GridPlateConfigureView.smoothFrom";
	
	
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	private static final double LBL_WIDTH = 30;
	
	private static final String CSS_CLASS_BUTTON_ORIENTATION = "btn-orientation";
	private static final String CSS_CLASS_LBL_RIGHT = "lbl-right";
	
	public GridPlateConfigureView() {
		build();
	}
	
	@Override
	protected void build() {
		getContents().setVgap(20);
		getContents().setHgap(5);
		getContents().setAlignment(Pos.TOP_CENTER);
		getContents().setPadding(new Insets(20, 0, 0, 0));
		
		getContents().getChildren().clear();
		
		hboxSelectGridPlate = new HBox();
		hboxSelectGridPlate.setSpacing(25);
		hboxSelectGridPlate.setAlignment(Pos.CENTER_LEFT);
		cbbGridPlates = new ComboBox<String>();
		cbbGridPlates.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbGridPlates.setMinSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbGridPlates.setMaxSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbGridPlates.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {
				if (newValue != null) {
					btnEdit.setDisable(false);
				} else {
					btnEdit.setDisable(true);
				}
			}
		});
		
		HBox hboxButtons = new HBox();
		btnEdit = createButton(EDIT_PATH, null, Translator.getTranslation(EDIT), BTN_WIDTH, BTN_HEIGHT, null);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnEdit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				cbbGridPlates.setDisable(true);
				getPresenter().clickedEdit(cbbGridPlates.valueProperty().get());
			}
		});
		btnEdit.setDisable(true);
		btnNew = createButton(ADD_PATH, null, Translator.getTranslation(NEW), BTN_WIDTH, BTN_HEIGHT, null);
		btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		btnNew.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().clickedNew();
			}
		});
		hboxButtons.getChildren().addAll(btnEdit, btnNew);
		hboxSelectGridPlate.getChildren().addAll(cbbGridPlates, hboxButtons);
		
		lblName = new Label(Translator.getTranslation(NAME));
		fullTxtName = new FullTextField(100);
		fullTxtName.setPrefWidth(250);
		fullTxtName.setMaxWidth(2 * BTN_WIDTH);
		fullTxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
				validate();
			}
		});
		
		lblOrientation = new Label(Translator.getTranslation(ORIENTATION));		
		orientationsBox = new HBox();
		btnHorizontal = createButton(HORIZONTAL_ICON, CSS_CLASS_BUTTON_ORIENTATION, Translator.getTranslation(HORIZONTAL), 80*0.9, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedOrientation(HoleOrientation.HORIZONTAL);
			}
		});
		btnHorizontal.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnTilted = createButton(TILTED_ICON, CSS_CLASS_BUTTON_ORIENTATION, Translator.getTranslation(TILTED), 80*0.9, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedOrientation(HoleOrientation.TILTED);
			}
		});
		btnTilted.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		orientationsBox.setAlignment(Pos.CENTER_LEFT);
		orientationsBox.getChildren().addAll(btnHorizontal, btnTilted);
		orientationsBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxPos = new HBox();
		hboxPos.setSpacing(15);
		lblPos = new Label(Translator.getTranslation(POSITION));
		lblPosX = new Label(Translator.getTranslation(X));
		lblPosX.setPrefWidth(LBL_WIDTH);
		lblPosX.setTextAlignment(TextAlignment.RIGHT);
		lblPosX.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtPosX = new NumericTextField(5);
		numtxtPosX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblPosY = new Label(Translator.getTranslation(Y));
		lblPosY.setPrefWidth(LBL_WIDTH);
		lblPosY.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtPosY = new NumericTextField(5);
		numtxtPosY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxPos.getChildren().addAll(lblPosX, numtxtPosX, lblPosY, numtxtPosY);
		hboxPos.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxDimPlate = new HBox();
		lblDimPlate = new Label(Translator.getTranslation(DIM_PLATE));
		lblLength = new Label("X");
		lblLength.setPrefWidth(LBL_WIDTH);
		lblLength.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtLength = new NumericTextField(5);
		numtxtLength.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblWidth = new Label("Y");
		lblWidth.setPrefWidth(LBL_WIDTH);
		lblWidth.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtWidth = new NumericTextField(5);
		numtxtWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblHeight = new Label("Z");
		lblHeight.setPrefWidth(LBL_WIDTH);
		lblHeight.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtHeight = new NumericTextField(5);
		numtxtHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxDimPlate.setSpacing(15);
		hboxDimPlate.getChildren().addAll(lblLength, numtxtLength, lblWidth, numtxtWidth, lblHeight, numtxtHeight);
		hboxDimPlate.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxDimHoles = new HBox();
		lblDimHoles = new Label(Translator.getTranslation(DIM_HOLES));
		lblHoleLength = new Label("X");
		lblHoleLength.setPrefWidth(LBL_WIDTH);
		lblHoleLength.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtHoleLength = new NumericTextField(5);
		numtxtHoleLength.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblHoleWidth = new Label("Y");
		lblHoleWidth.setPrefWidth(LBL_WIDTH);
		lblHoleWidth.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtHoleWidth = new NumericTextField(5);
		numtxtHoleWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxDimHoles.setSpacing(15);
		hboxDimHoles.getChildren().addAll(lblHoleLength, numtxtHoleLength, lblHoleWidth, numtxtHoleWidth);
		hboxDimHoles.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxFirst = new HBox();
		lblFirstHole = new Label(Translator.getTranslation(FIRST_HOLE));
		lblX = new Label("X");
		lblX.setPrefWidth(LBL_WIDTH);
		lblX.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtX = new NumericTextField(5);
		numtxtX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblY = new Label("Y");
		lblY.setPrefWidth(LBL_WIDTH);
		lblY.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtY = new NumericTextField(5);
		numtxtY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxFirst.setSpacing(15);
		hboxFirst.getChildren().addAll(lblX, numtxtX, lblY, numtxtY);
		hboxFirst.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxOffset = new HBox();
		lblOffset = new Label(Translator.getTranslation(OFFSET));
		lblOffsetX = new Label("\u0394" + "X");
		lblOffsetX.setPrefWidth(LBL_WIDTH);
		lblOffsetX.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtOffsetX = new NumericTextField(5);
		numtxtOffsetX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblOffsetY = new Label("\u0394" + "Y");
		lblOffsetY.setPrefWidth(LBL_WIDTH);
		lblOffsetY.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtOffsetY = new NumericTextField(5);
		numtxtOffsetY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxOffset.setSpacing(15);
		hboxOffset.getChildren().addAll(lblOffsetX, numtxtOffsetX, lblOffsetY, numtxtOffsetY);
		hboxOffset.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxCount = new HBox();
		lblCount = new Label(Translator.getTranslation(COUNT));
		lblCount.setPrefWidth(115);
		lblNbRows = new Label("#Y");
		lblNbRows.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		lblNbRows.setPrefWidth(LBL_WIDTH);
		itxtNbRows = new IntegerTextField(5);
		itxtNbRows.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(final ObservableValue<? extends Integer> arg0, final Integer arg1, final Integer arg2) {
				validate();
			}
		});
		lblNbCols = new Label("#X");
		lblNbCols.setPrefWidth(LBL_WIDTH);
		lblNbCols.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		itxtNbCols = new IntegerTextField(5);
		itxtNbCols.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(final ObservableValue<? extends Integer> arg0, final Integer arg1, final Integer arg2) {
				validate();
			}
		});
		hboxCount.setSpacing(15);
		hboxCount.getChildren().addAll(lblNbCols, itxtNbCols, lblNbRows, itxtNbRows);
		hboxCount.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxSmoothTo = new HBox();
		lblSmoothTo = new Label(Translator.getTranslation(SMOOTH_TO));
		lblSmoothToX = new Label("X");
		lblSmoothToX.setPrefWidth(LBL_WIDTH);
		lblSmoothToX.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtSmoothToX = new NumericTextField(5);
		numtxtSmoothToX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothToY = new Label("Y");
		lblSmoothToY.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		lblSmoothToY.setPrefWidth(LBL_WIDTH);
		numtxtSmoothToY = new NumericTextField(5);
		numtxtSmoothToY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothToZ = new Label("Z");
		lblSmoothToZ.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		lblSmoothToZ.setPrefWidth(LBL_WIDTH);
		numtxtSmoothToZ = new NumericTextField(5);
		numtxtSmoothToZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxSmoothTo.setSpacing(15);
		hboxSmoothTo.getChildren().addAll(lblSmoothToX, numtxtSmoothToX, lblSmoothToY, numtxtSmoothToY, lblSmoothToZ, numtxtSmoothToZ);
		hboxSmoothTo.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxSmoothFrom = new HBox();
		lblSmoothFrom = new Label(Translator.getTranslation(SMOOTH_FROM));
		lblSmoothFromX = new Label("X");
		lblSmoothFromX.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		lblSmoothFromX.setPrefWidth(LBL_WIDTH);
		numtxtSmoothFromX = new NumericTextField(5);
		numtxtSmoothFromX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothFromY = new Label("Y");
		lblSmoothFromY.setPrefWidth(LBL_WIDTH);
		lblSmoothFromY.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtSmoothFromY = new NumericTextField(5);
		numtxtSmoothFromY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothFromZ = new Label("Z");
		lblSmoothFromZ.setPrefWidth(LBL_WIDTH);
		lblSmoothFromZ.getStyleClass().add(CSS_CLASS_LBL_RIGHT);
		numtxtSmoothFromZ = new NumericTextField(5);
		numtxtSmoothFromZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxSmoothFrom.setSpacing(15);
		hboxSmoothFrom.getChildren().addAll(lblSmoothFromX, numtxtSmoothFromX, lblSmoothFromY, numtxtSmoothFromY, lblSmoothFromZ, numtxtSmoothFromZ);
		hboxSmoothFrom.setAlignment(Pos.CENTER_LEFT);
		
		btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().saveData(fullTxtName.getText(), 
						Float.parseFloat(numtxtX.getText()), 
						Float.parseFloat(numtxtY.getText()), 
						Float.parseFloat(numtxtOffsetX.getText()), 
						Float.parseFloat(numtxtOffsetY.getText()), 
						Integer.parseInt(itxtNbRows.getText()), 
						Integer.parseInt(itxtNbCols.getText()), 
						Float.parseFloat(numtxtHeight.getText()), 
						Float.parseFloat(numtxtHoleLength.getText()), 
						Float.parseFloat(numtxtHoleWidth.getText()), 
						Float.parseFloat(numtxtLength.getText()), 
						Float.parseFloat(numtxtWidth.getText()), 
						Float.parseFloat(numtxtPosX.getText()), 
						Float.parseFloat(numtxtPosY.getText()),
						Float.parseFloat(numtxtSmoothToX.getText()), 
						Float.parseFloat(numtxtSmoothToY.getText()), 
						Float.parseFloat(numtxtSmoothToZ.getText()), 
						Float.parseFloat(numtxtSmoothFromX.getText()), 
						Float.parseFloat(numtxtSmoothFromY.getText()), 
						Float.parseFloat(numtxtSmoothFromZ.getText()));
				getPresenter().disableEditMode();
				refresh();
				
				btnEdit.setDisable(true);
			}
		});
		btnDelete = createButton(DELETE_ICON_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(REMOVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().deleteGridPlate();
				refresh();
			}
		});
		btnDelete.getStyleClass().add("delete-btn");
	
		gpGridPlateConfig = new GridPane();
		gpGridPlateConfig.setPadding(new Insets(10,0,0,0));
		gpGridPlateConfig.setVgap(7);
		gpGridPlateConfig.setHgap(5);
		int row = 0;
		gpGridPlateConfig.add(lblName,0,row);
		gpGridPlateConfig.add(fullTxtName,2,row,5,1);
		gpGridPlateConfig.add(lblOrientation, 0,++row);
		gpGridPlateConfig.add(orientationsBox, 2, row,5,1);	
		gpGridPlateConfig.add(lblDimPlate,0, ++row);
		gpGridPlateConfig.add(hboxDimPlate, 2, row, 5, 1);
		gpGridPlateConfig.add(lblPos, 0, ++row);
		gpGridPlateConfig.add(hboxPos, 2, row, 3, 1);
		gpGridPlateConfig.add(lblDimHoles,0,++row);
		gpGridPlateConfig.add(hboxDimHoles, 2, row,3, 1);
		gpGridPlateConfig.add(lblCount, 0, ++row);
		gpGridPlateConfig.add(hboxCount, 2, row, 3, 1);
		gpGridPlateConfig.add(lblFirstHole, 0, ++row);
		gpGridPlateConfig.add(hboxFirst, 2, row, 3,1);
		gpGridPlateConfig.add(lblOffset, 0, ++row);
		gpGridPlateConfig.add(hboxOffset, 2, row, 3, 1);		
		gpGridPlateConfig.add(hboxSmoothTo,2,++row,5,1);
		gpGridPlateConfig.add(lblSmoothTo,0,row);
		gpGridPlateConfig.add(hboxSmoothFrom,2,++row,5,1);
		gpGridPlateConfig.add(lblSmoothFrom,0,row);
		
		StackPane spControls = new StackPane();
		spControls.getChildren().addAll(btnDelete, btnSave);
		spControls.setAlignment(Pos.CENTER);
		StackPane.setAlignment(btnDelete, Pos.CENTER_LEFT);
		StackPane.setAlignment(btnSave, Pos.CENTER_RIGHT);
		
		getContents().add(hboxSelectGridPlate, 0,0);
		getContents().add(gpGridPlateConfig, 0, 1);
		getContents().add(spControls, 0, 2, 2, 1);
	}
	
	public void setGridPlates(final Set<String> gridPlates) {
		cbbGridPlates.getItems().clear();
		cbbGridPlates.getItems().addAll(gridPlates);
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtName.setFocusListener(listener);
		numtxtX.setFocusListener(listener);
		numtxtY.setFocusListener(listener);
		numtxtOffsetX.setFocusListener(listener);
		numtxtOffsetY.setFocusListener(listener);
		itxtNbRows.setFocusListener(listener);
		itxtNbCols.setFocusListener(listener);
		numtxtHeight.setFocusListener(listener);
		numtxtHoleLength.setFocusListener(listener);
		numtxtHoleWidth.setFocusListener(listener);
		numtxtLength.setFocusListener(listener);
		numtxtWidth.setFocusListener(listener);
		numtxtPosX.setFocusListener(listener);
		numtxtPosY.setFocusListener(listener);
		numtxtSmoothToX.setFocusListener(listener);
		numtxtSmoothToY.setFocusListener(listener);
		numtxtSmoothToZ.setFocusListener(listener);
		numtxtSmoothFromX.setFocusListener(listener);
		numtxtSmoothFromY.setFocusListener(listener);
		numtxtSmoothFromZ.setFocusListener(listener);
	}
	
	@Override
	public void refresh() {
		reset();
		getPresenter().updateGridPlates();
	}
	
	public void showFormEdit() {
		gpGridPlateConfig.setVisible(true);
		btnNew.setDisable(true);
		btnDelete.setVisible(true);
		btnDelete.setDisable(false);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnSave.setVisible(true);
		validate();
	}
	
	public void showFormNew() {
		gpGridPlateConfig.setVisible(true);
		btnEdit.setDisable(true);
		btnDelete.setVisible(false);
		btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnSave.setVisible(true);
		btnDelete.setVisible(true);
		validate();
	}
	
	public void reset() {
		numtxtX.setText("");
		numtxtY.setText("");
		numtxtOffsetX.setText("");
		numtxtOffsetY.setText("");
		itxtNbRows.setText("");
		itxtNbCols.setText("");
		numtxtHeight.setText("");
		numtxtHoleLength.setText("");
		numtxtHoleWidth.setText("");
		numtxtLength.setText("");
		numtxtWidth.setText("");
		numtxtPosX.setText("");
		numtxtPosY.setText("");
		numtxtSmoothToX.setText("");
		numtxtSmoothToY.setText("");
		numtxtSmoothToZ.setText("");
		numtxtSmoothFromX.setText("");
		numtxtSmoothFromY.setText("");
		numtxtSmoothFromZ.setText("");
		fullTxtName.setText("");
		btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnNew.setDisable(false);
		btnTilted.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnHorizontal.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnHorizontal.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
		gpGridPlateConfig.setVisible(false);
		btnSave.setVisible(false);
		btnDelete.setVisible(false);
		cbbGridPlates.setValue(null);
		cbbGridPlates.setDisable(false);
	}

	public void validate() {
		if (!fullTxtName.getText().equals("") &&
				!numtxtX.getText().equals("") &&
				!numtxtY.getText().equals("") &&
				!numtxtOffsetX.getText().equals("") &&
				!numtxtOffsetY.getText().equals("") &&
				!itxtNbRows.getText().equals("") &&
				!itxtNbCols.getText().equals("") &&
				!numtxtHeight.getText().equals("") &&
				!numtxtHoleLength.getText().equals("") &&
				!numtxtHoleWidth.getText().equals("") &&
				!numtxtLength.getText().equals("") &&
				!numtxtWidth.getText().equals("") &&
				!numtxtPosX.getText().equals("") &&
				!numtxtPosY.getText().equals("") &&
				!numtxtSmoothToX.getText().equals("") &&
				!numtxtSmoothToY.getText().equals("") &&
				!numtxtSmoothToZ.getText().equals("") &&
				!numtxtSmoothFromX.getText().equals("") &&
				!numtxtSmoothFromY.getText().equals("") &&
				!numtxtSmoothFromZ.getText().equals("")) {
			btnSave.setDisable(false);
		} else {
			btnSave.setDisable(true);
			btnDelete.setDisable(true);
		}
	}
	
	public void gridPlateSelected(final GridPlateLayout gridPlate) {
		fullTxtName.setText("" + gridPlate.getName());
		numtxtX.setText("" + gridPlate.getFirstHolePosX());
		numtxtY.setText("" + gridPlate.getFirstHolePosY());
		numtxtOffsetX.setText("" + gridPlate.getHorizontalOffsetNxtPiece());
		numtxtOffsetY.setText("" + gridPlate.getVerticalOffsetNxtPiece());
		itxtNbRows.setText("" + gridPlate.getVerticalAmount());
		itxtNbCols.setText("" + gridPlate.getHorizontalAmount());
		numtxtHeight.setText("" + gridPlate.getHeight());
		numtxtHoleLength.setText("" + gridPlate.getHoleLength());
		numtxtHoleWidth.setText("" + gridPlate.getHoleWidth());
		numtxtLength.setText("" + gridPlate.getLength());
		numtxtWidth.setText("" + gridPlate.getWidth());
		numtxtPosX.setText("" + gridPlate.getPosX());
		numtxtPosY.setText("" + gridPlate.getPosY());
		Coordinates smoothTo = gridPlate.getSmoothTo();
		numtxtSmoothToX.setText(smoothTo.getX() +"");
		numtxtSmoothToY.setText(smoothTo.getY() + "");
		numtxtSmoothToZ.setText(smoothTo.getZ() + "");
		Coordinates smoothFrom = gridPlate.getSmoothFrom();
		numtxtSmoothFromX.setText(smoothFrom.getX() + "");
		numtxtSmoothFromY.setText(smoothFrom.getY() + "");
		numtxtSmoothFromZ.setText(smoothFrom.getZ() + "");
		getPresenter().changedOrientation(gridPlate.getHoleOrientation());
	}
	
	void setOrientation(final int orientation) {
		btnHorizontal.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnTilted.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		if (orientation == HoleOrientation.HORIZONTAL.getId()) {
			btnHorizontal.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		} else if (orientation == HoleOrientation.TILTED.getId()){
			btnTilted.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		}
	}

}
