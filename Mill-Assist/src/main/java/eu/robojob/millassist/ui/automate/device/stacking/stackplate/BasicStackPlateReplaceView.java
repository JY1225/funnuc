package eu.robojob.millassist.ui.automate.device.stacking.stackplate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class BasicStackPlateReplaceView extends AbstractFormView<BasicStackPlateReplacePresenter> {

	private Label lblFinishedAmount;
	private IntegerTextField itfFinishedAmount;
	private Button btnMax;
	private Button btnRefill;
	
	private static final String MAX = "BasicStackPlateRefillView.max";
	private static final String REFILL = "BasicStackPlateRefillView.refill";
	private static final String FINISHED_AMOUNT = "BasicStackPlateRefillView.finishedAmount";

	
	public BasicStackPlateReplaceView() {
		build();
	}
	
	@Override
	protected void build() {
		getContents().setVgap(15);
		getContents().setHgap(15);
		
		lblFinishedAmount = new Label(Translator.getTranslation(FINISHED_AMOUNT));
		lblFinishedAmount.getStyleClass().add(CSS_CLASS_FORM_LABEL);
		itfFinishedAmount = new IntegerTextField(4);
		btnMax = createButton(Translator.getTranslation(MAX), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				refreshMax();
			}
		});
		btnRefill = createButton(Translator.getTranslation(REFILL), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().replaceRawByFinished(Integer.parseInt(itfFinishedAmount.getText()));
			}
		});
		int row = 0; int column = 0;
		getContents().add(lblFinishedAmount, column++, row);
		GridPane.setMargin(lblFinishedAmount, new Insets(0, 0, 0, 50));
		getContents().add(itfFinishedAmount, column++, row);
		getContents().add(btnMax, column++, row);
		row++; column = 0;
		getContents().add(btnRefill, column++, row, 3, 1);
		GridPane.setMargin(btnRefill, new Insets(0, 120, 0, 115));
		GridPane.setHalignment(btnRefill, HPos.CENTER);
		
		hideNotification();

	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		itfFinishedAmount.setFocusListener(listener);
	}
	
	public void setButtonEnabled(final boolean enabled) {
		btnRefill.setDisable(!enabled);
	}

	@Override
	public void refresh() {
		itfFinishedAmount.setText("0");
		if (getPresenter().isAutoMode()) {
			setButtonEnabled(false);
		} else {
			setButtonEnabled(true);
		}
		hideNotification();
	}
	
	private void refreshMax() {
		itfFinishedAmount.setText("" + getPresenter().getNbRawWorkPiecesToReplace());
	}
}
