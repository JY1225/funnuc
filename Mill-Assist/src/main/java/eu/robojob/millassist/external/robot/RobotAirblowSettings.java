package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.positioning.Coordinates;

public class RobotAirblowSettings {
	
	private Coordinates bottomCoord, topCoord;
	
	public RobotAirblowSettings(Coordinates bottomCoord, Coordinates topCoord) {
		this.bottomCoord = bottomCoord;
		this.topCoord = topCoord;
	}
	
	public RobotAirblowSettings() {
		this(new Coordinates(), new Coordinates());
	}

	public Coordinates getBottomCoord() {
		return bottomCoord;
	}

	public void setBottomCoord(Coordinates bottomCoord) {
		this.bottomCoord = bottomCoord;
	}

	public Coordinates getTopCoord() {
		return topCoord;
	}

	public void setTopCoord(Coordinates topCoord) {
		this.topCoord = topCoord;
	}

}
