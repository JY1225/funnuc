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
		
	private static final int PREPARE_PUT_TIMEOUT = 2*60*1000;
	private static final int PREPARE_PICK_TIMEOUT = 2*60*1000;
	private static final int CLAMP_TIMEOUT = 1*60*1000;
	private static final int UNCLAMP_TIMEOUT = 1*60*1000;
	private static final int PUT_ALLOWED_TIMEOUT = 2*60*1000;
	private static final int START_CYCLE_TIMEOUT = 2*60*1000;
	private static final int CYCLE_FINISHED_TIMEOUT = Integer.MAX_VALUE;
	
	private static final float LENGTH_CLAMP_LOCATION_R = 0;
	private static final float WIDTH_CLAMP_LOCATION_R = 90;
	
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
		alarms = CNCMachineAlarm.parseAlarms(alarmReg1, alarmReg2);
	}
	
	@Override
	public void reset() throws ResponseTimedOutException, DisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.RESET_REQUEST;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
		// this one does not need to wait, we can assume it will work
	}
	
	@Override
	public void nCReset() throws ResponseTimedOutException, DisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.NC_RESET;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
		//TODO read the OTHER register and wait till the set bit is zero, this has to be implemented in the device interface, for now: wait 2 seconds
		Thread.sleep(2500);
	}

	@Override
	public void powerOff() throws ResponseTimedOutException, DisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.POWER_OFF;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
		// normally no more commands after this, so multiple IPC requests problem can't occur
	}

	@Override
	public void indicateAllProcessed() throws ResponseTimedOutException, DisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.ALL_WP_PROCESSED;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}

	@Override
	public void indicateOperatorRequested(boolean requested) throws ResponseTimedOutException, DisconnectedException, InterruptedException {
		int command = 0;
		if (requested) {
			command = command | CNCMachineConstants.OPERATOR_REQUESTED;
		}
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}
	
	@Override
	public void clearIndications() throws ResponseTimedOutException, DisconnectedException, InterruptedException {
		int command = 0;
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}
	
	@Override
	public void prepareForProcess(ProcessFlow process)  throws ResponseTimedOutException, DisconnectedException, InterruptedException {
		nCReset();
	}

	@Override
	public void startCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws ResponseTimedOutException, DisconnectedException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(startCylusSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + startCylusSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
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
			if (!cycleFinished) {
				throw new DeviceActionException(this, EXCEPTION_CYCLE_END_TIMEOUT);
			}
		}
	}

	@Override
	public void prepareForPick(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(pickSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_PICK_WA1_RQST;
		
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);

		// check put is prepared
		boolean pickReady =  waitForStatus(CNCMachineConstants.R_PICK_WA1_READY, PREPARE_PICK_TIMEOUT);
		if (!pickReady) {
			throw new DeviceActionException(this, EXCEPTION_PREPARE_PICK_TIMEOUT);
		}
	}

	@Override
	public void prepareForPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(putSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_PUT_WA1_REQUEST;
	
		int cncTask = 0;
		cncTask = cncTask | CNCMachineConstants.WA1_CNC_PROCESS;
		
		int registers[] = {command, cncTask};
		
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		// check put is prepared
		boolean putReady =  waitForStatus(CNCMachineConstants.R_PUT_WA1_READY, PREPARE_PUT_TIMEOUT);
		if (!putReady) {
			throw new DeviceActionException(this, EXCEPTION_PREPARE_PUT_TIMEOUT);
		} 
	}

	@Override
	public void releasePiece(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(pickSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
		
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY, UNCLAMP_TIMEOUT);
		if (!clampReady) {
			throw new DeviceActionException(this, EXCEPTION_UNCLAMP_TIMEOUT);
		}
	}

	@Override
	public void grabPiece(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(putSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_CLAMP_WA1_REQUEST;
		
		int registers[] = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA1_READY, CLAMP_TIMEOUT);
		if (!clampReady) {
			throw new DeviceActionException(this, EXCEPTION_CLAMP_TIMEOUT);
		} 
	}

	@Override
	public boolean canPut(DevicePutSettings putSettings) throws InterruptedException, DeviceActionException {
		// check first workarea is selected 
		if (!getWorkAreaIds().contains(putSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		boolean canPut =  waitForStatus(CNCMachineConstants.R_PUT_WA1_ALLOWED, PUT_ALLOWED_TIMEOUT);
		if (canPut) {
			return true;
		} else {
			return false;
		}
	}
	
	// this is not taken into account on the Machine-side for now
	@Override
	public boolean canPick(DevicePickSettings pickSettings) throws AbstractCommunicationException {
		return true;
	}
	
	@Override
	public boolean canIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		return true;
	}

	@Override
	public void prepareForIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException {
		//TODO for now we don't take action here
	}
	
	// these are not taken into account by the machine for now...
	@Override public void pickFinished(DevicePickSettings pickSettings) throws AbstractCommunicationException {}
	@Override public void putFinished(DevicePutSettings putSettings) throws AbstractCommunicationException {}
	@Override public void interventionFinished(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException {}
	@Override public void prepareForStartCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException {}
	
	@Override
	public void loadDeviceSettings(DeviceSettings deviceSettings) {
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			if (!getWorkAreaIds().contains(entry.getKey().getId()))
			getWorkAreaById(entry.getKey().getId()).setActiveClamping(entry.getValue());
		}
	}

	@Override
	public DeviceSettings getDeviceSettings() {
		return new DeviceSettings(getWorkAreas());
	}

	@Override
	public boolean validateStartCyclusSettings(ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		if ((startCyclusSettings != null) && (startCyclusSettings.getWorkArea() != null) && (getWorkAreaIds().contains(startCyclusSettings.getWorkArea().getId())) &&
				(startCyclusSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validatePickSettings(DevicePickSettings pickSettings) {
		if ((pickSettings != null) && (pickSettings.getWorkArea() != null) && (getWorkAreaIds().contains(pickSettings.getWorkArea().getId())) &&
				(pickSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validatePutSettings(DevicePutSettings putSettings) {
		if ((putSettings != null) && (putSettings.getWorkArea() != null) && (getWorkAreas().contains(putSettings.getWorkArea())) &&
				(putSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		} 
		return false;
	}

	@Override
	public boolean validateInterventionSettings(DeviceInterventionSettings interventionSettings) {
		if ((interventionSettings != null) && (interventionSettings.getWorkArea() != null) && (getWorkAreas().contains(interventionSettings.getWorkArea())) &&
				(interventionSettings.getWorkArea().getActiveClamping() != null) ) {
			return true;
		}
		return false;
	}

	@Override
	public Coordinates getPickLocation(WorkArea workArea, ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(LENGTH_CLAMP_LOCATION_R);
		} else {
			c.setR(WIDTH_CLAMP_LOCATION_R);
		}
		return c;
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(LENGTH_CLAMP_LOCATION_R);
		} else {
			c.setR(WIDTH_CLAMP_LOCATION_R);
		}
		return c;
	}

	@Override
	public boolean isConnected() {
		return cncMachineCommunication.isConnected();
	}
	
	@Override
	public void disconnect() {
		cncMachineCommunication.disconnect();
	}

}
