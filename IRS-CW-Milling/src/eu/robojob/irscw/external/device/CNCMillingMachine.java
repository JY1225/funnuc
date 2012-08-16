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
	public void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) {
		CNCMillingMachineStartCylusSettings cncStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCylusSettings;
		// TODO implement method
	}

	@Override
	public void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) {
		CNCMillingMachineStartCylusSettings cncStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCylusSettings;
		// TODO implement method
	}

	@Override
	public void releasePiece(AbstractDeviceClampingSettings clampingSettings) {
		CNCMillingMachineClampingSettings cncClampingSettings = (CNCMillingMachineClampingSettings) clampingSettings;
		// TODO implement method
	}

	@Override
	public void grabPiece(AbstractDeviceClampingSettings clampingSettings) {
		CNCMillingMachineClampingSettings cncClampingSettings = (CNCMillingMachineClampingSettings) clampingSettings;
		// TODO implement method
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		// TODO implement method
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		// TODO implement method
	}

	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) {
		CNCMillingMachineInterventionSettings cncInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		// TODO implement method
	}

	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings) {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		// TODO implement method
	}

	@Override
	public void putFinished(AbstractDevicePutSettings putSettings) {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		// TODO implement method
	}

	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) {
		CNCMillingMachineInterventionSettings cncInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		// TODO implement method
	}
}
