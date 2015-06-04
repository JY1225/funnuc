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
import javafx.scene.layout.Region;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class PalletConfigureView extends AbstractFormView<PalletConfigurePresenter>{

    private Pallet pallet;
    private ObservableList<String> userFrameNames;
    
    private Region spacer;

    private Label nameLabel;
    private FullTextField nameTextField;

    private Label userFrameLabel;
    private ComboBox<String> userFramesComboBox;
    
    private Button btnSave;

    private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
    private static final String SAVE = "UnloadPallet.save";
    private static final String NAME = "UnloadPallet.name";
    private static final String USERFRAME = "UnloadPallet.userframe";
    
    public PalletConfigureView() {
        userFrameNames = FXCollections.observableArrayList();
    }

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
        
        btnSave = createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getPresenter().saveData(nameTextField.getText(), userFramesComboBox.valueProperty().get());
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        getPresenter().updateUserFrames();
        if(pallet != null) {
            nameTextField.setText(pallet.getName());
            userFramesComboBox.setValue(pallet.getWorkAreaManagers().get(0).getUserFrame().getName());
        }
    }

    public Pallet getPallet() {
        return pallet;
    }

    public void setPallet(Pallet pallet) {
        this.pallet = pallet;
    }

    public void setUserFrames(final List<String> userFrames) {
        userFramesComboBox.valueProperty().set(null);
        userFrameNames.clear();
        userFrameNames.addAll(userFrames);
    }

}
