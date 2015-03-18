package eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.RectangularDimensions;

public class ConveyorRawWorkPieceView extends AbstractFormView<ConveyorRawWorkPiecePresenter> {

	protected static final String WIDTH_ICON = "M 42.25 0.03125 A 0.30003001 0.30003001 0 0 0 42.21875 0.0625 A 0.30003001 0.30003001 0 0 0 42.1875 0.0625 A 0.30003001 0.30003001 0 0 0 42.15625 0.09375 A 0.30003001 0.30003001 0 0 0 42.03125 0.25 A 0.30003001 0.30003001 0 0 0 42 0.3125 A 0.30003001 0.30003001 0 0 0 42 0.34375 L 42 9.6875 L 9.9375 24.78125 L 24.625 31.78125 L 56.71875 16.625 A 0.30003001 0.30003001 0 0 0 56.78125 16.71875 A 0.30003001 0.30003001 0 0 0 56.90625 16.8125 A 0.30003001 0.30003001 0 0 0 57.03125 16.8125 A 0.30003001 0.30003001 0 0 0 57.21875 16.71875 A 0.30003001 0.30003001 0 0 0 57.28125 16.5 A 0.30003001 0.30003001 0 0 0 57.28125 16.46875 L 57.28125 6.9375 A 0.30003001 0.30003001 0 0 0 57.28125 6.8125 A 0.30003001 0.30003001 0 0 0 57.1875 6.6875 A 0.30003001 0.30003001 0 0 0 57 6.625 A 0.30003001 0.30003001 0 0 0 56.90625 6.65625 A 0.30003001 0.30003001 0 0 0 56.8125 6.6875 A 0.30003001 0.30003001 0 0 0 56.6875 6.9375 L 56.6875 9.4375 L 56.15625 9 L 53.15625 6.40625 L 52.34375 5.71875 L 52.71875 6.71875 L 53.03125 7.59375 L 46.6875 4.46875 L 47.34375 4.25 L 48.34375 3.875 L 47.28125 3.71875 L 43.375 3.125 L 42.59375 3 L 42.59375 0.34375 A 0.30003001 0.30003001 0 0 0 42.59375 0.3125 A 0.30003001 0.30003001 0 0 0 42.4375 0.0625 A 0.30003001 0.30003001 0 0 0 42.40625 0.0625 A 0.30003001 0.30003001 0 0 0 42.25 0.03125 z M 42.59375 3.125 L 43.125 3.59375 L 46.15625 6.1875 L 46.9375 6.875 L 46.59375 5.875 L 46.21875 4.90625 L 52.6875 8.0625 L 51.9375 8.34375 L 50.96875 8.6875 L 52 8.875 L 55.9375 9.5 L 56.6875 9.59375 L 56.6875 16.46875 A 0.30003001 0.30003001 0 0 0 56.6875 16.5 L 42.59375 9.875 L 42.59375 3.125 z M 57.21875 17.03125 L 24.9375 32.28125 L 24.9375 48.25 L 57.21875 33.0625 L 57.21875 17.03125 z M 9.5625 25.25 L 9.5625 41.28125 L 24.34375 48.25 L 24.34375 32.3125 L 9.5625 25.25 z";
	protected static final String LENGTH_ICON = "M 41.9375 -0.28125 A 0.30003 0.30003 0 0 0 41.90625 -0.25 A 0.30003 0.30003 0 0 0 41.875 -0.21875 A 0.30003 0.30003 0 0 0 41.75 -0.0625 A 0.30003 0.30003 0 0 0 41.71875 0.03125 L 41.71875 2.65625 L 40.625 2.78125 L 35.71875 3.4375 L 34.40625 3.625 L 35.625 4.09375 L 36.71875 4.53125 L 14.40625 15.0625 L 14.84375 13.84375 L 15.3125 12.625 L 14.3125 13.46875 L 10.5 16.625 L 9.875 17.125 L 9.875 15.21875 A 0.3750375 0.3750375 0 0 0 9.8125 15 A 0.3750375 0.3750375 0 0 0 9.4375 14.84375 A 0.3750375 0.3750375 0 0 0 9.125 15.21875 L 9.125 24.78125 A 0.3750375 0.3750375 0 0 0 9.875 24.78125 L 9.875 17.375 L 10.75 17.21875 L 11.25 17.15625 A 0.30189829 0.30189829 0 0 0 11.34375 17.15625 L 11.375 17.125 A 0.30189829 0.30189829 0 0 0 11.40625 17.125 L 15.6875 16.5625 L 17 16.375 L 15.75 15.90625 L 14.8125 15.53125 L 36.96875 5.0625 L 36.5625 6.15625 L 36.0625 7.40625 L 37.0625 6.5625 L 40.90625 3.375 L 41.71875 2.71875 L 41.71875 9.59375 A 0.30003 0.30003 0 0 0 41.71875 9.65625 A 0.30003 0.30003 0 0 0 41.78125 9.8125 L 10 24.78125 L 24.65625 31.78125 L 56.84375 16.5625 L 42.28125 9.71875 A 0.30003 0.30003 0 0 0 42.3125 9.65625 A 0.30003 0.30003 0 0 0 42.3125 9.59375 L 42.3125 0.03125 A 0.30003 0.30003 0 0 0 42.28125 -0.09375 A 0.30003 0.30003 0 0 0 42.28125 -0.125 A 0.30003 0.30003 0 0 0 42 -0.28125 A 0.30003 0.30003 0 0 0 41.9375 -0.28125 z M 57.28125 17.03125 L 25 32.28125 L 25 48.25 L 57.28125 33.0625 L 57.28125 17.03125 z M 9.59375 25.25 L 9.59375 41.28125 L 24.40625 48.25 L 24.40625 32.3125 L 9.59375 25.25 z";
	private static final String HEIGTH_ICON = "M 42.15625 9.625 L 10 24.75 L 24.65625 31.78125 L 56.875 16.53125 L 42.15625 9.625 z M 57.28125 17 L 25 32.25 L 25 48.25 L 57.28125 33.0625 L 57.28125 17 z M 0.3125 20.78125 A 0.3750375 0.3750375 0 0 0 0.15625 20.90625 A 0.3750375 0.3750375 0 0 0 0.34375 21.5 L 1.96875 22.25 L 1.75 23 L 0.5625 26.8125 L 0.25 27.78125 L 1.03125 27.0625 L 1.59375 26.53125 L 1.59375 32.5 L 1.03125 31.90625 L 0.3125 31.15625 L 0.59375 32.1875 L 1.59375 36.03125 L 1.78125 36.75 L 1.09375 36.40625 A 0.3750375 0.3750375 0 0 0 0.90625 36.375 A 0.38305158 0.38305158 0 0 0 0.75 37.125 L 9.4375 41.15625 A 0.37791833 0.37791833 0 0 0 9.625 41.1875 L 9.625 41.25 L 24.40625 48.21875 L 24.40625 32.28125 L 9.625 25.25 L 9.625 40.40625 L 1.875 36.78125 L 2.125 36.03125 L 3.28125 32.25 L 3.625 31.21875 L 2.84375 31.96875 L 2.21875 32.5625 L 2.21875 26.46875 L 2.8125 27.125 L 3.5625 27.875 L 3.28125 26.84375 L 2.28125 23 L 2.09375 22.3125 L 8.96875 25.53125 A 0.38173637 0.38173637 0 0 0 9.4375 24.96875 A 0.38173637 0.38173637 0 0 0 9.28125 24.84375 L 0.65625 20.8125 A 0.3750375 0.3750375 0 0 0 0.4375 20.78125 A 0.3750375 0.3750375 0 0 0 0.375 20.78125 A 0.3750375 0.3750375 0 0 0 0.34375 20.78125 A 0.3750375 0.3750375 0 0 0 0.3125 20.78125 z";
	
