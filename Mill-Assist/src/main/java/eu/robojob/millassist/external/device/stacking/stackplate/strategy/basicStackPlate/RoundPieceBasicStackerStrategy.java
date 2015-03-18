package eu.robojob.millassist.external.device.stacking.stackplate.strategy.basicStackPlate;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.StudPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.StudPosition.StudType;
import eu.robojob.millassist.workpiece.RoundDimensions;

public final class RoundPieceBasicStackerStrategy extends ABasicStackPlateStrategy<RoundDimensions> {
	
	public RoundPieceBasicStackerStrategy(BasicStackPlateLayout context) {
		super(context);
	}	

	@Override
	public void configureOnlyRawStackingPos(RoundDimensions rawDimensions) throws IncorrectWorkPieceDataException {
		configureSinglePiece(rawDimensions);
	}

	@Override
	public void configureSameDimensionPositions(RoundDimensions rawPiece) throws IncorrectWorkPieceDataException {
		configureSinglePiece(rawPiece);
		// Same dimensions - add the raw stacking positions to the finished list as well
		getContext().getFinishedStackingPositions().addAll(getContext().getRawStackingPositions());
	}

	@Override
	public void configureStackingPositions(RoundDimensions rawPiece, RoundDimensions finishedPiece) throws IncorrectWorkPieceDataException {
		isValidWorkPiece(rawPiece);
		isValidWorkPiece(finishedPiece);

		//Raw is the largest piece
		if (rawPiece.compareTo(finishedPiece) == 1) {
			configureStacking(finishedPiece, rawPiece, false);
		} else {
			configureStacking(rawPiece, finishedPiece, true);
		}
	}
	
	private void configureSinglePiece(RoundDimensions dimensions) throws IncorrectWorkPieceDataException {
		isValidWorkPiece(dimensions);
		int nbHorizontalCovered = getNbStudsCoveredHorizontal(
				dimensions.getDiameter(), 
				getContext().getStudDiameter(), 
				getContext().getHorizontalHoleDistance(),
				getContext().getMinOverlap());
		double midX = getX(getContext().getHorizontalHoleDistance(), nbHorizontalCovered);
		int nxtHorizontal = nbHorizontalCovered;
		if (isInterfering(dimensions.getDiameter(), midX)) {
			nxtHorizontal++;
		}
		int nbVertical = getNbStudsCoveredVertical(dimensions);
		double yExtra = getYCompensation(dimensions.getDiameter(), getContext().getStudDiameter(), getContext().getHorizontalHoleDistance(), nbHorizontalCovered);
		int nbFirstVertical = getFirstStudPosVertical(yExtra);
		int firstHorizontal = getFirstStudPosHorizontal(dimensions, dimensions.getDiameter()/2 - midX);
		calcMaxHorizontalAmount(nxtHorizontal, nbHorizontalCovered, firstHorizontal);
		calcMaxVerticalAmount(nbVertical, dimensions.getDiameter(), nbFirstVertical, yExtra);
		yExtra -= dimensions.getDiameter() / 2;
		configureStackingPositions(nbHorizontalCovered, firstHorizontal, nbVertical, nbFirstVertical, nxtHorizontal, midX, yExtra);
	}
	
	/**
	 * 
	 * @param smallestPiece	- smallest piece
	 * @param largestPiece	- largest piece
	 * @param smallIsRaw	- flag indicating that the smallest piece given is the raw one or not
	 */
	private void configureStacking(RoundDimensions smallestPiece, RoundDimensions largestPiece, boolean smallIsRaw) {
		int smallCovered = getNbStudsCoveredHorizontal(
				smallestPiece.getDiameter(), 
				getContext().getStudDiameter(), 
				getContext().getHorizontalHoleDistance(),
				getContext().getMinOverlap());
		int largeCovered = getNbStudsCoveredHorizontal(
				largestPiece.getDiameter(), 
				getContext().getStudDiameter(), 
				getContext().getHorizontalHoleDistance(),
				getContext().getMinOverlap());
		int nbHorizontal = smallCovered;
		int nxtHorizontal = largeCovered;
		double midX = getX(getContext().getHorizontalHoleDistance(), nbHorizontal);
		if (isInterfering(largestPiece.getDiameter(), getX(getContext().getHorizontalHoleDistance(), largeCovered))) {
			nxtHorizontal++;
		}
		int nbVertical = getNbStudsCoveredVertical(largestPiece);
		double yExtraSmall = getYCompensation(smallestPiece.getDiameter(), getContext().getStudDiameter(), getContext().getHorizontalHoleDistance(), nbHorizontal);
		double yExtraLarge = getYCompensation(largestPiece.getDiameter(), getContext().getStudDiameter(), getContext().getHorizontalHoleDistance(), nbHorizontal);
		int nbFirstVertical = getFirstStudPosVertical(yExtraSmall);
		int firstHorizontal = getFirstStudPosHorizontal(largestPiece, (largestPiece.getDiameter() / 2 - midX));
		calcMaxHorizontalAmount(nxtHorizontal, nbHorizontal, firstHorizontal);
		calcMaxVerticalAmount(nbVertical, largestPiece.getDiameter(), nbFirstVertical, yExtraLarge);
		yExtraSmall -= smallestPiece.getDiameter() / 2;
		yExtraLarge -= largestPiece.getDiameter() / 2;
		if (smallIsRaw) {
			configureStackingPositions(nbHorizontal, firstHorizontal, nbVertical, nbFirstVertical, nxtHorizontal, midX, yExtraSmall, yExtraLarge);
		} else {
			configureStackingPositions(nbHorizontal, firstHorizontal, nbVertical, nbFirstVertical, nxtHorizontal, midX, yExtraLarge, yExtraSmall);
		}
	}
	
