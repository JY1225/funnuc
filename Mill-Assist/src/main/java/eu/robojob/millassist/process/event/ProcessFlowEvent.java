package eu.robojob.millassist.process.event;

import eu.robojob.millassist.process.ProcessFlow;

public class ProcessFlowEvent {

	private ProcessFlow source;
	private int id;
	private long when;
	
	public static final int MODE_CHANGED = 1;
	public static final int ACTIVE_STEP_CHANGED = 2;
	public static final int DATA_CHANGED = 3;
	public static final int FINISHED_AMOUNT_CHANGED = 4;
	public static final int EXCEPTION_OCCURED = 5;
	
	public ProcessFlowEvent(final ProcessFlow source, final int id) {
		this.source = source;
		this.id = id;
		this.when = System.currentTimeMillis();
	}

	public ProcessFlow getSource() {
		return source;
	}

	public int getId() {
		return id;
	}

	public long getWhen() {
		return when;
	}
	
	@Override
	public String toString() {
		switch (id) {
			case MODE_CHANGED:
				return "ProcessFlowEvent: MODE CHANGED: ";
			case ACTIVE_STEP_CHANGED:
				return "ProcessFlowEvent: ACTIVE_STEP_CHANGED: ";
			case DATA_CHANGED:
				return "ProcessFlowEvent: DATA_CHANGED: ";
			case FINISHED_AMOUNT_CHANGED:
				return "ProcessFlowEvent: FINISHED_AMOUNT_CHANGED: ";
			default: 
				throw new IllegalStateException("Unknown id.");
		}
	}
}
