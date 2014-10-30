package eu.robojob.millassist.ui.admin.device.cnc;

import java.util.List;
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
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.EWayOfOperating;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class CNCMachineConfigureView extends AbstractFormView<CNCMachineConfigurePresenter> {

	private StackPane spNav;
	private HBox hboxNavButtons;
	private Button btnGeneral;
	private Button btnMCodes;
	private Button btnSave;
	private Button activeButton;
	private StackPane contentPane;
	
	private static final String GENERAL = "CNCMachineConfigureView.general";
	private static final String M_CODES = "CNCMachineConfigureView.mCodes";
	private static final String SAVE_DIALOG = "CNCMachineConfigureView.saveDialog";
	private static final String ACTIVE_CHANGES = "CNCMachineConfigureView.activeChanges";
	private static final String CSS_CLASS_PADDING_BTN = "padding-button";
	private static final String CSS_CLASS_NAV_AREA = "nav-area";
		
	private static final String SAVE = "CNCMachineGeneralView.save";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final double WIDTH = 600;
	
	
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	
	private static final String CSS_CLASS_CNC_CONTENTPANE = "cnc-contentpane";
	
	private AbstractCNCMachine cncMachine;
	private Set<String> userFrames;
	
	private CNCMachineGeneralView cncMachineGeneralView;
	private CNCMachineMCodeView cncMachineMCodeView;
	
	public CNCMachineConfigureView() {
		this.cncMachineGeneralView = new CNCMachineGeneralView();
		this.cncMachineMCodeView = new CNCMachineMCodeView();
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
		getPresenter().updateUserFrames();
		if (cncMachine.getWayOfOperating() == EWayOfOperating.START_STOP) {
			btnMCodes.setDisable(true);
		} else if ((cncMachine.getWayOfOperating() == EWayOfOperating.M_CODES) || (cncMachine.getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			btnMCodes.setDisable(false);
		} else {
			throw new IllegalStateException("Unknown way of operating: " + cncMachine.getWayOfOperating());
		}
		cncMachineGeneralView.refresh(userFrames, cncMachine);
		cncMachineMCodeView.refresh(cncMachine.getMCodeAdapter());
	}
	
	public void refreshStatus() {
		cncMachineGeneralView.refreshStatus(cncMachine);
	}
	
	public void setMCodeActive(final boolean active) {
		btnMCodes.setDisable(!active);
	}
	
	@Override
	public void setPresenter(final CNCMachineConfigurePresenter presenter) {
		super.setPresenter(presenter);
		cncMachineGeneralView.setPresenter(presenter);
	}
	
	@Override
	protected void build() {
		setPrefWidth(600);
		setMinWidth(600);
		setMaxWidth(600);
		setAlignment(Pos.TOP_CENTER);
		spNav = new StackPane();
		spNav.getStyleClass().add(CSS_CLASS_NAV_AREA);
		hboxNavButtons = new HBox();
		btnGeneral = createButton(Translator.getTranslation(GENERAL), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {	
			@Override
			public void handle(final ActionEvent arg0) {
				contentPane.getChildren().clear();
				contentPane.getChildren().add(cncMachineGeneralView);
				setActiveButton(btnGeneral);
			}
		});
		btnGeneral.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_BAR_LEFT, CSS_CLASS_PADDING_BTN);
		btnMCodes = createButton(Translator.getTranslation(M_CODES), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				contentPane.getChildren().clear();
				contentPane.getChildren().add(cncMachineMCodeView);
				setActiveButton(btnMCodes);
			}
		});
		btnMCodes.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_BAR_RIGHT, CSS_CLASS_PADDING_BTN);
		hboxNavButtons.getChildren().addAll(btnGeneral, btnMCodes);
		hboxNavButtons.setPrefWidth(3 * UIConstants.BUTTON_HEIGHT * 2);
		hboxNavButtons.setMinWidth(3 * UIConstants.BUTTON_HEIGHT * 2);
		hboxNavButtons.setMaxWidth(3 * UIConstants.BUTTON_HEIGHT * 2);
		spNav.getChildren().add(hboxNavButtons);
		getContents().add(spNav, 0, 0);
		getContents().setAlignment(Pos.CENTER);
		btnSave = AbstractFormView.createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {	
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().saveData();
			}
		});
		spNav.getChildren().add(btnSave);
		StackPane.setMargin(hboxNavButtons, new Insets(0, 20, 0, 0));
		contentPane = new StackPane();
		contentPane.setPrefWidth(WIDTH);
		contentPane.setMinWidth(WIDTH);
		contentPane.setMaxWidth(WIDTH);
		spNav.setPrefWidth(WIDTH);
		spNav.setMinWidth(WIDTH);
		spNav.setMaxWidth(WIDTH);
		spNav.setPadding(new Insets(10, 10, 10, 10));
		StackPane.setAlignment(hboxNavButtons, Pos.CENTER);
		StackPane.setAlignment(btnSave, Pos.CENTER_RIGHT);
		
		getContents().add(contentPane, 0, 1, 2, 1);
		contentPane.getStyleClass().add(CSS_CLASS_CNC_CONTENTPANE);
		GridPane.setMargin(contentPane, new Insets(50, 0, 0, 0));
		GridPane.setVgrow(contentPane, Priority.ALWAYS);
		contentPane.setAlignment(Pos.CENTER);
		
		contentPane.getChildren().clear();
		contentPane.getChildren().add(cncMachineGeneralView);
		setActiveButton(btnGeneral);
	}
	
	public void showNotificationDialog() {
		ThreadManager.submit(new Thread() {
			@Override
			public void run() {
				getPresenter().showNotificationOverlay(Translator.getTranslation(SAVE_DIALOG), Translator.getTranslation(ACTIVE_CHANGES));
			}
		});
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
		cncMachineMCodeView.setTextFieldListener(listener);
	}
	
	public String getName() {
		return cncMachineGeneralView.getName();
	}
	
	public String getIp() {
		return cncMachineGeneralView.getIp();
	}
	
	public int getPort() {
		return cncMachineGeneralView.getPort();
	}
	
	public int getWidthR() {
		return cncMachineGeneralView.getWidthR();
	}
	
	public EWayOfOperating getWayOfOperating() {
		return cncMachineGeneralView.getWayOfOperating();
	}
	
	public boolean getNewDevInt() {
		return cncMachineGeneralView.getNewDevInt();
	}
	
	public void setNewDevInt(boolean isNewDevInt) {
		cncMachineGeneralView.setNewDevInt(isNewDevInt);
	}

	public List<String> getMCodeNames() {
		return cncMachineMCodeView.getMCodeNames();
	}
	
	public List<Set<Integer>> getMCodeRobotServiceInputs() {
		return cncMachineMCodeView.getMCodeRobotServiceInputs();
	}
	
	public List<Set<Integer>> getMCodeRobotServiceOutputs() {
		return cncMachineMCodeView.getMCodeRobotServiceOutputs();
	}
	
	public List<String> getRobotServiceInputNames() {
		return cncMachineMCodeView.getRobotServiceInputNames();
	}
	
	public List<String> getRobotServiceOutputNames() {
		return cncMachineMCodeView.getRobotServiceOutputNames();
	}

	public int getNbFixtures() {
		return cncMachineGeneralView.getNbFixtures();
	}

	public boolean getTIMAllowed() {
		return cncMachineGeneralView.getTIMAllowed();
	}
}