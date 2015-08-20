package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletListener;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.pallet.PalletLayoutView;
import eu.robojob.millassist.ui.general.device.stacking.pallet.UnloadPalletLayoutView;

public class PalletLayoutPresenter extends AbstractFormPresenter<PalletLayoutView<PalletLayoutPresenter>, PalletMenuPresenter> implements UnloadPalletListener{

    private Pallet pallet;
    
    public PalletLayoutPresenter(PalletLayoutView<PalletLayoutPresenter> view, final Pallet pallet) {
        super(view);
        this.pallet = pallet;
        this.pallet.addListener(this);
        getView().setPallet(pallet);
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
       return true;
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
