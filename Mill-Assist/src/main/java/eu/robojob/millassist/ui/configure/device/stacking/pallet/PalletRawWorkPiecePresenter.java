package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateLayout;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.configure.device.stacking.AbstractRawWorkPiecePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.stackplate.BasicStackPlateRawWorkPieceView;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;

public class PalletRawWorkPiecePresenter extends AbstractRawWorkPiecePresenter<PalletMenuPresenter>{

    private static final String WEIGHT_ZERO = "BasicStackPlateRawWorkPiecePresenter.weightZero";
    private static final String GRIDPLATE_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.gridplateNotOK";
    private static final String AMOUNT_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.amountNotOK";
    
    public PalletRawWorkPiecePresenter(final BasicStackPlateRawWorkPieceView view, final PickStep pickStep,
            final AbstractStackPlateDeviceSettings deviceSettings) {
        super(view, pickStep, deviceSettings);
    }

    @Override
    public void recalculate() {
        try {
            pickStep.getProcessFlow().setFinishedAmount(0);
            getLayout().configureStackingPositions(deviceSettings.getRawWorkPiece(),
                    deviceSettings.getFinishedWorkPiece(), deviceSettings.getOrientation(), deviceSettings.getLayers());
            getLayout().initRawWorkPieces(deviceSettings.getRawWorkPiece(), deviceSettings.getAmount());
            if ((deviceSettings.getOrientation() == 90)
                    || ((deviceSettings.getOrientation() == 45) && (getPallet().getTiltedR() < getPallet().getHorizontalR() ))
                    || ((deviceSettings.getOrientation() == 45) && (getPallet().getTiltedR() > getPallet().getHorizontalR() ))) {
                pickStep.getProcessFlow().getClampingType().setChanged(true);
            } else {
                pickStep.getProcessFlow().getClampingType().setChanged(false);
            }
            getView().hideNotification();
            if (!isWeightOk()) {
                getView().showNotification(Translator.getTranslation(WEIGHT_ZERO), Type.WARNING);
            } else if (!isGridPlateOK()) {
                getView().showNotification(Translator.getTranslation(GRIDPLATE_NOT_OK), Type.WARNING);
            } else if (!isAmountOk()) {
                getView().showNotification(Translator.getTranslation(AMOUNT_NOT_OK), Type.WARNING);
            }
        } catch (IncorrectWorkPieceDataException e) {
            getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
        }
        getPallet().notifyLayoutChanged();
        
    }

    @Override
    public void setMaxAmount() {
        Pallet plate = getPallet();
        plate.loadDeviceSettings(deviceSettings);
        deviceSettings.setAmount(plate.getGridLayout().getMaxPiecesPossibleAmount());
        recalculate();
        getView().refresh();
        pickStep.getProcessFlow().processProcessFlowEvent(
                new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
        plate.notifyLayoutChanged();
        
    }

    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    @Override
    public boolean isConfigured() {
        Pallet plate = getPallet();
        if ((workPiece.getDimensions() != null) && (plate.getGridLayout().getRawStackingPositions() != null)
                && (plate.getGridLayout().getRawStackingPositions().size() > 0) && (workPiece.getWeight() > 0)
                && (isAmountOk()) && isGridPlateOK()) {
            return true;
        }
        return false;
    }

    @Override
    public AbstractStackPlateLayout getLayout() {
        return getPallet().getGridLayout();
    }
    
    public Pallet getPallet() {
        return ((Pallet) pickStep.getDevice());
    }

}