	/**
	 * 
	 * @param nbHorizontal			- number of stud positions needed in the horizontal direction
	 * @param nbVertical			- number of stud positions needed in the vertical direction
	 * @param nxtStartHorizontal	- number of stud positions until the next piece can be placed
	 * @param xExtra				- x distance to add with the first stud position (x till the center of the circle)
	 * @param yExtra				- y distance to add with the first stud position (y till the center of the circle)
	 */
	private void configureStackingPositions(int nbHorizontal, int firstHorizontal, int nbVertical, int nbFirstVertical, int nxtStartHorizontal, double xExtra, double yExtra) {
		int vIndex = nbFirstVertical - 1; int hIndex = firstHorizontal - 1;
		int i = 0; int j = 0;
		StudPosition stPos1 = null;
		StudPosition stPos2 = null;
		while (j < getMaxVerticalPieces() ) {
			while (i < getMaxHorizontalPieces()) {
				stPos1 = new StudPosition(hIndex, vIndex, getStudPositions()[vIndex][hIndex].getCenterPosition(), StudType.NORMAL);
				stPos2 = new StudPosition(hIndex + nbHorizontal - 1, vIndex, getStudPositions()[vIndex][hIndex + nbHorizontal - 1].getCenterPosition(), StudType.NORMAL);
				StackPlateStackingPosition stackingPos = new StackPlateStackingPosition((float) (stPos1.getCenterPosition().getX() + xExtra), 
						(float) (stPos1.getCenterPosition().getY() - yExtra), getContext().getHorizontalR(), null, 0, 0);
				stackingPos.addstud(stPos1);
				stackingPos.addstud(stPos2);
				getContext().getRawStackingPositions().add(stackingPos);
				hIndex += nxtStartHorizontal;
				i++;
			}
			vIndex += nbVertical;
			hIndex = firstHorizontal - 1;
			i = 0; j++;
		}
	}
	
	/**
	 * 
	 * @param nbHorizontal			- number of stud positions needed in the horizontal direction
	 * @param nbVertical			- number of stud positions needed in the vertical direction
	 * @param nxtStartHorizontal	- number of stud positions until the next piece can be placed
	 * @param xExtra				- x distance to add with the first stud position (x till the center of the circle)
	 * @param yExtraRaw				- y distance of the raw workPiece to add with the first stud position (y till the center of the circle)
	 * @param yExtraFinished		- y distance of the finished workPiece to add with the first stud position (y till the center of the circle)
	 */
	private void configureStackingPositions(int nbHorizontal, int firstHorizontal, int nbVertical, int nbFirstVertical, int nxtStartHorizontal, double xExtra, double yExtraRaw, double yExtraFinished) {
		int vIndex = nbFirstVertical - 1; int hIndex = firstHorizontal - 1;
		int i = 0; int j = 0;
		StudPosition stPos1 = null;
		StudPosition stPos2 = null;
		while (j < getMaxVerticalPieces()) {
			while (i < getMaxHorizontalPieces()) {
				stPos1 = new StudPosition(hIndex, vIndex, getStudPositions()[vIndex][hIndex].getCenterPosition(), StudType.NORMAL);
				stPos2 = new StudPosition(hIndex + nbHorizontal - 1, vIndex, getStudPositions()[vIndex][hIndex + nbHorizontal - 1].getCenterPosition(), StudType.NORMAL);
				StackPlateStackingPosition stackingPosRaw = new StackPlateStackingPosition((float) (stPos1.getCenterPosition().getX() + xExtra), 
						(float) (stPos1.getCenterPosition().getY() - yExtraRaw), getContext().getHorizontalR(), null, 0, 0);
				StackPlateStackingPosition stackingPosFinished = new StackPlateStackingPosition((float) (stPos1.getCenterPosition().getX() + xExtra), 
						(float) (stPos1.getCenterPosition().getY() - yExtraFinished), getContext().getHorizontalR(), null, 0, 0);
				stackingPosRaw.addstud(stPos1);
				stackingPosRaw.addstud(stPos2);
				stackingPosFinished.addstud(stPos1);
				stackingPosFinished.addstud(stPos2);
				getContext().getRawStackingPositions().add(stackingPosRaw);
				getContext().getFinishedStackingPositions().add(stackingPosFinished);
				hIndex += nxtStartHorizontal;
				i++;
			}
			vIndex += nbVertical;
			hIndex = firstHorizontal - 1;
			j++; i = 0;
		}
	}
	
