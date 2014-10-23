package eu.robojob.millassist.ui.admin.device;

import java.util.List;

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
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class ReversalUnitConfigureView extends AbstractFormView<ReversalUnitConfigurePresenter> {

	private static final String NAME = "ReversalUnitConfigureView.name";
	private static final String USERFRAME = "ReversalUnitConfigureView.userframe";
	private static final String POSITION = "ReversalUnitConfigureView.position";
	private static final String SMOOTH_TO = "ReversalUnitConfigureView.smoothTo";
	private static final String SMOOTH_FROM = "ReversalUnitConfigureView.smoothFrom";
	private static final String SAVE = "ReversalUnitConfigureView.save";
	private static final String STATION_HEIGHT = "ReversalUnitConfigureView.stationHeight";
	
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	
	private ReversalUnit reversalUnit;
	
	private Label lblName;
	private FullTextField fullTxtName;
	
	private Label lblUserframe;
	private ComboBox<String> cbbUserFrame;
	
	private Label lblPosition;
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
	
	private Label lblStationHeight;
	private NumericTextField numtxtStationHeight;
	
	private ObservableList<String> userFrameNames;
	
	private Button btnSave;
	
	public ReversalUnitConfigureView() {
		userFrameNames = FXCollections.observableArrayList();
	}
	
	public void setReversalUnit(final ReversalUnit reversalUnit) {
		this.reversalUnit = reversalUnit;
	}
	
	@Override
	protected void build() {
		getContents().setAlignment(Pos.TOP_CENTER);
		getContents().setPadding(new Insets(50, 0, 0, 0));
		getContents().setVgap(15);
		getContents().setHgap(15);
		lblName = new Label(Translator.getTranslation(NAME));
		fullTxtName = new FullTextField(100);
		lblUserframe = new Label(Translator.getTranslation(USERFRAME));
		cbbUserFrame = new ComboBox<String>(userFrameNames);
		cbbUserFrame.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUserFrame.setMinSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUserFrame.setMaxSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		lblPosition = new Label(Translator.getTranslation(POSITION));
		lblX = new Label("X");
		numtxtX = new NumericTextField(10);
		lblY = new Label("Y");
		numtxtY = new NumericTextField(10);
		lblZ = new Label("Z");
		numtxtZ = new NumericTextField(10);
		lblW = new Label("W");
		numtxtW = new NumericTextField(10);
		lblP = new Label("P");
		numtxtP = new NumericTextField(10);
		lblR = new Label("R");
		numtxtR = new NumericTextField(10);
		lblSmoothTo = new Label(Translator.getTranslation(SMOOTH_TO));
		lblSmoothToX = new Label("X");
		numtxtSmoothToX = new NumericTextField(10);
		lblSmoothToY = new Label("Y");
		numtxtSmoothToY = new NumericTextField(10);
		lblSmoothToZ = new Label("Z");
		numtxtSmoothToZ = new NumericTextField(10);
		lblSmoothFrom = new Label(Translator.getTranslation(SMOOTH_FROM));
		lblSmoothFromX = new Label("X");
		numtxtSmoothFromX = new NumericTextField(10);
		lblSmoothFromY = new Label("Y");
		numtxtSmoothFromY = new NumericTextField(10);
		lblSmoothFromZ = new Label("Z");
		numtxtSmoothFromZ = new NumericTextField(10);
		lblStationHeight = new Label(Translator.getTranslation(STATION_HEIGHT));
		numtxtStationHeight = new NumericTextField(10);
		int column = 0; int row = 0;
		getContents().add(lblName, column++, row);
		getContents().add(fullTxtName, column++, row, 4, 1);
		column = 0; row++;
		getContents().add(lblUserframe, column++, row);
		getContents().add(cbbUserFrame, column++, row, 4, 1);
		column = 0; row++;
		getContents().add(lblPosition, column++, row);
		getContents().add(lblX, column++, row);
		getContents().add(numtxtX, column++, row);
		getContents().add(lblY, column++, row);
		getContents().add(numtxtY, column++, row);
		getContents().add(lblZ, column++, row);
		getContents().add(numtxtZ, column++, row);
		column = 0; row++;
		column++;
		getContents().add(lblW, column++, row);
		getContents().add(numtxtW, column++, row);
		getContents().add(lblP, column++, row);
		getContents().add(numtxtP, column++, row);
		getContents().add(lblR, column++, row);
		getContents().add(numtxtR, column++, row);
		column = 0; row++;
		getContents().add(lblSmoothTo, column++, row);
		getContents().add(lblSmoothToX, column++, row);
		getContents().add(numtxtSmoothToX, column++, row);
		getContents().add(lblSmoothToY, column++, row);
		getContents().add(numtxtSmoothToY, column++, row);
		getContents().add(lblSmoothToZ, column++, row);
		getContents().add(numtxtSmoothToZ, column++, row);
		column = 0; row++;
		getContents().add(lblSmoothFrom, column++, row);
		getContents().add(lblSmoothFromX, column++, row);
		getContents().add(numtxtSmoothFromX, column++, row);
		getContents().add(lblSmoothFromY, column++, row);
		getContents().add(numtxtSmoothFromY, column++, row);
		getContents().add(lblSmoothFromZ, column++, row);
		getContents().add(numtxtSmoothFromZ, column++, row);
		column = 0; row++;
		getContents().add(lblStationHeight, column++, row);
		getContents().add(numtxtStationHeight, column++, row,2,1);
		column = 0; row++;
		btnSave = createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, 
			new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent arg0) {
					getPresenter().saveData(fullTxtName.getText(), cbbUserFrame.getValue(), 
							Float.parseFloat(numtxtX.getText()),
							Float.parseFloat(numtxtY.getText()),
							Float.parseFloat(numtxtZ.getText()),
							Float.parseFloat(numtxtW.getText()),
							Float.parseFloat(numtxtP.getText()),
							Float.parseFloat(numtxtR.getText()),
							Float.parseFloat(numtxtSmoothToX.getText()),
							Float.parseFloat(numtxtSmoothToY.getText()),
							Float.parseFloat(numtxtSmoothToZ.getText()),
							Float.parseFloat(numtxtSmoothFromX.getText()),
							Float.parseFloat(numtxtSmoothFromY.getText()),
							Float.parseFloat(numtxtSmoothFromZ.getText()),
							Float.parseFloat(numtxtStationHeight.getText()));
				}
		});
		getContents().add(btnSave, column++, row, 7, 1);
		GridPane.setHalignment(btnSave, HPos.CENTER);
	}
	
	public void setUserFrames(final List<String> userFrames) {
		userFrameNames.clear();
		userFrameNames.addAll(userFrames);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtName.setFocusListener(listener);
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
		numtxtStationHeight.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		if (reversalUnit != null) {
			getPresenter().updateUserFrames();
			fullTxtName.setText(reversalUnit.getName());
			Coordinates relPosition = reversalUnit.getWorkAreas().get(0).getDefaultClamping().getRelativePosition();
			numtxtX.setText("" + relPosition.getX());
			numtxtY.setText("" + relPosition.getY());
			numtxtZ.setText("" + relPosition.getZ());
			numtxtW.setText("" + relPosition.getW());
			numtxtP.setText("" + relPosition.getP());
			numtxtR.setText("" + relPosition.getR());
			Coordinates smoothTo = reversalUnit.getWorkAreas().iterator().next().getDefaultClamping().getSmoothToPoint();
			numtxtSmoothToX.setText("" + smoothTo.getX());
			numtxtSmoothToY.setText("" + smoothTo.getY());
			numtxtSmoothToZ.setText("" + smoothTo.getZ());
			Coordinates smoothFrom = reversalUnit.getWorkAreas().iterator().next().getDefaultClamping().getSmoothFromPoint();
			numtxtSmoothFromX.setText("" + smoothFrom.getX());
			numtxtSmoothFromY.setText("" + smoothFrom.getY());
			numtxtSmoothFromZ.setText("" + smoothFrom.getZ());
			cbbUserFrame.setValue(reversalUnit.getWorkAreas().iterator().next().getUserFrame().getName());
			numtxtStationHeight.setText("" + reversalUnit.getStationHeight());
		}
	}

}
