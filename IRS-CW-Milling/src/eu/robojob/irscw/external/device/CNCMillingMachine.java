package eu.robojob.irscw.external.device;

import eu.robojob.irscw.external.communication.SocketConnection;

public class CNCMillingMachine extends AbstractProcessingDevice {

	private SocketConnection socketConnection;
	
	public CNCMillingMachine(String id, SocketConnection socketConnection) {
		super(id, true);
		this.socketConnection = socketConnection;
	}
	
	public SocketConnection getSocketConnection() {
		return socketConnection;
	}

	public void setSocketConnection(SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
	}

	@Override
	public String getStatus() {
		// TODO get status 
		return null;
	}
	
	public class CNCMillingMachinePutSettings extends AbstractDevicePutSettings{}
	public class CNCMillingMachinePickSettings extends AbstractDevicePickSettings{}
	public class CNCMillingMachineInterventionSettings extends AbstractDeviceInterventionSettings{}
	public class CNCMillingMachineStartCylusSettings extends AbstractProcessingDeviceStartCyclusSettings {}
	public class CNCMillingMachineClampingSettings extends AbstractDeviceClampingSettings {}

	@Override
	public void startCyclus(WorkArea workArea, AbstractProcessingDeviceStartCyclusSettings startCylusSettings) {
		CNCMillingMachineStartCylusSettings cncStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCylusSettings;
		// TODO implement method
	}

	@Override
	public void prepareForStartCyclus(WorkArea workArea, AbstractProcessingDeviceStartCyclusSettings startCylusSettings) {
		CNCMillingMachineStartCylusSettings cncStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCylusSettings;
		// TODO implement method
	}

	@Override
	public void releasePiece(WorkArea workArea, AbstractDeviceClampingSettings clampingSettings) {
		CNCMillingMachineClampingSettings cncClampingSettings = (CNCMillingMachineClampingSettings) clampingSettings;
		// TODO implement method
	}

	@Override
	public void grabPiece(WorkArea workArea, AbstractDeviceClampingSettings clampingSettings) {
		CNCMillingMachineClampingSettings cncClampingSettings = (CNCMillingMachineClampingSettings) clampingSettings;
		// TODO implement method
	}

	@Override
	public void prepareForPick(WorkArea workArea, AbstractDevicePickSettings pickSettings) {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		// TODO implement method
	}

	@Override
	public void prepareForPut(WorkArea workArea, AbstractDevicePutSettings putSettings) {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		// TODO implement method
	}

	@Override
	public void prepareForIntervention(WorkArea workArea, AbstractDeviceInterventionSettings interventionSettings) {
		CNCMillingMachineInterventionSettings cncInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		// TODO implement method
	}

	@Override
	public void pickFinished(WorkArea workArea, AbstractDevicePickSettings pickSettings) {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		// TODO implement method
	}

	@Override
	public void putFinished(WorkArea workArea, AbstractDevicePutSettings putSettings) {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		// TODO implement method
	}

	@Override
	public void interventionFinished(WorkArea workArea, AbstractDeviceInterventionSettings interventionSettings) {
		CNCMillingMachineInterventionSettings cncInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		// TODO implement method
	}
}
