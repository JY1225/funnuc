package eu.robojob.millassist.ui.admin.device.cnc;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import eu.robojob.millassist.external.device.WorkAreaBoundary;
import eu.robojob.millassist.external.device.WorkAreaManager;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.ui.controls.CoordinateBox;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.util.PropertyManager;
import eu.robojob.millassist.util.PropertyManager.Setting;
import eu.robojob.millassist.util.Translator;

public class CNCMachineOptionView extends GridPane {
	
	private Label lblAirblow, lblTIM, lblMachineAirblow, lblRRound, lblWorkNumberSearch, lblClampingPressureSelectable;
	private CheckBox cbTIMAllowed, cbMachineAirblow, cbWorkNumberSearch, cbClampingPressureSelectable;
	private CoordinateBox bottomCoord, topCoord;
	private ComboBox<WorkAreaBoundary> cbbWaBound;
	private NumericTextField ntxtRRound;
	
	private static final int COMBO_WIDTH = 150;
	private static final int COMBO_HEIGHT = 40;
	
	private CNCMachineConfigureView cncMachineConfigureView;
	
	private Label lblNbFixtures;
	private IntegerTextField itxtNbFix;
		
	private static final String TIM_ALLOWED = "CNCMachineGeneralView.TIMAllowed";
	private static final String MACHINE_AIRBLOW = "CNCMachineOptionView.machineAirblow";
	private static final String AIRBLOW_BOUND = "CNCMachineOptionView.airblowBound";
	private static final String WORKNUMBER_SEARCH = "CNCMachineOptionView.workNumberSearch";
	private static final String MAX_FIX = "CNCMachineGeneralView.maxFix";
	private static final String R_ROUND = "CNCMachineOptionView.rRound";
	private static final String CLAMPING_PRESSURE_SELECTABLE = "CNCMachineOptionView.clampingPressureSelectable";

	protected static final String CSS_CLASS_FORM_BUTTON_BAR_LEFT = "form-button-bar-left";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_RIGHT = "form-button-bar-right";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_CENTER = "form-button-bar-center";
	protected static final String CSS_CLASS_FORM_BUTTON_ACTIVE = "form-button-active";
	
	public CNCMachineOptionView() {
		build();
	}
	
	public void build() {
		initComponents();
		setVgap(15);
		setHgap(20);
		setAlignment(Pos.TOP_CENTER);
		setPadding(new Insets(40,0,0,60));
		
		addActionListeners();
		
		int column = 0; int row = 0;		
		column = 0; row++;
		
		GridPane boundGrid = new GridPane();
		boundGrid.add(lblAirblow, 0, 0);
		boundGrid.add(cbbWaBound, 1, 0);
		boundGrid.add(bottomCoord, 0, 1,2,1);
		boundGrid.add(topCoord, 0, 2,2,1);
		boundGrid.setHgap(20);
		boundGrid.setVgap(15);
		GridPane.setMargin(bottomCoord, new Insets(0,0,0,48));
		GridPane.setMargin(topCoord, new Insets(0,0,0,48));
		add(boundGrid, column, row, 2, 3);
		
		row = 3;
		column = 0; row++;
		HBox machineAirblowBox = new HBox();
		machineAirblowBox.getChildren().addAll(cbMachineAirblow, lblMachineAirblow);
		HBox.setMargin(cbMachineAirblow, new Insets(0,15,0,0));
		add(machineAirblowBox, column, row);
		column = 0; row++;
		HBox TIMBox = new HBox();
		TIMBox.getChildren().addAll(cbTIMAllowed, lblTIM);
		HBox.setMargin(cbTIMAllowed, new Insets(0,15,0,0));
		add(TIMBox, column, row, 2, 1);
		column = 0; row++;
		HBox workNumberSearchBox = new HBox();
		workNumberSearchBox.getChildren().addAll(cbWorkNumberSearch, lblWorkNumberSearch);
		HBox.setMargin(cbWorkNumberSearch, new Insets(0,15,0,0));
		add(workNumberSearchBox, column, row);
		column = 0; row++;
		HBox clampingPressureSelectableBox = new HBox();
		clampingPressureSelectableBox.getChildren().addAll(cbClampingPressureSelectable, lblClampingPressureSelectable);
        HBox.setMargin(cbClampingPressureSelectable, new Insets(0,15,0,0));
        add(clampingPressureSelectableBox, column, row);
        column = 0; row++;
		add(lblNbFixtures, column++, row);
		add(itxtNbFix, column, row);
		column = 0; row++;
		add(lblRRound, column++, row);
		add(ntxtRRound, column, row);
		airblowActive();
	}
	
