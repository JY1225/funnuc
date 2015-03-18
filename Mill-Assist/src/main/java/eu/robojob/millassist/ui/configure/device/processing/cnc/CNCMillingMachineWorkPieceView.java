package eu.robojob.millassist.ui.configure.device.processing.cnc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;

public class CNCMillingMachineWorkPieceView extends AbstractFormView<CNCMillingMachineWorkPiecePresenter> {

	private PickStep pickStep;
	private StackPane iconWidthPane;
	private SVGPath workPieceWidthPath, workPieceLengthPath, workPieceHeightPath,  workPieceDiameterPath, workPieceCylinderHeightPath;
	private Button btnResetWidth;
	private StackPane iconLengthPane;
	private Button btnResetLength;
	private StackPane iconHeightPane;
	private Button btnResetHeight;
	private Label lblWorkPieceWidth;
	private Label lblWorkPieceLength;
	private Label lblWorkPieceHeight;
	private Label lblWorkPieceWeight;
	private NumericTextField ntxtWorkPieceWidth;
	private NumericTextField ntxtWorkPieceLength;
	private NumericTextField ntxtWorkPieceHeight;
	private NumericTextField ntxtWorkPieceWeight;
	private Button btnCalcWeight;
	private Button btnResetWeight;
	
	protected static final String WIDTH_ICON = "M 42.25 0.03125 A 0.30003001 0.30003001 0 0 0 42.21875 0.0625 A 0.30003001 0.30003001 0 0 0 42.1875 0.0625 A 0.30003001 0.30003001 0 0 0 42.15625 0.09375 A 0.30003001 0.30003001 0 0 0 42.03125 0.25 A 0.30003001 0.30003001 0 0 0 42 0.3125 A 0.30003001 0.30003001 0 0 0 42 0.34375 L 42 9.6875 L 9.9375 24.78125 L 24.625 31.78125 L 56.71875 16.625 A 0.30003001 0.30003001 0 0 0 56.78125 16.71875 A 0.30003001 0.30003001 0 0 0 56.90625 16.8125 A 0.30003001 0.30003001 0 0 0 57.03125 16.8125 A 0.30003001 0.30003001 0 0 0 57.21875 16.71875 A 0.30003001 0.30003001 0 0 0 57.28125 16.5 A 0.30003001 0.30003001 0 0 0 57.28125 16.46875 L 57.28125 6.9375 A 0.30003001 0.30003001 0 0 0 57.28125 6.8125 A 0.30003001 0.30003001 0 0 0 57.1875 6.6875 A 0.30003001 0.30003001 0 0 0 57 6.625 A 0.30003001 0.30003001 0 0 0 56.90625 6.65625 A 0.30003001 0.30003001 0 0 0 56.8125 6.6875 A 0.30003001 0.30003001 0 0 0 56.6875 6.9375 L 56.6875 9.4375 L 56.15625 9 L 53.15625 6.40625 L 52.34375 5.71875 L 52.71875 6.71875 L 53.03125 7.59375 L 46.6875 4.46875 L 47.34375 4.25 L 48.34375 3.875 L 47.28125 3.71875 L 43.375 3.125 L 42.59375 3 L 42.59375 0.34375 A 0.30003001 0.30003001 0 0 0 42.59375 0.3125 A 0.30003001 0.30003001 0 0 0 42.4375 0.0625 A 0.30003001 0.30003001 0 0 0 42.40625 0.0625 A 0.30003001 0.30003001 0 0 0 42.25 0.03125 z M 42.59375 3.125 L 43.125 3.59375 L 46.15625 6.1875 L 46.9375 6.875 L 46.59375 5.875 L 46.21875 4.90625 L 52.6875 8.0625 L 51.9375 8.34375 L 50.96875 8.6875 L 52 8.875 L 55.9375 9.5 L 56.6875 9.59375 L 56.6875 16.46875 A 0.30003001 0.30003001 0 0 0 56.6875 16.5 L 42.59375 9.875 L 42.59375 3.125 z M 57.21875 17.03125 L 24.9375 32.28125 L 24.9375 48.25 L 57.21875 33.0625 L 57.21875 17.03125 z M 9.5625 25.25 L 9.5625 41.28125 L 24.34375 48.25 L 24.34375 32.3125 L 9.5625 25.25 z";
	protected static final String LENGTH_ICON = "M 41.9375 -0.28125 A 0.30003 0.30003 0 0 0 41.90625 -0.25 A 0.30003 0.30003 0 0 0 41.875 -0.21875 A 0.30003 0.30003 0 0 0 41.75 -0.0625 A 0.30003 0.30003 0 0 0 41.71875 0.03125 L 41.71875 2.65625 L 40.625 2.78125 L 35.71875 3.4375 L 34.40625 3.625 L 35.625 4.09375 L 36.71875 4.53125 L 14.40625 15.0625 L 14.84375 13.84375 L 15.3125 12.625 L 14.3125 13.46875 L 10.5 16.625 L 9.875 17.125 L 9.875 15.21875 A 0.3750375 0.3750375 0 0 0 9.8125 15 A 0.3750375 0.3750375 0 0 0 9.4375 14.84375 A 0.3750375 0.3750375 0 0 0 9.125 15.21875 L 9.125 24.78125 A 0.3750375 0.3750375 0 0 0 9.875 24.78125 L 9.875 17.375 L 10.75 17.21875 L 11.25 17.15625 A 0.30189829 0.30189829 0 0 0 11.34375 17.15625 L 11.375 17.125 A 0.30189829 0.30189829 0 0 0 11.40625 17.125 L 15.6875 16.5625 L 17 16.375 L 15.75 15.90625 L 14.8125 15.53125 L 36.96875 5.0625 L 36.5625 6.15625 L 36.0625 7.40625 L 37.0625 6.5625 L 40.90625 3.375 L 41.71875 2.71875 L 41.71875 9.59375 A 0.30003 0.30003 0 0 0 41.71875 9.65625 A 0.30003 0.30003 0 0 0 41.78125 9.8125 L 10 24.78125 L 24.65625 31.78125 L 56.84375 16.5625 L 42.28125 9.71875 A 0.30003 0.30003 0 0 0 42.3125 9.65625 A 0.30003 0.30003 0 0 0 42.3125 9.59375 L 42.3125 0.03125 A 0.30003 0.30003 0 0 0 42.28125 -0.09375 A 0.30003 0.30003 0 0 0 42.28125 -0.125 A 0.30003 0.30003 0 0 0 42 -0.28125 A 0.30003 0.30003 0 0 0 41.9375 -0.28125 z M 57.28125 17.03125 L 25 32.28125 L 25 48.25 L 57.28125 33.0625 L 57.28125 17.03125 z M 9.59375 25.25 L 9.59375 41.28125 L 24.40625 48.25 L 24.40625 32.3125 L 9.59375 25.25 z";
	private static final String HEIGTH_ICON = "M 42.15625 9.625 L 10 24.75 L 24.65625 31.78125 L 56.875 16.53125 L 42.15625 9.625 z M 57.28125 17 L 25 32.25 L 25 48.25 L 57.28125 33.0625 L 57.28125 17 z M 0.3125 20.78125 A 0.3750375 0.3750375 0 0 0 0.15625 20.90625 A 0.3750375 0.3750375 0 0 0 0.34375 21.5 L 1.96875 22.25 L 1.75 23 L 0.5625 26.8125 L 0.25 27.78125 L 1.03125 27.0625 L 1.59375 26.53125 L 1.59375 32.5 L 1.03125 31.90625 L 0.3125 31.15625 L 0.59375 32.1875 L 1.59375 36.03125 L 1.78125 36.75 L 1.09375 36.40625 A 0.3750375 0.3750375 0 0 0 0.90625 36.375 A 0.38305158 0.38305158 0 0 0 0.75 37.125 L 9.4375 41.15625 A 0.37791833 0.37791833 0 0 0 9.625 41.1875 L 9.625 41.25 L 24.40625 48.21875 L 24.40625 32.28125 L 9.625 25.25 L 9.625 40.40625 L 1.875 36.78125 L 2.125 36.03125 L 3.28125 32.25 L 3.625 31.21875 L 2.84375 31.96875 L 2.21875 32.5625 L 2.21875 26.46875 L 2.8125 27.125 L 3.5625 27.875 L 3.28125 26.84375 L 2.28125 23 L 2.09375 22.3125 L 8.96875 25.53125 A 0.38173637 0.38173637 0 0 0 9.4375 24.96875 A 0.38173637 0.38173637 0 0 0 9.28125 24.84375 L 0.65625 20.8125 A 0.3750375 0.3750375 0 0 0 0.4375 20.78125 A 0.3750375 0.3750375 0 0 0 0.375 20.78125 A 0.3750375 0.3750375 0 0 0 0.34375 20.78125 A 0.3750375 0.3750375 0 0 0 0.3125 20.78125 z";
	private static final String CYLINDER_DIAMETER = "m 8.4357379,16.70846 -0.015644,31.60486 c 0,1.83398 5.4596541,3.99378 12.0288061,3.99378 6.56915,0 11.900559,-2.2207 11.900559,-4.05469 l -0.0441,-31.48556 c -1.78515,1.45145 -6.433942,2.50296 -11.83022,2.50296 -5.509607,0 -10.311542,-1.07226 -12.0394961,-2.56135 z M 32.081029,14.60227 c 0,1.93077 -5.272392,3.53256 -11.683023,3.53256 -6.410638,0 -11.7873951,-1.567 -11.7873951,-3.49777 0,-1.93078 5.3767571,-3.49778 11.7873951,-3.49778 6.410631,0 11.683023,1.53222 11.683023,3.46299 z m -23.4103001,-0.02421 0,-10.2948892 M 32.018289,14.57536 l 0,-10.2416999 m -21.235196,1.41328 18.646656,0 0,0.21668 -18.646656,0 z m -0.309491,0.11481 c -0.46117,-0.00712 -1.3836751,0 -1.3836751,0 l 3.1787431,-0.85175 -0.880021,0.88003 c 0,0 -0.609922,-0.023566 -0.915047,-0.02828 z m 0.02376,0.0155 -1.3836661,0 3.1787421,0.85174 -0.880021,-0.88002 z m 19.757157,-0.005 1.38366,0 -3.178726,0.85174 0.880016,-0.88002 z m -0.0239,-0.0155 1.38366,0 -3.178725,-0.85175 0.880025,0.88003 z";
	private static final String CYLINDER_HEIGHT = "m 59.12577,42.48719 0,26.662364 -0.182734,0 0,-26.662364 z m -0.09784,-0.326464 c 0.007,-0.46117 0,-1.383675 0,-1.383675 l 0.85175,3.178743 -0.88003,-0.880021 c 0,0 0.0236,-0.609922 0.0283,-0.915047 z m -0.0155,0.02376 0,-1.383666 -0.85174,3.178742 0.88002,-0.880021 z m -0.0058,28.24891 0,1.38366 -0.85174,-3.178726 0.88002,0.880016 z m 0.0155,-0.0239 0,1.38366 0.85175,-3.178725 -0.88003,0.880025 z m -8.407166,1.723808 10.2417,0 m -10.401569,-31.720323 10.29489,0 M 50.17359,38.263197 c 0,1.930767 -5.272392,3.532557 -11.683023,3.532557 -6.410638,0 -11.787395,-1.567 -11.787395,-3.497767 0,-1.93078 5.376757,-3.49778 11.787395,-3.49778 6.410631,0 11.683023,1.53222 11.683023,3.46299 z m -23.645291,2.106187 -0.01564,31.60486 c 0,1.83398 5.459654,3.99378 12.028806,3.99378 6.56915,0 11.900559,-2.2207 11.900559,-4.05469 l -0.0441,-31.48556 c -1.78515,1.45145 -6.433942,2.50296 -11.83022,2.50296 -5.509607,0 -10.311542,-1.07226 -12.039496,-2.56135 z";
	protected static final double ICON_PANE_WIDTH = 60;
	protected static final double ICON_PANE_HEIGHT = 50;
	private static final int MAX_INTEGER_LENGTH = 6;

