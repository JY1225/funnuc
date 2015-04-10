package eu.robojob.millassist.ui.admin.device;

import java.util.List;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.VBox;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletType;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class PalletLayoutConfigureView extends AbstractFormView<PalletLayoutConfigurePresenter>{

    private Button btnCreateNew;
    private Button btnEdit;
    
    private ComboBox<String> cbbPalletLayouts;
    
    private ObservableList<String> layouts;
    private PalletLayout layout;
    private ObservableList<PalletType> stdPalletTypes;
    private Region spacer;

    private VBox form;
    
    private GridPane fieldsPane;
    private Label nameLabel;
    private FullTextField nameTextField;

    private Label stdPalletTypeLabel;
    private ComboBox<PalletType> stdPalletTypeComboBox;

    private Label widthLabel;
    private NumericTextField widthNumbericTextField;

    private Label lengthLabel;
    private NumericTextField lengthNumbericTextField;
    
    private Label heightLabel;
    private NumericTextField heightNumbericTextField;

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
    private Button btnRemove;

    private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";
    private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
    private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z"; 
    
    private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
    private static final String SAVE = "UnloadPallet.save";
    private static final String NAME = "UnloadPallet.name";
    private static final String WIDTH = "UnloadPallet.width";
    private static final String LENGTH = "UnloadPallet.length";
    private static final String HEIGHT = "UnloadPallet.height";
    private static final String BORDER = "UnloadPallet.border";
    private static final String XOFFSET = "UnloadPallet.xoffset";
    private static final String YOFFSET = "UnloadPallet.yoffset";
    private static final String MIN_INT = "UnloadPallet.minint";
    private static final String HOR_R = "UnloadPallet.horizontalR";
    private static final String VER_R = "UnloadPallet.verticalR";
    private static final String STD_PALLET = "UnloadPallet.StandardPallet";
    private static final String DELETE = "UnloadPallet.Delete";
    
    private static final String EDIT = "RobotGripperView.edit";
    private static final String NEW = "RobotGripperView.new";
    
    
    private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
    private static final double BTN_WIDTH = BTN_HEIGHT * 3;
    
    private float horizontalRValue;
    private float verticalRValue;

    public PalletLayoutConfigureView() {
        stdPalletTypes = FXCollections.observableArrayList();
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

        stdPalletTypeLabel = new Label(Translator.getTranslation(STD_PALLET));
        stdPalletTypeComboBox = new ComboBox<PalletType>();
        stdPalletTypeComboBox.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        stdPalletTypeComboBox.setMinSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        stdPalletTypeComboBox.setMaxSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        stdPalletTypeComboBox.setItems(getStdPalletTypes());
        stdPalletTypeComboBox.valueProperty().addListener(new ChangeListener<PalletType>() {
            @Override
            public void changed(ObservableValue<? extends PalletType> observable,
                    PalletType oldValue, PalletType newValue) {
                if(newValue != null) {
                    setPredefinedPalletType(newValue);
                } else {
                    widthNumbericTextField.setDisable(false);
                    lengthNumbericTextField.setDisable(false);
                    heightNumbericTextField.setDisable(false);
                }
            }
        });
        
        widthLabel = new Label(Translator.getTranslation(WIDTH));
        widthNumbericTextField = new NumericTextField(6);

        lengthLabel = new Label(Translator.getTranslation(LENGTH));
        lengthNumbericTextField = new NumericTextField(6);
        
        heightLabel = new Label(Translator.getTranslation(HEIGHT));
        heightNumbericTextField = new NumericTextField(6);

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
                getPresenter().saveData(nameTextField.getText(),Float.parseFloat(widthNumbericTextField.getText()),Float.parseFloat(lengthNumbericTextField.getText()), Float.parseFloat(heightNumbericTextField.getText()),Float.parseFloat(borderNumbericTextField.getText()),Float.parseFloat(xOffsetNumbericTextField.getText()),Float.parseFloat(yOffsetNumbericTextField.getText()), Float.parseFloat(minInterferenceTextField.getText()), horizontalRValue, verticalRValue);
            }
        });
        
        btnRemove = createButton(DELETE_ICON_PATH, "", Translator.getTranslation(DELETE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getPresenter().removePalletLayout();
            }
        });
        btnRemove.getStyleClass().add("delete-btn");

        fieldsPane = new GridPane();
        fieldsPane.setVgap(5);
        fieldsPane.setHgap(15);
        fieldsPane.setPadding(new Insets(15, 0, 0, 0));
        int row = 0;
        int column = 0;
        fieldsPane.add(nameLabel, column++, row);
        fieldsPane.add(nameTextField, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(stdPalletTypeLabel, column++, row);
        fieldsPane.add(stdPalletTypeComboBox, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(widthLabel, column++, row);
        fieldsPane.add(widthNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(lengthLabel, column++, row);
        fieldsPane.add(lengthNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(heightLabel, column++, row);
        fieldsPane.add(heightNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(borderLabel, column++, row);
        fieldsPane.add(borderNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(xOffsetLabel, column++, row);
        fieldsPane.add(xOffsetNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(yOffsetLabel, column++, row);
        fieldsPane.add(yOffsetNumbericTextField, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(minInterferenceLabel, column++, row);
        fieldsPane.add(minInterferenceTextField, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(horizontalRLabel, column++, row);
        fieldsPane.add(horizontalRBox, column++, row, 3, 1);
        column = 0; row++;
        fieldsPane.add(verticalRLabel, column++, row);
        fieldsPane.add(verticalRBox, column++, row, 3, 1);
        column = 0; row++;
        
        form = new VBox();
        form.getChildren().addAll(fieldsPane,new HBox(10, btnSave, btnRemove));
        
        GridPane.setHalignment(btnSave, HPos.CENTER);
        GridPane.setMargin(btnSave, new Insets(10, 0, 0, 0));
        
        HBox hboxButtons = new HBox();
        btnEdit = createButton(EDIT_PATH, null, Translator.getTranslation(EDIT), BTN_WIDTH, BTN_HEIGHT, null);
        btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().clickedEdit(cbbPalletLayouts.getSelectionModel().getSelectedItem());
            }
        });
        btnCreateNew = createButton(ADD_PATH, null, Translator.getTranslation(NEW), BTN_WIDTH, BTN_HEIGHT, null);
        btnCreateNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
        btnCreateNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().clickedNew();
            }
        });
        
        cbbPalletLayouts = new ComboBox<String>();
        cbbPalletLayouts.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        cbbPalletLayouts.setMinSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        cbbPalletLayouts.setMaxSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
        cbbPalletLayouts.setItems(layouts);
        cbbPalletLayouts.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                if (newValue != null) {
                    btnEdit.setDisable(false);
                } else {
                    btnEdit.setDisable(true);
                }
            }
            
        });
        
        hboxButtons.getChildren().addAll(btnEdit, btnCreateNew);
        HBox topBox = new HBox();
        topBox.getChildren().addAll(cbbPalletLayouts,hboxButtons);
        topBox.setSpacing(15);
        getContents().add(topBox, 0, 0);
        getContents().add(form, 0, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        nameTextField.setFocusListener(listener);
        widthNumbericTextField.setFocusListener(listener);
        lengthNumbericTextField.setFocusListener(listener);
        heightNumbericTextField.setFocusListener(listener);
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
        hideNotification();
        getPresenter().setEditMode(false);
        reset();
        getPresenter().updatePalletLayouts();
        cbbPalletLayouts.getSelectionModel().select(0);
    }

    public PalletLayout getPalletLayout() {
        return layout;
    }

    public void palletLayoutSelected(PalletLayout layout) {
        this.layout = layout;
    }
    
    private void setPredefinedPalletType(PalletType type) {
        widthNumbericTextField.setDisable(true);
        lengthNumbericTextField.setDisable(true);
        heightNumbericTextField.setDisable(true);
        float width = type.getWidth();
        float height =type.getHeight();
        float length = type.getLength();
        
        if(type == PalletType.CUSTOM) {
            widthNumbericTextField.setDisable(false);
            lengthNumbericTextField.setDisable(false);
            heightNumbericTextField.setDisable(false);
        }
        widthNumbericTextField.setText(width+"");
        lengthNumbericTextField.setText(length+"");
        heightNumbericTextField.setText(height+"");
        
    }
    
    private ObservableList<PalletType> getStdPalletTypes() {
        List<PalletType> types = PalletType.getPalletTypes();
        stdPalletTypes.addAll(types);
        return stdPalletTypes;
    }

    public void setPalletLayouts(Set<String> layoutsList) {
        layouts.clear();
        layouts.addAll(layoutsList);
    }
    
    public void reset() {
        form.setVisible(false);
        btnCreateNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnCreateNew.setDisable(false);
        btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnEdit.setDisable(false);
        cbbPalletLayouts.setDisable(false);
    }
    
    public void showFormEdit() {
        form.setVisible(true);
        btnRemove.setVisible(true);
        btnCreateNew.setDisable(true);
        cbbPalletLayouts.setDisable(true);
        btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        if(layout != null) {
            stdPalletTypeComboBox.valueProperty().set(PalletType.getPalletTypeForLayout(layout));
            nameTextField.setText(layout.getName());
            widthNumbericTextField.setText(layout.getPalletWidth()+"");
            lengthNumbericTextField.setText(layout.getPalletLength()+"");
            heightNumbericTextField.setText(layout.getPalletHeight()+"");
            borderNumbericTextField.setText(layout.getPalletFreeBorder()+"");
            xOffsetNumbericTextField.setText(layout.getMinXGap()+"");
            yOffsetNumbericTextField.setText(layout.getMinYGap()+"");
            minInterferenceTextField.setText(layout.getMinInterferenceDistance()+"");
            
            
            if(layout.getHorizontalR() == 0) {
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
            
            if(layout.getVerticalR() == -90) {
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
    
    public void showFormNew() {
        form.setVisible(true);
        btnEdit.setDisable(true);
        btnRemove.setVisible(false);
        cbbPalletLayouts.setDisable(true);
        btnCreateNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        
        nameTextField.clear();
        widthNumbericTextField.clear();
        lengthNumbericTextField.clear();
        heightNumbericTextField.clear();
        borderNumbericTextField.clear();
        xOffsetNumbericTextField.clear();
        yOffsetNumbericTextField.clear();
        minInterferenceTextField.clear();
        oneEightyButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        plusButton.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        if(!zeroButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)) {
            zeroButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        }
        if(!minusButton.getStyleClass().contains(CSS_CLASS_FORM_BUTTON_ACTIVE)) {
            minusButton.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
        }
        horizontalRValue = 0;
        verticalRValue = -90;
        stdPalletTypeComboBox.getSelectionModel().clearSelection();
    }

}
