package eu.robojob.millassist.external.device.processing.cnc.milling;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.communication.socket.SocketDisconnectedException;
import eu.robojob.millassist.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.ClampingManner.Type;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarm;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineConstants;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineMonitoringThread;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineSocketCommunication;
import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class CNCMillingMachine extends AbstractCNCMachine {
	
	private CNCMachineSocketCommunication cncMachineCommunication;
	private static final int PREPARE_PUT_TIMEOUT = 2 * 60 * 1000;
	private static final int PREPARE_PICK_TIMEOUT = 2 * 60 * 1000;

	private static final int CLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int UNCLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int PUT_ALLOWED_TIMEOUT = 2 * 60 * 1000;
	//private static final int START_CYCLE_TIMEOUT = 1 * 60 * 1000;
	private static final int SLEEP_TIME_AFTER_RESET = 500;
	
	private static final int M_CODE_LOAD = 0;
	private static final int M_CODE_UNLOAD = 1;
	
	private static Logger logger = LogManager.getLogger(CNCMillingMachine.class.getName());
	
	public CNCMillingMachine(final String name, final WayOfOperating wayOfOperating, final MCodeAdapter mCodeAdapter, final Set<Zone> zones, final SocketConnection socketConnection, final int clampingWidthR) {
		super(name, wayOfOperating, mCodeAdapter, zones, clampingWidthR);
		this.cncMachineCommunication = new CNCMachineSocketCommunication(socketConnection, this);
		CNCMachineMonitoringThread cncMachineMonitoringThread = new CNCMachineMonitoringThread(this);
		// start monitoring thread at creation of this object
		ThreadManager.submit(cncMachineMonitoringThread);
	}
	
	public CNCMillingMachine(final String name, final WayOfOperating wayOfOperating, final MCodeAdapter mCodeAdapter, final SocketConnection socketConnection, final int clampingWidthR) {
		this(name, wayOfOperating, mCodeAdapter, new HashSet<Zone>(), socketConnection, clampingWidthR);
	}
	
	public CNCMachineSocketCommunication getCNCMachineSocketCommunication() {
		return this.cncMachineCommunication;
	}
	
	@Override
	public void updateStatusAndAlarms() throws InterruptedException, SocketResponseTimedOutException, SocketDisconnectedException {
		int statusInt = (cncMachineCommunication.readRegisters(CNCMachineConstants.STATUS, 1)).get(0);
		setStatus(statusInt);
		List<Integer> alarmInts = cncMachineCommunication.readRegisters(CNCMachineConstants.ALARMS_REG1, 2);
		int alarmReg1 = alarmInts.get(0);
		int alarmReg2 = alarmInts.get(1);
		setAlarms(CNCMachineAlarm.parseCNCAlarms(alarmReg1, alarmReg2, getCncMachineTimeout()));
		if (getWayOfOperating() == WayOfOperating.M_CODES) {
			// read robot service 
			int robotServiceInputs = (cncMachineCommunication.readRegisters(CNCMachineConstants.IPC_READ_REQUEST_3, 1)).get(0);
			getMCodeAdapter().updateRobotServiceInputs(robotServiceInputs);
		}
	}
	
	@Override
	public void reset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.IPC_RESET_REQUEST;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
		setCncMachineTimeout(null);
	}
	
	@Override
	public void nCReset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.IPC_NC_RESET;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
		//TODO read the OTHER register and wait till the set bit is zero, this has to be implemented in the device interface, for now: wait 2 seconds
		Thread.sleep(SLEEP_TIME_AFTER_RESET);
	}

	@Override
	public void powerOff() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.IPC_POWER_OFF;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
		// normally no more commands after this, so multiple IPC requests problem can't occur
	}

	@Override
	public void indicateAllProcessed() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | CNCMachineConstants.IPC_ALL_WP_PROCESSED;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
	}

	@Override
	public void indicateOperatorRequested(final boolean requested) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		if (requested) {
			command = command | CNCMachineConstants.IPC_OPERATOR_REQUESTED;
		}
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
	}
	
	@Override
	public void clearIndications() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
	}
	
	@Override
	public void prepareForProcess(final ProcessFlow process)  throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		//FIXME review! potential problems with reset in double processflow execution
		clearIndications();
		int command = CNCMachineConstants.CNC_PROCESS_TYPE_WA1_TASK;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.CNC_PROCESS_TYPE, registers);
		if (getWayOfOperating() == WayOfOperating.M_CODES) {
			// wait half a second
			Thread.sleep(500);
			
			//TODO add check for more work areas
			command = 0;
			command = command | CNCMachineConstants.IPC_CYCLESTART_WA1_REQUEST;
			
			int[] registers2 = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers2);
		}
	}

	@Override
	public void startCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws SocketResponseTimedOutException, SocketDisconnectedException, DeviceActionException, InterruptedException {
		if (getWayOfOperating() == WayOfOperating.START_STOP) {
			// check a valid workarea is selected 
			if (!getWorkAreaNames().contains(startCylusSettings.getWorkArea().getName())) {
				throw new IllegalArgumentException("Unknown workarea: " + startCylusSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
			}
			int command = 0;
			command = command | CNCMachineConstants.IPC_CYCLESTART_WA1_REQUEST;
			
			int[] registers = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			// fix for jametal, comment next lines and uncomment sleep
			/*boolean cycleStartReady = waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA1, START_CYCLE_TIMEOUT);
			if (!cycleStartReady) {
				setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CYCLE_NOT_STARTED_TIMEOUT));
				waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA1);
				setCncMachineTimeout(null);
			}*/
			Thread.sleep(10000);
			// we now wait for pick requested
			waitForStatus(CNCMachineConstants.R_PICK_WA1_REQUESTED);
			nCReset();
		} else if (getWayOfOperating() == WayOfOperating.M_CODES) {
			// we sign of the m code for put
			if (getWayOfOperating() == WayOfOperating.M_CODES) {
				Set<Integer> robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_LOAD).getRobotServiceOutputsUsed();
				int command = 0;
				if (robotServiceOutputs.contains(0)) {
					logger.info("AFMELDEN M-CODE 0");
					command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
				}
				int[] registers = {command};
				cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
			}
			waitForMCode(M_CODE_UNLOAD);
		} else {
			throw new IllegalStateException("Unknown way of operating: " + getWayOfOperating());
		}
	}

	@Override
	public void prepareForPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}

		// if way of operation is m codes, await unloading m code!
		if (getWayOfOperating() == WayOfOperating.M_CODES) {
			waitForMCode(M_CODE_UNLOAD);
		}
		
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_PICK_WA1_RQST;
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);

		// check pick is prepared
		boolean pickReady =  waitForStatus(CNCMachineConstants.R_PICK_WA1_READY, PREPARE_PICK_TIMEOUT);
		if (!pickReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
			waitForStatus(CNCMachineConstants.R_PICK_WA1_READY);
			setCncMachineTimeout(null);
		}
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		
		// if way of operation is m codes, await unloading m code!
		if (getWayOfOperating() == WayOfOperating.M_CODES) {
			waitForMCode(M_CODE_LOAD);
		}
		
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_PUT_WA1_REQUEST;
	
		int[] registers = {command};
		
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		// check put is prepared
		boolean putReady =  waitForStatus(CNCMachineConstants.R_PUT_WA1_READY, PREPARE_PUT_TIMEOUT);
		if (!putReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
			waitForStatus(CNCMachineConstants.R_PUT_WA1_READY);
			setCncMachineTimeout(null);
		} 
	}

	@Override
	public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
		
		if (pickSettings.getWorkArea().getActiveClamping().getType() == Clamping.Type.DOUBLE) {
			command = command | CNCMachineConstants.IPC_UNCLAMP_WA2_RQST;
		}
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY, UNCLAMP_TIMEOUT);
		if (!clampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
			waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY);
			setCncMachineTimeout(null);
		}
	}

	@Override
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		int command = 0;
		//TODO for now WA1 is always used
		command = command | CNCMachineConstants.IPC_CLAMP_WA1_REQUEST;
		
		if (putSettings.getWorkArea().getActiveClamping().getType() == Clamping.Type.DOUBLE) {
			command = command | CNCMachineConstants.IPC_CLAMP_WA2_REQUEST;
		}
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA1_READY, CLAMP_TIMEOUT);
		if (!clampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
			waitForStatus(CNCMachineConstants.R_CLAMP_WA1_READY);
			setCncMachineTimeout(null);
		} 
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws InterruptedException, DeviceActionException {
		// check first workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
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
	public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, InterruptedException {
		indicateOperatorRequested(true);
	}
	
	@Override 
	public void pickFinished(final DevicePickSettings pickSettings) throws AbstractCommunicationException, InterruptedException {
		if (getWayOfOperating() == WayOfOperating.M_CODES) {
			if (pickSettings.getStep().getProcessFlow().getFinishedAmount() == pickSettings.getStep().getProcessFlow().getTotalAmount() - 1) {
				// last work piece: send reset in stead of finishing m code
				nCReset();
			} else {
				Set<Integer> robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_UNLOAD).getRobotServiceOutputsUsed();
				int command = 0;
				if (robotServiceOutputs.contains(0)) {
					logger.info("AFMELDEN M-CODE 0");
					command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
				}
				int[] registers = {command};
				cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
			}
		}	
	}
	
	@Override 
	public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, InterruptedException {}
	
	// these are not taken into account by the machine for now...
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException { }
	@Override public void prepareForStartCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException { }

	@Override
	public Coordinates getLocationOrientation(final WorkArea workArea) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		return c;
	}
	
	@Override
	public Coordinates getPickLocation(final WorkArea workArea, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(c.getR());
		} else {
			c.setR(c.getR() + getClampingWidthR());
		}
		return c;
	}

	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(c.getR());
		} else {
			c.setR(c.getR() +  getClampingWidthR());
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
	
	@Override
	public String toString() {
		return "CNCMillingMachine: " + getName();
	}

}
