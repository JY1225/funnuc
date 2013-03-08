package eu.robojob.irscw.ui.admin.device;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class CNCMachineClampingsView extends AbstractFormView<CNCMachineClampingsPresenter> {

	private ComboBox<String> cbbClampings;
	private Button btnEdit;
	private Button btnNew;
	private GridPane gpDetails;
	private Label lblName;
	private FullTextField fullTxtName;
	private Label lblHeight;
	private NumericTextField numtxtHeight;
	private Label lblX;
	private NumericTextField numtxtX;
	private Label lblY;
	private NumericTextField numtxtY;
	private Label lblZ;
	private NumericTextField numtxtZ;
	private Label lblR;
	private NumericTextField numtxtR;
	private Button btnSave;
	
	private static final String EDIT = "CNCMachineClampingsView.edit";
	private static final String NEW = "CNCMachineClampingsView.new";
	private static final String NAME = "CNCMachineClampingsView.name";
	private static final String HEIGHT = "CNCMachineClampingsView.height";
	private static final String SAVE = "CNCMacineClampingsView.save";
	
	private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";
	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	
	private ObservableList<String> clampingNames;
	
	public CNCMachineClampingsView() {
		clampingNames = FXCollections.observableArrayList();
		build();
	}
	
	@Override
	protected void build() {
		setVgap(15);
		setHgap(15);
		setAlignment(Pos.TOP_CENTER);
		setPadding(new Insets(50, 0, 0, 0));
		HBox hboxSelectClamping = new HBox();
		hboxSelectClamping.setAlignment(Pos.CENTER);
		cbbClampings = new ComboBox<String>();
		cbbClampings.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbClampings.setMinSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbClampings.setMaxSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbClampings.setItems(clampingNames);
		cbbClampings.valueProperty().addListener(new ChangeListener<String>() {
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
				getPresenter().clickedEdit(cbbClampings.valueProperty().get());
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
		hboxSelectClamping.setSpacing(15);
		hboxSelectClamping.getChildren().addAll(cbbClampings, hboxButtons);
		
		lblName = new Label(Translator.getTranslation(NAME));
		fullTxtName = new FullTextField(100);
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
		
		btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, null);

		gpDetails = new GridPane();
		gpDetails.setAlignment(Pos.CENTER);
		gpDetails.setVgap(15);
		gpDetails.setHgap(15);
		int column = 0;
		int row = 0;
		gpDetails.add(lblName, column++, row);
		gpDetails.add(fullTxtName, column++, row);
		column = 0; row++;
		gpDetails.add(lblHeight, column++, row);
		gpDetails.add(numtxtHeight, column++, row);
		column = 0; row++;
		gpDetails.add(lblX, column++, row);
		gpDetails.add(numtxtX, column++, row);
		column = 0; row++;
		gpDetails.add(lblY, column++, row);
		gpDetails.add(numtxtY, column++, row);
		column = 0; row++;
		gpDetails.add(lblZ, column++, row);
		gpDetails.add(numtxtZ, column++, row);
		column = 0; row++;
		gpDetails.add(lblR, column++, row);
		gpDetails.add(numtxtR, column++, row);
		column = 0; row++;
		gpDetails.add(btnSave, column++, row, 2, 1);
		gpDetails.setVisible(false);
		GridPane.setHalignment(btnSave, HPos.CENTER);
		
		add(hboxSelectClamping, 0, 0);
		add(gpDetails, 0, 1);
		GridPane.setMargin(gpDetails, new Insets(25, 0, 0, 0));
	}
	
	public void setClampingNames(final Set<String> clampingNames) {
		this.clampingNames.clear();
		this.clampingNames.addAll(clampingNames);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtName.setFocusListener(listener);
		numtxtHeight.setFocusListener(listener);
		numtxtX.setFocusListener(listener);
		numtxtY.setFocusListener(listener);
		numtxtZ.setFocusListener(listener);
		numtxtR.setFocusListener(listener);
	}

	@Override
	public void refresh() {
	}
	
	public void showFormEdit() {
		gpDetails.setVisible(true);
		btnNew.setDisable(true);
		cbbClampings.setDisable(true);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void showFormNew() {
		gpDetails.setVisible(true);
		btnEdit.setDisable(true);
		cbbClampings.setDisable(true);
		btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void reset() {
		fullTxtName.setText("");
		numtxtHeight.setText("");
		btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		if (cbbClampings.valueProperty().get() != null) {
			btnEdit.setDisable(false);
		} else {
			btnEdit.setDisable(true);
		}
		cbbClampings.setDisable(false);
		btnNew.setDisable(false);
		numtxtX.setText("");
		numtxtY.setText("");
		numtxtZ.setText("");
		numtxtR.setText("");
		validate();
		gpDetails.setVisible(false);
	}
	
	public void clampingSelected(final Clamping clamping) {
		fullTxtName.setText(clamping.getName());
		numtxtHeight.setText("" + clamping.getHeight());
		numtxtX.setText("" + clamping.getRelativePosition().getX());
		numtxtY.setText("" + clamping.getRelativePosition().getY());
		numtxtZ.setText("" + clamping.getRelativePosition().getZ());
		numtxtR.setText("" + clamping.getRelativePosition().getR());
	}

	public void validate() {
		if (!fullTxtName.getText().equals("") 
				&& !numtxtHeight.getText().equals("") 
				&& !numtxtX.getText().equals("")
				&& !numtxtY.getText().equals("")
				&& !numtxtZ.getText().equals("")
				&& !numtxtR.getText().equals("")) {
			btnSave.setDisable(false);
		} else {
			btnSave.setDisable(true);
		}
	}
	
}
