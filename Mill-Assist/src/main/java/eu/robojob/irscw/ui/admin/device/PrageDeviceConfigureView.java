package eu.robojob.irscw.ui.admin.device;

import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class PrageDeviceConfigureView extends AbstractFormView<PrageDeviceConfigurePresenter> {

	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblUserFrame;
	private ComboBox<String> cbbUserFrame;
	private Label lblX;
	private NumericTextField numtxtX;
	private Label lblY;
	private NumericTextField numtxtY;
	private Label lblZ;
	private NumericTextField numtxtZ;
	private Label lblR;
	private NumericTextField numtxtR;
	private Button btnSave;
	
	private static final String NAME = "PrageDeviceConfigureView.name";
	private static final String USERFRAME = "PrageDeviceConfigureView.userFrame";
	private static final String SAVE = "PrageDeviceConfigureView.save";
	
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";

	private ObservableList<String> userFrameNames;
	private PrageDevice prageDevice;
	
	@Override
	protected void build() {
		setAlignment(Pos.TOP_CENTER);
		setPadding(new Insets(50, 0, 0, 0));
		setVgap(15);
		setHgap(15);
		lblName = new Label(Translator.getTranslation(NAME));
		fulltxtName = new FullTextField(100);
		lblUserFrame = new Label (Translator.getTranslation(USERFRAME));
		cbbUserFrame = new ComboBox<String>();
		cbbUserFrame.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		userFrameNames = FXCollections.observableArrayList();
		cbbUserFrame.setItems(userFrameNames);
		lblX = new Label("X");
		numtxtX = new NumericTextField(6);
		lblY = new Label("Y");
		numtxtY = new NumericTextField(6);
		lblZ = new Label("Z");
		numtxtZ = new NumericTextField(6);
		lblR = new Label("R");
		numtxtR = new NumericTextField(6);
		btnSave = createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, null);
		int row = 0;
		int column = 0;
		add(lblName, column++, row);
		add(fulltxtName, column++, row);
		column = 0; row++;
		add(lblUserFrame, column++, row);
		add(cbbUserFrame, column++, row);
		column = 0; row++;
		
		int column2 = 0;
		int row2 = 0;
		GridPane gp = new GridPane();
		gp.setVgap(15);
		gp.setHgap(15);
		gp.add(lblX, column2++, row2);
		gp.add(numtxtX, column2++, row2);
		column2 = 0; row2++;
		gp.add(lblY, column2++, row2);
		gp.add(numtxtY, column2++, row2);
		column2 = 0; row2++;
		gp.add(lblZ, column2++, row2);
		gp.add(numtxtZ, column2++, row2);
		column2 = 0; row2++;
		gp.add(lblR, column2++, row2);
		gp.add(numtxtR, column2++, row2);
		add(gp, column++, row, 2, 1);
		gp.setAlignment(Pos.CENTER);
		GridPane.setHalignment(gp, HPos.CENTER);
		
		column = 0; row++;
		add(btnSave, column++, row, 2, 1);
		GridPane.setHalignment(btnSave, HPos.CENTER);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		numtxtX.setFocusListener(listener);
		numtxtY.setFocusListener(listener);
		numtxtZ.setFocusListener(listener);
		numtxtR.setFocusListener(listener);
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
		fulltxtName.setText(prageDevice.getName());
		cbbUserFrame.valueProperty().set(prageDevice.getWorkAreas().get(0).getUserFrame().getName());
		if (prageDevice.getWorkAreas().get(0).getActiveClamping() != null) {
			Coordinates relClampingPosition = prageDevice.getWorkAreas().get(0).getActiveClamping().getRelativePosition();
			numtxtX.setText("" + relClampingPosition.getX());
			numtxtY.setText("" + relClampingPosition.getY());
			numtxtZ.setText("" + relClampingPosition.getZ());
			numtxtR.setText("" + relClampingPosition.getR());
		}
	}

}
