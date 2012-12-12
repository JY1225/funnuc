package eu.robojob.irscw.external.device.processing.cnc;


public interface CNCMachineListener {

	void cNCMachineConnected(CNCMachineEvent event);
	void cNCMachineDisconnected(CNCMachineEvent event);
	
	void cNCMachineStatusChanged(CNCMachineEvent event);
	void cNCMachineAlarmsOccured(CNCMachineAlarmsOccuredEvent event);
	
}