	@Override
	protected void isValidWorkPiece(RoundDimensions dimensions) throws IncorrectWorkPieceDataException {
		if(dimensions == null || !dimensions.isValidDimension()) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_DATA);
		}
		// HoleDistance - studDiameter is de vrije ruimte tussen 2 paaltjes. Als de diameter kleiner is, valt het stuk er tussen.
		if (dimensions.getDiameter() - getContext().getMinOverlap() < (getContext().getHorizontalHoleDistance() - getContext().getStudDiameter())) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_SMALL);
		}
		if (dimensions.getDiameter() > (getContext().getPlateWidth() + getContext().getMaxOverflow() + getContext().getMaxUnderflow())) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}
	}

	private void calcMaxHorizontalAmount(int nbTillNext, int nbStudsNeeded, int firstHorizontal) {
		int nbPieces = (int) Math.floor((getContext().getHorizontalHoleAmount() - firstHorizontal) / (float) nbTillNext);
		int nbPosRemaining = getContext().getHorizontalHoleAmount() - nbPieces * nbTillNext;
		if (nbPosRemaining >= nbStudsNeeded) {
			nbPieces++;
		}
		setMaxHorizontalPieces(nbPieces);
	}
	
	/**
	 * Calculate the number of subsequent stud positions covered by a round piece
	 * 
	 * @param 	diameter of the round piece 
	 * @param 	studDiameter is the diameter of the stabilizing studs
	 * @param 	holeDistance is the distance between two subsequent stud positions
	 * @return	number of stud positions covered by a work piece with a given diameter
	 */
	private static int getNbStudsCoveredHorizontal(final double diameter, final double studDiameter, final double holeDistance, final double minOverlap) {
		int amountOfStudsWorkPiece = 2;
		// HoleDistance - studDiameter is de vrije ruimte tussen 2 paaltjes. Als de diameter kleiner is, valt het stuk er tussen.
		double disRest = diameter - (holeDistance - studDiameter) - minOverlap;
		while (disRest > holeDistance) {
			disRest -= holeDistance;
			amountOfStudsWorkPiece++;
		}
		return amountOfStudsWorkPiece;
	}
	
	/**
	 * 
	 * @param dimensions
	 * @param disLeftNotCovered	is the distance left from the first stabilizing stud
	 * @return
	 */
	private int getFirstStudPosHorizontal(RoundDimensions dimensions, double disLeftNotCovered) {
		int first = 1;
		double disToCover = disLeftNotCovered - getContext().getHorizontalPadding();
		double overflowPercentage = (getSurfaceCircularSegment(disToCover, dimensions.getDiameter()/2) / getSurfaceCircle( dimensions.getDiameter()/2)) * 100;
		while (disToCover > 0 && (disToCover > getContext().getMaxOverflow() || overflowPercentage > getContext().getOverflowPercentage())) {
			first++;
			disToCover -= getContext().getHorizontalHoleDistance();
		}
		return first;
	}
	
	/**
	 * Check that the distance between two subsequent pieces is large enough (interference distance)
	 * 
	 * @param 	diameter of the round piece
	 * @param 	middleX is the distance from the center of the piece till the center of one of the stabilizing studs (horizontal)
	 * @return	true in case the distance between two subsequent pieces is not big enough taking the interference distance into account.
	 * 			false, otherwise.
	 */
	private boolean isInterfering(final double diameter, double middleX) {
		//Radius - middleX is the distance from the last stabilizing stud till the piece (part of the piece coming after last stud)
		if (getContext().getHorizontalHoleDistance() - (2*(diameter/2 - middleX)) <= getContext().getInterferenceDistance()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Calculate the X value from the center of the first stabilizing stud till the center of the round piece
	 * 
	 * @param 	diameter of the round piece
	 * @param 	holeDistance is the distance between two subsequent stud positions
	 * @param 	nbStuds is the number of stud positions covered between the first and last stabilizing stud (taken the first and last into account)
	 * @return	(nbStuds - 1) * holeDistance / 2
	 */
	private static double getX(final double holeDistance, final int nbStuds) {
		return ((nbStuds - 1) * holeDistance) / 2;
	}
	
	private void calcMaxVerticalAmount(int nbStudsNeeded, double diameter, int nbFirstVertical, double yComp) {
		int nbPieces = (int) Math.floor((getContext().getVerticalHoleAmount()) / (nbStudsNeeded));
		if (canPlaceExtraTop(diameter, (getContext().getVerticalHoleAmount() - nbPieces * nbStudsNeeded - nbFirstVertical + 1), yComp)) {
			nbPieces++;
		}
		setMaxVerticalPieces(nbPieces);
	}
	
	private int getFirstStudPosVertical(double yDown) {
		double underflow = yDown - getContext().getVerticalPaddingBottom();
		int first = 1;
		while (underflow > getContext().getMaxUnderflow()) {
			underflow -= getContext().getVerticalHoleDistance();
			first++;
		}
		return first;
	}
	
	private int getNbStudsCoveredVertical(RoundDimensions dimensions) {
		double disRest = dimensions.getDiameter() + getContext().getInterferenceDistance();
		// 1e posities coveret ge altijd - rekening houden met afstand dat stuk valt?
		return ((int) Math.floor(disRest / getContext().getVerticalHoleDistance())) + 1;
	}
	
	private boolean canPlaceExtraTop(double diameter, int nbPosLeft, double yComp) {
		if (nbPosLeft <= 0) {
			return false;
		}
		// Er zijn een aantal stud posities over. Voor de berekening gaan we de YCompensation verwaarlozen.
		float disRest = ((nbPosLeft-1) * getContext().getVerticalHoleDistance()) + getContext().getVerticalPaddingTop();
		double overflow = diameter - disRest - yComp;
		double overflowPercentage = (getSurfaceCircularSegment(overflow, diameter/2) / getSurfaceCircle(diameter/2)) * 100;
		if (overflow < 0 || (overflow < getContext().getMaxOverflow() && overflowPercentage <= getContext().getOverflowPercentage())) {
			return true;
		} 
		return false;
	}
	
	private static double getSurfaceCircularSegment(double heightSegment, double radius) {
		double alpha = 2 * Math.acos(1 - (heightSegment / radius));
		return (radius * radius * (alpha - Math.sin(alpha)) / 2);
	}
	
	private static double getSurfaceCircle(double radius) {
		return (radius * radius * Math.PI);
	}
	
	/**
	 * Calculate the distance that a round piece with a given diameter falls down. A round piece is put at a stable position
	 * between two studs. The piece will fall down so that it is centered between the two studs. There is a small distance
	 * that the piece lays lower than the stabilizing stud. This distance is calculated in this method.
	 * 
	 * @param 	diameter of the round piece
	 * @param 	studDiameter is the diameter of the stabilizing studs
	 * @param 	holeDistance is the distance between two subsequent stud positions
	 * @param 	nbStuds is the number of stud positions covered between the first and the last stabilizing stud (taken the first and last into account)
	 * @return	distance that the round pieces is lower than the center of the stabilizing studs
	 */
	private static double getYCompensation(final double diameter, final double studDiameter, final double holeDistance, final int nbStuds) {
		//Straal stuk + straal paaltje (schuine zijde)
		double C = (diameter/2) + studDiameter /2;
		// (aantal paaltjes - 1) * afstand tussen paaltjes / 2 (middelpunt tot middelpunt)
		double B = getX(holeDistance, nbStuds);
		//Pythagoras (C² = A² + B²) 
		double A = Math.sqrt((Math.pow(C, 2) - Math.pow(B, 2)));
		//Straal cirkel - overstaande rechthoekzijde = extra Y
		return (diameter/2 - A);
	}	
	
}
