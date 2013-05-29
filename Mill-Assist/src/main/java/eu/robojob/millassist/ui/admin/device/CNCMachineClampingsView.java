package eu.robojob.millassist.ui.admin.device;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IconFlowSelector;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
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
	// relative position
	private Label lblRelativePosition;
	private Label lblX;
	private NumericTextField numtxtX;
	private Label lblY;
	private NumericTextField numtxtY;
	private Label lblZ;
	private NumericTextField numtxtZ;
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
	// image
	private StackPane spImage;
	private ImageView imageVw;
	
	private DeviceManager deviceManager;
	
	private FileChooser fileChooser;
	private String imagePath;
	
	private Button btnSave;
	private Button btnDelete;
	
	private static final String EDIT = "CNCMachineClampingsView.edit";
	private static final String NEW = "CNCMachineClampingsView.new";
	private static final String NAME = "CNCMachineClampingsView.name";
	private static final String HEIGHT = "CNCMachineClampingsView.height";
	private static final String SAVE = "CNCMacineClampingsView.save";
	private static final String REMOVE = "CNCMacineClampingsView.remove";
	
	private static final String RELATIVE_POSITION = "CNCMachineClampingsView.relativePosition";
	private static final String SMOOTH_TO = "CNCMachineClampingsView.smoothTo";
	private static final String SMOOTH_FROM = "CNCMachineClampingsView.smoothFrom";
	private static final String CSS_CLASS_GRIPPER_IMAGE_EDIT = "gripper-image-edit";

	private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";
	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";

	private static final double ICONFLOWSELECTOR_WIDTH = 540;
	private static final double IMG_WIDTH = 90;
	private static final double IMG_HEIGHT = 90;
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	
	public CNCMachineClampingsView() {
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
		fullTxtName = new FullTextField(100);
		fullTxtName.setMaxWidth(250);
		fullTxtName.setPrefWidth(250);
		fullTxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
				validate();
			}
		});
		lblHeight = new Label(Translator.getTranslation(HEIGHT));
		numtxtHeight = new NumericTextField(6);
		numtxtHeight.setMaxWidth(75);
		numtxtHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblRelativePosition = new Label(Translator.getTranslation(RELATIVE_POSITION));
		lblRelativePosition.setPrefWidth(125);
		lblX = new Label("X");
		numtxtX = new NumericTextField(6);
		numtxtX.setMaxWidth(75);
		numtxtX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblY = new Label("Y");
		numtxtY = new NumericTextField(6);
		numtxtY.setMaxWidth(75);
		numtxtY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblZ = new Label("Z");
		numtxtZ = new NumericTextField(6);
		numtxtZ.setMaxWidth(75);
		numtxtZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblR = new Label("R");
		numtxtR = new NumericTextField(6);
		numtxtR.setMaxWidth(75);
		numtxtR.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		lblSmoothTo = new Label(Translator.getTranslation(SMOOTH_TO));
		lblSmoothTo.setPrefWidth(125);
		lblSmoothToX = new Label("X");
		numtxtSmoothToX = new NumericTextField(5);
		lblSmoothToY = new Label("Y");
		numtxtSmoothToY = new NumericTextField(5);
		lblSmoothToZ = new Label("Z");
		numtxtSmoothToZ = new NumericTextField(5);
		
		lblSmoothFrom = new Label(Translator.getTranslation(SMOOTH_FROM));
		lblSmoothFrom.setPrefWidth(125);
		lblSmoothFromX = new Label("X");
		numtxtSmoothFromX = new NumericTextField(5);
		lblSmoothFromY = new Label("Y");
		numtxtSmoothFromY = new NumericTextField(5);
		lblSmoothFromZ = new Label("Z");
		numtxtSmoothFromZ = new NumericTextField(5);
		
		btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().saveData(fullTxtName.getText(), Float.parseFloat(numtxtHeight.getText()), imagePath,
						Float.parseFloat(numtxtX.getText()), Float.parseFloat(numtxtY.getText()), 
						Float.parseFloat(numtxtZ.getText()), Float.parseFloat(numtxtR.getText()),
						Float.parseFloat(numtxtSmoothToX.getText()), Float.parseFloat(numtxtSmoothToY.getText()),
						Float.parseFloat(numtxtSmoothToZ.getText()), Float.parseFloat(numtxtSmoothFromX.getText()), 
						Float.parseFloat(numtxtSmoothFromY.getText()), Float.parseFloat(numtxtSmoothFromZ.getText()));
			}
		});
		btnDelete = createButton(DELETE_ICON_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(REMOVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().deleteClamping();
			}
		});
		btnDelete.getStyleClass().add("delete-btn");
		
		VBox vboxButtons = new VBox();
		vboxButtons.getChildren().addAll(btnSave, btnDelete);
		vboxButtons.setSpacing(10);
		vboxButtons.setAlignment(Pos.CENTER);

		gpDetails = new GridPane();
		gpDetails.setAlignment(Pos.CENTER_LEFT);
		gpDetails.setVgap(10);
		gpDetails.setHgap(20);
		int column = 0;
		int row = 0;
		gpDetails.add(spImage, column++, row);
		GridPane gpNameHeight = new GridPane();
		gpNameHeight.setVgap(10);
		gpNameHeight.setHgap(10);
		gpNameHeight.add(lblName, 0, 0);
		gpNameHeight.add(fullTxtName, 1, 0);
		gpNameHeight.add(lblHeight, 0, 1);
		gpNameHeight.add(numtxtHeight, 1, 1);
		gpDetails.add(gpNameHeight, column++, row);
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
		gpRelativePosition.add(lblR, 7, 0);
		gpRelativePosition.add(numtxtR, 8, 0);
		gpRelativePosition.setAlignment(Pos.CENTER_LEFT);
		gpDetails.add(gpRelativePosition, column++, row, 2, 1);
		column = 0; row++;
		HBox hboxSmoothTo = new HBox();
		hboxSmoothTo.setAlignment(Pos.CENTER_LEFT);
		hboxSmoothTo.setSpacing(10);
		hboxSmoothTo.getChildren().addAll(lblSmoothTo, lblSmoothToX, numtxtSmoothToX, lblSmoothToY, numtxtSmoothToY, lblSmoothToZ, numtxtSmoothToZ);
		gpDetails.add(hboxSmoothTo, column++, row, 2, 1);
		column = 0; row++;
		HBox hboxSmoothFrom = new HBox();
		hboxSmoothFrom.setAlignment(Pos.CENTER_LEFT);
		hboxSmoothFrom.setSpacing(10);
		hboxSmoothFrom.getChildren().addAll(lblSmoothFrom, lblSmoothFromX, numtxtSmoothFromX, lblSmoothFromY, numtxtSmoothFromY, lblSmoothFromZ, numtxtSmoothFromZ);
		gpDetails.add(hboxSmoothFrom, column++, row, 2, 1);
		column = 0; row++;
		gpDetails.add(vboxButtons, column++, row, 2, 1);
		gpDetails.setVisible(false);
		GridPane.setHalignment(vboxButtons, HPos.CENTER);
		gpDetails.setAlignment(Pos.TOP_LEFT);
		
		getContents().add(vboxSelectClamping, 0, 0);
		getContents().add(gpDetails, 0, 1);
		GridPane.setMargin(gpDetails, new Insets(10, 0, 0, 0));
		GridPane.setValignment(spImage, VPos.TOP);
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtName.setFocusListener(listener);
		numtxtHeight.setFocusListener(listener);
		numtxtX.setFocusListener(listener);
		numtxtY.setFocusListener(listener);
		numtxtZ.setFocusListener(listener);
		numtxtR.setFocusListener(listener);
		numtxtSmoothToX.setFocusListener(listener);
		numtxtSmoothToY.setFocusListener(listener);
		numtxtSmoothToZ.setFocusListener(listener);
		numtxtSmoothFromX.setFocusListener(listener);
		numtxtSmoothFromY.setFocusListener(listener);
		numtxtSmoothFromZ.setFocusListener(listener);
	}

	public void setDeviceManager(final DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	
	@Override
	public void refresh() {
		ifsClampings.clearItems();
		int itemIndex = 0;
		for (AbstractCNCMachine machine : deviceManager.getCNCMachines()) {
			for (WorkArea workArea : machine.getWorkAreas()) {
				for (final Clamping clamping : workArea.getClampings()) {
					ifsClampings.addItem(itemIndex, clamping.getName(), clamping.getImageUrl(), new EventHandler<MouseEvent>(){
						@Override
						public void handle(final MouseEvent arg0) {
							getPresenter().selectedClamping(clamping);
						}
					});
					itemIndex++;
				}
			}
		}
		reset();
	}
	
	public void showFormEdit() {
		gpDetails.setVisible(true);
		btnNew.setDisable(true);
		btnDelete.setVisible(true);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void showFormNew() {
		gpDetails.setVisible(true);
		btnEdit.setDisable(true);
		btnDelete.setVisible(false);
		btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void reset() {
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
		numtxtR.setText("");
		numtxtSmoothToX.setText("");
		numtxtSmoothToY.setText("");
		numtxtSmoothToZ.setText("");
		numtxtSmoothFromX.setText("");
		numtxtSmoothFromY.setText("");
		numtxtSmoothFromZ.setText("");
		imagePath = null;
		imageVw.setImage(null);
		validate();
		gpDetails.setVisible(false);
	}
	
	public void clampingSelected(final Clamping clamping) {
		ifsClampings.setSelected(clamping.getName());
		btnEdit.setDisable(false);
		fullTxtName.setText(clamping.getName());
		numtxtHeight.setText("" + clamping.getHeight());
		numtxtX.setText("" + clamping.getRelativePosition().getX());
		numtxtY.setText("" + clamping.getRelativePosition().getY());
		numtxtZ.setText("" + clamping.getRelativePosition().getZ());
		numtxtR.setText("" + clamping.getRelativePosition().getR());
		numtxtSmoothToX.setText("" + clamping.getSmoothToPoint().getX());
		numtxtSmoothToY.setText("" + clamping.getSmoothToPoint().getY());
		numtxtSmoothToZ.setText("" + clamping.getSmoothToPoint().getZ());
		numtxtSmoothFromX.setText("" + clamping.getSmoothFromPoint().getX());
		numtxtSmoothFromY.setText("" + clamping.getSmoothFromPoint().getY());
		numtxtSmoothFromZ.setText("" + clamping.getSmoothFromPoint().getZ());
		String url = clamping.getImageUrl();
		if (url != null) {
			url = url.replace("file:///", "");
		}
		if ((url != null) && ((new File(url)).exists() || getClass().getClassLoader().getResource(url) != null)) {
			imageVw.setImage(new Image(clamping.getImageUrl(), IMG_WIDTH, IMG_HEIGHT, true, true));
		} else {
			imageVw.setImage(new Image(UIConstants.IMG_NOT_FOUND_URL, IMG_WIDTH, IMG_HEIGHT, true, true));
		}
		imagePath = clamping.getImageUrl();
	}

	public void validate() {
		if (!fullTxtName.getText().equals("") 
				&& !numtxtHeight.getText().equals("") 
				&& !numtxtX.getText().equals("")
				&& !numtxtY.getText().equals("")
				&& !numtxtZ.getText().equals("")
				&& !numtxtR.getText().equals("")
				&& !numtxtSmoothToX.getText().equals("")
				&& !numtxtSmoothToY.getText().equals("")
				&& !numtxtSmoothToZ.getText().equals("")
				&& !numtxtSmoothFromX.getText().equals("")
				&& !numtxtSmoothFromY.getText().equals("")
				&& !numtxtSmoothFromZ.getText().equals("")
				&& (imagePath != null) && !imagePath.equals("")) {
			btnSave.setDisable(false);
		} else {
			btnSave.setDisable(true);
		}
	}
	
}
