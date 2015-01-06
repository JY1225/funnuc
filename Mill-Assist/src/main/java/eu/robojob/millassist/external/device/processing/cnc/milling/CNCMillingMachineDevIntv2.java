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
import eu.robojob.millassist.external.device.AbstractDeviceActionSettings;
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
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineConstantsDevIntv2;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineMonitoringThreadDevIntv2;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineSocketCommunication;
import eu.robojob.millassist.external.device.processing.cnc.EWayOfOperating;
import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class CNCMillingMachineDevIntv2 extends AbstractCNCMachine {
	
	private CNCMachineSocketCommunication cncMachineCommunication;

	private static final int PREPARE_PUT_TIMEOUT = 2 * 60 * 1000;
	private static final int PREPARE_PICK_TIMEOUT = 2 * 60 * 1000;
	private static final int CLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int UNCLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int START_CYCLE_TIMEOUT = 3 * 60 * 1000;
	private static final int SLEEP_TIME_AFTER_RESET = 500;
	private static final int OPERATOR_RQST_BLUE_LAMP_VAL = 5;
	private static final int FINISH_BLUE_LAMP_VAL = 10;
	
	private static Logger logger = LogManager.getLogger(CNCMillingMachineDevIntv2.class.getName());
	
	public CNCMillingMachineDevIntv2(final String name, final EWayOfOperating wayOfOperating, final MCodeAdapter mCodeAdapter, final Set<Zone> zones, 
			final SocketConnection socketConnection, final int clampingWidthR, final int nbFixtures) {
		super(name, wayOfOperating, mCodeAdapter, zones, clampingWidthR, nbFixtures);
		this.cncMachineCommunication = new CNCMachineSocketCommunication(socketConnection, this);
		CNCMachineMonitoringThreadDevIntv2 cncMachineMonitoringThread = new CNCMachineMonitoringThreadDevIntv2(this);
		// start monitoring thread at creation of this object
		ThreadManager.submit(cncMachineMonitoringThread);
	}
	
	@Override
	public CNCMachineSocketCommunication getCNCMachineSocketCommunication() {
		return this.cncMachineCommunication;
	}
	
	@Override
	public void updateStatusAndAlarms() throws InterruptedException, SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException {
		List<Integer> statusInts = (cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv2.STATUS_SLOT_1, 2));
		setStatus(statusInts.get(0), CNCMachineConstantsDevIntv2.STATUS_SLOT_1);
		setStatus(statusInts.get(1), CNCMachineConstantsDevIntv2.STATUS_SLOT_2);
		statusInts = (cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv2.IPC_BUSY, 3));
		setStatus(statusInts.get(0), CNCMachineConstantsDevIntv2.IPC_BUSY);
		setStatus(statusInts.get(1), CNCMachineConstantsDevIntv2.IPC_ERROR);
		setStatus(statusInts.get(2), CNCMachineConstantsDevIntv2.IPC_OK);
		List<Integer> alarmInts = cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv2.ERROR_REG_1, 6);
		int alarmReg1 = alarmInts.get(0);
		int alarmReg2 = alarmInts.get(1);
		int alarmReg3 = alarmInts.get(2);
		int alarmReg4 = alarmInts.get(3);
		int alarmReg5 = alarmInts.get(4);
		int alarmReg6 = alarmInts.get(5);
		setAlarms(CNCMachineAlarm.parseCNCAlarms(getCncMachineTimeout(), alarmReg1, alarmReg2, alarmReg3, alarmReg4, alarmReg5, alarmReg6));
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			// read robot service 
			int robotServiceInputs = (cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv2.STATUS_SLOT_1, 1)).get(0);
			getMCodeAdapter().updateRobotServiceInputsDevIntv2(robotServiceInputs);
		}
	}
	
	@Override
	public void reset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		command = command | CNCMachineConstantsDevIntv2.IPC_RESET_CMD;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.IPC_COMMAND, registers);
		setCncMachineTimeout(null);
	}
	
	@Override
	public void nCReset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, DeviceActionException, SocketWrongResponseException {
		int command = 0;
		resetStatusValue(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_NC_RESET_OK);
		int maxResetTime = cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv2.PAR_MACHINE_MAX_NC_RESET_TIME, 1).get(0);
		command = command | CNCMachineConstantsDevIntv2.IPC_NC_RESET_CMD;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.IPC_COMMAND, registers);
		try {
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_NC_RESET_OK, maxResetTime);
		} catch (DeviceActionException e) {
			e.printStackTrace();
		}
		Thread.sleep(SLEEP_TIME_AFTER_RESET);
	}

	@Override
	public void powerOff() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		// Direct power off command
		command = command | CNCMachineConstantsDevIntv2.OUT_MACHINE_POWER_OFF;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.OUTPUT_SLOT_1, registers);
		// normally no more commands after this, so multiple IPC requests problem can't occur
	}

	@Override
	public void indicateAllProcessed() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, DeviceActionException, SocketWrongResponseException {
		// Still something todo?
		nCReset();
		int[] registers = {FINISH_BLUE_LAMP_VAL};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.PAR_MACHINE_BLUE_LAMP, registers);
	}

	@Override
	public void indicateOperatorRequested(final boolean requested) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		if (requested) {
			command = OPERATOR_RQST_BLUE_LAMP_VAL;
		}
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.PAR_MACHINE_BLUE_LAMP, registers);
	}
	
	@Override
	public void clearIndications() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		// reset blue lamp
		indicateOperatorRequested(false);
	}
	
	@Override
	public void prepareForProcess(final ProcessFlow process)  throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		// check work area
		for(WorkArea wa: getWorkAreas()) {
			wa.resetNbPossibleWPPerClamping(getWayOfOperating().getNbOfSides());
			wa.setNbUsedClampings(getWayOfOperating().getNbOfSides() * wa.getNbActiveClampingsEachSide());
		}
		//FIXME review! potential problems with reset in double processflow execution
		clearIndications();		
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			// wait half a second
			Thread.sleep(500);
			
			int selectionCommand = 0;
			int zoneNr = this.getWorkAreas().get(0).getZone().getZoneNr();
			if(zoneNr == 1) {
				selectionCommand = selectionCommand |  CNCMachineConstantsDevIntv2.ZONE1_SELECT;
			} else if (zoneNr == 2) {
				selectionCommand = selectionCommand |  CNCMachineConstantsDevIntv2.ZONE2_SELECT;
			} else {
				throw new IllegalArgumentException("Unknown zone number: " + zoneNr);
			}
			int startCommand = 0;
			startCommand = startCommand | CNCMachineConstantsDevIntv2.IPC_START_CNC_CMD;
			
			int[] registers = {selectionCommand, startCommand};
			cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.ZONE_WA_FIX_SELECT, registers);
		}
	}

	@Override
	public void startCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws SocketResponseTimedOutException, SocketDisconnectedException, DeviceActionException, InterruptedException, SocketWrongResponseException {
		if (getWayOfOperating() == EWayOfOperating.START_STOP) {
			// check a valid workarea is selected 
			if (!getWorkAreaNames().contains(startCylusSettings.getWorkArea().getName())) {
				throw new IllegalArgumentException("Unknown workarea: " + startCylusSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
			}
			resetStatusValue(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_START_CNC_OK);
			int command = 0;
			command = command | selectZone(startCylusSettings);
			int startCommand = 0 | CNCMachineConstantsDevIntv2.IPC_START_CNC_CMD;
			int[] registers = {command, startCommand};
			cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.ZONE_WA_FIX_SELECT, registers);

			// fix for jametal, comment next lines and uncomment sleep
			boolean cycleStartReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_START_CNC_OK, START_CYCLE_TIMEOUT);
			if (!cycleStartReady) {
				setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CYCLE_NOT_STARTED_TIMEOUT));
				waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_START_CNC_OK);
				setCncMachineTimeout(null);
			}
			if(startCylusSettings.getWorkArea().getZone().getZoneNr() == 1) {	
				waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.STATUS_SLOT_1, CNCMachineConstantsDevIntv2.R_MACHINE_ZONE1_PROCESSING);
				waitForStatusGoneDevIntv2(CNCMachineConstantsDevIntv2.STATUS_SLOT_1, CNCMachineConstantsDevIntv2.R_MACHINE_ZONE1_PROCESSING);
			} else if(startCylusSettings.getWorkArea().getZone().getZoneNr() == 2) {
				waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.STATUS_SLOT_1, CNCMachineConstantsDevIntv2.R_MACHINE_ZONE2_PROCESSING);
				waitForStatusGoneDevIntv2(CNCMachineConstantsDevIntv2.STATUS_SLOT_1, CNCMachineConstantsDevIntv2.R_MACHINE_ZONE2_PROCESSING);
			} else {
				throw new IllegalArgumentException("Unknown zone number: " + startCylusSettings.getWorkArea().getZone().getZoneNr());
			}	
	//		nCReset();
		} else if (getWayOfOperating() == EWayOfOperating.M_CODES) {
			int mCodeLoad = getMCodeIndex(startCylusSettings.getWorkArea(), true);
			// we sign of the m code for put
			finishMCode(mCodeLoad);
			Thread.sleep(500);
			int mCodeUnLoad = getMCodeIndex(startCylusSettings.getWorkArea(), false);
			waitForMCodes(mCodeUnLoad);
		}
		// Twee kanten (spiegel - we moeten dus eigenlijk 2 cycli door)
		else if (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD) {
			// we sign off the m code for put
			int mCodeLoad = getMCodeIndex(startCylusSettings.getWorkArea(), true);
			finishMCode(mCodeLoad);
			Thread.sleep(5000);	// wait 5 sec before checking again for m-code
			// we wait for unloading - in the meantime the just loaded piece has gone to the back for processing (kanteltafel)
			//FIXME- check this... what if we have reversals??? - is this the correct mCode to wait for?
			int mCodeUnLoad = getMCodeIndex(startCylusSettings.getWorkArea(), false);
			waitForMCodes(mCodeUnLoad);
			// now wait for next load and unload M-code
			// now wait for next load and unload M-code (last unload code is part of this cycle)
//			if (startCylusSettings.getWorkPieceType().equals(WorkPiece.Type.FINISHED) && startCylusSettings.getStep().getProcessFlow().hasReversalUnit()) {
			if (startCylusSettings.getWorkArea().getPrioIfCloned() == startCylusSettings.getStep().getProcessFlow().getNbCNCInFlow()) {
				mCodeLoad = getMCodeIndex(startCylusSettings.getWorkArea(), true);
				waitForMCodes(mCodeLoad);
				// We finish m-c load reversal because no load is suppose to come anymore
				if (startCylusSettings.getStep().getProcessFlow().getFinishedAmount() == startCylusSettings.getStep().getProcessFlow().getTotalAmount() - 1) {
					//TODO - afmelden enkel bij laatste CNC machine in de flow
					finishMCode(mCodeLoad);
				}
			} else {
				mCodeLoad = getMCodeIndex(startCylusSettings.getWorkArea(), true);
				waitForMCodes(mCodeLoad);
			}
			// we should finish this M-code if in teach mode (we only use 1 side)
			if ((startCylusSettings.getStep().getProcessFlow().getMode() == Mode.TEACH) || (startCylusSettings.getStep().getProcessFlow().getTotalAmount() <= 1)) {
				// first open fixtures 
				unclampAfterFinish(startCylusSettings.getWorkArea());
				// then finish m-code
				mCodeLoad = getMCodeIndex(startCylusSettings.getWorkArea(), true);
				finishMCode(mCodeLoad);
				Thread.sleep(500);
			}
			waitForNoMCode(mCodeLoad);
			mCodeUnLoad = getMCodeIndex(startCylusSettings.getWorkArea(), false);
			waitForMCodes(mCodeUnLoad);
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
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
			waitForMCodes(mCodeUnLoad);
		}
		
		if (pickSettings.getMachineAirblow()) {
			logger.debug("Set machine airblow for pick");
			setValue(CNCMachineConstantsDevIntv2.CONFIG_4, CNCMachineConstantsDevIntv2.CFG_FIX_1_AIRBLOW_PICK);
			setValue(CNCMachineConstantsDevIntv2.CONFIG_5, CNCMachineConstantsDevIntv2.CFG_FIX_2_AIRBLOW_PICK);
			setValue(CNCMachineConstantsDevIntv2.CONFIG_6, CNCMachineConstantsDevIntv2.CFG_FIX_3_AIRBLOW_PICK);
			setValue(CNCMachineConstantsDevIntv2.CONFIG_7, CNCMachineConstantsDevIntv2.CFG_FIX_4_AIRBLOW_PICK);
		} else {
			logger.debug("Reset machine airblow for pick");
			resetValue(CNCMachineConstantsDevIntv2.CONFIG_4, CNCMachineConstantsDevIntv2.CFG_FIX_1_AIRBLOW_PICK);
			resetValue(CNCMachineConstantsDevIntv2.CONFIG_5, CNCMachineConstantsDevIntv2.CFG_FIX_2_AIRBLOW_PICK);
			resetValue(CNCMachineConstantsDevIntv2.CONFIG_6, CNCMachineConstantsDevIntv2.CFG_FIX_3_AIRBLOW_PICK);
			resetValue(CNCMachineConstantsDevIntv2.CONFIG_7, CNCMachineConstantsDevIntv2.CFG_FIX_4_AIRBLOW_PICK);
		}
		
		resetStatusValue(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PICK_OK);
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(pickSettings);
		fixSelectCommand = fixSelectCommand | selectWorkArea(pickSettings);
		fixSelectCommand = fixSelectCommand | selectFixture(pickSettings.getWorkArea().getActiveClamping(true).getFixtureType());
		int command2 = 0 | CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PICK_CMD;
		
		int[] registers = {fixSelectCommand, command2};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.ZONE_WA_FIX_SELECT, registers);

		boolean pickReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PICK_OK, PREPARE_PICK_TIMEOUT);
		if(!pickReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PICK_OK);
			setCncMachineTimeout(null);
		}
		
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}		
		// if way of operation is m codes, await loading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int mCodeLoad = getMCodeIndex(putSettings.getWorkArea(), true);
			waitForMCodes(mCodeLoad);
		}
		
		if (putSettings.getMachineAirblow()) {
			logger.debug("Set machine airblow for put");
			setValue(CNCMachineConstantsDevIntv2.CONFIG_4, CNCMachineConstantsDevIntv2.CFG_FIX_1_AIRBLOW_PUT);
			setValue(CNCMachineConstantsDevIntv2.CONFIG_5, CNCMachineConstantsDevIntv2.CFG_FIX_2_AIRBLOW_PUT);
			setValue(CNCMachineConstantsDevIntv2.CONFIG_6, CNCMachineConstantsDevIntv2.CFG_FIX_3_AIRBLOW_PUT);
			setValue(CNCMachineConstantsDevIntv2.CONFIG_7, CNCMachineConstantsDevIntv2.CFG_FIX_4_AIRBLOW_PUT);
		} else {
			logger.debug("Reset machine airblow for put");
			resetValue(CNCMachineConstantsDevIntv2.CONFIG_4, CNCMachineConstantsDevIntv2.CFG_FIX_1_AIRBLOW_PUT);
			resetValue(CNCMachineConstantsDevIntv2.CONFIG_5, CNCMachineConstantsDevIntv2.CFG_FIX_2_AIRBLOW_PUT);
			resetValue(CNCMachineConstantsDevIntv2.CONFIG_6, CNCMachineConstantsDevIntv2.CFG_FIX_3_AIRBLOW_PUT);
			resetValue(CNCMachineConstantsDevIntv2.CONFIG_7, CNCMachineConstantsDevIntv2.CFG_FIX_4_AIRBLOW_PUT);
		}
		
		resetStatusValue(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PUT_OK);
		// Create prepare for put command
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(putSettings);
		fixSelectCommand = fixSelectCommand | selectWorkArea(putSettings);
		fixSelectCommand = fixSelectCommand | selectFixture(putSettings.getWorkArea().getActiveClamping(false).getFixtureType());
		int command2 = 0 | CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PUT_CMD;
		int[] registers = {fixSelectCommand, command2};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.ZONE_WA_FIX_SELECT, registers);
		
		boolean putReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PUT_OK, PREPARE_PUT_TIMEOUT);
		if(!putReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_PREPARE_FOR_PUT_OK);
			setCncMachineTimeout(null);
		}
	}

	@Override
	public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		resetStatusValue(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_UNCLAMP_OK);
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(pickSettings);
		fixSelectCommand = fixSelectCommand | selectWorkArea(pickSettings);
		fixSelectCommand = fixSelectCommand | selectFixture(pickSettings.getWorkArea().getActiveClamping(true).getFixtureType());
		int actionCommand = 0 | CNCMachineConstantsDevIntv2.IPC_UNCLAMP_CMD;
		
		int[] registers = {fixSelectCommand, actionCommand};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.ZONE_WA_FIX_SELECT, registers);
		
		boolean unclampReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_UNCLAMP_OK, UNCLAMP_TIMEOUT);
		if(!unclampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_UNCLAMP_OK);
			setCncMachineTimeout(null);
		}
	}

	@Override
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		resetStatusValue(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_CLAMP_OK);
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(putSettings);
		fixSelectCommand = fixSelectCommand | selectWorkArea(putSettings);
		fixSelectCommand = fixSelectCommand | selectFixture(putSettings.getWorkArea().getActiveClamping(false).getFixtureType());
		int actionCommand = 0 | CNCMachineConstantsDevIntv2.IPC_CLAMP_CMD;
		
		int[] registers = {fixSelectCommand, actionCommand};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.ZONE_WA_FIX_SELECT, registers);
		
		boolean clampReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_CLAMP_OK, CLAMP_TIMEOUT);
		if(!clampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_CLAMP_OK);
			setCncMachineTimeout(null);
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
				// last work piece: send reset in stead of finishing m code
				unclampAfterFinish(pickSettings.getWorkArea());
				nCReset();
				// also finish m code if still active after nc reset
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
	
	private int selectZone(final AbstractDeviceActionSettings<?> deviceActionSettings) throws IllegalArgumentException {
		return selectZoneByWorkArea(deviceActionSettings.getWorkArea());
	}
	
	private int selectZoneByWorkArea(final WorkArea workArea) {
		int command = 0;
		if(workArea.getZone().getZoneNr() == 1) {
			command = command | CNCMachineConstantsDevIntv2.ZONE1_SELECT;
		} else if (workArea.getZone().getZoneNr() == 2) {
			command = command | CNCMachineConstantsDevIntv2.ZONE2_SELECT;
		} else {
			throw new IllegalArgumentException("Unknown zone number: " + workArea.getZone().getZoneNr());
		}
		return command;
	}
	
	private int selectWorkArea(final AbstractDeviceActionSettings<?> deviceActionSettings) throws IllegalArgumentException {
		return selectWorkAreaByWorkArea(deviceActionSettings.getWorkArea());
	}
	
	private int selectWorkAreaByWorkArea(final WorkArea workArea) {
		int command = 0;
		if(workArea.getWorkAreaNr() == 1) {
			command = command | CNCMachineConstantsDevIntv2.WA1_SELECT;
		} else if (workArea.getWorkAreaNr() == 2) {
			command = command | CNCMachineConstantsDevIntv2.WA2_SELECT;
		} else {
			throw new IllegalArgumentException("Unknown workarea number: " + workArea.getWorkAreaNr());
		}
		return command;
	}
	
	private int selectFixture(final EFixtureType fixtureType) throws IllegalArgumentException {
		int command = 0;
		switch(fixtureType) {
		case FIXTURE_1:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_1;
			break;
		case FIXTURE_2:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_2;
			break;
		case FIXTURE_3:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_3;
			break;
		case FIXTURE_4:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_4;
			break;	
		case FIXTURE_1_2:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_2;
			break;	
		case FIXTURE_1_3:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_3;
			break;	
		case FIXTURE_1_4:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_4;
			break;	
		case FIXTURE_2_3:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_2;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_3;
			break;	
		case FIXTURE_2_4:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_2;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_4;
			break;
		case FIXTURE_3_4:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_3;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_4;
			break;
		case FIXTURE_1_2_3:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_2;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_3;
			break;
		case FIXTURE_1_2_3_4:
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_2;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_3;
			command = command | CNCMachineConstantsDevIntv2.FIX_SELECT_4;
			break;
		default:
			break;
		}
		return command;
	}
	
	private int selectAllFixtures(final WorkArea workArea) {
		int command = 0;
		for (Clamping clamping: workArea.getClampings()) {
			command = command | selectFixture(clamping.getFixtureType());
		}
		return command;
	}
	
	private void resetStatusValue(final int registerNr, final int value) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, DeviceActionException, SocketWrongResponseException {
		// Read current status from register
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if((currentStatus & value) > 0) {
			// Exclusive OR operation 
			int resultValue = currentStatus ^ value;
			int[] registerValue = {resultValue};
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
			waitForStatusGoneDevIntv2(registerNr, value);
		}
	}
	
	private void resetValue(final int registerNr, final int value) throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException {
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if((currentStatus & value) > 0) {
			// Exclusive OR operation 
			int resultValue = currentStatus ^ value;
			int[] registerValue = {resultValue};
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
		}
	}
	
	private void setValue(final int registerNr, final int value) throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException {
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if((currentStatus & value) == 0) {
			// bitwise OR operation 
			int resultValue = currentStatus | value;
			int[] registerValue = {resultValue};
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
		}
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
		return true;
	}
	
	private void unclampAfterFinish(final WorkArea workArea) 
			throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
		resetStatusValue(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_UNCLAMP_OK);
		
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZoneByWorkArea(workArea);
		fixSelectCommand = fixSelectCommand | selectWorkAreaByWorkArea(workArea);
		fixSelectCommand = fixSelectCommand | selectAllFixtures(workArea);
		int actionCommand = 0 | CNCMachineConstantsDevIntv2.IPC_UNCLAMP_CMD;
		
		int[] registers2 = {fixSelectCommand, actionCommand};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.ZONE_WA_FIX_SELECT, registers2);
		
		boolean clampReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_UNCLAMP_OK, UNCLAMP_TIMEOUT);
		if(!clampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv2.IPC_OK, CNCMachineConstantsDevIntv2.IPC_UNCLAMP_OK);
			setCncMachineTimeout(null);
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
		//TODO - use finish for all
		int command = 0;
		if (robotServiceOutputs.contains(0)) {
			logger.info("FINISH M CODE " + mCodeIndex);
			command = command | CNCMachineConstantsDevIntv2.IPC_MC_FINISH_CMD;
			int[] registers = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv2.IPC_COMMAND, registers);
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
	private boolean isLastWorkPiece(final DevicePickSettings pickSettings) {
		if (pickSettings.getStep().getProcessFlow().getType().equals(ProcessFlow.Type.CONTINUOUS)) {
			return false;
		}
		//laatste bewerking (workPieceType.FINISHED) - da gaat nog altijd normaal... de laatste zou niet mogen veranderen 
		if (pickSettings.getStep().getRobotSettings().getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
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
