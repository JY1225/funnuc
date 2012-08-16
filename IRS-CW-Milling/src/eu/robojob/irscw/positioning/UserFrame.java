package eu.robojob.irscw.positioning;

public class UserFrame {

	private int idNumber;
	private float zSafeDistance;
	
	// At a later stage other information can be added (e.g. coordinates)
	public UserFrame(int idNumber, float zSafeDistance) {
		this.idNumber = idNumber;
		this.zSafeDistance = zSafeDistance;
	}

	public int getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(int idNumber) {
		this.idNumber = idNumber;
	}

	public float getzSafeDistance() {
		return zSafeDistance;
	}

	public void setzSafeDistance(float zSafeDistance) {
		this.zSafeDistance = zSafeDistance;
	}
		
}
