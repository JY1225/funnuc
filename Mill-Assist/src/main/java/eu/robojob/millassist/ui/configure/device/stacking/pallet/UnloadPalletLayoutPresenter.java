package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import javafx.application.Platform;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletDeviceSettings;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletListener;
import eu.robojob.millassist.process.InterventionStep;
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
        getView().setUnloadPallet(unloadPallet);
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
                updateIntervention();
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
    
    /**
     * Update the layout type of the pallet. This updates the device settings and the unload pallet itself.
     * @param layoutType The new layout type of the pallet
     */
    public void updateLayoutType(PalletLayoutType layoutType) {
        unloadPallet.getLayout().setLayoutType(layoutType);
        unloadPallet.recalculateLayout();
        ((UnloadPalletDeviceSettings) putStep.getProcessFlow().getDeviceSettings().get(unloadPallet)).setLayoutType(unloadPallet.getLayout().getLayoutType());
        unloadPallet.notifyLayoutChanged();
        this.putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(this.putStep.getProcessFlow(), this.putStep, true));
        updateIntervention();
    }
    
    public void updateLayersBeforeCardboard(int nbOfLayersBeforeCardboard) {
        unloadPallet.getLayout().setLayersBeforeCardBoard(nbOfLayersBeforeCardboard);
        ((UnloadPalletDeviceSettings) putStep.getProcessFlow().getDeviceSettings().get(unloadPallet)).setLayersBeforeCardBoard(nbOfLayersBeforeCardboard);
        this.putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(this.putStep.getProcessFlow(), this.putStep, false));
        updateIntervention();
    }
    
    private void updateIntervention() {
        if(unloadPallet.getLayout().getLayersBeforeCardBoard() < unloadPallet.getLayers()) {
            int nextStep = this.putStep.getProcessFlow().getStepIndex(putStep)+1;
            if(this.putStep.getProcessFlow().getProcessSteps().size() == nextStep) {
                this.putStep.getProcessFlow().addStepAfter(this.putStep, new InterventionStep(new DeviceInterventionSettings(unloadPallet, unloadPallet.getWorkAreas().get(0)), unloadPallet.getLayout().getLayersBeforeCardBoard()*unloadPallet.getMaxPiecesPerLayerAmount()));
            } else {
                if(this.putStep.getProcessFlow().getStep(nextStep) instanceof InterventionStep){
                    InterventionStep iStep = (InterventionStep)this.putStep.getProcessFlow().getStep(nextStep);
                    iStep.setFrequency(unloadPallet.getLayout().getLayersBeforeCardBoard()*unloadPallet.getMaxPiecesPerLayerAmount());
                }
            }
            getMenuPresenter().processFlowUpdated();
        }
    }
}
