package eu.robojob.millassist.external.device.stacking.pallet;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.workpiece.WorkPiece;

public class UnloadPalletDeviceSettings extends DeviceSettings {
    
    public UnloadPalletDeviceSettings(final WorkPiece finishedWorkPiece) {
        super();
        this.finishedWorkPiece = finishedWorkPiece;
    }
    
    private WorkPiece finishedWorkPiece;

    public WorkPiece getFinishedWorkPiece() {
        return finishedWorkPiece;
    }

    public void setFinishedWorkPiece(WorkPiece finishedWorkPiece) {
        this.finishedWorkPiece = finishedWorkPiece;
    }
    
}
