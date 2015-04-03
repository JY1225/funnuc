package eu.robojob.millassist.ui.admin.device;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.Clamping.Type;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.EFixtureType;
import eu.robojob.millassist.external.device.WorkAreaManager;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.controls.CoordinateBox;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IconFlowSelector;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.SizeManager;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class CNCMachineClampingsView extends AbstractFormView<CNCMachineClampingsPresenter> {

	private IconFlowSelector ifsClampings;
	private Button btnEdit;
	private Button btnNew;
	private GridPane gpDetails;
	private Label lblName;
	private FullTextField fullTxtName;
	private Label lblHeight;
	private NumericTextField numtxtHeight;
	private Label lblType;
	private ComboBox<String> cbbType;
	private ComboBox<String> cbbFixtureType;
	// relative position
	private Label lblRelativePosition;
	private Label lblX;
	private NumericTextField numtxtX;
	private Label lblY;
	private NumericTextField numtxtY;
	private Label lblZ;
	private NumericTextField numtxtZ;
	private Label lblW;
	private NumericTextField numtxtW;
	private Label lblP;
	private NumericTextField numtxtP;
	private Label lblR;
	private NumericTextField numtxtR;
	// smooth to
	private Label lblSmoothTo;
	private Label lblSmoothToX;
	private NumericTextField numtxtSmoothToX;
	private Label lblSmoothToY;
	private NumericTextField numtxtSmoothToY;
	private Label lblSmoothToZ;
	private NumericTextField numtxtSmoothToZ;
	// smooth from
	private Label lblSmoothFrom;
	private Label lblSmoothFromX;
	private NumericTextField numtxtSmoothFromX;
	private Label lblSmoothFromY;
	private NumericTextField numtxtSmoothFromY;
	private Label lblSmoothFromZ;
	private NumericTextField numtxtSmoothFromZ;	
	// default airblow
	private CoordinateBox bottomAirblow, topAirblow;	
	// image
	private StackPane spImage;
	private ImageView imageVw;
	
	private ScrollPane spDetails;
	private StackPane spControls;
	
	private DeviceManager deviceManager;
	
	private FileChooser fileChooser;
	private String imagePath;
	
	 //WA1/WA2 checkbox
	 private CheckBox cbWa1, cbWa2;	
	
	private Button btnSave, btnCopy, btnDelete;
	
	private static final String EDIT = "CNCMachineClampingsView.edit";
	static final String COPY = "CNCMachineClampingsView.copy";
	static final String SAVE_AS_DIALOG = "CNCMachineClampingsView.saveAsDialog";
	static final String NAME = "CNCMachineClampingsView.name";
	private static final String NEW = "CNCMachineClampingsView.new";
	private static final String HEIGHT = "CNCMachineClampingsView.height";
	private static final String SAVE = "CNCMacineClampingsView.save";
	private static final String REMOVE = "CNCMacineClampingsView.remove";
	private static final String TYPE = "CNCMachineClampingsView.type";
	
	private static final String AIRBLOW_ERROR = "CNCMillingMachinePickPresenter.airblowOutOfBound";
	private static final String AIRBLOW_BOTTOM = "CNCMachineClampingsView.airblowBottom";
	private static final String AIRBLOW_TOP = "CNCMachineClampingsView.airblowTop";
	private static final String RELATIVE_POSITION = "CNCMachineClampingsView.relativePosition";
	private static final String SMOOTH_TO = "CNCMachineClampingsView.smoothTo";
	private static final String SMOOTH_FROM = "CNCMachineClampingsView.smoothFrom";
	private static final String CSS_CLASS_GRIPPER_IMAGE_EDIT = "gripper-image-edit";
	private static final String WA1 = "CNCMachineGeneralView.wa1";
	private static final String WA2 = "CNCMachineGeneralView.wa2";

	private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";
	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";

	private static final double ICONFLOWSELECTOR_PADDING = 20;
	
	private static final double ICONFLOWSELECTOR_WIDTH = SizeManager.WIDTH-ICONFLOWSELECTOR_PADDING*2 - SizeManager.ADMIN_MENU_WIDTH - SizeManager.ADMIN_SUBMENU_WIDTH;
	private static final double IMG_WIDTH = 90;
	private static final double IMG_HEIGHT = 90;
	private static final double LBL_WIDTH = 25;
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	
	private static final String CLAMPING_TYPE_CENTRUM = "Centrum";
	private static final String CLAMPING_TYPE_FIXED_XP = "Fix X +";
	private static final String CLAMPING_TYPE_FIXED_XM = "Fix X -";
	private static final String CLAMPING_TYPE_FIXED_YP = "Fix Y +";
	private static final String CLAMPING_TYPE_FIXED_YM = "Fix Y -";

	
	public CNCMachineClampingsView() {
		this(null);
	}
	
	public CNCMachineClampingsView(final DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
		build();
	}
	
	@Override
	protected void build() {
		getContents().setVgap(10);
		getContents().setHgap(10);
		getContents().setAlignment(Pos.TOP_CENTER);
		getContents().setPadding(new Insets(10, 0, 0, 0));
		
		getContents().getChildren().clear();
		
		VBox vboxSelectClamping = new VBox();
		vboxSelectClamping.setAlignment(Pos.CENTER_LEFT);
		
		ifsClampings = new IconFlowSelector();
		ifsClampings.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
		ifsClampings.setMaxWidth(ICONFLOWSELECTOR_WIDTH);
		ifsClampings.setMinWidth(ICONFLOWSELECTOR_WIDTH);
		
		HBox hboxButtons = new HBox();
		btnEdit = createButton(EDIT_PATH, null, Translator.getTranslation(EDIT), BTN_WIDTH, BTN_HEIGHT, null);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnEdit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().clickedEdit();
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
		vboxSelectClamping.setSpacing(10);
		vboxSelectClamping.getChildren().addAll(ifsClampings, hboxButtons);
		
		spImage = new StackPane();
		spImage.setPadding(new Insets(5, 5, 5, 5));
		spImage.setPrefSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
		spImage.setMinSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
		spImage.setMaxSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
		spImage.getStyleClass().add(CSS_CLASS_GRIPPER_IMAGE_EDIT);
		spImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG (*.png)", "*.png");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(null);
				if (file != null) {
					Image image = new Image("file:///" + file.getAbsolutePath(), IMG_WIDTH, IMG_HEIGHT, true, true);
					imageVw.setImage(image);
					imagePath = "file:///" + file.getAbsolutePath();
					validate();
				}
			}
		});
		imageVw = new ImageView();
		imageVw.setFitWidth(IMG_WIDTH);
		imageVw.setFitHeight(IMG_HEIGHT);
		spImage.getChildren().add(imageVw);
		
		lblName = new Label(Translator.getTranslation(NAME));
		lblName.setMinWidth(65);
		fullTxtName = new FullTextField(90);
		fullTxtName.setMinSize(240, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtName.setPrefSize(240, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtName.setMaxSize(240, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
				validate();
			}
		});
		lblHeight = new Label(Translator.getTranslation(HEIGHT));
		lblHeight.setMinWidth(65);
		numtxtHeight = new NumericTextField(6);
		numtxtHeight.setPrefHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtHeight.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtHeight.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		cbWa1 = new CheckBox(Translator.getTranslation(WA1));
		cbWa1.setMinWidth(65);
		cbWa1.setPrefWidth(65);
		cbWa1.setMaxWidth(65);
		cbWa1.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				//Do the opposite for cbWa2
				if (!cbWa2.isDisabled()) {
					cbWa2.setSelected(oldValue);
				} 
			}
		});
		cbWa2 = new CheckBox(Translator.getTranslation(WA2));
		cbWa2.setMinWidth(65);
		cbWa2.setPrefWidth(65);
		cbWa2.setMaxWidth(65);
		cbWa2.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				//Do the opposite for cbWa1
				if (!cbWa1.isDisabled()) {
					cbWa1.setSelected(oldValue);
				} 
			}
		});
		
		lblType = new Label(Translator.getTranslation(TYPE));
		lblType.setMinWidth(65);
		cbbType = new ComboBox<String>();
		cbbType.setPrefSize(100, UIConstants.COMBO_HEIGHT);
		cbbType.setMinSize(100, UIConstants.COMBO_HEIGHT);
		cbbType.getItems().add(CLAMPING_TYPE_CENTRUM);
		cbbType.getItems().add(CLAMPING_TYPE_FIXED_XM);
		cbbType.getItems().add(CLAMPING_TYPE_FIXED_XP);
		cbbType.getItems().add(CLAMPING_TYPE_FIXED_YM);
		cbbType.getItems().add(CLAMPING_TYPE_FIXED_YP);
		
		cbbFixtureType = new ComboBox<String>();
		cbbFixtureType.setPrefSize(130, UIConstants.COMBO_HEIGHT);
		cbbFixtureType.setMinSize(130, UIConstants.COMBO_HEIGHT);
		for (EFixtureType fixType: EFixtureType.values()) {
			if(fixType != EFixtureType.DEFAULT && fixType.getHighestNbOfFixtureUsed() <= deviceManager.getCNCMachines().iterator().next().getNbFixtures())
				cbbFixtureType.getItems().add(fixType.toString());
		}
		cbbFixtureType.setVisibleRowCount(5);
		
		lblRelativePosition = new Label(Translator.getTranslation(RELATIVE_POSITION));
		lblRelativePosition.setPrefWidth(150);
		lblRelativePosition.setMinWidth(150);
		lblX = new Label("X");
		lblX.setMinWidth(LBL_WIDTH);
		lblX.setPrefWidth(LBL_WIDTH);
		numtxtX = new NumericTextField(6);
		numtxtX.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtX.setMaxHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtX.setMinHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblY = new Label("Y");
		lblY.setMinWidth(LBL_WIDTH);
		lblY.setPrefWidth(LBL_WIDTH);
		numtxtY = new NumericTextField(6);
		numtxtY.setPrefHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtY.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtY.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblZ = new Label("Z");
		lblZ.setMinWidth(LBL_WIDTH);
		lblZ.setPrefWidth(LBL_WIDTH);
		numtxtZ = new NumericTextField(6);
		numtxtZ.setPrefHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtZ.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtZ.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblW = new Label("W");
		lblW.setMinWidth(LBL_WIDTH);
		lblW.setPrefWidth(LBL_WIDTH);
		numtxtW = new NumericTextField(6);
		numtxtW.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtW.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtW.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtW.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblP = new Label("P");
		lblP.setMinWidth(LBL_WIDTH);
		lblP.setPrefWidth(LBL_WIDTH);
		numtxtP = new NumericTextField(6);
		numtxtP.setPrefHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtP.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtP.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtP.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblR = new Label("R");
		lblR.setMinWidth(LBL_WIDTH);
		lblR.setPrefWidth(LBL_WIDTH);
		numtxtR = new NumericTextField(6);
		numtxtR.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtR.setMaxHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtR.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtR.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		lblSmoothTo = new Label(Translator.getTranslation(SMOOTH_TO));
		lblSmoothTo.setPrefWidth(150);
		lblSmoothTo.setMinWidth(150);
		lblSmoothToX = new Label("X");
		lblSmoothToX.setPrefWidth(LBL_WIDTH);
		lblSmoothToX.setMinWidth(LBL_WIDTH);
		numtxtSmoothToX = new NumericTextField(5);
		numtxtSmoothToX.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToX.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToX.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothToY = new Label("Y");
		lblSmoothToY.setPrefWidth(LBL_WIDTH);
		numtxtSmoothToY = new NumericTextField(5);
		numtxtSmoothToY.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToY.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToY.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothToZ = new Label("Z");
		lblSmoothToZ.setPrefWidth(LBL_WIDTH);
		numtxtSmoothToZ = new NumericTextField(5);
		numtxtSmoothToZ.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToZ.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToZ.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		lblSmoothFrom = new Label(Translator.getTranslation(SMOOTH_FROM));
		lblSmoothFrom.setPrefWidth(150);
		lblSmoothFrom.setMinWidth(150);
		lblSmoothFromX = new Label("X");
		lblSmoothFromX.setPrefWidth(LBL_WIDTH);
		numtxtSmoothFromX = new NumericTextField(5);
		numtxtSmoothFromX.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromX.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromX.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothFromY = new Label("Y");
		lblSmoothFromY.setPrefWidth(LBL_WIDTH);
		numtxtSmoothFromY = new NumericTextField(5);
		numtxtSmoothFromY.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromY.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromY.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothFromZ = new Label("Z");
		lblSmoothFromZ.setPrefWidth(LBL_WIDTH);
		numtxtSmoothFromZ = new NumericTextField(5);
		numtxtSmoothFromZ.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromZ.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromZ.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		Label airblowTopLabel = new Label(Translator.getTranslation(AIRBLOW_TOP));
		airblowTopLabel.setPrefWidth(150);
		airblowTopLabel.setMinWidth(150);
		airblowTopLabel.setMaxWidth(150);
		Label airblowBottomLabel = new Label(Translator.getTranslation(AIRBLOW_BOTTOM));
		airblowBottomLabel.setPrefWidth(150);
		airblowBottomLabel.setMinWidth(150);
		airblowBottomLabel.setMaxWidth(150);
		bottomAirblow = new CoordinateBox(5, "X","Y","Z");
		bottomAirblow.setPrefHeightDimension(UIConstants.TEXT_FIELD_HEIGHT);
		topAirblow = new CoordinateBox(5, "X","Y");
		topAirblow.setPrefHeightDimension(UIConstants.TEXT_FIELD_HEIGHT);
		addChangeListeners();
		
		btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				Clamping.Type type = null;
				String selectedType = cbbType.getValue();
				if (selectedType == CLAMPING_TYPE_CENTRUM) {
					type = Type.CENTRUM;
				} else if (selectedType == CLAMPING_TYPE_FIXED_XP) {
					type = Type.FIXED_XP;
				} else if (selectedType == CLAMPING_TYPE_FIXED_XM) {
					type = Type.FIXED_XM;
				} else if (selectedType == CLAMPING_TYPE_FIXED_YP) {
					type = Type.FIXED_YP;
				} else if (selectedType == CLAMPING_TYPE_FIXED_YM) {
					type = Type.FIXED_YM;
				}
				int waNr = 1;
				if (cbWa1.isSelected()) {
					waNr = 1;
				} else if (cbWa2.isSelected()) {
					waNr = 2;
				}
				EFixtureType fixtureType = EFixtureType.getFixtureTypeFromStringValue(cbbFixtureType.getValue());
				getPresenter().updateClamping(fullTxtName.getText(), Float.parseFloat(numtxtHeight.getText()), imagePath,
						Float.parseFloat(numtxtX.getText()), Float.parseFloat(numtxtY.getText()), 
						Float.parseFloat(numtxtZ.getText()), Float.parseFloat(numtxtW.getText()), 
						Float.parseFloat(numtxtP.getText()), Float.parseFloat(numtxtR.getText()),
						Float.parseFloat(numtxtSmoothToX.getText()), Float.parseFloat(numtxtSmoothToY.getText()),
						Float.parseFloat(numtxtSmoothToZ.getText()), Float.parseFloat(numtxtSmoothFromX.getText()), 
						Float.parseFloat(numtxtSmoothFromY.getText()), Float.parseFloat(numtxtSmoothFromZ.getText()),
						type, fixtureType, bottomAirblow.getCoordinate(), topAirblow.getCoordinate(), waNr);
			}
		});
		btnCopy = createButton(ADD_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(COPY), BTN_WIDTH + 40, BTN_HEIGHT, new EventHandler<ActionEvent>() {	
			@Override
			public void handle(final ActionEvent arg0) {
				Clamping.Type type = null;
				String selectedType = cbbType.getValue();
				if (selectedType == CLAMPING_TYPE_CENTRUM) {
					type = Type.CENTRUM;
				} else if (selectedType == CLAMPING_TYPE_FIXED_XP) {
					type = Type.FIXED_XP;
				} else if (selectedType == CLAMPING_TYPE_FIXED_XM) {
					type = Type.FIXED_XM;
				} else if (selectedType == CLAMPING_TYPE_FIXED_YP) {
					type = Type.FIXED_YP;
				} else if (selectedType == CLAMPING_TYPE_FIXED_YM) {
					type = Type.FIXED_YM;
				}
				int waNr = 1;
				if (cbWa1.isSelected()) {
					waNr = 1;
				} else if (cbWa2.isSelected()) {
					waNr = 2;
				}
				EFixtureType fixtureType = EFixtureType.getFixtureTypeFromStringValue(cbbFixtureType.getValue());
				getPresenter().copyClamping(Float.parseFloat(numtxtHeight.getText()), imagePath,
						Float.parseFloat(numtxtX.getText()), Float.parseFloat(numtxtY.getText()), 
						Float.parseFloat(numtxtZ.getText()), Float.parseFloat(numtxtW.getText()), 
						Float.parseFloat(numtxtP.getText()), Float.parseFloat(numtxtR.getText()),
						Float.parseFloat(numtxtSmoothToX.getText()), Float.parseFloat(numtxtSmoothToY.getText()),
						Float.parseFloat(numtxtSmoothToZ.getText()), Float.parseFloat(numtxtSmoothFromX.getText()), 
						Float.parseFloat(numtxtSmoothFromY.getText()), Float.parseFloat(numtxtSmoothFromZ.getText()),
						type, fixtureType, new Coordinates(bottomAirblow.getCoordinate()), new Coordinates(topAirblow.getCoordinate()),
						waNr);	
			}
		});
		btnDelete = createButton(DELETE_ICON_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(REMOVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().deleteClamping();
			}
		});
		btnDelete.getStyleClass().add("delete-btn");
		
		spControls = new StackPane();
		spControls.getChildren().addAll(btnDelete,btnCopy, btnSave);
		spControls.setAlignment(Pos.CENTER);
		StackPane.setAlignment(btnDelete, Pos.CENTER_LEFT);
		StackPane.setAlignment(btnCopy, Pos.CENTER);
		StackPane.setAlignment(btnSave, Pos.CENTER_RIGHT);

		spDetails = new ScrollPane();
		spDetails.setHbarPolicy(ScrollBarPolicy.NEVER);
		spDetails.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spDetails.setPannable(false);
		spDetails.setFitToHeight(true);
		spDetails.setFitToWidth(true);
			
		gpDetails = new GridPane();
		gpDetails.setAlignment(Pos.CENTER);
		gpDetails.setVgap(10);
		spDetails.setContent(gpDetails);
		int column = 0;
		int row = 0;
		gpDetails.add(spImage, column++, row);
		GridPane gpNameHeight = new GridPane();
		gpNameHeight.setVgap(10);
		gpNameHeight.setHgap(10);
		gpNameHeight.add(lblName, 0, 0);
		gpNameHeight.add(fullTxtName, 1, 0, 3, 1);
		gpNameHeight.add(lblHeight, 0, 1);
		gpNameHeight.add(numtxtHeight, 1, 1);
		gpNameHeight.add(cbWa1, 2, 1);
		gpNameHeight.add(cbWa2, 3, 1); 
		gpNameHeight.add(lblType, 0, 2);
		gpNameHeight.add(cbbType, 1, 2, 1, 1);
		gpNameHeight.add(cbbFixtureType, 2, 2, 2, 1);
		gpNameHeight.setAlignment(Pos.CENTER_LEFT);
		gpDetails.add(gpNameHeight, 1, row, 2, 1);
		column = 0; row++;
		GridPane gpRelativePosition = new GridPane();
		gpRelativePosition.setVgap(10);
		gpRelativePosition.setHgap(10);
		gpRelativePosition.add(lblRelativePosition, 0, 0);
		gpRelativePosition.add(lblX, 1, 0);
		gpRelativePosition.add(numtxtX, 2, 0);
		gpRelativePosition.add(lblY, 3, 0);
		gpRelativePosition.add(numtxtY, 4, 0);
		gpRelativePosition.add(lblZ, 5, 0);
		gpRelativePosition.add(numtxtZ, 6, 0);
		gpRelativePosition.add(lblW, 1, 1);
		gpRelativePosition.add(numtxtW, 2, 1);
		gpRelativePosition.add(lblP, 3, 1);
		gpRelativePosition.add(numtxtP, 4, 1);
		gpRelativePosition.add(lblR, 5, 1);
		gpRelativePosition.add(numtxtR, 6, 1);
		gpRelativePosition.setAlignment(Pos.CENTER_LEFT);
		gpDetails.add(gpRelativePosition, column++, row, 7, 1);
		column = 0; row++;
		HBox hboxSmoothTo = new HBox();
		hboxSmoothTo.setAlignment(Pos.CENTER_LEFT);
		hboxSmoothTo.setSpacing(10);
		hboxSmoothTo.getChildren().addAll(lblSmoothTo, lblSmoothToX, numtxtSmoothToX, lblSmoothToY, numtxtSmoothToY, lblSmoothToZ, numtxtSmoothToZ);
		gpDetails.add(hboxSmoothTo, column++, row, 7, 1);
		column = 0; row++;
		HBox hboxSmoothFrom = new HBox();
		hboxSmoothFrom.setAlignment(Pos.CENTER_LEFT);
		hboxSmoothFrom.setSpacing(10);
		hboxSmoothFrom.getChildren().addAll(lblSmoothFrom, lblSmoothFromX, numtxtSmoothFromX, lblSmoothFromY, numtxtSmoothFromY, lblSmoothFromZ, numtxtSmoothFromZ);
		gpDetails.add(hboxSmoothFrom, column++, row, 6, 1);
		column = 0; row++;
		gpDetails.add(airblowBottomLabel, column++, row);
		bottomAirblow.setPadding(new Insets(0,10,0,10));
		gpDetails.add(bottomAirblow, column, row, 6,1);
		column = 0; row++;
		gpDetails.add(airblowTopLabel, column++, row);
		topAirblow.setPadding(new Insets(0,10,0,10));
		gpDetails.add(topAirblow, column, row, 6,1);
		
		//Buttons
		column = 0; row++;
		spDetails.setVisible(false);
		spControls.setVisible(false);
		GridPane.setHalignment(spControls, HPos.CENTER);
		gpDetails.setAlignment(Pos.CENTER);
		GridPane.setVgrow(spDetails, Priority.ALWAYS);
		GridPane.setHalignment(gpDetails, HPos.CENTER);
		getContents().add(vboxSelectClamping, 0, 0);
		getContents().add(spDetails, 0, 1);
		getContents().add(spControls, 0,2);
		GridPane.setMargin(spDetails, new Insets(0, 0, 10, 0));
		gpDetails.setPadding(new Insets(10, 0, 10, 0));
		spControls.setPadding(new Insets(0, 0, 10, 0));
		getContents().setAlignment(Pos.CENTER);
		GridPane.setHalignment(spDetails, HPos.CENTER);
		GridPane.setValignment(spImage, VPos.TOP);
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtName.setFocusListener(listener);
		numtxtHeight.setFocusListener(listener);
		numtxtX.setFocusListener(listener);
		numtxtY.setFocusListener(listener);
		numtxtZ.setFocusListener(listener);
		numtxtW.setFocusListener(listener);
		numtxtP.setFocusListener(listener);
		numtxtR.setFocusListener(listener);
		numtxtSmoothToX.setFocusListener(listener);
		numtxtSmoothToY.setFocusListener(listener);
		numtxtSmoothToZ.setFocusListener(listener);
		numtxtSmoothFromX.setFocusListener(listener);
		numtxtSmoothFromY.setFocusListener(listener);
		numtxtSmoothFromZ.setFocusListener(listener);
		bottomAirblow.setTextFieldListener(listener);
		topAirblow.setTextFieldListener(listener);
	}

	public void setDeviceManager(final DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	
	private void addChangeListeners() {
		topAirblow.addChangeListeners(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				topAirblow.updateCoordinate();
				validate();
			}
		});
		bottomAirblow.addChangeListeners(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				bottomAirblow.updateCoordinate();
				validate();
			}
		});
	}
	
	@Override
	public void refresh() {
		ifsClampings.clearItems();
		boolean wa1Present = false;
		boolean wa2Present = false;
		int itemIndex = 0;
		Set<Integer> addedClampingIds = new HashSet<Integer>();
		for (AbstractCNCMachine machine : deviceManager.getCNCMachines()) {
			for (final WorkAreaManager workArea : machine.getWorkAreaManagers()) {
				if (workArea.getWorkAreaNr() == 1) {
					wa1Present = true;
				} else if (workArea.getWorkAreaNr() == 2) {
					wa2Present = true;
				}
				for (final Clamping clamping : workArea.getClampings()) {
					if (!addedClampingIds.contains(clamping.getId())) {
						ifsClampings.addItem(itemIndex, clamping.getName(), clamping.getImageUrl(), clamping.getFixtureType().toShortString(), new EventHandler<MouseEvent>(){
							@Override
							public void handle(final MouseEvent arg0) {
								getPresenter().selectedClamping(clamping, workArea.getWorkAreaNr());
							}
						});
						itemIndex++;
						addedClampingIds.add(clamping.getId());
					}
				}
			}
		}
		if (wa1Present && wa2Present) {
			cbWa1.setDisable(false);
			cbWa2.setDisable(false);
		} else {
			cbWa1.setDisable(true);
			cbWa2.setDisable(true);
		}
		getPresenter().disableEditMode();
		reset();
	}
	
	public void showFormEdit() {
		spDetails.setVisible(true);
		spControls.setVisible(true);
		btnNew.setDisable(true);
		btnDelete.setVisible(true);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void showFormNew() {
		spDetails.setVisible(true);
		spControls.setVisible(true);
		btnEdit.setDisable(true);
		btnDelete.setVisible(false);
		btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void reset() {
		numtxtX.setDisable(false);
		numtxtY.setDisable(false);
		numtxtZ.setDisable(false);
		numtxtR.setDisable(false);
		lblRelativePosition.setDisable(false);
		lblX.setDisable(false);
		lblY.setDisable(false);
		lblZ.setDisable(false);
		lblR.setDisable(false);
		fullTxtName.setText("");
		numtxtHeight.setText("");
		btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		ifsClampings.deselectAll();
		btnNew.setDisable(false);
		btnEdit.setDisable(true);
		numtxtX.setText("");
		numtxtY.setText("");
		numtxtZ.setText("");
		numtxtW.setText("");
		numtxtP.setText("");
		numtxtR.setText("");
		numtxtSmoothToX.setText("");
		numtxtSmoothToY.setText("");
		numtxtSmoothToZ.setText("");
		numtxtSmoothFromX.setText("");
		numtxtSmoothFromY.setText("");
		numtxtSmoothFromZ.setText("");
		cbWa1.setSelected(true);
		cbWa2.setSelected(false);
		imagePath = null;
		imageVw.setImage(null);
		validate();
		spDetails.setVisible(false);
		spControls.setVisible(false);
	}
	
	public void clampingSelected(final Clamping clamping, final int workAreaNr) {
		ifsClampings.setSelected(clamping.getName());
		btnEdit.setDisable(false);
		fullTxtName.setText(clamping.getName());
		numtxtHeight.setText("" + clamping.getHeight());
		numtxtX.setText("" + clamping.getRelativePosition().getX());
		numtxtY.setText("" + clamping.getRelativePosition().getY());
		numtxtZ.setText("" + clamping.getRelativePosition().getZ());
		numtxtW.setText("" + clamping.getRelativePosition().getW());
		numtxtP.setText("" + clamping.getRelativePosition().getP());
		numtxtR.setText("" + clamping.getRelativePosition().getR());
		numtxtSmoothToX.setText("" + clamping.getSmoothToPoint().getX());
		numtxtSmoothToY.setText("" + clamping.getSmoothToPoint().getY());
		numtxtSmoothToZ.setText("" + clamping.getSmoothToPoint().getZ());
		numtxtSmoothFromX.setText("" + clamping.getSmoothFromPoint().getX());
		numtxtSmoothFromY.setText("" + clamping.getSmoothFromPoint().getY());
		numtxtSmoothFromZ.setText("" + clamping.getSmoothFromPoint().getZ());
		String url = clamping.getImageUrl();
		if (clamping.getType() == Type.CENTRUM) {
			cbbType.setValue(CLAMPING_TYPE_CENTRUM);
		} else if (clamping.getType() == Type.FIXED_XP) {
			cbbType.setValue(CLAMPING_TYPE_FIXED_XP);
		}  else if (clamping.getType() == Type.FIXED_XM) {
			cbbType.setValue(CLAMPING_TYPE_FIXED_XM);
		} else if (clamping.getType() == Type.FIXED_YP) {
			cbbType.setValue(CLAMPING_TYPE_FIXED_YP);
		} else if (clamping.getType() == Type.FIXED_YM) {
			cbbType.setValue(CLAMPING_TYPE_FIXED_YM);
		}
		cbbFixtureType.setValue(clamping.getFixtureType().toString());
		if (url != null) {
			url = url.replace("file:///", "");
		}
		if ((url != null) && ((new File(url)).exists() || getClass().getClassLoader().getResource(url) != null)) {
			imageVw.setImage(new Image(clamping.getImageUrl(), IMG_WIDTH, IMG_HEIGHT, true, true));
		} else {
			imageVw.setImage(new Image(UIConstants.IMG_NOT_FOUND_URL, IMG_WIDTH, IMG_HEIGHT, true, true));
		}
		imagePath = clamping.getImageUrl();
		if (clamping.getDefaultAirblowPoints().getTopCoord() != null && clamping.getDefaultAirblowPoints().getBottomCoord() != null) {
			topAirblow.setCoordinate(clamping.getDefaultAirblowPoints().getTopCoord());
			topAirblow.reset();
			bottomAirblow.setCoordinate(clamping.getDefaultAirblowPoints().getBottomCoord());
			bottomAirblow.reset();
		}
		if (workAreaNr == 1) {
			cbWa1.setSelected(true);
			cbWa2.setSelected(false);
		} else if (workAreaNr == 2) {
			cbWa2.setSelected(true);
			cbWa1.setSelected(false);
		}
	}

	public void validate() {
		hideNotification();
		if (!fullTxtName.getText().equals("") 
				&& !numtxtHeight.getText().equals("") 
				&& isRelPosFilled()
				&& !numtxtSmoothToX.getText().equals("")
				&& !numtxtSmoothToY.getText().equals("")
				&& !numtxtSmoothToZ.getText().equals("")
				&& !numtxtSmoothFromX.getText().equals("")
				&& !numtxtSmoothFromY.getText().equals("")
				&& !numtxtSmoothFromZ.getText().equals("")
				&& (imagePath != null) && !imagePath.equals("")
				&& topAirblow.isConfigured()
				&& bottomAirblow.isConfigured()
				&& (cbWa1.isSelected() || cbWa2.isSelected())
				&& isInsideMachineBoundaries()
				) {
			btnSave.setDisable(false);
		} else {
			btnSave.setDisable(true);
		}
	}
	
	private boolean isRelPosFilled() {
		return (!numtxtX.getText().equals("")
				&& !numtxtY.getText().equals("")
				&& !numtxtZ.getText().equals("")
				&& !numtxtW.getText().equals("")
				&& !numtxtP.getText().equals("")
				&& !numtxtR.getText().equals(""));
	}
	
	private boolean isInsideMachineBoundaries() {
		WorkAreaManager workarea;
		if (cbWa1.isSelected()) {
			workarea = getPresenter().getWorkArea(1);
		} else {
			workarea = getPresenter().getWorkArea(2);
		}	
		if (workarea != null && workarea.getBoundaries() != null && isRelPosFilled()) {
			AirblowSquare square = workarea.getBoundaries().getBoundary();
			Coordinates relPos = new Coordinates(numtxtX.getValue(), numtxtY.getValue(), numtxtZ.getValue(), numtxtW.getValue(), numtxtP.getValue(), numtxtR.getValue());
			Coordinates lowerLeftCorner = Coordinates.add(bottomAirblow.getCoordinate(), relPos);
			Coordinates upperRightCorner = Coordinates.add(topAirblow.getCoordinate(), relPos);
			upperRightCorner.setZ(lowerLeftCorner.getZ());
			if (!square.isCoordinateInsideArea(lowerLeftCorner) || !square.isCoordinateInsideArea(upperRightCorner)) {
				showNotification(Translator.getTranslation(AIRBLOW_ERROR), eu.robojob.millassist.ui.general.NotificationBox.Type.WARNING);
				return false;
			}
			return true;
		}
		return false;
	}
	
}