	private void initComponents() {
		cbTIMAllowed = new CheckBox();
		lblTIM = new Label(Translator.getTranslation(TIM_ALLOWED));
		lblAirblow = new Label(Translator.getTranslation(AIRBLOW_BOUND));
		lblRRound = new Label(Translator.getTranslation(R_ROUND));
		ntxtRRound = new NumericTextField(6);
		cbMachineAirblow = new CheckBox();
		lblMachineAirblow = new Label(Translator.getTranslation(MACHINE_AIRBLOW));
		bottomCoord = new CoordinateBox(6, "X", "Y", "Z");
		topCoord = new CoordinateBox(6, "X", "Y", "Z");
		lblNbFixtures = new Label(Translator.getTranslation(MAX_FIX));
		itxtNbFix = new IntegerTextField(1);
		cbbWaBound = new ComboBox<WorkAreaBoundary>();
		cbbWaBound.setPrefSize(COMBO_WIDTH, COMBO_HEIGHT);
		cbWorkNumberSearch = new CheckBox();
		lblWorkNumberSearch = new Label(Translator.getTranslation(WORKNUMBER_SEARCH));
		cbClampingPressureSelectable = new CheckBox();
		lblClampingPressureSelectable = new Label(Translator.getTranslation(CLAMPING_PRESSURE_SELECTABLE));
	}
	
	private void addActionListeners() {
		bottomCoord.addChangeListeners(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observableValue, final Float oldValue, final Float newValue) {
				bottomCoord.updateCoordinate();
			}
		});
		topCoord.addChangeListeners(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observableValue, final Float oldValue, final Float newValue) {
				topCoord.updateCoordinate();
			}
		});
		cbTIMAllowed.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				cbTIMAllowed.setSelected(newValue);
			}
		});
		cbMachineAirblow.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				cbMachineAirblow.setSelected(newValue);
			}
		});
		cbbWaBound.valueProperty().addListener(new ChangeListener<WorkAreaBoundary>() {
			@Override
			public void changed(ObservableValue<? extends WorkAreaBoundary> observable,	WorkAreaBoundary oldValue, WorkAreaBoundary newValue) {
				if ((oldValue != null || newValue != null) && oldValue != newValue) {
					setBoundary();
				}
			}	
		});
		cbWorkNumberSearch.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
		        cbWorkNumberSearch.setSelected(newValue);
		    }
        });
		cbClampingPressureSelectable.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                cbClampingPressureSelectable.setSelected(newValue);
            }
        });
	}
	
	public void refresh(final AbstractCNCMachine cncMachine) {
		cbTIMAllowed.setSelected(cncMachine.getTIMAllowed());
		cbMachineAirblow.setSelected(cncMachine.getMachineAirblow());
		cbWorkNumberSearch.setSelected(cncMachine.hasWorkNumberSearch());
		cbClampingPressureSelectable.setSelected(cncMachine.isClampingPressureSelectable());
		itxtNbFix.setText("" + cncMachine.getNbFixtures());
		ntxtRRound.setText("" + cncMachine.getRRoundPieces());
		fillBoundBox();
		setBoundary();
	}
	
	private void fillBoundBox() {
		cbbWaBound.getItems().clear();
		for (WorkAreaManager wa: cncMachineConfigureView.getCNCMachine().getWorkAreaManagers()) {
			if (wa.getBoundaries() != null) {
				cbbWaBound.getItems().add(wa.getBoundaries());
			} else {
				WorkAreaBoundary bound = new WorkAreaBoundary(wa, new AirblowSquare());
				cbbWaBound.getItems().add(bound);
			}
		}
		cbbWaBound.getSelectionModel().selectFirst();
	}
	
	private void setBoundary() {
		try {
			AirblowSquare airblowBound = cbbWaBound.getSelectionModel().getSelectedItem().getBoundary();
			if (airblowBound != null) {
				topCoord.setCoordinate(airblowBound.getTopCoord());
				topCoord.reset();
				bottomCoord.setCoordinate(airblowBound.getBottomCoord());
				bottomCoord.reset();
			} 
		} catch (NullPointerException e) {
			//null if not yet initialized
		}
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		bottomCoord.setTextFieldListener(listener);
		topCoord.setTextFieldListener(listener);
		itxtNbFix.setFocusListener(listener);
		ntxtRRound.setFocusListener(listener);
	}

	public boolean getTIMAllowed() {
		return cbTIMAllowed.isSelected();
	}
	
	public boolean getMachineAirblow() {
		return cbMachineAirblow.isSelected();
	}
	
	public boolean hasWorkNumberSearch() {
	    return cbWorkNumberSearch.isSelected();
	}
	
	public boolean isClampingPressureSelectable() {
	    return cbClampingPressureSelectable.isSelected();
	}
	
	public int getNbFixtures() {
		return Integer.parseInt(itxtNbFix.getText());
	}
	
	public float getRRoundPieces() {
		return Float.parseFloat(ntxtRRound.getText());
	}
	
	public List<WorkAreaBoundary> getAirblowBounds() {
		return cbbWaBound.getItems();
	}
	
	private void airblowActive() {
		if (PropertyManager.hasSettingValue(Setting.AIRBLOW, "false")) {
			topCoord.setDisable(true);
			bottomCoord.setDisable(true);
		}
	}

	public void setConfigureView(CNCMachineConfigureView cncMachineConfigureView) {
		this.cncMachineConfigureView = cncMachineConfigureView;
	}
}
