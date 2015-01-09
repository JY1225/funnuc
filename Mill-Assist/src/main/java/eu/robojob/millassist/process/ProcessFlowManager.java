package eu.robojob.millassist.process;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.process.ProcessFlowMapper;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.RobotManager;
import eu.robojob.millassist.external.robot.RobotSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class ProcessFlowManager {

	private ProcessFlowMapper processFlowMapper;
	private DeviceManager deviceManager; 
	private RobotManager robotManager;
	private ProcessFlow activeProcessFlow;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowManager.class.getName());
	
	public ProcessFlowManager(final ProcessFlowMapper processFlowMapper, final DeviceManager deviceManager, final RobotManager robotManager) {
		this.processFlowMapper = processFlowMapper;
		this.deviceManager = deviceManager;
		this.deviceManager.setProcessFlowManager(this);
		this.robotManager = robotManager;
		this.robotManager.setProcessFlowManager(this);
	}
	
	public void setActiveProcessFlow(final ProcessFlow processFlow) {
		this.activeProcessFlow = processFlow;
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
	
	//TODO add buffer and look at delete!
	public List<ProcessFlow> getProcessFlows() {
		try {
			return processFlowMapper.getAllProcessFlows();
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
		PickStep pickStep = new PickStep(stackingFromDevice.getDefaultPickSettings(WorkPiece.Type.RAW), robot.getDefaultPickSettings());
		PutStep putStep = new PutStep(cncMachine.getDefaultPutSettings(WorkPiece.Type.RAW), robot.getDefaultPutSettings());
		ProcessingStep processingStep = new ProcessingStep(cncMachine.getDefaultStartCyclusSettings(WorkPiece.Type.FINISHED));
		PickStep pickStep2 = new PickStep(cncMachine.getDefaultPickSettings(WorkPiece.Type.FINISHED), robot.getDefaultPickSettings());
		PutStep putStep2 = new PutStep(stackingToDevice.getDefaultPutSettings(WorkPiece.Type.FINISHED), robot.getDefaultPutSettings());
		List<AbstractProcessStep> processSteps = new ArrayList<AbstractProcessStep>();
		WorkPiece rawWorkPiece = new WorkPiece(Type.RAW, new WorkPieceDimensions(), Material.OTHER, 0.0f);
		WorkPiece finishedWorkPiece = new WorkPiece(Type.FINISHED, new WorkPieceDimensions(), Material.OTHER, 0.0f);
		pickStep.getRobotSettings().setWorkPiece(rawWorkPiece);
		pickStep2.getRobotSettings().setWorkPiece(finishedWorkPiece);
		processSteps.add(pickStep);
		processSteps.add(putStep);
		processSteps.add(processingStep);
		processSteps.add(pickStep2);
		processSteps.add(putStep2);
		Map<AbstractDevice, DeviceSettings> deviceSettings = new HashMap<AbstractDevice, DeviceSettings>();
		if (stackingFromDevice instanceof AbstractStackingDevice) {
			((AbstractStackingDevice) stackingFromDevice).clearDeviceSettings();
		}
		if (stackingToDevice instanceof AbstractStackingDevice) {
			((AbstractStackingDevice) stackingToDevice).clearDeviceSettings();
		}
		deviceSettings.put(stackingFromDevice, stackingFromDevice.getDeviceSettings());
		if (stackingFromDevice instanceof BasicStackPlate) {
			((AbstractStackPlateDeviceSettings) deviceSettings.get(stackingFromDevice)).setRawWorkPiece(rawWorkPiece);
		}
		if (stackingToDevice instanceof BasicStackPlate) {
			((AbstractStackPlateDeviceSettings) deviceSettings.get(stackingToDevice)).setFinishedWorkPiece(finishedWorkPiece);
		}
		// always assign both raw and finished work piece to conveyor!
		if (stackingFromDevice instanceof Conveyor) {
			((ConveyorSettings) deviceSettings.get(stackingFromDevice)).setRawWorkPiece(rawWorkPiece);
			((ConveyorSettings) deviceSettings.get(stackingFromDevice)).setFinishedWorkPiece(finishedWorkPiece);
		}
		if (stackingToDevice instanceof Conveyor) {
			((ConveyorSettings) deviceSettings.get(stackingToDevice)).setRawWorkPiece(rawWorkPiece);
			((ConveyorSettings) deviceSettings.get(stackingToDevice)).setFinishedWorkPiece(finishedWorkPiece);
		}
		if (stackingFromDevice instanceof eu.robojob.millassist.external.device.stacking.conveyor.eaton.Conveyor) {
			((eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) deviceSettings.get(stackingFromDevice)).setRawWorkPiece(rawWorkPiece);
			((eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) deviceSettings.get(stackingFromDevice)).setFinishedWorkPiece(finishedWorkPiece);
		}
		if (stackingToDevice instanceof eu.robojob.millassist.external.device.stacking.conveyor.eaton.Conveyor) {
			((eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) deviceSettings.get(stackingToDevice)).setRawWorkPiece(rawWorkPiece);
			((eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) deviceSettings.get(stackingToDevice)).setFinishedWorkPiece(finishedWorkPiece);
		}
		deviceSettings.put(cncMachine, cncMachine.getDeviceSettings());
		if (!stackingToDevice.equals(stackingFromDevice)) {
			deviceSettings.put(stackingToDevice, stackingToDevice.getDeviceSettings());
		}
		Map<AbstractRobot, RobotSettings> robotSettings = new HashMap<AbstractRobot, RobotSettings>();
		robotSettings.put(robot, robot.getRobotSettings());
		for (AbstractProcessStep step : processSteps) {
			if (step instanceof DeviceStep) {
				// if only one work area present: use it
				DeviceStep deviceStep = (DeviceStep) step;
				if (deviceStep.getDevice().getWorkAreas().size() > 0) {
					
					WorkArea workArea = deviceStep.getDevice().getWorkAreas().get(0);
					for (WorkArea wk: deviceStep.getDevice().getWorkAreas()) {
						if (!wk.isClone()) {
							workArea = wk;
						}
					}

					if ((deviceStep instanceof PickStep) && (deviceStep.getDevice() instanceof Conveyor)) {
						workArea = ((Conveyor) deviceStep.getDevice()).getRawWorkArea();
					} else if ((deviceStep instanceof PutStep) && (deviceStep.getDevice() instanceof Conveyor)) {
						workArea = ((Conveyor) deviceStep.getDevice()).getFinishedWorkArea();
					}
					
					deviceStep.getDeviceSettings().setWorkArea(workArea);
					if (step instanceof RobotStep) {
						((RobotStep) step).getRobotSettings().setWorkArea(workArea);						
					}
					
					// if clampings present: use them
					if (workArea.getClampings().size() > 0) {
						Clamping clamping = workArea.getClampings().iterator().next();
						deviceSettings.get(deviceStep.getDevice()).setClamping(workArea, clamping);
						if (step instanceof PickStep) {
							if (((PickStep) step).getDevice() instanceof AbstractCNCMachine) {
								((PickStep) step).getRobotSettings().setSmoothPoint(new Coordinates(clamping.getSmoothFromPoint()));
							}
						} else if (step instanceof PutStep) {
							if (((PutStep) step).getDevice() instanceof AbstractCNCMachine) {
								((PutStep) step).getRobotSettings().setSmoothPoint(new Coordinates(clamping.getSmoothToPoint()));
							}
						}
					}
				}
			}
			if (step instanceof RobotStep) {
				RobotStep robotStep = (RobotStep) step;
				// we assume there always is at least one head!
				//TODO review for later (more than 2 heads, other steps after processing)
				GripperHead headA = robotStep.getRobot().getGripperBody().getGripperHeadByName("A");
				GripperHead headB = robotStep.getRobot().getGripperBody().getGripperHeadByName("B");
				robotStep.getRobotSettings().setGripperHead(headA);
				if ((robotStep instanceof PickStep) && (((PickStep) robotStep).getDevice() instanceof AbstractCNCMachine)) {
					robotStep.getRobotSettings().setGripperHead(headB);
				}
				if ((robotStep instanceof PutStep) && (((PutStep) robotStep).getDevice() instanceof AbstractStackingDevice)) {
					robotStep.getRobotSettings().setGripperHead(headB);
				}
			}
		}
		ProcessFlow processFlow = new ProcessFlow("", processSteps, deviceSettings, robotSettings, new Timestamp(System.currentTimeMillis()), null);
		
		return processFlow;
	}

	public void updateProcessFlow(final ProcessFlow processFlow) throws DuplicateProcessFlowNameException, IllegalArgumentException {
		try {
			int idForName = ProcessFlowMapper.getProcessFlowIdForName(processFlow.getName());
			if (idForName == -1) {
				saveProcessFlow(processFlow);
			} else if (idForName == processFlow.getId()) {
				if (processFlow.getId() > 0) {
					// update
					logger.info("Updating processflow with id: [" + processFlow.getId() + "] and name: [" + processFlow.getName() + "].");
					processFlowMapper.updateProcessFlow(processFlow);
				} else {
					throw new IllegalArgumentException("ProcessFlow should have a valid id for save");
				}
			} else {
				throw new DuplicateProcessFlowNameException();
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void updateLastOpened(final ProcessFlow processFlow) {
		try {
			processFlowMapper.updateLastOpened(processFlow);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void saveProcessFlow(final ProcessFlow processFlow) throws DuplicateProcessFlowNameException {
		try {
			int idForName = ProcessFlowMapper.getProcessFlowIdForName(processFlow.getName());
			if (idForName == -1) {
				logger.info("Saving processflow with name: [" + processFlow.getName() + "].");
				processFlowMapper.saveProcessFlow(processFlow);
			} else {
				throw new DuplicateProcessFlowNameException();
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void deleteProcessFlow(final ProcessFlow processFlow) {
		try {
			processFlowMapper.deleteProcessFlow(processFlow);
			processFlow.setId(0);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public ProcessFlow getActiveProcessFlow() {
		return this.activeProcessFlow;
	}
}
