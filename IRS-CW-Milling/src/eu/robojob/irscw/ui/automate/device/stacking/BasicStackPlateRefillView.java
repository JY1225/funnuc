package eu.robojob.irscw.ui.automate.device.stacking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class BasicStackPlateRefillView extends AbstractFormView<BasicStackPlateRefillPresenter> {

	private BasicStackPlate basicStackPlate;
	private Label lblAmount;
	private IntegerTextField itfAmount;
	private Button btnMax;
	private Button btnRefill;
	
	private static final String AMOUNT = "BasicStackPlateRefillView.amount";
	private static final String MAX = "BasicStackPlateRefillView.max";
	private static final String REFILL = "BasicStackPlateRefillView.refill";
	
	public BasicStackPlateRefillView() {
		build();
	}
	
	public void setBasicStackPlate(final BasicStackPlate basicStackPlate) {
		this.basicStackPlate = basicStackPlate;
	}
	
	@Override
	protected void build() {
		setVgap(15);
		setHgap(15);
		lblAmount = new Label(Translator.getTranslation(AMOUNT));
		lblAmount.getStyleClass().add(CSS_CLASS_FORM_LABEL);
		itfAmount = new IntegerTextField(4);
		btnMax = createButton(Translator.getTranslation(MAX), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				refreshMax();
			}
		});
		btnRefill = createButton(Translator.getTranslation(REFILL), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().refill(Integer.parseInt(itfAmount.getText()));
			}
		});
		int row = 0; int column = 0;
		add(lblAmount, column++, row);
		add(itfAmount, column++, row);
		add(btnMax, column++, row);
		row++; column = 0;
		add(btnRefill, column++, row, 3, 1);
		GridPane.setHalignment(btnRefill, HPos.CENTER);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		itfAmount.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		refreshMax();
	}
	
	private void refreshMax() {
		itfAmount.setText("" + basicStackPlate.getFinishedWorkPiecesPresentAmount());
	}

}
