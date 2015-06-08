package eu.robojob.millassist.ui.automate.device.stacking.pallet;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletListener;
import eu.robojob.millassist.ui.configure.device.stacking.pallet.UnloadPalletMenuPresenter;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.pallet.UnloadPalletLayoutView;

public class UnloadPalletLayoutPresenter extends AbstractFormPresenter<UnloadPalletLayoutView<UnloadPalletLayoutPresenter>, UnloadPalletMenuPresenter> 
implements UnloadPalletListener{

    private UnloadPallet unloadPallet;
    public UnloadPalletLayoutPresenter(UnloadPalletLayoutView<UnloadPalletLayoutPresenter> view, UnloadPallet unloadPallet) {
        super(view);
        getView().setUnloadPallet(unloadPallet);
        this.unloadPallet = unloadPallet;
        this.unloadPallet.addListener(this);
        getView().setControlsHidden(true);
        getView().build();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigured() {
       return (unloadPallet.getPalletLayout().getStackingPositions().size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void layoutChanged() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getView().build();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister() {
        unloadPallet.removeListener(this);
    }

}
