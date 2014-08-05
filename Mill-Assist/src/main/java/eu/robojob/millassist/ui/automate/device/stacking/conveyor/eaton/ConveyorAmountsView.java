package eu.robojob.millassist.ui.automate.device.stacking.conveyor.eaton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class ConveyorAmountsView extends AbstractFormView<ConveyorAmountsPresenter> {

	private Label lblFinishedAmount;
	private IntegerTextField itfFinishedAmount;
	private Label lblTotalAmount;
	private IntegerTextField itfTotalAmount;
	
	private Button btnUpdate;
	private Button btnRefresh;
	private Button btnClear;
	
	private ProcessFlow processFlow;
	
	private static final String FINISHED_AMOUNT = "ConveyorAmountsView.finishedAmount";
	private static final String TOTAL_AMOUNT = "ConveyorAmountsView.totalAmount";
	private static final String UPDATE = "ConveyorAmountsView.update";
	private static final String REFRESH = "ConveyorAmountsView.refresh";
	private static final String CLEAR = "ConveyorAmountsView.clear";
	private static final String INCORRECT_DATA = "ConveyorAmountsView.incorrectData";
	
	@Override
	protected void build() {
		getContents().setVgap(15);
		getContents().setHgap(15);
		
		lblTotalAmount = new Label(Translator.getTranslation(TOTAL_AMOUNT));
		lblTotalAmount.getStyleClass().add(CSS_CLASS_FORM_LABEL);
		itfTotalAmount = new IntegerTextField(4);
		itfTotalAmount.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(final ObservableValue<? extends Integer> observable, final Integer oldValue, final Integer newValue) {
				getPresenter().changedData(itfFinishedAmount.getText(), "" + newValue);
			}
		});
	
		lblFinishedAmount = new Label(Translator.getTranslation(FINISHED_AMOUNT));
		lblFinishedAmount.getStyleClass().add(CSS_CLASS_FORM_LABEL);
		itfFinishedAmount = new IntegerTextField(4);
		itfFinishedAmount.setOnChange(new ChangeListener<Integer>() {
			@Override
			public void changed(final ObservableValue<? extends Integer> observable, final Integer oldValue, final Integer newValue) {
				getPresenter().changedData("" + newValue, itfTotalAmount.getText());
			}
		});
		btnUpdate = createButton(Translator.getTranslation(UPDATE), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().updateAmounts(itfFinishedAmount.getText(), itfTotalAmount.getText());
			}
		});
		
		btnRefresh = createButton(Translator.getTranslation(REFRESH), UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				refresh();
			}
		});
		
		btnClear = createButton(Translator.getTranslation(CLEAR), UIConstants.BUTTON_HEIGHT*1.5, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				itfFinishedAmount.setText("0");
				itfTotalAmount.setText("");
				btnUpdate.setDisable(false);
				hideNotification();
			}
		});
		
		int row = 0; int column = 0;
		getContents().add(lblTotalAmount, column++, row);
		getContents().add(itfTotalAmount, column++, row);
		row++; column = 0;
		getContents().add(lblFinishedAmount, column++, row);
		getContents().add(itfFinishedAmount, column++, row);
		row++; column = 0;
		HBox hboxRefreshClear = new HBox();
		hboxRefreshClear.setSpacing(10);
		hboxRefreshClear.getChildren().addAll(btnRefresh, btnClear);
		hboxRefreshClear.setAlignment(Pos.CENTER);
		getContents().add(hboxRefreshClear, column++, row, 3, 1);
		GridPane.setHalignment(hboxRefreshClear, HPos.CENTER);
		row++; column = 0;
		getContents().add(btnUpdate, column++, row, 3, 1);
		GridPane.setHalignment(btnUpdate, HPos.CENTER);
		hideNotification();
		refresh();
	}
	
	public void setProcessFlow(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		itfFinishedAmount.setFocusListener(listener);
		itfTotalAmount.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		hideNotification();
		btnUpdate.setDisable(false);
		refreshFinishedValue();
		if (processFlow.getTotalAmount() != -1) {
			refreshTotalValue();
		} else {
			itfTotalAmount.setText("");
		}
	}
	
	private void refreshFinishedValue() {
		itfFinishedAmount.setText("" + processFlow.getFinishedAmount());
	}
	
	private void refreshTotalValue() {
		itfTotalAmount.setText("" + processFlow.getTotalAmount());
	}
	
	public void notifyIncorrectData() {
		showNotification(Translator.getTranslation(INCORRECT_DATA), Type.WARNING);
		btnUpdate.setDisable(true);
	}
	
	public void correctData() {
		hideNotification();
		btnUpdate.setDisable(false);
	}

}
