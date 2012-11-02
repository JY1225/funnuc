package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDeviceSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotSettings;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;

public class ProcessFlow {
	
	enum ProcessFlowType  {
		CNC_MILLING, CNC_TURNING;
	}
	
	public enum Mode {
		TEACH, READY, AUTO, PAUSED, STOPPED, CONFIG
	}
	
	//private static Logger logger = Logger.getLogger(ProcessFlow.class);
	
	private List<AbstractProcessStep> processSteps;
	
	private Map<AbstractDevice, AbstractDevice.AbstractDeviceSettings> deviceSettings;
	private Map<AbstractRobot, AbstractRobot.AbstractRobotSettings> robotSettings;
		
	private int totalAmount;
	private int finishedAmount;
	
	private boolean needsTeaching;
	
	private String name;
	
	private Set<ProcessFlowListener> listeners;
	private Mode mode;
	
	
	//TODO refactor constructors so there is one constructor, called by the others
	public ProcessFlow(String name) {
		this.name = name;
		this.processSteps = new ArrayList<AbstractProcessStep>();
		this.deviceSettings = new HashMap<AbstractDevice, AbstractDevice.AbstractDeviceSettings>();
		this.robotSettings = new HashMap<AbstractRobot, AbstractRobot.AbstractRobotSettings>();
		needsTeaching = true;
		this.totalAmount = 0;
		this.finishedAmount = 0;
		this.mode = Mode.CONFIG;
		this.listeners = new HashSet<ProcessFlowListener>();
	}
			
	public ProcessFlow(String name, List<AbstractProcessStep>processSteps, Map<AbstractDevice, AbstractDevice.AbstractDeviceSettings> deviceSettings,
			Map<AbstractRobot, AbstractRobot.AbstractRobotSettings> robotSettings) {
		this.name = name;
		needsTeaching = true;
		this.deviceSettings = deviceSettings;
		this.robotSettings = robotSettings;
		this.listeners = new HashSet<ProcessFlowListener>();
		this.totalAmount = 0;
		this.finishedAmount = 0;
		this.mode = Mode.CONFIG;
		setUpProcess(processSteps);
	}
	
	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		processProcessFlowEvent(new ModeChangedEvent(this, mode));
	}

	public void addListener(ProcessFlowListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ProcessFlowListener listener) {
		this.listeners.remove(listener);
	}
	
	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getFinishedAmount() {
		return finishedAmount;
	}

	public void setFinishedAmount(int finishedAmount) {
		this.finishedAmount = finishedAmount;
		processProcessFlowEvent(new FinishedAmountChangedEvent(this, finishedAmount, totalAmount));
	}

	public void processProcessFlowEvent(ProcessFlowEvent event) {
		switch(event.getId()) {
			case ProcessFlowEvent.MODE_CHANGED:
				for (ProcessFlowListener listener : listeners) {
					listener.modeChanged((ModeChangedEvent) event);
				}
				break;
			case ProcessFlowEvent.ACTIVE_STEP_CHANGED:
				System.out.println("ACTIVE STEP CHANGED: " + event);
				for (ProcessFlowListener listener : listeners) {
					listener.activeStepChanged((ActiveStepChangedEvent) event);
				}
				break;
			case ProcessFlowEvent.EXCEPTION_OCCURED:
				for (ProcessFlowListener listener : listeners) {
					listener.exceptionOccured((ExceptionOccuredEvent) event);
				}
				break;
			case ProcessFlowEvent.DATA_CHANGED:
				for (ProcessFlowListener listener : listeners) {
					listener.dataChanged(event);
				}
				break;
			case ProcessFlowEvent.FINISHED_AMOUNT_CHANGED:
				for (ProcessFlowListener listener : listeners) {
					listener.finishedAmountChanged((FinishedAmountChangedEvent) event);
				}
				break;
			default:
				throw new IllegalArgumentException("Unkown event-id");
		}
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
		processProcessFlowEvent(new ProcessFlowEvent(this, ProcessFlowEvent.DATA_CHANGED));
	}
	
	public void removeSteps(List<AbstractProcessStep> steps) {
		processSteps.removeAll(steps);
		processProcessFlowEvent(new ProcessFlowEvent(this, ProcessFlowEvent.DATA_CHANGED));
	}
	
	public void addStepAfter(AbstractProcessStep step, AbstractProcessStep newStep) {
		if (processSteps.indexOf(step) == -1) {
			throw new IllegalArgumentException("Could not find this step");
		} else {
			processSteps.add(processSteps.indexOf(step) + 1, newStep);
			processProcessFlowEvent(new ProcessFlowEvent(this, ProcessFlowEvent.DATA_CHANGED));
		}
	}
	
	public void addStepBefore(AbstractProcessStep step, AbstractProcessStep newStep) {
		if (processSteps.indexOf(step) == -1) {
			throw new IllegalArgumentException("Could not find this step");
		} else {
			processSteps.add(processSteps.indexOf(step), newStep);
			processProcessFlowEvent(new ProcessFlowEvent(this, ProcessFlowEvent.DATA_CHANGED));
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
	
	public Set<AbstractDevice> getDevices() {
		Set<AbstractDevice> devices = new HashSet<AbstractDevice>();
		for (AbstractProcessStep step : processSteps) {
			devices.add(step.getDevice());
		}
		return devices;
	}
	
	public Set<AbstractRobot> getRobots() {
		Set<AbstractRobot> robots = new HashSet<AbstractRobot>();
		for (AbstractProcessStep step : processSteps) {
			if (step instanceof AbstractTransportStep) {
				AbstractTransportStep transportStep = (AbstractTransportStep) step;
				robots.add(transportStep.getRobot());
			}
		}
		return robots;
	}
}
