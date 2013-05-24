package eu.robojob.millassist.ui.admin.device.cnc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.util.UIConstants;

public class CNCMachineMCodeView extends GridPane {

	private Label lblRs1;
	private Label lblRs2;
	private Label lblRs3;
	private Label lblRs4;
	private Label lblRs5;
	private Label lblRsa;
	private Label lblGmc1;
	private Label lblGmc2;	
	
	private FullTextField fullTxtRs1;
	private FullTextField fullTxtRs2;
	private FullTextField fullTxtRs3;
	private FullTextField fullTxtRs4;
	private FullTextField fullTxtRs5;
	private FullTextField fullTxtRsa;
	private FullTextField fullTxtGmc1;
	private FullTextField fullTxtGmc2;
	
	private CheckBox cbbGmc1Rs1;
	private CheckBox cbbGmc1Rs2;
	private CheckBox cbbGmc1Rs3;
	private CheckBox cbbGmc1Rs4;
	private CheckBox cbbGmc1Rs5;
	private CheckBox cbbGmc1Rsa;
	
	private CheckBox cbbGmc2Rs1;
	private CheckBox cbbGmc2Rs2;
	private CheckBox cbbGmc2Rs3;
	private CheckBox cbbGmc2Rs4;
	private CheckBox cbbGmc2Rs5;
	private CheckBox cbbGmc2Rsa;
	
	public CNCMachineMCodeView() {
		setAlignment(Pos.TOP_CENTER);
		setVgap(10);
		setHgap(10);
		build();
	}
	
	private void build() {
		lblRs1 = new Label("RS1");
		lblRs2 = new Label("RS2");
		lblRs3 = new Label("RS3");
		lblRs4 = new Label("RS4");
		lblRs5 = new Label("RS5");
		lblRsa = new Label("RSA");
		lblGmc1 = new Label("GMC1");
		lblGmc2 = new Label("GMC2");
		fullTxtRs1 = new FullTextField(5);
		fullTxtRs1.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs2 = new FullTextField(5);
		fullTxtRs2.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs3 = new FullTextField(5);
		fullTxtRs3.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs4 = new FullTextField(5);
		fullTxtRs4.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs5 = new FullTextField(5);
		fullTxtRs5.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRsa = new FullTextField(5);
		fullTxtRsa.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs1.setAlignment(Pos.CENTER);
		fullTxtRs2.setAlignment(Pos.CENTER);
		fullTxtRs3.setAlignment(Pos.CENTER);
		fullTxtRs4.setAlignment(Pos.CENTER);
		fullTxtRs5.setAlignment(Pos.CENTER);
		fullTxtRsa.setAlignment(Pos.CENTER);
		fullTxtGmc1 = new FullTextField(5);
		fullTxtGmc1.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtGmc2 = new FullTextField(5);
		fullTxtGmc2.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtGmc1.setAlignment(Pos.CENTER);
		fullTxtGmc2.setAlignment(Pos.CENTER);
		cbbGmc1Rs1 = new CheckBox();
		cbbGmc1Rs2 = new CheckBox();
		cbbGmc1Rs3 = new CheckBox();
		cbbGmc1Rs4 = new CheckBox();
		cbbGmc1Rs5 = new CheckBox();
		cbbGmc1Rsa = new CheckBox();
		cbbGmc2Rs1 = new CheckBox();
		cbbGmc2Rs2 = new CheckBox();
		cbbGmc2Rs3 = new CheckBox();
		cbbGmc2Rs4 = new CheckBox();
		cbbGmc2Rs5 = new CheckBox();
		cbbGmc2Rsa = new CheckBox();
		int column = 0;
		int row = 0;
		column++;
		VBox vboxRs1 = new VBox();
		vboxRs1.getChildren().addAll(lblRs1, fullTxtRs1);
		vboxRs1.setAlignment(Pos.CENTER);
		vboxRs1.setSpacing(5);
		add(vboxRs1, column++, row);
		VBox vboxRs2 = new VBox();
		vboxRs2.getChildren().addAll(lblRs2, fullTxtRs2);
		vboxRs2.setAlignment(Pos.CENTER);
		vboxRs2.setSpacing(5);
		add(vboxRs2, column++, row);
		VBox vboxRs3 = new VBox();
		vboxRs3.getChildren().addAll(lblRs3, fullTxtRs3);
		vboxRs3.setAlignment(Pos.CENTER);
		vboxRs3.setSpacing(5);
		add(vboxRs3, column++, row);
		VBox vboxRs4 = new VBox();
		vboxRs4.getChildren().addAll(lblRs4, fullTxtRs4);
		vboxRs4.setAlignment(Pos.CENTER);
		vboxRs4.setSpacing(5);
		add(vboxRs4, column++, row);
		VBox vboxRs5 = new VBox();
		vboxRs5.getChildren().addAll(lblRs5, fullTxtRs5);
		vboxRs5.setAlignment(Pos.CENTER);
		vboxRs5.setSpacing(5);
		add(vboxRs5, column++, row);
		VBox vboxRsa = new VBox();
		vboxRsa.getChildren().addAll(lblRsa, fullTxtRsa);
		vboxRsa.setAlignment(Pos.CENTER);
		vboxRsa.setSpacing(5);
		add(vboxRsa, column++, row);
		column = 0; row++;
		VBox vboxGmc1 = new VBox();
		vboxGmc1.getChildren().addAll(lblGmc1, fullTxtGmc1);
		vboxGmc1.setAlignment(Pos.CENTER);
		vboxGmc1.setSpacing(5);
		add(vboxGmc1, column++, row);
		add(cbbGmc1Rs1, column++, row);
		add(cbbGmc1Rs2, column++, row);
		add(cbbGmc1Rs3, column++, row);
		add(cbbGmc1Rs4, column++, row);
		add(cbbGmc1Rs5, column++, row);
		add(cbbGmc1Rsa, column++, row);
		column = 0; row++;
		VBox vboxGmc2 = new VBox();
		vboxGmc2.getChildren().addAll(lblGmc2, fullTxtGmc2);
		vboxGmc2.setAlignment(Pos.CENTER);
		vboxGmc2.setSpacing(5);
		add(vboxGmc2, column++, row);
		add(cbbGmc2Rs1, column++, row);
		add(cbbGmc2Rs2, column++, row);
		add(cbbGmc2Rs3, column++, row);
		add(cbbGmc2Rs4, column++, row);
		add(cbbGmc2Rs5, column++, row);
		add(cbbGmc2Rsa, column++, row);
		for (Node node : getChildren()) {
			GridPane.setHalignment(node, HPos.CENTER);
			GridPane.setValignment(node, VPos.CENTER);
		}
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtGmc1.setFocusListener(listener);
		fullTxtGmc2.setFocusListener(listener);
		fullTxtRs1.setFocusListener(listener);
		fullTxtRs2.setFocusListener(listener);
		fullTxtRs3.setFocusListener(listener);
		fullTxtRs4.setFocusListener(listener);
		fullTxtRs5.setFocusListener(listener);
		fullTxtRsa.setFocusListener(listener);
	}
	
