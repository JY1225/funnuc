package eu.robojob.millassist.ui.admin.device;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class UnloadPalletConfigureView extends AbstractFormView<UnloadPalletConfigurePresenter>{

    private UnloadPallet unloadPallet;
    private ObservableList<String> userFrameNames;
    private Region spacer;

    private Label nameLabel;
    private FullTextField nameTextField;

    private Label userFrameLabel;
    private ComboBox<String> userFramesComboBox;

    private Label widthLabel;
    private NumericTextField widthNumbericTextField;

    private Label lengthLabel;
    private NumericTextField lengthNumbericTextField;

    private Label borderLabel;
    private NumericTextField borderNumbericTextField;

    private Label xOffsetLabel;
    private NumericTextField xOffsetNumbericTextField;

    private Label yOffsetLabel;
    private NumericTextField yOffsetNumbericTextField;

    private Label minInterferenceLabel;
    private NumericTextField minInterferenceTextField;
    
    private Label horizontalRLabel;
    private Button zeroButton;
    private Button oneEightyButton;
    
    private Label verticalRLabel;
    private Button minusButton;
    private Button plusButton;
    
    private Button btnSave;

    private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
    private static final String SAVE = "UnloadPallet.save";
    private static final String NAME = "UnloadPallet.name";
    private static final String USERFRAME = "UnloadPallet.userframe";
    private static final String WIDTH = "UnloadPallet.width";
    private static final String LENGTH = "UnloadPallet.length";
    private static final String BORDER = "UnloadPallet.border";
    private static final String XOFFSET = "UnloadPallet.xoffset";
    private static final String YOFFSET = "UnloadPallet.yoffset";
    private static final String MIN_INT = "UnloadPallet.minint";
    private static final String HOR_R = "UnloadPallet.horizontalR";
    private static final String VER_R = "UnloadPallet.verticalR";
    
    private float horizontalRValue;
    private float verticalRValue;

    public UnloadPalletConfigureView() {
        userFrameNames = FXCollections.observableArrayList();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void build() {
        getContents().setVgap(5);
        getContents().setHgap(15);
        getContents().setPadding(new Insets(15, 0, 0, 0));
        getContents().setAlignment(Pos.TOP_CENTER);
        spacer = new Region();
        spacer.setPrefWidth(20);

        nameLabel = new Label(Translator.getTranslation(NAME));
        nameTextField = new FullTextField(100);

        userFrameLabel = new Label(Translator.getTranslation(USERFRAME));
        userFramesComboBox = new ComboBox<String>();
        userFramesComboBox.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        userFramesComboBox.setMinSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        userFramesComboBox.setMaxSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        userFramesComboBox.setItems(userFrameNames);

        widthLabel = new Label(Translator.getTranslation(WIDTH));
        widthNumbericTextField = new NumericTextField(6);

        lengthLabel = new Label(Translator.getTranslation(LENGTH));
        lengthNumbericTextField = new NumericTextField(6);

        borderLabel = new Label(Translator.getTranslation(BORDER));
        borderNumbericTextField = new NumericTextField(4);

        xOffsetLabel = new Label(Translator.getTranslation(XOFFSET));
        xOffsetNumbericTextField = new NumericTextField(4);

        yOffsetLabel = new Label(Translator.getTranslation(YOFFSET));
        yOffsetNumbericTextField = new NumericTextField(4);
        
        minInterferenceLabel = new Label(Translator.getTranslation(MIN_INT));
        minInterferenceTextField = new NumericTextField(4);
        
        horizontalRLabel = new Label(Translator.getTranslation(HOR_R));
//        horizontalRTextField = new NumericTextField(4);
        HBox horizontalRBox = new HBox();
        zeroButton = createButton("0°", UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                horizontalRValue = 0;
                oneEightyButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                if(!zeroButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)) {
                    zeroButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            }
        });
        zeroButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        horizontalRBox.getChildren().add(zeroButton);
        
        oneEightyButton = createButton("+180°", UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT,new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                horizontalRValue = 180;
                zeroButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                if(!oneEightyButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)) {
                    oneEightyButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            }
        });
        oneEightyButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
        horizontalRBox.getChildren().add(oneEightyButton);
        
        
        HBox verticalRBox = new HBox();
        minusButton = createButton("-90°", UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                verticalRValue = -90;
                plusButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                if(!minusButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)) {
                    minusButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            }
        });
        minusButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        verticalRBox.getChildren().add(minusButton);
        
        plusButton = createButton("+90°", UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT,new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                verticalRValue = 90;
                minusButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                if(!plusButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)) {
                    plusButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            }
        });
        plusButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
        verticalRBox.getChildren().add(plusButton);
        
        verticalRLabel = new Label(Translator.getTranslation(VER_R));

        btnSave = createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getPresenter().saveData(nameTextField.getText(), userFramesComboBox.valueProperty().get(),Float.parseFloat(widthNumbericTextField.getText()),Float.parseFloat(lengthNumbericTextField.getText()),Float.parseFloat(borderNumbericTextField.getText()),Float.parseFloat(xOffsetNumbericTextField.getText()),Float.parseFloat(yOffsetNumbericTextField.getText()), Float.parseFloat(minInterferenceTextField.getText()), horizontalRValue, verticalRValue);
            }
        });

        int row = 0;
        int column = 0;
        getContents().add(nameLabel, column++, row);
        getContents().add(nameTextField, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(userFrameLabel, column++, row);
        getContents().add(userFramesComboBox, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(widthLabel, column++, row);
        getContents().add(widthNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(lengthLabel, column++, row);
        getContents().add(lengthNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(borderLabel, column++, row);
        getContents().add(borderNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(xOffsetLabel, column++, row);
        getContents().add(xOffsetNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(yOffsetLabel, column++, row);
        getContents().add(yOffsetNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(minInterferenceLabel, column++, row);
        getContents().add(minInterferenceTextField, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(horizontalRLabel, column++, row);
        getContents().add(horizontalRBox, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(verticalRLabel, column++, row);
        getContents().add(verticalRBox, column++, row, 3, 1);
        column = 0; row++;

        getContents().add(btnSave, column++, row, 5, 1);
        GridPane.setHalignment(btnSave, HPos.CENTER);
        GridPane.setMargin(btnSave, new Insets(10, 0, 0, 0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        nameTextField.setFocusListener(listener);
        widthNumbericTextField.setFocusListener(listener);
        lengthNumbericTextField.setFocusListener(listener);
        borderNumbericTextField.setFocusListener(listener);
        xOffsetNumbericTextField.setFocusListener(listener);
        yOffsetNumbericTextField.setFocusListener(listener);
        minInterferenceTextField.setFocusListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        getPresenter().updateUserFrames();
        if(unloadPallet != null) {
            nameTextField.setText(unloadPallet.getName());
            widthNumbericTextField.setText(unloadPallet.getLayout().getPalletWidth()+"");
            lengthNumbericTextField.setText(unloadPallet.getLayout().getPalletLength()+"");
            borderNumbericTextField.setText(unloadPallet.getLayout().getPalletFreeBorder()+"");
            xOffsetNumbericTextField.setText(unloadPallet.getLayout().getMinXGap()+"");
            yOffsetNumbericTextField.setText(unloadPallet.getLayout().getMinYGap()+"");
            minInterferenceTextField.setText(unloadPallet.getLayout().getMinInterferenceDistance()+"");
            userFramesComboBox.valueProperty().set(unloadPallet.getWorkAreaManagers().get(0).getUserFrame().getName());
            if(unloadPallet.getLayout().getHorizontalR() == 0) {
                if(!zeroButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    zeroButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                    oneEightyButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else {
                if(!oneEightyButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    oneEightyButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                    zeroButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            }
            
            if(unloadPallet.getLayout().getHorizontalR() == -90) {
                if(!minusButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    minusButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                    plusButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            } else {
                if(!plusButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)){
                    plusButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                    minusButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                }
            }
        }
    }

    public UnloadPallet getUnloadPallet() {
        return unloadPallet;
    }

    public void setUnloadPallet(UnloadPallet unloadPallet) {
        this.unloadPallet = unloadPallet;
    }

    public void setUserFrames(final List<String> userFrames) {
        userFrameNames.clear();
        userFrameNames.addAll(userFrames);
    }

}
