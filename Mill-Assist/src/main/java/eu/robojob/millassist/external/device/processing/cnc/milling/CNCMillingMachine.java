package eu.robojob.millassist.external.device.processing.cnc.milling;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.communication.socket.SocketDisconnectedException;
import eu.robojob.millassist.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.millassist.external.communication.socket.SocketWrongResponseException;
import eu.robojob.millassist.external.device.Clamping;
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
	
	private static Logger logger = LogManager.getLogger(CNCMillingMachine.class.getName());
	
	public CNCMillingMachine(final String name, final EWayOfOperating wayOfOperating, final MCodeAdapter mCodeAdapter, final Set<Zone> zones, 
			final SocketConnection socketConnection, final int clampingWidthR, final int nbFixtures) {
		super(name, wayOfOperating, mCodeAdapter, zones, clampingWidthR, nbFixtures);
		this.cncMachineCommunication = new CNCMachineSocketCommunication(socketConnection, this);
		CNCMachineMonitoringThread cncMachineMonitoringThread = new CNCMachineMonitoringThread(this);
		// start monitoring thread at creation of this object
		ThreadManager.submit(cncMachineMonitoringThread);
	}

	@Override
	public CNCMachineSocketCommunication getCNCMachineSocketCommunication() {
		return this.cncMachineCommunication;
	}
	
	@Override
	public void updateStatusAndAlarms() throws InterruptedException, SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException {
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
	public void reset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		command = command | CNCMachineConstants.IPC_RESET_REQUEST;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
		setCncMachineTimeout(null);
	}
	
	@Override
	public void nCReset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		command = command | CNCMachineConstants.IPC_NC_RESET;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
		//TODO read the OTHER register and wait till the set bit is zero, this has to be implemented in the device interface, for now: wait 2 seconds
		Thread.sleep(SLEEP_TIME_AFTER_RESET);
	}

	@Override
	public void powerOff() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		command = command | CNCMachineConstants.IPC_POWER_OFF;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
		// normally no more commands after this, so multiple IPC requests problem can't occur
	}

	@Override
	public void indicateAllProcessed() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		command = command | CNCMachineConstants.IPC_ALL_WP_PROCESSED;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
	}

	@Override
	public void indicateOperatorRequested(final boolean requested) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		if (requested) {
			command = command | CNCMachineConstants.IPC_OPERATOR_REQUESTED;
		}
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
	}
	
	@Override
	public void clearIndications() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
	}
	
	@Override
	public void prepareForProcess(final ProcessFlow process)  throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		//FIXME review! potential problems with reset in double processflow execution
		clearIndications();
		// check work area
		for(WorkArea wa: getWorkAreas()) {
			wa.resetNbPossibleWPPerClamping(getWayOfOperating().getNbOfSides());
			wa.setNbUsedClampings(getWayOfOperating().getNbOfSides() * wa.getNbActiveClampingsEachSide());
		}
		
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
	public void startCyclus(final ProcessingDeviceStartCyclusSettings startCyclusSettings) throws SocketResponseTimedOutException, SocketDisconnectedException, DeviceActionException, InterruptedException, SocketWrongResponseException {
		int ufNr = startCyclusSettings.getWorkArea().getUserFrame().getNumber();
		if (getWayOfOperating().equals(EWayOfOperating.START_STOP)) {
			// check a valid workarea is selected 
			if (!getWorkAreaNames().contains(startCyclusSettings.getWorkArea().getName())) {
				throw new IllegalArgumentException("Unknown workarea: " + startCyclusSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
			}
			int command = 0;
			if (ufNr == 3) {
				if (startCyclusSettings.getWorkArea().getDefaultClamping().getFixtureType() == EFixtureType.FIXTURE_2) {
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
				if (startCyclusSettings.getWorkArea().getDefaultClamping().getFixtureType() == EFixtureType.FIXTURE_2) {
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
		} else if (getWayOfOperating().equals(EWayOfOperating.M_CODES)) {
			int mCodeLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), true);
			// we sign of the m code for put
			finishMCode(mCodeLoad);
			Thread.sleep(500);
			int mCodeUnLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), false);
			waitForMCodes(mCodeUnLoad);
		} 
		// Twee kanten (spiegel - we moeten dus eigenlijk 2 cycli door)
		else if (getWayOfOperating().equals(EWayOfOperating.M_CODES_DUAL_LOAD)) {
			// we sign off the m code for put
			int mCodeLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), true);
			finishMCode(mCodeLoad);
			// we wait for unloading - in the meantime the just loaded piece has gone to the back for processing (kanteltafel)
			//FIXME- check this... what if we have reversals??? - is this the correct mCode to wait for?
			int mCodeUnLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), false);
			waitForMCodes(mCodeUnLoad);
			// now wait for next load and unload M-code (last unload code is part of this cycle)
			//if (startCylusSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED) && startCylusSettings.getStep().getProcessFlow().hasReversalUnit()) {
			if (startCyclusSettings.getWorkArea().getPrioIfCloned() == startCyclusSettings.getStep().getProcessFlow().getNbCNCInFlow()) {
				mCodeLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), true);
				waitForMCodes(mCodeLoad);
				// We finish m-c load reversal because no load is suppose to come anymore
				if (startCyclusSettings.getStep().getProcessFlow().getFinishedAmount() == startCyclusSettings.getStep().getProcessFlow().getTotalAmount() - 1) {
					//TODO - afmelden enkel bij laatste CNC machine in de flow
					finishMCode(mCodeLoad);
				}
			} else {
				mCodeLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), true);
				waitForMCodes(mCodeLoad);
			}
			// we should finish this M-code if in teach mode (we only use 1 side)
			if ((startCyclusSettings.getStep().getProcessFlow().getMode() == Mode.TEACH) || (startCyclusSettings.getStep().getProcessFlow().getTotalAmount() <= 1)) {
				unclampAfterFinish(startCyclusSettings.getWorkArea());
				// then finish m-code
				mCodeLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), true);
				finishMCode(mCodeLoad);
			}
			waitForNoMCode(mCodeLoad);
			//Unload (cycle finished)
			mCodeUnLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), false);
			waitForMCodes(mCodeUnLoad);
		} else {
			//This can only occur if a new way of operating is added without updating the function
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
			int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
			waitForMCodes(mCodeUnLoad);
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
		// if way of operation is m codes, await loading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int mCodeLoad = getMCodeIndex(putSettings.getWorkArea(), true);
			waitForMCodes(mCodeLoad);
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
			int mCodeLoad = getMCodeIndex(putSettings.getWorkArea(), true);
			return getMCodeAdapter().isMCodeActive(mCodeLoad);
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
	public void pickFinished(final DevicePickSettings pickSettings, final int processId) throws AbstractCommunicationException, InterruptedException, DeviceActionException {
		if (getWayOfOperating() == EWayOfOperating.M_CODES) {
			if (isLastWorkPiece(pickSettings)) {
				// last work piece: send reset in stead of finishing m code
				nCReset();
			} else {
				int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
				finishMCode(mCodeUnLoad);
				Thread.sleep(500);
			}
		} else if (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD) {
			if (isLastWorkPiece(pickSettings)) {
				// all clamps should be empty, because this was the last piece; open them.
				unclampAfterFinish(pickSettings.getWorkArea());
				// last work piece: send reset instead of finishing m code
				nCReset();
				Thread.sleep(500);
				// also finish m code if still active after nc reset
				for (int activeMCode : getMCodeAdapter().getActiveMCodes()) {
					finishMCode(activeMCode);
					Thread.sleep(500);
				}
			} else {
				int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
				finishMCode(mCodeUnLoad);
				Thread.sleep(500);
				int nbActiveClampings = pickSettings.getWorkArea().getMaxNbClampingOtherProcessThread(processId);
				// We are going to put the piece that we have just picked back to the stacker ( +1), so in fact we have finished getFinishedAmount + 1.
				// There are maximum nbActiveClampings workPieces still in the flow. 
				if ((pickSettings.getStep().getProcessFlow().getFinishedAmount() + 1 + nbActiveClampings == pickSettings.getStep().getProcessFlow().getTotalAmount()) &&
						(pickSettings.getStep().getProcessFlow().getType() != ProcessFlow.Type.CONTINUOUS)) {
					if (!pickSettings.getStep().getRobotSettings().getWorkPiece().getType().equals(WorkPiece.Type.HALF_FINISHED)) {
						int mCodeLoad = getMCodeIndex(pickSettings.getWorkArea(), true);
						// last but one work piece: no upcoming put, but we wait for the upcoming LOAD M-code and confirm it
						waitForMCodes(mCodeLoad);
						finishMCode(mCodeLoad);
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
	
	private void unclampAfterFinish(final WorkArea workarea) throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
		int command2 = 0;
		boolean multi = false;
		Set<Clamping> clampingsPresent = workarea.getClampings();
		if (workarea.getUserFrame().getNumber() == 3) {
			command2 = command2 | CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
			for (Clamping activeClamp: clampingsPresent) {
				if (activeClamp.getFixtureType() == EFixtureType.FIXTURE_2) {
					multi = true;
					command2 = command2 | CNCMachineConstants.IPC_UNCLAMP_WA2_RQST;
				} else if (activeClamp.getFixtureType().equals(EFixtureType.FIXTURE_1_2)) {
					multi = true;
					command2 = command2 | CNCMachineConstants.IPC_UNCLAMP_WA2_RQST;
				}
			}
		} else if (workarea.getUserFrame().getNumber() == 4) {
			command2 = command2 | CNCMachineConstants.IPC_UNCLAMP_WA2_RQST;
		}		
		
		//FIXME review 
		int[] registers2 = {command2};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers2);
		
		boolean clampReady;
		if (multi) {
			clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY | CNCMachineConstants.R_UNCLAMP_WA2_READY, UNCLAMP_TIMEOUT);
			if (!clampReady) {
				setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
				waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY | CNCMachineConstants.R_UNCLAMP_WA2_READY);
				setCncMachineTimeout(null);
			}
		} else {
			clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY, UNCLAMP_TIMEOUT);
			if (!clampReady) {
				setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
				waitForStatus(CNCMachineConstants.R_UNCLAMP_WA1_READY);
				setCncMachineTimeout(null);
			}
		}
	}
	
	/**
	 * Finish the m-code given in case it is configured to be finished
	 * 
	 * @param mCodeIndex
	 * @throws SocketResponseTimedOutException
	 * @throws SocketDisconnectedException
	 * @throws SocketWrongResponseException
	 * @throws InterruptedException
	 * @throws DeviceActionException
	 */
	private void finishMCode(final int mCodeIndex) 
			throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
		Set<Integer> robotServiceOutputs = getMCodeAdapter().getGenericMCode(mCodeIndex).getRobotServiceOutputsUsed();
		int command = 0;
		if (robotServiceOutputs.contains(0)) {
			logger.info("FINISH M CODE " + mCodeIndex);
			command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
			int[] registers = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
			waitForNoMCode(mCodeIndex);
		}
	}
	
	/**
	 * Check whether the piece that needs to be picked from the machine is the last work piece of the flow
	 * 
	 * @param 	pickSettings
	 * @return	- false in case the processflow is continuous. This means that we do not have a fixed amount
	 * 			of pieces to do, so we will never reach the last one.
	 * 			- true in case the work piece that needs to be picked is the last one of the process executor
	 * 			(E.g. if one reversal unit is part of the flow, the last work piece of the executor is the one
	 * 			that has been processed after reversal) AND we are in teach mode OR it is the last one of 
	 * 			the entire process
	 * 			- false otherwise
	 */
	//TODO - duplicate methods (see CNCMillingMachineDevIntv2)
	private boolean isLastWorkPiece(final DevicePickSettings pickSettings) {
		if (pickSettings.getStep().getProcessFlow().getType().equals(ProcessFlow.Type.CONTINUOUS)) {
			return false;
		}
		if (pickSettings.getStep().getProcessFlow().getNbCNCInFlow() == pickSettings.getWorkArea().getPrioIfCloned()) {
			//final piece of the flow (amount)
			if (pickSettings.getStep().getProcessFlow().getFinishedAmount() == pickSettings.getStep().getProcessFlow().getTotalAmount() - 1) {
				return true;
			} else if (pickSettings.getStep().getProcessFlow().getMode().equals(Mode.TEACH)) {
				return true;
			}
		}
		return false;
	}
}

