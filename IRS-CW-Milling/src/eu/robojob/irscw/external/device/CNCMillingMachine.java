package eu.robojob.irscw.external.device;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePickSettings;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePutSettings;

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
	
	public class CNCMillingMachinePutSettings extends AbstractDevicePutSettings{
		public CNCMillingMachinePutSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public class CNCMillingMachinePickSettings extends AbstractDevicePickSettings{
		public CNCMillingMachinePickSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public class CNCMillingMachineInterventionSettings extends AbstractDeviceInterventionSettings{
		public CNCMillingMachineInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public class CNCMillingMachineStartCylusSettings extends AbstractProcessingDeviceStartCyclusSettings {
		public CNCMillingMachineStartCylusSettings(WorkArea workArea) {
			super(workArea);
		}
	}

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
	public void releasePiece(AbstractDevicePickSettings pickSettings) {
	CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		// TODO implement method
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings) {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
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