	private StackPane icon1Pane;
	private SVGPath workPieceWidthPath;
	private StackPane icon2Pane;
	private SVGPath workPieceLengthPath;
	private StackPane icon3Pane;
	private SVGPath workPieceHeightPath;
	
	private Label lblWorkPieceWidth;
	private Label lblWorkPieceLength;
	private Label lblWorkPieceHeight;
	private Label lblWorkPieceWeight;	
	
	private Label lblMaterial;
	private Button btnCalc;
	
	private NumericTextField ntxtWorkPieceWidth;
	private NumericTextField ntxtWorkPieceLength;
	private NumericTextField ntxtWorkPieceHeight;
	private NumericTextField ntxtWorkPieceWeight;
	
	private HBox materialsBox;
	private Button btnAl;
	private Button btnFe;
	private Button btnCu;
	private Button btnOther;
	
	private Region spacer;
	
	private ConveyorSettings settings;
	
	protected static final int MAX_INTEGER_LENGTH = 6;
	private static final int HGAP = 10;
	private static final int VGAP = 0;
	private static final double BTN_WIDTH = 80;
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	protected static final double ICON_PANE_WIDTH = 60;
	protected static final double ICON_PANE_HEIGHT = 50;
	
	protected static final String WIDTH = "ConveyorRawWorkPieceView.width";
	protected static final String LENGTH = "ConveyorRawWorkPieceView.length";
	private static final String HEIGHT = "ConveyorRawWorkPieceView.height";
	private static final String WEIGHT = "ConveyorRawWorkPieceView.weight";
	private static final String MATERIAL = "ConveyorRawWorkPieceView.material";
	private static final String CALC = "ConveyorRawWorkPieceView.calc";
	private static final String OTHER = "ConveyorRawWorkPieceView.other";
	
