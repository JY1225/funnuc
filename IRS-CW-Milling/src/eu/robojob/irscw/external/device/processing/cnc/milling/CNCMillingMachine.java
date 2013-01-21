package eu.robojob.irscw.external.device.processing.cnc.milling;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.communication.socket.SocketConnection;
import eu.robojob.irscw.external.communication.socket.SocketDisconnectedException;
import eu.robojob.irscw.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.ClampingManner.Type;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineAlarm;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineConstants;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineMonitoringThread;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineSocketCommunication;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class CNCMillingMachine extends AbstractCNCMachine {

	private CNCMachineSocketCommunication cncMachineCommunication;
		
	private static final int PREPARE_PUT_TIMEOUT = 2 * 60 * 1000;
	private static final int PREPARE_PICK_TIMEOUT = 2 * 60 * 1000;
	private static final int CLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int UNCLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int PUT_ALLOWED_TIMEOUT = 2 * 60 * 1000;
	private static final int START_CYCLE_TIMEOUT = 2 * 60 * 1000;
	private static final int CYCLE_FINISHED_TIMEOUT = Integer.MAX_VALUE;
	private static final int SLEEP_TIME_AFTER_RESET = 2500;
	
	private static final float LENGTH_CLAMP_LOCATION_R = 0;
	private static final float WIDTH_CLAMP_LOCATION_R = 90;
	
	private static final String EXCEPTION_CYCLE_NOT_STARTED = "CNCMillingMachine.cycleNotStarted";
	private static final String EXCEPTION_CYCLE_END_TIMEOUT = "CNCMillingMachine.cycleEndTimeout";
	private static final String EXCEPTION_PREPARE_PICK_TIMEOUT = "CNCMillingMachine.preparePickTimeout";
	private static final String EXCEPTION_PREPARE_PUT_TIMEOUT = "CNCMillingMachine.preparePutTimeout";
	private static final String EXCEPTION_UNCLAMP_TIMEOUT = "CNCMillingMachine.unclampTimeout";
	private static final String EXCEPTION_CLAMP_TIMEOUT = "CNCMillingMachine.clampTimeout";
			
	public CNCMillingMachine(final String id, final List<Zone> zones, final SocketConnection socketConnection) {
		super(id, zones);
		this.cncMachineCommunication = new CNCMachineSocketCommunication(socketConnection, this);
		CNCMachineMonitoringThread cncMachineMonitoringThread = new CNCMachineMonitoringThread(this);
		// start monitoring thread at creation of this object
		ThreadManager.submit(cncMachineMonitoringThread);
	}
	
	public CNCMillingMachine(final String id, final SocketConnection socketConnection) {
		this(id, new ArrayList<Zone>(), socketConnection);
		
	}
	
	@Override
	public void updateStatusAndAlarms() throws InterruptedException, SocketResponseTimedOutException, SocketDisconnectedException {
		int statusInt = (cncMachineCommunication.readRegisters(CNCMachineConstants.STATUS, 1)).get(0);
		setStatus(statusInt);
		List<Integer> alarmInts = cncMachineCommunication.readRegisters(CNCMachineConstants.ALARMS_REG1, 2);
		int alarmReg1 = alarmInts.get(0);
		int alarmReg2 = alarmInts.get(1);
		setAlarms(CNCMachineAlarm.parseCNCAlarms(alarmReg1, alarmReg2));
	}
	
	@Override
	public void reset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.RESET_REQUEST;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
		// this one does not need to wait, we can assume it will work
	}
	
	@Override
	public void nCReset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.NC_RESET;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
		//TODO read the OTHER register and wait till the set bit is zero, this has to be implemented in the device interface, for now: wait 2 seconds
		Thread.sleep(SLEEP_TIME_AFTER_RESET);
	}

	@Override
	public void powerOff() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.POWER_OFF;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
		// normally no more commands after this, so multiple IPC requests problem can't occur
	}

	@Override
	public void indicateAllProcessed() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.ALL_WP_PROCESSED;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}

	@Override
	public void indicateOperatorRequested(final boolean requested) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		if (requested) {
			command = command | CNCMachineConstants.OPERATOR_REQUESTED;
		}
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}
	
	@Override
	public void clearIndications() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.OTHER, registers);
	}
	
	@Override
	public void prepareForProcess(final ProcessFlow process)  throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		//FIXME review! potential problems with reset in double processflow execution
		clearIndications();
	}

	@Override
	public void startCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws SocketResponseTimedOutException, SocketDisconnectedException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(startCylusSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + startCylusSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		command = command | CNCMachineConstants.IPC_CYCLESTART_WA1_REQUEST;
		
		int[] registers = {command};
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
			nCReset();
		}
	}

	@Override
	public void prepareForPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(pickSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_PICK_WA1_RQST;
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);

		// check put is prepared
		boolean pickReady =  waitForStatus(CNCMachineConstants.R_PICK_WA1_READY, PREPARE_PICK_TIMEOUT);
		if (!pickReady) {
			throw new DeviceActionException(this, EXCEPTION_PREPARE_PICK_TIMEOUT);
		}
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(putSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_PUT_WA1_REQUEST;
	
		int cncTask = 0;
		cncTask = cncTask | CNCMachineConstants.WA1_CNC_PROCESS;
		
		int[] registers = {command, cncTask};
		
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		// check put is prepared
		boolean putReady =  waitForStatus(CNCMachineConstants.R_PUT_WA1_READY, PREPARE_PUT_TIMEOUT);
		if (!putReady) {
			throw new DeviceActionException(this, EXCEPTION_PREPARE_PUT_TIMEOUT);
		} 
	}

	@Override
	public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(pickSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY, UNCLAMP_TIMEOUT);
		if (!clampReady) {
			throw new DeviceActionException(this, EXCEPTION_UNCLAMP_TIMEOUT);
		}
	}

	@Override
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaIds().contains(putSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_CLAMP_WA1_REQUEST;
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA1_READY, CLAMP_TIMEOUT);
		if (!clampReady) {
			throw new DeviceActionException(this, EXCEPTION_CLAMP_TIMEOUT);
		} 
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws InterruptedException, DeviceActionException {
		// check first workarea is selected 
		if (!getWorkAreaIds().contains(putSettings.getWorkArea().getId())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getId() + " valid workareas are: " + getWorkAreaIds());
		}
		boolean canPut =  waitForStatus(CNCMachineConstants.R_PUT_WA1_ALLOWED, PUT_ALLOWED_TIMEOUT);
		if (canPut) {
			return true;
		}
		return false;
	}
	
	// this is not taken into account on the Machine-side for now
	@Override
	public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException {
		return true;
	}
	
	@Override
	public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		return true;
	}

	@Override
	public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException {
		//TODO for now we don't take action here
	}
	
	// these are not taken into account by the machine for now...
	@Override public void pickFinished(final DevicePickSettings pickSettings) throws AbstractCommunicationException { }
	@Override public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException { }
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException { }
	@Override public void prepareForStartCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException { }

	@Override
	public Coordinates getPickLocation(final WorkArea workArea, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(LENGTH_CLAMP_LOCATION_R);
		} else {
			c.setR(WIDTH_CLAMP_LOCATION_R);
		}
		return c;
	}

	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
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
