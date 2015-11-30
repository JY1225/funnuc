package eu.robojob.millassist.ui.admin.general;

import java.io.File;
import java.sql.SQLException;
import java.util.Set;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IconFlowSelector;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.user.User;
import eu.robojob.millassist.util.SizeManager;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class EmailAdminView extends AbstractFormView<EmailAdminPresenter>{

    private IconFlowSelector ifsUsers;

    private Label lblName;
    private FullTextField fulltxtName;
    private Label lblEmail;
    private FullTextField fulltxtEmail;
    private GridPane gpDetails;

    // image
    private StackPane spImage;
    private ImageView imageVw;

    private ScrollPane spDetails;
    private StackPane spControls;

    private FileChooser fileChooser;
    private String imagePath;

    // Email options
    private CheckBox cbEmailEndBatch;
    private CheckBox cbEmailError;
    private Label lblDelay;
    private IntegerTextField ntfDelay;

    // Buttons
    private Button btnEdit;
    private Button btnNew;
    private Button btnSave, btnTest, btnDelete;

    private static final String NAME = "EmailAdminView.name";
    private static final String EMAIL = "EmailAdminView.email";
    private static final String EDIT = "CNCMachineClampingsView.edit";
    private static final String NEW = "CNCMachineClampingsView.new";
    private static final String EMAIL_END_BATCH = "EmailAdminView.emailEndBatch";
    private static final String EMAIL_ERROR = "EmailAdminView.emailError";
    private static final String EMAIL_DELAY = "EmailAdminView.emailDelay";
    private static final String SAVE = "CNCMacineClampingsView.save";
    private static final String REMOVE = "CNCMacineClampingsView.remove";
    private static final String SEND_TEST = "EmailAdminView.sendTestMail";

    private static final int HGAP = 15;
    private static final int VGAP = 15;

    private static final String EDIT_PATH = "M 15.71875,0 3.28125,12.53125 0,20 7.46875,16.71875 20,4.28125 C 20,4.28105 19.7362,2.486 18.625,1.375 17.5134,0.2634 15.71875,0 15.71875,0 z M 3.53125,12.78125 c 0,0 0.3421,-0.0195 1.0625,0.3125 C 4.85495,13.21295 5.1112,13.41 5.375,13.625 l 0.96875,0.96875 c 0.2258,0.2728 0.4471,0.5395 0.5625,0.8125 C 7.01625,15.66565 7.25,16.5 7.25,16.5 L 3,18.34375 C 2.5602,17.44355 2.55565,17.44 1.65625,17 l 1.875,-4.21875 z";
    private static final String MAIL_PATH = "M30.841,15.832L63.38,0.565c1.492-0.701,3.141-0.727,4.581-0.205c1.439,0.52,2.688,1.591,3.388,3.083l7.634,16.27  c0.606,1.299,0.704,2.71,0.381,4.002c0,0.091-0.014,0.175-0.029,0.265c-0.052,0.268-0.158,0.51-0.307,0.718  c-0.568,1.272-1.569,2.352-2.927,2.984L43.559,42.949c-1.491,0.698-3.133,0.727-4.575,0.204c-1.358-0.487-2.542-1.467-3.262-2.827  l-17.727,8.321l5.928-3.488l14.049-8.822l1.111,2.368l6.304-11.393c0.484-0.876,1.589-1.195,2.468-0.711  c0.873,0.484,1.188,1.593,0.704,2.464l-5.62,10.163L73.769,24.77l-11.6-2.213c-0.985-0.184-1.632-1.134-1.444-2.119  c0.188-0.982,1.138-1.627,2.119-1.442l12.935,2.463c-0.023-0.068-0.052-0.135-0.084-0.204L68.271,5.43l-9.005,16.761  c-0.905,1.677-2.43,3.011-4.161,3.798c-1.755,0.802-3.75,1.063-5.563,0.595l-18.449-4.771c0.03,0.136,0.075,0.268,0.135,0.401  l-3.065,2.012l-0.202-0.43c-0.7-1.495-0.73-3.135-0.207-4.577C28.274,17.776,29.347,16.529,30.841,15.832L30.841,15.832z   M39.05,38.769l-0.031,0.013l0.013,0.02L39.05,38.769L39.05,38.769z M17.222,40.711l-7.427,4.363l23.07-10.821l1.67,3.563  l3.067-2.012c-0.985-2.1-1.815-4.041-2.795-6.138L17.222,40.711L17.222,40.711z M9.408,37.135L0,42.666l30.172-14.156l1.156,2.469  l3.069-2.017c-0.915-1.943-1.78-3.856-2.702-5.82L9.408,37.135L9.408,37.135z M33.361,18.656l17.092,4.421  c0.985,0.252,2.118,0.09,3.146-0.375c1.046-0.478,1.96-1.26,2.479-2.229l8.958-16.671l-0.116,0.05L33.361,18.656z";
    private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
    private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";
    private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";

    private static final String CSS_CLASS_GRIPPER_IMAGE_EDIT = "gripper-image-edit";

    private static final double ICONFLOWSELECTOR_PADDING = 20;

    private static final double ICONFLOWSELECTOR_WIDTH = SizeManager.WIDTH-(ICONFLOWSELECTOR_PADDING*2) - SizeManager.ADMIN_MENU_WIDTH - SizeManager.ADMIN_SUBMENU_WIDTH;
    private static final double IMG_WIDTH = 90;
    private static final double IMG_HEIGHT = 90;
    private static final double LBL_WIDTH = 25;
    private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
    private static final double BTN_WIDTH = BTN_HEIGHT * 3;


    public EmailAdminView() {
        super();
        build();
    }

    @Override
    protected void build() {
        getContents().setHgap(HGAP);
        getContents().setVgap(VGAP);
        getContents().setAlignment(Pos.TOP_CENTER);
        getContents().setPadding(new Insets(10, 0, 0, 0));

        getContents().getChildren().clear();

        createUserDetails();
        createActionButtons();

        getContents().add(createIconSelector(),0,0);
        getContents().add(spDetails, 0, 1);
        getContents().add(spControls,0,2);
        showDetails(false,true);
    }

    private VBox createIconSelector() {
        VBox vboxSelectUser = new VBox();
        vboxSelectUser.setAlignment(Pos.CENTER_LEFT);

        ifsUsers = new IconFlowSelector();
        ifsUsers.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        ifsUsers.setMaxWidth(ICONFLOWSELECTOR_WIDTH);
        ifsUsers.setMinWidth(ICONFLOWSELECTOR_WIDTH);

        HBox hboxButtons = new HBox();
        btnEdit = createButton(EDIT_PATH, null, Translator.getTranslation(EDIT), BTN_WIDTH, BTN_HEIGHT, null);
        btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_LEFT);
        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().clickedEdit();
            }
        });
        btnEdit.setDisable(true);
        btnNew = createButton(ADD_PATH, null, Translator.getTranslation(NEW), BTN_WIDTH, BTN_HEIGHT, null);
        btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_BAR_RIGHT);
        btnNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().clickedNew();
            }
        });
        hboxButtons.getChildren().addAll(btnEdit, btnNew);
        vboxSelectUser.setSpacing(10);
        vboxSelectUser.getChildren().addAll(ifsUsers, hboxButtons);
        return vboxSelectUser;
    }

    private void createUserDetails() {
        createImageView();

        spDetails = new ScrollPane();
        spDetails.setHbarPolicy(ScrollBarPolicy.NEVER);
        spDetails.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        spDetails.setPannable(false);
        spDetails.setFitToHeight(true);
        spDetails.setFitToWidth(true);

        gpDetails = new GridPane();
        gpDetails.setAlignment(Pos.CENTER);
        gpDetails.setVgap(10);
        spDetails.setContent(gpDetails);
        int column = 0;
        int row = 0;
        gpDetails.add(spImage, column++, row);
        GridPane gpNameHeight = new GridPane();
        gpNameHeight.setVgap(10);
        gpNameHeight.setHgap(10);

        lblName = new Label(Translator.getTranslation(NAME));
        gpNameHeight.add(lblName, 0, 0);
        fulltxtName = new FullTextField(100);
        fulltxtName.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
        fulltxtName.setOnChange(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
                validate();
            }
        });
        gpNameHeight.add(fulltxtName, 1, 0);

        lblEmail = new Label(Translator.getTranslation(EMAIL));
        gpNameHeight.add(lblEmail, 0,1);
        fulltxtEmail = new FullTextField(100);
        fulltxtEmail.setOnChange(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
                validate();
            }
        });
        fulltxtEmail.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
        gpNameHeight.add(fulltxtEmail, 1, 1);
        gpDetails.add(gpNameHeight, column++, row);

        column = 0;
        row++;
        cbEmailEndBatch = new CheckBox(Translator.getTranslation(EMAIL_END_BATCH));
        gpDetails.add(cbEmailEndBatch, column++, row, 2, 1);
        column = 0;
        row++;
        cbEmailError = new CheckBox(Translator.getTranslation(EMAIL_ERROR));
        gpDetails.add(cbEmailError, column++, row,2,1);
        column = 0;
        row++;
        lblDelay = new Label(Translator.getTranslation(EMAIL_DELAY));
        ntfDelay = new IntegerTextField(3);
        ntfDelay.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
        gpDetails.add(lblDelay, column++, row);
        gpDetails.add(ntfDelay, column++, row,2,1);

        gpDetails.setAlignment(Pos.TOP_CENTER);
        GridPane.setVgrow(spDetails, Priority.ALWAYS);
        GridPane.setHalignment(gpDetails, HPos.CENTER);
        gpDetails.setPadding(new Insets(10, 0, 10, 0));

    }

    private void createImageView() {
        spImage = new StackPane();
        spImage.setPadding(new Insets(5, 5, 5, 5));
        spImage.setPrefSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
        spImage.setMinSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
        spImage.setMaxSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
        spImage.getStyleClass().add(CSS_CLASS_GRIPPER_IMAGE_EDIT);
        spImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG (*.png)", "*.png");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    Image image = new Image("file:///" + file.getAbsolutePath(), IMG_WIDTH, IMG_HEIGHT, true, true);
                    imageVw.setImage(image);
                    imagePath = "file:///" + file.getAbsolutePath();
                    validate();
                }
            }
        });
        imageVw = new ImageView();
        imageVw.setFitWidth(IMG_WIDTH);
        imageVw.setFitHeight(IMG_HEIGHT);
        spImage.getChildren().add(imageVw);
    }

    private void createActionButtons() {
        btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().saveUser(fulltxtName.getText(), fulltxtEmail.getText(), imagePath, cbEmailEndBatch.isSelected(), cbEmailError.isSelected(), Integer.parseInt(ntfDelay.getText()));
            }
        });
        btnTest = createButton(MAIL_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SEND_TEST), BTN_WIDTH + 40, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().sendTestEmail();
            }
        });
        btnDelete = createButton(DELETE_ICON_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(REMOVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().deleteUser();
            }
        });
        btnDelete.getStyleClass().add("delete-btn");
        spControls = new StackPane();
        spControls.getChildren().addAll(btnDelete,btnTest, btnSave);
        spControls.setAlignment(Pos.CENTER);
        StackPane.setAlignment(btnDelete, Pos.CENTER_LEFT);
        StackPane.setAlignment(btnTest, Pos.CENTER);
        StackPane.setAlignment(btnSave, Pos.CENTER_RIGHT);
        spControls.setPadding(new Insets(0, 0, 10, 0));
    }

    @Override
    public void setTextFieldListener(final TextInputControlListener listener) {
        ntfDelay.setFocusListener(listener);
        fulltxtEmail.setFocusListener(listener);
        fulltxtName.setFocusListener(listener);

    }

    @Override
    public void refresh() {
        ifsUsers.clearItems();
        Set<User> users = null;
        try {
            users = GeneralMapper.getAllUsers();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int itemIndex = 0;
        for(final User user: users) {
            final int index = itemIndex;
            ifsUsers.addItem(itemIndex, user.getName(), user.getImageURL(), user.getEmail(), new EventHandler<MouseEvent>(){
                @Override
                public void handle(final MouseEvent arg0) {
                    getPresenter().userSelected(user, index);
                }
            });
            itemIndex++;
        }
        validate();
    }


    public void validate() {
        hideNotification();
        if(!fulltxtName.getText().equals("") && !fulltxtEmail.getText().equals("")) {
            btnSave.setDisable(false);
            btnTest.setDisable(false);
        } else {
            btnSave.setDisable(true);
            btnTest.setDisable(true);
        }
    }

    public void showDetails(final boolean show, final boolean clickedEdit) {
        spDetails.setVisible(show);
        spControls.setVisible(show);
        if(clickedEdit) {
            btnNew.setDisable(show);
            btnEdit.setDisable(!show);
            if(show) {
                btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
            } else {
                btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
            }
        } else {
            if(show) {
                btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
                btnNew.setDisable(!show);
                btnEdit.setDisable(show);
            } else {
                btnNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
                btnNew.setDisable(show);
                btnEdit.setDisable(!show);
            }
        }
    }

    public void userSelected(final User user, final int index) {
        ifsUsers.setSelected(index);
        fulltxtName.setText(user.getName());
        fulltxtEmail.setText(user.getEmail());
        String url = user.getImageURL();
        if (url != null) {
            url = url.replace("file:///", "");
        }
        if ((url != null) && ((new File(url)).exists() || (getClass().getClassLoader().getResource(url) != null))) {
            imageVw.setImage(new Image(user.getImageURL(), IMG_WIDTH, IMG_HEIGHT, true, true));
        } else {
            imageVw.setImage(new Image(UIConstants.IMG_NOT_FOUND_URL, IMG_WIDTH, IMG_HEIGHT, true, true));
        }
        imagePath = user.getImageURL();
        cbEmailEndBatch.setSelected(user.getEmailSettings().isEmailAtBatchEnd());
        cbEmailError.setSelected(user.getEmailSettings().isEmailAtError());
        ntfDelay.setText(user.getEmailSettings().getEmailErrorDelay()+"");
        validate();
    }

    public void reset() {
        ifsUsers.deselectAll();
        fulltxtName.setText("");
        fulltxtEmail.setText("");
        imagePath = null;
        imageVw.setImage(null);
        cbEmailEndBatch.setSelected(false);
        cbEmailError.setSelected(false);
        ntfDelay.setText("0");
        btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        validate();
    }

    public void enableEditButton(final boolean enable) {
        btnEdit.setDisable(!enable);
    }

}
