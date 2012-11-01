package eu.robojob.irscw.external.device.cnc;

import java.util.List;

public interface CNCMachineListener {

	public void cNCMachineConnected(CNCMachineEvent event);
	public void cNCMachineDisconnected(CNCMachineEvent event);
	
	public void cNCMachineStatusChanged(CNCMachineStatusChangedEvent event);
	public void cNCMachineAlarmsOccured(CNCMachineAlarmsOccuredEvent event);
	
}
