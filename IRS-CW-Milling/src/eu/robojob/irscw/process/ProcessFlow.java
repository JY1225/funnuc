package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotSettings;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class ProcessFlow {
	
	public enum Mode {
		CONFIG, 	// The initial mode, the ProcessFlow-data has not been checked. 
		READY, 		// The ProcessFlow is ready to be executed.
		TEACH, 		// The ProcessFlow is being executed in 'teach mode'.
		AUTO, 		// The ProcessFlow is being executed in 'auto mode'.
		PAUSED, 	// The Execution of ProcessFlow was paused.
		STOPPED, 	// The Execution of ProcessFlow was stopped.
		FINISHED	// The Execution of ProcessFlow has finished.
	}
	
	private List<AbstractProcessStep> processSteps;
	
	private Map<AbstractDevice, DeviceSettings> deviceSettings;		// device settings that are independent of the process steps
	private Map<AbstractRobot, RobotSettings> robotSettings;		// robot settings that are independent of the process steps
		
	private Integer totalAmount;
	private Integer finishedAmount;
	
	private String name;
	
	private Set<ProcessFlowListener> listeners;
	private Mode mode;
	
	private static Logger logger = LogManager.getLogger(ProcessFlow.class.getName());
	
	private ClampingManner clampingManner;
	
	public ProcessFlow(final String name, final List<AbstractProcessStep>processSteps, final Map<AbstractDevice, DeviceSettings> deviceSettings, final Map<AbstractRobot, 
			RobotSettings> robotSettings, final int totalAmount, final ClampingManner clampingManner) {
		this.name = name;
		this.clampingManner = clampingManner;
		this.deviceSettings = deviceSettings;
		this.robotSettings = robotSettings;
		this.listeners = new HashSet<ProcessFlowListener>();
		this.totalAmount = totalAmount;
		this.finishedAmount = 0;
		this.mode = Mode.CONFIG;
		setUpProcess(processSteps);
		initialize();
	}
	
	public ProcessFlow(final String name, final List<AbstractProcessStep> processSteps, final Map<AbstractDevice, DeviceSettings> deviceSettings, final Map<AbstractRobot, RobotSettings> robotSettings) {
		this(name, processSteps, deviceSettings, robotSettings, 0, new ClampingManner());
	}
	
	public ProcessFlow(final String name) {
		this(name, new ArrayList<AbstractProcessStep>(), new HashMap<AbstractDevice, DeviceSettings>(), new HashMap<AbstractRobot, RobotSettings>());
	}
	
	public void initialize() {
		logger.info("Initializing [" + getName() + "].");
		loadAllSettings();
		setFinishedAmount(0);
	}
	
	public void loadFromOtherProcessFlow(final ProcessFlow processFlow) {
		this.processSteps = processFlow.getProcessSteps();
		for (AbstractProcessStep step : this.processSteps) {
			step.setProcessFlow(this);
		}
		this.deviceSettings = processFlow.getDeviceSettings();
		this.robotSettings = processFlow.getRobotSettings();
		initialize();
		this.totalAmount = processFlow.getTotalAmount();
		this.finishedAmount = processFlow.getFinishedAmount();
		this.clampingManner.setType(processFlow.getClampingType().getType());
		for (AbstractDevice device : getDevices()) {
			if (device instanceof BasicStackPlate) {
				((BasicStackPlate) device).placeFinishedWorkPieces(processFlow.getFinishedAmount());
			}
		}
	}
	
	public Map<AbstractDevice, DeviceSettings> getDeviceSettings() {
		return deviceSettings;
	}
	
	public Map<AbstractRobot, RobotSettings> getRobotSettings() {
		return robotSettings;
	}
	
	public Mode getMode() {
		return mode;
	}

	public void setMode(final Mode mode) {
		if (mode != this.mode) { 
			this.mode = mode;
			processProcessFlowEvent(new ModeChangedEvent(this, mode));
		}
	}

	public void addListener(final ProcessFlowListener listener) {
		this.listeners.add(listener);
		logger.debug("Now listening to [" + toString() + "]: " + listener.toString());
	}
	
	public void removeListener(final ProcessFlowListener listener) {
		this.listeners.remove(listener);
		logger.debug("Stopped listening to [" + toString() + "]: " + listener.toString());
	}
	
	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(final Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getFinishedAmount() {
		return finishedAmount;
	}
	
	public void incrementFinishedAmount() {
		setFinishedAmount(finishedAmount + 1);
	}

	public void setFinishedAmount(final int finishedAmount) {
		this.finishedAmount = finishedAmount;
		processProcessFlowEvent(new FinishedAmountChangedEvent(this, finishedAmount, totalAmount));
	}

	public void processProcessFlowEvent(final ProcessFlowEvent event) {
		switch(event.getId()) {
			case ProcessFlowEvent.MODE_CHANGED:
				for (ProcessFlowListener listener : listeners) {
					listener.modeChanged((ModeChangedEvent) event);
				}
				break;
			case ProcessFlowEvent.ACTIVE_STEP_CHANGED:
				for (ProcessFlowListener listener : listeners) {
					listener.statusChanged((StatusChangedEvent) event);
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
				throw new IllegalArgumentException("Unkown event type.");
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<AbstractProcessStep> getProcessSteps() {
		return processSteps;
	}
	
	private void setUpProcess(final List<AbstractProcessStep> processSteps) {
		this.processSteps = processSteps;
		if (processSteps.size() < 2) {
			throw new IllegalArgumentException("A process should have a minimum of 2 steps (Pick & Put).");
		} 
	}
	
	public void addStep(final int index, final AbstractProcessStep newStep) {
		processSteps.add(index, newStep);
		newStep.setProcessFlow(this);
	}
	
	public void addStep(final AbstractProcessStep newStep) {
		processSteps.add(newStep);
		newStep.setProcessFlow(this);
	}
	
	public int getStepIndex(final AbstractProcessStep step) {
		return processSteps.indexOf(step);
	}
	
	public AbstractProcessStep getStep(final int index) {
		return processSteps.get(index);
	}
	
	public void removeStep(final AbstractProcessStep step) {
		processSteps.remove(step);
		initialize();		// always initialize after updates to the process flow
	}
	
	public void removeSteps(final List<AbstractProcessStep> steps) {
		processSteps.removeAll(steps);
		initialize();		// always initialize after updates to the process flow
	}
	
	public void addStepAfter(final AbstractProcessStep step, final AbstractProcessStep newStep) {
		if (processSteps.indexOf(step) == -1) {
			throw new IllegalArgumentException("Could not find step: [" + step + "].");
		} else {
			processSteps.add(processSteps.indexOf(step) + 1, newStep);
			newStep.setProcessFlow(this);
		}
		initialize();		// always initialize after updates to the process flow
	}
	
	public void addStepBefore(final AbstractProcessStep step, final AbstractProcessStep newStep) {
		if (processSteps.indexOf(step) == -1) {
			throw new IllegalArgumentException("Could not find this step");
		} else {
			processSteps.add(processSteps.indexOf(step), newStep);
			newStep.setProcessFlow(this);
		}
		initialize();		// always initialize after updates to the process flow
	}
	
	public DeviceSettings getDeviceSettings(final AbstractDevice device) {
		return deviceSettings.get(device);
	}
	
	public RobotSettings getRobotSettings(final AbstractRobot robot) {
		return robotSettings.get(robot);
	}
	
	public void setDeviceSettings(final AbstractDevice device, final DeviceSettings settings) {
		deviceSettings.put(device, settings);
	}
	
	public void setRobotSettings(final AbstractRobot robot, final RobotSettings settings) {
		robotSettings.put(robot, settings);
	}
	
	public void loadAllDeviceSettings() {
		for (Entry<AbstractDevice, DeviceSettings> settings : deviceSettings.entrySet()) {
			settings.getKey().loadDeviceSettings(settings.getValue());
		}
	}
	
	public void loadAllRobotSettings() {
		for (Entry<AbstractRobot, RobotSettings> settings : robotSettings.entrySet()) {
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
				if ((!pickStep.getDevice().validatePickSettings(pickStep.getDeviceSettings())) || (!pickStep.getRobot().validatePickSettings(pickStep.getRobotSettings()))) {
					return false;
				}
			} else if (step instanceof PutStep) {
				PutStep putStep = (PutStep) step;
				if ((!putStep.getDevice().validatePutSettings(putStep.getDeviceSettings())) || (!putStep.getRobot().validatePutSettings(putStep.getRobotSettings()))) {
					return false;
				}
			} else if (step instanceof InterventionStep) {
				InterventionStep interventionStep = (InterventionStep) step;
				if (!interventionStep.getDevice().validateInterventionSettings(interventionStep.getDeviceSettings())) {
					return false;
				}
			} else if (step instanceof ProcessingStep) {
				ProcessingStep processingStep = (ProcessingStep) step;
				if (!processingStep.getDevice().validateStartCyclusSettings(processingStep.getDeviceSettings())) {
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
			if (step instanceof DeviceStep) {
				devices.add(((DeviceStep) step).getDevice());
			}
		}
		return devices;
	}
	
	public Set<AbstractRobot> getRobots() {
		Set<AbstractRobot> robots = new HashSet<AbstractRobot>();
		for (AbstractProcessStep step : processSteps) {
			if (step instanceof RobotStep) {
				robots.add(((RobotStep) step).getRobot());
			}
		}
		return robots;
	}

	public ClampingManner getClampingType() {
		return clampingManner;
	}

	public void setClampingType(final ClampingManner clampingType) {
		this.clampingManner = clampingType;
	}
	
}
