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
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class UnloadPalletConfigureView extends AbstractFormView<UnloadPalletConfigurePresenter> {

    private UnloadPallet unloadPallet;
    private ObservableList<String> userFrameNames;

    private Label nameLabel;
    private FullTextField nameTextField;

    private Label userFrameLabel;
    private ComboBox<String> userFramesComboBox;

    private Label maxHeightLabel;
    private NumericTextField maxHeightNumbericTextField;

    // Position of the clamping
    private Label lblPosition;
    private Label lblX;
    private NumericTextField numtxtX;
    private Label lblY;
    private NumericTextField numtxtY;
    private Label lblZ;
    private NumericTextField numtxtZ;
    private Label lblW;
    private NumericTextField numtxtW;
    private Label lblP;
    private NumericTextField numtxtP;
    private Label lblR;
    private NumericTextField numtxtR;
    
    private Button btnSave;

    private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
    private static final String SAVE = "UnloadPallet.save";
    private static final String NAME = "UnloadPallet.name";
    private static final String USERFRAME = "UnloadPallet.userframe";
    private static final String HEIGHT = "UnloadPallet.height";
    private static final String POSITION = "UnloadPallet.position";

    public UnloadPalletConfigureView() {
        userFrameNames = FXCollections.observableArrayList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void build() {
        getContents().setVgap(10);
        getContents().setHgap(10);
        getContents().setPadding(new Insets(25, 0, 0, 0));
        getContents().setAlignment(Pos.TOP_CENTER);

        nameLabel = new Label(Translator.getTranslation(NAME));
        nameTextField = new FullTextField(100);
        nameTextField.setMinWidth(UIConstants.TEXT_FIELD_HEIGHT*7 +8);
        nameTextField.setPrefWidth(UIConstants.TEXT_FIELD_HEIGHT*7 +8);
        nameTextField.setMaxWidth(UIConstants.TEXT_FIELD_HEIGHT*7 +8);

        userFrameLabel = new Label(Translator.getTranslation(USERFRAME));
        userFramesComboBox = new ComboBox<String>();
        userFramesComboBox.setMinWidth(UIConstants.TEXT_FIELD_HEIGHT*7 +8);
        userFramesComboBox.setPrefWidth(UIConstants.TEXT_FIELD_HEIGHT*7 +8);
        userFramesComboBox.setMaxWidth(UIConstants.TEXT_FIELD_HEIGHT*7 +8);
        userFramesComboBox.setItems(userFrameNames);

        maxHeightLabel = new Label(Translator.getTranslation(HEIGHT));
        maxHeightNumbericTextField = new NumericTextField(6);

        lblPosition = new Label(Translator.getTranslation(POSITION));
        lblX = new Label("X");
        numtxtX = new NumericTextField(10);
        lblY = new Label("Y");
        numtxtY = new NumericTextField(10);
        lblZ = new Label("Z");
        numtxtZ = new NumericTextField(10);
        lblW = new Label("W");
        numtxtW = new NumericTextField(10);
        lblP = new Label("P");
        numtxtP = new NumericTextField(10);
        lblR = new Label("R");
        numtxtR = new NumericTextField(10);
        
        btnSave = createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3,
                UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        getPresenter().saveData(nameTextField.getText(), userFramesComboBox.valueProperty().get(),
                                Float.parseFloat(numtxtX.getText()),
                                Float.parseFloat(numtxtY.getText()),
                                Float.parseFloat(numtxtZ.getText()),
                                Float.parseFloat(numtxtW.getText()),
                                Float.parseFloat(numtxtP.getText()),
                                Float.parseFloat(numtxtR.getText()));
                    }
                });

        int row = 0;
        int column = 0;
        getContents().add(nameLabel, column++, row);
        getContents().add(nameTextField, column++, row, 5, 1);
        column = 0;
        row++;
        getContents().add(userFrameLabel, column++, row);
        getContents().add(userFramesComboBox, column++, row, 5, 1);
        column = 0;
        row++;
        getContents().add(maxHeightLabel, column++, row);
        getContents().add(maxHeightNumbericTextField, column++, row, 5, 1);
        column = 0; row++;
        getContents().add(lblPosition, column++, row);
        getContents().add(lblX, column++, row);
        getContents().add(numtxtX, column++, row);
        getContents().add(lblY, column++, row);
        getContents().add(numtxtY, column++, row);
        getContents().add(lblZ, column++, row);
        getContents().add(numtxtZ, column++, row);
        column = 0; row++;
        column++;
        getContents().add(lblW, column++, row);
        getContents().add(numtxtW, column++, row);
        getContents().add(lblP, column++, row);
        getContents().add(numtxtP, column++, row);
        getContents().add(lblR, column++, row);
        getContents().add(numtxtR, column++, row);
        column = 0; row++;

        getContents().add(btnSave, column++, row, 7, 1);
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
        numtxtX.setFocusListener(listener);
        numtxtY.setFocusListener(listener);
        numtxtZ.setFocusListener(listener);
        numtxtW.setFocusListener(listener);
        numtxtP.setFocusListener(listener);
        numtxtR.setFocusListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        getPresenter().updateUserFrames();
        if (unloadPallet != null) {
            nameTextField.setText(unloadPallet.getName());
            userFramesComboBox.setValue(unloadPallet.getWorkAreaManagers().get(0).getUserFrame().getName());
            maxHeightNumbericTextField.setText(unloadPallet.getMaxHeight() + "");
            Coordinates relPosition = unloadPallet.getWorkAreas().get(0).getDefaultClamping().getRelativePosition();
            numtxtX.setText("" + relPosition.getX());
            numtxtY.setText("" + relPosition.getY());
            numtxtZ.setText("" + relPosition.getZ());
            numtxtW.setText("" + relPosition.getW());
            numtxtP.setText("" + relPosition.getP());
            numtxtR.setText("" + relPosition.getR());
        }
    }

    public UnloadPallet getUnloadPallet() {
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

}
