package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletDeviceSettings;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletListener;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.pallet.UnloadPalletLayoutView;

public class UnloadPalletLayoutPresenter extends AbstractFormPresenter<UnloadPalletLayoutView<UnloadPalletLayoutPresenter>, UnloadPalletMenuPresenter> implements UnloadPalletListener{

    private UnloadPallet unloadPallet;
    private PutStep putStep;
    
    public UnloadPalletLayoutPresenter(UnloadPalletLayoutView<UnloadPalletLayoutPresenter> view, final UnloadPallet unloadPallet, final PutStep putStep) {
        super(view);
        this.unloadPallet = unloadPallet;
        this.unloadPallet.addListener(this);
        this.putStep = putStep;
        unloadPallet.recalculateLayout();
        getView().build();
        getView().setUnloadPallet(unloadPallet);
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
       return (unloadPallet.getLayout().getStackingPositions().size() > 0);
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
    
    public void updateLayoutType(PalletLayoutType layoutType) {
        unloadPallet.getLayout().setLayoutType(layoutType);
        unloadPallet.recalculateLayout();
        ((UnloadPalletDeviceSettings) putStep.getProcessFlow().getDeviceSettings().get(unloadPallet)).setLayoutType(unloadPallet.getLayout().getLayoutType());
        unloadPallet.notifyLayoutChanged();
        this.putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(this.putStep.getProcessFlow(), this.putStep, true));
    }
}
