package eu.robojob.millassist.db.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.ConnectionManager;
import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnitSettings;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorSettings;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPalletDeviceSettings;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.external.robot.Gripper;
import eu.robojob.millassist.external.robot.GripperBody;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.external.robot.RobotManager;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.external.robot.RobotProcessingWhileWaitingSettings;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.external.robot.RobotSettings;
import eu.robojob.millassist.external.robot.fanuc.FanucRobot;
import eu.robojob.millassist.external.robot.fanuc.FanucRobotPickSettings;
import eu.robojob.millassist.external.robot.fanuc.FanucRobotPutSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.DeviceStep;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickAfterWaitStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessingStep;
import eu.robojob.millassist.process.ProcessingWhileWaitingStep;
import eu.robojob.millassist.process.PutAndWaitStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.RobotStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public class ProcessFlowMapper {

	private static final int STEP_TYPE_PICK = 1;
	private static final int STEP_TYPE_PUT = 2;
	private static final int STEP_TYPE_PROCESSING = 3;
	private static final int STEP_TYPE_INTERVENTION = 4;
	private static final int STEP_TYPE_PUTANDWAIT = 5;
	private static final int STEP_TYPE_PICKAFTERWAIT = 6;
	private static final int STEP_TYPE_PROCESSINGWHILEWAITING = 7;
	
	private static final int CLAMPING_MANNER_LENGTH = 1;
	private static final int CLAMPING_MANNER_WIDTH = 2;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowMapper.class.getName());
	
	private DeviceManager deviceManager;
	private RobotManager robotManager;
	private GeneralMapper generalMapper;
	
	public ProcessFlowMapper(final GeneralMapper generalMapper, final DeviceManager deviceManager, final RobotManager robotManager) {
		this.generalMapper = generalMapper;
		this.deviceManager = deviceManager;
		this.robotManager = robotManager;
	} 
	
	public void updateLastOpened(final ProcessFlow processFlow) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE PROCESSFLOW SET LASTOPENED = ? WHERE ID = ?");
		stmt.setTimestamp(1, processFlow.getLastOpened());
		stmt.setInt(2, processFlow.getId());
		stmt.executeUpdate();
	}
	
	public List<ProcessFlow> getAllProcessFlows() throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM PROCESSFLOW ORDER BY LASTOPENED DESC");
		ResultSet results = stmt.executeQuery();
		List<ProcessFlow> processFlows = new ArrayList<ProcessFlow>();
		while (results.next()) {
			int id = results.getInt("ID");
			ProcessFlow processFlow = getLightWeightedProcessFlowById(id);
			processFlows.add(processFlow);
		}
		return processFlows;
	}
	
	public List<ProcessFlow> getLastOpenedProcessFlows(final int amount) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM PROCESSFLOW ORDER BY LASTOPENED DESC");
		stmt.setMaxRows(amount);
		ResultSet results = stmt.executeQuery();
		List<ProcessFlow> processFlows = new ArrayList<ProcessFlow>();
		while (results.next()) {
			int id = results.getInt("ID");
			ProcessFlow processFlow = getProcessFlowById(id);
			processFlows.add(processFlow);
		}
		return processFlows;
	}
	
	public void updateProcessFlow(final ProcessFlow processFlow) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE PROCESSFLOW SET NAME = ?, LASTOPENED = ?, CLAMPING_MANNER = ? , SINGLE_CYCLE = ? WHERE ID = ?");
		stmt.setString(1, processFlow.getName());
		stmt.setTimestamp(2, processFlow.getLastOpened());
		int clampingMannerId = CLAMPING_MANNER_LENGTH;
		if (processFlow.getClampingType().getType() == ClampingManner.Type.WIDTH) {
			clampingMannerId = CLAMPING_MANNER_WIDTH;
		}
		stmt.setInt(3, clampingMannerId);
		stmt.setBoolean(4, processFlow.isSingleCycle());
		stmt.setInt(5, processFlow.getId());
		try {
			stmt.executeUpdate();
			deleteStepsAndSettings(processFlow);
			clearProcessFlowStepsSettingsAndReferencedIds(processFlow);
			saveProcessFlowStepsAndSettings(processFlow);
			ConnectionManager.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
			ConnectionManager.getConnection().rollback();
		}
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void saveProcessFlow(final ProcessFlow processFlow) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		//We will clear all the IDs given by the software and assign new IDs that match with the ID given by the Database manager
		clearProcessFlowStepsSettingsAndReferencedIds(processFlow);
		processFlow.setCreation(new Timestamp(System.currentTimeMillis()));
		processFlow.setLastOpened(new Timestamp(System.currentTimeMillis()));
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO PROCESSFLOW (NAME, CREATION, LASTOPENED, CLAMPING_MANNER, SINGLE_CYCLE) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, processFlow.getName());
		stmt.setTimestamp(2, processFlow.getCreation());
		stmt.setTimestamp(3, processFlow.getLastOpened());
		int clampingMannerId = CLAMPING_MANNER_LENGTH;
		if (processFlow.getClampingType().getType() == ClampingManner.Type.WIDTH) {
			clampingMannerId = CLAMPING_MANNER_WIDTH;
		}
		stmt.setInt(4, clampingMannerId);
		stmt.setBoolean(5, processFlow.isSingleCycle());
		try {
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				clearIds(processFlow);
				processFlow.setId(resultSet.getInt(1));
				saveProcessFlowStepsAndSettings(processFlow);
				ConnectionManager.getConnection().commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			ConnectionManager.getConnection().rollback();
		}
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	private void clearIds(final ProcessFlow processFlow) {
		processFlow.setId(0);
		for (DeviceSettings deviceSettings : processFlow.getDeviceSettings().values()) {
			deviceSettings.setId(0);
			if (deviceSettings instanceof AbstractStackPlateDeviceSettings) {
				((AbstractStackPlateDeviceSettings) deviceSettings).getFinishedWorkPiece().setId(0);
				((AbstractStackPlateDeviceSettings) deviceSettings).getRawWorkPiece().setId(0);
			}
		}
		for (RobotSettings robotSettings : processFlow.getRobotSettings().values()) {
			robotSettings.setId(0);
		}
		for (AbstractProcessStep step : processFlow.getProcessSteps()) {
			step.setId(0);
		}
	}
	
	public void deleteProcessFlow(final ProcessFlow processFlow) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		generalMapper.clearBuffers(processFlow.getId());
		deleteStepsAndSettings(processFlow);
		clearProcessFlowStepsSettingsAndReferencedIds(processFlow);
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM PROCESSFLOW WHERE ID = ?");
		stmt.setInt(1, processFlow.getId());
		stmt.executeUpdate();
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	private void deleteStepsAndSettings(final ProcessFlow processFlow) throws SQLException {
		// delete all coordinates and work pieces (these are not cascaded)
		// delete all coordinates
		PreparedStatement stmtgetCoordinatesToDelete = ConnectionManager.getConnection().prepareStatement(""  
                + "		(select step_teachedcoordinates.coordinates from step_teachedcoordinates "
                + "			JOIN step "
                + "			ON step.id = step_teachedcoordinates.step "
                + "			where step.processflow=?"
                + "		) " 	
                + " 		union "																						
                + "		(select robotactionsettings.smoothpoint from robotactionsettings "
                + "			JOIN step "
                + "			ON step.id = robotactionsettings.step "
                + "			where step.processflow=?"
                + "		) "
				);	
		stmtgetCoordinatesToDelete.setInt(1, processFlow.getId());
		stmtgetCoordinatesToDelete.setInt(2, processFlow.getId());
		ResultSet resultCoordinates = stmtgetCoordinatesToDelete.executeQuery();
		while(resultCoordinates.next()) {
			deleteCoordinate(resultCoordinates.getInt(1));
		}
		// delete all work pieces (it suffices to delete the work pieces from the pick setting
		PreparedStatement stmtGetWorkPiecesToDelete = ConnectionManager.getConnection().prepareStatement("" 	
				+ "select distinct workpiece from robotpicksettings "
				+ "join robotactionsettings "
				+ "on robotactionsettings.id = robotpicksettings.id "
				+ "join step "
				+ "on robotactionsettings.step = step.id "
				+ "where step.processflow=?"
				+ "");
		stmtGetWorkPiecesToDelete.setInt(1, processFlow.getId());
		ResultSet resultset =stmtGetWorkPiecesToDelete.executeQuery();
		while(resultset.next()) {
			deleteWorkPiece(resultset.getInt("workpiece"));
		}
		
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM DEVICESETTINGS WHERE PROCESSFLOW = ?");
		stmt.setInt(1, processFlow.getId());
		stmt.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("DELETE FROM ROBOTSETTINGS WHERE PROCESSFLOW = ?");
		stmt2.setInt(1, processFlow.getId());
		stmt2.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		PreparedStatement stmt3 = ConnectionManager.getConnection().prepareStatement("DELETE FROM STEP WHERE PROCESSFLOW = ?");
		stmt3.setInt(1, processFlow.getId());
		stmt3.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
	}

	private void deleteWorkPiece(Integer workPieceId) throws SQLException {
		PreparedStatement stmtDeleteCoordinates = ConnectionManager.getConnection().prepareStatement("delete from workpiece where id=?");
		stmtDeleteCoordinates.setInt(1, workPieceId);
		stmtDeleteCoordinates.executeUpdate();
	}
	
	private void deleteCoordinate(Integer coordinateId) throws SQLException {
		PreparedStatement stmtDeleteCoordinates = ConnectionManager.getConnection().prepareStatement("delete from coordinates where id=?");
		stmtDeleteCoordinates.setInt(1, coordinateId);
		stmtDeleteCoordinates.executeUpdate();
	}
	
	private void clearProcessFlowStepsSettingsAndReferencedIds(final ProcessFlow processFlow) {
		for (AbstractProcessStep step : processFlow.getProcessSteps()) {
			if (step instanceof PickStep) {
				PickStep pStep = (PickStep) step;
				pStep.getRobotSettings().getWorkPiece().setId(0);
			}
			if (step instanceof RobotStep) {
				AbstractRobotActionSettings<?> robotActionSettings = ((RobotStep) step).getRobotSettings();
				if (robotActionSettings.getSmoothPoint() != null) {
					robotActionSettings.getSmoothPoint().setId(0);
				}
			}
			if (step instanceof AbstractTransportStep) {
				AbstractTransportStep trStep = (AbstractTransportStep) step;
				if (trStep.getRelativeTeachedOffset() != null) {
					trStep.getRelativeTeachedOffset().setId(0);
				}
			}
		}
	}
	
	private void saveProcessFlowStepsAndSettings(final ProcessFlow processFlow) throws SQLException {
		int index = 1;
		for (AbstractProcessStep step : processFlow.getProcessSteps()) {
			saveStep(step, index);
			index++;
		}
		for (Entry<AbstractDevice, DeviceSettings> entry : processFlow.getDeviceSettings().entrySet()) {
			saveDeviceSettings(processFlow.getId(), entry.getKey(), entry.getValue());
		}
		for (Entry<AbstractRobot, RobotSettings> entry : processFlow.getRobotSettings().entrySet()) {
			saveRobotSettings(processFlow.getId(), entry.getKey(), entry.getValue());
		}
	}
	
	private void saveStep(final AbstractProcessStep step, final int index) throws SQLException {
		int type = 0;
		if (step instanceof PickAfterWaitStep) {	// note: these have to go first!
			type = STEP_TYPE_PICKAFTERWAIT;
		} else if (step instanceof PutAndWaitStep) {
			type = STEP_TYPE_PUTANDWAIT;
		} else if (step instanceof ProcessingWhileWaitingStep) {
			type = STEP_TYPE_PROCESSINGWHILEWAITING;
		} else if (step instanceof PutStep) {
			type = STEP_TYPE_PUT;
		} else if (step instanceof PickStep) {
			type = STEP_TYPE_PICK;
		} else if (step instanceof InterventionStep) {
			type = STEP_TYPE_INTERVENTION;
		} else if (step instanceof ProcessingStep) {
			type = STEP_TYPE_PROCESSING;
		} else {
			throw new IllegalStateException("Unknown step type: " + step);
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO STEP (PROCESSFLOW, TYPE, INDEX) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, step.getProcessFlow().getId());
		stmt.setInt(2, type);
		stmt.setInt(3, index);
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if ((keys != null) && (keys.next())) {
			step.setId(keys.getInt(1));
		}
		if (step instanceof DeviceStep) {
			saveDeviceActionSettings((DeviceStep) step);
		}
		if (step instanceof RobotStep) {
			saveRobotActionSettings((RobotStep) step);
		}
		if (step instanceof InterventionStep) {
			InterventionStep iStep = (InterventionStep) step;
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO INTERVENTIONSTEP (ID, FREQUENCY) VALUES (?, ?)");
			stmt2.setInt(1, step.getId());
			stmt2.setInt(2, iStep.getFrequency());
			stmt2.executeUpdate();
		}
		if (step instanceof AbstractTransportStep) {
			AbstractTransportStep transportStep = (AbstractTransportStep) step;
			if (transportStep.getRobotSettings().getGripperHead().getGripper().isFixedHeight() && (transportStep.getRelativeTeachedOffset() != null)) {
				generalMapper.saveCoordinates(transportStep.getRelativeTeachedOffset());
				PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO STEP_TEACHEDCOORDINATES (STEP, COORDINATES) VALUES (?, ?)");
				stmt2.setInt(1, step.getId());
				stmt2.setInt(2, transportStep.getRelativeTeachedOffset().getId());
				stmt2.executeUpdate();
			}
		}
	}
	
	private void saveDeviceActionSettings(final DeviceStep deviceStep) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO DEVICEACTIONSETTINGS (DEVICE, WORKAREA, STEP, MACHINE_AIRBLOW, WORKNUMBER_SEARCH) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, deviceStep.getDevice().getId());
		stmt.setInt(2, deviceStep.getDeviceSettings().getWorkArea().getId());
		stmt.setInt(3, ((AbstractProcessStep) deviceStep).getId());
		if (deviceStep.getDeviceSettings() instanceof DevicePutSettings) {
			stmt.setBoolean(4, ((DevicePutSettings) deviceStep.getDeviceSettings()).getMachineAirblow()); 
		} else if (deviceStep.getDeviceSettings() instanceof DevicePickSettings) {
			stmt.setBoolean(4, ((DevicePickSettings) deviceStep.getDeviceSettings()).getMachineAirblow()); 
		} else {
			stmt.setBoolean(4, false);
		}
		if (deviceStep.getDeviceSettings() instanceof ProcessingDeviceStartCyclusSettings) {
		    stmt.setInt(5, ((ProcessingDeviceStartCyclusSettings) deviceStep.getDeviceSettings()).getWorkNumber());
		} else {
		    stmt.setInt(5, -1);
		}
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if ((keys != null) && (keys.next())) {
			deviceStep.getDeviceSettings().setId(keys.getInt(1));
		}
	}
	
	private void saveRobotActionSettings(final RobotStep robotStep) throws SQLException {
		if (robotStep.getRobotSettings().getSmoothPoint() != null) {
			generalMapper.saveCoordinates(robotStep.getRobotSettings().getSmoothPoint());
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTACTIONSETTINGS (STEP, GRIPPERHEAD, SMOOTHPOINT, ROBOT, GRIPINNER) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, ((AbstractProcessStep) robotStep).getId());
		stmt.setInt(2, robotStep.getRobotSettings().getGripperHead().getId());
		if (robotStep.getRobotSettings().getSmoothPoint() != null) {
			stmt.setInt(3, robotStep.getRobotSettings().getSmoothPoint().getId());
		} else {
			stmt.setNull(3, java.sql.Types.INTEGER);
		}
		stmt.setInt(4, robotStep.getRobot().getId());
		stmt.setBoolean(5, robotStep.getRobotSettings().isGripInner());
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if ((keys != null) && (keys.next())) {
			robotStep.getRobotSettings().setId(keys.getInt(1));
		}
		if (robotStep instanceof PickStep) {
			RobotPickSettings robotPickSettings = ((PickStep) robotStep).getRobotSettings();
			generalMapper.saveWorkPiece(robotPickSettings.getWorkPiece());
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTPICKSETTINGS (ID, WORKPIECE, AIRBLOW, APPROACHTYPE, TURN_IN_MACHINE) VALUES (?, ?, ?, ?, ?)");
			stmt2.setInt(1, robotStep.getRobotSettings().getId());
			stmt2.setInt(2, robotPickSettings.getWorkPiece().getId());
			stmt2.setBoolean(3, robotPickSettings.isRobotAirblow());
			stmt2.setInt(4, robotPickSettings.getApproachType().getId());
			stmt2.setBoolean(5, robotPickSettings.getTurnInMachine());
			stmt2.executeUpdate();
			saveRobotAirblowSettings(robotPickSettings);
		} else if (robotStep instanceof PutStep) {
			RobotPutSettings robotPutSettings = ((PutStep) robotStep).getRobotSettings();
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTPUTSETTINGS (ID, AIRBLOW, RELEASEBEFORE, APPROACHTYPE, TURN_IN_MACHINE) VALUES (?, ?, ?, ?, ?)");
			stmt2.setInt(1, robotStep.getRobotSettings().getId());
			stmt2.setBoolean(2, robotPutSettings.isRobotAirblow());
			stmt2.setBoolean(3, ((PutStep) robotStep).getRobotSettings().isReleaseBeforeMachine());
			stmt2.setInt(4, robotPutSettings.getApproachType().getId());
			stmt2.setBoolean(5, robotPutSettings.getTurnInMachine());
			stmt2.executeUpdate();
			saveRobotAirblowSettings(robotPutSettings);
		}
	}
	
	private void saveRobotAirblowSettings(final AbstractRobotActionSettings<?> robotActionSettings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTAIRBLOWSETTINGS (ID, CLAMPING, BOTTOMCOORD, TOPCOORD) VALUES (?, ?, ?, ?)");
		for (Integer clampingId: robotActionSettings.getRobotAirblowSettings().keySet()) {
			AirblowSquare tmpAirblowSettings = robotActionSettings.getRobotAirblowSettings().get(clampingId);
			generalMapper.saveCoordinates(tmpAirblowSettings.getBottomCoord());
			generalMapper.saveCoordinates(tmpAirblowSettings.getTopCoord());
			stmt.setInt(1, robotActionSettings.getId());
			stmt.setInt(2, clampingId);
			stmt.setInt(3, tmpAirblowSettings.getBottomCoord().getId());
			stmt.setInt(4, tmpAirblowSettings.getTopCoord().getId());
			stmt.executeUpdate();
		}
	}
	
	private void saveDeviceSettings(final int processFlowId, final AbstractDevice device, final DeviceSettings deviceSettings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO DEVICESETTINGS (DEVICE, PROCESSFLOW) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, device.getId());
		stmt.setInt(2, processFlowId);
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if ((keys != null) && (keys.next())) {
			deviceSettings.setId(keys.getInt(1));
		}
		for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM WORKAREA_CLAMPING WHERE WORKAREA = ? AND CLAMPING = ?");
			SimpleWorkArea workarea = entry.getKey();
			// We saven enkel de workArea's die in gebruik zijn
			if (workarea.isInUse() || !(device instanceof AbstractCNCMachine)) {
				stmt2.setInt(1, workarea.getWorkAreaManager().getId());
				stmt2.setInt(2, entry.getValue().getId());
				ResultSet results = stmt2.executeQuery();
				int id = 0;
				if (results.next()) {
					id = results.getInt("ID");
					PreparedStatement stmt3 = ConnectionManager.getConnection().prepareStatement("INSERT INTO DEVICESETTINGS_WORKAREA_CLAMPING (DEVICESETTINGS, WORKAREA_CLAMPING, DEFAULT_FL, WORKAREA) VALUES (?, ?, ?, ?)");
					stmt3.setInt(1, deviceSettings.getId());
					stmt3.setInt(2, id);
					stmt3.setBoolean(3, true);
					stmt3.setInt(4, workarea.getId());
					stmt3.executeUpdate();
					for(Clamping relClamping : entry.getValue().getRelatedClampings()) {
						stmt2.setInt(2, relClamping.getId());
						ResultSet results2 = stmt2.executeQuery();
						if(results2.next()) {
							stmt3.setInt(2, results2.getInt("ID"));
							stmt3.setBoolean(3, false);
							stmt3.setInt(4, workarea.getId());
							stmt3.executeUpdate();
						}
					}
				} else {
					throw new IllegalStateException("Couldn't find entry for workarea: [" + entry.getKey() + "] and clamping: [" + entry.getValue() + "].");
				}
			}
		}
		if (deviceSettings instanceof AbstractStackPlateDeviceSettings) {
			AbstractStackPlateDeviceSettings bspSettings = (AbstractStackPlateDeviceSettings) deviceSettings;
			generalMapper.saveWorkPiece(bspSettings.getRawWorkPiece());
			generalMapper.saveWorkPiece(bspSettings.getFinishedWorkPiece());
			PreparedStatement stmt4 = ConnectionManager.getConnection().prepareStatement("INSERT INTO STACKPLATESETTINGS (ID, AMOUNT, ORIENTATION, RAWWORKPIECE, FINISHEDWORKPIECE, LAYERS, STUDHEIGHT) VALUES (?, ?, ?, ?, ?, ?, ?)");
			stmt4.setInt(1, bspSettings.getId());
			stmt4.setInt(2, bspSettings.getAmount());
			stmt4.setFloat(3, bspSettings.getOrientation());
			stmt4.setInt(4, bspSettings.getRawWorkPiece().getId());
			stmt4.setInt(5, bspSettings.getFinishedWorkPiece().getId());
			stmt4.setInt(6,  bspSettings.getLayers());
			stmt4.setFloat(7,  bspSettings.getStudHeight());
			stmt4.executeUpdate();
			if(bspSettings.getGridId() > 0) {
				PreparedStatement stmt5 = ConnectionManager.getConnection().prepareStatement("UPDATE STACKPLATESETTINGS SET GRID_ID = ? WHERE ID = ?");
				stmt5.setInt(1, bspSettings.getGridId());
				stmt5.setInt(2, bspSettings.getId());
				stmt5.executeUpdate();
			}
		} else if (deviceSettings instanceof ConveyorSettings) {
			ConveyorSettings cSettings = (ConveyorSettings) deviceSettings;
			generalMapper.saveWorkPiece(cSettings.getRawWorkPiece());
			generalMapper.saveWorkPiece(cSettings.getFinishedWorkPiece());
			PreparedStatement stmt4 = ConnectionManager.getConnection().prepareStatement("INSERT INTO CONVEYORSETTINGS (ID, AMOUNT, RAWWORKPIECE, FINISHEDWORKPIECE, OFFSETSUPPORT1, OFFSETOTHERSUPPORTS) VALUES (?, ?, ?, ?, ?, ?)");
			stmt4.setInt(1, cSettings.getId());
			stmt4.setInt(2, cSettings.getAmount());
			stmt4.setInt(3, cSettings.getRawWorkPiece().getId());
			stmt4.setInt(4, cSettings.getFinishedWorkPiece().getId());
			stmt4.setFloat(5, cSettings.getOffsetSupport1());
			stmt4.setFloat(6, cSettings.getOffsetOtherSupports());
			stmt4.executeUpdate();
		} else if (deviceSettings instanceof eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) {
			eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings cSettings = (eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) deviceSettings;
			generalMapper.saveWorkPiece(cSettings.getRawWorkPiece());
			generalMapper.saveWorkPiece(cSettings.getFinishedWorkPiece());
			PreparedStatement stmt4 = ConnectionManager.getConnection().prepareStatement("INSERT INTO CONVEYOREATONSETTINGS (ID, AMOUNT, RAWWORKPIECE, FINISHEDWORKPIECE) VALUES (?, ?, ?, ?)");
			stmt4.setInt(1, cSettings.getId());
			stmt4.setInt(2, cSettings.getAmount());
			stmt4.setInt(3, cSettings.getRawWorkPiece().getId());
			stmt4.setInt(4, cSettings.getFinishedWorkPiece().getId());
			stmt4.executeUpdate();
		} else if (deviceSettings instanceof ReversalUnitSettings) {
			ReversalUnitSettings rSettings = (ReversalUnitSettings) deviceSettings;
			PreparedStatement stmt4 = ConnectionManager.getConnection().prepareStatement("INSERT INTO REVERSALUNITSETTINGS (ID, CONFIGWIDTH, SHIFTED_ORIGIN) VALUES (?, ?, ?)");
			stmt4.setInt(1, rSettings.getId());
			stmt4.setFloat(2, rSettings.getConfigWidth());
			stmt4.setBoolean(3, rSettings.isShiftedOrigin());
			stmt4.executeUpdate();
		} else if (deviceSettings instanceof UnloadPalletDeviceSettings) {
		    UnloadPalletDeviceSettings uSettings = (UnloadPalletDeviceSettings) deviceSettings;
            PreparedStatement stmt4 = ConnectionManager.getConnection().prepareStatement("INSERT INTO UNLOADPALLETSETTINGS (ID, FINISHEDWORKPIECE, LAYOUT_TYPE, LAYERS_CARDBOARD, PALLET_LAYOUT) VALUES (?, ?, ?, ?, ?)");
            stmt4.setInt(1, uSettings.getId());
            stmt4.setInt(2, uSettings.getFinishedWorkPiece().getId());
            stmt4.setInt(3, uSettings.getLayoutType().getId());
            stmt4.setInt(4, uSettings.getLayersBeforeCardBoard());
            stmt4.setInt(5, uSettings.getLayout().getId());
            stmt4.executeUpdate();
        }
	}
	
	private void saveRobotSettings(final int processFlowId, final AbstractRobot robot, final RobotSettings robotSettings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTSETTINGS (ROBOT, ACTIVEGRIPPERBODY, PROCESSFLOW) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, robot.getId());
		stmt.setInt(2, robotSettings.getGripperBody().getId());
		stmt.setInt(3, processFlowId);
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if ((keys != null) && (keys.next())) {
			robotSettings.setId(keys.getInt(1));
		}
		for (Entry<GripperHead, Gripper> entry : robotSettings.getGrippers().entrySet()) {
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM GRIPPERHEAD_GRIPPER WHERE GRIPPERHEAD = ? AND GRIPPER = ?");
			stmt2.setInt(1, entry.getKey().getId());
			if (entry.getValue() != null) {
				stmt2.setInt(2, entry.getValue().getId());
			}
			ResultSet results = stmt2.executeQuery();
			int id = 0;
			if (results.next()) {
				id = results.getInt("ID");
			} else {
				throw new IllegalStateException("Could not find entry for GripperHead: [" + entry.getKey() + "] and Gripper: [" + entry.getValue() + "].");
			}
			PreparedStatement stmt3 = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTSETTINGS_GRIPPERHEAD_GRIPPER(ROBOTSETTINGS, GRIPPERHEAD_GRIPPER) VALUES (?, ?)");
			stmt3.setInt(1, robotSettings.getId());
			stmt3.setInt(2, id);
			stmt3.executeUpdate();
		}
	}
	
	public ProcessFlow getProcessFlowById(final int id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM PROCESSFLOW WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		ProcessFlow processFlow = null;
		if (results.next()) {
			generalMapper.clearBuffers(id);
			String name = results.getString("NAME");
			Timestamp creation = results.getTimestamp("CREATION");
			Timestamp lastOpened = results.getTimestamp("LASTOPENED");
			int clampingMannerId = results.getInt("CLAMPING_MANNER");
			boolean isSingleCycle = results.getBoolean("SINGLE_CYCLE");
			List<AbstractProcessStep> processSteps = getProcessSteps(id);
			// We have 1 deviceSetting per device - per processFlow
			Map<AbstractDevice, DeviceSettings> deviceSettings = getDeviceSettings(id);
			Map<AbstractRobot, RobotSettings> robotSettings = getRobotSettings(id);
			processFlow = new ProcessFlow(name, processSteps, deviceSettings, robotSettings, creation, lastOpened);
			processFlow.setId(id);
			processFlow.setSingleCycle(isSingleCycle);
			if (clampingMannerId == CLAMPING_MANNER_LENGTH) {
				processFlow.getClampingType().setType(ClampingManner.Type.LENGTH);
			} else if (clampingMannerId == CLAMPING_MANNER_WIDTH) {
				processFlow.getClampingType().setType(ClampingManner.Type.WIDTH);
			} else {
				throw new IllegalStateException("Unknown clamping manner type: " + clampingMannerId);
			}
		}
		return processFlow;
	}
	
	private ProcessFlow getLightWeightedProcessFlowById(final int id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT ID, NAME, LASTOPENED FROM PROCESSFLOW WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		ProcessFlow processFlow = null;
		if (results.next()) {
			generalMapper.clearBuffers(id);
			String name = results.getString("NAME");
			Timestamp lastOpened = results.getTimestamp("LASTOPENED");
			processFlow = new ProcessFlow(name);
			processFlow.setId(id);
			processFlow.setLastOpened(lastOpened);
		}
		
		return processFlow;
	}
	
	public Map<AbstractRobot, RobotSettings> getRobotSettings(final int processId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROBOTSETTINGS WHERE PROCESSFLOW = ?");
		stmt.setInt(1, processId);
		ResultSet results = stmt.executeQuery();
		Map<AbstractRobot, RobotSettings> settings = new HashMap<AbstractRobot, RobotSettings>();
		while (results.next()) {
			int robotId = results.getInt("ROBOT");
			int activeGripperBodyId = results.getInt("ACTIVEGRIPPERBODY");
			int id = results.getInt("ID");
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROBOTSETTINGS_GRIPPERHEAD_GRIPPER JOIN GRIPPERHEAD_GRIPPER ON ROBOTSETTINGS_GRIPPERHEAD_GRIPPER.GRIPPERHEAD_GRIPPER = GRIPPERHEAD_GRIPPER.ID WHERE ROBOTSETTINGS_GRIPPERHEAD_GRIPPER.ROBOTSETTINGS = ?");
			stmt2.setInt(1, id);
			ResultSet results2 = stmt2.executeQuery();
			AbstractRobot robot = robotManager.getRobotById(robotId);
			GripperBody activeGripperBody = robot.getGripperBodyById(activeGripperBodyId);
			Map<GripperHead, Gripper> grippers = new HashMap<GripperHead, Gripper>();
			while (results2.next()) {
				int gripperHeadId = results2.getInt("GRIPPERHEAD");
				int gripperId = results2.getInt("GRIPPER");
				GripperHead gripperHead = robot.getGripperHeadById(gripperHeadId);
				Gripper gripper = gripperHead.getGripperById(gripperId);
				grippers.put(gripperHead, gripper);
			}
			RobotSettings robotSettings = new RobotSettings(activeGripperBody, grippers);
			settings.put(robot, robotSettings);
		}
		return settings;
	}
	
	public Map<AbstractDevice, DeviceSettings> getDeviceSettings(final int processId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICESETTINGS WHERE PROCESSFLOW = ?");
		stmt.setInt(1, processId);
		ResultSet results = stmt.executeQuery();
		Map<AbstractDevice, DeviceSettings> settings = new HashMap<AbstractDevice, DeviceSettings>();
		while (results.next()) {
			int deviceId = results.getInt("DEVICE");
			int id = results.getInt("ID");
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			Map<SimpleWorkArea, Clamping> clampings = getDefaultClampingPerWorkArea(id, device);
			if (device instanceof BasicStackPlate) {
				AbstractStackPlateDeviceSettings stackPlateSettings = getBasicStackPlateSettings(processId, id, (BasicStackPlate) device, clampings);
				settings.put(device, stackPlateSettings);
			} else if (device instanceof Conveyor) {
				ConveyorSettings conveyorSettings = getConveyorSettings(processId, id, (Conveyor) device, clampings);
				settings.put(device, conveyorSettings);
			} else if (device instanceof eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton) {
				eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings conveyorSettings = getConveyorSettings(processId, id, (eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton) device, clampings);
				settings.put(device, conveyorSettings);
			} else if (device instanceof ReversalUnit) {
				ReversalUnitSettings reversalSettings = getReversalUnitSettings(id, clampings);
				settings.put(device, reversalSettings);
			} else if (device instanceof UnloadPallet) {
			    UnloadPalletDeviceSettings unloadPalletSettings = getUnloadPalletSettings(processId, id, (UnloadPallet)device);
			    settings.put(device, unloadPalletSettings);
			} else {
				DeviceSettings deviceSettings = new DeviceSettings(clampings);
				deviceSettings.setId(id);
				settings.put(device, deviceSettings);
			}
		}
		return settings;
	}
	
	private Map<SimpleWorkArea, Clamping> getDefaultClampingPerWorkArea(final int deviceSettingsId, final AbstractDevice device) throws SQLException {
		Map<SimpleWorkArea, Clamping> defaultClampings = new HashMap<SimpleWorkArea, Clamping>();
		PreparedStatement stmt = ConnectionManager.getConnection()
			.prepareStatement("SELECT CLAMPING, DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA "           			  +
								"FROM DEVICESETTINGS_WORKAREA_CLAMPING "                     					  +
							    "JOIN WORKAREA_CLAMPING "                                                         +
							      "ON DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA_CLAMPING = WORKAREA_CLAMPING.ID " +
							   "WHERE DEVICESETTINGS_WORKAREA_CLAMPING.DEVICESETTINGS = ? "  					  +
				     			 "AND DEVICESETTINGS_WORKAREA_CLAMPING.DEFAULT_FL = ?");
		stmt.setInt(1, deviceSettingsId);
		stmt.setBoolean(2, true);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			int workAreaId = results.getInt("WORKAREA");
			int clampingId = results.getInt("CLAMPING");
			SimpleWorkArea workArea = device.getWorkAreaById(workAreaId);
			try {
				Clamping clamping = workArea.getWorkAreaManager().getClampingById(clampingId).clone();
				defaultClampings.put(workArea, clamping);
				addRelatedClampings(deviceSettingsId, clamping, workArea);
				// Do not set default because this is a cloned clamping, which is thus not the same. 
				//workArea.setDefaultClamping(clamping);
			} catch (CloneNotSupportedException e) {
				logger.error(e);
			}
		}
		return defaultClampings;
	}
	
	private void addRelatedClampings(final int deviceSettingsId, final Clamping clamping, final SimpleWorkArea workArea) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection()
				.prepareStatement("SELECT CLAMPING "													              +
									"FROM DEVICESETTINGS_WORKAREA_CLAMPING "                     					  +
								    "JOIN WORKAREA_CLAMPING "                                                         +
								      "ON DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA_CLAMPING = WORKAREA_CLAMPING.ID " +
								   "WHERE DEVICESETTINGS_WORKAREA_CLAMPING.DEVICESETTINGS = ? "  					  +
								     "AND DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA = ? " 		  					  +
					     			 "AND DEVICESETTINGS_WORKAREA_CLAMPING.DEFAULT_FL = ?");
			stmt.setInt(1, deviceSettingsId);
			stmt.setInt(2, workArea.getId());
			stmt.setBoolean(3, false);
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				try {
					int clampingId = results.getInt("CLAMPING");
					Clamping relClamping = workArea.getWorkAreaManager().getClampingById(clampingId).clone();
					clamping.addRelatedClamping(relClamping);
				} catch (CloneNotSupportedException e) {
					logger.error(e);
				}
			}
	}
	
	private ConveyorSettings getConveyorSettings(final int processFlowId, final int deviceSettingsId, final Conveyor conveyor, final Map<SimpleWorkArea, Clamping> clampings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CONVEYORSETTINGS WHERE ID = ?");
		stmt.setInt(1, deviceSettingsId);
		ResultSet results = stmt.executeQuery();
		ConveyorSettings conveyorSettings = null;
		if (results.next()) {
			int amount = results.getInt("AMOUNT");
			int rawWorkPieceId = results.getInt("RAWWORKPIECE");
			int finishedWorkPieceId = results.getInt("FINISHEDWORKPIECE");
			float offsetSupport1 = results.getFloat("OFFSETSUPPORT1");
			float offsetOtherSupports = results.getFloat("OFFSETOTHERSUPPORTS");
			WorkPiece rawWorkPiece = generalMapper.getWorkPieceById(processFlowId, rawWorkPieceId);
			WorkPiece finishedWorkPiece = generalMapper.getWorkPieceById(processFlowId, finishedWorkPieceId);
			conveyorSettings = new ConveyorSettings(rawWorkPiece, finishedWorkPiece, amount, offsetSupport1, offsetOtherSupports);
			conveyorSettings.setClampings(clampings);
		}
		return conveyorSettings;
	}
	
	private eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings getConveyorSettings(final int processFlowId, final int deviceSettingsId, final eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyor, final Map<SimpleWorkArea, Clamping> clampings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CONVEYOREATONSETTINGS WHERE ID = ?");
		stmt.setInt(1, deviceSettingsId);
		ResultSet results = stmt.executeQuery();
		eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings conveyorSettings = null;
		if (results.next()) {
			int amount = results.getInt("AMOUNT");
			int rawWorkPieceId = results.getInt("RAWWORKPIECE");
			int finishedWorkPieceId = results.getInt("FINISHEDWORKPIECE");
			WorkPiece rawWorkPiece = generalMapper.getWorkPieceById(processFlowId, rawWorkPieceId);
			WorkPiece finishedWorkPiece = generalMapper.getWorkPieceById(processFlowId, finishedWorkPieceId);
			conveyorSettings = new eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings(rawWorkPiece, finishedWorkPiece, amount);
			conveyorSettings.setClampings(clampings);
		}
		return conveyorSettings;
	}
	
	private ReversalUnitSettings getReversalUnitSettings(final int deviceSettingsId, final Map<SimpleWorkArea, Clamping> clampings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM REVERSALUNITSETTINGS WHERE ID = ?");
		stmt.setInt(1, deviceSettingsId);
		ResultSet results = stmt.executeQuery();
		ReversalUnitSettings reversalSettings = null;
		if (results.next()) {
			float configWidth = results.getInt("CONFIGWIDTH");
			boolean shiftedOrigin = results.getBoolean("SHIFTED_ORIGIN");
			reversalSettings = new ReversalUnitSettings(configWidth, shiftedOrigin);
			reversalSettings.setClampings(clampings);
		}
		return reversalSettings;
	}
	
	private AbstractStackPlateDeviceSettings getBasicStackPlateSettings(final int processFlowId, final int deviceSettingsId, final BasicStackPlate stackPlate, final Map<SimpleWorkArea, Clamping> clampings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM STACKPLATESETTINGS WHERE ID = ?");
		stmt.setInt(1, deviceSettingsId);
		ResultSet results = stmt.executeQuery();
		AbstractStackPlateDeviceSettings basicStackPlateSettings = null;
		if (results.next()) {
			int amount = results.getInt("AMOUNT");
			float orientation = results.getInt("ORIENTATION");
			int rawWorkPieceId = results.getInt("RAWWORKPIECE");
			int finishedWorkPieceId = results.getInt("FINISHEDWORKPIECE");
			int layers = results.getInt("LAYERS");
			float studHeight = results.getFloat("STUDHEIGHT");
			int gridId = results.getInt("GRID_ID");
			WorkPiece rawWorkPiece = generalMapper.getWorkPieceById(processFlowId, rawWorkPieceId);
			WorkPiece finishedWorkPiece = generalMapper.getWorkPieceById(processFlowId, finishedWorkPieceId);
			basicStackPlateSettings = new AbstractStackPlateDeviceSettings(rawWorkPiece, finishedWorkPiece, orientation, layers, amount, studHeight, gridId);
			basicStackPlateSettings.setClampings(clampings);
		}
		return basicStackPlateSettings;
	}
	
	private UnloadPalletDeviceSettings getUnloadPalletSettings(final int processFlowId, final int deviceSettingsId, final UnloadPallet unloadPallet) throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM UNLOADPALLETSETTINGS WHERE ID = ?");
        stmt.setInt(1, deviceSettingsId);
        ResultSet results = stmt.executeQuery();
        UnloadPalletDeviceSettings unloadPalletSettings = null;
        if (results.next()) {
            int finishedWorkPieceId = results.getInt("FINISHEDWORKPIECE");
            int layoutType = results.getInt("LAYOUT_TYPE");
            int layersBeforeCardBoard = results.getInt("LAYERS_CARDBOARD");
            int palletLayoutId = results.getInt("PALLET_LAYOUT");
            WorkPiece finishedWorkPiece = generalMapper.getWorkPieceById(processFlowId, finishedWorkPieceId);
            unloadPalletSettings = new UnloadPalletDeviceSettings(finishedWorkPiece, PalletLayoutType.getTypeById(layoutType), layersBeforeCardBoard, deviceManager.getPalletLayoutById(palletLayoutId));
        }
        return unloadPalletSettings;
	}
	
	
	public List<AbstractProcessStep> getProcessSteps(final int processId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM STEP WHERE PROCESSFLOW = ? ORDER BY INDEX ASC");
		stmt.setInt(1, processId);
		ResultSet results = stmt.executeQuery();
		List<AbstractProcessStep> processSteps = new ArrayList<AbstractProcessStep>();
		while (results.next()) {
			int type = results.getInt("TYPE");
			int id = results.getInt("ID");
			switch (type) {
				case STEP_TYPE_PICK:
					PickStep pickStep = getPickStep(processId, id);
					processSteps.add(pickStep);
					break;
				case STEP_TYPE_PUT:
					PutStep putStep = getPutStep(processId, id);
					processSteps.add(putStep);
					break;
				case STEP_TYPE_PROCESSING:
					ProcessingStep processingStep = getProcessingStep(processId, id);
					processSteps.add(processingStep);
					break;
				case STEP_TYPE_INTERVENTION:
					InterventionStep interventionStep = getInterventionStep(processId, id);
					processSteps.add(interventionStep);
					break;
				case STEP_TYPE_PUTANDWAIT:
					PutAndWaitStep putAndWaitStep = getPutAndWaitStep(processId, id);
					processSteps.add(putAndWaitStep);
					break;
				case STEP_TYPE_PICKAFTERWAIT:
					PickAfterWaitStep pickAfterWaitStep = getPickAfterWaitStep(processId, id);
					processSteps.add(pickAfterWaitStep);
					break;
				case STEP_TYPE_PROCESSINGWHILEWAITING:
					ProcessingWhileWaitingStep processingWhileWaitingStep = getProcessingWhileWaitingStep(processId, id);
					processSteps.add(processingWhileWaitingStep);
					break;
				default:
					throw new IllegalStateException("Unkown step type: [" + type + "].");
			}
		}
		return processSteps;
	}
	
	public Coordinates getRelativeTeachedCoordinates(final int processFlowId, final int stepId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM STEP_TEACHEDCOORDINATES WHERE STEP = ?");
		stmt.setInt(1, stepId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			int coordinates = results.getInt("COORDINATES");
			return generalMapper.getCoordinatesById(processFlowId, coordinates);
		}
		return null;
	}
	
	public PickStep getPickStep(final int processFlowId, final int id) throws SQLException {
		DevicePickSettings deviceSettings = getDevicePickSettingsByStepId(id);
		RobotPickSettings robotSettings = getRobotPickSettingsByStepId(processFlowId, id, deviceSettings.getWorkArea());
		PickStep pickStep = new PickStep(deviceSettings, robotSettings);
		pickStep.setRelativeTeachedOffset(getRelativeTeachedCoordinates(processFlowId, id));
		return pickStep;
	}
	
	public PickAfterWaitStep getPickAfterWaitStep(final int processFlowId, final int id) throws SQLException {
		DevicePickSettings deviceSettings = getDevicePickSettingsByStepId(id);
		RobotPickSettings robotSettings = getRobotPickSettingsByStepId(processFlowId, id, deviceSettings.getWorkArea());
		PickAfterWaitStep pickAfterWaitStep = new PickAfterWaitStep(deviceSettings, robotSettings);
		pickAfterWaitStep.setRelativeTeachedOffset(getRelativeTeachedCoordinates(processFlowId, id));
		return pickAfterWaitStep;
	}
	
	public PutStep getPutStep(final int processFlowId, final int id) throws SQLException {
		DevicePutSettings deviceSettings = getDevicePutSettingsByStepId(id);
		RobotPutSettings robotSettings = getRobotPutSettingsByStepId(processFlowId, id, deviceSettings.getWorkArea());
		PutStep putStep = new PutStep(deviceSettings, robotSettings);
		putStep.setRelativeTeachedOffset(getRelativeTeachedCoordinates(processFlowId, id));
		return putStep;
	}
	
	public PutAndWaitStep getPutAndWaitStep(final int processFlowId, final int id) throws SQLException {
		DevicePutSettings deviceSettings = getDevicePutSettingsByStepId(id);
		RobotPutSettings robotSettings = getRobotPutSettingsByStepId(processFlowId, id, deviceSettings.getWorkArea());
		PutAndWaitStep putAndWaitStep = new PutAndWaitStep(deviceSettings, robotSettings);
		putAndWaitStep.setRelativeTeachedOffset(getRelativeTeachedCoordinates(processFlowId, id));
		return putAndWaitStep;
	}
	
	public ProcessingStep getProcessingStep(final int processFlowId, final int id) throws SQLException {
		ProcessingDeviceStartCyclusSettings processingDeviceStartCyclusSettings = getStartCyclusSettingsByStepId(id);
		ProcessingStep processingStep = new ProcessingStep(processingDeviceStartCyclusSettings);
		return processingStep;
	}
	
	public InterventionStep getInterventionStep(final int processFlowId, final int id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM INTERVENTIONSTEP WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			int frequency = results.getInt("FREQUENCY");
			DeviceInterventionSettings deviceInterventionSettings = getDeviceInterventionSettings(id);
			InterventionStep interventionStep = new InterventionStep(deviceInterventionSettings, frequency);
			return interventionStep;
		}
		return null;
	}
	
	public ProcessingWhileWaitingStep getProcessingWhileWaitingStep(final int processFlowId, final int id) throws SQLException {
		ProcessingDeviceStartCyclusSettings processingDeviceStartCyclusSettings = getStartCyclusSettingsByStepId(id);
		RobotProcessingWhileWaitingSettings robotProcessingWhileWaitingSettings = getRobotProcessingWhileWaitingRobotSettings(id, processingDeviceStartCyclusSettings.getWorkArea());
		ProcessingWhileWaitingStep processingWhileWaitingStep = new ProcessingWhileWaitingStep(processingDeviceStartCyclusSettings, robotProcessingWhileWaitingSettings);
		return processingWhileWaitingStep;
	}
	
	public RobotProcessingWhileWaitingSettings getRobotProcessingWhileWaitingRobotSettings(final int processingWhileWaitingId, final SimpleWorkArea workArea) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROBOTACTIONSETTINGS WHERE STEP = ?");
		stmt.setInt(1, processingWhileWaitingId);
		ResultSet results = stmt.executeQuery();
		RobotProcessingWhileWaitingSettings robotProcessingWhileWaitingSettings = null;
		if (results.next()) {
			int id = results.getInt("ID");
			int robotId = results.getInt("ROBOT");
			int gripperHeadId = results.getInt("GRIPPERHEAD");
			AbstractRobot robot = robotManager.getRobotById(robotId);
			GripperHead gripperHead = robot.getGripperHeadById(gripperHeadId);
			if (robot instanceof FanucRobot) {
				robotProcessingWhileWaitingSettings = new RobotProcessingWhileWaitingSettings(robot, workArea, gripperHead);
				robotProcessingWhileWaitingSettings.setId(id);
			} else {
				throw new IllegalStateException("Unknown robot type: " + robot);
			}
		}
		return robotProcessingWhileWaitingSettings;
	}
	
	public DeviceInterventionSettings getDeviceInterventionSettings(final int interventionStepId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICEACTIONSETTINGS WHERE STEP = ?");
		stmt.setInt(1, interventionStepId);
		ResultSet results = stmt.executeQuery();
		DeviceInterventionSettings deviceInterventionSettings = null;
		if (results.next()) {
			int id = results.getInt("ID");
			int deviceId = results.getInt("DEVICE");
			int workAreaId = results.getInt("WORKAREA");
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			SimpleWorkArea workArea = device.getWorkAreaById(workAreaId);
			deviceInterventionSettings = new DeviceInterventionSettings(device, workArea);
			deviceInterventionSettings.setId(id);
		}
		return deviceInterventionSettings;
	}
	
	public ProcessingDeviceStartCyclusSettings getStartCyclusSettingsByStepId(final int processingStepId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICEACTIONSETTINGS WHERE STEP = ?");
		stmt.setInt(1, processingStepId);
		ResultSet results = stmt.executeQuery();
		ProcessingDeviceStartCyclusSettings processingDeviceStartCyclusSettings = null;
		if (results.next()) {
			int id = results.getInt("ID");
			int deviceId = results.getInt("DEVICE");
			int workAreaId = results.getInt("WORKAREA");
			int workNumber = results.getInt("WORKNUMBER_SEARCH");
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			SimpleWorkArea workArea = device.getWorkAreaById(workAreaId);
			processingDeviceStartCyclusSettings = new ProcessingDeviceStartCyclusSettings((AbstractProcessingDevice) device, workArea);
			processingDeviceStartCyclusSettings.setId(id);
			if (workNumber > 0) {
			    processingDeviceStartCyclusSettings.setWorkNumber(workNumber);
			}
		}
		return processingDeviceStartCyclusSettings;
	}
	
	public DevicePickSettings getDevicePickSettingsByStepId(final int pickStepId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICEACTIONSETTINGS WHERE STEP = ?");
		stmt.setInt(1, pickStepId);
		ResultSet results = stmt.executeQuery();
		DevicePickSettings devicePickSettings = null;
		if (results.next()) {
			int id = results.getInt("ID");
			int deviceId = results.getInt("DEVICE");
			int workAreaId = results.getInt("WORKAREA");
			boolean machineAirblow = results.getBoolean("MACHINE_AIRBLOW");
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			SimpleWorkArea workArea = device.getWorkAreaById(workAreaId);
			devicePickSettings = new DevicePickSettings(device, workArea);
			devicePickSettings.setId(id);
			devicePickSettings.setIsMachineAirblow(machineAirblow);
		}
		return devicePickSettings;
	}
	
	public DevicePutSettings getDevicePutSettingsByStepId(final int putStepId) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICEACTIONSETTINGS WHERE STEP = ?");
		stmt.setInt(1, putStepId);
		ResultSet results = stmt.executeQuery();
		DevicePutSettings devicePutSettings = null;
		if (results.next()) {
			int id = results.getInt("ID");
			int deviceId = results.getInt("DEVICE");
			// simpleWorkArea
			int workAreaId = results.getInt("WORKAREA");
			boolean machineAirblow = results.getBoolean("MACHINE_AIRBLOW");
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			SimpleWorkArea workArea = device.getWorkAreaById(workAreaId);
			devicePutSettings = new DevicePutSettings(device, workArea);
			devicePutSettings.setId(id);
			devicePutSettings.setIsMachineAirblow(machineAirblow);
		}
		return devicePutSettings;
	}
	
	public RobotPickSettings getRobotPickSettingsByStepId(final int processFlowId, final int pickStepId, final SimpleWorkArea workArea) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROBOTACTIONSETTINGS JOIN ROBOTPICKSETTINGS ON ROBOTACTIONSETTINGS.ID = ROBOTPICKSETTINGS.ID WHERE ROBOTACTIONSETTINGS.STEP = ?");
		stmt.setInt(1, pickStepId);
		ResultSet results = stmt.executeQuery();
		RobotPickSettings robotPickSettings = null;
		if (results.next()) {
			int id = results.getInt("ID");
			int gripperHeadId = results.getInt("GRIPPERHEAD");
			int smoothPointId = results.getInt("SMOOTHPOINT");
			int robotId = results.getInt("ROBOT");
			int workPieceId = results.getInt("WORKPIECE");
			boolean airblow = results.getBoolean("AIRBLOW");
			boolean gripInner = results.getBoolean("GRIPINNER");
			ApproachType approachType = ApproachType.getById(results.getInt("APPROACHTYPE"));
			boolean turnInMachine = results.getBoolean("TURN_IN_MACHINE");
			AbstractRobot robot = robotManager.getRobotById(robotId);
			GripperHead gripperHead = robot.getGripperHeadById(gripperHeadId);
			Coordinates smoothPoint = generalMapper.getCoordinatesById(processFlowId, smoothPointId);
			WorkPiece workPiece = generalMapper.getWorkPieceById(processFlowId, workPieceId);
			if (robot instanceof FanucRobot) {
				robotPickSettings = new FanucRobotPickSettings(robot, workArea, gripperHead, smoothPoint, null, workPiece, airblow, gripInner);
				robotPickSettings.setApproachType(approachType);
				robotPickSettings.setTurnInMachine(turnInMachine);
				robotPickSettings.setId(id);
				getRobotAirblowSettings(processFlowId, robotPickSettings);
			} else {
				throw new IllegalStateException("Unknown robot type: " + robot);
			}
		}
		return robotPickSettings;
	}
	
	public RobotPutSettings getRobotPutSettingsByStepId(final int processFlowId, final int putStepId, final SimpleWorkArea workArea) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROBOTACTIONSETTINGS JOIN ROBOTPUTSETTINGS ON ROBOTACTIONSETTINGS.ID = ROBOTPUTSETTINGS.ID WHERE ROBOTACTIONSETTINGS.STEP = ?");
		stmt.setInt(1, putStepId);
		ResultSet results = stmt.executeQuery();
		RobotPutSettings robotPutSettings = null;
		if (results.next()) {
			int id = results.getInt("ID");
			int gripperHeadId = results.getInt("GRIPPERHEAD");
			int smoothPointId = results.getInt("SMOOTHPOINT");
			int robotId = results.getInt("ROBOT");
			boolean airblow = results.getBoolean("AIRBLOW");
			boolean releaseBefore = results.getBoolean("RELEASEBEFORE");
			boolean gripInner = results.getBoolean("GRIPINNER");
			ApproachType approachType = ApproachType.getById(results.getInt("APPROACHTYPE"));
			boolean turnInMachine = results.getBoolean("TURN_IN_MACHINE");
			AbstractRobot robot = robotManager.getRobotById(robotId);
			GripperHead gripperHead = robot.getGripperHeadById(gripperHeadId);
			Coordinates smoothPoint = generalMapper.getCoordinatesById(processFlowId, smoothPointId);
			if (robot instanceof FanucRobot) {
				robotPutSettings = new FanucRobotPutSettings(robot, workArea, gripperHead, smoothPoint, null, airblow, releaseBefore, gripInner);
				robotPutSettings.setApproachType(approachType);
				robotPutSettings.setTurnInMachine(turnInMachine);
				robotPutSettings.setId(id);
				getRobotAirblowSettings(processFlowId, robotPutSettings);
			} else {
				throw new IllegalStateException("Unknown robot type: " + robot);
			}
		}
		return robotPutSettings;
	}
	
	private void getRobotAirblowSettings(final int processFlowId, final AbstractRobotActionSettings<?> robotActionSettings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM ROBOTAIRBLOWSETTINGS WHERE ID = ?");
		stmt.setInt(1, robotActionSettings.getId());
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			int clampingId = results.getInt("CLAMPING");
			int bottomCoordId = results.getInt("BOTTOMCOORD");
			Coordinates bottomCoord = generalMapper.getCoordinatesById(processFlowId, bottomCoordId);
			int topCoordId = results.getInt("TOPCOORD");
			Coordinates topCoord = generalMapper.getCoordinatesById(processFlowId, topCoordId);
			AirblowSquare airblowSettings = new AirblowSquare(bottomCoord, topCoord);
			robotActionSettings.addRobotAirblowSettings(clampingId, airblowSettings);
		}
	}
	
	public static int getProcessFlowIdForName(final String name) throws SQLException {
		int id = -1;
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM PROCESSFLOW WHERE NAME = ?");
		stmt.setString(1, name);
		ResultSet resultSet = stmt.executeQuery();
		if (resultSet.next()) {
			id = resultSet.getInt("ID");
		}
		return id;
	}
	
}