	public void refresh(final MCodeAdapter mCodeAdapter) {
		if (mCodeAdapter != null) {
			fullTxtGmc1.setText(mCodeAdapter.getGenericMCode(0).getName());
			fullTxtGmc2.setText(mCodeAdapter.getGenericMCode(1).getName());
			fullTxtRs1.setText(mCodeAdapter.getRobotServiceInputNames().get(0));
			fullTxtRs2.setText(mCodeAdapter.getRobotServiceInputNames().get(1));
			fullTxtRs3.setText(mCodeAdapter.getRobotServiceInputNames().get(2));
			fullTxtRs4.setText(mCodeAdapter.getRobotServiceInputNames().get(3));
			fullTxtRs5.setText(mCodeAdapter.getRobotServiceInputNames().get(4));
			fullTxtRsa.setText(mCodeAdapter.getRobotServiceOutputNames().get(0));
			cbbGmc1Rs1.selectedProperty().set(mCodeAdapter.getGenericMCode(0).getRobotServiceInputsRequired().contains(0));
			cbbGmc1Rs2.selectedProperty().set(mCodeAdapter.getGenericMCode(0).getRobotServiceInputsRequired().contains(1));
			cbbGmc1Rs3.selectedProperty().set(mCodeAdapter.getGenericMCode(0).getRobotServiceInputsRequired().contains(2));
			cbbGmc1Rs4.selectedProperty().set(mCodeAdapter.getGenericMCode(0).getRobotServiceInputsRequired().contains(3));
			cbbGmc1Rs5.selectedProperty().set(mCodeAdapter.getGenericMCode(0).getRobotServiceInputsRequired().contains(4));
			cbbGmc1Rsa.selectedProperty().set(mCodeAdapter.getGenericMCode(0).getRobotServiceOutputsUsed().contains(0));
			cbbGmc2Rs1.selectedProperty().set(mCodeAdapter.getGenericMCode(1).getRobotServiceInputsRequired().contains(0));
			cbbGmc2Rs2.selectedProperty().set(mCodeAdapter.getGenericMCode(1).getRobotServiceInputsRequired().contains(1));
			cbbGmc2Rs3.selectedProperty().set(mCodeAdapter.getGenericMCode(1).getRobotServiceInputsRequired().contains(2));
			cbbGmc2Rs4.selectedProperty().set(mCodeAdapter.getGenericMCode(1).getRobotServiceInputsRequired().contains(3));
			cbbGmc2Rs5.selectedProperty().set(mCodeAdapter.getGenericMCode(1).getRobotServiceInputsRequired().contains(4));
			cbbGmc2Rsa.selectedProperty().set(mCodeAdapter.getGenericMCode(1).getRobotServiceOutputsUsed().contains(0));
		} else {
			fullTxtGmc1.setText("GMC1");
			fullTxtGmc2.setText("GMC2");
			fullTxtRs1.setText("RS1");
			fullTxtRs2.setText("RS2");
			fullTxtRs3.setText("RS3");
			fullTxtRs4.setText("RS4");
			fullTxtRs5.setText("RS5");
			fullTxtRsa.setText("RSA");
			cbbGmc1Rs1.selectedProperty().set(true);
			cbbGmc1Rs2.selectedProperty().set(false);
			cbbGmc1Rs3.selectedProperty().set(false);
			cbbGmc1Rs4.selectedProperty().set(false);
			cbbGmc1Rs5.selectedProperty().set(false);
			cbbGmc1Rsa.selectedProperty().set(true);
			cbbGmc2Rs1.selectedProperty().set(true);
			cbbGmc2Rs2.selectedProperty().set(true);
			cbbGmc2Rs3.selectedProperty().set(false);
			cbbGmc2Rs4.selectedProperty().set(false);
			cbbGmc2Rs5.selectedProperty().set(false);
			cbbGmc2Rsa.selectedProperty().set(true);
		}
	}
	
