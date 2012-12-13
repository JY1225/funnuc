package eu.robojob.irscw.positioning;

public class UserFrame {

	private int idNumber;
	private float zSafeDistance;
	
	public UserFrame(final int idNumber, final float zSafeDistance) {
		this.idNumber = idNumber;
		this.zSafeDistance = zSafeDistance;
	}

	public int getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(final int idNumber) {
		this.idNumber = idNumber;
	}

	public float getzSafeDistance() {
		return zSafeDistance;
	}

	public void setzSafeDistance(final float zSafeDistance) {
		this.zSafeDistance = zSafeDistance;
	}
		
}