	private static final String RESET = "CNCMillingMachineWorkPieceView.reset";
	protected static final String WIDTH = "CNCMillingMachineWorkPieceView.width";
	protected static final String LENGTH = "CNCMillingMachineWorkPieceView.length";
	private static final String HEIGHT = "CNCMillingMachineWorkPieceView.height";
	private static final String DIAMETER = "BasicStackPlateWorkPieceView.diameter";
	private static final String CALC = "CNCMillingMachineWorkPieceView.calc";
	private static final String WEIGHT = "CNCMillingMachineWorkPieceView.weight";
	
	private static final String CSS_CLASS_CYLINDER = "cylinder-svg";

	@Override
	protected void build() {
		
		int column = 0;
		int row = 0;
				
		buildIcons();
		
		getContents().setAlignment(Pos.CENTER);
		
		GridPane dimensions = new GridPane();
		dimensions.setHgap(10);
		
		iconLengthPane = new StackPane();
		iconLengthPane.getChildren().add(workPieceLengthPath);
		iconLengthPane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
		StackPane.setAlignment(workPieceLengthPath, Pos.BOTTOM_RIGHT);
		dimensions.add(iconLengthPane, column++, row);
		lblWorkPieceLength = new Label(Translator.getTranslation(LENGTH));
		dimensions.add(lblWorkPieceLength, column++, row);
		ntxtWorkPieceLength = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtWorkPieceLength.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceLength.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceLength.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedLength(newValue);
			}
		});
		dimensions.add(ntxtWorkPieceLength, column++, row);
		dimensions.setPrefWidth(450);
		dimensions.setAlignment(Pos.CENTER);
		btnResetLength = new Button();
		Text txtBtnResetLength = new Text(Translator.getTranslation(RESET));
		txtBtnResetLength.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_LABEL, CSS_CLASS_CENTER_TEXT);
		btnResetLength.setGraphic(txtBtnResetLength);
		btnResetLength.setAlignment(Pos.CENTER);
		btnResetLength.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().resetLength();
			}
		});
		btnResetLength.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
		btnResetLength.setPrefSize(UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT);
		dimensions.add(btnResetLength, column++, row);
		column = 0;
		row++;
		
		iconWidthPane = new StackPane();
		iconWidthPane.getChildren().add(workPieceWidthPath);
		iconWidthPane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
		StackPane.setAlignment(workPieceWidthPath, Pos.BOTTOM_RIGHT);
		dimensions.add(iconWidthPane, column++, row);
		lblWorkPieceWidth = new Label(Translator.getTranslation(WIDTH));
		dimensions.add(lblWorkPieceWidth, column++, row);
		ntxtWorkPieceWidth = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtWorkPieceWidth.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWidth.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedDiameterWidth(newValue);
			}
		});
		dimensions.add(ntxtWorkPieceWidth, column++, row);
		btnResetWidth = new Button();
		Text txtBtnResetWidth = new Text(Translator.getTranslation(RESET));
		txtBtnResetWidth.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_LABEL, CSS_CLASS_CENTER_TEXT);
		btnResetWidth.setGraphic(txtBtnResetWidth);
		btnResetWidth.setAlignment(Pos.CENTER);
		btnResetWidth.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().resetWidth();
			}
		});
		btnResetWidth.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
		btnResetWidth.setPrefSize(UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT);
		dimensions.add(btnResetWidth, column++, row);
		column = 0;
		row++;
		
		iconHeightPane = new StackPane();
		iconHeightPane.getChildren().add(workPieceHeightPath);
		iconHeightPane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
		StackPane.setAlignment(workPieceHeightPath, Pos.BOTTOM_RIGHT);
		dimensions.add(iconHeightPane, column++, row);
		lblWorkPieceHeight = new Label(Translator.getTranslation(HEIGHT));
		dimensions.add(lblWorkPieceHeight, column++, row);
		ntxtWorkPieceHeight = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtWorkPieceHeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceHeight.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedHeight(newValue);
			}
		});
		dimensions.add(ntxtWorkPieceHeight, column++, row);
		btnResetHeight = new Button();
		Text txtBtnResetHeight = new Text(Translator.getTranslation(RESET));
		txtBtnResetHeight.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_LABEL, CSS_CLASS_CENTER_TEXT);
		btnResetHeight.setGraphic(txtBtnResetHeight);
		btnResetHeight.setAlignment(Pos.CENTER);
		btnResetHeight.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().resetHeight();
			}
		});
		btnResetHeight.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
		btnResetHeight.setPrefSize(UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT);
		dimensions.add(btnResetHeight, column++, row);
		
		column = 0;
		row++;
		lblWorkPieceWeight = new Label(Translator.getTranslation(WEIGHT));
		column++;
		dimensions.add(lblWorkPieceWeight, column++, row);
		ntxtWorkPieceWeight = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtWorkPieceWeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWeight.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedWeight(newValue);
			}
		});
		dimensions.add(ntxtWorkPieceWeight, column++, row);
		btnCalcWeight = createButton(Translator.getTranslation(CALC), UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().calcWeight();
			}
		});
		dimensions.add(btnCalcWeight, column++, row);
		btnResetWeight = createButton(Translator.getTranslation(RESET), UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().resetWeight();
			}
		});
		dimensions.add(btnResetWeight, column++, row);
		
		column = 0;
		row++;
		getContents().add(dimensions, column++, row);
		
		hideNotification();
		refresh();
	}
	
	public void setPickStep(final PickStep pickStep) {
		this.pickStep = pickStep;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		ntxtWorkPieceHeight.setFocusListener(listener);
		ntxtWorkPieceLength.setFocusListener(listener);
		ntxtWorkPieceWidth.setFocusListener(listener);
		ntxtWorkPieceWeight.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		hideNotification();
		setDimensions(pickStep.getRobotSettings().getWorkPiece().getDimensions());
		setWeight(pickStep.getRobotSettings().getWorkPiece().getWeight());
		if (pickStep.getRobotSettings().getWorkPiece().getMaterial().equals(Material.OTHER)) {
			btnCalcWeight.setDisable(true);
		} else {
			btnCalcWeight.setDisable(false);
		}
		showShapeView(pickStep.getRobotSettings().getWorkPiece().getShape());
		getPresenter().recalculate();
	}

	private void setDimensions(final IWorkPieceDimensions workPieceDimensions) {
		float widthDiameter = workPieceDimensions.getDimension(Dimensions.WIDTH);
		if (widthDiameter == -1) {
			widthDiameter = workPieceDimensions.getDimension(Dimensions.DIAMETER);
		}		
		if (widthDiameter > 0) {
			ntxtWorkPieceWidth.setText("" + widthDiameter);
		} else {
			ntxtWorkPieceWidth.setText("");
		}
		float length = workPieceDimensions.getDimension(Dimensions.LENGTH);
		if (length > 0) {
			ntxtWorkPieceLength.setText("" + length);
		} else {
			ntxtWorkPieceLength.setText("");
		}
		float height = workPieceDimensions.getDimension(Dimensions.HEIGHT);
		if (height > 0) {
			ntxtWorkPieceHeight.setText("" + height);
		} else {
			ntxtWorkPieceHeight.setText("");
		}
	}

	private void setWeight(final float weight) {
		if (weight > 0) {
			ntxtWorkPieceWeight.setText("" + weight);		
			ntxtWorkPieceWeight.cleanText();
		} else {
			ntxtWorkPieceWeight.setText("");
		}
	}
	
	private void buildIcons() {
		workPieceLengthPath = new SVGPath();
		workPieceLengthPath.setContent(LENGTH_ICON);
		workPieceLengthPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
		workPieceHeightPath = new SVGPath();
		workPieceHeightPath.setContent(HEIGTH_ICON);
		workPieceHeightPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
		workPieceWidthPath = new SVGPath();
		workPieceWidthPath.setContent(WIDTH_ICON);
		workPieceWidthPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
		workPieceDiameterPath = new SVGPath();
		workPieceDiameterPath.setContent(CYLINDER_DIAMETER);
		workPieceDiameterPath.getStyleClass().addAll(CSS_CLASS_FORM_ICON, CSS_CLASS_CYLINDER);
		workPieceCylinderHeightPath = new SVGPath();
		workPieceCylinderHeightPath.setContent(CYLINDER_HEIGHT);
		workPieceCylinderHeightPath.getStyleClass().addAll(CSS_CLASS_FORM_ICON, CSS_CLASS_CYLINDER);
	}
	
	private void showShapeView(WorkPieceShape shape) {
		if (shape.equals(WorkPieceShape.CUBIC)) {
			iconWidthPane.getChildren().clear();
			iconWidthPane.getChildren().add(workPieceWidthPath);
			iconHeightPane.getChildren().clear();
			iconHeightPane.getChildren().add(workPieceHeightPath);
			lblWorkPieceWidth.setText(Translator.getTranslation(WIDTH));
			setCubicFieldsVisible(true);
		} else if (shape.equals(WorkPieceShape.CYLINDRICAL)) {
			iconWidthPane.getChildren().clear();
			iconWidthPane.getChildren().add(workPieceDiameterPath);
			iconHeightPane.getChildren().clear();
			iconHeightPane.getChildren().add(workPieceCylinderHeightPath);
			lblWorkPieceWidth.setText(Translator.getTranslation(DIAMETER));
			setCubicFieldsVisible(false);
		}
	}
	
	private void setCubicFieldsVisible(boolean visible) {
		lblWorkPieceLength.setVisible(visible);
		ntxtWorkPieceLength.setVisible(visible);
		iconLengthPane.setVisible(visible);
		lblWorkPieceLength.setManaged(visible);
		ntxtWorkPieceLength.setManaged(visible);
		iconLengthPane.setManaged(visible);
		btnResetLength.setVisible(visible);
		btnResetLength.setManaged(visible);
	}
}