	public List<String> getMCodeNames() {
		List<String> mCodeNames = new ArrayList<String>();
		mCodeNames.add(fullTxtGmc1.getText());
		mCodeNames.add(fullTxtGmc2.getText());
		return mCodeNames;
	}
	
	public List<Set<Integer>> getMCodeRobotServiceInputs() {
		List<Set<Integer>> robotServiceInputs = new ArrayList<Set<Integer>>();
		Set<Integer> robotServiceInputs1 = new HashSet<Integer>();
		if (cbbGmc1Rs1.selectedProperty().getValue()) {
			robotServiceInputs1.add(0);
		}
		if (cbbGmc1Rs2.selectedProperty().getValue()) {
			robotServiceInputs1.add(1);
		}
		if (cbbGmc1Rs3.selectedProperty().getValue()) {
			robotServiceInputs1.add(2);
		}
		if (cbbGmc1Rs4.selectedProperty().getValue()) {
			robotServiceInputs1.add(3);
		}
		if (cbbGmc1Rs5.selectedProperty().getValue()) {
			robotServiceInputs1.add(4);
		}
		Set<Integer> robotServiceInputs2 = new HashSet<Integer>();
		if (cbbGmc2Rs1.selectedProperty().getValue()) {
			robotServiceInputs2.add(0);
		}
		if (cbbGmc2Rs2.selectedProperty().getValue()) {
			robotServiceInputs2.add(1);
		}
		if (cbbGmc2Rs3.selectedProperty().getValue()) {
			robotServiceInputs2.add(2);
		}
		if (cbbGmc2Rs4.selectedProperty().getValue()) {
			robotServiceInputs2.add(3);
		}
		if (cbbGmc2Rs5.selectedProperty().getValue()) {
			robotServiceInputs2.add(4);
		}
		robotServiceInputs.add(robotServiceInputs1);
		robotServiceInputs.add(robotServiceInputs2);
		return robotServiceInputs;
	}
	
	public List<Set<Integer>> getMCodeRobotServiceOutputs() {
		List<Set<Integer>> robotServiceOutputs = new ArrayList<Set<Integer>>();
		Set<Integer> robotServiceOutput1 = new HashSet<Integer>();
		if (cbbGmc1Rsa.selectedProperty().getValue()) {
			robotServiceOutput1.add(0);
		}
		Set<Integer> robotServiceOutput2 = new HashSet<Integer>();
		if (cbbGmc2Rsa.selectedProperty().getValue()) {
			robotServiceOutput2.add(0);
		}
		robotServiceOutputs.add(robotServiceOutput1);
		robotServiceOutputs.add(robotServiceOutput2);
		return robotServiceOutputs;
	}
	
	public List<String> getRobotServiceInputNames() {
		List<String> robotServiceInputNames = new ArrayList<String>();
		robotServiceInputNames.add(fullTxtRs1.getText());
		robotServiceInputNames.add(fullTxtRs2.getText());
		robotServiceInputNames.add(fullTxtRs3.getText());
		robotServiceInputNames.add(fullTxtRs4.getText());
		robotServiceInputNames.add(fullTxtRs5.getText());
		return robotServiceInputNames;
	}
	
	public List<String> getRobotServiceOutputNames() {
		List<String> robotServiceOutputNames = new ArrayList<String>();
		robotServiceOutputNames.add(fullTxtRsa.getText());
		return robotServiceOutputNames;
	}
}
