package eu.robojob.irscw.external.device.processing.cnc;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.ClampingManner.Type;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
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
	
	private static final String EXCEPTION_CYCLE_NOT_STARTED = "CNCMillingMachine.cycleNotStarted";
	private static final String EXCEPTION_CYCLE_END_TIMEOUT = "CNCMillingMachine.cycleEndTimeout";
	private static final String EXCEPTION_PREPARE_PICK_TIMEOUT = "CNCMillingMachine.preparePickTimeout";
	private static final String EXCEPTION_PREPARE_PUT_TIMEOUT = "CNCMillingMachine.preparePutTimeout";
	private static final String EXCEPTION_UNCLAMP_TIMEOUT = "CNCMillingMachine.unclampTimeout";
	private static final String EXCEPTION_CLAMP_TIMEOUT = "CNCMillingMachine.clampTimeout";
			
	public CNCMillingMachine(String id, SocketConnection socketConnection) {
		super(id);
		this.cncMachineCommunication = new CNCMachineCommunication(socketConnection, this);
	}
	
	public CNCMillingMachine(String id, List<Zone> zones, SocketConnection socketConnection) {
		super(id, zones);
		this.cncMachineCommunication = new CNCMachineCommunication(socketConnection, this);
	}
	
	@Override
	public void updateStatusAndAlarms() throws InterruptedException, ResponseTimedOutException, DisconnectedException {
		int statusInt = (cncMachineCommunication.readRegisters(CNCMachineConstants.STATUS, 1)).get(0);
		this.currentStatus = statusInt;
		
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
	public void nCReset() throws AbstractCommunicationException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.NC_RESET;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}

	@Override
	public void powerOff() throws AbstractCommunicationException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.POWER_OFF;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}

	/*
	 * This method will also take care of turning on the blue lamp
	 */
	@Override
	public void indicateAllProcessed() throws AbstractCommunicationException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.ALL_WP_PROCESSED;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}

	@Override
	public void indicateOperatorRequested(boolean requested) throws AbstractCommunicationException, InterruptedException {
		int command = 0;
		if (requested) {
			command = command | CNCMachineConstants.OPERATOR_REQUESTED;
		}
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}
	
	@Override
	public void clearIndications() throws AbstractCommunicationException, InterruptedException {
		int command = 0;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}
	
	@Override
	public void prepareForProcess(ProcessFlow process)  throws AbstractCommunicationException, InterruptedException {
		nCReset();
	}

	@Override
	public void startCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check first workarea is selected 
		if (startCylusSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			int command = 0;
			command = command | CNCMachineConstants.IPC_CYCLESTART_WA1_REQUEST;
			
			int registers[] = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			
			boolean cycleStartReady = waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA1, START_CYCLE_TIMEOUT);
			if (!cycleStartReady) {
				throw new DeviceActionException(this, EXCEPTION_CYCLE_NOT_STARTED);
			} else {
				// we now wait for pick requested
				boolean cycleFinished =  waitForStatus(CNCMachineConstants.R_PICK_WA1_REQUESTED, CYCLE_FINISHED_TIMEOUT);
				if (cycleFinished) {
					return;
				} else {
					throw new DeviceActionException(this, EXCEPTION_CYCLE_END_TIMEOUT);
				}
			}
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + startCylusSettings.getWorkArea().getId());
		}
	}

	// this is not taken into account for now
	@Override
	public void prepareForStartCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException {
	}

	@Override
	public void prepareForPick(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
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
				throw new DeviceActionException(this, EXCEPTION_PREPARE_PICK_TIMEOUT);
			} else {
			}
			
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + pickSettings.getWorkArea().getId());
		}
	}

	@Override
	public void prepareForPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
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
				throw new DeviceActionException(this, EXCEPTION_PREPARE_PUT_TIMEOUT);
			} 
					
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + putSettings.getWorkArea().getId());
		}
	}

	@Override
	public void releasePiece(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check first workarea is selected 
		if (pickSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			int command = 0;
			command = command | CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
			
			int registers[] = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			
			boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY, UNCLAMP_TIMEOUT);
			if (!clampReady) {
				throw new DeviceActionException(this, EXCEPTION_UNCLAMP_TIMEOUT);
			} else {
			}
			
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + pickSettings.getWorkArea().getId());
		}
	}

	@Override
	public void grabPiece(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check first workarea is selected 
		if (putSettings.getWorkArea().getId().equals(getWorkAreas().get(0).getId())) {
			int command = 0;
			command = command | CNCMachineConstants.IPC_CLAMP_WA1_REQUEST;
			
			int registers[] = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			
			boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA1_READY, CLAMP_TIMEOUT);
			if (!clampReady) {
				throw new DeviceActionException(this, EXCEPTION_CLAMP_TIMEOUT);
			} else {
			}
			
		} else {
			throw new IllegalArgumentException("Wrong workarea, should be: " + getWorkAreas().get(0).getId() + " but got: " + putSettings.getWorkArea().getId());
		}
	}

	@Override
	public boolean canPut(DevicePutSettings putSettings) throws InterruptedException, DeviceActionException {
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
	public boolean canPick(DevicePickSettings pickSettings) throws AbstractCommunicationException {
		return true;
	}
	
	@Override
	public boolean canIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		return false;
	}

	// be aware! this will not be easy! of toch: prepare for pick!!
	@Override
	public void prepareForIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException {
	}
	
	// these are not taken into account by the machine for now...
	@Override
	public void pickFinished(DevicePickSettings pickSettings) throws AbstractCommunicationException {
	}
	@Override
	public void putFinished(DevicePutSettings putSettings) throws AbstractCommunicationException {
	}
	@Override
	public void interventionFinished(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException {
	}

	@Override
	public void loadDeviceSettings(DeviceSettings deviceSettings) {
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setActiveClamping(entry.getValue());
		}
	}

	@Override
	public DeviceSettings getDeviceSettings() {
		return new DeviceSettings(getWorkAreas());
	}

	@Override
	public boolean validateStartCyclusSettings(ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		if ((startCyclusSettings != null) && (startCyclusSettings.getWorkArea() != null) && (getWorkAreas().contains(startCyclusSettings.getWorkArea())) &&
				(startCyclusSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validatePickSettings(DevicePickSettings pickSettings) {
		if ((pickSettings != null) && (pickSettings.getWorkArea() != null) && (getWorkAreas().contains(pickSettings.getWorkArea())) &&
				(pickSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validatePutSettings(DevicePutSettings putSettings) {
		if ((putSettings != null) && (putSettings.getWorkArea() != null) && (getWorkAreas().contains(putSettings.getWorkArea())) &&
				(putSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validateInterventionSettings(DeviceInterventionSettings interventionSettings) {
		if ((interventionSettings != null) && (interventionSettings.getWorkArea() != null) && (getWorkAreas().contains(interventionSettings.getWorkArea())) &&
				(interventionSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Coordinates getPickLocation(WorkArea workArea, ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(0);
		} else {
			c.setR(90);
		}
		return c;
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingManner clampType) {
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
	public void reset() throws AbstractCommunicationException, InterruptedException {
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
