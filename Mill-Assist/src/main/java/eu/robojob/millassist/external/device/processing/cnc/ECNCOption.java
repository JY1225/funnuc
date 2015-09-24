package eu.robojob.millassist.external.device.processing.cnc;

public enum ECNCOption {
	
	//Turn in Machine
	TIM_ALLOWED(1),
	MACHINE_AIRBLOW(2),
	WORKNUMBER_SEARCH(3),
	CLAMPING_PRESSURE_SELECTABLE(4);
	
	private int id;
	
	private ECNCOption(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static ECNCOption getCNCOptionById(int id) {
		for (ECNCOption cncOptionId: values()) {
			if (cncOptionId.getId() == id) {
				return cncOptionId;
			}
		}
		throw new IllegalArgumentException("Unknown option " + id);
	}
}