package eu.robojob.irscw.process.event;

import eu.robojob.irscw.process.ProcessFlow;

public class ProcessFlowEvent {

	private ProcessFlow source;
	private int id;
	private long when;
	
	public static final int MODE_CHANGED = 1;
	public static final int ACTIVE_STEP_CHANGED = 2;
	public static final int EXCEPTION_OCCURED = 4;
	public static final int DATA_CHANGED = 5;
	public static final int FINISHED_AMOUNT_CHANGED = 6;
	
	public ProcessFlowEvent(ProcessFlow source, int id) {
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
}
