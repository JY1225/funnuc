package eu.robojob.millassist.external.device.stacking.pallet.strategy;

import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.workpiece.RoundDimensions;

public class RoundPiecePalletUnloadStrategy extends
        AbstractPalletUnloadStrategy<RoundDimensions> {
    public RoundPiecePalletUnloadStrategy(PalletLayout layout) {
        super(layout);
    }

    @Override
    public void configureFinishedPieces(RoundDimensions workPieceDimensions) {
        if(layout.getLayoutType() == PalletLayoutType.SHIFTED) {
            configureFinishedPiecesShifted(workPieceDimensions);
        } else if(layout.getLayoutType() == PalletLayoutType.HORIZONTAL || layout.getLayoutType() == PalletLayoutType.VERTICAL) {
            configureFinishedPiecesNotShifted(workPieceDimensions);
        } else if(layout.getLayoutType() == PalletLayoutType.OPTIMAL) {
            Integer[] dimensionsShifted = determineHorizontalNumberOfPiecesShifted(workPieceDimensions);
            Integer[] dimensionsNotShifted = determineNumberOfPiecesNotShifted(workPieceDimensions);
            int totalNotShifted = dimensionsNotShifted[0]*dimensionsNotShifted[1];
            int totalShifted = dimensionsShifted[0]*dimensionsShifted[1] - (int)Math.floor(dimensionsShifted[1]/2);
            if(totalShifted > totalNotShifted) {
                configureFinishedPiecesShifted(workPieceDimensions);
            } else {
                configureFinishedPiecesNotShifted(workPieceDimensions);
            }
        }
    }
    
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
                this.layout.getStackingPositions().add(new StackingPosition(currentX - offsetX, currentY - offsetY, 0, null));
                currentX += unitX;
            }
            currentY += unitY;
            currentX = layout.getPalletFreeBorder() + unitX;
        }
    }
    
    private Integer[] determineNumberOfPiecesNotShifted(RoundDimensions workPieceDimensions) {
        Integer[] result = new Integer[2];
        int numberOfVertical = (int) Math.floor(layout.getUsableWidth() / (workPieceDimensions.getDiameter() + layout.getMinYGap()));
        int numberOfHorizontal = (int) Math.floor(layout.getUsableLength() / (workPieceDimensions.getDiameter() + layout.getMinXGap()));
        result[0] = numberOfHorizontal;
        result[1] = numberOfVertical;
        return result;
    }
    
    private void configureFinishedPiecesShifted(RoundDimensions workPieceDimensions) {
        Integer[] dimensions = determineHorizontalNumberOfPiecesShifted(workPieceDimensions);
        layout.setNumberOfVerticalPieces(dimensions[1]);
        layout.setNumberOfHorizontalPieces(dimensions[0]);
        
        float unitX = layout.getUsableLength() / dimensions[0];
        float offsetX = (unitX - workPieceDimensions.getDiameter())/2 + workPieceDimensions.getDiameter()/2;
        float currentX = layout.getPalletFreeBorder() + unitX;
        
        float currentY = workPieceDimensions.getDiameter()/2 + layout.getPalletFreeBorder();
        float spaceBetween = unitX - workPieceDimensions.getDiameter();
        float heightOffset = spaceBetween/2 + workPieceDimensions.getDiameter()/2;
        
        if(2*heightOffset/Math.sqrt(2) < workPieceDimensions.getDiameter() + layout.getMinInterferenceDistance()) {
            heightOffset = (float)Math.sqrt(2) * (workPieceDimensions.getDiameter()+layout.getMinInterferenceDistance()) /2;
        }
        
        for(int i = 0; i < dimensions[1]; ++i) {
            if((i % 2) == 1) {
                for(int j = 0; j < dimensions[0]-1; ++j) {
                    this.layout.getStackingPositions().add(new StackingPosition(currentX - offsetX, currentY, 0, null));
                    currentX += unitX;
                }
                currentX = layout.getPalletFreeBorder() + unitX;
            }
            else {
                for(int j = 0; j < dimensions[0]; ++j) {
                    this.layout.getStackingPositions().add(new StackingPosition(currentX - offsetX, currentY, 0, null));
                    currentX += unitX;
                }
                currentX = layout.getPalletFreeBorder() + unitX + (spaceBetween/2 + workPieceDimensions.getDiameter()/2);
            }
            currentY += heightOffset;
        }
    }
    
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
