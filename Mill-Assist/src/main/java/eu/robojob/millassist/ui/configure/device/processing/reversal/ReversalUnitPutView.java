package eu.robojob.millassist.ui.configure.device.processing.reversal;

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
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class ReversalUnitPutView extends AbstractFormView<ReversalUnitPutPresenter> {
	
	private static Label lblSmoothInfo;
	
	private HBox hBoxSmoothPoint;
	
	private Label lblSmoothX;
	private Label lblSmoothY;
	private Label lblSmoothZ;
	private Label lblConfigWidth;
	
	private Button btnResetSmooth;
	
	private Label lblShiftedOrigin;
	private Button btnHome, btnHomeExtraX;
	
	private NumericTextField ntxtSmoothX;
	private NumericTextField ntxtSmoothY;
	private NumericTextField ntxtSmoothZ;
	private NumericTextField ntxtConfigWidth;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final int MAX_INTEGER_LENGTH = 6;
	
	private static final String SMOOTH_PUT_INFO = "ReversalUnitPutView.smoothPickInfo";
	private static final String SMOOTH_X = "ReversalUnitPutView.smoothX";
	private static final String SMOOTH_Y = "ReversalUnitPutView.smoothY";
	private static final String SMOOTH_Z = "ReversalUnitPutView.smoothZ";
	private static final String SMOOTH_RESET = "ReversalUnitPutView.resetSmooth";
	private static final String CONFIG_WIDTH = "ReversalUnitPutView.configWidth";
	private static final String SHIFTED_ORIGIN = "ReversalUnitPutView.shiftedOrigin";
	private static final String NORMAL_ORIGIN = "ReversalUnitPutView.normalOrigin";
	private static final String EXTRA_X_ORIGIN = "ReversalUnitPutView.extraXOrigin";
	
	private static final String CSS_CLASS_CENTER_TEXT = "center-text";
		
	public ReversalUnitPutView() {
		super();
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
	}
	
	@Override
	protected void build() {
		lblSmoothInfo = new Label(Translator.getTranslation(SMOOTH_PUT_INFO));
		
		lblSmoothX = new Label(Translator.getTranslation(SMOOTH_X));
		lblSmoothY = new Label(Translator.getTranslation(SMOOTH_Y));
		lblSmoothZ = new Label(Translator.getTranslation(SMOOTH_Z));
		lblConfigWidth = new Label(Translator.getTranslation(CONFIG_WIDTH));
		lblShiftedOrigin = new Label(Translator.getTranslation(SHIFTED_ORIGIN));
				
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
		ntxtConfigWidth = new NumericTextField(MAX_INTEGER_LENGTH);
		ntxtConfigWidth.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		ntxtConfigWidth.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedConfigWidth(newValue);
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
				
		btnHome = createButton(Translator.getTranslation(NORMAL_ORIGIN), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				refreshOrigin(false);
				getPresenter().changedShiftedOrigin(false);
			}
		});
		btnHome.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
		btnHomeExtraX = createButton(Translator.getTranslation(EXTRA_X_ORIGIN), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				refreshOrigin(true);
				getPresenter().changedShiftedOrigin(true);
			}
		});
		btnHomeExtraX.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
		btnHomeExtraX.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_LABEL, CSS_CLASS_CENTER_TEXT, CSS_CLASS_FORM_BUTTON);
		btnHome.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_LABEL, CSS_CLASS_CENTER_TEXT, CSS_CLASS_FORM_BUTTON);
		HBox hboxExtraX = new HBox();
		hboxExtraX.getChildren().addAll(btnHome, btnHomeExtraX);
		
		HBox hBoxConfigWidth = new HBox();
		hBoxConfigWidth.getChildren().addAll(lblConfigWidth, ntxtConfigWidth);
		HBox.setMargin(ntxtConfigWidth, new Insets(0, 20, 0, 10));
		hBoxConfigWidth.setFillHeight(false);
		hBoxConfigWidth.setAlignment(Pos.CENTER_LEFT);
		
		hBoxSmoothPoint = new HBox();
		hBoxSmoothPoint.getChildren().addAll(lblSmoothX, ntxtSmoothX, lblSmoothY, ntxtSmoothY, lblSmoothZ, ntxtSmoothZ, btnResetSmooth);
		HBox.setMargin(ntxtSmoothX, new Insets(0, 20, 0, 10));
		HBox.setMargin(ntxtSmoothY, new Insets(0, 20, 0, 10));
		HBox.setMargin(ntxtSmoothZ, new Insets(0, 20, 0, 10));
		hBoxSmoothPoint.setFillHeight(false);
		hBoxSmoothPoint.setAlignment(Pos.CENTER_LEFT);
		
		int column = 0;
		int row = 0;

		getContents().add(lblShiftedOrigin, column, row++);
		getContents().add(hboxExtraX, column, row++);
		column = 0;
		getContents().add(hBoxConfigWidth, column, row);	
		
		column = 0;
		row++;
		getContents().add(lblSmoothInfo, column, row);
		
		column = 0;
		row++;
		getContents().add(hBoxSmoothPoint, column++, row);
		
		refresh();
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		ntxtSmoothX.setFocusListener(listener);
		ntxtSmoothY.setFocusListener(listener);
		ntxtSmoothZ.setFocusListener(listener);
		ntxtConfigWidth.setFocusListener(listener);
	}

	private void refreshOrigin(final boolean isShifted) {
	    btnHome.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
	    btnHomeExtraX.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
	    if (isShifted) {
	        btnHome.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
	        btnHomeExtraX.getStyleClass().add(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
	    } else {
	        btnHome.getStyleClass().add(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
	        btnHomeExtraX.getStyleClass().remove(AbstractFormView.CSS_CLASS_FORM_BUTTON_ACTIVE);
	    }
	}

	@Override
	public void refresh() {
		if (getPresenter().getPutStep().getRobotSettings().getSmoothPoint() != null) {
			ntxtSmoothX.setText("" + getPresenter().getPutStep().getRobotSettings().getSmoothPoint().getX());
			ntxtSmoothY.setText("" + getPresenter().getPutStep().getRobotSettings().getSmoothPoint().getY());
			ntxtSmoothZ.setText("" + getPresenter().getPutStep().getRobotSettings().getSmoothPoint().getZ());
		}
		if (getPresenter().getDeviceSettings().getDefaultClamping(getPresenter().getPutStep().getDeviceSettings().getWorkArea()) == null) {
			btnResetSmooth.setDisable(true);
		} else {
			btnResetSmooth.setDisable(false);
		}
		if (getPresenter().getDeviceSettings().getConfigWidth() > 0 ) {
			ntxtConfigWidth.setText("" + getPresenter().getDeviceSettings().getConfigWidth());
		}
		refreshOrigin(getPresenter().getDeviceSettings().isShiftedOrigin());
		manageOriginButtons();
	}

	private void manageOriginButtons() {
		if (getPresenter().hasShiftingPin()) {
			btnHome.setManaged(true);
			btnHomeExtraX.setManaged(true);
			lblShiftedOrigin.setManaged(true);
			btnHome.setVisible(true);
			btnHomeExtraX.setVisible(true);
			lblShiftedOrigin.setVisible(true);
		} else {
			btnHome.setManaged(false);
			btnHomeExtraX.setManaged(false);
			lblShiftedOrigin.setManaged(false);
			btnHome.setVisible(false);
			btnHomeExtraX.setVisible(false);
			lblShiftedOrigin.setVisible(false);
		}
	}

}
