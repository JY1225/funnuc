package eu.robojob.millassist.ui.admin.device;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class UserFramesConfigureView extends AbstractFormView<UserFramesConfigurePresenter> {

	private HBox hboxSelectUf;
	private ComboBox<String> cbbUfs;
	private Button btnEdit;
	private Button btnNew;
	
	private VBox vboxForm;
	private HBox hboxName;
	private Label lblName;
	private FullTextField fulltxtName;
	private HBox hboxDetails;
	private Label lblNumber;
	private IntegerTextField itxtNumber;
	private Label lblZSafe;
	private NumericTextField numtxtZSafe;
	private HBox hboxCoordinates1;
	private Label lblX;
	private NumericTextField numTxtX;
	private Label lblY;
	private NumericTextField numTxtY;
	private Label lblZ;
	private NumericTextField numTxtZ;
	private HBox hboxCoordinates2;
	private Label lblW;
	private NumericTextField numTxtW;
	private Label lblP;
	private NumericTextField numTxtP;
	private Label lblR;
	private NumericTextField numTxtR;
	
	private Button btnSave;
	
	private static final String EDIT = "UserFramesConfigureView.edit";
	private static final String NEW = "UserFramesConfigureView.new";
	private static final String NAME = "UserFramesConfigureView.name";
	private static final String NUMBER = "UserFramesConfigureView.number";
	private static final String ZSAFE = "UserFramesConfigureView.zSafe";
	private static final String SAVE = "UserFramesConfigureView.save";

	private static final double LABEL_WIDTH = 55;
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	
	private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";
	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	
	private ObservableList<String> userFrameNames;
	
	public UserFramesConfigureView() {
		this.userFrameNames = FXCollections.observableArrayList();
	}
	
	@Override
	protected void build() {
		getContents().setVgap(15);
		getContents().setHgap(15);
		getContents().setPadding(new Insets(50, 0, 0, 0));
		getContents().setAlignment(Pos.TOP_CENTER);
		hboxSelectUf = new HBox();
		hboxSelectUf.setSpacing(15);
		hboxSelectUf.setAlignment(Pos.CENTER_LEFT);
		cbbUfs = new ComboBox<String>();
		cbbUfs.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUfs.setMinSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUfs.setMaxSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUfs.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {
				if (newValue != null) {
					btnEdit.setDisable(false);
				} else {
					btnEdit.setDisable(true);
				}
			}
		});
		cbbUfs.setItems(userFrameNames);

		HBox hboxButtons = new HBox();
		btnEdit = createButton(EDIT_PATH, null, Translator.getTranslation(EDIT), BTN_WIDTH, BTN_HEIGHT, null);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnEdit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().clickedEdit(cbbUfs.valueProperty().get());
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
		hboxSelectUf.getChildren().addAll(cbbUfs, hboxButtons);
		
		vboxForm = new VBox();
		vboxForm.setSpacing(15);
		
		hboxName = new HBox();
		hboxName.setSpacing(15);
		hboxName.setAlignment(Pos.CENTER_LEFT);
		lblName = new Label(Translator.getTranslation(NAME));
		lblName.setPrefWidth(LABEL_WIDTH);
		fulltxtName = new FullTextField(100);
		fulltxtName.setPrefWidth(285);
		fulltxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
				validate();
			}
		});
		hboxName.getChildren().addAll(lblName, fulltxtName);
		hboxName.setPrefWidth(355);
		hboxName.setMaxWidth(355);
		
		hboxDetails = new HBox();
		hboxDetails.setSpacing(15);
		hboxDetails.setAlignment(Pos.CENTER_LEFT);
		lblNumber = new Label(Translator.getTranslation(NUMBER));
		lblNumber.setPrefWidth(LABEL_WIDTH);
		itxtNumber = new IntegerTextField(5);
		itxtNumber.setPrefWidth(75);
		itxtNumber.setMinWidth(75);
		itxtNumber.setMaxWidth(75);
		itxtNumber.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(final ObservableValue<? extends Integer> arg0, final Integer arg1, final Integer arg2) {
				validate();
			}
		});
		lblZSafe = new Label(Translator.getTranslation(ZSAFE));
		lblZSafe.setPrefWidth(LABEL_WIDTH);
		HBox.setMargin(lblZSafe, new Insets(0, 0, 0, 50));
		numtxtZSafe = new NumericTextField(6);
		numtxtZSafe.setPrefWidth(75);
		numtxtZSafe.setMinWidth(75);
		numtxtZSafe.setMaxWidth(75);
		numtxtZSafe.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxDetails.getChildren().addAll(lblNumber, itxtNumber, lblZSafe, numtxtZSafe);
		hboxDetails.setPrefWidth(355);
		hboxDetails.setMaxWidth(355);
		
		hboxCoordinates1 = new HBox();
		hboxCoordinates1.setSpacing(12);
		hboxCoordinates1.setAlignment(Pos.CENTER_LEFT);
		lblX = new Label("X");
		lblX.setPrefWidth(20);
		numTxtX = new NumericTextField(6);
		numTxtX.setPrefWidth(75);
		numTxtX.setMinWidth(75);
		numTxtX.setMaxWidth(75);
		numTxtX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblY = new Label("Y");
		lblY.setPrefWidth(20);
		HBox.setMargin(lblY, new Insets(0, 0, 0, 5));
		numTxtY = new NumericTextField(6);
		numTxtY.setPrefWidth(75);
		numTxtY.setMinWidth(75);
		numTxtY.setMaxWidth(75);
		numTxtY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblZ = new Label("Z");
		lblZ.setPrefWidth(20);
		HBox.setMargin(lblZ, new Insets(0, 0, 0, 5));
		numTxtZ = new NumericTextField(6);
		numTxtZ.setPrefWidth(75);
		numTxtZ.setMinWidth(75);
		numTxtZ.setMaxWidth(75);
		numTxtZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxCoordinates1.getChildren().addAll(lblX, numTxtX, lblY, numTxtY, lblZ, numTxtZ);
		hboxCoordinates1.setPrefWidth(355);
		hboxCoordinates1.setMaxWidth(355);
		
		hboxCoordinates2 = new HBox();
		hboxCoordinates2.setSpacing(12);
		hboxCoordinates2.setAlignment(Pos.CENTER_LEFT);
		lblW = new Label("W");
		lblW.setPrefWidth(20);
		numTxtW = new NumericTextField(6);
		numTxtW.setPrefWidth(75);
		numTxtW.setMinWidth(75);
		numTxtW.setMaxWidth(75);
		numTxtW.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblP = new Label("P");
		lblP.setPrefWidth(20);
		HBox.setMargin(lblP, new Insets(0, 0, 0, 5));
		numTxtP = new NumericTextField(6);
		numTxtP.setPrefWidth(75);
		numTxtP.setMinWidth(75);
		numTxtP.setMaxWidth(75);
		numTxtP.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblR = new Label("R");
		lblR.setPrefWidth(20);
		HBox.setMargin(lblR, new Insets(0, 0, 0, 5));
		numTxtR = new NumericTextField(6);
		numTxtR.setPrefWidth(75);
		numTxtR.setMinWidth(75);
		numTxtR.setMaxWidth(75);
		numTxtR.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		hboxCoordinates2.getChildren().addAll(lblW, numTxtW, lblP, numTxtP, lblR, numTxtR);
		hboxCoordinates2.setPrefWidth(355);
		hboxCoordinates2.setMaxWidth(355);
		
		btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().saveData(fulltxtName.getText(), Integer.parseInt(itxtNumber.getText()), Float.parseFloat(numtxtZSafe.getText()), 
						Float.parseFloat(numTxtX.getText()), Float.parseFloat(numTxtY.getText()), Float.parseFloat(numTxtZ.getText()),
						Float.parseFloat(numTxtW.getText()), Float.parseFloat(numTxtP.getText()), Float.parseFloat(numTxtR.getText()));
			}
		});
		
		vboxForm.getChildren().addAll(hboxName, hboxDetails, hboxCoordinates1, hboxCoordinates2, btnSave);
		VBox.setMargin(btnSave, new Insets(15, 0, 0, 0));
		
		vboxForm.setPrefWidth(355);
		setMargin(vboxForm, new Insets(50, 0, 0, 0));
		vboxForm.setAlignment(Pos.CENTER);
		vboxForm.setVisible(false);
		
		getContents().	add(hboxSelectUf, 0, 0);
		getContents().	add(vboxForm, 0, 1);
		
		refresh();
		
	}
	
	public void setUserFrames(final List<String> userFrameNames) {
		this.userFrameNames.clear();
		this.userFrameNames.addAll(userFrameNames);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		itxtNumber.setFocusListener(listener);
		numtxtZSafe.setFocusListener(listener);
		numTxtX.setFocusListener(listener);
		numTxtY.setFocusListener(listener);
		numTxtZ.setFocusListener(listener);
		numTxtW.setFocusListener(listener);
		numTxtP.setFocusListener(listener);
		numTxtR.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		reset();
		getPresenter().updateUserFrames();
	}
	
	public void userFrameSelected(final UserFrame userFrame) {
		fulltxtName.setText(userFrame.getName());
		itxtNumber.setText("" + userFrame.getNumber());
		numtxtZSafe.setText("" + userFrame.getzSafeDistance());
		numTxtX.setText("" + userFrame.getLocation().getX());
		numTxtY.setText("" + userFrame.getLocation().getY());
		numTxtZ.setText("" + userFrame.getLocation().getZ());
		numTxtW.setText("" + userFrame.getLocation().getW());
		numTxtP.setText("" + userFrame.getLocation().getP());
		numTxtR.setText("" + userFrame.getLocation().getR());
	}
	
	public void showFormEdit() {
		vboxForm.setVisible(true);
		btnNew.setDisable(true);
		cbbUfs.setDisable(true);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void showFormNew() {
		vboxForm.setVisible(true);
		btnEdit.setDisable(true);
		cbbUfs.setDisable(true);
		btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void reset() {
		fulltxtName.setText("");
		itxtNumber.setText("");
		numtxtZSafe.setText("");
		btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		if (cbbUfs.valueProperty().get() != null) {
			btnEdit.setDisable(false);
		} else {
			btnEdit.setDisable(true);
		}
		cbbUfs.setDisable(false);
		btnNew.setDisable(false);
		numTxtX.setText("");
		numTxtY.setText("");
		numTxtZ.setText("");
		numTxtW.setText("");
		numTxtP.setText("");
		numTxtR.setText("");
		validate();
		vboxForm.setVisible(false);
	}
	
	public void validate() {
		if (!fulltxtName.getText().equals("") 
				&& !itxtNumber.getText().equals("") && (Integer.parseInt(itxtNumber.getText()) > 0) 
				&& !numtxtZSafe.getText().equals("")
				&& !numTxtX.getText().equals("")
				&& !numTxtY.getText().equals("")
				&& !numTxtZ.getText().equals("")
				&& !numTxtW.getText().equals("") 
				&& !numTxtP.getText().equals("")
				&& !numTxtR.getText().equals("")) {
			btnSave.setDisable(false);
		} else {
			btnSave.setDisable(true);
		}
	}

}
