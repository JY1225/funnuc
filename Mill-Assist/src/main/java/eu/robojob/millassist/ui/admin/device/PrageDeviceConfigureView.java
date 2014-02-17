package eu.robojob.millassist.ui.admin.device;

import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.Clamping.Type;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class PrageDeviceConfigureView extends AbstractFormView<PrageDeviceConfigurePresenter> {

	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblType;
	private ComboBox<String> cbbType;
	private Label lblUserFrame;
	private ComboBox<String> cbbUserFrame;
	private Label lblRelativePosition;
	private Label lblX;
	private NumericTextField numtxtX;
	private Label lblY;
	private NumericTextField numtxtY;
	private Label lblZ;
	private NumericTextField numtxtZ;
	private Label lblR;
	private NumericTextField numtxtR;
	private Label lblSmoothTo;
	private Label lblSmoothToX;
	private NumericTextField numtxtSmoothToX;
	private Label lblSmoothToY;
	private NumericTextField numtxtSmoothToY;
	private Label lblSmoothToZ;
	private NumericTextField numtxtSmoothToZ;
	private Label lblSmoothFrom;
	private Label lblSmoothFromX;
	private NumericTextField numtxtSmoothFromX;
	private Label lblSmoothFromY;
	private NumericTextField numtxtSmoothFromY;
	private Label lblSmoothFromZ;
	private NumericTextField numtxtSmoothFromZ;
	private Label lblClampingWidthR;
	private Button btnClampingWidthROffsetm90;
	private Button btnClampingWidthROffsetp90;
	
	private int clampingWidthROffset;
	
	private Button btnSave;
	
	private static final String NAME = "PrageDeviceConfigureView.name";
	private static final String USERFRAME = "PrageDeviceConfigureView.userFrame";
	private static final String SAVE = "PrageDeviceConfigureView.save";
	private static final String RELATIVE_POSITION = "PrageDeviceConfigureView.relativePosition";
	private static final String SMOOTH_TO = "PrageDeviceConfigureView.smoothTo";
	private static final String SMOOTH_FROM = "PrageDeviceConfigureView.smoothFrom";
	private static final String CLAMPING_WIDTH_R = "PrageDeviceConfigureView.clampingWidthR";
	private static final String TYPE = "PrageDeviceConfigureView.type";
	
	private static final String CLAMPING_TYPE_CENTRUM = "Centrum";
	private static final String CLAMPING_TYPE_FIXED_XP = "Fix X +";
	private static final String CLAMPING_TYPE_FIXED_XM = "Fix X -";
	private static final String CLAMPING_TYPE_FIXED_YP = "Fix Y +";
	private static final String CLAMPING_TYPE_FIXED_YM = "Fix Y -";
	
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";

	private ObservableList<String> userFrameNames;
	private PrageDevice prageDevice;
	
	@Override
	protected void build() {
		this.clampingWidthROffset = 0;
		getContents().setAlignment(Pos.TOP_CENTER);
		getContents().setPadding(new Insets(50, 0, 0, 0));
		getContents().setVgap(15);
		getContents().setHgap(15);
		lblName = new Label(Translator.getTranslation(NAME));
		fulltxtName = new FullTextField(100);
		fulltxtName.setPrefWidth(250);
		fulltxtName.setMaxWidth(250);
		fulltxtName.setMinWidth(250);
		lblType = new Label(Translator.getTranslation(TYPE));
		cbbType = new ComboBox<String>();
		cbbType.setPrefSize(100, UIConstants.COMBO_HEIGHT);
		cbbType.setMinSize(100, UIConstants.COMBO_HEIGHT);
		cbbType.getItems().add(CLAMPING_TYPE_CENTRUM);
		cbbType.getItems().add(CLAMPING_TYPE_FIXED_XM);
		cbbType.getItems().add(CLAMPING_TYPE_FIXED_XP);
		cbbType.getItems().add(CLAMPING_TYPE_FIXED_YM);
		cbbType.getItems().add(CLAMPING_TYPE_FIXED_YP);
		lblUserFrame = new Label (Translator.getTranslation(USERFRAME));
		cbbUserFrame = new ComboBox<String>();
		cbbUserFrame.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUserFrame.setDisable(true);
		userFrameNames = FXCollections.observableArrayList();
		cbbUserFrame.setItems(userFrameNames);
		lblRelativePosition = new Label(Translator.getTranslation(RELATIVE_POSITION));
		lblX = new Label("X");
		numtxtX = new NumericTextField(6);
		lblY = new Label("Y");
		numtxtY = new NumericTextField(6);
		lblZ = new Label("Z");
		numtxtZ = new NumericTextField(6);
		lblR = new Label("R");
		numtxtR = new NumericTextField(6);
		lblSmoothTo = new Label(Translator.getTranslation(SMOOTH_TO));
		lblSmoothToX = new Label("X");
		numtxtSmoothToX = new NumericTextField(6);
		lblSmoothToY = new Label("Y");
		numtxtSmoothToY = new NumericTextField(6);
		lblSmoothToZ = new Label("Z");
		numtxtSmoothToZ = new NumericTextField(6);
		lblSmoothFrom = new Label(Translator.getTranslation(SMOOTH_FROM));
		lblSmoothFromX = new Label("X");
		numtxtSmoothFromX = new NumericTextField(6);
		lblSmoothFromY = new Label("Y");
		numtxtSmoothFromY = new NumericTextField(6);
		lblSmoothFromZ = new Label("Z");
		numtxtSmoothFromZ = new NumericTextField(6);
		lblClampingWidthR = new Label(Translator.getTranslation(CLAMPING_WIDTH_R));
		btnClampingWidthROffsetm90 = createButton("-90°", UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				clampingWidthROffset = -90;
				btnClampingWidthROffsetm90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
				btnClampingWidthROffsetp90.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
			}
		});
		btnClampingWidthROffsetm90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnClampingWidthROffsetp90 = createButton("+90°", UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				clampingWidthROffset = 90;
				btnClampingWidthROffsetm90.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
				btnClampingWidthROffsetp90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			}
		});
		btnClampingWidthROffsetp90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		
		btnSave = createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
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
				getPresenter().saveData(fulltxtName.getText(), type, Float.parseFloat(numtxtX.getText()), Float.parseFloat(numtxtY.getText()),
						Float.parseFloat(numtxtZ.getText()), Float.parseFloat(numtxtR.getText()), Float.parseFloat(numtxtSmoothToX.getText()),
						Float.parseFloat(numtxtSmoothToY.getText()), Float.parseFloat(numtxtSmoothToZ.getText()), Float.parseFloat(numtxtSmoothFromX.getText()), 
						Float.parseFloat(numtxtSmoothFromY.getText()), Float.parseFloat(numtxtSmoothFromZ.getText()), clampingWidthROffset);
			}
		});
		int row = 0;
		int column = 0;
		getContents().add(lblName, column++, row);
		getContents().add(fulltxtName, column++, row);
		column = 0; row++;
		getContents().add(lblType, column++, row);
		getContents().add(cbbType, column++, row);
		column = 0; row++;
		getContents().add(lblUserFrame, column++, row);
		getContents().add(cbbUserFrame, column++, row);
		
		column = 0; row++;
		HBox hbox = new HBox();
		HBox hboxControls = new HBox();
		hboxControls.setAlignment(Pos.CENTER_LEFT);
		hboxControls.setSpacing(0);
		hboxControls.getChildren().addAll(btnClampingWidthROffsetm90, btnClampingWidthROffsetp90);
		hbox.getChildren().addAll(lblClampingWidthR, hboxControls);
		hbox.setSpacing(9);
		hbox.setAlignment(Pos.CENTER_LEFT);

		
		int column2 = 0;
		int row2 = 0;
		GridPane gp = new GridPane();
		gp.setVgap(15);
		gp.setHgap(10);
		gp.add(lblRelativePosition, column2++, row2);
		gp.add(lblX, column2++, row2);
		gp.add(numtxtX, column2++, row2);
		gp.add(lblY, column2++, row2);
		gp.add(numtxtY, column2++, row2);
		gp.add(lblZ, column2++, row2);
		gp.add(numtxtZ, column2++, row2);
		gp.add(lblR, column2++, row2);
		gp.add(numtxtR, column2++, row2);
		column2 = 0; row2++;
		gp.add(lblSmoothTo, column2++, row2);
		gp.add(lblSmoothToX, column2++, row2);
		gp.add(numtxtSmoothToX, column2++, row2);
		gp.add(lblSmoothToY, column2++, row2);
		gp.add(numtxtSmoothToY, column2++, row2);
		gp.add(lblSmoothToZ, column2++, row2);
		gp.add(numtxtSmoothToZ, column2++, row2);
		column2 = 0; row2++;
		gp.add(lblSmoothFrom, column2++, row2);
		gp.add(lblSmoothFromX, column2++, row2);
		gp.add(numtxtSmoothFromX, column2++, row2);
		gp.add(lblSmoothFromY, column2++, row2);
		gp.add(numtxtSmoothFromY, column2++, row2);
		gp.add(lblSmoothFromZ, column2++, row2);
		gp.add(numtxtSmoothFromZ, column2++, row2);
		getContents().add(gp, column++, row, 2, 1);
		column = 0; row++;

		getContents().add(hbox, column++, row, 2, 1);
				gp.setAlignment(Pos.CENTER);
		GridPane.setHalignment(gp, HPos.CENTER);
		
		column = 0; row++;
		getContents().add(btnSave, column++, row, 2, 1);
		GridPane.setHalignment(btnSave, HPos.CENTER);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
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
	
	public void setPrageDevice(final PrageDevice prageDevice) {
		this.prageDevice = prageDevice;
	}
	
	public void setUserFrameNames(final Set<String> userFrameNames) {
		this.userFrameNames.clear();
		this.userFrameNames.addAll(userFrameNames);
	}

	@Override
	public void refresh() {
		getPresenter().updateUserFrames();
		if (prageDevice != null) {
			fulltxtName.setText(prageDevice.getName());
			cbbUserFrame.valueProperty().set(prageDevice.getWorkAreas().get(0).getUserFrame().getName());
			if (prageDevice.getWorkAreas().get(0).getActiveClamping() != null) {
				Coordinates relClampingPosition = prageDevice.getWorkAreas().get(0).getActiveClamping().getRelativePosition();
				numtxtX.setText("" + relClampingPosition.getX());
				numtxtY.setText("" + relClampingPosition.getY());
				numtxtZ.setText("" + relClampingPosition.getZ());
				numtxtR.setText("" + relClampingPosition.getR());
				Coordinates smoothTo = prageDevice.getWorkAreas().get(0).getActiveClamping().getSmoothToPoint();
				numtxtSmoothToX.setText("" + smoothTo.getX());
				numtxtSmoothToY.setText("" + smoothTo.getY());
				numtxtSmoothToZ.setText("" + smoothTo.getZ());
				Coordinates smoothFrom = prageDevice.getWorkAreas().get(0).getActiveClamping().getSmoothFromPoint();
				numtxtSmoothFromX.setText("" + smoothFrom.getX());
				numtxtSmoothFromY.setText("" + smoothFrom.getY());
				numtxtSmoothFromZ.setText("" + smoothFrom.getZ());
				Clamping clamping = prageDevice.getWorkAreas().get(0).getActiveClamping();
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
				} else {
					System.out.println("PROBLEM: " + clamping.getType());
				}
			}
			btnClampingWidthROffsetm90.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
			btnClampingWidthROffsetp90.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
			if (prageDevice.getClampingWidthDeltaR() == 90) {
				btnClampingWidthROffsetp90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
				clampingWidthROffset = 90;
			} else if (prageDevice.getClampingWidthDeltaR() == -90) {
				btnClampingWidthROffsetm90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
				clampingWidthROffset = -90;
			} else {
				throw new IllegalStateException("Clamping width delta r should be +/-90°");
			}
		}
	}

}
