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
    
    private int layersBeforeCardBoard;
    
    private float cardBoardThickness;
    
    private PalletLayout layout;
    
    public UnloadPalletDeviceSettings(final WorkPiece finishedWorkPiece, final PalletLayoutType layoutType, final int layersBeforeCardBoard, final PalletLayout layout, final float cardBoardThickness) {
        super();
        this.finishedWorkPiece = finishedWorkPiece;
        this.layoutType = layoutType;
        this.layersBeforeCardBoard = layersBeforeCardBoard;
        this.layout = layout;
        this.cardBoardThickness = cardBoardThickness;
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

    public int getLayersBeforeCardBoard() {
        return layersBeforeCardBoard;
    }

    public void setLayersBeforeCardBoard(int layersBeforeCardBoard) {
        this.layersBeforeCardBoard = layersBeforeCardBoard;
    }

    public PalletLayout getLayout() {
        return layout;
    }

    public void setLayout(PalletLayout layout) {
        this.layout = layout;
    }

    public float getCardBoardThickness() {
        return this.cardBoardThickness;
    }

    public void setCardBoardThickness(float cardBoardThickness) {
        this.cardBoardThickness = cardBoardThickness;
    }
}
