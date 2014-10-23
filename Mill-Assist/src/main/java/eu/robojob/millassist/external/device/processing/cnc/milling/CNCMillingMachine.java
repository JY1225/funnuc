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
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.ClampingManner.Type;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.EFixtureType;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarm;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineConstants;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineMonitoringThread;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineSocketCommunication;
import eu.robojob.millassist.external.device.processing.cnc.EWayOfOperating;
import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessingStep;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

//TODO - deze klasse opsplitsen op basis van wayOfOperating
public class CNCMillingMachine extends AbstractCNCMachine {
	
	private CNCMachineSocketCommunication cncMachineCommunication;
	private static final int PREPARE_PUT_TIMEOUT = 2 * 60 * 1000;
	private static final int PREPARE_PICK_TIMEOUT = 2 * 60 * 1000;

	private static final int CLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int UNCLAMP_TIMEOUT = 1 * 60 * 1000;
	//private static final int PUT_ALLOWED_TIMEOUT = 2 * 60 * 1000;
	private static final int START_CYCLE_TIMEOUT = 3 * 60 * 1000;
	private static final int SLEEP_TIME_AFTER_RESET = 500;
	
	public static final int M_CODE_LOAD = 0;
	public static final int M_CODE_UNLOAD = 1;
	public static final int M_CODE_LOAD_REVERSAL = 2;
	public static final int M_CODE_UNLOAD_REVERSAL = 3;
	
	private static Logger logger = LogManager.getLogger(CNCMillingMachine.class.getName());
	
	public CNCMillingMachine(final String name, final EWayOfOperating wayOfOperating, final MCodeAdapter mCodeAdapter, final Set<Zone> zones, final SocketConnection socketConnection, final int clampingWidthR) {
		super(name, wayOfOperating, mCodeAdapter, zones, clampingWidthR);
		this.cncMachineCommunication = new CNCMachineSocketCommunication(socketConnection, this);
		CNCMachineMonitoringThread cncMachineMonitoringThread = new CNCMachineMonitoringThread(this);
		// start monitoring thread at creation of this object
		ThreadManager.submit(cncMachineMonitoringThread);
	}
	
	public CNCMillingMachine(final String name, final EWayOfOperating wayOfOperating, final MCodeAdapter mCodeAdapter, final SocketConnection socketConnection, final int clampingWidthR) {
		this(name, wayOfOperating, mCodeAdapter, new HashSet<Zone>(), socketConnection, clampingWidthR);
	}

	@Override
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
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
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
		// check work area
		
