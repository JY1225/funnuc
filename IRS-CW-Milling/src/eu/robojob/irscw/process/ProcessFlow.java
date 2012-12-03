package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDeviceSettings;
import eu.robojob.irscw.external.device.ClampingType;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotSettings;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;

public class ProcessFlow {
	
	public enum Mode {
		TEACH, READY, AUTO, PAUSED, STOPPED, CONFIG, FINISHED
	}
	
	private List<AbstractProcessStep> processSteps;
	
	private Map<AbstractDevice, AbstractDevice.AbstractDeviceSettings> deviceSettings;
	private Map<AbstractRobot, AbstractRobot.AbstractRobotSettings> robotSettings;
		
	private Integer totalAmount;
	private Integer finishedAmount;
	
	private boolean needsTeaching;
	
	private String name;
	
	private Set<ProcessFlowListener> listeners;
	private Mode mode;
	
	private static final Logger logger = Logger.getLogger(ProcessFlow.class);
	
	private ClampingType clampingType;
	
	private int currentStepIndex;
	
	//TODO refactor constructors so there is one constructor, called by the others
	public ProcessFlow(String name) {
		this.clampingType = new ClampingType();
		this.name = name;
		this.processSteps = new ArrayList<AbstractProcessStep>();
		this.deviceSettings = new HashMap<AbstractDevice, AbstractDevice.AbstractDeviceSettings>();
		this.robotSettings = new HashMap<AbstractRobot, AbstractRobot.AbstractRobotSettings>();
		needsTeaching = true;
		this.totalAmount = 0;
		this.finishedAmount = 0;
		this.mode = Mode.CONFIG;
		this.listeners = new HashSet<ProcessFlowListener>();
		initialize();
	}
			
	public ProcessFlow(String name, List<AbstractProcessStep>processSteps, Map<AbstractDevice, AbstractDevice.AbstractDeviceSettings> deviceSettings,
			Map<AbstractRobot, AbstractRobot.AbstractRobotSettings> robotSettings) {
		this.name = name;
		this.clampingType = new ClampingType();
		needsTeaching = true;
		this.deviceSettings = deviceSettings;
		this.robotSettings = robotSettings;
		this.listeners = new HashSet<ProcessFlowListener>();
		this.totalAmount = 0;
		this.finishedAmount = 0;
		this.mode = Mode.CONFIG;
		setUpProcess(processSteps);
		initialize();
	}
	
	public void restart() {
		logger.info("restarted");
		incrementFinishedAmount();
		currentStepIndex = 0;
		processProcessFlowEvent(new ActiveStepChangedEvent(this, null, ActiveStepChangedEvent.NONE_ACTIVE));
	}
	
	public void initialize() {
		logger.info("Process flow: initialize");
		currentStepIndex = 0;
		loadAllDeviceSettings();
		loadAllRobotSettings();
		setFinishedAmount(0);
		//setMode(Mode.READY);
		processProcessFlowEvent(new ActiveStepChangedEvent(this, null, ActiveStepChangedEvent.NONE_ACTIVE));
	}
	
	public void loadFromOtherProcessFlow(ProcessFlow processFlow) {
		this.processSteps = processFlow.getProcessSteps();
		this.deviceSettings = processFlow.getDeviceSettings();
		this.robotSettings = processFlow.getRobotSettings();
		initialize();
	}
	
	public Map<AbstractDevice, AbstractDevice.AbstractDeviceSettings> getDeviceSettings() {
		return deviceSettings;
	}
	
	public Map<AbstractRobot, AbstractRobot.AbstractRobotSettings> getRobotSettings() {
		return robotSettings;
	}
	
	public boolean hasNextStep() {
		if (getProcessSteps().size() > currentStepIndex + 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasStep() {
		if (getProcessSteps().size() > currentStepIndex) {
			return true;
		} else {
			return false;
		}
	}
	
	public void nextStep() {
		currentStepIndex++;
	}
	
	public AbstractProcessStep getCurrentStep() {
		if (hasStep()) {
			return getStep(currentStepIndex);
		} else {
			return null;
		}
	}
	
	public AbstractProcessStep getNextStep() {
		if (hasNextStep()) {
			return getProcessSteps().get(currentStepIndex + 1);
		} else if (finishedAmount < totalAmount) {
			return getProcessSteps().get(0);
		} else {
			return null;
		}
	}
	
	public int getCurrentStepIndex() {
		return currentStepIndex;
	}
	
	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		if (mode != this.mode) { 
			this.mode = mode;
			if (mode == Mode.STOPPED) {
				logger.info("ProcessFlow mode is now stopped");
				initialize();
			}
			processProcessFlowEvent(new ModeChangedEvent(this, mode));
		}
	}

	public void addListener(ProcessFlowListener listener) {
		logger.debug("added listener: " + listener);
		this.listeners.add(listener);
	}
	
	public void removeListener(ProcessFlowListener listener) {
		logger.debug("removed listener: " + listener);
		this.listeners.remove(listener);
	}
	
	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getFinishedAmount() {
		return finishedAmount;
	}
	
	public void incrementFinishedAmount() {
		setFinishedAmount(finishedAmount + 1);
	}

	public void setFinishedAmount(int finishedAmount) {
		this.finishedAmount = finishedAmount;
		processProcessFlowEvent(new FinishedAmountChangedEvent(this, finishedAmount, totalAmount));
	}

	public void processProcessFlowEvent(ProcessFlowEvent event) {
		logger.info("processing event: " + event);
		switch(event.getId()) {
			case ProcessFlowEvent.MODE_CHANGED:
				for (ProcessFlowListener listener : listeners) {
					listener.modeChanged((ModeChangedEvent) event);
				}
				break;
			case ProcessFlowEvent.ACTIVE_STEP_CHANGED:
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
		initialize();
	}
	
	public void removeSteps(List<AbstractProcessStep> steps) {
		processSteps.removeAll(steps);
		initialize();
	}
	
	public void addStepAfter(AbstractProcessStep step, AbstractProcessStep newStep) {
		if (processSteps.indexOf(step) == -1) {
			throw new IllegalArgumentException("Could not find this step");
		} else {
			processSteps.add(processSteps.indexOf(step) + 1, newStep);
			newStep.setProcessFlow(this);
		}
		initialize();
	}
	
	public void addStepBefore(AbstractProcessStep step, AbstractProcessStep newStep) {
		if (processSteps.indexOf(step) == -1) {
			throw new IllegalArgumentException("Could not find this step");
		} else {
			processSteps.add(processSteps.indexOf(step), newStep);
			newStep.setProcessFlow(this);
		}
		initialize();
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
	
	public boolean isTeached() {
		for (AbstractProcessStep step : processSteps) {
			if (step instanceof PickStep) {
				PickStep pickStep = (PickStep) step;
				if (pickStep.needsTeaching() && pickStep.getRelativeTeachedOffset() == null) {
					return false;
				}
			} else if (step instanceof PutStep) {
				PutStep putStep = (PutStep) step;
				if (putStep.needsTeaching() && putStep.getRelativeTeachedOffset() == null) {
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

	public ClampingType getClampingType() {
		return clampingType;
	}

	public void setClampingType(ClampingType clampingType) {
		this.clampingType = clampingType;
	}
	
}
