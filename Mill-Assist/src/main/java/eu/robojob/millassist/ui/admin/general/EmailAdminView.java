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
    private static final String MAIL_PATH = "M 15.20502,11.695603 29.072374,4.8135773 c 0.635857,-0.3159952 1.33862,-0.3277164 1.952314,-0.092411 0.613267,0.2344044 1.145562,0.717187 1.443885,1.3897475 l 3.253432,7.3341559 c 0.258262,0.585561 0.300027,1.221608 0.162372,1.804013 0,0.04102 -0.006,0.07888 -0.01244,0.119456 -0.02215,0.120809 -0.06734,0.229898 -0.130831,0.323659 -0.242069,0.573389 -0.668672,1.06023 -1.247419,1.34512 l -13.868629,6.882027 c -0.63543,0.314642 -1.33521,0.327715 -1.949757,0.09198 -0.578677,-0.219544 -1.08327,-0.661305 -1.390117,-1.274364 l -7.5548285,3.750923 2.5263735,-1.572314 5.987353,-3.976763 0.473481,1.067443 2.686616,-5.135713 c 0.206268,-0.39488 0.677196,-0.538679 1.051805,-0.320503 0.37205,0.218178 0.506296,0.718089 0.300027,1.110717 l -2.395111,4.581257 13.139018,-6.517349 -4.943646,-0.997571 c -0.419784,-0.08294 -0.69552,-0.511182 -0.615399,-0.955197 0.08011,-0.442664 0.484988,-0.733416 0.903067,-0.650022 l 5.512592,1.110265 c -0.0097,-0.03065 -0.02215,-0.06085 -0.03579,-0.09197 L 31.156803,7.0066115 27.319084,14.562098 c -0.385689,0.755954 -1.03561,1.357293 -1.77332,1.712055 -0.74794,0.361525 -1.59816,0.479178 -2.37082,0.268213 l -7.862527,-2.150661 c 0.01279,0.06131 0.03197,0.120808 0.05753,0.180763 l -1.306226,0.906964 -0.08609,-0.193835 c -0.298322,-0.673912 -0.311108,-1.413188 -0.08822,-2.063209 0.221612,-0.650473 0.678899,-1.212593 1.315607,-1.526785 l 0,0 z m 3.498481,10.339493 -0.01322,0.0058 0.0055,0.009 0.0076,-0.01482 0,0 z m -9.3025769,0.875411 -3.165213,1.966744 9.8318899,-4.87787 0.711714,1.606122 1.307083,-0.906965 c -0.419783,-0.946634 -0.77351,-1.821593 -1.191163,-2.766875 l -7.4943109,4.978844 0,0 z m -3.3301427,-1.611982 -4.0094681,2.493251 12.8585947,-6.381211 0.492659,1.11297 1.307937,-0.909219 c -0.389952,-0.87586 -0.758594,-1.7382 -1.151529,-2.623527 l -9.4981936,6.307736 0,0 z M 16.278984,12.9686 l 7.284208,1.992887 c 0.419783,0.113597 0.90264,0.04057 1.340751,-0.169041 0.445779,-0.215471 0.835304,-0.56798 1.05649,-1.004783 l 3.817688,-7.5149184 -0.04944,0.022533 -13.4497,6.6733165 z";
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
