package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.SocketConnection;

public class CNCMillingMachine extends AbstractCNCMachine {

	private SocketConnection socketConnection;
	
	private static Logger logger = Logger.getLogger(AbstractProcessingDevice.class);
	
	private static final String STATUS = "STATUS";
	private static final String START_CYCLUS = "START_CYCLUS";
	private static final String PREAPARE_FOR_START_CYCLUS = "PREAPARE_FOR_START_CYCLUS";
	private static final String RELEASE_PIECE = "RELEASE_PIECE";
	private static final String GRAB_PIECE = "GRAB_PIECE";
	private static final String PREPARE_FOR_PICK = "PREPARE_FOR_PICK";
	private static final String PREPARE_FOR_PUT = "PREPARE_FOR_PUT";
	private static final String PREAPRE_FOR_INTERVENTION = "PREAPRE_FOR_INTERVENTION";
	private static final String PICK_FINISHED = "PICK_FINISHED";
	private static final String PUT_FINISHED = "PUT_FINISHED";
	private static final String INTERVENTION_FINISHED = "INTERVENTION_FINISHED";
		
	public CNCMillingMachine(String id, SocketConnection socketConnection) {
		super(id);
		this.socketConnection = socketConnection;
	}
	
	public CNCMillingMachine(String id, List<Zone> zones, SocketConnection socketConnection) {
		super(id, zones);
		this.socketConnection = socketConnection;
	}
	
	public SocketConnection getSocketConnection() {
		return socketConnection;
	}

	public void setSocketConnection(SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
	}

	@Override
	public String getStatus() throws IOException {
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			return socketConnection.synchronizedSendAndRead(STATUS);
		}
	}
	
	public static class CNCMillingMachinePutSettings extends AbstractCNCMachinePutSettings{
		public CNCMillingMachinePutSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}
	}
	public static class CNCMillingMachinePickSettings extends AbstractCNCMachinePickSettings{
		public CNCMillingMachinePickSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}
	}
	public static class CNCMillingMachineInterventionSettings extends AbstractCNCMachineInterventionSettings{
		public CNCMillingMachineInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public static class CNCMillingMachineStartCylusSettings extends AbstractCNCMachineStartCyclusSettings {
		public CNCMillingMachineStartCylusSettings(WorkArea workArea) {
			super(workArea);
		}
	}

	@Override
	public void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws IOException {
		CNCMillingMachineStartCylusSettings cncStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCylusSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(START_CYCLUS);
		}
	}

	@Override
	public void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws IOException {
		CNCMillingMachineStartCylusSettings cncStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCylusSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(PREAPARE_FOR_START_CYCLUS);
		}
	}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings) throws IOException {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(RELEASE_PIECE);
		}
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings) throws IOException {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(GRAB_PIECE);
		}
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) throws IOException {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(PREPARE_FOR_PICK);
		}
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) throws IOException {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(PREPARE_FOR_PUT);
		}
	}

	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) throws IOException {
		CNCMillingMachineInterventionSettings cncInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(PREAPRE_FOR_INTERVENTION);
		}
	}

	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings) throws IOException {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(PICK_FINISHED);
		}
	}

	@Override
	public void putFinished(AbstractDevicePutSettings putSettings) throws IOException {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(PUT_FINISHED);
		}
	}

	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) throws IOException {
		CNCMillingMachineInterventionSettings cncInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(INTERVENTION_FINISHED);
		}
	}

}
