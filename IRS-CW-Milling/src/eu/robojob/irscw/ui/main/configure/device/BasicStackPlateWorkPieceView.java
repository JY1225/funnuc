package eu.robojob.irscw.ui.main.configure.device;

import javafx.scene.control.Label;
import javafx.scene.shape.SVGPath;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.configure.AbstractFormView;
import eu.robojob.irscw.util.UIConstants;

public class BasicStackPlateWorkPieceView extends AbstractFormView<BasicStackPlateWorkPiecePresenter> {

	private PickStep pickStep;
	
	private static final String widthPath = "M 32.6875 0.125 A 0.30003 0.30003 0 0 0 32.625 0.15625 A 0.30003 0.30003 0 0 0 32.4375 0.40625 A 0.30003 0.30003 0 0 0 32.4375 0.4375 L 32.4375 9.78125 L 0.375 24.875 L 15.0625 31.875 L 47.15625 16.71875 A 0.30003 0.30003 0 0 0 47.46875 16.90625 A 0.30003 0.30003 0 0 0 47.65625 16.8125 A 0.30003 0.30003 0 0 0 47.71875 16.59375 A 0.30003 0.30003 0 0 0 47.71875 16.5625 L 47.71875 7.03125 A 0.30003 0.30003 0 0 0 47.625 6.78125 A 0.30003 0.30003 0 0 0 47.4375 6.71875 A 0.30003 0.30003 0 0 0 47.25 6.78125 A 0.30003 0.30003 0 0 0 47.125 7.03125 L 47.125 9.53125 L 46.59375 9.09375 L 43.59375 6.5 L 42.78125 5.8125 L 43.15625 6.8125 L 43.46875 7.6875 L 37.125 4.5625 L 37.78125 4.34375 L 38.78125 3.96875 L 37.71875 3.8125 L 33.8125 3.21875 L 33.03125 3.09375 L 33.03125 0.4375 A 0.30003 0.30003 0 0 0 33.03125 0.40625 A 0.30003 0.30003 0 0 0 32.875 0.15625 A 0.30003 0.30003 0 0 0 32.6875 0.125 z M 33.03125 3.21875 L 33.5625 3.6875 L 36.59375 6.28125 L 37.375 6.96875 L 37.03125 5.96875 L 36.65625 5 L 43.125 8.15625 L 42.375 8.4375 L 41.40625 8.78125 L 42.4375 8.96875 L 46.375 9.59375 L 47.125 9.6875 L 47.125 16.5625 A 0.30003 0.30003 0 0 0 47.125 16.59375 L 33.03125 9.96875 L 33.03125 3.21875 z M 47.65625 17.125 L 15.375 32.375 L 15.375 48.34375 L 47.65625 33.15625 L 47.65625 17.125 z M 0 25.34375 L 0 41.375 L 14.78125 48.34375 L 14.78125 32.40625 L 0 25.34375 z";
	private static final String lengthPath = "M 32.84375 0.03125 A 0.30003 0.30003 0 0 0 32.8125 0.0625 A 0.30003 0.30003 0 0 0 32.625 0.34375 L 32.625 2.96875 L 31.53125 3.09375 L 26.625 3.75 L 25.3125 3.9375 L 26.53125 4.40625 L 27.625 4.84375 L 5.3125 15.375 L 5.75 14.15625 L 6.21875 12.9375 L 5.21875 13.78125 L 1.40625 16.9375 L 0.78125 17.4375 L 0.78125 15.53125 A 0.3750375 0.3750375 0 0 0 0.34375 15.15625 A 0.3750375 0.3750375 0 0 0 0.03125 15.53125 L 0.03125 25.09375 A 0.3750375 0.3750375 0 0 0 0.78125 25.09375 L 0.78125 17.6875 L 1.65625 17.53125 L 2.15625 17.46875 A 0.30189829 0.30189829 0 0 0 2.25 17.46875 L 2.28125 17.4375 A 0.30189829 0.30189829 0 0 0 2.3125 17.4375 L 6.59375 16.875 L 7.90625 16.6875 L 6.65625 16.21875 L 5.71875 15.84375 L 27.875 5.375 L 27.46875 6.46875 L 26.96875 7.71875 L 27.96875 6.875 L 31.8125 3.6875 L 32.625 3.03125 L 32.625 9.90625 A 0.30003 0.30003 0 0 0 32.625 9.96875 A 0.30003 0.30003 0 0 0 32.6875 10.125 L 0.90625 25.09375 L 15.5625 32.09375 L 47.75 16.875 L 33.1875 10.03125 A 0.30003 0.30003 0 0 0 33.21875 9.90625 L 33.21875 0.34375 A 0.30003 0.30003 0 0 0 33.1875 0.21875 A 0.30003 0.30003 0 0 0 32.84375 0.03125 z M 48.1875 17.34375 L 15.90625 32.59375 L 15.90625 48.5625 L 48.1875 33.375 L 48.1875 17.34375 z M 0.5 25.5625 L 0.5 41.59375 L 15.3125 48.5625 L 15.3125 32.625 L 0.5 25.5625 z";
	private static final String heightPath = "M 42.0625 0 L 9.90625 15.125 L 24.5625 22.15625 L 56.78125 6.90625 L 42.0625 0 z M 57.1875 7.375 L 24.90625 22.625 L 24.90625 38.625 L 57.1875 23.4375 L 57.1875 7.375 z M 0.25 11.15625 A 0.3750375 0.3750375 0 0 0 0.25 11.875 L 1.875 12.625 L 1.65625 13.375 L 0.46875 17.1875 L 0.15625 18.15625 L 0.9375 17.4375 L 1.5 16.90625 L 1.5 22.875 L 0.9375 22.28125 L 0.21875 21.53125 L 0.5 22.5625 L 1.5 26.40625 L 1.6875 27.125 L 1 26.78125 A 0.3750375 0.3750375 0 0 0 0.8125 26.75 A 0.3750375 0.3750375 0 0 0 0.65625 27.5 L 9.34375 31.53125 A 0.37791833 0.37791833 0 0 0 9.53125 31.5625 L 9.53125 31.625 L 24.3125 38.59375 L 24.3125 22.65625 L 9.53125 15.625 L 9.53125 30.78125 L 1.78125 27.15625 L 2.03125 26.40625 L 3.1875 22.625 L 3.53125 21.59375 L 2.75 22.34375 L 2.125 22.9375 L 2.125 16.84375 L 2.71875 17.5 L 3.46875 18.25 L 3.1875 17.21875 L 2.1875 13.375 L 2 12.6875 L 8.875 15.90625 A 0.38173637 0.38173637 0 0 0 9.34375 15.34375 A 0.38173637 0.38173637 0 0 0 9.1875 15.21875 L 0.5625 11.1875 A 0.3750375 0.3750375 0 0 0 0.34375 11.15625 A 0.3750375 0.3750375 0 0 0 0.28125 11.15625 A 0.3750375 0.3750375 0 0 0 0.25 11.15625 z";
	
