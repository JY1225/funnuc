package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDeviceSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotSettings;

public class ProcessFlow {
	
	enum ProcessFlowType  {
		CNC_MILLING, CNC_TURNING;
	}
	
	//private static Logger logger = Logger.getLogger(ProcessFlow.class);
	
	private List<AbstractProcessStep> processSteps;
	
	private Map<AbstractDevice, AbstractDevice.AbstractDeviceSettings> deviceSettings;
	private Map<AbstractRobot, AbstractRobot.AbstractRobotSettings> robotSettings;
		
	private boolean needsTeaching;
	
	private String name;
	
	
	//TODO refactor constructors so there is one constructor, called by the others
	public ProcessFlow(String name) {
		this.name = name;
		this.processSteps = new ArrayList<AbstractProcessStep>();
		this.deviceSettings = new HashMap<AbstractDevice, AbstractDevice.AbstractDeviceSettings>();
		this.robotSettings = new HashMap<AbstractRobot, AbstractRobot.AbstractRobotSettings>();
		needsTeaching = true;
	}
			
	public ProcessFlow(String name, List<AbstractProcessStep>processSteps, Map<AbstractDevice, AbstractDevice.AbstractDeviceSettings> deviceSettings,
			Map<AbstractRobot, AbstractRobot.AbstractRobotSettings> robotSettings) {
		this.name = name;
		needsTeaching = true;
		this.deviceSettings = deviceSettings;
		this.robotSettings = robotSettings;
		setUpProcess(processSteps);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AbstractProcessStep> getProcessSteps() {
		return processSteps;
	}
	
	private void setUpProcess(List<AbstractProcessStep> processSteps) {
		this.processSteps = processSteps;
		if (processSteps.size() < 2) {
			throw new IllegalArgumentException("A process should have a minimum of 2 step (Pick & Put)");
		} 
	}
	
	public boolean needsTeaching() {
		return needsTeaching;
	}

	public void setNeedsTeaching(boolean needsTeaching) {
		this.needsTeaching = needsTeaching;
	}

	public boolean willNeedDevice(AbstractDevice device, int currentStepNumber) {
		for (int i = currentStepNumber; i < processSteps.size(); i++) {
			if (processSteps.get(i).getDevice().equals(device)) {
				return true;
			}
		}
		return false;
	}
	
	public void addStep(int index, AbstractProcessStep newStep) {
		processSteps.add(index, newStep);
		newStep.setProcessFlow(this);
	}
	
	public void addStep(AbstractProcessStep newStep) {
		processSteps.add(newStep);
		newStep.setProcessFlow(this);
	}
	
	public int getStepIndex (AbstractProcessStep step) {
		return processSteps.indexOf(step);
	}
	
	public AbstractProcessStep getStep(int index) {
		return processSteps.get(index);
	}
	
	public boolean occursMultipleTimes(AbstractProcessStep step) {
		if ((processSteps.indexOf(step) != -1) && (processSteps.indexOf(step) != processSteps.lastIndexOf(step))) {
			return true;
		} else {
			return false;
		}
	}
	
	public void removeStep (AbstractProcessStep step) {
		processSteps.remove(step);
	}
	
	public void removeSteps(List<AbstractProcessStep> steps) {
		processSteps.removeAll(steps);
	}
	
	public void addStepAfter(AbstractProcessStep step, AbstractProcessStep newStep) {
		if (processSteps.indexOf(step) == -1) {
			throw new IllegalArgumentException("Could not find this step");
		} else {
			processSteps.add(processSteps.indexOf(step) + 1, newStep);
		}
	}
	
	public void addStepBefore(AbstractProcessStep step, AbstractProcessStep newStep) {
		if (processSteps.indexOf(step) == -1) {
			throw new IllegalArgumentException("Could not find this step");
		} else {
			processSteps.add(processSteps.indexOf(step), newStep);
		}
	}
	
	public AbstractDeviceSettings getDeviceSettings(AbstractDevice device) {
		return deviceSettings.get(device);
	}
	
	public AbstractRobotSettings getRobotSettings(AbstractRobot robot) {
		return robotSettings.get(robot);
	}
	
	public void setDeviceSettings(AbstractDevice device, AbstractDeviceSettings settings) {
		deviceSettings.put(device, settings);
	}
	
	public void setRobotSettings(AbstractRobot robot, AbstractRobotSettings settings) {
		robotSettings.put(robot, settings);
	}
	
	public void loadAllDeviceSettings() {
		for (Entry<AbstractDevice, AbstractDeviceSettings> settings : deviceSettings.entrySet()) {
			settings.getKey().loadDeviceSettings(settings.getValue());
		}
	}
	
	public void loadAllRobotSettings() {
		for (Entry<AbstractRobot, AbstractRobotSettings> settings : robotSettings.entrySet()) {
			settings.getKey().loadRobotSettings(settings.getValue());
		}
	}
	
	public void loadAllSettings() {
		loadAllDeviceSettings();
		loadAllRobotSettings();
	}
	
	public boolean isConfigured() {
		loadAllDeviceSettings();
		for (AbstractProcessStep step : processSteps) {
			if (step instanceof PickStep) {
				PickStep pickStep = (PickStep) step;
				if (  (!pickStep.getDevice().validatePickSettings(pickStep.getDeviceSettings())) || 
						(!pickStep.getRobot().validatePickSettings(pickStep.getRobotSettings()))  ) {
					return false;
				}
			} else if (step instanceof PutStep) {
				PutStep putStep = (PutStep) step;
				if (  (!putStep.getDevice().validatePutSettings(putStep.getDeviceSettings())) || 
						(!putStep.getRobot().validatePutSettings(putStep.getRobotSettings()))  ) {
					return false;
				}
			} else if (step instanceof InterventionStep) {
				InterventionStep interventionStep = (InterventionStep) step;
				if (!interventionStep.getDevice().validateInterventionSettings(interventionStep.getInterventionSettings())) {
					return false;
				}
			} else if (step instanceof ProcessingStep) {
				ProcessingStep processingStep = (ProcessingStep) step;
				if (!processingStep.getDevice().validateStartCyclusSettings(processingStep.getStartCyclusSettings())) {
					return false;
				}
			}
		}
		return true;
	}
}
