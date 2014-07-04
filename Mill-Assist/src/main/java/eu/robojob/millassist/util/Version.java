package eu.robojob.millassist.util;

public class Version implements Comparable<Version> {
	
	private int majorReleaseId, minorReleaseId, patchReleaseId;
	
	public Version(int majorReleaseId, int minorReleaseId, int patchReleaseId) {
		this.majorReleaseId = majorReleaseId;
		this.minorReleaseId = minorReleaseId;
		this.patchReleaseId = patchReleaseId;
	}
	
	public Version(String version) {
		String[] releaseIds = version.split("\\.");
		try{
			this.majorReleaseId = Integer.parseInt(releaseIds[0]);
			this.minorReleaseId = Integer.parseInt(releaseIds[1]);
			this.patchReleaseId = Integer.parseInt(releaseIds[2]);
		} catch(NumberFormatException e) {
			//Version is not correctly formatted
			e.printStackTrace();
		}
	}
	
	public int getMajorReleaseId() {
		return majorReleaseId;
	}

	public int getMinorReleaseId() {
		return minorReleaseId;
	}

	public int getPatchReleaseId() {
		return patchReleaseId;
	}

	@Override
	public int compareTo(Version v2) {
		if(this.majorReleaseId > v2.getMajorReleaseId())
			return 1;
		if(this.minorReleaseId > v2.getMinorReleaseId())
			return 1;
		if(this.patchReleaseId > v2.getPatchReleaseId() )
			return 1;
		else if(this.patchReleaseId < v2.getPatchReleaseId())
			return -1;
		return 0;
	}
	
	@Override
	public String toString() {
		return this.majorReleaseId + "." + this.minorReleaseId + "." + this.patchReleaseId;
	}

}
