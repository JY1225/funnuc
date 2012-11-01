package eu.robojob.irscw.external.device.cnc;


public interface CNCMachineListener {

	public void cNCMachineConnected(CNCMachineEvent event);
	public void cNCMachineDisconnected(CNCMachineEvent event);
	
	public void cNCMachineStatusChanged(CNCMachineStatusChangedEvent event);
	public void cNCMachineAlarmsOccured(CNCMachineAlarmsOccuredEvent event);
	
}
