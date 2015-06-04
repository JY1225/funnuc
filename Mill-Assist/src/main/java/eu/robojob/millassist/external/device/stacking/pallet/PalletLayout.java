package eu.robojob.millassist.external.device.stacking.pallet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.pallet.strategy.AbstractPalletUnloadStrategy;
import eu.robojob.millassist.external.device.stacking.pallet.strategy.CubicPiecePalletUnloadStrategy;
import eu.robojob.millassist.external.device.stacking.pallet.strategy.RoundPiecePalletUnloadStrategy;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
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
    
    /**
     * Enumeration determining the type of the pallet.
     */
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
        
        @Override
        public String toString() {
            if(this == CUSTOM) {
                return name();
            }
            return name() + " ("+ (int)length + " x " + (int)width + ")";
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
    
    /**
     * Height of the pallet.
     */
    private float palletHeight;
    
    private int layersBeforeCardBoard;
    
    private float cardBoardThickness;
    
    private String name;
    
    private int id;
    
    public PalletLayout(final String name, final float palletWidth, final float palletLength, final float palletHeight) {
        this(name, palletWidth, palletLength, palletHeight,0,0,0,0,0,0);
    }

    public PalletLayout(final String name, final float palletWidth, final float palletLength, final float palletHeight, final float palletFreeBorder, final float minXGap, final float minYGap, final float minInterferenceDistance, final float horizontalR, final float verticalR) {
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
        this.horizontalR = horizontalR;
        this.verticalR = verticalR;
        this.name = name;
        this.layoutType = PalletLayoutType.OPTIMAL;
        this.cardBoardThickness = 0;
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
        for(PalletStackingPosition position: this.stackingPositions) {
            position.setWorkPiece(finishedWorkPiece);
            position.setAmount(0);
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

    public int getLayersBeforeCardBoard() {
        return layersBeforeCardBoard;
    }

    public void setLayersBeforeCardBoard(int layersBeforeCardBoard) {
        this.layersBeforeCardBoard = layersBeforeCardBoard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getCardBoardThickness() {
        return this.cardBoardThickness;
    }

    public void setCardBoardThickness(float cardBoardThickness) {
        this.cardBoardThickness = cardBoardThickness;
    }

}