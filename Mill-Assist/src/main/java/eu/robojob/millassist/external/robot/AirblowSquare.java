package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.positioning.Coordinates;

public class AirblowSquare {
	
	private Coordinates bottomCoord, topCoord;
	
	public AirblowSquare(Coordinates bottomCoord, Coordinates topCoord) {
		this.bottomCoord = bottomCoord;
		this.topCoord = topCoord;
	}
	
	public AirblowSquare() {
		this(new Coordinates(), new Coordinates());
	}

	/**
	 * Get the coordinates of the lower left corner.
	 * 
	 * @return
	 */
	public Coordinates getBottomCoord() {
		return bottomCoord;
	}

	public void setBottomCoord(Coordinates bottomCoord) {
		this.bottomCoord = bottomCoord;
	}

	/**
	 * Get the coordinates of the upper right corner
	 * 
	 * @return
	 */
	public Coordinates getTopCoord() {
		return topCoord;
	}

	public void setTopCoord(Coordinates topCoord) {
		this.topCoord = topCoord;
	}
	
	public boolean isCoordinateInsideArea(Coordinates coord) {
	    if (coord.getX() < getBottomCoord().getX())
	        return false;
	    if (coord.getY() < getBottomCoord().getY())
	        return false;
	    if (coord.getX() > getTopCoord().getX())
	        return false;
	    if (coord.getY() > getTopCoord().getY())
	        return false;
	    if (coord.getZ() < getBottomCoord().getZ())
	        return false;
	    if (coord.getZ() > getTopCoord().getZ())
	        return false;
	    return true;
	}

}
