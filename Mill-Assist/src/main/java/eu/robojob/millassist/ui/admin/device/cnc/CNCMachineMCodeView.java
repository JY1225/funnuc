package eu.robojob.millassist.ui.admin.device.cnc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.external.device.processing.cnc.mcode.GenericMCode;
import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.controls.mcode.MCodeNode;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class CNCMachineMCodeView extends GridPane {

	private static Label lblRs1, lblRs2, lblRs3, lblRs4, lblRs5, lblRsa;	
	private static FullTextField fullTxtRs1, fullTxtRs2, fullTxtRs3, fullTxtRs4, fullTxtRs5, fullTxtRsa;
	private static List<MCodeNode> gmcNodeList;
	private static Button btnAdd, btnDelete;
	private static Label lblAdd, lblDelete;
	private static GridPane spGrid;
	private static TextInputControlListener textListener;
	private static ScrollPane spDetails;
		
	private static final String BTN_DELETE = "CNCMachineMCodeView.delete";
	private static final String BTN_ADD = "CNCMachineMCodeView.add";
	
	private static final String CSS_CLASS_BUTTON = "form-button";
	private static final String CSS_CLASS_BUTTON_LABEL = "btn-start-label";
	
	public CNCMachineMCodeView() {
		setAlignment(Pos.TOP_CENTER);
		setVgap(10);
		setHgap(10);
		initComponents();
		build();
		addStyleSheets();
		addActionListeners();
	}
	
	private static void initComponents() {
		gmcNodeList = new ArrayList<MCodeNode>();
		lblRs1 = new Label("RS1");
		lblRs2 = new Label("RS2");
		lblRs3 = new Label("RS3");
		lblRs4 = new Label("RS4");
		lblRs5 = new Label("RS5");
		lblRsa = new Label("RSA");
		fullTxtRs1 = new FullTextField(5);
		fullTxtRs2 = new FullTextField(5);
		fullTxtRs3 = new FullTextField(5);
		fullTxtRs4 = new FullTextField(5);
		fullTxtRs5 = new FullTextField(5);
		fullTxtRsa = new FullTextField(5);
		btnAdd = new Button();
		lblAdd = new Label(Translator.getTranslation(BTN_ADD));
		btnAdd.setGraphic(lblAdd);
		lblDelete = new Label(Translator.getTranslation(BTN_DELETE));
		btnDelete = new Button(); 
		btnDelete.setGraphic(lblDelete);
		spDetails = new ScrollPane();
		spGrid = new GridPane();
	}

	private void build() {
		fullTxtRs1.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs2.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs3.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs4.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs5.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRsa.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 2, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtRs1.setAlignment(Pos.CENTER);
		fullTxtRs2.setAlignment(Pos.CENTER);
		fullTxtRs3.setAlignment(Pos.CENTER);
		fullTxtRs4.setAlignment(Pos.CENTER);
		fullTxtRs5.setAlignment(Pos.CENTER);
		fullTxtRsa.setAlignment(Pos.CENTER);

		int column = 0;
		int row = 0;
		Region region = new Region();
		region.setPrefSize(UIConstants.TEXT_FIELD_HEIGHT * 3, UIConstants.TEXT_FIELD_HEIGHT);
		add (region, column++, row);
		
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
		
		spDetails.setContent(spGrid);
		spGrid.setAlignment(Pos.TOP_CENTER);
		spGrid.setVgap(10);
		spGrid.setHgap(10);
		spDetails.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spDetails.setHbarPolicy(ScrollBarPolicy.NEVER);
		spDetails.setPannable(false);
		spDetails.setFitToHeight(false);
		spDetails.setFitToWidth(false);
		spDetails.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT*9);
		addNodesToScrollPane();
		add(spDetails, 0, 2, MCodeNode.NB_COLUMNS, 6);
		row = row + 8;
		
		HBox btnBox = new HBox();
		btnBox.setSpacing(20);
		btnBox.setAlignment(Pos.CENTER);
		btnAdd.setPrefSize(UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT);
		btnDelete.setPrefSize(UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT);
		btnBox.getChildren().addAll(btnAdd, btnDelete);
		add(btnBox, 0, row, MCodeNode.NB_COLUMNS, 1);
	}
	
	private static void addNodesToScrollPane() {
		//Reset content
		spGrid.getChildren().removeAll(spGrid.getChildren());
		int column = 0; int row = 0;
		for (MCodeNode mcodeNode: gmcNodeList) {
			spGrid.add(mcodeNode, column, row++, MCodeNode.NB_COLUMNS, 2);
			row++;
		}
	}
	
	private static void addStyleSheets() {
		btnAdd.getStyleClass().add(CSS_CLASS_BUTTON);
		lblAdd.getStyleClass().add(CSS_CLASS_BUTTON_LABEL);
		btnDelete.getStyleClass().add(CSS_CLASS_BUTTON);
		lblDelete.getStyleClass().add(CSS_CLASS_BUTTON_LABEL);
	}
	
	static void buildMCodeNodes(final MCodeAdapter mCodeAdapter) {
		if (mCodeAdapter != null) {
			for (GenericMCode mCode: mCodeAdapter.getGenericMCodes()) {
				gmcNodeList.add(new MCodeNode(mCode.getIndex() + 1));
			}
		}
		addNodesToScrollPane();
	}
	
	private static void addActionListeners() {
		btnAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MCodeNode newMCode = new MCodeNode(gmcNodeList.size() + 1);
				newMCode.setTextFieldListener(textListener);
				gmcNodeList.add(newMCode);
				addNodesToScrollPane();
			}
		});
		btnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gmcNodeList.remove(gmcNodeList.size() - 1);
				addNodesToScrollPane();
			}
		});
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		textListener = listener;
		for (MCodeNode mcodeNode: gmcNodeList) {
			mcodeNode.setTextFieldListener(textListener);
		}
		fullTxtRs1.setFocusListener(textListener);
		fullTxtRs2.setFocusListener(textListener);
		fullTxtRs3.setFocusListener(textListener);
		fullTxtRs4.setFocusListener(textListener);
		fullTxtRs5.setFocusListener(textListener);
		fullTxtRsa.setFocusListener(textListener);
	}
	
	public void refresh(final MCodeAdapter mCodeAdapter) {
		if (mCodeAdapter != null) {
			fullTxtRs1.setText(mCodeAdapter.getRobotServiceInputNames().get(0));
			fullTxtRs2.setText(mCodeAdapter.getRobotServiceInputNames().get(1));
			fullTxtRs3.setText(mCodeAdapter.getRobotServiceInputNames().get(2));
			fullTxtRs4.setText(mCodeAdapter.getRobotServiceInputNames().get(3));
			fullTxtRs5.setText(mCodeAdapter.getRobotServiceInputNames().get(4));
			fullTxtRsa.setText(mCodeAdapter.getRobotServiceOutputNames().get(0));
		} else {
			fullTxtRs1.setText("RS1");
			fullTxtRs2.setText("RS2");
			fullTxtRs3.setText("RS3");
			fullTxtRs4.setText("RS4");
			fullTxtRs5.setText("RS5");
			fullTxtRsa.setText("RSA");
		}
		for (MCodeNode mcodeNode: gmcNodeList) {
			mcodeNode.refresh(mCodeAdapter);
		}
	}
	
	public static List<String> getMCodeNames() {
		List<String> mCodeNames = new ArrayList<String>();
		for (MCodeNode mcodeNode: gmcNodeList) {
			mCodeNames.add(mcodeNode.getName());
		}
		return mCodeNames;
	}
	
	public static List<Set<Integer>> getMCodeRobotServiceInputs() {
		List<Set<Integer>> robotServiceInputs = new ArrayList<Set<Integer>>();
		for (MCodeNode mcodeNode: gmcNodeList) {
			robotServiceInputs.add(mcodeNode.getMCodeRobotServiceInputs());
		}
		return robotServiceInputs;
	}
	
	public static List<Set<Integer>> getMCodeRobotServiceOutputs() {
		List<Set<Integer>> robotServiceOutputs = new ArrayList<Set<Integer>>();
		for (MCodeNode mcodeNode: gmcNodeList) {
			robotServiceOutputs.add(mcodeNode.getMCodeRobotServiceOutputs());
		}
		return robotServiceOutputs;
	}
	
	public static List<String> getRobotServiceInputNames() {
		List<String> robotServiceInputNames = new ArrayList<String>();
		robotServiceInputNames.add(fullTxtRs1.getText());
		robotServiceInputNames.add(fullTxtRs2.getText());
		robotServiceInputNames.add(fullTxtRs3.getText());
		robotServiceInputNames.add(fullTxtRs4.getText());
		robotServiceInputNames.add(fullTxtRs5.getText());
		return robotServiceInputNames;
	}
	
	public static List<String> getRobotServiceOutputNames() {
		List<String> robotServiceOutputNames = new ArrayList<String>();
		robotServiceOutputNames.add(fullTxtRsa.getText());
		return robotServiceOutputNames;
	}
}
