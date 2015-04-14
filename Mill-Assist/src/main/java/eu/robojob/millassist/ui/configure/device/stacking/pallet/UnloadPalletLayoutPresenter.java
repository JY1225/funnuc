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
import eu.robojob.millassist.process.event.ProcessChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.ui.general.device.stacking.pallet.UnloadPalletLayoutView;
import eu.robojob.millassist.util.Translator;

public class UnloadPalletLayoutPresenter extends AbstractFormPresenter<UnloadPalletLayoutView<UnloadPalletLayoutPresenter>, UnloadPalletMenuPresenter> implements UnloadPalletListener{

    private static final String NOT_ENOUGH_LAYERS = "UnloadPalletLayoutView.NotEnoughLayers";
    
    private UnloadPallet unloadPallet;
    private PutStep putStep;
    private InterventionStep interventionStep;
    
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
       return (unloadPallet.getLayout().getStackingPositions().size() > 0) && (unloadPallet.getLayout().getLayersBeforeCardBoard() < unloadPallet.getLayers());
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
                updateIntervention();
            }
        });
    }

    public void refresh() {
        updateIntervention();
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
        this.putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(this.putStep.getProcessFlow(), this.putStep, true));
//        unloadPallet.notifyLayoutChanged();
        layoutChanged();
    }
    
    public void updateLayersBeforeCardboard(int nbOfLayersBeforeCardboard) {
        unloadPallet.getLayout().setLayersBeforeCardBoard(nbOfLayersBeforeCardboard);
        if(nbOfLayersBeforeCardboard < unloadPallet.getLayers()) {
            getView().hideNotification();
            ((UnloadPalletDeviceSettings) putStep.getProcessFlow().getDeviceSettings().get(unloadPallet)).setLayersBeforeCardBoard(nbOfLayersBeforeCardboard);
            this.putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(this.putStep.getProcessFlow(), this.putStep, false));
            updateIntervention();
            if(nbOfLayersBeforeCardboard != 0) {
                getView().showCardboardThickness(true);
            }
            else {
                getView().showCardboardThickness(false);
            }
        } else {
            unloadPallet.getLayout().setLayersBeforeCardBoard(nbOfLayersBeforeCardboard);
            getView().showNotification(Translator.getTranslation(NOT_ENOUGH_LAYERS), Type.WARNING);
        }
    }
    
    public void updateCardBoardThickness(final float thickness) {
        unloadPallet.getLayout().setCardBoardThickness(thickness);
        if(unloadPallet.getLayout().getLayersBeforeCardBoard() > unloadPallet.getLayers()) {
            getView().showNotification(Translator.getTranslation(NOT_ENOUGH_LAYERS), Type.WARNING);
        } else {
            getView().hideNotification();
        }
        ((UnloadPalletDeviceSettings) putStep.getProcessFlow().getDeviceSettings().get(unloadPallet)).setCardBoardThickness(thickness);
        this.putStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(this.putStep.getProcessFlow(), this.putStep, false));
    }
    
    public void updateIntervention() {
        if(unloadPallet.getLayout().getLayersBeforeCardBoard() < unloadPallet.getLayers()) {
            int frequency = unloadPallet.getLayout().getLayersBeforeCardBoard()*unloadPallet.getMaxPiecesPerLayerAmount();
            if(interventionStep == null) {
                if(this.putStep.getProcessFlow().getStep(0) instanceof InterventionStep) {
                    interventionStep = (InterventionStep)this.putStep.getProcessFlow().getStep(0);
                    updateInterventionStep(frequency);
                }
                else {
                    addInterventionStep(frequency);
                }
               
            } else {
                updateInterventionStep(frequency);
            }
        }
    }
    
    private void addInterventionStep(final int frequency) {
        if(unloadPallet.getLayout().getLayersBeforeCardBoard()*unloadPallet.getMaxPiecesPerLayerAmount() != 0) {
            interventionStep = new InterventionStep(new DeviceInterventionSettings(unloadPallet, unloadPallet.getWorkAreas().get(0)), frequency);
            this.putStep.getProcessFlow().addStepBefore(this.putStep.getProcessFlow().getStep(0), interventionStep);
            this.putStep.getProcessFlow().processProcessFlowEvent(new ProcessChangedEvent(this.putStep.getProcessFlow()));
        }
    }
    
    private void updateInterventionStep(final int frequency) {
        if(interventionStep.getFrequency() != frequency) {
            if(unloadPallet.getLayout().getLayersBeforeCardBoard()*unloadPallet.getMaxPiecesPerLayerAmount() != 0) {
                interventionStep.setFrequency(frequency);
            } else {
                this.putStep.getProcessFlow().removeStep(interventionStep);
                interventionStep = null;
            }
            this.putStep.getProcessFlow().processProcessFlowEvent(new ProcessChangedEvent(this.putStep.getProcessFlow()));
        }
    }
}
