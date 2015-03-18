package eu.robojob.millassist.external.device.stacking.stackplate.strategy.basicStackPlate;

import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.StudPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.StudPosition.StudType;
import eu.robojob.millassist.external.device.stacking.stackplate.strategy.AbstractPieceStackingStrategy;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;

public abstract class ABasicStackPlateStrategy<T extends IWorkPieceDimensions> extends AbstractPieceStackingStrategy<T, BasicStackPlateLayout> {
	
		//Calculated stackingPositions can be found in AbstractStackingLayout
		private StudPosition[][] studPositions;
		private int maxHorizontalPieces;
		private int maxVerticalPieces;
		
		protected ABasicStackPlateStrategy(BasicStackPlateLayout context) {
			super(context);
			initStudPositions();
		}
		
		/**
		 * Initialize all stud positions with their coordinates by setting the possible stud positions back to StudType.NONE
		 *
		 * @post stacking device is cleared from studs
		 */
		private void initStudPositions() {
			int verticalHoleAmount = getContext().getVerticalHoleAmount();
			int horizontalHoleAmount = getContext().getHorizontalHoleAmount();
			this.studPositions = new StudPosition[verticalHoleAmount][horizontalHoleAmount];
			for (int i = 0; i < verticalHoleAmount; i++) {
				for (int j = 0; j < horizontalHoleAmount; j++) {
					//Calculate coordinates using the origin from the stacker
					float x = j * getContext().getHorizontalHoleDistance() + getContext().getHorizontalPadding();
					float y = i * getContext().getVerticalHoleDistance() + getContext().getVerticalPaddingBottom();
					studPositions[i][j] = new StudPosition(j, i, x, y, StudType.NONE);
				}
			}
		}
		
		protected void clearStudPositions() {
			for (StudPosition[] vertPos : studPositions) {
				for (StudPosition pos : vertPos) {
					pos.setStudType(StudType.NONE);
				}
			}
		}
		
		public StudPosition[][] getStudPositions() {
			return this.studPositions;
		}
		
		protected void setMaxHorizontalPieces(int nbPieces) {
			this.maxHorizontalPieces = nbPieces;
		}
		
		protected int getMaxHorizontalPieces() {
			return this.maxHorizontalPieces;
		}
		
		protected void setMaxVerticalPieces(int nbPieces) {
			this.maxVerticalPieces = nbPieces;
		}
		
		protected int getMaxVerticalPieces() {
			return this.maxVerticalPieces;
		}
}
