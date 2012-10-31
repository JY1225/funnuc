package eu.robojob.irscw.external.device.cnc;

import java.util.List;

public interface CNCMillingMachineListener {

	public void cNCMillingMachineConnected(CNCMillingMachineEvent event);
	public void cNCMillingMachineDisconnected(CNCMillingMachineEvent event);
	
	public void cNCMillingMachineStatusChanged(CNCMillingMachineEvent event, CNCMillingMachineStatus status);
	public void cNCMillingMachineAlarmsOccured(CNCMillingMachineEvent event, List<CNCMillingMachineAlarm> alarms);
	
}
