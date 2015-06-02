package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateLayout;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.ui.configure.device.stacking.AbstractRawWorkPiecePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.stackplate.BasicStackPlateRawWorkPieceView;

public class PalletRawWorkPiecePresenter extends AbstractRawWorkPiecePresenter<PalletMenuPresenter>{

    public PalletRawWorkPiecePresenter(final BasicStackPlateRawWorkPieceView view, final PickStep pickStep,
            final AbstractStackPlateDeviceSettings deviceSettings) {
        super(view, pickStep, deviceSettings);
    }

    @Override
    public void recalculate() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setMaxAmount() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    @Override
    public boolean isConfigured() {
        Pallet plate = getPallet();
        if ((workPiece.getDimensions() != null) && (plate.getLayout().getRawStackingPositions() != null)
                && (plate.getLayout().getRawStackingPositions().size() > 0) && (workPiece.getWeight() > 0)
                && (isAmountOk()) && isGridPlateOK()) {
            return true;
        }
        return false;
    }

    @Override
    public AbstractStackPlateLayout getLayout() {
        return getPallet().getLayout();
    }
    
    public Pallet getPallet() {
        return ((Pallet) pickStep.getDevice());
    }

}
