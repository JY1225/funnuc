package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.ExternalCommunication;
import eu.robojob.irscw.external.communication.ExternalCommunicationThread;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class CNCMillingMachine extends AbstractCNCMachine {

	private ExternalCommunication externalCommunication;
	
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
	
	private static final int READ_TIMEOUT = 10000;
		
	public CNCMillingMachine(String id, SocketConnection socketConnection) {
		super(id);
		ExternalCommunicationThread externalCommunicationThread = new ExternalCommunicationThread(socketConnection);
		ThreadManager.getInstance().submit(externalCommunicationThread);
		this.externalCommunication = new ExternalCommunication(externalCommunicationThread);
	}
	
	public CNCMillingMachine(String id, List<Zone> zones, SocketConnection socketConnection) {
		super(id, zones);
		ExternalCommunicationThread externalCommunicationThread = new ExternalCommunicationThread(socketConnection);
		ThreadManager.getInstance().submit(externalCommunicationThread);
		this.externalCommunication = new ExternalCommunication(externalCommunicationThread);
	}
	
	@Override
	public String getStatus() throws IOException {
		if (!isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			//TODO
			return null;
		}
	}

	@Override
	public void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws IOException {
		CNCMillingMachineStartCylusSettings cncStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCylusSettings;
		String response = externalCommunication.writeAndRead("START CYCLUS IN WA: " + cncStartCyclusSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws IOException {
		CNCMillingMachineStartCylusSettings cncStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCylusSettings;
		String response = externalCommunication.writeAndRead("PREPARE FOR START CYCLUS IN WA: " + cncStartCyclusSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings) throws IOException {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		String response = externalCommunication.writeAndRead("RELEASE PIECE IN WA: " + cncPickSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings) throws IOException {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		String response = externalCommunication.writeAndRead("GRAB PIECE IN WA: " + cncPutSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) throws IOException {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		String response = externalCommunication.writeAndRead("PREPARE FOR PICK IN WA: " + cncPickSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) throws IOException {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		String response = externalCommunication.writeAndRead("PREPARE FOR PUT IN WA: " + cncPutSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) throws IOException {
		CNCMillingMachineInterventionSettings cncInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		String response = externalCommunication.writeAndRead("PREPARE FOR INTERVENTION IN WA: " + cncInterventionSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings) throws IOException {
		CNCMillingMachinePickSettings cncPickSettings = (CNCMillingMachinePickSettings) pickSettings;
		String response = externalCommunication.writeAndRead("PICK HAS FINISHED IN WA: " + cncPickSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void putFinished(AbstractDevicePutSettings putSettings) throws IOException {
		CNCMillingMachinePutSettings cncPutSettings = (CNCMillingMachinePutSettings) putSettings;
		String response = externalCommunication.writeAndRead("PUT HAS FINISHED IN WA: " + cncPutSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}

	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) throws IOException {
		CNCMillingMachineInterventionSettings cncInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		String response = externalCommunication.writeAndRead("INTERVENTION FINISHED IN WA: " + cncInterventionSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.info(response);
	}
	
	public static class CNCMillingMachinePutSettings extends AbstractCNCMachinePutSettings{
		public CNCMillingMachinePutSettings(WorkArea workArea) {
			super(workArea);
		}

		@Override
		public boolean isPutPositionFixed() {
			return false;
		}
	}
	public static class CNCMillingMachinePickSettings extends AbstractCNCMachinePickSettings{
		public CNCMillingMachinePickSettings(WorkArea workArea) {
			super(workArea);
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
	public class CNCMillingMachineSettings extends AbstractDeviceSettings {

		private Map<WorkArea, Clamping> clampings;
		
		public CNCMillingMachineSettings() {
			clampings = new HashMap<WorkArea, Clamping>();
		}
		
		public CNCMillingMachineSettings(List<WorkArea> workAreas) {
			this();
			for (WorkArea workArea : workAreas) {
				clampings.put(workArea, workArea.getActiveClamping());
			}
		}
		
		public void setClamping(WorkArea workArea, Clamping clamping) {
			clampings.put(workArea, clamping);
		}

		public Map<WorkArea, Clamping> getClampings() {
			return clampings;
		}

		public void setClampings(Map<WorkArea, Clamping> clampings) {
			this.clampings = clampings;
		}
		
		public Clamping getClamping(WorkArea workArea) {
			return clampings.get(workArea);
		}
	
	}


	@Override
	public void loadDeviceSettings(AbstractDeviceSettings deviceSettings) {
		if (deviceSettings instanceof CNCMillingMachineSettings) {
			CNCMillingMachineSettings settings = (CNCMillingMachineSettings) deviceSettings;
			for (Entry<WorkArea, Clamping> entry : settings.getClampings().entrySet()) {
				entry.getKey().setActiveClamping(entry.getValue());
			}
		} else {
			throw new IllegalArgumentException("Unknown device settings");
		}
	}

	@Override
	public AbstractDeviceSettings getDeviceSettings() {
		return new CNCMillingMachineSettings(getWorkAreas());
	}

	@Override
	public boolean validateStartCyclusSettings(AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		CNCMillingMachineStartCylusSettings cncMillingStartCyclusSettings = (CNCMillingMachineStartCylusSettings) startCyclusSettings;
		if ((cncMillingStartCyclusSettings != null) && (cncMillingStartCyclusSettings.getWorkArea() != null) && (getWorkAreas().contains(cncMillingStartCyclusSettings.getWorkArea())) &&
				(cncMillingStartCyclusSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validatePickSettings(AbstractDevicePickSettings pickSettings) {
		CNCMillingMachinePickSettings cncMillingMachinePickSettings = (CNCMillingMachinePickSettings) pickSettings;
		if ((cncMillingMachinePickSettings != null) && (cncMillingMachinePickSettings.getWorkArea() != null) && (getWorkAreas().contains(cncMillingMachinePickSettings.getWorkArea())) &&
				(cncMillingMachinePickSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validatePutSettings(AbstractDevicePutSettings putSettings) {
		CNCMillingMachinePutSettings cncMillingMachinePutSettings = (CNCMillingMachinePutSettings) putSettings;
		if ((cncMillingMachinePutSettings != null) && (cncMillingMachinePutSettings.getWorkArea() != null) && (getWorkAreas().contains(cncMillingMachinePutSettings.getWorkArea())) &&
				(cncMillingMachinePutSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validateInterventionSettings(AbstractDeviceInterventionSettings interventionSettings) {
		CNCMillingMachineInterventionSettings cncMillingMachineInterventionSettings = (CNCMillingMachineInterventionSettings) interventionSettings;
		if ((cncMillingMachineInterventionSettings != null) && (cncMillingMachineInterventionSettings.getWorkArea() != null) && (getWorkAreas().contains(cncMillingMachineInterventionSettings.getWorkArea())) &&
				(cncMillingMachineInterventionSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePickSettings pickSettings) {
		return new CNCMillingMachineInterventionSettings(pickSettings.getWorkArea());
	}

	@Override
	public AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePutSettings putSettings) {
		return new CNCMillingMachineInterventionSettings(putSettings.getWorkArea());
	}

	@Override
	public Coordinates getPickLocation(WorkArea workArea) {
		return workArea.getActiveClamping().getRelativePosition();
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea,
			WorkPieceDimensions workPieceDimensions) {
		Coordinates c = workArea.getActiveClamping().getRelativePosition();
		c.offset(new Coordinates(workPieceDimensions.getLength(), workPieceDimensions.getWidth(), workPieceDimensions.getHeight(), 0, 0, 0));
		return c;
	}

	@Override
	public boolean isConnected() {
		return externalCommunication.isConnected();
	}

}
