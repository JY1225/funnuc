package eu.robojob.millassist.ui.automate.device.stacking.pallet;

import eu.robojob.millassist.ui.automate.AbstractMenuPresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class PalletMenuPresenter extends AbstractMenuPresenter<PalletMenuView> {

    private PalletAddRemoveFinishedPresenter addRemoveFinishedPresenter;
    private PalletLayoutPresenter palletLayoutPresenter;

    public PalletMenuPresenter(final PalletMenuView view, PalletLayoutPresenter palletLayoutPresenter,
            PalletAddRemoveFinishedPresenter addRemoveFinishedPresenter) {
        super(view);
        if (palletLayoutPresenter != null) {
            this.palletLayoutPresenter = palletLayoutPresenter;
        }

        if (addRemoveFinishedPresenter != null) {
            this.addRemoveFinishedPresenter = addRemoveFinishedPresenter;
        }
        getView().build();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        addRemoveFinishedPresenter.setTextFieldListener(listener);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setPresenter() {
        getView().setPresenter(this);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openFirst() {
        showLayout();

    }

    public void showLayout() {
        getView().setLayoutActive();
        getParent().setBottomRight(palletLayoutPresenter.getView());
    }

    public void showAddRemove() {
        getView().setAddRemoveActive();
        addRemoveFinishedPresenter.getView().refresh();
        getParent().setBottomRight(addRemoveFinishedPresenter.getView());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterListeners() {
        palletLayoutPresenter.unregister();
    }

}
