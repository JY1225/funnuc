package eu.robojob.millassist.external.device.stacking.conveyor;

import eu.robojob.millassist.util.Translator;

public abstract class ConveyorAlarm {

	private static final int DEFAULT_PRIORITY = 5;

	private int id;
	
	public ConveyorAlarm(final int id) {
		this.id = id;
	}
	
	public String getLocalizedMessage() {
		return Translator.getTranslation("ConveyorAlarm." + id);
	}
	
	public String getMessage() {
		return "Conveyor alarm: id = " + id;
	}
	
	//TODO implement priorities, for now: all the same
	public int getPriority() {
		return DEFAULT_PRIORITY;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o instanceof ConveyorAlarm) {
			if (((ConveyorAlarm) o).getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return "Conveyor alarm: " + id;
	}
	
}