	public void setDeviceSettings(final ConveyorSettings settings) {
		this.settings = settings;
	}
	
	@Override
	protected void build() {
		
		getContents().setHgap(HGAP);
		getContents().setVgap(VGAP);
		
		int row = 0;
		int column = 0;
		
		getContents().setPadding(new Insets(0, 0, 0, 30));

		workPieceLengthPath = new SVGPath();
		workPieceLengthPath.setContent(LENGTH_ICON);
		workPieceLengthPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
		icon2Pane = new StackPane();
		icon2Pane.getChildren().add(workPieceLengthPath);
		icon2Pane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
		StackPane.setAlignment(workPieceLengthPath, Pos.BOTTOM_RIGHT);
		getContents().add(icon2Pane, column++, row);
		lblWorkPieceLength = new Label(Translator.getTranslation(LENGTH));
		getContents().add(lblWorkPieceLength, column++, row);
		ntxtWorkPieceLength = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtWorkPieceLength.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceLength.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceLength.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedLength(newValue);
			}
		});
		getContents().add(ntxtWorkPieceLength, column++, row);
		column = 0;
		row++;
		
		workPieceWidthPath = new SVGPath();
		workPieceWidthPath.setContent(WIDTH_ICON);
		workPieceWidthPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
		icon1Pane = new StackPane();
		icon1Pane.getChildren().add(workPieceWidthPath);
		icon1Pane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
		StackPane.setAlignment(workPieceWidthPath, Pos.BOTTOM_RIGHT);
		getContents().add(icon1Pane, column++, row);
		lblWorkPieceWidth = new Label(Translator.getTranslation(WIDTH));
		getContents().add(lblWorkPieceWidth, column++, row);
		ntxtWorkPieceWidth = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtWorkPieceWidth.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWidth.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedWidth(newValue);
			}
		});
		getContents().add(ntxtWorkPieceWidth, column++, row);
		column = 0;
		row++;
		
		workPieceHeightPath = new SVGPath();
		workPieceHeightPath.setContent(HEIGTH_ICON);
		workPieceHeightPath.getStyleClass().add(CSS_CLASS_FORM_ICON);
		icon3Pane = new StackPane();
		icon3Pane.getChildren().add(workPieceHeightPath);
		icon3Pane.setPrefSize(ICON_PANE_WIDTH, ICON_PANE_HEIGHT);
		StackPane.setAlignment(workPieceHeightPath, Pos.BOTTOM_RIGHT);
		getContents().add(icon3Pane, column++, row);
		lblWorkPieceHeight = new Label(Translator.getTranslation(HEIGHT));
		getContents().add(lblWorkPieceHeight, column++, row);
		ntxtWorkPieceHeight = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtWorkPieceHeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceHeight.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedHeight(newValue);
			}
		});
		getContents().add(ntxtWorkPieceHeight, column++, row);
		column = 0;
		row++;
		
		materialsBox = new HBox();
		materialsBox.setAlignment(Pos.CENTER_LEFT);
		lblMaterial = new Label(Translator.getTranslation(MATERIAL));
		lblMaterial.getStyleClass().add(CSS_CLASS_FORM_LABEL);
		GridPane.setMargin(materialsBox, new Insets(5, 0, 0, 0));
		btnAl = createButton("Al", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedMaterial(Material.AL);
			}
		});
		btnAl.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		materialsBox.getChildren().add(btnAl);
		btnCu = createButton("Cu", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedMaterial(Material.CU);
			}
		});
		btnCu.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
		materialsBox.getChildren().add(btnCu);
		btnFe = createButton("Fe", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedMaterial(Material.FE);
			}
		});
		btnFe.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
		materialsBox.getChildren().add(btnFe);
		btnOther = createButton(Translator.getTranslation(OTHER), BTN_WIDTH + 2, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedMaterial(Material.OTHER);
			}
		});
		btnOther.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		materialsBox.getChildren().add(btnOther);
		
		column++;
		getContents().add(lblMaterial, column++, row);
		getContents().add(materialsBox, column++, row, 6, 1);
		
		spacer = new Region();
		spacer.setPrefWidth(10);
		
		column = 4; row = 0;
		getContents().add(spacer, column++, row);
		
		column = 5;
		lblWorkPieceWeight = new Label(Translator.getTranslation(WEIGHT));
		getContents().add(lblWorkPieceWeight, column++, row);
		ntxtWorkPieceWeight = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtWorkPieceWeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWeight.setMaxSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtWorkPieceWeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedWeight(newValue);
			}
		});
		getContents().add(ntxtWorkPieceWeight, column++, row);
		btnCalc = createButton(Translator.getTranslation(CALC), UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().recalcWeight();
			}
		});
		getContents().add(btnCalc, column++, row);
		
		hideNotification();
		
		
		refresh();
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		ntxtWorkPieceHeight.setFocusListener(listener);
		ntxtWorkPieceLength.setFocusListener(listener);
		ntxtWorkPieceWeight.setFocusListener(listener);
		ntxtWorkPieceWidth.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		setDimensions((RectangularDimensions) settings.getRawWorkPiece().getDimensions());
		setWeight(settings.getRawWorkPiece().getMaterial(), settings.getRawWorkPiece().getWeight());
	}
	
	private void setDimensions(final RectangularDimensions workPieceDimensions) {
		float width = workPieceDimensions.getWidth();
		if (width > 0) {
			ntxtWorkPieceWidth.setText("" + width);
		} else {
			ntxtWorkPieceWidth.setText("");
		}
		float length = workPieceDimensions.getLength();
		if (length > 0) {
			ntxtWorkPieceLength.setText("" + length);
		} else {
			ntxtWorkPieceLength.setText("");
		}
		float height = workPieceDimensions.getHeight();
		if (height > 0) {
			ntxtWorkPieceHeight.setText("" + height);
		} else {
			ntxtWorkPieceHeight.setText("");
		}
	}
	
	public void setWeight(final WorkPiece.Material material, final float weight) {
		ntxtWorkPieceWeight.setText("" + weight);
		ntxtWorkPieceWeight.cleanText();
		btnAl.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnCu.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnFe.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnOther.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		if (material.equals(Material.OTHER)) {
			btnOther.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			btnCalc.setDisable(true);
		} else {
			btnCalc.setDisable(false);
			if (material.equals(Material.AL)) {
				btnAl.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			} else if (material.equals(Material.CU)) {
				btnCu.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			} else if (material.equals(Material.FE)) {
				btnFe.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			}
		}
	}

}
