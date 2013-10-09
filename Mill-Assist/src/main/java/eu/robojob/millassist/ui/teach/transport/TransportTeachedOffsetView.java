package eu.robojob.millassist.ui.teach.transport;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class TransportTeachedOffsetView extends AbstractFormView<TransportTeachedOffsetPresenter> {

	private Coordinates coordinates;
	
	private Label lblX;
	private NumericTextField numtxtX;
	private Button btnResetX;
	private Label lblY;
	private NumericTextField numtxtY;
	private Button btnResetY;
	private Label lblZ;
	private NumericTextField numtxtZ;
	private Button btnResetZ;
	private Label lblW;
	private NumericTextField numtxtW;
	private Button btnResetW;
	private Label lblP;
	private NumericTextField numtxtP;
	private Button btnResetP;
	private Label lblR;
	private NumericTextField numtxtR;
	private Button btnResetR;
	private CheckBox cbAlsoUpdateNext;
	private Region spacer;
	
	private DecimalFormat df;
	
	private Button btnSave;
	
	private static final String SAVE = "TransportTeachedOffsetView.save";
	private static final String UPDATE_NEXT = "TransportTeachedOffsetView.updateNext";
	private static final int NUM_WIDTH = 60;
	private static final int SPACE = 15;
	
	private static final String SAVE_ICON = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";

	public TransportTeachedOffsetView() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		df = new DecimalFormat("#.##", otherSymbols);
	}
	
	public void setCoordinates(final Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	@Override
	protected void build() {
		getContents().setHgap(10);
		getContents().setVgap(10);
		
		lblX = new Label("X");
		numtxtX = new NumericTextField(8);
		numtxtX.setPrefWidth(NUM_WIDTH);
		numtxtX.setMinWidth(NUM_WIDTH);
		numtxtX.setMaxWidth(NUM_WIDTH);
		btnResetX = createButton("Reset", UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				numtxtX.setText(df.format(coordinates.getX()));
			}
		});
		lblY = new Label("Y");
		numtxtY = new NumericTextField(8);
		numtxtY.setPrefWidth(NUM_WIDTH);
		numtxtY.setMinWidth(NUM_WIDTH);
		numtxtY.setMaxWidth(NUM_WIDTH);
		btnResetY = createButton("Reset", UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				numtxtY.setText(df.format(coordinates.getY()));
			}
		});
		lblZ = new Label("Z");
		numtxtZ = new NumericTextField(8);
		numtxtZ.setPrefWidth(NUM_WIDTH);
		numtxtZ.setMinWidth(NUM_WIDTH);
		numtxtZ.setMaxWidth(NUM_WIDTH);
		btnResetZ = createButton("Reset", UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				numtxtZ.setText(df.format(coordinates.getZ()));
			}
		});
		lblW = new Label("W");
		numtxtW = new NumericTextField(8);
		numtxtW.setPrefWidth(NUM_WIDTH);
		numtxtW.setMinWidth(NUM_WIDTH);
		numtxtW.setMaxWidth(NUM_WIDTH);
		btnResetW = createButton("Reset", UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				numtxtW.setText(df.format(coordinates.getW()));
			}
		});
		lblP = new Label("P");
		numtxtP = new NumericTextField(8);
		numtxtP.setPrefWidth(NUM_WIDTH);
		numtxtP.setMinWidth(NUM_WIDTH);
		numtxtP.setMaxWidth(NUM_WIDTH);
		btnResetP = createButton("Reset", UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				numtxtP.setText(df.format(coordinates.getP()));
			}
		});
		lblR = new Label("R");
		numtxtR = new NumericTextField(8);
		numtxtR.setPrefWidth(NUM_WIDTH);
		numtxtR.setMinWidth(NUM_WIDTH);
		numtxtR.setMaxWidth(NUM_WIDTH);
		btnResetR = createButton("Reset", UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				numtxtR.setText(df.format(coordinates.getR()));
			}
		});
		cbAlsoUpdateNext = new CheckBox(Translator.getTranslation(UPDATE_NEXT));
		cbAlsoUpdateNext.setSelected(true);
		btnSave = createButton(SAVE_ICON, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().saveAbsoluteOffset(Float.parseFloat(numtxtX.getText()), Float.parseFloat(numtxtY.getText()),
						Float.parseFloat(numtxtZ.getText()), Float.parseFloat(numtxtW.getText()), Float.parseFloat(numtxtP.getText()),
						Float.parseFloat(numtxtR.getText()), cbAlsoUpdateNext.isSelected());
			}
		});
		
		spacer = new Region();
		spacer.setPrefWidth(SPACE);
		spacer.setMinWidth(SPACE);
		spacer.setMaxWidth(SPACE);
		
		int row = 0;
		int column = 0;
		
		getContents().add(lblX, column++, row);
		getContents().add(numtxtX, column++, row);
		getContents().add(btnResetX, column++, row);
		getContents().add(spacer, column++, row);
		getContents().add(lblW, column++, row);
		getContents().add(numtxtW, column++, row);
		getContents().add(btnResetW, column++, row);
		
		row++;
		column = 0;
		getContents().add(lblY, column++, row);
		getContents().add(numtxtY, column++, row);
		getContents().add(btnResetY, column++, row);
		column++;
		getContents().add(lblP, column++, row);
		getContents().add(numtxtP, column++, row);
		getContents().add(btnResetP, column++, row);
		
		row++;
		column = 0;
		getContents().add(lblZ, column++, row);
		getContents().add(numtxtZ, column++, row);
		getContents().add(btnResetZ, column++, row);
		column++;
		getContents().add(lblR, column++, row);
		getContents().add(numtxtR, column++, row);
		getContents().add(btnResetR, column++, row);
		
		row++;
		column = 0;
		getContents().add(cbAlsoUpdateNext, column++, row, 7, 1);
		
		row++;
		column = 0;
		getContents().add(btnSave, column++, row, 7, 1); 
		GridPane.setHalignment(cbAlsoUpdateNext, HPos.CENTER);
		GridPane.setHalignment(btnSave, HPos.CENTER);
		GridPane.setMargin(cbAlsoUpdateNext, new Insets(10, 0, 0, 0));
		
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		numtxtX.setFocusListener(listener);
		numtxtY.setFocusListener(listener);
		numtxtZ.setFocusListener(listener);
		numtxtW.setFocusListener(listener);
		numtxtP.setFocusListener(listener);
		numtxtR.setFocusListener(listener);
	}
	
	public void setCheckBoxUpdateNextEnabled(final boolean enable) {
		cbAlsoUpdateNext.setSelected(false);
		cbAlsoUpdateNext.setDisable(!enable);
	}

	@Override
	public void refresh() {
		getPresenter().refresh();
		numtxtX.setText(df.format(coordinates.getX()));
		numtxtY.setText(df.format(coordinates.getY()));
		numtxtZ.setText(df.format(coordinates.getZ()));
		numtxtW.setText(df.format(coordinates.getW()));
		numtxtP.setText(df.format(coordinates.getP()));
		numtxtR.setText(df.format(coordinates.getR()));
	}

}
