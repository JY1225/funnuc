package eu.robojob.millassist.ui.controls.mcode;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.util.UIConstants;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class MCodeNode extends GridPane {
	
	private CheckBox[] cbGmcList;
	private CheckBox cbGmcRsa;
	private FullTextField fullTxtGmc;
	private Label lblGmc;
	private int GMCIndex;
	
	private static final String gmcString = "GMC";
	public static final int NB_COLUMNS = 10;
	
	public MCodeNode(final int GMCIndex) {
		this.GMCIndex = GMCIndex;
		initComponents();
		build();
	}

	private void initComponents() {
		cbGmcList = new CheckBox[5];
		lblGmc = new Label(gmcString + GMCIndex);
		for (int i = 0; i < cbGmcList.length; i++) {
			cbGmcList[i] = new CheckBox();
			cbGmcList[i].setSelected(true);
		}
		cbGmcRsa = new CheckBox();
		cbGmcRsa.setSelected(true);
		fullTxtGmc = new FullTextField(7);
		fullTxtGmc.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 3, UIConstants.TEXT_FIELD_HEIGHT);
	}
	
	private void build() {
		setVgap(10);
		setHgap(10);
		
		int column = 0;
		int row = 0;
		
		VBox vboxGmcNames = new VBox();
		vboxGmcNames.getChildren().addAll(lblGmc, fullTxtGmc);
		vboxGmcNames.setAlignment(Pos.CENTER);
		vboxGmcNames.setSpacing(5);
		
		add(vboxGmcNames, column++, row);
		for (CheckBox cbGmc: cbGmcList) {
			cbGmc.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
			cbGmc.setAlignment(Pos.CENTER);
			add(cbGmc, column++, row);
		}
		cbGmcRsa.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		cbGmcRsa.setAlignment(Pos.CENTER);
		add(cbGmcRsa, column++, row);
		
		for (Node node : getChildren()) {
			GridPane.setHalignment(node, HPos.CENTER);
			GridPane.setValignment(node, VPos.CENTER);
		}
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtGmc.setFocusListener(listener);
	}
	
	public void refresh(final MCodeAdapter mCodeAdapter) {
		if (mCodeAdapter != null) {
			fullTxtGmc.setText(mCodeAdapter.getGenericMCode(GMCIndex - 1).getName());
			for (int i = 0; i < cbGmcList.length; i++) {
				cbGmcList[i].selectedProperty().set(mCodeAdapter.getGenericMCode(GMCIndex - 1).getRobotServiceInputsRequired().contains(i));
			}
			cbGmcRsa.selectedProperty().set(mCodeAdapter.getGenericMCode(GMCIndex - 1).getRobotServiceOutputsUsed().contains(0));
		} else {
			fullTxtGmc.setText(lblGmc.getText());
			for (int i = 0; i < cbGmcList.length; i++) {
				cbGmcList[i].selectedProperty().set(true);
			}
			cbGmcRsa.selectedProperty().set(true);
		}
	}
	
	public Set<Integer> getMCodeRobotServiceInputs() {
		Set<Integer> robotServiceInputs = new HashSet<Integer>();
		for (int i = 0; i < cbGmcList.length; i++) {
			if (cbGmcList[i].selectedProperty().getValue()) {
				robotServiceInputs.add(i);
			}
		}
		return robotServiceInputs;
	}
	
	public Set<Integer> getMCodeRobotServiceOutputs() {
		Set<Integer> robotServiceInputs = new HashSet<Integer>();
		if (cbGmcRsa.selectedProperty().getValue()) {
			robotServiceInputs.add(0);
		}
		return robotServiceInputs;
	}
	
	public String getName() {
		return fullTxtGmc.getText();
	}
}
