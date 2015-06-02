package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.workpiece.WorkPiece;

public class PalletDeviceSettings extends AbstractStackPlateDeviceSettings {

    private GridPlate gridPlate;
    public PalletDeviceSettings(WorkPiece workPiece, WorkPiece finishedWorkPiece, GridPlate gridPlate) {
        super(workPiece,finishedWorkPiece,0, 1, 0);
        this.gridPlate = gridPlate;
        setGridId(gridPlate.getId());
    }

    public GridPlate getGridPlate() {
        return this.gridPlate;
    }

    public void setGridPlate(GridPlate gridPlate) {
        this.gridPlate = gridPlate;
        setGridId(gridPlate.getId());
    }

}
