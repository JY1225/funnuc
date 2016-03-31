package eu.robojob.millassist.ui.admin.general;

import eu.robojob.millassist.ui.admin.MainMenuPresenter;
import eu.robojob.millassist.ui.admin.SubMenuAdminView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.SubContentPresenter;

public class GeneralAdminPresenter implements SubContentPresenter {

    private SubMenuAdminView view;
    private GeneralMenuPresenter generalMenuPresenter;
    private MainMenuPresenter parent;
    
    public GeneralAdminPresenter(final SubMenuAdminView view, final GeneralMenuPresenter generalMenuPresenter) {
        this.view = view;
        this.generalMenuPresenter = generalMenuPresenter;
        generalMenuPresenter.setParent(this);
        getView().setMenuView(generalMenuPresenter.getView());
    }
    
    public SubMenuAdminView getView() {
        return view;
    }
    @Override
    public void setActive(boolean active) {
    }

    @Override
    public void setParent(MainMenuPresenter mainContentPresenter) {
        this.parent = mainContentPresenter;
        
    }
    
    public MainMenuPresenter getParent() {
        return this.parent;
    }
    
    public void setContentView(final AbstractFormView<?> node) {
        getView().setContentView(node);
    }
    
    public void setTextFieldListener(final TextInputControlListener listener) {
        generalMenuPresenter.setTextFieldListener(listener);
    }

}
