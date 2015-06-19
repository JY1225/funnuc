package eu.robojob.millassist.ui.automate.device.stacking.pallet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class UnloadPalletAddRemoveFinishedView extends AbstractFormView<UnloadPalletAddRemoveFinishedPresenter>{

    private Label lblNumberFinished;
    private IntegerTextField itfNumberFinished;
    private Button btnMaxFinished;
    
    private Button btnChange;
    private static final String MAX = "BasicStackPlateAddView.max";
    private static final String FINISHED = "BasicStackPlateAddView.finished";
    private static final String CHANGE = "StackingDeviceConfigureView.change";
    
    public UnloadPalletAddRemoveFinishedView() {
       build();
    }
    
    @Override
    protected void build() {
        getContents().setVgap(15);
        getContents().setHgap(15);
        
        
        lblNumberFinished = new Label("#"+Translator.getTranslation(FINISHED));
        lblNumberFinished.getStyleClass().add(CSS_CLASS_FORM_LABEL);
        itfNumberFinished = new IntegerTextField(4);
        lblNumberFinished.setAlignment(Pos.CENTER);
        
        btnMaxFinished = createButton(Translator.getTranslation(MAX), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                refreshMaxFinished();
            }
        });
        
        btnChange = createButton(Translator.getTranslation(CHANGE), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                changeAmounts();
            }
        });

        
        int row = 0; int column = 0;
        getContents().add(lblNumberFinished, column++, row);
        GridPane.setMargin(lblNumberFinished, new Insets(0, 0, 0, 0));
        getContents().add(itfNumberFinished, column++, row);
        getContents().add(btnMaxFinished, column++, row);
        row++; column = 1;
        getContents().add(btnChange, column ++, row);
                
        hideNotification();
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        itfNumberFinished.setFocusListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        refreshFinished();
        hideNotification();
    }
    
    public void setButtonEnabled(final boolean enabled) {
        btnChange.setDisable(!enabled);
    }

    
    
    /**
     * Rests the max amount of pieces to remove.
     */
    private void refreshMaxFinished() {
        itfNumberFinished.setText("" + getPresenter().getMaxFinishedPieces());
        if(Integer.parseInt(itfNumberFinished.getText()) <0) {
            itfNumberFinished.setText("0");
        }
    }
    
    private void changeAmounts() {
        int newAmount = Integer.parseInt(itfNumberFinished.getText());
        if(newAmount > getPresenter().getCurrentFinishedPieces()) {
            getPresenter().addFinishedWorkPieces(newAmount-getPresenter().getCurrentFinishedPieces());
        } else if (newAmount < getPresenter().getCurrentFinishedPieces()) {
            getPresenter().removeFinishedWorkPieces(getPresenter().getCurrentFinishedPieces()- newAmount);
        }
    }
    
    
    private void refreshFinished() {
        itfNumberFinished.setText("" + getPresenter().getCurrentFinishedPieces());
        if(Integer.parseInt(itfNumberFinished.getText()) <0) {
            itfNumberFinished.setText("0");
        }
    }

}
