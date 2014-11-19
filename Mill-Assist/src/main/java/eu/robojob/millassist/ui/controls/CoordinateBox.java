package eu.robojob.millassist.ui.controls;

import eu.robojob.millassist.positioning.Coordinates;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class CoordinateBox extends GridPane {
	
	private NumericTextField[] numTfList;
	private Label[] numLabels;
	private String[] lblTexts;
	private int maxLengthTf;
	private float[] coordValues;
	private Coordinates coord;
	
	public CoordinateBox(int maxLengthTf, String... lblTexts) {
		this(new Coordinates(), maxLengthTf, lblTexts);
	}
	
	public CoordinateBox(Coordinates coord, int maxLengthTf, String... lblTexts) {
		this.coord = coord;
		this.lblTexts = lblTexts;
		this.maxLengthTf = maxLengthTf;
		this.numTfList = new NumericTextField[lblTexts.length];
		this.numLabels = new Label[lblTexts.length];
		this.coordValues = new float[lblTexts.length];
		build();
	}
	
	private void build() {
		initComponents();
		
		setHgap(10);
		setVgap(15);
		
		int col = 0; int row = 0;
		for (int i = 0; i < numTfList.length; i++) {
			add(numLabels[i], col++, row);
			add(numTfList[i], col++, row);
			if (i == 2) {
				col = 0;
				row++;
			}
		}
	}

	private void initComponents() {
		for (int i = 0; i < numTfList.length; i++) {
			NumericTextField tmpField = new NumericTextField(maxLengthTf);
			numTfList[i] = tmpField;
			numLabels[i] = new Label(lblTexts[i]);
			numLabels[i].setMinWidth(25);
		}
	}
	
	public void addChangeListeners(final ChangeListener<Float> changeListener) {
		for (NumericTextField tf: numTfList) {
			tf.setOnChange(changeListener);
		}
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		for (NumericTextField tf: numTfList) {
			tf.setFocusListener(listener);
		}
	}
	
	public void updateCoordinate() {
		for (int i = 0; i < numTfList.length; i++) {
			coordValues[i] = numTfList[i].getValue();
		}
		coord.setCoordinateValues(coordValues);
	}
	
	public Coordinates getCoordinate() {
		return this.coord;
	}
	
	public void setCoordinate(Coordinates coordinate) {
		this.coord = coordinate;
	}
	
	public void reset() {
		float[] tmpCoordVal = coord.getCoordValues();
		for (int i = 0; i < numTfList.length; i++) {
			numTfList[i].setText("" + tmpCoordVal[i]);
		}
	}
	
	public void setPrefHeightDimension(double height) {
		for (NumericTextField tf: numTfList) {
			tf.setPrefHeight(height);
			tf.setMinHeight(height);
			tf.setMaxHeight(height);	
		}
	}
	
	public boolean isConfigured() {
		for (NumericTextField tf: numTfList) {
			if (tf.getText().equals("")) {
				return false;
			}
		}
		return true;
	}
	
}
