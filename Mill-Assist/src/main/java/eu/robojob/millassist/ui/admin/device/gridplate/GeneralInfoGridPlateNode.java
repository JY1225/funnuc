package eu.robojob.millassist.ui.admin.device.gridplate;

import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.ui.admin.device.GridPlateConfigureView2;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class GeneralInfoGridPlateNode extends GridPane {
	
	private static final String LENGTH = "GeneralInfoGridPlateNode.length";
	private static final String WIDTH = "GeneralInfoGridPlateNode.width";
	private static final String HEIGHT = "GeneralInfoGridPlateNode.height";
	private static final String DIM_PLATE = "GeneralInfoGridPlateNode.dimPlate";
	private static final String DIM_HOLES = "GeneralInfoGridPlateNode.dimHoles";
	private static final String ORIGIN = "GeneralInfoGridPlateNode.origin";
	private static final String OFFSET_ORIGIN_X = "GeneralInfoGridPlateNode.offsetOriginX";
	private static final String OFFSET_ORIGIN_Y = "GeneralInfoGridPlateNode.offsetOriginY";
	private static final String NAME = "GridPlateConfigureView.name";
	private static final String PREVIEW = "GeneralInfoGridPlateNode.preview";
	
	private Label lblName;
	private FullTextField tfName;
	private Label lblDimPlate, lblLength, lblWidth, lblHeight;
	private NumericTextField numtxtLength, numtxtWidth, numtxtHeight;
	private Label lblOrigin, lblOriginX, lblOriginY;
	private NumericTextField numtxtOriginX, numtxtOriginY;
	private Label lblDimHoles, lblHoleLength, lblHoleWidth;
	private NumericTextField numtxtHoleLength, numtxtHoleWidth;
	private Button previewButton;
	
	private GridPlateConfigureView2 parent;
		
	public GeneralInfoGridPlateNode() {
		initComponents();
		buildComponents();
		addChangeListeners();
	}
	
	private void initComponents() {
		setVgap(10);
		setHgap(15);
		lblName = new Label(Translator.getTranslation(NAME));
		tfName = new FullTextField(25);
		tfName.setMaxWidth(UIConstants.TEXT_FIELD_HEIGHT*5.5);
		tfName.setPrefWidth(UIConstants.TEXT_FIELD_HEIGHT*5.5);
		tfName.setMinWidth(UIConstants.TEXT_FIELD_HEIGHT*5.5);
		lblLength = new Label(Translator.getTranslation(LENGTH));
		lblWidth = new Label(Translator.getTranslation(WIDTH));
		lblHeight = new Label(Translator.getTranslation(HEIGHT));
		numtxtLength = new NumericTextField(5);
		numtxtWidth = new NumericTextField(5);
		numtxtHeight = new NumericTextField(5);
		lblDimPlate = new Label(Translator.getTranslation(DIM_PLATE));
		lblHoleLength = new Label(Translator.getTranslation(LENGTH));
		lblHoleWidth = new Label(Translator.getTranslation(WIDTH));
		lblDimHoles = new Label(Translator.getTranslation(DIM_HOLES));
		numtxtHoleLength = new NumericTextField(5);
		numtxtHoleWidth = new NumericTextField(5);
		lblOrigin = new Label(Translator.getTranslation(ORIGIN));
		lblOriginX = new Label(Translator.getTranslation(OFFSET_ORIGIN_X));
		lblOriginY = new Label(Translator.getTranslation(OFFSET_ORIGIN_Y));
		numtxtOriginX = new NumericTextField(5);
		numtxtOriginY = new NumericTextField(5);
		previewButton = AbstractFormView.createButton(Translator.getTranslation(PREVIEW), UIConstants.TEXT_FIELD_HEIGHT*6.55, 
				UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				parent.showGridPlate();
			}
		});
		setPadding(new Insets(10, 50, 10, 20));
	}
	
	private void buildComponents() {
		int column = 0; int row = 0;
		add(lblName, column, row, 4,1);
		row++; column++;
		add(tfName, column, row, 3, 1);
		row++; column = 0;
		add(lblDimPlate, column, row, 4, 1);
		row++;
		add(lblLength, column++, row);
		add(numtxtLength, column++, row);
		add(lblWidth, column++, row);
		add(numtxtWidth, column, row);
		column = 0; row++;
		add(lblHeight, column++, row);
		add(numtxtHeight, column, row);
		column = 0; row++;
		add(lblDimHoles, column, row, 4,1);
		column = 0; row++;
		add(lblHoleLength, column++, row);
		add(numtxtHoleLength, column++, row);
		add(lblHoleWidth, column++, row);
		add(numtxtHoleWidth, column, row);
		column = 0; row++;
		add(lblOrigin, column, row, 4, 1);
		column = 0; row++;
		add(lblOriginX, column++, row);
		add(numtxtOriginX, column++, row);
		add(lblOriginY, column++, row);
		add(numtxtOriginY, column, row);
		column = 0; row++;
		add(previewButton, column, row, 4,1);
	}
	
	private void addChangeListeners() {
		tfName.setOnChange(new ChangeListener<String>() {	
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {			
				validate();
			}
		});
		numtxtLength.setOnChange(new ChangeListener<Float>() {

			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				validate();
			}
		});
		numtxtWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				validate();
			}
		});
		numtxtHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				validate();
			}
		});
		numtxtHoleLength.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				validate();
			}
		});
		numtxtHoleWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				validate();
			}
		});
		numtxtOriginX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				validate();
			}
		});
		numtxtOriginY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				validate();
			}
		});
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		numtxtLength.setFocusListener(listener);
		numtxtWidth.setFocusListener(listener);
		numtxtHeight.setFocusListener(listener);
		numtxtHoleLength.setFocusListener(listener);
		numtxtHoleWidth.setFocusListener(listener);
		numtxtOriginX.setFocusListener(listener);
		numtxtOriginY.setFocusListener(listener);
		tfName.setFocusListener(listener);
	}
	
	public String getName() {
		return tfName.getText();
	}
	
	public float getPlateWidth() {
		return numtxtLength.getValue();
	}
	
	public float getPlateHeight() {
		return numtxtWidth.getValue();
	}
	
	public float getPlateDepth() {
		return numtxtHeight.getValue();
	}
	
	public float getHoleLength() {
		return numtxtHoleLength.getValue();
	}
	
	public float getHoleWidth() {
		return numtxtHoleWidth.getValue();
	}
	
	public float getOffsetX() {
		return numtxtOriginX.getValue();
	}
	
	public float getOffsetY() {
		return numtxtOriginY.getValue();
	}
	
	public void setParent(GridPlateConfigureView2 parent) {
		this.parent = parent;
	}
	
	public boolean isConfigured() {
		return (!tfName.getText().equals("") &&
				numtxtLength.getValue() > 0  &&
				numtxtWidth.getValue()  > 0  &&
				numtxtHeight.getValue() > 0  &&
				numtxtHoleLength.getValue() > 0 &&
				numtxtHoleWidth.getValue() > 0 &&
				!numtxtOriginX.getText().equals("") &&
				!numtxtOriginY.getText().equals(""));
	}
	
	private void validate() {
		if (isConfigured()) {
			previewButton.setDisable(false);
			parent.validate(true);
		} else { 
			previewButton.setDisable(true);
			parent.validate(false);
		}
	}
	
	public void refresh() {
		validate();
	}

	public void setGridPlate(GridPlate selectedGridPlate) {
		numtxtLength.setText("" + selectedGridPlate.getWidth());
		numtxtWidth.setText("" + selectedGridPlate.getHeight());
		numtxtHeight.setText("" + selectedGridPlate.getDepth());
		numtxtHoleLength.setText("" + selectedGridPlate.getHoleLength());
		numtxtHoleWidth.setText("" + selectedGridPlate.getHoleWidth());
		numtxtOriginX.setText("" + selectedGridPlate.getOffsetX());
		numtxtOriginY.setText("" + selectedGridPlate.getOffsetY());
		tfName.setText("" + selectedGridPlate.getName());
		validate();
	}
	
	public void reset() {
		numtxtLength.setText("");
		numtxtWidth.setText("");
		numtxtHeight.setText("");
		numtxtHoleLength.setText("");
		numtxtHoleWidth.setText("");
		numtxtOriginX.setText("");
		numtxtOriginY.setText("");
		tfName.setText("");
		previewButton.setDisable(true);
	}
}
