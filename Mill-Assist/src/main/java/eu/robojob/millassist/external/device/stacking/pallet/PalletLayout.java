package eu.robojob.millassist.external.device.stacking.pallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.strategy.AbstractPalletUnloadStrategy;
import eu.robojob.millassist.external.device.stacking.pallet.strategy.CubicPiecePalletUnloadStrategy;
import eu.robojob.millassist.external.device.stacking.pallet.strategy.RoundPiecePalletUnloadStrategy;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class PalletLayout {
    
    public enum PalletLayoutType {
        SHIFTED, HORIZONTAL, VERTICAL, OPTIMAL
    }
    
    private List<StackingPosition> stackingPositions = new ArrayList<StackingPosition>();
    
    private float palletWidth;
    private float palletLength;
    private float palletFreeBorder;
    private float minXGap;
    private float minYGap;
    private float minInterferenceDistance;
    private PalletLayoutType layoutType;
    
    private boolean rotate90 = false;
    
    private int numberOfHorizontalPieces;
    private int numberOfVerticalPieces;
    
    private WorkPiece currentWorkPiece;
    private Map<WorkPieceShape, AbstractPalletUnloadStrategy<? extends IWorkPieceDimensions>> strategyMap;
    
    public PalletLayout(final float palletWidth, final float palletLength, final float palletFreeBorder, final float minXGap, final float minYGap, final float minInterferenceDistance) {
        this.palletWidth = palletWidth;
        this.palletLength = palletLength;
        this.palletFreeBorder = palletFreeBorder;
        this.minXGap = minXGap;
        this.minYGap = minYGap;
        this.minInterferenceDistance = minInterferenceDistance;
        this.layoutType = PalletLayoutType.OPTIMAL;
        this.strategyMap = new HashMap<WorkPieceShape, AbstractPalletUnloadStrategy<? extends IWorkPieceDimensions>>();
        strategyMap.put(WorkPieceShape.CYLINDRICAL, new RoundPiecePalletUnloadStrategy(this));
        strategyMap.put(WorkPieceShape.CUBIC, new CubicPiecePalletUnloadStrategy(this));
    }
    
    public List<StackingPosition> calculateLayoutForWorkPiece(WorkPiece workPiece) {
        this.currentWorkPiece = workPiece;
        stackingPositions.clear();
        if (currentWorkPiece.getShape().equals(WorkPieceShape.CYLINDRICAL)) {
            RoundPiecePalletUnloadStrategy strategy = (RoundPiecePalletUnloadStrategy)this.strategyMap.get(currentWorkPiece.getShape());
            strategy.configureFinishedPieces((RoundDimensions) currentWorkPiece.getDimensions());
        }
        else {
            CubicPiecePalletUnloadStrategy strategy = (CubicPiecePalletUnloadStrategy)this.strategyMap.get(currentWorkPiece.getShape());
            strategy.configureFinishedPieces((RectangularDimensions) currentWorkPiece.getDimensions());
        }
        return this.stackingPositions;
    }
    
    public void initFinishedWorkPieces(WorkPiece finishedWorkPiece) {
        for(StackingPosition position: this.stackingPositions) {
            position.setWorkPiece(finishedWorkPiece);
        }
    }

    public List<StackingPosition> getStackingPositions() {
        return stackingPositions;
    }

    public float getPalletWidth() {
        return palletWidth;
    }

    public void setPalletWidth(float palletWidth) {
        this.palletWidth = palletWidth;
    }

    public float getPalletLength() {
        return palletLength;
    }

    public void setPalletLength(float palletLength) {
        this.palletLength = palletLength;
    }

    public float getPalletFreeBorder() {
        return palletFreeBorder;
    }

    public void setPalletFreeBorder(float palletFreeBorder) {
        this.palletFreeBorder = palletFreeBorder;
    }

    public float getMinXGap() {
        return minXGap;
    }

    public void setMinXGap(float minXGap) {
        this.minXGap = minXGap;
    }

    public float getMinYGap() {
        return minYGap;
    }

    public void setMinYGap(float minYGap) {
        this.minYGap = minYGap;
    }

    public void setStackingPositions(List<StackingPosition> stackingPositions) {
        this.stackingPositions = stackingPositions;
    }

    public int getNumberOfHorizontalPieces() {
        return numberOfHorizontalPieces;
    }

    public void setNumberOfHorizontalPieces(int numberOfHorizontalPieces) {
        this.numberOfHorizontalPieces = numberOfHorizontalPieces;
    }

    public int getNumberOfVerticalPieces() {
        return numberOfVerticalPieces;
    }

    public void setNumberOfVerticalPieces(int numberOfVerticalPieces) {
        this.numberOfVerticalPieces = numberOfVerticalPieces;
    }

    public boolean isRotate90() {
        return rotate90;
    }

    public void setRotate90(boolean rotate90) {
        this.rotate90 = rotate90;
    }

    public PalletLayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(PalletLayoutType layoutType) {
        this.layoutType = layoutType;
    }

    public float getMinInterferenceDistance() {
        return minInterferenceDistance;
    }

    public void setMinInterferenceDistance(float minInterferenceDistance) {
        this.minInterferenceDistance = minInterferenceDistance;
    }

    public float getUsableWidth() {
        return this.palletWidth - 2* this.palletFreeBorder;
    }
    
    public float getUsableLength() {
        return this.palletLength - 2* this.palletFreeBorder;
    }

}