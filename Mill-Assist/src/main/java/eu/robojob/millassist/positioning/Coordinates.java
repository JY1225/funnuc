package eu.robojob.millassist.positioning;

import eu.robojob.millassist.external.robot.AirblowSquare;


public class Coordinates {
	
	private int id;
	private float x;
	private float y;
	private float z;
	private float w;
	private float p;
	private float r;
		
	public Coordinates(final float x, final float y, final float z, final float w, final float p, final float r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.p = p;
		this.r = r;
	}
	
	public Coordinates(final Coordinates c) {
		this.x = c.getX();
		this.y = c.getY();
		this.z = c.getZ();
		this.w = c.getW();
		this.p = c.getP();
		this.r = c.getR();
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public Coordinates() {
		this(0f, 0f, 0f, 0f, 0f, 0f);
	}

	public float getX() {
		return x;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(final float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(final float z) {
		this.z = z;
	}

	public float getW() {
		return w;
	}

	public void setW(final float w) {
		this.w = w;
	}

	public float getP() {
		return p;
	}

	public void setP(final float p) {
		this.p = p;
	}

	public float getR() {
		return r;
	}

	public void setR(final float r) {
		this.r = r;
	}
	
	public void minus(final Coordinates c) {
		setX(getX() - c.getX());
		setY(getY() - c.getY());
		setZ(getZ() - c.getZ());
		setW(getW() - c.getW());
		setP(getP() - c.getP());
		setR(getR() - c.getR());
	}
	
	public void plus(final Coordinates c) {
		setX(getX() + c.getX());
		setY(getY() + c.getY());
		setZ(getZ() + c.getZ());
		setW(getW() + c.getW());
		setP(getP() + c.getP());
		setR(getR() + c.getR());
	}
	
	public Coordinates calculateOffset(final Coordinates coordinates) {
		return new Coordinates(getX() - coordinates.getX(), getY() - coordinates.getY(), getZ() - coordinates.getZ(), getW() - coordinates.getW(), getP() - coordinates.getP(), getR() - coordinates.getR());
	}
	
	public void offset(final Coordinates coordinates) {
		setX(getX() + coordinates.getX());
		setY(getY() + coordinates.getY());
		setZ(getZ() + coordinates.getZ());
		setW(getW() + coordinates.getW());
		setP(getP() + coordinates.getP());
		setR(getR() + coordinates.getR());
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ", " + p + ", " + r + ")";
	}
	
	/**
	 * Set the values of the coordinate given a list of values. The order of the values should
	 * match the order X,Y,Z,W,P,R. Only the given values will be updated. This means that in case
	 * the list of values contains only 2 values, only X and Y will be updated.
	 * 
	 * @param coordValues
	 * @post this.getX() == coordValues[0]
	 * 		 this.getY() == coordValues[1]
	 * 		 this.getZ() == coordValues[2]
	 * 		 this.getW() == coordValues[3]
	 * 		 this.getP() == coordValues[4]
	 * 		 this.getR() == coordValues[5]	
	 */
	public void setCoordinateValues(final float[] coordValues) {
		try {
			setX(coordValues[0]);
			setY(coordValues[1]);
			setZ(coordValues[2]);
			setW(coordValues[3]);
			setP(coordValues[4]);
			setR(coordValues[5]);
		} catch (IndexOutOfBoundsException e) {
			return;
		}
	}
	
	public float[] getCoordValues() {
		return new float[]{getX(), getY(), getZ(), getW(), getP(), getR()};
	}
	
	public static Coordinates add(Coordinates coord1, Coordinates coord2) {
		Coordinates coordResult = new Coordinates();
		coordResult.plus(coord1);
		coordResult.plus(coord2);
		return coordResult;
	}
	
	public boolean isInsideSquare(AirblowSquare square) {
		if (getX() < square.getBottomCoord().getX())
			return false;
		if (getY() < square.getBottomCoord().getY())
			return false;
		if (getX() > square.getTopCoord().getX())
			return false;
		if (getY() > square.getTopCoord().getY())
			return false;
		if (getZ() < square.getBottomCoord().getZ())
			return false;
		if (getZ() < square.getTopCoord().getZ())
			return false;
		return true;
	}
}
