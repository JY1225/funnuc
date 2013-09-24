package eu.robojob.millassist.ui.configure.device.processing.cnc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class CNCMillingMachinePutView extends AbstractFormView<CNCMillingMachinePutPresenter> {

	private PutStep putStep;
	private DeviceSettings deviceSettings;
	
	private Label lblSmoothInfo;
	
	private HBox hBoxSmoothPoint;
	
	private Label lblSmoothX;
	private Label lblSmoothY;
	private Label lblSmoothZ;
	
	private Button btnResetSmooth;
	
	private Label lblReleasePieceBeforeClamp;
	private Button btnBeforeClamp;
	private Button btnAfterClamp;
	
	private NumericTextField ntxtSmoothX;
	private NumericTextField ntxtSmoothY;
	private NumericTextField ntxtSmoothZ;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final int MAX_INTEGER_LENGTH = 6;
	
	private Label lblAirblow;
	private CheckBox cbAirblow;
	
	private static final String SMOOTH_PUT_INFO = "CNCMillingMachinePutView.smoothPickInfo";
	private static final String SMOOTH_X = "CNCMillingMachinePutView.smoothX";
	private static final String SMOOTH_Y = "CNCMillingMachinePutView.smoothY";
	private static final String SMOOTH_Z = "CNCMillingMachinePutView.smoothZ";
	private static final String SMOOTH_RESET = "CNCMillingMachinePutView.resetSmooth";
	private static final String AIRBLOW = "CNCMillingMachinePutView.airblow";
	private static final String ROBOT_RELEASES = "CNCMillingMachinePutView.robotReleases";
	private static final String AFTER_CLAMP = "CNCMillingMachinePutView.afterClamp";
	private static final String BEFORE_CLAMP = "CNCMillingMachinePutView.beforeClamp";
	
	private static final String CSS_CLASS_CENTER_TEXT = "center-text";
	
	public CNCMillingMachinePutView() {
		super();
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
	}
	
	public void setPutStep(final PutStep putStep) {
		this.putStep = putStep;
	}
	
	public void setDeviceSettings(final DeviceSettings deviceSettings) {
		this.deviceSettings = deviceSettings;
	}
	
	@Override
	protected void build() {
		lblSmoothInfo = new Label(Translator.getTranslation(SMOOTH_PUT_INFO));
		
		lblSmoothX = new Label(Translator.getTranslation(SMOOTH_X));
		lblSmoothY = new Label(Translator.getTranslation(SMOOTH_Y));
		lblSmoothZ = new Label(Translator.getTranslation(SMOOTH_Z));
		
		ntxtSmoothX = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtSmoothX.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtSmoothX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedSmoothX(newValue);
			}
		});
		ntxtSmoothY = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtSmoothY.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtSmoothY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedSmoothY(newValue);
			}
		});
		ntxtSmoothZ = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtSmoothZ.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtSmoothZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedSmoothZ(newValue);
			}
		});
		
		btnResetSmooth = new Button();
		Text txtBtnResetSmooth = new Text(Translator.getTranslation(SMOOTH_RESET));
		txtBtnResetSmooth.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_LABEL, CSS_CLASS_CENTER_TEXT);
		btnResetSmooth.setGraphic(txtBtnResetSmooth);
		btnResetSmooth.setAlignment(Pos.CENTER);
		btnResetSmooth.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().resetSmooth();
			}
		});
		btnResetSmooth.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
		btnResetSmooth.setPrefSize(UIConstants.BUTTON_HEIGHT * 1.5, UIConstants.BUTTON_HEIGHT);
		
		hBoxSmoothPoint = new HBox();
		hBoxSmoothPoint.getChildren().addAll(lblSmoothX, ntxtSmoothX, lblSmoothY, ntxtSmoothY, lblSmoothZ, ntxtSmoothZ, btnResetSmooth);
		HBox.setMargin(ntxtSmoothX, new Insets(0, 20, 0, 10));
		HBox.setMargin(ntxtSmoothY, new Insets(0, 20, 0, 10));
		HBox.setMargin(ntxtSmoothZ, new Insets(0, 20, 0, 10));
		hBoxSmoothPoint.setFillHeight(false);
		hBoxSmoothPoint.setAlignment(Pos.CENTER_LEFT);
		
		HBox hboxAirblow = new HBox();
		lblAirblow = new Label(Translator.getTranslation(AIRBLOW));
		cbAirblow = new CheckBox();
		cbAirblow.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				getPresenter().changedAirblow(newValue);
			}
		});
		hboxAirblow.getChildren().addAll(lblAirblow, cbAirblow);
		hboxAirblow.setSpacing(10);
		
		lblReleasePieceBeforeClamp = new Label(Translator.getTranslation(ROBOT_RELEASES));
		
		HBox hboxReleaseButtons = new HBox();
		hboxReleaseButtons.getChildren().add(lblReleasePieceBeforeClamp);
		HBox.setMargin(lblReleasePieceBeforeClamp, new Insets(0, 10, 0, 0));
		hboxReleaseButtons.setAlignment(Pos.CENTER_LEFT);
		
		btnBeforeClamp = createButton(Translator.getTranslation(BEFORE_CLAMP), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedReleaseBefore(true);
			}
		});
		btnBeforeClamp.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		hboxReleaseButtons.getChildren().add(btnBeforeClamp);
		btnAfterClamp = createButton(Translator.getTranslation(AFTER_CLAMP), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedReleaseBefore(false);
			}
		});
		btnAfterClamp.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		hboxReleaseButtons.getChildren().add(btnAfterClamp);
		
		int column = 0;
		int row = 0;
		getContents().add(lblSmoothInfo, column++, row);
		
		column = 0;
		row++;
		getContents().add(hBoxSmoothPoint, column++, row);
		
		column = 0;
		row++;
		getContents().add(hboxReleaseButtons, column++, row);
			
		column = 0;
		row++;
		getContents().add(hboxAirblow, column++, row);
		
		refresh();
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		ntxtSmoothX.setFocusListener(listener);
		ntxtSmoothY.setFocusListener(listener);
		ntxtSmoothZ.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		if (putStep.getRobotSettings().getSmoothPoint() != null) {
			ntxtSmoothX.setText("" + putStep.getRobotSettings().getSmoothPoint().getX());
			ntxtSmoothY.setText("" + putStep.getRobotSettings().getSmoothPoint().getY());
			ntxtSmoothZ.setText("" + putStep.getRobotSettings().getSmoothPoint().getZ());
		}
		if (deviceSettings.getClamping(putStep.getDeviceSettings().getWorkArea()) == null) {
			btnResetSmooth.setDisable(true);
		} else {
			btnResetSmooth.setDisable(false);
		}
		if (putStep.getRobotSettings().isDoMachineAirblow()) {
			cbAirblow.setSelected(true);
		} else {
			cbAirblow.setSelected(false);
		}
		btnBeforeClamp.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnAfterClamp.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
		if (putStep.getRobotSettings().isReleaseBeforeMachine()) {
			btnBeforeClamp.getStyleClass().add(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
		} else {
			btnAfterClamp.getStyleClass().add(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
		}
	}

}
