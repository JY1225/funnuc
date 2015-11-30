package eu.robojob.millassist.ui.admin.general;

import eu.robojob.millassist.ui.admin.AbstractSubMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class GeneralMenuPresenter extends AbstractSubMenuPresenter<GeneralMenuView, GeneralAdminPresenter>{

    private EmailAdminPresenter emailAdminPresenter;
    
    public GeneralMenuPresenter(GeneralMenuView view, EmailAdminPresenter emailAdminPresenter) {
        super(view);
        this.emailAdminPresenter = emailAdminPresenter;
    }

    @Override
    public boolean isConfigured() {
        return false;
    }

    @Override
    public void setBlocked(boolean blocked) {
        // No-op
    }

    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        emailAdminPresenter.setTextFieldListener(listener);
        
    }

    @Override
    protected void setPresenter() {
        getView().setPresenter(this);
    }

    @Override
    public void openFirst() {
        configureEmail();
    }

    @Override
    public void unregisterListeners() {
        // TODO Auto-generated method stub
        
    }
    
    public void configureEmail() {
        getView().setConfigureEmailActive();
        getParent().setContentView(emailAdminPresenter.getView());
    }

}
