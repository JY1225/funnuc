package eu.robojob.millassist.ui.admin.general;

import java.sql.SQLException;

import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.user.User;
import eu.robojob.millassist.user.UserEmailSettings;
import eu.robojob.millassist.util.EmailUtil;

public class EmailAdminPresenter  extends AbstractFormPresenter<EmailAdminView, GeneralMenuPresenter>{

    private boolean editMode;
    private User selectedUser;

    public EmailAdminPresenter(final EmailAdminView view) {
        super(view);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    @Override
    public boolean isConfigured() {
        // TODO Auto-generated method stub
        return false;
    }

    public void clickedEdit() {
        if(editMode) {
            getView().showDetails(false,true);
            getView().reset();
        } else {
            getView().showDetails(true,true);
        }
        editMode = !editMode;
    }

    public void clickedNew() {
        selectedUser = null;
        getView().reset();
        if(editMode) {
            getView().showDetails(false, false);
        } else {
            getView().showDetails(true, false);
        }
        editMode = !editMode;
    }

    public void userSelected(final User user, final int index) {
        if (!editMode) {
            selectedUser = user;
            getView().userSelected(selectedUser,index);
            getView().enableEditButton(true);
        }
    }

    public void saveUser(final String name, final String email, final String imageURL, final boolean emailAtBatchEnd, final boolean emailError, final int erroDelay) {
        if(selectedUser == null) {
            User newUser = new User(0,name, email, imageURL);
            UserEmailSettings settings = new UserEmailSettings(emailAtBatchEnd, emailError, erroDelay);
            newUser.setEmailSettings(settings);
            selectedUser = newUser;
        } else {
            selectedUser.setEmail(email);
            selectedUser.setName(name);
            selectedUser.setImageURL(imageURL);
            selectedUser.getEmailSettings().setEmailAtBatchEnd(emailAtBatchEnd);
            selectedUser.getEmailSettings().setEmailAtError(emailError);
            selectedUser.getEmailSettings().setEmailErrorDelay(erroDelay);
        }
        try {
            GeneralMapper.updateUser(selectedUser);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getView().refresh();
    }

    public void copyUser(final String name, final String email, final String imageURL, final boolean emailAtBatchEnd, final boolean emailError, final int erroDelay) {
        selectedUser = null;
        saveUser(name, email, imageURL, emailAtBatchEnd, emailError, erroDelay);
    }

    public void deleteUser() {
        if(selectedUser != null) {
            try {
                GeneralMapper.deleteUser(selectedUser.getId());
                selectedUser = null;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        getView().refresh();
        clickedEdit();
    }

    public void sendTestEmail() {
        if (selectedUser != null) {
            EmailUtil.sendMailTest(selectedUser);
        }
    }

}
