package eu.robojob.millassist.ui.configure.transport;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.PutAndWaitStep;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.model.TransportInformation;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class TransportInterventionView extends AbstractFormView<TransportInterventionPresenter> {

	private TransportInformation transportInfo;
	
	private Button btnInterventionBeforePick;
	private Button btnInterventionAfterPut;
	
	private Label lblInterventionBeforeInterval;
	private Label lblIntervnetionAfterInterval;
	
	private IntegerTextField itfInterventionBeforePickFrequency;
	private IntegerTextField itfInterventionAfterPutFrequency;
		
	private static final double BTN_WIDTH = 130;
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final int VGAP = 15;
	private static final int HGAP = 15;
	
	private static final String CSS_CLASS_LABEL_INTERVENTION = "lbl-intervention";
	
	private static final String INTERVAL = "TransportInterventionView.interval";
	private static final String INTERVENTION_BEFORE_PICK = "TransportInterventionView.interventionBeforePick";
	private static final String INTERVENTION_AFTER_PUT = "TransportInterventionView.interventionAfterPut";
	
	private static final double LBL_WIDTH = 75;
	
	public void setTransportInfo(final TransportInformation transportInfo) {
		this.transportInfo = transportInfo;
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
	}
	
	@Override
	protected void build() {
		btnInterventionBeforePick = createButton(Translator.getTranslation(INTERVENTION_BEFORE_PICK), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().clickedInterventionBeforePick();
			}
		});
		
		int column = 0;
		int row = 0;
		
		getContents().add(btnInterventionBeforePick, column++, row);
		
		lblInterventionBeforeInterval = new Label(Translator.getTranslation(INTERVAL));
		lblInterventionBeforeInterval.setPrefWidth(LBL_WIDTH);
		lblInterventionBeforeInterval.getStyleClass().add(CSS_CLASS_LABEL_INTERVENTION);
		getContents().add(lblInterventionBeforeInterval, column++, row);
		
		itfInterventionBeforePickFrequency = new IntegerTextField(2);
		itfInterventionBeforePickFrequency.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		getContents().add(itfInterventionBeforePickFrequency, column++, row);
		itfInterventionBeforePickFrequency.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(final ObservableValue<? extends Integer> arg0, final Integer arg1, final Integer arg2) {
				getPresenter().changedInterventionBeforePickInterval(arg2);
			}
		});
		
		column = 0;
		row++;
		
		btnInterventionAfterPut = createButton(Translator.getTranslation(INTERVENTION_AFTER_PUT), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().clickedInterventionAfterPut();
			}
		});
		
		getContents().add(btnInterventionAfterPut, column++, row);
		
		lblIntervnetionAfterInterval = new Label(Translator.getTranslation(INTERVAL));
		lblIntervnetionAfterInterval.setPrefWidth(LBL_WIDTH);
		lblIntervnetionAfterInterval.getStyleClass().add(CSS_CLASS_LABEL_INTERVENTION);
		getContents().add(lblIntervnetionAfterInterval, column++, row);
		
		itfInterventionAfterPutFrequency = new IntegerTextField(2);
		itfInterventionAfterPutFrequency.setPrefSize(UIConstants.NUMERIC_TEXT_FIELD_WIDTH, UIConstants.TEXT_FIELD_HEIGHT);
		getContents().add(itfInterventionAfterPutFrequency, column++, row);
		itfInterventionAfterPutFrequency.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(final ObservableValue<? extends Integer> arg0, final Integer arg1, final Integer arg2) {
				getPresenter().changedInterventionAfterPutInterval(arg2);
			}
		});
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		itfInterventionAfterPutFrequency.setFocusListener(listener);
		itfInterventionBeforePickFrequency.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		btnInterventionAfterPut.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnInterventionBeforePick.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		itfInterventionAfterPutFrequency.setDisable(true);
		lblIntervnetionAfterInterval.setDisable(true);
		itfInterventionAfterPutFrequency.setText("");
		itfInterventionBeforePickFrequency.setDisable(true);
		lblInterventionBeforeInterval.setDisable(true);
		itfInterventionBeforePickFrequency.setText("");
		if (transportInfo.hasInterventionBeforePick()) {
			btnInterventionBeforePick.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			itfInterventionBeforePickFrequency.setText(transportInfo.getInterventionBeforePick().getFrequency() + "");
			itfInterventionBeforePickFrequency.setDisable(false);
			lblInterventionBeforeInterval.setDisable(false);
		}
		if (transportInfo.hasInterventionAfterPut()) {
			btnInterventionAfterPut.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			itfInterventionAfterPutFrequency.setText(transportInfo.getInterventionAfterPut().getFrequency() + "");
			itfInterventionAfterPutFrequency.setDisable(false);
			lblIntervnetionAfterInterval.setDisable(false);
		}
		if ((transportInfo.getPickStep() != null) && (transportInfo.getIndex() == 0)) {
			btnInterventionBeforePick.setDisable(true);
			itfInterventionBeforePickFrequency.setDisable(true);
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
