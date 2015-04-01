package eu.robojob.millassist.external.device.stacking.pallet;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.workpiece.WorkPiece;

public class UnloadPalletDeviceSettings extends DeviceSettings {
    
    /**
     * The finished work piece that will be put in this position.
     */
    private WorkPiece finishedWorkPiece;
    /**
     * The layout type of the unload pallet.
     */
    private PalletLayoutType layoutType;
    
    public UnloadPalletDeviceSettings(final WorkPiece finishedWorkPiece, final PalletLayoutType layoutType) {
        super();
        this.finishedWorkPiece = finishedWorkPiece;
        this.layoutType = layoutType;
    }

    public WorkPiece getFinishedWorkPiece() {
        return finishedWorkPiece;
    }

    public void setFinishedWorkPiece(WorkPiece finishedWorkPiece) {
        this.finishedWorkPiece = finishedWorkPiece;
    }

    public PalletLayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(PalletLayoutType layoutType) {
        this.layoutType = layoutType;
    }
    
}
