package eu.robojob.millassist.ui.automate.device.stacking.stackplate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class BasicStackPlateAddView extends AbstractFormView<BasicStackPlateAddPresenter> {

	private Label lblAmount;
	private IntegerTextField itfAmount;
	private Button btnMax;
	private Button btnAdd;
	private CheckBox cbReplaceFinishedPieces;
	
	private static final String AMOUNT = "BasicStackPlateAddView.amount";
	private static final String MAX = "BasicStackPlateAddView.max";
	private static final String ADD = "BasicStackPlateAddView.add";
	private static final String REPLACE_FINISHED = "BasicStackPlateAddView.replaceFinished";
	
	public BasicStackPlateAddView() {
		build();
	}
	
	@Override
	protected void build() {
		getContents().setVgap(15);
		getContents().setHgap(15);
		
		lblAmount = new Label(Translator.getTranslation(AMOUNT));
		lblAmount.getStyleClass().add(CSS_CLASS_FORM_LABEL);
		itfAmount = new IntegerTextField(4);
		cbReplaceFinishedPieces = new CheckBox(Translator.getTranslation(REPLACE_FINISHED));
		cbReplaceFinishedPieces.setSelected(true);
		btnMax = createButton(Translator.getTranslation(MAX), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				refreshMax();
			}
		});
		btnAdd = createButton(Translator.getTranslation(ADD), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().addWorkpieces(Integer.parseInt(itfAmount.getText()), cbReplaceFinishedPieces.isSelected());
			}
		});
		cbReplaceFinishedPieces.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				refreshMax();
			}	
		}
		);
		int row = 0; int column = 0;
		getContents().add(lblAmount, column++, row);
		GridPane.setMargin(lblAmount, new Insets(0, 0, 0, 0));
		getContents().add(itfAmount, column++, row);
		getContents().add(btnMax, column++, row);
		row++; column = 0;
		getContents().add(cbReplaceFinishedPieces, column, row,3,1);
		GridPane.setMargin(cbReplaceFinishedPieces, new Insets(0, 0, 0, 0));
		row++; column = 0;
		getContents().add(btnAdd, column++, row, 3, 1);
		GridPane.setMargin(btnAdd, new Insets(0, 0, 0, 20));
		GridPane.setHalignment(btnAdd, HPos.CENTER);
		GridPane.setHalignment(cbReplaceFinishedPieces, HPos.LEFT);
		
		hideNotification();
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		itfAmount.setFocusListener(listener);
	}
	
	public void setButtonEnabled(final boolean enabled) {
		btnAdd.setDisable(!enabled);
	}
	
	public void disableReplaceFinishedBox() {
		cbReplaceFinishedPieces.setSelected(false);
		cbReplaceFinishedPieces.setDisable(true);
	}

	@Override
	public void refresh() {
		refreshMax();
		hideNotification();
	}
	
	private void refreshMax() {
		if(cbReplaceFinishedPieces.isSelected()) {
			itfAmount.setText("" + (getPresenter().getMaxFinishedToReplaceAmount() + getPresenter().getMaxAddAmount()));
		} else {
			itfAmount.setText("" + getPresenter().getMaxAddAmount());
		}
		if(Integer.parseInt(itfAmount.getText()) <0) {
			itfAmount.setText("0");
		}
	}
}
