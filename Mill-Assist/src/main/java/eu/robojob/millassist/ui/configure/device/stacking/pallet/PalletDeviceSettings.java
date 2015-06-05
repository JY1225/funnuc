package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.workpiece.WorkPiece;

public class PalletDeviceSettings extends AbstractStackPlateDeviceSettings {

    private GridPlate gridPlate;
    private PalletLayout palletLayout;
    
    public PalletDeviceSettings(WorkPiece workPiece, WorkPiece finishedWorkPiece, GridPlate gridPlate, int amount, int layers, PalletLayout palletLayout) {
        super(workPiece,finishedWorkPiece,0, layers, amount);
        if (gridPlate != null) {
            this.gridPlate = gridPlate;
            setGridId(gridPlate.getId());
        }
        this.palletLayout = palletLayout;
    }

    public GridPlate getGridPlate() {
        return this.gridPlate;
    }

    public void setGridPlate(GridPlate gridPlate) {
        this.gridPlate = gridPlate;
        setGridId(gridPlate.getId());
    }

    public PalletLayout getPalletLayout() {
        return this.palletLayout;
    }

    public void setPalletLayout(PalletLayout palletLayout) {
        this.palletLayout = palletLayout;
    }

}
