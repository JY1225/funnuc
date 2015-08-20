package eu.robojob.millassist.ui.automate.device.stacking.stackplate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    private Label lblNumberRaw;
    private IntegerTextField itfNumberRaw;
    private Button btnMaxRaw;

    private Label lblNumberFinished;
    private IntegerTextField itfNumberFinished;
    private Button btnChange;
    private static final String MAX = "BasicStackPlateAddView.max";
    private static final String FINISHED = "BasicStackPlateAddView.finished";
    private static final String RAW = "BasicStackPlateAddView.raw";
    private static final String CHANGE = "StackingDeviceConfigureView.change";
	
	public BasicStackPlateAddView() {
		build();
	}
	
	@Override
	protected void build() {
	    getContents().setVgap(15);
        getContents().setHgap(15);
        
        lblNumberRaw = new Label("# "+Translator.getTranslation(RAW));
        lblNumberRaw.getStyleClass().add(CSS_CLASS_FORM_LABEL);
        itfNumberRaw = new IntegerTextField(4);
        lblNumberRaw.setAlignment(Pos.CENTER);

        lblNumberFinished = new Label("# "+Translator.getTranslation(FINISHED));
        lblNumberFinished.getStyleClass().add(CSS_CLASS_FORM_LABEL);
        itfNumberFinished = new IntegerTextField(4);
        lblNumberFinished.setAlignment(Pos.CENTER);

        btnMaxRaw = createButton(Translator.getTranslation(MAX), UIConstants.BUTTON_HEIGHT * 2, UIConstants.BUTTON_HEIGHT,
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent arg0) {
                        refreshMaxRaw();
                    }
                });

        btnChange = createButton(Translator.getTranslation(CHANGE), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                changeAmounts();
            }
        });

        int row = 0;
        int column = 0;
        getContents().add(lblNumberRaw, column++, row);
        GridPane.setMargin(lblNumberRaw, new Insets(0, 0, 0, 0));
        getContents().add(itfNumberRaw, column++, row);
        GridPane.setHalignment(itfNumberRaw, HPos.CENTER);
        getContents().add(btnMaxRaw, column++, row);
        row++; column = 0;
        
        getContents().add(lblNumberFinished, column++, row);
        GridPane.setMargin(lblNumberFinished, new Insets(0, 0, 0, 0));
        getContents().add(itfNumberFinished, column++, row);
        GridPane.setHalignment(itfNumberFinished, HPos.CENTER);
//        getContents().add(btnMaxFinished, column++, row);
        row++; column = 1;
        getContents().add(btnChange, column ++, row);
        hideNotification();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        itfNumberRaw.setFocusListener(listener);
        itfNumberFinished.setFocusListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        refreshRaw();
        refreshFinished();
        setFinishedEditable(getPresenter().getProcessFlow().hasBasicStackPlateForFinishedPieces());
        hideNotification();
    }

    public void setButtonEnabled(final boolean enabled) {
        btnChange.setDisable(!enabled);
    }
    
    /**
     * Rests the max amount of pieces to remove.
     */
    private void refreshMaxRaw() {
        itfNumberRaw.setText("" + getPresenter().getMaxRawPieces(Integer.parseInt(itfNumberFinished.getText())));
        if(Integer.parseInt(itfNumberRaw.getText()) <0) {
            itfNumberRaw.setText("0");
        }
    }
    
    private void changeAmounts() {
        getPresenter().changeAmounts(Integer.parseInt(itfNumberRaw.getText()), Integer.parseInt(itfNumberFinished.getText()));
    }
    
    
    private void refreshFinished() {
        itfNumberFinished.setText("" + getPresenter().getCurrentFinishedPieces());
        if(Integer.parseInt(itfNumberFinished.getText()) <0) {
            itfNumberFinished.setText("0");
        }
    }
    
    private void refreshRaw() {
        itfNumberRaw.setText("" + getPresenter().getCurrentRawPieces());
        if(Integer.parseInt(itfNumberRaw.getText()) <0) {
            itfNumberRaw.setText("0");
        }
    }
    
    public void setFinishedEditable(boolean editable) {
        itfNumberFinished.setManaged(editable);
        lblNumberFinished.setManaged(editable);
    }
}
