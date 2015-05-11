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
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.WorkAreaManager;
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
			final SocketConnection socketConnection, final int clampingWidthR, final int nbFixtures, final float rRoundPieces) {
		super(name, wayOfOperating, mCodeAdapter, zones, clampingWidthR, nbFixtures, rRoundPieces);
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
		for(WorkAreaManager waManager: getWorkAreaManagers()) {
			waManager.resetNbPossibleWPPerClamping(getWayOfOperating().getNbOfSides());
			waManager.setMaxClampingsToUse(getWayOfOperating().getNbOfSides() * waManager.getNbActiveClampingsEachSide());
		}
		
		int command = 0;
		int ufNr = 0;
		for (AbstractProcessStep step : process.getProcessSteps()) {
			if ((step instanceof ProcessingStep) && ((ProcessingStep) step).getDevice().equals(this)) {
				ufNr = ((ProcessingDeviceStartCyclusSettings) ((ProcessingStep) step).getDeviceSettings()).getWorkArea().getWorkAreaManager().getUserFrame().getNumber();
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
	public void startCyclus(final ProcessingDeviceStartCyclusSettings startCyclusSettings, final int processId) 
			throws SocketResponseTimedOutException, SocketDisconnectedException, DeviceActionException, InterruptedException, SocketWrongResponseException {
		int ufNr = startCyclusSettings.getWorkArea().getWorkAreaManager().getUserFrame().getNumber();
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
			finishMCode(processId, mCodeLoad);
			Thread.sleep(500);
			int mCodeUnLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), false);
			waitForMCodes(processId, mCodeUnLoad);
		} 
		// Twee kanten (spiegel - we moeten dus eigenlijk 2 cycli door)
		else if (getWayOfOperating().equals(EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int nbCncInFlow = startCyclusSettings.getStep().getProcessFlow().getNbCNCInFlow();
			// we sign off the m code for put
			int mCodeLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), true);
			finishMCode(processId, mCodeLoad);
			// we wait for unloading the finished piece from the other process - in the meantime the just loaded piece has gone to the back for processing (kanteltafel)
			// depending on where we are in the flow, there could be multiple unload options. That is if the other process is in a different CNC step.
			int prvUnload = getPrvMCode(mCodeLoad, nbCncInFlow);
			int nxtUnload = getNxtMCode(mCodeLoad, nbCncInFlow);
			waitForMCodes(processId, prvUnload, nxtUnload);
			// now wait for next load. This is the load following on the unload from the other process.
			int nxtPrvLoad = getNxtMCode(prvUnload, nbCncInFlow);
			int nxtLoad = getNxtMCode(nxtUnload, nbCncInFlow);
			waitForMCodes(processId, nxtPrvLoad, nxtLoad);
			// We are in the final step of 1 process. The workpiece has been unloaded and we will now wait for a new process
			// to load a raw workpiece. However, all workpieces are done, so we can directly finish the mCode (no load will come anymore).
			int nbClampsFilled = startCyclusSettings.getWorkArea().getNbClampingsPerProcessThread(processId);
			if (startCyclusSettings.getStep().getProcessFlow().getFinishedAmount() == startCyclusSettings.getStep().getProcessFlow().getTotalAmount() - nbClampsFilled) {
				// This test only succeeds if this process is the last one to be executed - so no other startCyclusSettings of other processes anymore
				finishMCode(processId, nxtPrvLoad);
				finishMCode(processId, nxtLoad);
			} 
			// we should finish this M-code if in teach mode (we only use 1 side - no pieces in the clampings that is currently at the front of the machine)
			else if ((startCyclusSettings.getStep().getProcessFlow().getMode() == Mode.TEACH) || (startCyclusSettings.getStep().getProcessFlow().getTotalAmount() <= nbClampsFilled)) {
				unclampAfterFinish(startCyclusSettings.getWorkArea().getWorkAreaManager());
				// then finish m-code
				finishMCode(processId, nxtPrvLoad);
				finishMCode(processId, nxtLoad);
			}
			waitForNoMCode(processId, nxtPrvLoad, nxtLoad);
			//Unload (cycle finished - this process)
			int mCodeUnLoad = getMCodeIndex(startCyclusSettings.getWorkArea(), false);
			waitForMCodes(processId, mCodeUnLoad);
		} else {
			//This can only occur if a new way of operating is added without updating the function
			throw new IllegalStateException("Unknown way of operating: " + getWayOfOperating());
		}
	}

	@Override
	public void prepareForPick(final DevicePickSettings pickSettings, final int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		int ufNr = pickSettings.getWorkArea().getWorkAreaManager().getUserFrame().getNumber();

		// if way of operation is m codes, await unloading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
			waitForMCodes(processId, mCodeUnLoad);
		}
		
		int command = 0;
		if (ufNr == 3) {
			command = command | CNCMachineConstants.IPC_PICK_WA1_RQST;
			if (pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_2) {
				command = 0 | CNCMachineConstants.IPC_PICK_WA2_RQST;
			} else if (pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_1_2) {
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
			if (pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_2) {
				boolean pickReady =  waitForStatus(CNCMachineConstants.R_PICK_WA2_READY, PREPARE_PICK_TIMEOUT);
				if (!pickReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_PICK_WA2_READY);
					setCncMachineTimeout(null);
				}
			} else if (pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_1_2) {
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
	public void prepareForPut(final DevicePutSettings putSettings, final int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException {		
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		int ufNr = putSettings.getWorkArea().getWorkAreaManager().getUserFrame().getNumber();
		// if way of operation is m codes, await loading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int mCodeLoad = getMCodeIndex(putSettings.getWorkArea(), true);
			waitForMCodes(processId, mCodeLoad);
		}
			
		int command = 0;
		if (ufNr == 3) {
			command = command | CNCMachineConstants.IPC_PUT_WA1_REQUEST;
			if (putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false, putSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_2) {
				command = 0 | CNCMachineConstants.IPC_PUT_WA2_REQUEST;
			} else if (putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false, putSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_1_2) {
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
			if (putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false,putSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_2) {
				boolean putReady =  waitForStatus(CNCMachineConstants.R_PUT_WA2_READY, PREPARE_PUT_TIMEOUT);
				if (!putReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_PUT_WA2_READY);
					setCncMachineTimeout(null);
				} 
			} else if (putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false, putSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_1_2) {
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
		int ufNr = pickSettings.getWorkArea().getWorkAreaManager().getUserFrame().getNumber();
		
		int command = 0;
		if (ufNr == 3) {
			command = command | CNCMachineConstants.IPC_UNCLAMP_WA1_RQST;
			if (pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_2) {
				command = 0 | CNCMachineConstants.IPC_UNCLAMP_WA2_RQST;
			} else if (pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_1_2) {
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
			if (pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_2) {
				boolean clampReady =  waitForStatus(CNCMachineConstants.R_UNCLAMP_WA2_READY, UNCLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_UNCLAMP_WA2_READY);
					setCncMachineTimeout(null);
				}
			} else if (pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true,pickSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_1_2) {
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
		int ufNr = putSettings.getWorkArea().getWorkAreaManager().getUserFrame().getNumber();
		
		int command = 0;
		if (ufNr == 3) {
			command = command | CNCMachineConstants.IPC_CLAMP_WA1_REQUEST;
			if (putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false, putSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_2) {
				command = 0 | CNCMachineConstants.IPC_CLAMP_WA2_REQUEST;
			} else if (putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false, putSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_1_2) {
				command = command | CNCMachineConstants.IPC_CLAMP_WA2_REQUEST;
			}
		} else if (ufNr == 4) {
			command = command | CNCMachineConstants.IPC_CLAMP_WA2_REQUEST;
		} else {
			throw new IllegalArgumentException("Invalid user frame number: " + putSettings.getWorkArea().getWorkAreaManager().getUserFrame().getNumber());
		}
		
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_REQUEST, registers);
		
		if (ufNr == 3) {
			if (putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false,putSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_2) {
				boolean clampReady =  waitForStatus(CNCMachineConstants.R_CLAMP_WA2_READY, CLAMP_TIMEOUT);
				if (!clampReady) {
					setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
					waitForStatus(CNCMachineConstants.R_CLAMP_WA2_READY);
					setCncMachineTimeout(null);
				} 
			} else if (putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false, putSettings.getWorkArea().getSequenceNb()).getFixtureType() == EFixtureType.FIXTURE_1_2) {
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
			throw new IllegalArgumentException("Invalid user frame number: " + putSettings.getWorkArea().getWorkAreaManager().getUserFrame().getNumber());
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
				finishMCode(processId, mCodeUnLoad);
				Thread.sleep(500);
			}
		} else if (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD) {
			if (isLastWorkPiece(pickSettings)) {
				logger.debug("This was the last workpiece to be picked.");
				// all clamps should be empty, because this was the last piece; open them.
				unclampAfterFinish(pickSettings.getWorkArea().getWorkAreaManager());
				// thread.sleep(500) after every request to the CNC machine - otherwise we could get the error of multiple IPC requests (CNCMachine.Alarm 23)
				Thread.sleep(500);
				// last work piece: send reset instead of finishing m code
				nCReset();
				Thread.sleep(500);
				// also finish m code if still active after nc reset
				for (int activeMCode : getMCodeAdapter().getActiveMCodes()) {
					finishMCode(processId, activeMCode);
					Thread.sleep(500);
				}
			} else {
				int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
				finishMCode(processId, mCodeUnLoad);
				Thread.sleep(500);
				int nbActiveClampings = getNbClampingsInUse(processId);
				// We are going to put the piece that we have just picked back to the stacker ( +1), so in fact we have finished getFinishedAmount + 1.
				// There are maximum nbActiveClampings workPieces still in the flow. 
				if ((pickSettings.getStep().getProcessFlow().getFinishedAmount() + 1 + nbActiveClampings == pickSettings.getStep().getProcessFlow().getTotalAmount()) &&
						(pickSettings.getStep().getProcessFlow().getType() != ProcessFlow.Type.CONTINUOUS)) {
					if (!pickSettings.getStep().getRobotSettings().getWorkPiece().getType().equals(WorkPiece.Type.HALF_FINISHED)) {
						// The process executor which holds the last workpiece is currently waiting for the sequence unload/load from this
						// executor (last but one - depending on number of fixtures). The pickFinished has already finished the unloading code
						// so, here we need to get the loading code and confirm it. This is because there is no load m-code supposed to come
						// anymore. Off course, this is only true in case of a non HALF_FINISHED piece, because otherwise loading can still
						// come (for reversing the piece).
						int mCodeLoad = getMCodeIndex(pickSettings.getWorkArea(), true);
						// last but one work piece: no upcoming put, but we wait for the upcoming LOAD M-code and confirm it
						mCodeLoad = getNxtMCode(mCodeLoad, pickSettings.getStep().getProcessFlow().getNbCNCInFlow());
						mCodeLoad = getNxtMCode(mCodeLoad, pickSettings.getStep().getProcessFlow().getNbCNCInFlow());
						waitForMCodes(processId, mCodeLoad);
						finishMCode(processId, mCodeLoad);
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
	@Override public void prepareForStartCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException {
	    if (startCylusSettings.getWorkNumber() > 0) {
	        logger.debug("Prepare for process program id " + startCylusSettings.getWorkNumber());
	        // TODO - add worknumber search command
	    }
	}

	@Override
	public Coordinates getLocationOrientation(final SimpleWorkArea workArea, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getWorkAreaManager().getActiveClamping(true, workArea.getSequenceNb()).getRelativePosition());
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
	
	private void unclampAfterFinish(final WorkAreaManager workarea) throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
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
	private void finishMCode(final int processId, final int mCodeIndex) 
			throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
		Set<Integer> robotServiceOutputs = getMCodeAdapter().getGenericMCode(mCodeIndex).getRobotServiceOutputsUsed();
		int command = 0;
		if (robotServiceOutputs.contains(0)) {
			logger.info("PRC[" + processId + "] FINISH M CODE " + mCodeIndex);
			command = command | CNCMachineConstants.IPC_DOORS_SERV_REQ_FINISH;
			int[] registers = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstants.IPC_READ_REQUEST_2, registers);
			waitForNoMCode(processId, mCodeIndex);
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
		if (pickSettings.getStep().getProcessFlow().getNbCNCInFlow() == pickSettings.getWorkArea().getSequenceNb()) {
			//final piece of the flow (amount)
			if (pickSettings.getStep().getProcessFlow().getFinishedAmount() == pickSettings.getStep().getProcessFlow().getTotalAmount() - 1) {
				return true;
			} else if (pickSettings.getStep().getProcessFlow().getMode().equals(Mode.TEACH)) {
				return true;
			}
		}
		return false;
	}
	
	private int getNbClampingsInUse(final int processId) {
		int result = 0;
		for (Zone zone: getZones()) {
			for (WorkAreaManager workAreaManager: zone.getWorkAreaManagers()) {
				if (workAreaManager.isInUse()) {
					result += workAreaManager.getMaxNbClampingOtherProcessThread(processId);
				}
			}
		}
		return result;
	}

}

