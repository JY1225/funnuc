package eu.robojob.irscw.external.device.cnc;

import java.util.List;

public interface CNCMillingMachineListener {

	public void CNCMillingMachineConnected(CNCMillingMachineEvent event);
	public void CNCMillingMachineDisconnected(CNCMillingMachineEvent event);
	
	public void CNCMillingMachineStatusChanged(CNCMillingMachineEvent event, CNCMillingMachineStatus status);
	public void CNCMillingMachineAlarmsOccured(CNCMillingMachineEvent event, List<CNCMillingMachineAlarm> alarms);
	
}
