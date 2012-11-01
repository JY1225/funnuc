package eu.robojob.irscw.external.device.cnc;

public class CNCMachineStatusChangedEvent extends CNCMachineEvent {

	private CNCMachineStatus status;
	
	public CNCMachineStatusChangedEvent(AbstractCNCMachine source, CNCMachineStatus status) {
		super(source, CNCMachineEvent.STATUS_CHANGED);
		this.status = status;
	}

	public CNCMachineStatus getStatus() {
		return status;
	}
}
