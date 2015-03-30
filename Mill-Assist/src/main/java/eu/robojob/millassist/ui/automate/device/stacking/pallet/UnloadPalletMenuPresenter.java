package eu.robojob.millassist.ui.automate.device.stacking.pallet;

import eu.robojob.millassist.ui.automate.AbstractMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class UnloadPalletMenuPresenter extends AbstractMenuPresenter<UnloadPalletMenuView> {
    
    private UnloadPalletLayoutPresenter unloadPalletLayoutPresenter;
    private UnloadPalletAddRemoveFinishedPresenter addRemovePresenter;
    
    public UnloadPalletMenuPresenter(UnloadPalletMenuView view, UnloadPalletLayoutPresenter unloadPalletLayoutPresenter, UnloadPalletAddRemoveFinishedPresenter addRemovePresenter) {
        super(view);
        this.unloadPalletLayoutPresenter = unloadPalletLayoutPresenter;
        this.addRemovePresenter = addRemovePresenter;
        getView().build();
    }
    
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        addRemovePresenter.setTextFieldListener(listener);
        
    }

    @Override
    protected void setPresenter() {
        getView().setPresenter(this);
        
    }

    @Override
    public void openFirst() {
        showLayout();
        
    }

    public void showLayout() {
        getView().setLayoutActive();
        getParent().setBottomRight(unloadPalletLayoutPresenter.getView());
    }
    
    public void showAddRemove() {
        getView().setAddRemoveActive();
        addRemovePresenter.getView().refresh();
        getParent().setBottomRight(addRemovePresenter.getView());
    }

    @Override
    public void unregisterListeners() {
        unloadPalletLayoutPresenter.unregister();
    }

}
