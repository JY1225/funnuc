package eu.robojob.irscw.process;

import java.sql.Timestamp;
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
import eu.robojob.irscw.external.device.stacking.BasicStackPlateSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotSettings;
import eu.robojob.irscw.process.event.DataChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessChangedEvent;
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
		
	private Integer finishedAmount;
	
	private int id;
	
	private String name;
	private Timestamp creation;
	private Timestamp lastOpened;
	
	private Set<ProcessFlowListener> listeners;
	private Mode mode;
	
	private Map<Integer, Integer> currentIndices;
	
	private static Logger logger = LogManager.getLogger(ProcessFlow.class.getName());
	
	private static final int WORKPIECE_0_ID = 0;
	private static final int WORKPIECE_1_ID = 1;
	
	private ClampingManner clampingManner;
	
	public ProcessFlow(final String name, final List<AbstractProcessStep> processSteps, final Map<AbstractDevice, DeviceSettings> deviceSettings, final Map<AbstractRobot, 
			RobotSettings> robotSettings, final ClampingManner clampingManner, final Timestamp creation, final Timestamp lastOpened) {
		this.name = name;
		this.clampingManner = clampingManner;
		this.deviceSettings = deviceSettings;
		this.robotSettings = robotSettings;
		this.listeners = new HashSet<ProcessFlowListener>();
		this.finishedAmount = 0;
		this.mode = Mode.CONFIG;
		this.currentIndices = new HashMap<Integer, Integer>();
		this.creation = creation;
		this.lastOpened = lastOpened;
		setUpProcess(processSteps);
		this.currentIndices = new HashMap<Integer, Integer>();
		//TODO more than two concurrent steps possible?
		setCurrentIndex(WORKPIECE_0_ID, -1);
		setCurrentIndex(WORKPIECE_1_ID, -1);
		setFinishedAmount(0);
	}
	
	public ProcessFlow(final String name, final List<AbstractProcessStep> processSteps, final Map<AbstractDevice, DeviceSettings> deviceSettings, final Map<AbstractRobot, RobotSettings> robotSettings,
			final Timestamp creation, final Timestamp lastOpened) {
		this(name, processSteps, deviceSettings, robotSettings, new ClampingManner(), creation, lastOpened);
	}
	
	public ProcessFlow(final String name) {
		this(name, new ArrayList<AbstractProcessStep>(), new HashMap<AbstractDevice, DeviceSettings>(), new HashMap<AbstractRobot, RobotSettings>(), null, null);
	}
	
	public void initialize() {
		logger.info("Initializing [" + toString() + "].");
		this.currentIndices = new HashMap<Integer, Integer>();
		//TODO more than two concurrent steps possible?
		setCurrentIndex(WORKPIECE_0_ID, -1);
		setCurrentIndex(WORKPIECE_1_ID, -1);
		loadAllSettings();
		setFinishedAmount(0);
	}
	
	public void setId(final int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void loadFromOtherProcessFlow(final ProcessFlow processFlow) {
		this.processSteps = processFlow.getProcessSteps();
		for (AbstractProcessStep step : this.processSteps) {
			step.setProcessFlow(this);
		}
		this.id = processFlow.getId();
		this.name = processFlow.getName();
		this.creation = processFlow.getCreation();
		this.lastOpened = new Timestamp(System.currentTimeMillis());
		this.deviceSettings = processFlow.getDeviceSettings();
		this.robotSettings = processFlow.getRobotSettings();
		initialize();
		this.finishedAmount = processFlow.getFinishedAmount();
		this.clampingManner.setType(processFlow.getClampingType().getType());
		for (AbstractDevice device : getDevices()) {
			if (device instanceof BasicStackPlate) {
				((BasicStackPlate) device).placeFinishedWorkPieces(processFlow.getFinishedAmount());
			}
		}
		this.processProcessFlowEvent(new ProcessChangedEvent(this));
	}
	
	public int getCurrentIndex(final int workpieceId) {
		return currentIndices.get(workpieceId);
	}
	
	public void setCurrentIndex(final int workpieceId, final int index) {
		currentIndices.put(workpieceId, index);
	}
	
	public Map<AbstractDevice, DeviceSettings> getDeviceSettings() {
		return deviceSettings;
	}
	
	public Map<AbstractRobot, RobotSettings> getRobotSettings() {
		return robotSettings;
	}

	public Timestamp getCreation() {
		return creation;
	}

	public void setCreation(final Timestamp creation) {
		this.creation = creation;
	}

	public Timestamp getLastOpened() {
		return lastOpened;
	}

	public void setLastOpened(final Timestamp lastOpened) {
		this.lastOpened = lastOpened;
	}

	public Mode getMode() {
		return mode;
	}

	public synchronized void setMode(final Mode mode) {
		if (mode != this.mode) { 
			this.mode = mode;
			processProcessFlowEvent(new ModeChangedEvent(this, mode));
		}
	}

	public synchronized void addListener(final ProcessFlowListener listener) {
		this.listeners.add(listener);
		logger.debug("Now listening to [" + toString() + "]: " + listener.toString());
	}
	
	public synchronized void removeListener(final ProcessFlowListener listener) {
		this.listeners.remove(listener);
		logger.debug("Stopped listening to [" + toString() + "]: " + listener.toString());
	}
	
	public int getTotalAmount() {
		for (AbstractDevice device : deviceSettings.keySet()) {
			if (device instanceof BasicStackPlate) {
				BasicStackPlateSettings basicStackPlateSettings = (BasicStackPlateSettings) deviceSettings.get(device);
				return basicStackPlateSettings.getAmount();
			}
		}
		return 0;
	}

	public int getFinishedAmount() {
		return finishedAmount;
	}
	
	public void incrementFinishedAmount() {
		setFinishedAmount(finishedAmount + 1);
	}

	public void setFinishedAmount(final int finishedAmount) {
		this.finishedAmount = finishedAmount;
		processProcessFlowEvent(new FinishedAmountChangedEvent(this, finishedAmount, getTotalAmount()));
	}

	public synchronized void processProcessFlowEvent(final ProcessFlowEvent event) {
		// use temporary set so during the invocation of events on listeners the listener list can be updated
		Set<ProcessFlowListener> tempListeners = new HashSet<ProcessFlowListener>(listeners);
		switch(event.getId()) {
			case ProcessFlowEvent.MODE_CHANGED:
				for (ProcessFlowListener listener : tempListeners) {
					listener.modeChanged((ModeChangedEvent) event);
				}
				break;
			case ProcessFlowEvent.ACTIVE_STEP_CHANGED:
				StatusChangedEvent scEvent = (StatusChangedEvent) event;
				if (scEvent.getActiveStep() != null) {
					setCurrentIndex(scEvent.getWorkPieceId(), getStepIndex(scEvent.getActiveStep()));
				} else {
					setCurrentIndex(scEvent.getWorkPieceId(), -1);
				}
				for (ProcessFlowListener listener : tempListeners) {
					listener.statusChanged(scEvent);
				}
				break;
			case ProcessFlowEvent.DATA_CHANGED:
				for (ProcessFlowListener listener : tempListeners) {
					listener.dataChanged((DataChangedEvent) event);
				}
				break;
			case ProcessFlowEvent.FINISHED_AMOUNT_CHANGED:
				for (ProcessFlowListener listener : tempListeners) {
					listener.finishedAmountChanged((FinishedAmountChangedEvent) event);
				}
				break;
			case ProcessFlowEvent.EXCEPTION_OCCURED:
				for (ProcessFlowListener listener : tempListeners) {
					listener.exceptionOccured((ExceptionOccuredEvent) event);
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
		for (AbstractProcessStep step : processSteps) {
			step.setProcessFlow(this);
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
	
	public String toString() {
		return "ProcessFlow: " + getName();
	}
	
}
