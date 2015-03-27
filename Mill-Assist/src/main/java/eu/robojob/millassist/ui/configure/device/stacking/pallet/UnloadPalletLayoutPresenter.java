package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
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
        getView().build();
        getView().setUnloadPallet(unloadPallet);
        getView().refresh();
    }
    
    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    @Override
    public boolean isConfigured() {
       return (unloadPallet.getLayout().getStackingPositions().size() > 0);
    }

    @Override
    public void layoutChanged() {
        putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(putStep.getProcessFlow(), putStep, false));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getView().build();
                getView().refresh();
            }
        });
    }

    @Override
    public void unregister() {
        unloadPallet.removeListener(this);
    }
}