		int command = 0;
		int ufNr = 0;
		for (AbstractProcessStep step : process.getProcessSteps()) {
			if ((step instanceof ProcessingStep) && ((ProcessingStep) step).getDevice().equals(this)) {
				ufNr = ((ProcessingDeviceStartCyclusSettings) ((ProcessingStep) step).getDeviceSettings()).getWorkArea().getUserFrame().getNumber();
			}
		}
		if (ufNr == 3) {
			command = CNCMachineConstants.CNC_PROCESS_TYPE_WA1_TASK;
		} else if (ufNr == 4) {
			command = CNCMachineConstants.CNC_PROCESS_TYPE_WA2_TASK;
		} else {
			throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
		}
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.CNC_PROCESS_TYPE, registers);
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			// wait half a second
			Thread.sleep(500);
			
			command = 0;
			if (ufNr == 3) {
				command = command | CNCMachineConstants.IPC_CYCLESTART_WA1_REQUEST;
			} else if (ufNr == 4) {
				command = command | CNCMachineConstants.IPC_CYCLESTART_WA2_REQUEST;
			} else {
				throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
			}
			
			int[] registers2 = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers2);
		}
	}

	@Override
	public void startCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws SocketResponseTimedOutException, SocketDisconnectedException, DeviceActionException, InterruptedException {
		int ufNr = startCylusSettings.getWorkArea().getUserFrame().getNumber();
		if (getWayOfOperating() == EWayOfOperating.START_STOP) {
			// check a valid workarea is selected 
			if (!getWorkAreaNames().contains(startCylusSettings.getWorkArea().getName())) {
				throw new IllegalArgumentException("Unknown workarea: " + startCylusSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
			}
			int command = 0;
			if (ufNr == 3) {
				if (startCylusSettings.getWorkArea().getDefaultClamping().getFixtureType() == EFixtureType.FIXTURE_2) {
					command = command | CNCMachineConstants.IPC_CYCLESTART_WA2_REQUEST;
				} else {
					command = command | CNCMachineConstants.IPC_CYCLESTART_WA1_REQUEST;
				}
			} else if (ufNr == 4) {
				command = command | CNCMachineConstants.IPC_CYCLESTART_WA2_REQUEST;
			} else {
				throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
			}
		
			int[] registers = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
			// fix for jametal, comment next lines and uncomment sleep
			if (ufNr == 3) {
				if (startCylusSettings.getWorkArea().getDefaultClamping().getFixtureType() == EFixtureType.FIXTURE_2) {
					boolean cycleStartReady = waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA2, START_CYCLE_TIMEOUT);
					if (!cycleStartReady) {
						setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CYCLE_NOT_STARTED_TIMEOUT));
						waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA2);
						setCncMachineTimeout(null);
					}
					//Thread.sleep(10000);
					// we now wait for pick requested
					waitForStatus(CNCMachineConstants.R_PICK_WA2_REQUESTED);
				} else {
					boolean cycleStartReady = waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA1, START_CYCLE_TIMEOUT);
					if (!cycleStartReady) {
						setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CYCLE_NOT_STARTED_TIMEOUT));
						waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA1);
						setCncMachineTimeout(null);
					}
					//Thread.sleep(10000);
					// we now wait for pick requested
					waitForStatus(CNCMachineConstants.R_PICK_WA1_REQUESTED);
				}
			} else if (ufNr == 4) {
				boolean cycleStartReady = waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA2, START_CYCLE_TIMEOUT);
				if (!cycleStartReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CYCLE_NOT_STARTED_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_CYCLE_STARTED_WA2);
					setCncMachineTimeout(null);
				}
				//Thread.sleep(10000);
				// we now wait for pick requested
				waitForStatus(CNCMachineConstants.R_PICK_WA2_REQUESTED);
			} else {
				throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
			}
			
			nCReset();
		} else if (getWayOfOperating() == EWayOfOperating.M_CODES) {
			// we sign of the m code for put
			Set<Integer> robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_LOAD).getRobotServiceOutputsUsed();
			robotServiceOutputs.addAll(getMCodeAdapter().getGenericMCode(M_CODE_LOAD_REVERSAL).getRobotServiceOutputsUsed());
			int command = 0;
			if (robotServiceOutputs.contains(0)) {
				logger.info("Finish load m-c");
				command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
			}
			int[] registers = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
			Thread.sleep(500);
			if (startCylusSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED) && startCylusSettings.getStep().getProcessFlow().hasReversalUnit()) {
				waitForMCodes(M_CODE_UNLOAD_REVERSAL);
			} else {
				waitForMCodes(M_CODE_UNLOAD);
			}
		} else if (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD) {
			// we sign of the m code for put
			Set<Integer> robotServiceOutputs;
			if (startCylusSettings.getStep().getProcessFlow().hasReversalUnit() && startCylusSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED)) {
				robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_LOAD_REVERSAL).getRobotServiceOutputsUsed();
			} else {
				robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_LOAD).getRobotServiceOutputsUsed();
			}
			int command = 0;
			if (robotServiceOutputs.contains(0)) {
				logger.info("Finish load m-c");
				command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
			}
			int[] registers = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
			Thread.sleep(5000);	// wait 5 sec before checking again for m-code
			if (startCylusSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED) && startCylusSettings.getStep().getProcessFlow().hasReversalUnit()) {
				waitForMCodes(M_CODE_UNLOAD_REVERSAL);
			} else {
				waitForMCodes(M_CODE_UNLOAD);
			}
			// now wait for next load and unload M-code
			if (startCylusSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED) && startCylusSettings.getStep().getProcessFlow().hasReversalUnit()) {
				waitForMCodes(M_CODE_LOAD_REVERSAL);
				// We finish m-c load reversal because no load is suppose to come anymore
				if (startCylusSettings.getStep().getProcessFlow().getFinishedAmount() == startCylusSettings.getStep().getProcessFlow().getTotalAmount() - 1) {
					robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_LOAD_REVERSAL).getRobotServiceOutputsUsed();
					command = 0;
					if (robotServiceOutputs.contains(0)) {
						logger.info("Finish load_reversal m-c");
						command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
					}
					cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
				}
			} else {
				waitForMCodes(M_CODE_LOAD);
			}
			// we should finish this M-code if in teach mode
			if ((startCylusSettings.getStep().getProcessFlow().getMode() == Mode.TEACH) || (startCylusSettings.getStep().getProcessFlow().getTotalAmount() <= 1)) {
				// first open fixtures 
				//FIXME review 
				int command2 = CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
				int[] registers2 = {command2};
				cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers2);
				boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY, UNCLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY);
					setCncMachineTimeout(null);
				}
				// then finish m-code
				cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
				Thread.sleep(500);
			}
			waitForNoMCode(M_CODE_LOAD, M_CODE_LOAD_REVERSAL);
			if (startCylusSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED) && startCylusSettings.getStep().getProcessFlow().hasReversalUnit()) {
				waitForMCodes(M_CODE_UNLOAD_REVERSAL);
			} else {
				waitForMCodes(M_CODE_UNLOAD);
			}
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
		int ufNr = pickSettings.getWorkArea().getUserFrame().getNumber();

		// if way of operation is m codes, await unloading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			if (pickSettings.getStep().getProcessFlow().hasReversalUnit() && pickSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED)) {
				waitForMCodes(M_CODE_UNLOAD_REVERSAL);
			} else {
				waitForMCodes(M_CODE_UNLOAD);
			}
		}
		
		int command = 0;
		if (ufNr == 3) {
			command = command | CNCMachineConstants.IPC_PICK_WA1_RQST;
			if (pickSettings.getWorkArea().getActiveClamping(true).getFixtureType() == EFixtureType.FIXTURE_2) {
				command = 0 | CNCMachineConstants.IPC_PICK_WA2_RQST;
			} else if (pickSettings.getWorkArea().getActiveClamping(true).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				command = command | CNCMachineConstants.IPC_PICK_WA2_RQST;
			}
		} else if (ufNr == 4) {
			command = command | CNCMachineConstants.IPC_PICK_WA2_RQST;
		} else {
			throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
		}
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);

		// check pick is prepared
		if (ufNr == 3) {
			if (pickSettings.getWorkArea().getActiveClamping(true).getFixtureType() == EFixtureType.FIXTURE_2) {
				boolean pickReady =  waitForStatus(CNCMachineConstants.R_PICK_WA2_READY, PREPARE_PICK_TIMEOUT);
				if (!pickReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_PICK_WA2_READY);
					setCncMachineTimeout(null);
				}
			} else if (pickSettings.getWorkArea().getActiveClamping(true).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				boolean pickReady =  waitForStatus((CNCMachineConstants.R_PICK_WA1_READY | CNCMachineConstants.R_PICK_WA2_READY), PREPARE_PICK_TIMEOUT);
				if (!pickReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
					waitForStatus((CNCMachineConstants.R_PICK_WA1_READY | CNCMachineConstants.R_PICK_WA2_READY));
					setCncMachineTimeout(null);
				}
			} else {
				boolean pickReady =  waitForStatus(CNCMachineConstants.R_PICK_WA1_READY, PREPARE_PICK_TIMEOUT);
				if (!pickReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_PICK_WA1_READY);
					setCncMachineTimeout(null);
				}
			}
		} else if (ufNr == 4) {
			boolean pickReady =  waitForStatus(CNCMachineConstants.R_PICK_WA2_READY, PREPARE_PICK_TIMEOUT);
			if (!pickReady) {
				setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
				waitForStatus(CNCMachineConstants.R_PICK_WA2_READY);
				setCncMachineTimeout(null);
			}
		} else {
			throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
		}
		
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {		
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		int ufNr = putSettings.getWorkArea().getUserFrame().getNumber();
		// if way of operation is m codes, await unloading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			waitForMCodes(M_CODE_LOAD, M_CODE_LOAD_REVERSAL);
		}
			
		int command = 0;
		if (ufNr == 3) {
			command = command | CNCMachineConstants.IPC_PUT_WA1_REQUEST;
			if (putSettings.getWorkArea().getActiveClamping(false).getFixtureType() == EFixtureType.FIXTURE_2) {
				command = 0 | CNCMachineConstants.IPC_PUT_WA2_REQUEST;
			} else if (putSettings.getWorkArea().getActiveClamping(false).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				command = command | CNCMachineConstants.IPC_PUT_WA2_REQUEST;
			}
		} else if (ufNr == 4) {
			command = command | CNCMachineConstants.IPC_PUT_WA2_REQUEST;
		} else {
			throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
		}		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
				
		// check put is prepared
		if (ufNr == 3) {
			if (putSettings.getWorkArea().getActiveClamping(false).getFixtureType() == EFixtureType.FIXTURE_2) {
				boolean putReady =  waitForStatus(CNCMachineConstants.R_PUT_WA2_READY, PREPARE_PUT_TIMEOUT);
				if (!putReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_PUT_WA2_READY);
					setCncMachineTimeout(null);
				} 
			} else if (putSettings.getWorkArea().getActiveClamping(false).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				boolean putReady =  waitForStatus((CNCMachineConstants.R_PUT_WA1_READY | CNCMachineConstants.R_PUT_WA2_READY), PREPARE_PUT_TIMEOUT);
				if (!putReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
					waitForStatus((CNCMachineConstants.R_PUT_WA1_READY | CNCMachineConstants.R_PUT_WA2_READY));
					setCncMachineTimeout(null);
				} 
			} else {
				boolean putReady =  waitForStatus(CNCMachineConstants.R_PUT_WA1_READY, PREPARE_PUT_TIMEOUT);
				if (!putReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_PUT_WA1_READY);
					setCncMachineTimeout(null);
				} 
			}
		} else if (ufNr == 4) {
			boolean putReady =  waitForStatus(CNCMachineConstants.R_PUT_WA2_READY, PREPARE_PUT_TIMEOUT);
			if (!putReady) {
				setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
				waitForStatus(CNCMachineConstants.R_PUT_WA2_READY);
				setCncMachineTimeout(null);
			} 
		} else {
			throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
		}		
	}

	@Override
	public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		int ufNr = pickSettings.getWorkArea().getUserFrame().getNumber();
		
		int command = 0;
		if (ufNr == 3) {
			command = command | CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
			if (pickSettings.getWorkArea().getActiveClamping(true).getFixtureType() == EFixtureType.FIXTURE_2) {
				command = 0 | CNCMachineConstants.IPC_UNCLAMP_WA2_RQST;
			} else if (pickSettings.getWorkArea().getActiveClamping(true).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				command = command | CNCMachineConstants.IPC_UNCLAMP_WA2_RQST;
			}
		} else if (ufNr == 4) {
			command = command | CNCMachineConstants.IPC_UNCLAMP_WA2_RQST;
		} else {
			throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
		}	
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		if (ufNr == 3) {
			if (pickSettings.getWorkArea().getActiveClamping(true).getFixtureType() == EFixtureType.FIXTURE_2) {
				boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA2_READY, UNCLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_UNCLAMP_WA2_READY);
					setCncMachineTimeout(null);
				}
			} else if (pickSettings.getWorkArea().getActiveClamping(true).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				boolean clampReady =  waitForStatus((CNCMachineConstants.R_UNCLAMP_WA1_READY | CNCMachineConstants.R_UNCLAMP_WA2_READY), UNCLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
					waitForStatus((CNCMachineConstants.R_UNCLAMP_WA1_READY | CNCMachineConstants.R_UNCLAMP_WA2_READY));
					setCncMachineTimeout(null);
				}
			} else {
				boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY, UNCLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY);
					setCncMachineTimeout(null);
				}
			}
		} else if (ufNr == 4) {
			boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA2_READY, UNCLAMP_TIMEOUT);
			if (!clampReady) {
				setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
				waitForStatus(CNCMachineConstants.R_UNCLAMP_WA2_READY);
				setCncMachineTimeout(null);
			}
		} else {
			throw new IllegalArgumentException("Unknown userframe number: " + ufNr);
		}	
		
	}

	@Override
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		int ufNr = putSettings.getWorkArea().getUserFrame().getNumber();
		
		int command = 0;
		if (ufNr == 3) {
			command = command | CNCMachineConstants.IPC_CLAMP_WA1_REQUEST;
			if (putSettings.getWorkArea().getActiveClamping(false).getFixtureType() == EFixtureType.FIXTURE_2) {
				command = 0 | CNCMachineConstants.IPC_CLAMP_WA2_REQUEST;
			} else if (putSettings.getWorkArea().getActiveClamping(false).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				command = command | CNCMachineConstants.IPC_CLAMP_WA2_REQUEST;
			}
		} else if (ufNr == 4) {
			command = command | CNCMachineConstants.IPC_CLAMP_WA2_REQUEST;
		} else {
			throw new IllegalArgumentException("Invalid user frame number: " + putSettings.getWorkArea().getUserFrame().getNumber());
		}
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		if (ufNr == 3) {
			if (putSettings.getWorkArea().getActiveClamping(false).getFixtureType() == EFixtureType.FIXTURE_2) {
				boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA2_READY, CLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_CLAMP_WA2_READY);
					setCncMachineTimeout(null);
				} 
			} else if (putSettings.getWorkArea().getActiveClamping(false).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				boolean clampReady =  waitForStatus((CNCMachineConstants.R_CLAMP_WA1_READY | CNCMachineConstants.R_CLAMP_WA2_READY), CLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
					waitForStatus((CNCMachineConstants.R_CLAMP_WA1_READY | CNCMachineConstants.R_CLAMP_WA2_READY));
					setCncMachineTimeout(null);
				} 
			} else {
				boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA1_READY, CLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_CLAMP_WA1_READY);
					setCncMachineTimeout(null);
				} 
			}
		} else if (ufNr == 4) {
			boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA2_READY, CLAMP_TIMEOUT);
			if (!clampReady) {
				setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
				waitForStatus(CNCMachineConstants.R_CLAMP_WA2_READY);
				setCncMachineTimeout(null);
			} 
		} else {
			throw new IllegalArgumentException("Invalid user frame number: " + putSettings.getWorkArea().getUserFrame().getNumber());
		}
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws InterruptedException, DeviceActionException {
		// check first workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			if (putSettings.getWorkPieceType().equals(WorkPiece.Type.HALF_FINISHED)) {
				return getMCodeAdapter().isMCodeActive(M_CODE_LOAD_REVERSAL);
			} else {
				return getMCodeAdapter().isMCodeActive(M_CODE_LOAD);
			}
		}
		return true;
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
	public void pickFinished(final DevicePickSettings pickSettings) throws AbstractCommunicationException, InterruptedException, DeviceActionException {
		if (getWayOfOperating() == EWayOfOperating.M_CODES) {
			if (((pickSettings.getStep().getProcessFlow().getFinishedAmount() == pickSettings.getStep().getProcessFlow().getTotalAmount() - 1) &&
					(pickSettings.getStep().getProcessFlow().getType() != ProcessFlow.Type.CONTINUOUS) && (pickSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED))) || 
					(pickSettings.getStep().getProcessFlow().getMode() == Mode.TEACH) && (pickSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED))) {
				// last work piece: send reset in stead of finishing m code
				nCReset();
			} else {
				Set<Integer> robotServiceOutputs;
				if (pickSettings.getStep().getProcessFlow().hasReversalUnit() && pickSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED)) {
					robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_UNLOAD_REVERSAL).getRobotServiceOutputsUsed();
				} else {
					robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_UNLOAD).getRobotServiceOutputsUsed();
				}
				int command = 0;
				if (robotServiceOutputs.contains(0)) {
					logger.info("AFMELDEN M-CODE UNLOAD");
					command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
				}
				int[] registers = {command};
				cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
			}
		} else if (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD) {
			if (((pickSettings.getStep().getProcessFlow().getFinishedAmount() == pickSettings.getStep().getProcessFlow().getTotalAmount() - 1) &&
					(pickSettings.getStep().getProcessFlow().getType() != ProcessFlow.Type.CONTINUOUS) &&
					(pickSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED)))
				||  ((pickSettings.getStep().getProcessFlow().getMode() == Mode.TEACH) && 
					(pickSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED)))
				) {
				// last work piece: send reset in stead of finishing m code
				nCReset();
				// also finish m code if still active after nc reset
				Thread.sleep(500);
				boolean finishMCode = false;
				for (int activeMCode : getMCodeAdapter().getActiveMCodes()) {
					if (getMCodeAdapter().getGenericMCode(activeMCode).getRobotServiceOutputsUsed().contains(0)) {
						finishMCode = true;
					}
				}
				if (finishMCode) {
					int command = CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
					int[] registers = {command};
					cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
					Thread.sleep(500);
				}
			} else {
				Set<Integer> robotServiceOutputs;
				if (pickSettings.getStep().getProcessFlow().hasReversalUnit() && pickSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED)) {
					robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_UNLOAD_REVERSAL).getRobotServiceOutputsUsed();
				} else {
					robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_UNLOAD).getRobotServiceOutputsUsed();
				}
				int command = 0;
				if (robotServiceOutputs.contains(0)) {
					logger.info("AFMELDEN M-CODE UNLOAD");
					command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
				}
				int[] registers = {command};
				cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
				Thread.sleep(500);
				if ((pickSettings.getStep().getProcessFlow().getFinishedAmount() == pickSettings.getStep().getProcessFlow().getTotalAmount() - 2) &&
						(pickSettings.getStep().getProcessFlow().getType() != ProcessFlow.Type.CONTINUOUS)) {
					if (!pickSettings.getWorkPieceType().equals(WorkPiece.Type.HALF_FINISHED)) {
						// last but one work piece: no upcoming put, but we wait for the upcoming LOAD M-code and confirm it
						waitForMCodes(M_CODE_LOAD, M_CODE_LOAD_REVERSAL);
						if (pickSettings.getStep().getProcessFlow().hasReversalUnit() && pickSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED)) {
							robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_LOAD_REVERSAL).getRobotServiceOutputsUsed();
						} else {
							robotServiceOutputs = getMCodeAdapter().getGenericMCode(M_CODE_LOAD).getRobotServiceOutputsUsed();
						}
						command = 0;
						if (robotServiceOutputs.contains(0)) {
							logger.info("Finish M-C load");
							command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
						}
						registers[0] = command;
						cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
						Thread.sleep(500);
					}
				}
			}
		}
	}
	
	@Override 
	public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, InterruptedException {}
	
	// these are not taken into account by the machine for now...
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException { }
	@Override public void prepareForStartCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException { }

	@Override
	public Coordinates getLocationOrientation(final WorkArea workArea, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping(true).getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged()) {
				c.setR(c.getR() + getClampingWidthR());
			} else {
				c.setR(c.getR());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + getClampingWidthR());
			}
		}
		return c;
	}
	
	@Override
	public Coordinates getPickLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping(true).getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged()) {
				c.setR(c.getR() + getClampingWidthR());
			} else {
				c.setR(c.getR());
			}
			switch (workArea.getActiveClamping(true).getType()) {
				case CENTRUM:
					// no action needed
					break;
				case FIXED_XM:
					c.setX(c.getX() - workPieceDimensions.getWidth()/2);
					break;
				case FIXED_YM:
					c.setY(c.getY() - workPieceDimensions.getWidth()/2);
					break;
				case FIXED_XP:
					c.setX(c.getX() + workPieceDimensions.getWidth()/2);
					break;
				case FIXED_YP:
					c.setY(c.getY() + workPieceDimensions.getWidth()/2);
					break;
				case NONE:
					throw new IllegalArgumentException("Machine clamping type can't be NONE.");
				default:
					throw new IllegalArgumentException("Unknown clamping type: " + workArea.getActiveClamping(true).getType());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + getClampingWidthR());
			}
			switch (workArea.getActiveClamping(true).getType()) {
			case CENTRUM:
				// no action needed
				break;
			case FIXED_XM:
				c.setX(c.getX() - workPieceDimensions.getLength()/2);
				break;
			case FIXED_YM:
				c.setY(c.getY() - workPieceDimensions.getLength()/2);
				break;
			case FIXED_XP:
				c.setX(c.getX() + workPieceDimensions.getLength()/2);
				break;
			case FIXED_YP:
				c.setY(c.getY() + workPieceDimensions.getLength()/2);
				break;
			case NONE:
				throw new IllegalArgumentException("Machine clamping type can't be NONE.");
			default:
				throw new IllegalArgumentException("Unknown clamping type: " + workArea.getActiveClamping(true).getType());
			}
		}
		return c;
	}

	//TODO - duplicate code
	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping(false).getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged()) {
				c.setR(c.getR() + getClampingWidthR());
			} else {
				c.setR(c.getR());
			}
			switch (workArea.getActiveClamping(false).getType()) {
				case CENTRUM:
					// no action needed
					break;
				case FIXED_XM:
					c.setX(c.getX() - workPieceDimensions.getWidth()/2);
					break;
				case FIXED_YM:
					c.setY(c.getY() - workPieceDimensions.getWidth()/2);
					break;
				case FIXED_XP:
					c.setX(c.getX() + workPieceDimensions.getWidth()/2);
					break;
				case FIXED_YP:
					c.setY(c.getY() + workPieceDimensions.getWidth()/2);
					break;
				case NONE:
					throw new IllegalArgumentException("Machine clamping type can't be NONE.");
				default:
					throw new IllegalArgumentException("Unknown clamping type: " + workArea.getActiveClamping(false).getType());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + getClampingWidthR());
			}
			switch (workArea.getActiveClamping(false).getType()) {
			case CENTRUM:
				// no action needed
				break;
			case FIXED_XM:
				c.setX(c.getX() - workPieceDimensions.getLength()/2);
				break;
			case FIXED_YM:
				c.setY(c.getY() - workPieceDimensions.getLength()/2);
				break;
			case FIXED_XP:
				c.setX(c.getX() + workPieceDimensions.getLength()/2);
				break;
			case FIXED_YP:
				c.setY(c.getY() + workPieceDimensions.getLength()/2);
				break;
			case NONE:
				throw new IllegalArgumentException("Machine clamping type can't be NONE.");
			default:
				throw new IllegalArgumentException("Unknown clamping type: " + workArea.getActiveClamping(false).getType());
			}
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

	@Override
	public boolean isUsingNewDevInt() {
		return false;
	}

}

