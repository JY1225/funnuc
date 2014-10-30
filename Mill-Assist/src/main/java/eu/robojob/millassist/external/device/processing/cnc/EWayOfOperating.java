package eu.robojob.millassist.external.device.processing.cnc;

public enum EWayOfOperating {
	
	START_STOP(1) {
		@Override
		public int getNbOfSides() {
			return 1;
		}
	},
	M_CODES(2) {
		@Override
		public int getNbOfSides() {
			return 1;
		}
	},
	M_CODES_DUAL_LOAD(3) {
		@Override
		public int getNbOfSides() {
			return 2;
		}
	};
	
	public abstract int getNbOfSides();
	
	private int id;
	
	private EWayOfOperating(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static EWayOfOperating getWayOfOperatingById(int id) {
		for (EWayOfOperating wayOfOperating: values()) {
			if (wayOfOperating.getId() == id) {
				return wayOfOperating;
			}
		}
		throw new IllegalArgumentException("Unknown wayOfOperating " + id);
	}
}