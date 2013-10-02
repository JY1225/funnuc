package eu.robojob.millassist.ui.admin.robot;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.Gripper;
import eu.robojob.millassist.external.robot.Gripper.Type;
import eu.robojob.millassist.external.robot.GripperBody;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IconFlowSelector;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class RobotGripperView extends AbstractFormView<RobotGripperPresenter> {

	private IconFlowSelector ifsGrippers;
	private AbstractRobot robot;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final double ICONFLOWSELECTOR_WIDTH = 540;
	
	private static final String NAME = "RobotGripperView.name";
	private static final String HEIGHT = "RobotGripperView.height";
	private static final String FIXED_HEIGHT = "RobotGripperView.fixedHeight";
	private static final String EDIT = "RobotGripperView.edit";
	private static final String NEW = "RobotGripperView.new";
	private static final String SAVE = "RobotGripperView.save";
	private static final String REMOVE = "RobotGripperView.remove";
	private static final String GRIPPER_TYPE = "RobotGripperView.type";
	private static final String GRIPPER_TYPE_TWOPOINT = "RobotGripperView.twoPoint";
	private static final String GRIPPER_TYPE_VACUUM = "RobotGripperView.vacuum";
	
	private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";
	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";

	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	private static final double IMG_WIDTH = 90;
	private static final double IMG_HEIGHT = 90;
	
	private static final String CSS_CLASS_GRIPPER_IMAGE_EDIT = "gripper-image-edit";
	
	private Button btnCreateNew;
	private Button btnEdit;
	
	private VBox vboxForm;
	private GridPane gpEditor;
	private StackPane spImage;
	private ImageView imageVw;
	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblGripperType;
	private RadioButton rbGripperTypeTwoPoint;
	private RadioButton rbGripperTypeVacuum;
	private Label lblHeight;
	private NumericTextField numtxtHeight;
	private Region spacer;
	private Label lblFixedHeight;
	private CheckBox cbFixedHeight;
	private CheckBox cbA;
	private CheckBox cbB;
	private CheckBox cbC;
	private CheckBox cbD;
	
	private FileChooser fileChooser;
	private Button btnSave;
	private Button btnDelete;
	
	private String imagePath;
	
	public RobotGripperView() {
		super();
		build();
	}
	
	@Override
	protected void build() {
		getContents().setHgap(HGAP);
		getContents().setVgap(VGAP);
		getContents().setAlignment(Pos.TOP_CENTER);
		getContents().setPadding(new Insets(15, 0, 0, 0));
		
		getContents().getChildren().clear();
		
		ifsGrippers = new IconFlowSelector();
		ifsGrippers.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
		
		int column = 0;
		int row = 0;
		getContents().add(ifsGrippers, column++, row);
		
		HBox hboxButtons = new HBox();
		btnEdit = createButton(EDIT_PATH, null, Translator.getTranslation(EDIT), BTN_WIDTH, BTN_HEIGHT, null);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnEdit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().clickedEdit();
			}
		});
		btnCreateNew = createButton(ADD_PATH, null, Translator.getTranslation(NEW), BTN_WIDTH, BTN_HEIGHT, null);
		btnCreateNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		btnCreateNew.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().clickedNew();
			}
		});
		hboxButtons.getChildren().addAll(btnEdit, btnCreateNew);
		column = 0;
		row++;
		getContents().add(hboxButtons, column++, row);
		
		HBox hbox = new HBox();
		
		gpEditor = new GridPane();
		gpEditor.setVgap(VGAP);
		gpEditor.setHgap(10);
		
		spImage = new StackPane();
		spImage.setPrefSize(IMG_WIDTH, IMG_HEIGHT);
		spImage.setMaxSize(IMG_WIDTH, IMG_HEIGHT);
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
		spImage.setPadding(new Insets(10, 10, 10, 10));
		spImage.setPrefSize(IMG_HEIGHT + 20, IMG_HEIGHT + 20);
		spImage.setMinSize(IMG_HEIGHT + 20, IMG_HEIGHT + 20);
		spImage.setMaxSize(IMG_HEIGHT + 20, IMG_HEIGHT + 20);
		lblName = new Label(Translator.getTranslation(NAME));
		fulltxtName = new FullTextField(100);
		fulltxtName.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		lblGripperType = new Label(Translator.getTranslation(GRIPPER_TYPE));
		rbGripperTypeTwoPoint = new RadioButton(Translator.getTranslation(GRIPPER_TYPE_TWOPOINT));
		rbGripperTypeVacuum = new RadioButton(Translator.getTranslation(GRIPPER_TYPE_VACUUM));
		ToggleGroup tgGripperType = new ToggleGroup();
		rbGripperTypeTwoPoint.setToggleGroup(tgGripperType);
		rbGripperTypeVacuum.setToggleGroup(tgGripperType);
		rbGripperTypeTwoPoint.setSelected(true);
		lblHeight = new Label(Translator.getTranslation(HEIGHT));
		numtxtHeight = new NumericTextField(3);
		numtxtHeight.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtHeight.setMaxWidth(UIConstants.TEXT_FIELD_HEIGHT * 2);
		spacer = new Region();
		spacer.setPrefWidth(15);
		lblFixedHeight = new Label(Translator.getTranslation(FIXED_HEIGHT));
		cbFixedHeight = new CheckBox();
		cbA = new CheckBox("A");
		cbB = new CheckBox("B");
		cbC = new CheckBox("C");
		cbD = new CheckBox("D");
		cbA.setDisable(true);
		cbB.setDisable(true);
		cbC.setDisable(true);
		cbD.setDisable(true);
		gpEditor.setAlignment(Pos.CENTER);
		int column2 = 0;
		int row2 = 0;
		gpEditor.add(lblName, column2++, row2);
		gpEditor.add(fulltxtName, column2++, row2, 4, 1);
		column2 = 0;
		row2++;
		gpEditor.add(lblGripperType, column2++, row2);
		gpEditor.add(rbGripperTypeTwoPoint, column2++, row2);
		gpEditor.add(rbGripperTypeVacuum, column2++, row2);		
		column2 = 0;
		row2++;
		gpEditor.add(lblHeight, column2++, row2);
		gpEditor.add(numtxtHeight, column2++, row2);
		gpEditor.add(spacer, column2++, row2);
		gpEditor.add(lblFixedHeight, column2++, row2);
		gpEditor.add(cbFixedHeight, column2++, row2);
		HBox hboxHeads = new HBox();
		hboxHeads.getChildren().addAll(cbA, cbB, cbC, cbD);
		hboxHeads.setSpacing(20);
		column2 = 0;
		row2++;
		gpEditor.add(hboxHeads, column2++, row2, 5, 1);
		
		column = 0;
		row++;
		
		hbox.getChildren().addAll(spImage, gpEditor);
		hbox.setSpacing(20);
		hbox.setPrefWidth(USE_COMPUTED_SIZE);
		hbox.setAlignment(Pos.TOP_CENTER);
		
		btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				Gripper.Type type = Gripper.Type.TWOPOINT;
				if (rbGripperTypeTwoPoint.isSelected()) {
					type = Gripper.Type.TWOPOINT;
				} else if (rbGripperTypeVacuum.isSelected()) {
					type = Type.VACUUM;
				} else {
					throw new IllegalStateException("No type radio button selected");
				}
				getPresenter().saveData(fulltxtName.getText(), type, imagePath, Float.parseFloat(numtxtHeight.getText()), cbFixedHeight.selectedProperty().get(),
						cbA.selectedProperty().get(), cbB.selectedProperty().get(), cbC.selectedProperty().get(), cbD.selectedProperty().get());
			}
		});
		btnSave.getStyleClass().add("save-btn");
		
		btnDelete = createButton(DELETE_ICON_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(REMOVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().deleteGripper();
			}
		});
		btnDelete.getStyleClass().add("delete-btn");
		
		vboxForm = new VBox();
		vboxForm.getChildren().addAll(hbox, btnSave, btnDelete);
		vboxForm.setAlignment(Pos.CENTER);
		vboxForm.setSpacing(VGAP);
		
		column = 0;
		row++;
		getContents().add(vboxForm, column++, row);
		setMargin(vboxForm, new Insets(30, 0, 0, 0));

		GridPane.setHalignment(vboxForm, HPos.CENTER);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		numtxtHeight.setFocusListener(listener);
	}
	
	public void setRobot(final AbstractRobot robot) {
		this.robot = robot;
	}

	@Override
	public void refresh() {
		ifsGrippers.clearItems();
		Set<Gripper> grippers = new HashSet<Gripper>();
		for (GripperHead head : robot.getGripperBody().getGripperHeads()) {
			grippers.addAll(head.getPossibleGrippers());
		}
		int itemIndex = 0;
		for (final Gripper gripper : grippers) {
			ifsGrippers.addItem(itemIndex, gripper.getName(), gripper.getImageUrl(), new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					getPresenter().selectedGripper(gripper);
				}
			});
			itemIndex++;
		}
		reset();
	}
	
	public void gripperSelected(final Gripper gripper) {
		ifsGrippers.setSelected(gripper.getName());
		btnEdit.setDisable(false);
		fulltxtName.setText(gripper.getName());
		numtxtHeight.setText("" + gripper.getHeight());
		cbFixedHeight.setSelected(gripper.isFixedHeight());
		if (gripper.getType() == Type.TWOPOINT) {
			rbGripperTypeTwoPoint.setSelected(true);
		} else if (gripper.getType() == Type.VACUUM) {
			rbGripperTypeVacuum.setSelected(true);
		} else {
			throw new IllegalStateException("Unknown gripper type: " + gripper.getType());
		}
		String url = gripper.getImageUrl();
		if (url != null) {
			url = url.replace("file:///", "");
		}
		if ((url != null) && ((new File(url)).exists() || getClass().getClassLoader().getResource(url) != null)) {
			imageVw.setImage(new Image(gripper.getImageUrl(), IMG_WIDTH, IMG_HEIGHT, true, true));
		} else {
			imageVw.setImage(new Image(UIConstants.IMG_NOT_FOUND_URL, IMG_WIDTH, IMG_HEIGHT, true, true));
		}
		imagePath = gripper.getImageUrl();
		GripperBody body = robot.getGripperBody();
		cbA.setSelected((body.getGripperHeadByName("A") != null) && (body.getGripperHeadByName("A").getGripperById(gripper.getId()) != null));
		cbB.setSelected((body.getGripperHeadByName("B") != null) && (body.getGripperHeadByName("B").getGripperById(gripper.getId()) != null));
		cbC.setSelected((body.getGripperHeadByName("C") != null) && (body.getGripperHeadByName("C").getGripperById(gripper.getId()) != null));
		cbD.setSelected((body.getGripperHeadByName("D") != null) && (body.getGripperHeadByName("D").getGripperById(gripper.getId()) != null));
	}
	
	public void reset() {
		ifsGrippers.deselectAll();
		btnCreateNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnCreateNew.setDisable(false);
		cbFixedHeight.setSelected(false);
		btnEdit.setDisable(true);
		fulltxtName.setText("");
		numtxtHeight.setText("");
		cbA.setSelected(true);
		cbB.setSelected(true);
		cbC.setSelected(false);
		cbD.setSelected(false);
		imageVw.setImage(null);
		imagePath = null;
		validate();
		setFormVisible(false);
	}
	
	public void showFormNew() {
		setFormVisible(true);
		btnEdit.setDisable(true);
		btnDelete.setVisible(false);
		btnCreateNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void showFormEdit() {
		setFormVisible(true);
		btnCreateNew.setDisable(true);
		btnDelete.setVisible(true);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void setFormVisible(final boolean visible) {
		vboxForm.setVisible(visible);
	}
	
	private void validate() {
		if (!fulltxtName.getText().equals("") && !numtxtHeight.getText().equals("") && (Float.parseFloat(numtxtHeight.getText()) > 0) 
				&& (imagePath != null) && !imagePath.equals("")) {
			btnSave.setDisable(false);
		} else {
			btnSave.setDisable(true);
		}
	}

}
