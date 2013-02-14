package eu.robojob.irscw.db.process;

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

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

import eu.robojob.irscw.db.ConnectionManager;
import eu.robojob.irscw.db.GeneralMapper;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobotActionSettings;
import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.external.robot.GripperBody;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.external.robot.RobotProcessingWhileWaitingSettings;
import eu.robojob.irscw.external.robot.RobotPutSettings;
import eu.robojob.irscw.external.robot.RobotSettings;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.DeviceStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.ProcessingWhileWaitingStep;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.RobotStep;
import eu.robojob.irscw.workpiece.WorkPiece;

public class ProcessFlowMapper {

	private static final int STEP_TYPE_PICK = 1;
	private static final int STEP_TYPE_PUT = 2;
	private static final int STEP_TYPE_PROCESSING = 3;
	private static final int STEP_TYPE_INTERVENTION = 4;
	private static final int STEP_TYPE_PUTANDWAIT = 5;
	private static final int STEP_TYPE_PICKAFTERWAIT = 6;
	private static final int STEP_TYPE_PROCESSINGWHILEWAITING = 7;
	
	private static final int STACKPLATE_ORIENTATION_HORIZONTAL = 1;
	private static final int STACKPLATE_ORIENTATION_TILTED = 2;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowMapper.class.getName());
	
	private DeviceManager deviceManager;
	private RobotManager robotManager;
	
	private GeneralMapper generalMapper;
	
	public ProcessFlowMapper(final GeneralMapper generalMapper, final DeviceManager deviceManager, final RobotManager robotManager) {
		this.generalMapper = generalMapper;
		this.deviceManager = deviceManager;
		this.robotManager = robotManager;
	} 
	
	public List<ProcessFlow> getAllProcessFlows() throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM PROCESSFLOW ORDER BY LASTOPENED DESC");
		ResultSet results = stmt.executeQuery();
		List<ProcessFlow> processFlows = new ArrayList<ProcessFlow>();
		while (results.next()) {
			int id = results.getInt("ID");
			ProcessFlow processFlow = getProcessFlowById(id);
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
		clearProcessFlowStepsSettingsAndReferencedIds(processFlow);
		ConnectionManager.getConnection().setAutoCommit(false);
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE PROCESSFLOW SET NAME = ?, LASTOPENED = ? WHERE ID = ?");
		stmt.setString(1, processFlow.getName());
		stmt.setTimestamp(2, processFlow.getLastOpened());
		stmt.setInt(3, processFlow.getId());
		try {
			stmt.executeUpdate();
			deleteProcessFlowStepsAndSettings(processFlow);
			saveProcessFlowStepsAndSettings(processFlow);
			ConnectionManager.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
			ConnectionManager.getConnection().rollback();
		}
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	//TODO: delete all id s (work piece, coordinates, ...)
	public void saveProcessFlow(final ProcessFlow processFlow) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		processFlow.setCreation(new Timestamp(System.currentTimeMillis()));
		processFlow.setLastOpened(new Timestamp(System.currentTimeMillis()));
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO PROCESSFLOW (NAME, CREATION, LASTOPENED) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, processFlow.getName());
		stmt.setTimestamp(2, processFlow.getCreation());
		stmt.setTimestamp(3, processFlow.getLastOpened());
		try {
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				processFlow.setId(resultSet.getInt(1));
				saveProcessFlowStepsAndSettings(processFlow);
				ConnectionManager.getConnection().commit();
			}
		} catch (SQLException e) {
			ConnectionManager.getConnection().rollback();
		}
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	public void deleteProcessFlow(final ProcessFlow processFlow) throws SQLException {
		ConnectionManager.getConnection().setAutoCommit(false);
		deleteProcessFlowStepsAndSettings(processFlow);
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM PROCESSFLOW WHERE ID = ?");
		stmt.setInt(1, processFlow.getId());
		stmt.executeUpdate();
		ConnectionManager.getConnection().commit();
		ConnectionManager.getConnection().setAutoCommit(true);
	}
	
	private void deleteProcessFlowStepsAndSettings(final ProcessFlow processFlow) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM DEVICESETTINGS WHERE PROCESSFLOW = ?");
		stmt.setInt(1, processFlow.getId());
		stmt.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("DELETE FROM ROBOTSETTINGS WHERE PROCESSFLOW = ?");
		stmt2.setInt(1, processFlow.getId());
		stmt2.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		PreparedStatement stmt3 = ConnectionManager.getConnection().prepareStatement("DELETE FROM STEP WHERE PROCESSFLOW = ?");
		stmt3.setInt(1, processFlow.getId());
		stmt3.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		for (AbstractProcessStep step : processFlow.getProcessSteps()) {
			if (step instanceof PickStep) {
				PickStep pStep = (PickStep) step;
				if (pStep.getRobotSettings().getWorkPiece().getId() > 0) {
					PreparedStatement stmt4 = ConnectionManager.getConnection().prepareStatement("DELETE FROM WORKPIECE WHERE ID = ?");
					stmt4.setInt(1, pStep.getRobotSettings().getWorkPiece().getId());
					stmt4.executeUpdate();	
				}
			}
			if (step instanceof RobotStep) {
				AbstractRobotActionSettings<?> robotActionSettings = ((RobotStep) step).getRobotSettings();
				if ((robotActionSettings.getSmoothPoint() != null) && (robotActionSettings.getSmoothPoint().getId() > 0)) {
					PreparedStatement stmt5 = ConnectionManager.getConnection().prepareStatement("DELETE FROM COORDINATES WHERE ID = ?");
					stmt5.setInt(1, robotActionSettings.getSmoothPoint().getId());
					stmt5.executeUpdate();	
				}
			}
		}
		clearProcessFlowStepsSettingsAndReferencedIds(processFlow);
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
	}
	
	private void saveDeviceActionSettings(final DeviceStep deviceStep) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO DEVICEACTIONSETTINGS (DEVICE, WORKAREA, STEP) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, deviceStep.getDevice().getId());
		stmt.setInt(2, deviceStep.getDeviceSettings().getWorkArea().getId());
		stmt.setInt(3, ((AbstractProcessStep) deviceStep).getId());
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
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTACTIONSETTINGS (STEP, GRIPPERHEAD, SMOOTHPOINT, ROBOT) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, ((AbstractProcessStep) robotStep).getId());
		stmt.setInt(2, robotStep.getRobotSettings().getGripperHead().getId());
		if (robotStep.getRobotSettings().getSmoothPoint() != null) {
			stmt.setInt(3, robotStep.getRobotSettings().getSmoothPoint().getId());
		} else {
			stmt.setNull(3, Type.INT);
		}
		stmt.setInt(4, robotStep.getRobot().getId());
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if ((keys != null) && (keys.next())) {
			robotStep.getRobotSettings().setId(keys.getInt(1));
		}
		if (robotStep instanceof PickStep) {
			RobotPickSettings robotPickSettings = ((PickStep) robotStep).getRobotSettings();
			generalMapper.saveWorkPiece(robotPickSettings.getWorkPiece());
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTPICKSETTINGS (ID, WORKPIECE, AIRBLOW) VALUES (?, ?, ?)");
			stmt2.setInt(1, robotStep.getRobotSettings().getId());
			stmt2.setInt(2, robotPickSettings.getWorkPiece().getId());
			stmt2.setBoolean(3, robotPickSettings.isDoMachineAirblow());
			stmt2.executeUpdate();
		} else if (robotStep instanceof PutStep) {
			RobotPutSettings robotPutSettings = ((PutStep) robotStep).getRobotSettings();
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("INSERT INTO ROBOTPUTSETTINGS (ID, AIRBLOW) VALUES (?, ?)");
			stmt2.setInt(1, robotStep.getRobotSettings().getId());
			stmt2.setBoolean(2, robotPutSettings.isDoMachineAirblow());
			stmt2.executeUpdate();
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
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM WORKAREA_CLAMPING WHERE WORKAREA = ? AND CLAMPING = ?");
			stmt2.setInt(1, entry.getKey().getId());
			stmt2.setInt(2, entry.getValue().getId());
			ResultSet results = stmt2.executeQuery();
			int id = 0;
			if (results.next()) {
				id = results.getInt("ID");
				PreparedStatement stmt3 = ConnectionManager.getConnection().prepareStatement("INSERT INTO DEVICESETTINGS_WORKAREA_CLAMPING (DEVICESETTINGS, WORKAREA_CLAMPING) VALUES (?, ?)");
				stmt3.setInt(1, deviceSettings.getId());
				stmt3.setInt(2, id);
				stmt3.executeUpdate();
			} else {
				throw new IllegalStateException("Couldn't find entry for workarea: [" + entry.getKey() + "] and clamping: [" + entry.getValue() + "].");
			}
		}
		if (deviceSettings instanceof BasicStackPlateSettings) {
			BasicStackPlateSettings bspSettings = (BasicStackPlateSettings) deviceSettings;
			generalMapper.saveWorkPiece(bspSettings.getRawWorkPiece());
			generalMapper.saveWorkPiece(bspSettings.getFinishedWorkPiece());
			PreparedStatement stmt4 = ConnectionManager.getConnection().prepareStatement("INSERT INTO STACKPLATESETTINGS (ID, AMOUNT, ORIENTATION, RAWWORKPIECE, FINISHEDWORKPIECE) VALUES (?, ?, ?, ?, ?)");
			stmt4.setInt(1, bspSettings.getId());
			stmt4.setInt(2, bspSettings.getAmount());
			int orientation = 0;
			if (bspSettings.getOrientation() == WorkPieceOrientation.HORIZONTAL) {
				orientation = STACKPLATE_ORIENTATION_HORIZONTAL;
			} else if (bspSettings.getOrientation() == WorkPieceOrientation.TILTED) {
				orientation = STACKPLATE_ORIENTATION_TILTED;
			} else {
				throw new IllegalStateException("Unknown workpiece orientation: [" + bspSettings.getOrientation() + "].");
			}
			stmt4.setInt(3, orientation);
			stmt4.setInt(4, bspSettings.getRawWorkPiece().getId());
			stmt4.setInt(5, bspSettings.getFinishedWorkPiece().getId());
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
			stmt2.setInt(2, entry.getValue().getId());
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
			String name = results.getString("NAME");
			Timestamp creation = results.getTimestamp("CREATION");
			Timestamp lastOpened = results.getTimestamp("LASTOPENED");
			List<AbstractProcessStep> processSteps = getProcessSteps(id);
			Map<AbstractDevice, DeviceSettings> deviceSettings = getDeviceSettings(id);
			Map<AbstractRobot, RobotSettings> robotSettings = getRobotSettings(id);
			processFlow = new ProcessFlow(name, processSteps, deviceSettings, robotSettings, creation, lastOpened);
			processFlow.setId(id);
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
			PreparedStatement stmt2 = ConnectionManager.getConnection().prepareStatement("SELECT * FROM DEVICESETTINGS_WORKAREA_CLAMPING JOIN WORKAREA_CLAMPING ON DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA_CLAMPING = WORKAREA_CLAMPING.ID WHERE DEVICESETTINGS_WORKAREA_CLAMPING.DEVICESETTINGS = ?");
			stmt2.setInt(1, id);
			ResultSet results2 = stmt2.executeQuery();
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			Map<WorkArea, Clamping> clampings = new HashMap<WorkArea, Clamping>();
			while (results2.next()) {
				int workareaId = results2.getInt("WORKAREA");
				int clampingId = results2.getInt("CLAMPING");
				WorkArea workArea = device.getWorkAreaById(workareaId);
				Clamping clamping = workArea.getClampingById(clampingId);
				clampings.put(workArea, clamping);
			}
			if (device instanceof BasicStackPlate) {
				BasicStackPlateSettings stackPlateSettings = getBasicStackPlateSettings(id, (BasicStackPlate) device, clampings);
				settings.put(device, stackPlateSettings);
			} else {
				DeviceSettings deviceSettings = new DeviceSettings(clampings);
				settings.put(device, deviceSettings);
			}
		}
		return settings;
	}
	
	private BasicStackPlateSettings getBasicStackPlateSettings(final int deviceSettingsId, final BasicStackPlate stackPlate, final Map<WorkArea, Clamping> clampings) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM STACKPLATESETTINGS WHERE ID = ?");
		stmt.setInt(1, deviceSettingsId);
		ResultSet results = stmt.executeQuery();
		BasicStackPlateSettings basicStackPlateSettings = null;
		if (results.next()) {
			int amount = results.getInt("AMOUNT");
			int orientation = results.getInt("ORIENTATION");
			int rawWorkPieceId = results.getInt("RAWWORKPIECE");
			int finishedWorkPieceId = results.getInt("FINISHEDWORKPIECE");
			WorkPiece rawWorkPiece = generalMapper.getWorkPieceById(rawWorkPieceId);
			WorkPiece finishedWorkPiece = generalMapper.getWorkPieceById(finishedWorkPieceId);
			if (orientation == STACKPLATE_ORIENTATION_HORIZONTAL) {
				basicStackPlateSettings = new BasicStackPlateSettings(rawWorkPiece, finishedWorkPiece, WorkPieceOrientation.HORIZONTAL, amount);
			} else if (orientation == STACKPLATE_ORIENTATION_TILTED) {
				basicStackPlateSettings = new BasicStackPlateSettings(rawWorkPiece, finishedWorkPiece, WorkPieceOrientation.TILTED, amount);
			} else {
				throw new IllegalStateException("Unknown workpiece orientation: [" + orientation + "].");
			}
		}
		basicStackPlateSettings.setClampings(clampings);
		return basicStackPlateSettings;
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
					PickStep pickStep = getPickStep(id);
					processSteps.add(pickStep);
					break;
				case STEP_TYPE_PUT:
					PutStep putStep = getPutStep(id);
					processSteps.add(putStep);
					break;
				case STEP_TYPE_PROCESSING:
					ProcessingStep processingStep = getProcessingStep(id);
					processSteps.add(processingStep);
					break;
				case STEP_TYPE_INTERVENTION:
					InterventionStep interventionStep = getInterventionStep(id);
					processSteps.add(interventionStep);
					break;
				case STEP_TYPE_PUTANDWAIT:
					PutAndWaitStep putAndWaitStep = getPutAndWaitStep(id);
					processSteps.add(putAndWaitStep);
					break;
				case STEP_TYPE_PICKAFTERWAIT:
					PickAfterWaitStep pickAfterWaitStep = getPickAfterWaitStep(id);
					processSteps.add(pickAfterWaitStep);
					break;
				case STEP_TYPE_PROCESSINGWHILEWAITING:
					ProcessingWhileWaitingStep processingWhileWaitingStep = getProcessingWhileWaitingStep(id);
					processSteps.add(processingWhileWaitingStep);
					break;
				default:
					throw new IllegalStateException("Unkown step type: [" + type + "].");
			}
		}
		return processSteps;
	}
	
	public PickStep getPickStep(final int id) throws SQLException {
		DevicePickSettings deviceSettings = getDevicePickSettingsByStepId(id);
		RobotPickSettings robotSettings = getRobotPickSettingsByStepId(id, deviceSettings.getWorkArea());
		PickStep pickStep = new PickStep(deviceSettings, robotSettings);
		return pickStep;
	}
	
	public PickAfterWaitStep getPickAfterWaitStep(final int id) throws SQLException {
		DevicePickSettings deviceSettings = getDevicePickSettingsByStepId(id);
		RobotPickSettings robotSettings = getRobotPickSettingsByStepId(id, deviceSettings.getWorkArea());
		PickAfterWaitStep pickAfterWaitStep = new PickAfterWaitStep(deviceSettings, robotSettings);
		return pickAfterWaitStep;
	}
	
	public PutStep getPutStep(final int id) throws SQLException {
		DevicePutSettings deviceSettings = getDevicePutSettingsByStepId(id);
		RobotPutSettings robotSettings = getRobotPutSettingsByStepId(id, deviceSettings.getWorkArea());
		PutStep putStep = new PutStep(deviceSettings, robotSettings);
		return putStep;
	}
	
	public PutAndWaitStep getPutAndWaitStep(final int id) throws SQLException {
		DevicePutSettings deviceSettings = getDevicePutSettingsByStepId(id);
		RobotPutSettings robotSettings = getRobotPutSettingsByStepId(id, deviceSettings.getWorkArea());
		PutAndWaitStep putAndWaitStep = new PutAndWaitStep(deviceSettings, robotSettings);
		return putAndWaitStep;
	}
	
	public ProcessingStep getProcessingStep(final int id) throws SQLException {
		ProcessingDeviceStartCyclusSettings processingDeviceStartCyclusSettings = getStartCyclusSettingsByStepId(id);
		ProcessingStep processingStep = new ProcessingStep(processingDeviceStartCyclusSettings);
		return processingStep;
	}
	
	public InterventionStep getInterventionStep(final int id) throws SQLException {
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
	
	public ProcessingWhileWaitingStep getProcessingWhileWaitingStep(final int id) throws SQLException {
		ProcessingDeviceStartCyclusSettings processingDeviceStartCyclusSettings = getStartCyclusSettingsByStepId(id);
		RobotProcessingWhileWaitingSettings robotProcessingWhileWaitingSettings = getRobotProcessingWhileWaitingRobotSettings(id, processingDeviceStartCyclusSettings.getWorkArea());
		ProcessingWhileWaitingStep processingWhileWaitingStep = new ProcessingWhileWaitingStep(processingDeviceStartCyclusSettings, robotProcessingWhileWaitingSettings);
		return processingWhileWaitingStep;
	}
	
	public RobotProcessingWhileWaitingSettings getRobotProcessingWhileWaitingRobotSettings(final int processingWhileWaitingId, final WorkArea workArea) throws SQLException {
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
			WorkArea workArea = device.getWorkAreaById(workAreaId);
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
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			WorkArea workArea = device.getWorkAreaById(workAreaId);
			processingDeviceStartCyclusSettings = new ProcessingDeviceStartCyclusSettings((AbstractProcessingDevice) device, workArea);
			processingDeviceStartCyclusSettings.setId(id);
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
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			WorkArea workArea = device.getWorkAreaById(workAreaId);
			devicePickSettings = new DevicePickSettings(device, workArea);
			devicePickSettings.setId(id);
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
			int workAreaId = results.getInt("WORKAREA");
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			WorkArea workArea = device.getWorkAreaById(workAreaId);
			devicePutSettings = new DevicePutSettings(device, workArea);
			devicePutSettings.setId(id);
		}
		return devicePutSettings;
	}
	
	public RobotPickSettings getRobotPickSettingsByStepId(final int pickStepId, final WorkArea workArea) throws SQLException {
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
			AbstractRobot robot = robotManager.getRobotById(robotId);
			GripperHead gripperHead = robot.getGripperHeadById(gripperHeadId);
			Coordinates smoothPoint = generalMapper.getCoordinatesById(smoothPointId);
			WorkPiece workPiece = generalMapper.getWorkPieceById(workPieceId);
			if (robot instanceof FanucRobot) {
				robotPickSettings = new FanucRobotPickSettings(robot, workArea, gripperHead, smoothPoint, null, workPiece, airblow);
				robotPickSettings.setId(id);
			} else {
				throw new IllegalStateException("Unknown robot type: " + robot);
			}
		}
		return robotPickSettings;
	}
	
	public RobotPutSettings getRobotPutSettingsByStepId(final int putStepId, final WorkArea workArea) throws SQLException {
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
			AbstractRobot robot = robotManager.getRobotById(robotId);
			GripperHead gripperHead = robot.getGripperHeadById(gripperHeadId);
			Coordinates smoothPoint = generalMapper.getCoordinatesById(smoothPointId);
			if (robot instanceof FanucRobot) {
				robotPutSettings = new FanucRobotPutSettings(robot, workArea, gripperHead, smoothPoint, null, airblow);
				robotPutSettings.setId(id);
			} else {
				throw new IllegalStateException("Unknown robot type: " + robot);
			}
		}
		return robotPutSettings;
	}
	
	public int getProcessFlowIdForName(final String name) throws SQLException {
		int id = 0;
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM PROCESSFLOW WHERE NAME = ?");
		stmt.setString(1, name);
		ResultSet resultSet = stmt.executeQuery();
		if (resultSet.next()) {
			id = resultSet.getInt("ID");
		}
		return id;
	}
	
}
