package eu.robojob.millassist.ui.automate.device.stacking.pallet;

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

public class UnloadPalletAddRemoveFinishedView extends AbstractFormView<UnloadPalletAddRemoveFinishedPresenter>{

    private Label lblAmount;
    private IntegerTextField itfAmount;
    private Button btnMax;
    private Button btnAdd;
    
    private Label lblAmountRemove;
    private IntegerTextField itfAmountRemove;
    private Button btnMaxRemove;
    private Button btnRemove;
    private static final String AMOUNT = "BasicStackPlateAddView.amount";
    private static final String MAX = "BasicStackPlateAddView.max";
    private static final String ADD = "BasicStackPlateAddView.add";
    private static final String REMOVE = "UnloadPalletAddRemoveFinishedView.remove";
    
    public UnloadPalletAddRemoveFinishedView() {
       build();
    }
    
    @Override
    protected void build() {
        getContents().setVgap(15);
        getContents().setHgap(15);
        
        lblAmount = new Label(Translator.getTranslation(AMOUNT));
        lblAmount.getStyleClass().add(CSS_CLASS_FORM_LABEL);
        itfAmount = new IntegerTextField(4);
        
        btnMax = createButton(Translator.getTranslation(MAX), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                refreshMaxRemove();
            }
        });
        btnAdd = createButton(Translator.getTranslation(ADD), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().addWorkpieces(Integer.parseInt(itfAmount.getText()));
                refreshMax();
                refreshMaxRemove();
            }
        });
        
        lblAmountRemove = new Label(Translator.getTranslation(AMOUNT));
        lblAmountRemove.getStyleClass().add(CSS_CLASS_FORM_LABEL);
        itfAmountRemove = new IntegerTextField(4);
        
        btnMaxRemove = createButton(Translator.getTranslation(MAX), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                refreshMaxRemove();
            }
        });
        btnRemove = createButton(Translator.getTranslation(REMOVE), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().removeWorkPieces(Integer.parseInt(itfAmountRemove.getText()));
                refreshMax();
                refreshMaxRemove();
            }
        });
        
        int row = 0; int column = 0;
        getContents().add(lblAmount, column++, row);
        GridPane.setMargin(lblAmount, new Insets(0, 0, 0, 0));
        getContents().add(itfAmount, column++, row);
        getContents().add(btnMax, column++, row);
        getContents().add(btnAdd, column++, row);
        GridPane.setMargin(btnAdd, new Insets(0, 0, 0, 20));
        GridPane.setHalignment(btnAdd, HPos.CENTER);
        
        row++; column = 0;
        getContents().add(lblAmountRemove, column++, row);
        GridPane.setMargin(lblAmountRemove, new Insets(0, 0, 0, 0));
        getContents().add(itfAmountRemove, column++, row);
        getContents().add(btnMaxRemove, column++, row);

        getContents().add(btnRemove, column++, row);
        GridPane.setMargin(btnRemove, new Insets(0, 0, 0, 20));
        GridPane.setHalignment(btnRemove, HPos.CENTER);
        
        hideNotification();
        
    }

    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        itfAmount.setFocusListener(listener);
        itfAmountRemove.setFocusListener(listener);
    }

    @Override
    public void refresh() {
        refreshMax();
        refreshMaxRemove();
        hideNotification();
    }
    
    public void setButtonEnabled(final boolean enabled) {
        btnAdd.setDisable(!enabled);
        btnRemove.setDisable(!enabled);
    }
    
    private void refreshMax() {
        itfAmount.setText("" + getPresenter().getMaxPiecesToAdd());
        if(Integer.parseInt(itfAmount.getText()) <0) {
            itfAmount.setText("0");
        }
    }
    
    private void refreshMaxRemove() {
        itfAmountRemove.setText("" + getPresenter().getMaxPiecesToRemove());
        if(Integer.parseInt(itfAmountRemove.getText()) <0) {
            itfAmountRemove.setText("0");
        }
    }

}
