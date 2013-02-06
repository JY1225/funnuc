package eu.robojob.irscw.db.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.ProcessingWhileWaitingStep;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.process.PutStep;
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
	
	private DeviceManager deviceManager;
	private RobotManager robotManager;
	
	private GeneralMapper generalMapper;
	
	public ProcessFlowMapper(final GeneralMapper generalMapper, final DeviceManager deviceManager, final RobotManager robotManager) {
		this.generalMapper = generalMapper;
		this.deviceManager = deviceManager;
		this.robotManager = robotManager;
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
	
	public ProcessFlow getProcessFlowById(final int id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM PROCESSFLOW WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		ProcessFlow processFlow = null;
		if (results.next()) {
			String name = results.getString("NAME");
			String description = results.getString("DESCRIPTION");
			Timestamp creation = results.getTimestamp("CREATION");
			Timestamp lastOpened = results.getTimestamp("LASTOPENED");
			List<AbstractProcessStep> processSteps = getProcessSteps(id);
			Map<AbstractDevice, DeviceSettings> deviceSettings = getDeviceSettings(id);
			Map<AbstractRobot, RobotSettings> robotSettings = getRobotSettings(id);
			processFlow = new ProcessFlow(name, description, processSteps, deviceSettings, robotSettings, creation, lastOpened);
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
			int locationId = results.getInt("LOCATION");
			int robotId = results.getInt("ROBOT");
			int workPieceId = results.getInt("WORKPIECE");
			boolean airblow = results.getBoolean("AIRBLOW");
			AbstractRobot robot = robotManager.getRobotById(robotId);
			GripperHead gripperHead = robot.getGripperHeadById(gripperHeadId);
			Coordinates smoothPoint = generalMapper.getCoordinatesById(smoothPointId);
			Coordinates location = generalMapper.getCoordinatesById(locationId);
			WorkPiece workPiece = generalMapper.getWorkPieceById(workPieceId);
			if (robot instanceof FanucRobot) {
				robotPickSettings = new FanucRobotPickSettings(robot, workArea, gripperHead, smoothPoint, location, workPiece, airblow);
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
			int locationId = results.getInt("LOCATION");
			int robotId = results.getInt("ROBOT");
			boolean airblow = results.getBoolean("AIRBLOW");
			AbstractRobot robot = robotManager.getRobotById(robotId);
			GripperHead gripperHead = robot.getGripperHeadById(gripperHeadId);
			Coordinates smoothPoint = generalMapper.getCoordinatesById(smoothPointId);
			Coordinates location = generalMapper.getCoordinatesById(locationId);
			if (robot instanceof FanucRobot) {
				robotPutSettings = new FanucRobotPutSettings(robot, workArea, gripperHead, smoothPoint, location, airblow);
				robotPutSettings.setId(id);
			} else {
				throw new IllegalStateException("Unknown robot type: " + robot);
			}
		}
		return robotPutSettings;
	}
}
