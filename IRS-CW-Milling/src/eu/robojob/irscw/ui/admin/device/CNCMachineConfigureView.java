package eu.robojob.irscw.ui.admin.device;

import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.ui.admin.device.cnc.CNCMachineGeneralView;
import eu.robojob.irscw.ui.admin.device.cnc.CNCMachinePartsOperationView;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class CNCMachineConfigureView extends AbstractFormView<CNCMachineConfigurePresenter> {

	private HBox hboxNavButtons;
	private Button btnGeneral;
	private Button btnPartsWorking;
	private Button btnCriteria;
	private Button btnClampConditions;
	private Button btnTimers;
	private Button btnMCodes;
	
	private Button activeButton;
	
	private StackPane contentPane;
	
	private static final String GENERAL = "CNCMachineConfigureView.general";
	private static final String PARTS_WORKING = "CNCMachineConfigureView.partsWorking";
	private static final String CRITERIA = "CNCMachineConfigureView.criteria";
	private static final String CLAMP_CONDITIONS = "CNCMachineConfigureView.clampConditions";
	private static final String TIMERS = "CNCMachineConfigureView.timers";
	private static final String M_CODES = "CNCMachineConfigureView.mCodes";
	private static final String CSS_CLASS_PADDING_BTN = "padding-button";
		
	private static final double WIDTH = 550;
	private static final double HEIGHT = 505;
	
	private static final String CSS_CLASS_CNC_CONTENTPANE = "cnc-contentpane";
	
	private AbstractCNCMachine cncMachine;
	private Set<String> userFrames;
	
	private CNCMachineGeneralView cncMachineGeneralView;
	private CNCMachinePartsOperationView cncMachinePartsOperationView;
	
	public CNCMachineConfigureView() {
		this.cncMachineGeneralView = new CNCMachineGeneralView();
		this.cncMachinePartsOperationView = new CNCMachinePartsOperationView();
	}
	
	public void setCNCMachine(final AbstractCNCMachine cncMachine) {
		this.cncMachine = cncMachine;
		refresh();
	}
	
	public void setUserFrameNames(final Set<String> userFrames) {
		this.userFrames = userFrames;
	}
	
	@Override
	public void refresh() {
		cncMachineGeneralView.refresh(userFrames, cncMachine);
	}
	
	@Override
	protected void build() {
		setPrefWidth(600);
		setMinWidth(600);
		setMaxWidth(600);
		setAlignment(Pos.TOP_CENTER);
		setPadding(new Insets(30, 0, 0, 0));
		hboxNavButtons = new HBox();
		btnGeneral = createButton(Translator.getTranslation(GENERAL), Button.USE_COMPUTED_SIZE, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {	
			@Override
			public void handle(final ActionEvent arg0) {
				contentPane.getChildren().clear();
				contentPane.getChildren().add(cncMachineGeneralView);
				setActiveButton(btnGeneral);
			}
		});
		btnGeneral.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_BAR_LEFT, CSS_CLASS_PADDING_BTN);
		btnPartsWorking = createButton(Translator.getTranslation(PARTS_WORKING), Button.USE_COMPUTED_SIZE, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				contentPane.getChildren().clear();
				contentPane.getChildren().add(cncMachinePartsOperationView);
				setActiveButton(btnPartsWorking);
			}
		});
		btnPartsWorking.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_BAR_CENTER, CSS_CLASS_PADDING_BTN);
		btnCriteria = createButton(Translator.getTranslation(CRITERIA), Button.USE_COMPUTED_SIZE, UIConstants.BUTTON_HEIGHT, null);
		btnCriteria.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_BAR_CENTER, CSS_CLASS_PADDING_BTN);
		btnClampConditions = createButton(Translator.getTranslation(CLAMP_CONDITIONS), Button.USE_COMPUTED_SIZE, UIConstants.BUTTON_HEIGHT, null);
		btnClampConditions.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_BAR_CENTER, CSS_CLASS_PADDING_BTN);
		btnTimers = createButton(Translator.getTranslation(TIMERS), Button.USE_COMPUTED_SIZE, UIConstants.BUTTON_HEIGHT, null);
		btnTimers.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_BAR_CENTER, CSS_CLASS_PADDING_BTN);
		btnMCodes = createButton(Translator.getTranslation(M_CODES), Button.USE_COMPUTED_SIZE, UIConstants.BUTTON_HEIGHT, null);
		btnMCodes.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_BAR_RIGHT, CSS_CLASS_PADDING_BTN);
		hboxNavButtons.getChildren().addAll(btnGeneral, btnPartsWorking, btnCriteria, btnClampConditions, btnTimers, btnMCodes);
		add(hboxNavButtons, 0, 0);

		contentPane = new StackPane();
		contentPane.setPrefWidth(WIDTH);
		contentPane.setMinWidth(WIDTH);
		contentPane.setMaxWidth(WIDTH);
		add(contentPane, 0, 1);
		contentPane.getStyleClass().add(CSS_CLASS_CNC_CONTENTPANE);
		GridPane.setMargin(contentPane, new Insets(20, 0, 0, 0));
		GridPane.setVgrow(contentPane, Priority.ALWAYS);
		contentPane.setAlignment(Pos.CENTER);
	}
	
	public void setActiveButton(final Button button) {
		if (activeButton != null) {
			activeButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		}
		this.activeButton = button;
		activeButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
	}
	
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		cncMachineGeneralView.setTextFieldListener(listener);
	}

}
