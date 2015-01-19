package eu.robojob.millassist.ui.configure.device.processing.reversal;

import java.util.Map.Entry;

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
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class ReversalUnitPickView extends AbstractFormView<ReversalUnitPickPresenter> {

	private Label lblSmoothInfo;
	private HBox hBoxSmoothPoint;
	private Label lblSmoothX;
	private Label lblSmoothY;
	private Label lblSmoothZ;
	private Button btnResetSmooth;
	private NumericTextField ntxtSmoothX;
	private NumericTextField ntxtSmoothY;
	private NumericTextField ntxtSmoothZ;
	
	private Label lblLoadType;
	private Button btnTopLoad, btnBottomLoad, btnFrontLoad, btnLeftLoad;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final int MAX_INTEGER_LENGTH = 6;
	
	private static final String SMOOTH_PICK_INFO = "ReversalUnitPickView.smoothPickInfo";
	private static final String SMOOTH_X = "ReversalUnitPickView.smoothX";
	private static final String SMOOTH_Y = "ReversalUnitPickView.smoothY";
	private static final String SMOOTH_Z = "ReversalUnitPickView.smoothZ";
	private static final String RESET = "ReversalUnitPickView.resetSmooth";
	private static final String LOAD_TYPE = "ReversalUnitPickView.loadType";
	private static final String TOP_LOAD = "ReversalUnitPickView.topLoad";
	private static final String FRONT_LOAD = "ReversalUnitPutView.frontLoad";
	private static final String LEFT_LOAD = "ReversalUnitPutView.leftLoad";
	private static final String BOTTOM_LOAD = "ReversalUnitPickView.bottomLoad";
			
	public ReversalUnitPickView() {
		super();
		getContents().setHgap(HGAP);
		getContents().setVgap(VGAP);
	}
	
	@Override
	protected void build() {
		lblSmoothInfo = new Label(Translator.getTranslation(SMOOTH_PICK_INFO));
		
		lblSmoothX = new Label(Translator.getTranslation(SMOOTH_X));
		lblSmoothY = new Label(Translator.getTranslation(SMOOTH_Y));
		lblSmoothZ = new Label(Translator.getTranslation(SMOOTH_Z));
			
		lblLoadType = new Label(Translator.getTranslation(LOAD_TYPE));
		
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
		Text txtBtnResetSmooth = new Text(Translator.getTranslation(RESET));
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
		
		HBox hboxLoadType = new HBox();
		hboxLoadType.setAlignment(Pos.CENTER_LEFT);
		
		btnTopLoad = createButton(Translator.getTranslation(TOP_LOAD), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedPickType(ApproachType.TOP);
				refreshLoadType(ApproachType.TOP);

			}
		});
		btnTopLoad.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnBottomLoad = createButton(Translator.getTranslation(BOTTOM_LOAD), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedPickType(ApproachType.BOTTOM);
				refreshLoadType(ApproachType.BOTTOM);
			}
		});
		btnBottomLoad.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		btnFrontLoad = createButton(Translator.getTranslation(FRONT_LOAD), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedPickType(ApproachType.FRONT);
				refreshLoadType(ApproachType.FRONT);
			}
		});
		btnFrontLoad.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
		btnLeftLoad = createButton(Translator.getTranslation(LEFT_LOAD), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				getPresenter().changedPickType(ApproachType.LEFT);
				refreshLoadType(ApproachType.LEFT);
			}
		});
		btnLeftLoad.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
		hboxLoadType.getChildren().addAll(btnTopLoad, btnFrontLoad, btnLeftLoad, btnBottomLoad);
		
		int column = 0;
		int row = 0;
		
		column = 0;
		row = 0;
		getContents().add(lblSmoothInfo, column++, row);
		
		column = 0;
		row++;
		getContents().add(hBoxSmoothPoint, column++, row);
		
		column = 0;
		row++;
		getContents().add(lblLoadType, column++, row);
		
		column = 0;
		row++;
		getContents().add(hboxLoadType, column++, row);
		refresh();
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		ntxtSmoothX.setFocusListener(listener);
		ntxtSmoothY.setFocusListener(listener);
		ntxtSmoothZ.setFocusListener(listener);
	}
	
	private void refreshLoadType(ApproachType activeLoadType) {
		hideNotification();
		btnTopLoad.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnBottomLoad.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnLeftLoad.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnFrontLoad.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
		switch (activeLoadType) {
		case BOTTOM:
			btnBottomLoad.getStyleClass().add(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
			break;
		case TOP:
			btnTopLoad.getStyleClass().add(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
			break;
		case LEFT:
			btnLeftLoad.getStyleClass().add(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
			break;
		case FRONT:
			btnFrontLoad.getStyleClass().add(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
			break;
		default:
			break;
		}
		if (getPresenter().getMenuPresenter() != null && getPresenter().getMenuPresenter().isSameApproachType()) {
			showNotification(Translator.getTranslation(ReversalUnitMenuPresenter.SAME_APPROACHTYPES), Type.WARNING);
		}
	}

	@Override
	public void refresh() {
		if (getPresenter().getPickStep().getRobotSettings().getSmoothPoint() != null) {
			ntxtSmoothX.setText("" + getPresenter().getPickStep().getRobotSettings().getSmoothPoint().getX());
			ntxtSmoothY.setText("" + getPresenter().getPickStep().getRobotSettings().getSmoothPoint().getY());
			ntxtSmoothZ.setText("" + getPresenter().getPickStep().getRobotSettings().getSmoothPoint().getZ());
		}
		if (getPresenter().getDeviceSettings().getDefaultClamping(getPresenter().getPickStep().getDeviceSettings().getWorkArea()) == null) {
			btnResetSmooth.setDisable(true);
		} else {
			btnResetSmooth.setDisable(false);
		} 
		for (Entry<ApproachType, Boolean> entry: ((ReversalUnit) getPresenter().getPickStep().getDevice()).getAllowedApproachTypes().entrySet()) {
			enableApproachType(entry.getKey(), entry.getValue());
		}
		refreshLoadType(getPresenter().getPickStep().getRobotSettings().getApproachType());
		refreshLoadButtons();
	}
	
	private void enableApproachType(ApproachType approachType, boolean enable) {
		switch(approachType) {
		case BOTTOM:
			btnBottomLoad.setManaged(enable);
			btnBottomLoad.setVisible(enable);
			break;
		case TOP:
			btnTopLoad.setManaged(enable);
			btnTopLoad.setVisible(enable);
			break;
		case LEFT:
			btnLeftLoad.setManaged(enable);
			btnLeftLoad.setVisible(enable);
			break;
		case FRONT:
			btnFrontLoad.setManaged(enable);
			btnFrontLoad.setVisible(enable);
			break;
		}
	}
	
	private void refreshLoadButtons() {
		//final button
		if (!btnBottomLoad.isVisible()) {
			if (btnLeftLoad.isVisible()) {
				btnLeftLoad.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
				btnLeftLoad.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
			} else {
				if (btnFrontLoad.isVisible()) {
					btnFrontLoad.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
					btnFrontLoad.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
				}
			}
		}
		//first button
		if (!btnTopLoad.isVisible()) {
			if (btnFrontLoad.isVisible()) {
				btnFrontLoad.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
				btnFrontLoad.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
			} else {
				if (btnLeftLoad.isVisible()) {
					btnLeftLoad.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_BAR_CENTER);
					btnLeftLoad.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
				}
			}
		}
	}
}
