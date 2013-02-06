package eu.robojob.irscw.process;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.db.process.ProcessFlowMapper;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.external.robot.RobotSettings;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPiece.Type;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class ProcessFlowManager {

	private ProcessFlowMapper processFlowMapper;
	private DeviceManager deviceManager; 
	private RobotManager robotManager;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowManager.class.getName());
	
	public ProcessFlowManager(final ProcessFlowMapper processFlowMapper, final DeviceManager deviceManager, final RobotManager robotManager) {
		this.processFlowMapper = processFlowMapper;
		this.deviceManager = deviceManager;
		this.robotManager = robotManager;
	}
	
	public ProcessFlow getLastProcessFlow() {
		List<ProcessFlow> processFlows;
		try {
			processFlows = processFlowMapper.getLastOpenedProcessFlows(1);
			if (processFlows.size() > 0) {
				return processFlows.get(0);
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	public ProcessFlow createNewProcessFlow() {
		//TODO add checks if the needed devices are in the database
		AbstractDevice stackingFromDevice = deviceManager.getStackingFromDevices().iterator().next();
		AbstractDevice stackingToDevice = deviceManager.getStackingToDevices().iterator().next();
		AbstractCNCMachine cncMachine = deviceManager.getCNCMachines().iterator().next();
		AbstractRobot robot = robotManager.getRobots().iterator().next();
		PickStep pickStep = new PickStep(stackingFromDevice.getDefaultPickSettings(), robot.getDefaultPickSettings());
		PutStep putStep = new PutStep(cncMachine.getDefaultPutSettings(), robot.getDefaultPutSettings());
		ProcessingStep processingStep = new ProcessingStep(cncMachine.getDefaultStartCyclusSettings());
		PickStep pickStep2 = new PickStep(cncMachine.getDefaultPickSettings(), robot.getDefaultPickSettings());
		PutStep putStep2 = new PutStep(stackingToDevice.getDefaultPutSettings(), robot.getDefaultPutSettings());
		List<AbstractProcessStep> processSteps = new ArrayList<AbstractProcessStep>();
		WorkPiece rawWorkPiece = new WorkPiece(Type.RAW, new WorkPieceDimensions());
		WorkPiece finishedWorkPiece = new WorkPiece(Type.FINISHED, new WorkPieceDimensions());
		pickStep.getRobotSettings().setWorkPiece(rawWorkPiece);
		pickStep2.getRobotSettings().setWorkPiece(finishedWorkPiece);
		processSteps.add(pickStep);
		processSteps.add(putStep);
		processSteps.add(processingStep);
		processSteps.add(pickStep2);
		processSteps.add(putStep2);
		Map<AbstractDevice, DeviceSettings> deviceSettings = new HashMap<AbstractDevice, DeviceSettings>();
		deviceSettings.put(stackingFromDevice, stackingFromDevice.getDeviceSettings());
		if (stackingFromDevice instanceof BasicStackPlate) {
			((BasicStackPlateSettings) deviceSettings.get(stackingFromDevice)).setRawWorkPieceDimensions(rawWorkPiece.getDimensions());
		}
		if (stackingToDevice instanceof BasicStackPlate) {
			((BasicStackPlateSettings) deviceSettings.get(stackingToDevice)).setFinishedWorkPieceDimensions(finishedWorkPiece.getDimensions());
		}
		for (AbstractProcessStep step : processSteps) {
			if ((step instanceof DeviceStep) && (step instanceof RobotStep)) {
				if (((DeviceStep) step).getDeviceSettings().getWorkArea() != null) {
					((RobotStep) step).getRobotSettings().setWorkArea(((DeviceStep) step).getDeviceSettings().getWorkArea());
				}
			}
		}
		deviceSettings.put(cncMachine, cncMachine.getDeviceSettings());
		deviceSettings.put(stackingToDevice, stackingToDevice.getDeviceSettings());
		Map<AbstractRobot, RobotSettings> robotSettings = new HashMap<AbstractRobot, RobotSettings>();
		robotSettings.put(robot, robot.getRobotSettings());
		ProcessFlow processFlow = new ProcessFlow("", "", processSteps, deviceSettings, robotSettings, new Timestamp(System.currentTimeMillis()), null);
		return processFlow;
	}
	
}
