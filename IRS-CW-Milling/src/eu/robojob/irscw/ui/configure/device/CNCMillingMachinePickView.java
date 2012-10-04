package eu.robojob.irscw.ui.configure.device;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.util.UIConstants;

public class CNCMillingMachinePickView extends AbstractFormView<CNCMillingMachinePickPresenter> {

	private PickStep pickStep;
	private CNCMillingMachineSettings deviceSettings;

	private Label lblSmoothInfo;
	
	private HBox hBoxSmoothPoint;
	private HBox hboxHeight;
	
	private Label lblSmoothX;
	private Label lblSmoothY;
	private Label lblSmoothZ;
	
	private Button btnResetSmooth;
	
	private NumericTextField ntxtSmoothX;
	private NumericTextField ntxtSmoothY;
	private NumericTextField ntxtSmoothZ;
	
	private Label lblHeight;
	private NumericTextField ntxtHeight;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	public CNCMillingMachinePickView() {
		super();
		setHgap(HGAP);
		setVgap(VGAP);
	}
	
	@Override
	protected void build() {
		lblSmoothInfo = new Label(translator.getTranslation("smoothPickInfo"));
		
		lblSmoothX = new Label(translator.getTranslation("smoothX"));
		lblSmoothY = new Label(translator.getTranslation("smoothY"));
		lblSmoothZ = new Label(translator.getTranslation("smoothZ"));
		
		ntxtSmoothX = new NumericTextField(6);
		ntxtSmoothX.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtSmoothX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				presenter.changedSmoothX(newValue);
			}
		});
		ntxtSmoothY = new NumericTextField(6);
		ntxtSmoothY.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtSmoothY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				presenter.changedSmoothY(newValue);
			}
		});
		ntxtSmoothZ = new NumericTextField(6);
		ntxtSmoothZ.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtSmoothZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> observable,
					Float oldValue, Float newValue) {
				presenter.changedSmoothZ(newValue);
			}
		});
		
		btnResetSmooth = new Button();
		Text txtBtnResetSmooth = new Text(translator.getTranslation("resetSmooth"));
		txtBtnResetSmooth.getStyleClass().addAll("form-button-label", "center-text");
		btnResetSmooth.setGraphic(txtBtnResetSmooth);
		btnResetSmooth.setAlignment(Pos.CENTER);
		btnResetSmooth.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				presenter.resetSmooth();
			}
		});
		btnResetSmooth.getStyleClass().add("form-button");
		btnResetSmooth.setPrefSize(UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT);
		
		lblHeight = new Label(translator.getTranslation("height"));
		
		ntxtHeight = new NumericTextField(6);
		ntxtHeight.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(ObservableValue<? extends Float> overvable, Float oldValue, Float newValue) {
				presenter.changedHeight(newValue);
			}
		});
		
		hBoxSmoothPoint = new HBox();
		hBoxSmoothPoint.getChildren().addAll(lblSmoothX, ntxtSmoothX, lblSmoothY, ntxtSmoothY, lblSmoothZ, ntxtSmoothZ, btnResetSmooth);
		HBox.setMargin(ntxtSmoothX, new Insets(0, 20, 0, 0));
		HBox.setMargin(ntxtSmoothY, new Insets(0, 20, 0, 0));
		HBox.setMargin(ntxtSmoothZ, new Insets(0, 20, 0, 0));
		hBoxSmoothPoint.setFillHeight(false);
		hBoxSmoothPoint.setAlignment(Pos.CENTER_LEFT);
		
		hboxHeight = new HBox();
		hboxHeight.getChildren().addAll(lblHeight, ntxtHeight);
		hboxHeight.setAlignment(Pos.CENTER_LEFT);
		hboxHeight.setFillHeight(false);
		hboxHeight.setPadding(new Insets(15, 0, 0, 0));
		
		int column = 0;
		int row = 0;
		add(lblSmoothInfo, column++, row);
		
		column = 0;
		row++;
		add(hBoxSmoothPoint, column++, row);
				
		column = 0;
		row++;
		add(hboxHeight, column++, row);
		
		refresh();
	}

	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		ntxtSmoothX.setFocusListener(listener);
		ntxtSmoothY.setFocusListener(listener);
		ntxtSmoothZ.setFocusListener(listener);
		ntxtHeight.setFocusListener(listener);
	}
	
	public void setPickStep(PickStep pickStep) {
		this.pickStep = pickStep;
	}
	
	public void setDeviceSettings(CNCMillingMachineSettings deviceSettings) {
		this.deviceSettings = deviceSettings;
	}

	@Override
	public void refresh() {
		if (pickStep.getRobotSettings().getSmoothPoint() != null) {
			ntxtSmoothX.setText(""+pickStep.getRobotSettings().getSmoothPoint().getX());
			ntxtSmoothY.setText(""+pickStep.getRobotSettings().getSmoothPoint().getY());
			ntxtSmoothZ.setText(""+pickStep.getRobotSettings().getSmoothPoint().getZ());
		}
		if(deviceSettings.getClamping(pickStep.getDeviceSettings().getWorkArea()) == null) {
			btnResetSmooth.setDisable(true);
		} else {
			btnResetSmooth.setDisable(false);
		}if (pickStep.getRobotSettings().getWorkPieceDimensions() != null) {
			if (pickStep.getRobotSettings().getWorkPieceDimensions().getHeight() != -1) {
				ntxtHeight.setText("" + pickStep.getRobotSettings().getWorkPieceDimensions().getHeight());
			}
		}
	}

}
