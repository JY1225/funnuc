package eu.robojob.millassist.ui.configure.device.stacking.stackplate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.configure.device.stacking.AbstractRawWorkPiecePresenter;
import eu.robojob.millassist.ui.general.NotificationBox.Type;
import eu.robojob.millassist.util.Translator;

public class BasicStackPlateRawWorkPiecePresenter extends AbstractRawWorkPiecePresenter<BasicStackPlateMenuPresenter> {

    static Logger logger = LogManager.getLogger(BasicStackPlateRawWorkPiecePresenter.class.getName());
    static final String STUD_HEIGHT_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.studHeightNotOK";
    private static final String WEIGHT_ZERO = "BasicStackPlateRawWorkPiecePresenter.weightZero";
    private static final String GRIDPLATE_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.gridplateNotOK";
    private static final String AMOUNT_NOT_OK = "BasicStackPlateRawWorkPiecePresenter.amountNotOK";
    
    
    public BasicStackPlateRawWorkPiecePresenter(final BasicStackPlateRawWorkPieceView view, final PickStep pickStep,
            final AbstractStackPlateDeviceSettings deviceSettings) {
        super(view, pickStep, deviceSettings);
    }

    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    public boolean isStudHeightOk() {
        return deviceSettings.getStudHeight() >= 0;
    }

    

    @Override
    public boolean isConfigured() {
        BasicStackPlate plate = getStackPlate();
        if ((workPiece.getDimensions() != null) && (plate.getLayout().getRawStackingPositions() != null)
                && (plate.getLayout().getRawStackingPositions().size() > 0) && (workPiece.getWeight() > 0)
                && (isAmountOk()) && isGridPlateOK()) {
            return true;
        }
        return false;
    }

    public BasicStackPlate getStackPlate() {
        return ((BasicStackPlate) pickStep.getDevice());
    }

    

    @Override
    public void setMaxAmount() {
        BasicStackPlate plate = getStackPlate();
        plate.loadDeviceSettings(deviceSettings);
        deviceSettings.setAmount(plate.getLayout().getMaxPiecesPossibleAmount());
        recalculate();
        getView().refresh();
        pickStep.getProcessFlow().processProcessFlowEvent(
                new DataChangedEvent(pickStep.getProcessFlow(), pickStep, false));
        plate.notifyLayoutChanged();
    }

    @Override
    public void recalculate() {
        try {
            pickStep.getProcessFlow().setFinishedAmount(0);
            getLayout().configureStackingPositions(deviceSettings.getRawWorkPiece(),
                    deviceSettings.getFinishedWorkPiece(), deviceSettings.getOrientation(), deviceSettings.getLayers());
            getLayout().initRawWorkPieces(deviceSettings.getRawWorkPiece(), deviceSettings.getAmount());
            if ((deviceSettings.getOrientation() == 90)
                    || ((deviceSettings.getOrientation() == 45) && (getStackPlate().getBasicLayout().getTiltedR() < getStackPlate()
                            .getBasicLayout().getHorizontalR() && (!getStackPlate().getBasicLayout().isRightAligned())))
                    || ((deviceSettings.getOrientation() == 45) && (getStackPlate().getBasicLayout().getTiltedR() > getStackPlate()
                            .getBasicLayout().getHorizontalR() && (getStackPlate().getBasicLayout().isRightAligned())))) {
                pickStep.getProcessFlow().getClampingType().setChanged(true);
            } else {
                pickStep.getProcessFlow().getClampingType().setChanged(false);
            }
            getView().hideNotification();
            if (!isWeightOk()) {
                getView().showNotification(Translator.getTranslation(WEIGHT_ZERO), Type.WARNING);
            } else if (!isStudHeightOk()) {
                getView().showNotification(Translator.getTranslation(STUD_HEIGHT_NOT_OK), Type.WARNING);
            } else if (!isGridPlateOK()) {
                getView().showNotification(Translator.getTranslation(GRIDPLATE_NOT_OK), Type.WARNING);
            } else if (!isAmountOk()) {
                getView().showNotification(Translator.getTranslation(AMOUNT_NOT_OK), Type.WARNING);
            }
        } catch (IncorrectWorkPieceDataException e) {
            getView().showNotification(e.getLocalizedMessage(), Type.WARNING);
        }
        getStackPlate().notifyLayoutChanged();
    }

    @Override
    public AbstractStackPlateLayout getLayout() {
        return getStackPlate().getLayout();
    }
}
