package eu.robojob.millassist.external.device.stacking.pallet.strategy;

import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.PalletStackingPosition;
import eu.robojob.millassist.workpiece.RoundDimensions;

public class RoundPiecePalletUnloadStrategy extends
        AbstractPalletUnloadStrategy<RoundDimensions> {
    public RoundPiecePalletUnloadStrategy(PalletLayout layout) {
        super(layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureFinishedPieces(RoundDimensions workPieceDimensions) {
        switch (layout.getLayoutType()) {
        case SHIFTED_VERTICAL:
            configureFinishedPiecesShiftedVertical(workPieceDimensions);
            break;
        case SHIFTED_HORIZONTAL:
            configureFinishedPiecesShiftedHorizontal(workPieceDimensions);
            break;
        case NOT_SHIFTED_HORIZONTAL:
            configureFinishedPiecesNotShifted(workPieceDimensions);
            break;
        case NOT_SHIFTED_VERTICAL:
            configureFinishedPiecesNotShifted(workPieceDimensions);
            break;
        case OPTIMAL:
            configureFinishedPiecesOptimal(workPieceDimensions);
            break;
        default:
            break;
        }
    }
    
    /**
     * Optimal configuration of the layout for the given work piece dimensions.
     * @param workPieceDimensions Dimensions of the work piece that will be stacked 
     */
    private void configureFinishedPiecesOptimal(RoundDimensions workPieceDimensions) {
        Integer[] dimensionsShiftedHorizontal = determineHorizontalNumberOfPiecesShifted(workPieceDimensions);
        Integer[] dimensionsShiftedVertical = determineVerticalNumberOfPiecesShifted(workPieceDimensions);
        Integer[] dimensionsNotShifted = determineNumberOfPiecesNotShifted(workPieceDimensions);
        int totalNotShifted = dimensionsNotShifted[0]*dimensionsNotShifted[1];
        int totalShiftedHorizontal = dimensionsShiftedHorizontal[0]*dimensionsShiftedHorizontal[1] - (int)Math.floor(dimensionsShiftedHorizontal[1]/2);
        int totalShiftedVertical = dimensionsShiftedVertical[0]*dimensionsShiftedVertical[1] - (int)Math.floor(dimensionsShiftedVertical[0]/2);
        
        if(totalShiftedHorizontal > totalNotShifted && totalShiftedHorizontal > totalShiftedVertical) {
            configureFinishedPiecesShiftedHorizontal(workPieceDimensions);
        }
        else if (totalNotShifted > totalShiftedHorizontal && totalNotShifted > totalShiftedVertical) {
            configureFinishedPiecesNotShifted(workPieceDimensions);
        }
        else {
            configureFinishedPiecesShiftedVertical(workPieceDimensions);
        }
    }
    
    /**
     * Not shifted configuration of the layout for the given work piece dimensions.
     * @param workPieceDimensions Dimensions of the work piece that will be stacked 
     */
    private void configureFinishedPiecesNotShifted(RoundDimensions workPieceDimensions) {
        
        Integer[] dimensions = determineNumberOfPiecesNotShifted(workPieceDimensions);
        layout.setNumberOfVerticalPieces(dimensions[1]);
        layout.setNumberOfHorizontalPieces(dimensions[0]);
        
        float unitX = layout.getUsableLength() / layout.getNumberOfHorizontalPieces();
        float unitY = layout.getUsableWidth() / layout.getNumberOfVerticalPieces();
        float offsetX = (unitX - workPieceDimensions.getDiameter())/2 + workPieceDimensions.getDiameter()/2;
        float offsetY = (unitY - workPieceDimensions.getDiameter())/2 + workPieceDimensions.getDiameter()/2;
        
        float currentX = layout.getPalletFreeBorder() + unitX;
        float currentY = layout.getPalletFreeBorder() + unitY;
        for(int i = 0; i < layout.getNumberOfVerticalPieces(); ++i) {
            for(int j = 0; j < layout.getNumberOfHorizontalPieces(); ++j) {
                this.layout.getStackingPositions().add(new PalletStackingPosition(currentX - offsetX, currentY - offsetY, 0, null));
                currentX += unitX;
            }
            currentY += unitY;
            currentX = layout.getPalletFreeBorder() + unitX;
        }
    }
    
    /**
     * Determine the number of work pieces if they will be stacked not shifted.
     * @param workPieceDimensions Dimensions of the work piece that will be stacked
     * @return A set containing the number of work pieces on the x-axis (horizontal) and on the y-axis (vertical)
     */
    private Integer[] determineNumberOfPiecesNotShifted(RoundDimensions workPieceDimensions) {
        Integer[] result = new Integer[2];
        int numberOfVertical = (int) Math.floor(layout.getUsableWidth() / (workPieceDimensions.getDiameter() + layout.getMinYGap()));
        int numberOfHorizontal = (int) Math.floor(layout.getUsableLength() / (workPieceDimensions.getDiameter() + layout.getMinXGap()));
        result[0] = numberOfHorizontal;
        result[1] = numberOfVertical;
        return result;
    }
    
    /**
     * Horizontal base shifted configuration of the layout for the given work piece dimensions.
     * @param workPieceDimensions Dimensions of the work piece that will be stacked 
     */
    private void configureFinishedPiecesShiftedHorizontal(RoundDimensions workPieceDimensions) {
        Integer[] dimensions = determineHorizontalNumberOfPiecesShifted(workPieceDimensions);
        layout.setNumberOfVerticalPieces(dimensions[1]);
        layout.setNumberOfHorizontalPieces(dimensions[0]);
        
        float unitX = layout.getUsableLength() / dimensions[0];
        float offsetX = (unitX - workPieceDimensions.getDiameter())/2 + workPieceDimensions.getDiameter()/2;
        float currentX = layout.getPalletFreeBorder() + unitX;
        
        float currentY = workPieceDimensions.getDiameter()/2 + layout.getPalletFreeBorder();
        float spaceBetween = unitX - workPieceDimensions.getDiameter();
        float heightOffset = spaceBetween/2 + workPieceDimensions.getDiameter()/2;
        float extraY = (layout.getUsableWidth() - workPieceDimensions.getDiameter())/(dimensions[1]-1);
        
        if(2*heightOffset/Math.sqrt(2) < workPieceDimensions.getDiameter() + layout.getMinInterferenceDistance()) {
            heightOffset = (float)Math.sqrt(2) * (workPieceDimensions.getDiameter()+layout.getMinInterferenceDistance()) /2;
        }
        if(extraY > heightOffset) {
            heightOffset = extraY;
        }
        
        for(int i = 0; i < dimensions[1]; ++i) {
            if((i % 2) == 1) {
                for(int j = 0; j < dimensions[0]-1; ++j) {
                    this.layout.getStackingPositions().add(new PalletStackingPosition(currentX - offsetX, currentY, 0, null));
                    currentX += unitX;
                }
                currentX = layout.getPalletFreeBorder() + unitX;
            }
            else {
                for(int j = 0; j < dimensions[0]; ++j) {
                    this.layout.getStackingPositions().add(new PalletStackingPosition(currentX - offsetX, currentY, 0, null));
                    currentX += unitX;
                }
                currentX = layout.getPalletFreeBorder() + unitX + (spaceBetween/2 + workPieceDimensions.getDiameter()/2);
            }
            currentY += heightOffset;
        }
    }
    
    /**
     * Vertical base shifted configuration of the layout for the given work piece dimensions.
     * @param workPieceDimensions Dimensions of the work piece that will be stacked 
     */
    private void configureFinishedPiecesShiftedVertical(RoundDimensions workPieceDimensions) {
        Integer[] dimensions = determineVerticalNumberOfPiecesShifted(workPieceDimensions);
        layout.setNumberOfVerticalPieces(dimensions[1]);
        layout.setNumberOfHorizontalPieces(dimensions[0]);
        
        float unitY = layout.getUsableWidth() / dimensions[1];
        float offsetY = (unitY - workPieceDimensions.getDiameter())/2 + workPieceDimensions.getDiameter()/2;
        float currentY = layout.getPalletFreeBorder() + unitY;
        
        float currentX = workPieceDimensions.getDiameter()/2 + layout.getPalletFreeBorder();
        float spaceBetween = unitY - workPieceDimensions.getDiameter();
        
        float widthOffset = spaceBetween/2 + workPieceDimensions.getDiameter()/2;
        
        if(2*widthOffset/Math.sqrt(2) < workPieceDimensions.getDiameter() + layout.getMinInterferenceDistance()) {
            widthOffset = (float)Math.sqrt(2) * (workPieceDimensions.getDiameter()+layout.getMinInterferenceDistance()) /2;
        }
        float extraX = (layout.getUsableLength() - workPieceDimensions.getDiameter())/(dimensions[0]-1);
        
        if(extraX > widthOffset) {
            widthOffset = extraX;
        }
        
        for(int i = 0; i < dimensions[0]; ++i) {
            if((i % 2) == 1) {
                for(int j = 0; j < dimensions[1]-1; ++j) {
                    this.layout.getStackingPositions().add(new PalletStackingPosition(currentX, currentY - offsetY, 0, null));
                    currentY += unitY;
                }
                currentY = layout.getPalletFreeBorder() + unitY;
            }
            else {
                for(int j = 0; j < dimensions[1]; ++j) {
                    this.layout.getStackingPositions().add(new PalletStackingPosition(currentX, currentY - offsetY, 0, null));
                    currentY += unitY;
                }
                currentY = layout.getPalletFreeBorder() + unitY + (spaceBetween/2 + workPieceDimensions.getDiameter()/2);
            }
            currentX += widthOffset;
        }
    }
    
    /**
     * Determine the number of work pieces if they will be stacked shifted with a vertical optimal base.
     * @param workPieceDimensions Dimensions of the work piece that will be stacked
     * @return A set containing the number of work pieces on the x-axis (horizontal) and on the y-axis (vertical)
     */
    private Integer[] determineVerticalNumberOfPiecesShifted(RoundDimensions workPieceDimensions) {
        Integer[] result = new Integer[2];
        int numberOfVertical = (int) Math.floor(layout.getUsableWidth() / (workPieceDimensions.getDiameter() + layout.getMinYGap()));
        float unitY = layout.getUsableLength() / numberOfVertical;
        float spaceBetween = unitY - workPieceDimensions.getDiameter();
        float widthOffset = spaceBetween/2 + workPieceDimensions.getDiameter()/2;
        
        if(2*widthOffset/Math.sqrt(2) < workPieceDimensions.getDiameter() + layout.getMinInterferenceDistance()) {
            widthOffset = (float)Math.sqrt(2) * (workPieceDimensions.getDiameter()+layout.getMinInterferenceDistance()) /2;
        }
        
        int numberOfHorizontal = (int) Math.floor((layout.getUsableLength() - workPieceDimensions.getDiameter())/ widthOffset);
        result[0] = numberOfHorizontal+1;
        result[1] = numberOfVertical;
        return result;
    }
    
    /**
     * Determine the number of work pieces if they will be stacked shifted with a horizontal optimal base.
     * @param workPieceDimensions Dimensions of the work piece that will be stacked
     * @return A set containing the number of work pieces on the x-axis (horizontal) and on the y-axis (vertical)
     */
    private Integer[] determineHorizontalNumberOfPiecesShifted(RoundDimensions workPieceDimensions) {
        Integer[] result = new Integer[2];
        int numberOfHorizontal = (int) Math.floor(layout.getUsableLength() / (workPieceDimensions.getDiameter() + layout.getMinXGap()));
        float unitX = layout.getUsableLength() / numberOfHorizontal;
        float spaceBetween = unitX - workPieceDimensions.getDiameter();
        float heightOffset = spaceBetween/2 + workPieceDimensions.getDiameter()/2;
        
        if(2*heightOffset/Math.sqrt(2) < workPieceDimensions.getDiameter() + layout.getMinInterferenceDistance()) {
            heightOffset = (float)Math.sqrt(2) * (workPieceDimensions.getDiameter()+layout.getMinInterferenceDistance()) /2;
        }
        
        int numberOfVertical = (int) Math.floor((layout.getUsableWidth() - workPieceDimensions.getDiameter())/ heightOffset);
        result[0] = numberOfHorizontal;
        result[1] = numberOfVertical+1;
        return result;
    }
}
