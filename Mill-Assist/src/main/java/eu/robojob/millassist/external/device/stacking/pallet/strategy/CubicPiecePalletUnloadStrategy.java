package eu.robojob.millassist.external.device.stacking.pallet.strategy;

import javafx.geometry.Orientation;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.workpiece.RectangularDimensions;

public class CubicPiecePalletUnloadStrategy extends
        AbstractPalletUnloadStrategy<RectangularDimensions> {
    
    
    public CubicPiecePalletUnloadStrategy(PalletLayout layout) {
        super(layout);
    }

    @Override
    public void configureFinishedPieces(RectangularDimensions workPieceDimensions) {
        PalletLayoutType layoutType = this.layout.getLayoutType();
        Integer[] dimensions = new Integer[2];
        if(layoutType == PalletLayoutType.OPTIMAL || layoutType == PalletLayoutType.SHIFTED) {
            dimensions = determineOptimalNumberOfPieces(workPieceDimensions);
        }
        else if(layoutType == PalletLayoutType.HORIZONTAL) {
            dimensions = determineHorizontalOrientationNumberOfPieces(workPieceDimensions);
        }
        else if(layoutType == PalletLayoutType.VERTICAL){
            dimensions = determineVerticalOrientationNumberOfPieces(workPieceDimensions);
        }
        
        layout.setNumberOfHorizontalPieces(dimensions[0]);
        layout.setNumberOfVerticalPieces(dimensions[1]);
        
        float usableWidth = layout.getPalletWidth() - 2* layout.getPalletFreeBorder();
        float usableLength = layout.getPalletLength() - 2* layout.getPalletFreeBorder();
        float unitX = usableLength / layout.getNumberOfHorizontalPieces();
        float unitY = usableWidth / layout.getNumberOfVerticalPieces();
        float offsetX = (unitX - workPieceDimensions.getLength())/2 + workPieceDimensions.getLength()/2;
        float offsetY = (unitY - workPieceDimensions.getHeight())/2 + workPieceDimensions.getHeight()/2;
        if(layout.isRotate90()) {
            offsetX = (unitX - workPieceDimensions.getHeight())/2 + workPieceDimensions.getHeight()/2;
            offsetY = (unitY - workPieceDimensions.getLength())/2 + workPieceDimensions.getLength()/2;
        }
        
        float currentX = layout.getPalletFreeBorder() + unitX;
        float currentY = layout.getPalletFreeBorder() + unitY;
        for(int i = 0; i < layout.getNumberOfVerticalPieces(); ++i) {
            for(int j = 0; j < layout.getNumberOfHorizontalPieces(); ++j) {
                this.layout.getStackingPositions().add(new StackingPosition(currentX - offsetX, currentY - offsetY, 0, null));
                currentX += unitX;
            }
            currentY+= unitY;
            currentX = layout.getPalletFreeBorder() + unitX;
        }
        
    }
    
    private Integer[] determineHorizontalOrientationNumberOfPieces(RectangularDimensions workPieceDimensions) {
        Integer[] result = new Integer[2];
        
        float usableWidth = layout.getPalletWidth() - 2* layout.getPalletFreeBorder();
        int verAmount = (int) Math.floor(usableWidth / (workPieceDimensions.getWidth() + this.layout.getMinYGap()));
        
        float usableLength = layout.getPalletLength() - 2* layout.getPalletFreeBorder();
        int horAmount = (int) Math.floor(usableLength / (workPieceDimensions.getLength() + this.layout.getMinXGap()));
        
        result[0] = horAmount;
        result[1] = verAmount;
        layout.setRotate90(false);
        return result;
    }
    
    private Integer[] determineVerticalOrientationNumberOfPieces(RectangularDimensions workPieceDimensions) {
        Integer[] result = new Integer[2];
        
        float usableWidth = layout.getPalletWidth() - 2* layout.getPalletFreeBorder();
        float usableLength = layout.getPalletLength() - 2* layout.getPalletFreeBorder();
        int horAmount_2= (int) Math.floor(usableLength / (workPieceDimensions.getWidth() + this.layout.getMinXGap()));
        int verAmount_2 = (int) Math.floor(usableWidth / (workPieceDimensions.getLength() + this.layout.getMinYGap()));
        
        result[0] = horAmount_2;
        result[1] = verAmount_2;
        layout.setRotate90(true);
        return result;
    }
    
    private Integer[] determineOptimalNumberOfPieces(RectangularDimensions workPieceDimensions) {
        Integer[] result;
        
        Integer[] hor = determineHorizontalOrientationNumberOfPieces(workPieceDimensions);
        int total_1 = hor[0] * hor[1];
        
        Integer[] ver = determineVerticalOrientationNumberOfPieces(workPieceDimensions);
        
        int total_2 = ver[0] * ver[1];
        
        if(total_2 > total_1) {
            result = ver;
            layout.setRotate90(true);
        }
        else {
            result = hor;
            layout.setRotate90(false);
        }
        return result;
    }

}
