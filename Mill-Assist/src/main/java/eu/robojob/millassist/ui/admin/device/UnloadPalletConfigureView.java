package eu.robojob.millassist.ui.admin.device;

import java.util.List;
import java.util.Set;

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
import eu.robojob.millassist.external.device.stacking.pallet.AbstractPallet;
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
    private ObservableList<String> layouts;
    
    private Region spacer;

    private Label nameLabel;
    private FullTextField nameTextField;

    private Label userFrameLabel;
    private ComboBox<String> userFramesComboBox;
    
    private Label stdPalletLayoutLabel;
    private ComboBox<String> cbbPalletLayouts;
    
    private Label maxHeightLabel;
    private NumericTextField maxHeightNumbericTextField;
    
    private Button btnSave;

    private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
    private static final String SAVE = "UnloadPallet.save";
    private static final String NAME = "UnloadPallet.name";
    private static final String USERFRAME = "UnloadPallet.userframe";
    private static final String HEIGHT = "UnloadPallet.height";
    private static final String STANDARD_LAYOUT = "UnloadPallet.standardLayout";
    
    public UnloadPalletConfigureView() {
        userFrameNames = FXCollections.observableArrayList();
        layouts = FXCollections.observableArrayList();
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
        
        stdPalletLayoutLabel = new Label(Translator.getTranslation(STANDARD_LAYOUT));
        cbbPalletLayouts = new ComboBox<String>();
        cbbPalletLayouts.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        cbbPalletLayouts.setMinSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        cbbPalletLayouts.setMaxSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        cbbPalletLayouts.setItems(layouts);
        
        maxHeightLabel = new Label(Translator.getTranslation(HEIGHT));
        maxHeightNumbericTextField = new NumericTextField(6);
        
        btnSave = createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getPresenter().saveData(nameTextField.getText(), userFramesComboBox.valueProperty().get(), cbbPalletLayouts.valueProperty().get());
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
        getContents().add(maxHeightLabel, column++, row);
        getContents().add(maxHeightNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        getContents().add(stdPalletLayoutLabel, column++, row);
        getContents().add(cbbPalletLayouts, column++, row, 3, 1);
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
        maxHeightNumbericTextField.setFocusListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        getPresenter().updateUserFrames();
        getPresenter().updatePalletLayouts();
        if(unloadPallet != null) {
            nameTextField.setText(unloadPallet.getName());
            userFramesComboBox.setValue(unloadPallet.getWorkAreaManagers().get(0).getUserFrame().getName());
            maxHeightNumbericTextField.setText(unloadPallet.getMaxHeight()+"");
            cbbPalletLayouts.setValue(unloadPallet.getDefaultLayout().getName());
        }
    }

    public AbstractPallet getUnloadPallet() {
        return unloadPallet;
    }

    public void setUnloadPallet(UnloadPallet unloadPallet) {
        this.unloadPallet = unloadPallet;
    }

    public void setUserFrames(final List<String> userFrames) {
        userFramesComboBox.valueProperty().set(null);
        userFrameNames.clear();
        userFrameNames.addAll(userFrames);
    }
    
    public void setPalletLayouts(Set<String> layoutsList) {
        cbbPalletLayouts.valueProperty().set(null);
        layouts.clear();
        layouts.addAll(layoutsList);
    }

}
