package eu.robojob.millassist.ui.automate.device.stacking.pallet;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.pallet.PalletLayoutView;

public class PalletLayoutPresenter extends AbstractFormPresenter<PalletLayoutView<PalletLayoutPresenter>, PalletMenuPresenter> 
implements UnloadPalletListener{

    private Pallet pallet;
    public PalletLayoutPresenter(PalletLayoutView<PalletLayoutPresenter> view, Pallet pallet) {
        super(view);
        getView().setPallet(pallet);
        this.pallet = pallet;
        this.pallet.addListener(this);
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
       return (pallet.getGridLayout().getStackingPositions().size() > 0);
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
        pallet.removeListener(this);
    }

}