	private SVGPath workPieceWidthPath;
	private SVGPath workPieceLengthPath;
	private SVGPath workPieceHeightPath;
	
	private Label lblWorkPieceWidth;
	private Label lblWorkPieceLength;
	private Label lblWorkPieceHeight;
	
	private Label lblWorkPieceAmount;
	
	private NumericTextField ntxtWorkPieceWidth;
	private NumericTextField ntxtWorkPieceLength;
	private NumericTextField ntxtWorkPieceHeight;
	private IntegerTextField itxtWorkPieceAmount;
	
	private static final int nMaxLength = 6;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	public void setPickStep(PickStep pickStep) {
		this.pickStep = pickStep;
		setHgap(HGAP);
		setVgap(VGAP);
	}
	
	@Override
	protected void build() {
		int row = 0;
		int column = 0;
		
		workPieceWidthPath = new SVGPath();
		workPieceWidthPath.setContent(widthPath);
		add(workPieceWidthPath, column++, row);
		lblWorkPieceWidth = new Label(translator.getTranslation("width"));
		add(lblWorkPieceWidth, column++, row);
		ntxtWorkPieceWidth = new NumericTextField(nMaxLength);
		ntxtWorkPieceWidth.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWidth.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		add(ntxtWorkPieceWidth, column++, row);
		
		column = 0;
		row++;
		
		workPieceLengthPath = new SVGPath();
		workPieceLengthPath.setContent(lengthPath);
		add(workPieceLengthPath, column++, row);
		lblWorkPieceLength = new Label(translator.getTranslation("length"));
		add(lblWorkPieceLength, column++, row);
		ntxtWorkPieceLength = new NumericTextField(nMaxLength);
		ntxtWorkPieceLength.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceLength.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		add(ntxtWorkPieceLength, column++, row);
		
		column = 0;
		row++;
		
		workPieceHeightPath = new SVGPath();
		workPieceHeightPath.setContent(heightPath);
		add(workPieceHeightPath, column++, row);
		lblWorkPieceHeight = new Label(translator.getTranslation("height"));
		add(lblWorkPieceHeight, column++, row);
		ntxtWorkPieceHeight = new NumericTextField(nMaxLength);
		ntxtWorkPieceHeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceHeight.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		add(ntxtWorkPieceHeight, column++, row);
		
		column = 0;
		row++;
		
		lblWorkPieceAmount = new Label(translator.getTranslation("WorkPieceAmount"));
		add(lblWorkPieceAmount, column++, row, 2, 1);
		itxtWorkPieceAmount = new IntegerTextField(nMaxLength);
		itxtWorkPieceAmount.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		itxtWorkPieceAmount.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		column++;
		add(itxtWorkPieceAmount, column++, row);
		
	}

	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		itxtWorkPieceAmount.setFocusListener(listener);
		ntxtWorkPieceHeight.setFocusListener(listener);
		ntxtWorkPieceLength.setFocusListener(listener);
		ntxtWorkPieceWidth.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		
	}

}
