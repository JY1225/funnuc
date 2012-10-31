package eu.robojob.irscw.external.device.cnc;

public class CNCMillingMachineStatus {

	private int status;
	
	public CNCMillingMachineStatus(int status) {
		setStatus(status);
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public boolean isPutWa1Allowed() {
		return ((status & CNCMachineConstants.R_PUT_WA1_ALLOWED) > 0);
	}
	public boolean isPutWa2Allowed() {
		return ((status & CNCMachineConstants.R_PUT_WA2_ALLOWED) > 0);
	}
	public boolean isPutWa1Ready() {
		return ((status & CNCMachineConstants.R_PUT_WA1_READY) > 0);
	}
	public boolean isPutWa2Ready() {
		return ((status & CNCMachineConstants.R_PUT_WA2_READY) > 0);
	}
	public boolean isClampWa1Ready() {
		return ((status & CNCMachineConstants.R_CLAMP_WA1_READY) > 0);
	}
	public boolean isClampWa2Ready() {
		return ((status & CNCMachineConstants.R_CLAMP_WA2_READY) > 0);
	}
	public boolean isCycleStartedWa1() {
		return ((status & CNCMachineConstants.R_CYCLE_STARTED_WA1) > 0);
	}
	public boolean isCycleStartedWa2() {
		return ((status & CNCMachineConstants.R_CYCLE_STARTED_WA2) > 0);
	}
	public boolean isPickWa1Requested() {
		return ((status & CNCMachineConstants.R_PICK_WA1_REQUESTED) > 0);
	}
	public boolean isPickWa2Requested() {
		return ((status & CNCMachineConstants.R_PICK_WA2_REQUESTED) > 0);
	}
	public boolean isPickWa1Ready() {
		return ((status & CNCMachineConstants.R_PICK_WA1_READY) > 0);
	}
	public boolean isPickWa2Ready() {
		return ((status & CNCMachineConstants.R_PICK_WA2_READY) > 0);
	}
	public boolean isUnclampWa1Ready() {
		return ((status & CNCMachineConstants.R_UNCLAMP_WA1_READY) > 0);
	}
	public boolean isUnclampWa2Ready() {
		return ((status & CNCMachineConstants.R_UNCLAMP_WA2_READY) > 0);
	}
}
