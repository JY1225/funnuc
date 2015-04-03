package eu.robojob.millassist.external.device.stacking.pallet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.strategy.AbstractPalletUnloadStrategy;
import eu.robojob.millassist.external.device.stacking.pallet.strategy.CubicPiecePalletUnloadStrategy;
import eu.robojob.millassist.external.device.stacking.pallet.strategy.RoundPiecePalletUnloadStrategy;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class PalletLayout {
    
    public static final float EUR_WIDTH = 800.0f;
    public static final float EUR_HEIGHT = 1200.0f;
    
    public static final float EUR2_WIDTH = 1200.0f;
    public static final float EUR2_HEIGHT = 1000.0f;
    
    public static final float EUR3_WIDTH = 1000.0f;
    public static final float EUR3_HEIGHT = 1200.0f;
    
    public static final float EUR6_WIDTH = 800.0f;
    public static final float EUR6_HEIGHT = 600.0f;
    
    public static final float STANDARD_HEIGHT = 144.0f;
    
    private static Logger logger = LogManager.getLogger(PalletLayout.class.getName());
    /**
     * Enumeration determining the type of the pallet layout.
     */
    public enum PalletLayoutType {
        SHIFTED_HORIZONTAL(1), SHIFTED_VERTICAL(2), OPTIMAL(3), NOT_SHIFTED_HORIZONTAL(4), NOT_SHIFTED_VERTICAL(5);
        
        private int id;
        private PalletLayoutType(int id) {
            this.id = id;
        }
        
        public int getId() {
            return this.id;
        }
        
        public static PalletLayoutType getTypeById(int id) {
            for (PalletLayoutType type: values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown device type " + id);
        }
    }
    
    public enum PalletType {
        EUR(1, EUR_WIDTH, STANDARD_HEIGHT, EUR_HEIGHT),EUR2(2, EUR2_WIDTH, STANDARD_HEIGHT, EUR2_HEIGHT), EUR3(3, EUR3_WIDTH, STANDARD_HEIGHT, EUR3_HEIGHT), EUR6(4, EUR6_WIDTH, STANDARD_HEIGHT,EUR6_HEIGHT), CUSTOM(5,0.0f,0.0f,0.0f);
        
        private int id;
        private float width;
        private float height;
        private float length;
        private PalletType(int id, final float width, final float height, final float length) {
            this.id = id;
            this.width = width;
            this.length = length;
            this.height = height;
        }
        
        public int getId() {
            return this.id;
        }
        
        public static PalletType getTypeById(int id) {
            for (PalletType type: values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown device type " + id);
        }
        
        public static List<PalletType> getPalletTypes() {
            return Arrays.asList(values());
        }
        
        public static PalletType getPalletTypeForLayout(PalletLayout layout) {
            for (PalletType type: values()) {
                if(type.width == layout.getPalletWidth() && type.height == layout.getPalletHeight() && type.length == layout.getPalletLength()) {
                    return type;
                }
            }
            return CUSTOM;
        }
        
        public float getWidth() {
            return this.width;
        }
        
        public float getLength() {
            return this.length;
        }
        
        public float getHeight() {
            return this.height;
        }
        
    }
    
    /**
     * List of positions on which the work pieces can be stacked on this pallet layout.
     */
    private List<PalletStackingPosition> stackingPositions = new ArrayList<PalletStackingPosition>();
    
    /**
     * Width of this pallet layout (Y-axis).
     */
    private float palletWidth;
    /**
     * Length of this pallet layout (X-axis).
     */
    private float palletLength;
    /**
     * Minimal distance between the work pieces and the side of this pallet layout.
     */
    private float palletFreeBorder;
    /**
     * Minimal distance between two work pieces on the X-axis.
     */
    private float minXGap;
    /**
     * Minimal distance between two work pieces on the Y-axis.
     */
    private float minYGap;
    /**
     * Minimal distance between two work pieces in all directions.
     */
    private float minInterferenceDistance;
    /**
     * Type of the layout.
     */
    private PalletLayoutType layoutType;
    /**
     * Indicate if the work piece must be rotated 90° or not.
     */
    private boolean rotate90 = false;
    /**
     * Number of pieces in the horizontal direction.
     */
    private int numberOfHorizontalPieces;
    /**
     * Number of pieces in the vertical direction.
     */
    private int numberOfVerticalPieces;
    /**
     * Reference to the current work piece that will be stacked in this layout.
     */
    private WorkPiece currentWorkPiece;
    /**
     * Map containing the strategies to determine the positions in this layout.
     */
    private Map<WorkPieceShape, AbstractPalletUnloadStrategy<? extends IWorkPieceDimensions>> strategyMap;
    
    /**
     * Horizontal work piece orientation R correction.
     */
    private float horizontalR;
    /**
     * Vertical work piece orientation R correction.
     */
    private float verticalR;
    
    /**
     * Number of allowed layers on the pallet
     */
    private int layers;
    
    private float palletHeight;
    
    public PalletLayout(final float palletWidth, final float palletLength, final float palletHeight, final float palletFreeBorder, final float minXGap, final float minYGap, final float minInterferenceDistance, final float horizontalR, final float verticalR) {
        this.palletWidth = palletWidth;
        this.palletLength = palletLength;
        this.palletHeight = palletHeight;
        this.palletFreeBorder = palletFreeBorder;
        this.minXGap = minXGap;
        this.minYGap = minYGap;
        this.minInterferenceDistance = minInterferenceDistance;
        this.strategyMap = new HashMap<WorkPieceShape, AbstractPalletUnloadStrategy<? extends IWorkPieceDimensions>>();
        strategyMap.put(WorkPieceShape.CYLINDRICAL, new RoundPiecePalletUnloadStrategy(this));
        strategyMap.put(WorkPieceShape.CUBIC, new CubicPiecePalletUnloadStrategy(this));
        //FIXME not hard coded
        this.layers = 2;
        this.horizontalR = horizontalR;
        this.verticalR = verticalR;
    }
    
    /**
     * Calculates the positions for this pallet layout for the given work piece.
     * @param workPiece That will be stacked on this pallet layout
     * @return List of positions where the work pieces can be stacked on this pallet layout
     */
    public List<PalletStackingPosition> calculateLayoutForWorkPiece(WorkPiece workPiece) {
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
    
    /**
     * Sets a work piece for each position on this pallet layout.
     * @param finishedWorkPiece The work piece that will be set for each position
     */
    public void initFinishedWorkPieces(WorkPiece finishedWorkPiece) {
        for(StackingPosition position: this.stackingPositions) {
            position.setWorkPiece(finishedWorkPiece);
        }
    }

    public List<PalletStackingPosition> getStackingPositions() {
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

    public void setStackingPositions(List<PalletStackingPosition> stackingPositions) {
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

    /**
     * The width of the pallet that can be used to put work pieces on.
     * @return Width of the pallet - 2 * Minimal distance between the work pieces and the side of this pallet layout (palletFreeBorder)
     */
    public float getUsableWidth() {
        return this.palletWidth - 2* this.palletFreeBorder;
    }
    
    /**
     * The length of the pallet that can be used to put work pieces on.
     * @return Length of the pallet - 2 * Minimal distance between the work pieces and the side of this pallet layout (palletFreeBorder)
     */
    public float getUsableLength() {
        return this.palletLength - 2* this.palletFreeBorder;
    }
    
    /**
     * Manually add finished work pieces to the pallet.
     * @param finishedWorkPiece The finished work piece that will be added
     * @param amount The amount of finished work pieces that will be added
     * @param isAddOperation Boolean indicating whether this is an add operation
     */
    public void placeFinishedWorkPieces(final WorkPiece finishedWorkPiece, final int amount, boolean isAddOperation) {
        logger.debug("Adding finished workpieces: [" + amount + "].");
        int placedAmount = 0;
        int stackingPos = 0;
        //For any number of layers, we only put 1 workPiece on the first position. This ensures that the robot
        //places finished products always at the same position
        PalletStackingPosition stPos = getStackingPositions().get(0);
        while (placedAmount < amount && stackingPos < getStackingPositions().size()) {
            stPos = getStackingPositions().get(stackingPos);
            while(placedAmount < amount && addOneWorkPiece(finishedWorkPiece, stPos) ) {
                placedAmount++;
            }
            stackingPos++;
        }
        if (isAddOperation) {
            transferFirstToLast(stackingPos-1);
        }
    }
    
    /**
     * Transfer the work piece in the first position to the last position if possible.
     * @param lastPosition Index of the last position to which the first work piece will be transfered
     */
    private void transferFirstToLast(int lastPosition) {
        //If the final stack of pieces (in case of multiple layers) does not hold the maximum, try to move pieces from the 
        //first raw stack to the last raw stack (min of first stack is always 1)
        if (getLayers() > 1) {
            PalletStackingPosition lastStackingPosition = getStackingPositions().get(lastPosition);
            PalletStackingPosition firstStackingPosition = null;
            if (lastStackingPosition.getAmount() < getLayers()) {
                int amountToTransfer1 = getLayers() - lastStackingPosition.getAmount();
                int amountToTransfer2 = 0;
                for (PalletStackingPosition stPlatePosition: getStackingPositions()) {
                    //Find the first stacking position that has raw workpieces
                    if (stPlatePosition.getWorkPiece() != null && stPlatePosition.getWorkPiece().getType().equals(Type.RAW)) {
                        firstStackingPosition = stPlatePosition;
                        amountToTransfer2 = stPlatePosition.getAmount() - 1;
                        break;
                    }
                }
                int amountToTransfer = Math.min(amountToTransfer1, amountToTransfer2);
                if (firstStackingPosition != null && !firstStackingPosition.equals(getStackingPositions().get(0))) {
                    lastStackingPosition.setAmount(lastStackingPosition.getAmount() + amountToTransfer);
                    firstStackingPosition.setAmount(lastStackingPosition.getAmount() - amountToTransfer);
                }
            }
        } 
    }
    
    /**
     * Add one work piece to the pallet.
     * @param workPiece The work piece that will be added
     * @param position The position on which the work piece will be added
     * @param maxNbOfPieces The maximum number of work pieces 
     * @return Boolean indicating that the work piece is added to the PalletStackingPosition
     */
    private boolean addOneWorkPiece(final WorkPiece workPiece, PalletStackingPosition position) {
        if(position.hasWorkPiece()) {
            if(position.getWorkPiece().getType().equals(workPiece.getType())) {
                if(getWorkPieceAmount(WorkPiece.Type.FINISHED) >= position.getAmount() * getMaxPiecesPerLayerAmount()){
                    position.incrementAmount();
                    return true;
                }
                return false;
            }
            return false;
        }
        else {
            position.setWorkPiece(workPiece);
            position.setAmount(1);
            return true;
        }
    }

    public int getLayers() {
        return layers;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }
    
    public int getMaxPiecesPossibleAmount() {
        return  getLayers() * stackingPositions.size();
    }
    
    public int getMaxPiecesPerLayerAmount() {
        return stackingPositions.size();
    }
    
    /**
     * Get the number of work pieces for the given type
     * @param type The type of work piece from which the amount is requested
     * @return The number of work pieces of the given type on the pallet
     */
    public int getWorkPieceAmount(WorkPiece.Type type) {
        if(type == WorkPiece.Type.FINISHED) {
            int result = 0;
            for(PalletStackingPosition position: getStackingPositions()) {
                result += position.getAmount();
            }
            return result;
        }
        return 0;
    }
    
    /**
     * Removes the given number of finished work pieces from the pallet.
     * @param amount The amount of finished work pieces that will be removed from the pallet
     */
    public void removeFinishedWorkPieces(final int amount) {
        logger.debug("Removing finished workpieces: [" + amount + "].");
        int removedAmount = 0;
        int stackingPos = 0;
        
        PalletStackingPosition stPos = getStackingPositions().get(0);
        while (removedAmount < amount && stackingPos < getStackingPositions().size()) {
            stPos = getStackingPositions().get(stackingPos);
            while(removedAmount < amount && removeOneWorkPiece(stPos)) {
                removedAmount++;
            }
            stackingPos++;
        }
    }
    
    /**
     * Removes a single work piece from the pallet.
     * @param position The position from which a work piece will be removed
     * @return A boolean indicating if the work piece is removed from that position on the pallet
     */
    private boolean removeOneWorkPiece(PalletStackingPosition position) {
        if(position.hasWorkPiece()) {
            if(position.getAmount() > 0) {
                position.decrementAmount();
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public float getHorizontalR() {
        return horizontalR;
    }

    public void setHorizontalR(float horizontalR) {
        this.horizontalR = horizontalR;
    }

    public float getVerticalR() {
        return verticalR;
    }

    public void setVerticalR(float verticalR) {
        this.verticalR = verticalR;
    }

    public float getPalletHeight() {
        return palletHeight;
    }

    public void setPalletHeight(float palletHeight) {
        this.palletHeight = palletHeight;
    }

}