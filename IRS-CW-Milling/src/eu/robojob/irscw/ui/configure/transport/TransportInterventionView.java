package eu.robojob.irscw.ui.configure.transport;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.ui.configure.AbstractFormView;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.general.model.TransportInformation;
import eu.robojob.irscw.util.UIConstants;

public class TransportInterventionView extends AbstractFormView<TransportInterventionPresenter> {

	private TransportInformation transportInfo;
	
	private Button btnInterventionBeforePick;
	private Button btnInterventionAfterPut;
	
	private Label lblInterventionBeforeInterval;
	private Label lblIntervnetionAfterInterval;
	
	private IntegerTextField itfInterventionBeforePickFrequency;
	private IntegerTextField itfInterventionAfterPutFrequency;
	
	private static final String PAUSE_LEFT_ICON = "M 9.9375 0.125 C 4.496733 0.125 0.0625 4.5580014 0.0625 10 C 0.0625 15.439535 4.496733 19.875 9.9375 19.875 C 15.038219 19.875 19.182943 15.966807 19.6875 11 L 19.6875 11.8125 L 34.4375 11.8125 L 29.875 16.0625 L 34.375 16.0625 L 40.125 10.6875 L 40.125 10.5625 L 34.375 5.25 L 29.875 5.25 L 34.375 9.375 L 19.6875 9.375 C 19.360574 4.2287004 15.165241 0.125 9.9375 0.125 z M 6.5625 5.5625 L 8.8125 5.5625 L 8.8125 14.4375 L 6.5625 14.4375 L 6.5625 5.5625 z M 11 5.5625 L 13.25 5.5625 L 13.25 14.4375 L 11 14.4375 L 11 5.5625 z";
	private static final String PAUSE_RIGHT_ICON = "M 30.34375 0.03125 C 25.33388 0.03125 21.208284 3.790937 20.5625 8.625 L 9.65625 8.625 L 4.8125 4.1875 L 0.125 4.1875 L 4.1875 8.0625 L 6.21875 9.9375 L 6.21875 10 L 0.0625 15.71875 L 4.8125 15.71875 L 9.71875 11.25 L 20.5625 11.25 C 21.208284 16.082494 25.33388 19.78125 30.34375 19.78125 C 35.801573 19.78125 40.25 15.394087 40.25 9.9375 C 40.25 4.4784419 35.801573 0.03125 30.34375 0.03125 z M 27.03125 5.5 L 29.21875 5.5 L 29.21875 14.40625 L 27.03125 14.40625 L 27.03125 5.5 z M 31.46875 5.5 L 33.65625 5.5 L 33.65625 14.40625 L 31.46875 14.40625 L 31.46875 5.5 z";
	
	private static final double BTN_WIDTH = 200;
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	
	private static final int VGAP = 15;
	private static final int HGAP = 15;
	
	private static final double LBL_WIDTH = 100;
	
	public void setTransportInfo(TransportInformation transportInfo) {
		this.transportInfo = transportInfo;
		setVgap(VGAP);
		setHgap(HGAP);
	}
	
	@Override
	protected void build() {
		btnInterventionBeforePick = createButton(PAUSE_LEFT_ICON, "btn-intervention", translator.getTranslation("interventionBeforePick"), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.clickedInterventionBeforePick();
			}
		}, 40.188);
		
		int column = 0;
		int row = 0;
		
		add(btnInterventionBeforePick, column++, row);
		
		lblInterventionBeforeInterval = new Label(translator.getTranslation("interval"));
		lblInterventionBeforeInterval.setPrefWidth(LBL_WIDTH);
		lblInterventionBeforeInterval.getStyleClass().add("lbl-intervention");
		add(lblInterventionBeforeInterval, column++, row);
		
		itfInterventionBeforePickFrequency = new IntegerTextField(2);
		itfInterventionBeforePickFrequency.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		add(itfInterventionBeforePickFrequency, column++, row);
		itfInterventionBeforePickFrequency.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> arg0, Integer arg1,
					Integer arg2) {
				presenter.changedInterventionBeforePickInterval(arg2);
			}
		});
		
		column = 0;
		row++;
		
		btnInterventionAfterPut = createButton(PAUSE_RIGHT_ICON, "btn-intervention", translator.getTranslation("interventionAfterPut"), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.clickedInterventionAfterPut();
			}
		}, 40.188);
		
		add(btnInterventionAfterPut, column++, row);
		
		lblIntervnetionAfterInterval = new Label(translator.getTranslation("interval"));
		lblIntervnetionAfterInterval.setPrefWidth(LBL_WIDTH);
		lblIntervnetionAfterInterval.getStyleClass().add("lbl-intervention");
		add(lblIntervnetionAfterInterval, column++, row);
		
		itfInterventionAfterPutFrequency = new IntegerTextField(2);
		itfInterventionAfterPutFrequency.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		add(itfInterventionAfterPutFrequency, column++, row);
		itfInterventionAfterPutFrequency.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> arg0, Integer arg1,
					Integer arg2) {
				presenter.changedInterventionAfterPutInterval(arg2);
			}
		});
	}

	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		itfInterventionAfterPutFrequency.setFocusListener(listener);
		itfInterventionBeforePickFrequency.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		btnInterventionAfterPut.getStyleClass().remove("form-button-active");
		btnInterventionBeforePick.getStyleClass().remove("form-button-active");
		itfInterventionAfterPutFrequency.setDisable(true);
		lblIntervnetionAfterInterval.setDisable(true);
		itfInterventionAfterPutFrequency.setText("");
		itfInterventionBeforePickFrequency.setDisable(true);
		lblInterventionBeforeInterval.setDisable(true);
		itfInterventionBeforePickFrequency.setText("");
		if (transportInfo.hasInterventionBeforePick()) {
			btnInterventionBeforePick.getStyleClass().add("form-button-active");
			itfInterventionBeforePickFrequency.setText(transportInfo.getInterventionBeforePick().getFrequency() + "");
			itfInterventionBeforePickFrequency.setDisable(false);
			lblInterventionBeforeInterval.setDisable(false);
		}
		if (transportInfo.hasInterventionAfterPut()) {
			btnInterventionAfterPut.getStyleClass().add("form-button-active");
			itfInterventionAfterPutFrequency.setText(transportInfo.getInterventionAfterPut().getFrequency() + "");
			itfInterventionAfterPutFrequency.setDisable(false);
			lblIntervnetionAfterInterval.setDisable(false);
		}
		if ((transportInfo.getPutStep() != null) && (transportInfo.getPutStep() instanceof PutAndWaitStep)) {
			btnInterventionAfterPut.setDisable(true);
			itfInterventionAfterPutFrequency.setDisable(true);
		} 
		if ((transportInfo.getPickStep() != null) && (transportInfo.getPickStep() instanceof PickAfterWaitStep)) {
			btnInterventionBeforePick.setDisable(true);
			itfInterventionBeforePickFrequency.setDisable(true);
		} 
	}

}
