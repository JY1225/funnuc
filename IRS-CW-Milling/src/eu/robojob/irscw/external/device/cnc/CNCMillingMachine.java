package eu.robojob.irscw.external.device.cnc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.ClampingType;
import eu.robojob.irscw.external.device.ClampingType.Type;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class CNCMillingMachine extends AbstractCNCMachine {

	private CNCMachineCommunication cncMachineCommunication;
		
	private static final int PREPARE_PUT_TIMEOUT = 5*60*1000;
	private static final int PREPARE_PICK_TIMEOUT = 5*60*1000;
	private static final int CLAMP_TIMEOUT = 5*60*1000;
	private static final int UNCLAMP_TIMEOUT = 5*60*1000;
	private static final int PUT_ALLOWED_TIMEOUT = 5*60*1000;
	private static final int START_CYCLE_TIMEOUT = 5*60*1000;
	private static final int CYCLE_FINISHED_TIMEOUT = Integer.MAX_VALUE;
			
	public CNCMillingMachine(String id, SocketConnection socketConnection) {
		super(id);
		this.cncMachineCommunication = new CNCMachineCommunication(socketConnection, this);
	}
	
	public CNCMillingMachine(String id, List<Zone> zones, SocketConnection socketConnection) {
		super(id, zones);
		this.cncMachineCommunication = new CNCMachineCommunication(socketConnection, this);
	}
	
	@Override
	public void updateStatusAndAlarms() throws CommunicationException {
		int statusInt = (cncMachineCommunication.readRegisters(CNCMachineConstants.STATUS, 1)).get(0);
		this.status = new CNCMachineStatus(statusInt);
		
		this.alarms = new HashSet<CNCMachineAlarm>();
		List<Integer> alarmInts = cncMachineCommunication.readRegisters(CNCMachineConstants.ALARMS_REG1, 2);
		int alarmReg1 = alarmInts.get(0);
		int alarmReg2 = alarmInts.get(1);
		if ((alarmReg1 & CNCMachineConstants.ALR_MACHINE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.MACHINE));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_FEED_HOLD)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.FEED_HOLD));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_MAIN_PRESSURE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.MAIN_PRESSURE));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_OIL_TEMP_HIGH)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.OIL_TEMP_HIGH));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_OIL_LEVEL_LOW)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.OIL_LEVEL_LOW));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR1_NOT_OPEN)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR1_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR2_NOT_OPEN)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR2_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR1_NOT_CLOSE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR1_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR2_NOT_CLOSE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR2_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP1_NOT_OPEN)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP1_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP2_NOT_OPEN)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP2_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP1_NOT_CLOSE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP1_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP2_NOT_CLOSE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP2_NOT_CLOSED));
		}
		
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_PUT)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_PUT));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_PUT)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_PUT));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_PICK)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_PICK));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_PICK)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_PICK));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_CYST)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_CYCLUS_START));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_CYST)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_CYCLUS_START));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_CLAMP)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_CLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_CLAMP)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_CLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_UNCLAMP)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_UNCLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_UNCLAMP)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_UNCLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_MULTIPLE_IPC_RQST)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.MULTIPLE_IPC_REQUESTS));
		}
	}
	
	@Override
	public void nCReset() throws CommunicationException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.NC_RESET;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
		/*command = CNCMachineConstants.NC_RESET;
		int registers2[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers2);*/
		// TODO: no way of knowing this succeeded? 
	}

	@Override
	public void powerOff() throws CommunicationException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.POWER_OFF;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}

	/*
	 * This method will also take care of turning on the blue lamp
	 */
	@Override
	public void indicateAllProcessed() throws CommunicationException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.ALL_WP_PROCESSED;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}

	@Override
	public void operatorRequested(boolean requested) throws CommunicationException, InterruptedException {
		int command = 0;
		if (requested) {
			command = command | CNCMachineConstants.OPERATOR_REQUESTED;
		}
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}
	
	@Override
	public void stopIndications() throws CommunicationException, InterruptedException {
		int command = 0;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}
	
	@Override
	public void prepareForProcess(ProcessFlow process)  throws CommunicationException, InterruptedException {
		nCReset();
	}

	@Override
	public void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// check first workarea is selected 
		if (startCylusSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			int command = 0;
			command = command | CNCMachineConstants.IPC_CYCLESTART_WA1_REQUEST;
			
			int registers[] = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			
			boolean cycleStartReady = waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA1, START_CYCLE_TIMEOUT);
			if (!cycleStartReady) {
				throw new DeviceActionException("Machine cycle start was not given");
			} else {
				// we now wait for pick requested
				boolean cycleFinished =  waitForStatus(CNCMachineConstants.R_PICK_WA1_REQUESTED, CYCLE_FINISHED_TIMEOUT);
				if (cycleFinished) {
					return;
				} else {
					throw new DeviceActionException("Timeout when waiting for machine to start cyclus");
				}
			}
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + startCylusSettings.getWorkArea().getId());
		}
	}

	// this is not taken into account for now
	@Override
	public void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws CommunicationException, DeviceActionException {
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// check first workarea is selected 
		if (pickSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			// first WA
			int command = 0;
			command = command | CNCMachineConstants.IPC_PICK_WA1_RQST;
			
			int registers[] = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);

			// check put is prepared
			boolean pickReady =  waitForStatus(CNCMachineConstants.R_PICK_WA1_READY, PREPARE_PICK_TIMEOUT);
			if (!pickReady) {
				throw new DeviceActionException("Machine could not prepare for pick");
			} else {
			}
			
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + pickSettings.getWorkArea().getId());
		}
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// check first workarea is selected 
		if (putSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			// first WA
			int command = 0;
			command = command | CNCMachineConstants.IPC_PUT_WA1_REQUEST;
			
			// with this kind of machines, the work-area stays the same, so the WA (clamp) of the finished product is the same as that of the raw
			// TODO not sure if this is necessary here!
			int cncTask = 0;
			cncTask = cncTask | CNCMachineConstants.WA1_CNC_PROCESS;
			
			int registers[] = {command, cncTask};
			
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			
			// check put is prepared
			boolean putReady =  waitForStatus(CNCMachineConstants.R_PUT_WA1_READY, PREPARE_PUT_TIMEOUT);
			if (!putReady) {
				throw new DeviceActionException("Machine could not prepare for put");
			} 
					
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + putSettings.getWorkArea().getId());
		}
	}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// check first workarea is selected 
		if (pickSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			int command = 0;
			command = command | CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
			
			int registers[] = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			
			boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY, UNCLAMP_TIMEOUT);
			if (!clampReady) {
				throw new DeviceActionException("Could not open clamp");
			} else {
			}
			
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + pickSettings.getWorkArea().getId());
		}
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// check first workarea is selected 
		if (putSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			int command = 0;
			command = command | CNCMachineConstants.IPC_CLAMP_WA1_REQUEST;
			
			int registers[] = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			
			boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA1_READY, CLAMP_TIMEOUT);
			if (!clampReady) {
				throw new DeviceActionException("Could not close clamp");
			} else {
			}
			
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + putSettings.getWorkArea().getId());
		}
	}

	@Override
	public boolean canPut(AbstractDevicePutSettings putSettings) throws CommunicationException, InterruptedException {
		// check first workarea is selected 
		if (putSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			boolean canPut =  waitForStatus(CNCMachineConstants.R_PUT_WA1_ALLOWED, PUT_ALLOWED_TIMEOUT);
			if (canPut) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + putSettings.getWorkArea().getId());
		}
	}
	
	// this is not taken into account on the Machine-side for now
	@Override
	public boolean canPick(AbstractDevicePickSettings pickSettings) throws CommunicationException {
		return true;
	}

	// be aware! this will not be easy! of toch: prepare for pick!!
	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) throws CommunicationException {
		// TODO Auto-generated method stub
		
	}
	
	// these are not taken into account by the machine for now...
	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings) throws CommunicationException {
	}
	@Override
	public void putFinished(AbstractDevicePutSettings putSettings) throws CommunicationException {
	}
	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) throws CommunicationException {
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
	public Coordinates getPickLocation(WorkArea workArea, ClampingType clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(0);
		} else {
			c.setR(90);
		}
		return c;
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingType clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(0);
		} else {
			c.setR(90);
		}
		return c;
	}

	@Override
	public boolean isConnected() {
		if (cncMachineCommunication != null) {
			return cncMachineCommunication.isConnected();
		}
		return false;
	}
	
	@Override
	public void disconnect() {
		cncMachineCommunication.disconnect();
	}

	@Override
	public void reset() throws CommunicationException, InterruptedException {
		/*int command = 0;
		command = command | CNCMachineConstants.RESET_REQUEST;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);*/
		/*command = CNCMachineConstants.NC_RESET;
		int registers2[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers2);*/
		// TODO: no way of knowing this succeeded? 
	}

}
