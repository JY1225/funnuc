package eu.robojob.millassist.ui.admin.device.cnc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.ui.controls.CoordinateBox;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.util.Translator;

public class CNCMachineOptionView extends GridPane {
	
	private Label lblAirblow, lblTIM;
	private CheckBox cbTIMAllowed;
	private CoordinateBox bottomCoord, topCoord;
	
	private Label lblNbFixtures;
	private IntegerTextField itxtNbFix;
		
	private static final String TIM_ALLOWED = "CNCMachineGeneralView.TIMAllowed";
	private static final String AIRBLOW_BOUND = "CNCMachineOptionView.airblowBound";
	private static final String MAX_FIX = "CNCMachineGeneralView.maxFix";

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
		setPadding(new Insets(60,0,0,60));
		
		addActionListeners();
		
		int column = 0; int row = 0;
		add(cbTIMAllowed, column++, row);
		add(lblTIM, column, row);
		
		column = 0; row++;
		add(lblAirblow, column, row++,3,1);
		add(bottomCoord, column, row++,6,1);
		add(topCoord, column, row,6,1);
		
		column = 0; row++;
		add(lblNbFixtures, column++,row,1,1);
		add(itxtNbFix, column, row);
		
		airblowActive();
	}
	
	private void initComponents() {
		cbTIMAllowed = new CheckBox();
		lblTIM = new Label(Translator.getTranslation(TIM_ALLOWED));
		lblAirblow = new Label(Translator.getTranslation(AIRBLOW_BOUND));
		bottomCoord = new CoordinateBox(6, "X", "Y", "Z");
		topCoord = new CoordinateBox(6, "X", "Y", "Z");
		lblNbFixtures = new Label(Translator.getTranslation(MAX_FIX));
		itxtNbFix = new IntegerTextField(1);
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
	}
	
	public void refresh(final AbstractCNCMachine cncMachine) {
		cbTIMAllowed.setSelected(cncMachine.getTIMAllowed());
		itxtNbFix.setText("" + cncMachine.getNbFixtures());
		AirblowSquare airblowBound = cncMachine.getZones().iterator().next().getBoundaries();
		if (airblowBound != null) {
			topCoord.setCoordinate(airblowBound.getTopCoord());
			topCoord.reset();
			bottomCoord.setCoordinate(airblowBound.getBottomCoord());
			bottomCoord.reset();
		} 
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		bottomCoord.setTextFieldListener(listener);
		topCoord.setTextFieldListener(listener);
		itxtNbFix.setFocusListener(listener);
	}

	public boolean getTIMAllowed() {
		return cbTIMAllowed.isSelected();
	}
	
	public int getNbFixtures() {
		return Integer.parseInt(itxtNbFix.getText());
	}
	
	public AirblowSquare getAirblowBound() {
		return new AirblowSquare(bottomCoord.getCoordinate(), topCoord.getCoordinate());
	}
	
	private void airblowActive() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("settings.properties")));
			if ((properties.get("robot-airblow") != null) && (properties.get("robot-airblow").equals("false"))) {
				topCoord.setDisable(true);
				bottomCoord.setDisable(true);
			}
		} catch (IOException e) {

		}
	}
}
