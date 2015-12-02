package eu.robojob.millassist.ui.admin.general;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IconFlowSelector;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.user.UserGroup;
import eu.robojob.millassist.util.SizeManager;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class EmailAdminView extends AbstractFormView<EmailAdminPresenter>{

    private IconFlowSelector ifsUsers;

    private Label lblName;
    private FullTextField fulltxtName;
    private Label lblEmail;
    private GridPane gpDetails;
    private VBox vBoxEmails;

    private Image defaultImage = new Image("file:///Extra/images/user_1.png", IMG_WIDTH, IMG_HEIGHT, true, true);

    // image
    private StackPane spImage;
    private ImageView imageVw;

    private ScrollPane spDetails;
    private StackPane spControls;

    private FileChooser fileChooser;
    private String imagePath = "file:///Extra/images/user_1.png";

    // Email options
    private CheckBox cbEmailEndBatch;
    private CheckBox cbEmailError;
    private Label lblDelay;
    private IntegerTextField ntfDelay;

    // Buttons
    private Button btnEdit;
    private Button btnNew;
    private Button btnSave, btnTest, btnDelete;
    private Button btnAddEmail;

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
    private static final String MAIL_PATH = "m 12.501312,7.7589713 13.461443,-6.315985 c 0.617243,-0.290005 1.299437,-0.300762 1.895168,-0.08481 0.595317,0.215125 1.11203,0.658199 1.401621,1.275442 l 3.1582,6.730928 c 0.250703,0.537399 0.291246,1.1211317 0.15762,1.6556347 0,0.03765 -0.0058,0.0724 -0.012,0.109631 -0.02151,0.110872 -0.06536,0.210988 -0.127007,0.297038 -0.234982,0.526229 -0.649098,0.973026 -1.210905,1.234486 l -13.462683,6.315985 c -0.61683,0.288764 -1.296128,0.300761 -1.892686,0.0844 -0.561807,-0.201472 -1.05163,-0.6069 -1.349495,-1.169535 l -7.3336916,3.442413 2.452424,-1.442992 5.8120966,-3.649677 0.459622,0.979646 2.607977,-4.713305 c 0.200231,-0.362402 0.657372,-0.494373 1.021016,-0.294142 0.361161,0.200232 0.491477,0.659027 0.291246,1.019362 L 17.506274,17.437942 30.2607,11.456641 25.461759,10.541119 c -0.407496,-0.07612 -0.675161,-0.469138 -0.597385,-0.8766337 0.07778,-0.406255 0.470792,-0.673093 0.876634,-0.596558 l 5.351232,1.0189477 c -0.0095,-0.02813 -0.02151,-0.05585 -0.03475,-0.0844 L 27.98617,3.4556453 24.260786,10.3897 c -0.3744,0.693778 -1.005296,1.245657 -1.721413,1.57124 -0.726047,0.331789 -1.551382,0.439765 -2.301424,0.246152 l -7.632384,-1.973771 c 0.01241,0.05626 0.03103,0.110872 0.05585,0.165895 l -1.267996,0.832368 -0.08357,-0.177892 c -0.289591,-0.618484 -0.302002,-1.2969547 -0.08564,-1.8935127 0.215125,-0.596972 0.659027,-1.112858 1.277097,-1.401208 l 0,0 z m 3.396078,9.4890777 -0.01282,0.0054 0.0054,0.0083 0.0074,-0.01365 0,0 z m -9.0302816,0.803409 -3.072564,1.804981 9.5441006,-4.476668 0.690882,1.47402 1.268823,-0.832368 c -0.407496,-0.868774 -0.750869,-1.671769 -1.156296,-2.539302 l -7.2749456,4.569337 0,0 z m -3.232666,-1.479398 -3.89210699,2.288185 12.48221059,-5.856363 0.478239,1.02143 1.269651,-0.834437 c -0.378537,-0.803822 -0.736389,-1.595234 -1.117822,-2.407744 l -9.2201716,5.788929 0,0 z M 13.543841,8.9272653 20.614832,10.75624 c 0.407496,0.104253 0.87622,0.03723 1.301506,-0.155138 0.432732,-0.197749 0.810855,-0.521264 1.025566,-0.9221407 l 3.705941,-6.896823 -0.04799,0.02068 -13.056015,6.124442 z";
    private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
    private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";
    private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
    private static final String DELETE_SIMPLE_ICON = "M 3.6875 1.5 C 3.6321551 1.5180584 3.5743859 1.5512948 3.53125 1.59375 L 1.59375 3.53125 C 1.423759 3.7005604 1.4751389 4.0379792 1.6875 4.25 L 7.46875 10 L 1.6875 15.75 C 1.4751389 15.962191 1.423759 16.297908 1.59375 16.46875 L 3.53125 18.40625 C 3.7020918 18.577772 4.0386598 18.55526 4.25 18.34375 L 10 12.5625 L 15.75 18.34375 C 15.962021 18.55526 16.297908 18.577772 16.46875 18.40625 L 18.40625 16.46875 C 18.57556 16.297908 18.524691 15.962191 18.3125 15.75 L 12.53125 10 L 18.3125 4.25 C 18.524691 4.0379792 18.57556 3.7005604 18.40625 3.53125 L 16.46875 1.59375 C 16.297908 1.4244396 15.962021 1.4759897 15.75 1.6875 L 10 7.46875 L 4.25 1.6875 C 4.1439896 1.5809791 4.0004088 1.5136129 3.875 1.5 C 3.8124658 1.4931936 3.7428449 1.4819416 3.6875 1.5 z ";

    private static final String CSS_CLASS_GRIPPER_IMAGE_EDIT = "gripper-image-edit";
    private static final String CSS_CLASS_DELETE_OPEN_BTN = "delete-open";

    private static final double ICONFLOWSELECTOR_PADDING = 20;

    private static final double ICONFLOWSELECTOR_WIDTH = SizeManager.WIDTH-(ICONFLOWSELECTOR_PADDING*2) - SizeManager.ADMIN_MENU_WIDTH - SizeManager.ADMIN_SUBMENU_WIDTH;
    private static final double IMG_WIDTH = 90;
    private static final double IMG_HEIGHT = 90;
    private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
    private static final double BTN_WIDTH = BTN_HEIGHT * 3;

    private Set<FullTextField> emailFields = new HashSet<>();


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
        gpDetails.setVgap(VGAP);
        gpDetails.setHgap(2*HGAP);
        spDetails.setContent(gpDetails);

        gpDetails.add(createLeftDetails(), 0, 1);

        VBox right = createRightDetails();

        gpDetails.add(right, 1, 0,1,2);
        GridPane.setHgrow(right, Priority.ALWAYS);
        GridPane.setVgrow(right, Priority.ALWAYS);
        gpDetails.setAlignment(Pos.TOP_CENTER);
        GridPane.setHgrow(spDetails, Priority.NEVER);
        GridPane.setVgrow(spDetails, Priority.ALWAYS);
        GridPane.setHalignment(gpDetails, HPos.LEFT);

    }

    private GridPane createLeftDetails() {
        int column = 0;
        int row = 0;
        GridPane gpNameHeight = new GridPane();
        gpNameHeight.setVgap(10);
        gpNameHeight.setHgap(10);
        gpNameHeight.add(spImage, column++,row);
        column = 0;
        row++;
        lblName = new Label(Translator.getTranslation(NAME));
        gpNameHeight.add(lblName, column++, row);
        fulltxtName = new FullTextField(32);
        fulltxtName.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
        fulltxtName.setPrefWidth(UIConstants.TEXT_FIELD_HEIGHT * 5);
        fulltxtName.setOnChange(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
                validate();
            }
        });
        gpNameHeight.add(fulltxtName, column++, row);
        column = 0;
        row++;
        lblDelay = new Label(Translator.getTranslation(EMAIL_DELAY));
        ntfDelay = new IntegerTextField(3);
        ntfDelay.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
        gpNameHeight.add(lblDelay, column++, row);
        gpNameHeight.add(ntfDelay, column++, row);
        column = 0;
        row++;
        cbEmailEndBatch = new CheckBox(Translator.getTranslation(EMAIL_END_BATCH));
        gpNameHeight.add(cbEmailEndBatch, column++, row, 2,1);
        column = 0;
        row++;
        cbEmailError = new CheckBox(Translator.getTranslation(EMAIL_ERROR));
        gpNameHeight.add(cbEmailError, column++, row, 2,1);
        return gpNameHeight;
    }

    private VBox createRightDetails() {
        HBox emailHeader = new HBox(HGAP);

        lblEmail = new Label(Translator.getTranslation(EMAIL));
        //        gpDetails.add(lblEmail, 0, 1);
        btnAddEmail = createButton(ADD_PATH, CSS_CLASS_FORM_BUTTON,Translator.getTranslation(NEW), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                vBoxEmails.getChildren().add(getEmailTextField(""));
                validate();
            }
        });
        //        gpDetails.add(btnAddEmail, 1, 1);
        VBox vBoxEmail = new VBox(VGAP);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        emailHeader.getChildren().addAll(lblEmail, spacer, btnAddEmail);
        // Create  email scroll pane
        ScrollPane scpEmails = new ScrollPane();
        scpEmails.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scpEmails.setHbarPolicy(ScrollBarPolicy.NEVER);
        vBoxEmails = new VBox(VGAP);
        vBoxEmails.getChildren().addAll(getEmailTextField(""));
        vBoxEmail.getChildren().addAll(emailHeader, scpEmails);
        VBox.setVgrow(scpEmails, Priority.ALWAYS);
        scpEmails.setContent(vBoxEmails);
        return vBoxEmail;
    }

    private HBox getEmailTextField(final String email) {
        final HBox result = new HBox(HGAP);
        final FullTextField textField = new FullTextField(100);
        textField.setText(email);
        emailFields.add(textField);
        textField.setFocusListener(fulltxtName.getFocusListener());
        textField.setOnChange(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
                validate();
            }
        });
        Button removeButton = createButton(DELETE_SIMPLE_ICON, CSS_CLASS_DELETE_OPEN_BTN, "", 32, 0, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                vBoxEmails.getChildren().remove(result);
                emailFields.remove(textField);
                validate();
            }
        });
        HBox.setMargin(removeButton, new Insets(16, 0, 0, 0));
        result.getChildren().addAll(textField, removeButton);
        return result;
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
        imageVw.setImage(defaultImage);
        spImage.getChildren().add(imageVw);
    }

    private List<String> getEmails() {
        List<String> result = new ArrayList<>();
        for(FullTextField textField: emailFields) {
            result.add(textField.getText());
        }
        return result;
    }

    private void createActionButtons() {
        btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().saveUser(fulltxtName.getText(), getEmails(), imagePath, cbEmailEndBatch.isSelected(), cbEmailError.isSelected(), Integer.parseInt(ntfDelay.getText()));
            }
        });
        btnTest = createButton(MAIL_PATH, CSS_CLASS_FORM_BUTTON, Translator.getTranslation(SEND_TEST), BTN_WIDTH + 60, BTN_HEIGHT, new EventHandler<ActionEvent>() {
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
        fulltxtName.setFocusListener(listener);
        for(FullTextField ftf: emailFields) {
            ftf.setFocusListener(listener);
        }
    }

    @Override
    public void refresh() {
        ifsUsers.clearItems();
        Set<UserGroup> users = null;
        try {
            users = GeneralMapper.getAllUserGroups();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int itemIndex = 0;
        for(final UserGroup user: users) {
            final int index = itemIndex;
            ifsUsers.addItem(itemIndex, user.getName(), user.getImageURL(), "", new EventHandler<MouseEvent>(){
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
        boolean isValid = true;
        for(String email: getEmails()) {
            if("".equals(email)) {
                isValid = false;
                break;
            }
        }
        if(getEmails().size() == 0) {
            isValid = false;
        }
        if(!fulltxtName.getText().equals("") && isValid) {
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

    public void userSelected(final UserGroup user, final int index) {
        ifsUsers.setSelected(index);
        fulltxtName.setText(user.getName());

        vBoxEmails.getChildren().clear();
        emailFields.clear();
        for(String email: user.getEmails()) {
            vBoxEmails.getChildren().add(getEmailTextField(email));
        }

        String url = user.getImageURL();
        if (url != null) {
            url = url.replace("file:///", "");
        }
        if ((url != null) && ((new File(url)).exists() || (getClass().getClassLoader().getResource(url) != null)) && !"".equals(url)) {
            imageVw.setImage(new Image(user.getImageURL(), IMG_WIDTH, IMG_HEIGHT, true, true));
            imagePath = user.getImageURL();
        } else {
            imageVw.setImage(defaultImage);
            imagePath = "file:///Extra/images/user_1.png";
        }

        cbEmailEndBatch.setSelected(user.getEmailSettings().isEmailAtBatchEnd());
        cbEmailError.setSelected(user.getEmailSettings().isEmailAtError());
        ntfDelay.setText(user.getEmailSettings().getEmailErrorDelay()+"");
        validate();
    }

    public void reset() {
        ifsUsers.deselectAll();
        fulltxtName.setText("");
        imagePath = "file:///Extra/images/user_1.png";
        imageVw.setImage(defaultImage);
        cbEmailEndBatch.setSelected(false);
        cbEmailError.setSelected(false);
        ntfDelay.setText("0");
        btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        btnNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
        vBoxEmails.getChildren().clear();
        emailFields.clear();
        vBoxEmails.getChildren().add(getEmailTextField(""));

        validate();
    }

    public void enableEditButton(final boolean enable) {
        btnEdit.setDisable(!enable);
    }

}
