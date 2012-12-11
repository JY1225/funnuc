package eu.robojob.irscw.external.device.processing.cnc;


public interface CNCMachineListener {

	public void cNCMachineConnected(CNCMachineEvent event);
	public void cNCMachineDisconnected(CNCMachineEvent event);
	
	public void cNCMachineStatusChanged(CNCMachineEvent event);
	public void cNCMachineAlarmsOccured(CNCMachineAlarmsOccuredEvent event);
	
}
