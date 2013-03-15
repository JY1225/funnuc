package eu.robojob.irscw.process.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.external.device.stacking.AbstractStackingDevice;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.DeviceStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.ProcessingWhileWaitingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.RobotStep;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.util.Translator;

/**
 * This optimized Automate-thread will be able to (sometimes) execute 2 steps in parallel
 */
public class AutomateOptimizedThread extends Thread implements ProcessExecutor {

	private static Logger logger = LogManager.getLogger(AutomateOptimizedThread.class.getName());
	private static final int WORKPIECE_0_ID = 0;
	private static final int WORKPIECE_1_ID = 1;
	protected static final String OTHER_EXCEPTION = "Exception.otherException";
	
	private static final int WAIT_FOR_ROBOT_ACTION_FINISHED_DELAY = 500;
	
	private ProcessFlow processFlow;
	private boolean running;
	private boolean finished;
	
	private Map<Integer, Boolean> isRunning;
	private int mainProcessIndex;
	private int secondProcessIndex;
	private boolean paused;

	private Object syncObject;
	
	public AutomateOptimizedThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.syncObject = new Object();
		reset();
	}
	
	public void reset() {
		this.finished = false;
		this.mainProcessIndex = WORKPIECE_0_ID;
		this.secondProcessIndex = WORKPIECE_1_ID;
		this.isRunning = new HashMap<Integer, Boolean>();
		isRunning.put(WORKPIECE_0_ID, false);
		isRunning.put(WORKPIECE_1_ID, false);	
		getProcessFlow().setCurrentIndex(WORKPIECE_0_ID, -1);
		getProcessFlow().setCurrentIndex(WORKPIECE_1_ID, -1);
	}
	
	public void setCurrentIndices(final int workPiece0Index, final int workPiece1Index) {
		getProcessFlow().setCurrentIndex(WORKPIECE_0_ID, workPiece0Index);
		getProcessFlow().setCurrentIndex(WORKPIECE_1_ID, workPiece1Index);
	}
	
	//TODO add intervention steps execution
	@Override
	public void run() {
		logger.debug("Started execution, processflow [" + processFlow + "].");
		this.finished = false;
		this.running = true;
		this.paused = false;
		try {
			processFlow.setMode(ProcessFlow.Mode.AUTO);
			setRunning(true);
			try {
				if (getProcessFlow().getCurrentIndex(WORKPIECE_0_ID) == -1) {
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.PREPARE, WORKPIECE_0_ID));
					for (AbstractRobot robot :processFlow.getRobots()) {	// first recalculate TCPs
						robot.recalculateTCPs();
					}
					for (AbstractDevice device: processFlow.getDevices()) {	// prepare devices for this processflow
						device.prepareForProcess(processFlow);
					}
					getProcessFlow().setCurrentIndex(WORKPIECE_0_ID, 0);
				}
				if (getProcessFlow().getCurrentIndex(WORKPIECE_1_ID) == -1) {
					getProcessFlow().setCurrentIndex(WORKPIECE_1_ID, 0);
				}
				for (AbstractDevice device : getProcessFlow().getDevices()) {
					if (device instanceof AbstractCNCMachine) {
						((AbstractCNCMachine) device).indicateOperatorRequested(false);
					}
				}
				while (isRunning()) {
					switch (mainProcessIndex) {
						case WORKPIECE_0_ID:
							executeProcess(mainProcessIndex, WORKPIECE_1_ID);
							break;
						case WORKPIECE_1_ID:
							executeProcess(mainProcessIndex, WORKPIECE_0_ID);
							break;
						default:
							throw new IllegalStateException("Unknown main process index [" + mainProcessIndex + "].");
					}
					synchronized (syncObject) {
						syncObject.wait();
					}
				}
				if (finished) {
					processFlow.setMode(Mode.FINISHED);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
					for (AbstractDevice device : processFlow.getDevices()) {
						if (device instanceof CNCMillingMachine) {
							((CNCMillingMachine) device).indicateAllProcessed();
						}
					}
					for (AbstractRobot robot : processFlow.getRobots()) {
						robot.moveToHome();
					}
				} else if (paused) {
					processFlow.setMode(Mode.PAUSED);
				} else {
					processFlow.setMode(Mode.STOPPED);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
				}
			} catch (AbstractCommunicationException e) {
				e.printStackTrace();
				logger.error(e);
				processFlow.setMode(Mode.STOPPED);
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
				getProcessFlow().initialize();
			} catch (InterruptedException e) {
				if (!running) {
					logger.info("Execution of one or more steps got interrupted, so let't just stop.");
				} else {
					e.printStackTrace();
					logger.error(e);
					getProcessFlow().initialize();
				}
				processFlow.setMode(Mode.STOPPED);
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
				processFlow.setMode(Mode.STOPPED);
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Throwable t) {
			logger.error(t);
			t.printStackTrace();
		}
		logger.info(toString() + " ended...");
	}
	
	@Override
	public void pause() {
		this.paused = true;
		this.running = false;
	}
	
	private synchronized void executeProcess(final int mainProcessId, final int secondProcessId) throws InterruptedException, ExecutionException {
		int currentStepIndexMain = getProcessFlow().getCurrentIndex(mainProcessId);
		int currentStepIndexSecondary = getProcessFlow().getCurrentIndex(secondProcessId);
		int mainProcId = mainProcessId;
		int secondProcId = secondProcessId;
		// update indices for intervention steps
		AbstractProcessStep step1 = getProcessFlow().getStep(currentStepIndexMain);
		AbstractProcessStep secStep = getProcessFlow().getStep(currentStepIndexSecondary);
		if (step1 instanceof InterventionStep) {
			if (!((InterventionStep) step1).isInterventionNeeded(getProcessFlow().getFinishedAmount())) {
				currentStepIndexMain++;
			}
		}
		if (secStep instanceof InterventionStep) {
			if (!((InterventionStep) secStep).isInterventionNeeded(getProcessFlow().getFinishedAmount())) {
				currentStepIndexSecondary++;
			}
		}
		
		if (currentStepIndexMain == processFlow.getProcessSteps().size()) {
			currentStepIndexMain = currentStepIndexSecondary;
			currentStepIndexSecondary = 0;
			int temp = secondProcessId;
			secondProcId = mainProcessId;
			mainProcId = temp;
		}
		processFlow.setCurrentIndex(mainProcId, currentStepIndexMain);
		processFlow.setCurrentIndex(secondProcId, currentStepIndexSecondary);
		
		// if the first process is processing (and not as part of put and wait) the second process can use the robot
		// if the second process is processing (and not as part of pick and wait) the first process can use the robot
		// the first process has priority so it can finish first
		// sometimes, a special optimization is possible: when the first process does a pick and the second process's next step is 
		// a put in the same WorkArea, this should be allowed, before the first process does it's put
		//TODO Set put in machine always free after!
		if (!isRunning.get(mainProcId)) {
			AbstractProcessStep step = processFlow.getStep(currentStepIndexMain);
			
			if ((step instanceof PutStep) && (currentStepIndexMain > 0)) {
				PickStep previousStep = (PickStep) processFlow.getStep(currentStepIndexMain - 1);	// before put step is always a pick step
				AbstractProcessStep stepSecondProcess = processFlow.getStep(currentStepIndexSecondary);
				if (stepSecondProcess instanceof PutStep) {
					PutStep secondProcessPutStep = (PutStep) stepSecondProcess;
					if (secondProcessPutStep.getRobotSettings().getWorkArea().equals(previousStep.getDeviceSettings().getWorkArea())) {
						AbstractProcessStep stepSecond = processFlow.getStep(currentStepIndexSecondary);
						secondProcessPutStep.getRobotSettings().setFreeAfter(true);
						if (processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 2) {
							// next time: last piece: free after pick is true!
							previousStep.getRobotSettings().setFreeAfter(true);
						}
						ProcessStepExecutionThread exThread2 = new ProcessStepExecutionThread(stepSecond, secondProcId, this);
						isRunning.put(secondProcId, true);
						ThreadManager.submit(exThread2);
						return;
					}
				}
			}
			// if step is robot step, and robot is busy: wait here (note: only if step is not pick after wait step)
			if ((step instanceof RobotStep) && !(step instanceof PickAfterWaitStep) && !(step instanceof ProcessingWhileWaitingStep)) {
				while (((RobotStep) step).getRobot().isExecutionInProgress()) {
					logger.debug(("Waiting for " + ((RobotStep) step).getRobot()) + " to finish current action, step = " + step);
					Thread.sleep(WAIT_FOR_ROBOT_ACTION_FINISHED_DELAY);
				}
			}
			ProcessStepExecutionThread exThread = new ProcessStepExecutionThread(step, mainProcId, this);
			isRunning.put(mainProcId, true);
			ThreadManager.submit(exThread);
		}
		if (!isRunning.get(secondProcId)) {
			if (isConcurrentExecutionPossible(currentStepIndexMain, currentStepIndexSecondary) && (processFlow.getFinishedAmount() < processFlow.getTotalAmount() - 1)) {
				AbstractProcessStep stepSecond = processFlow.getStep(currentStepIndexSecondary);
				if (stepSecond instanceof PickAfterWaitStep) {
					((PickAfterWaitStep) stepSecond).getRobotSettings().setFreeAfter(true);
				}
				ProcessStepExecutionThread exThread2 = new ProcessStepExecutionThread(stepSecond, secondProcId, this);
				isRunning.put(secondProcId, true);
				ThreadManager.submit(exThread2);
			} /*else {
				AbstractProcessStep step = processFlow.getStep(currentStepIndexMain);
				if (!(step instanceof RobotStep)) {
					for (AbstractRobot robot : processFlow.getRobots()) {
						try {
							robot.moveToHome();
						} catch (AbstractCommunicationException | RobotActionException e) {
							notifyException(e);
						}
					}
				}
			} */
		}
	}
	
	private boolean isConcurrentExecutionPossible(final int stepIndexFirst, final int stepIndexSecond) {
		// concurrent execution is possible
		// - if both steps are NOT robotsteps
		//   -  if the second step is a device step and the device will not be used by the first process
		// - if the first is a robot step, the second not
		//   - if the second step is a device step and the device will not be used by the first process
		// - if the second step is a robot step, the first not
		//   - if the second step is a device step and the device will not be used by the first process, unless if it's a stacking device
		//   - if the gripper head used by the second step will not be used by the first process
		if (processFlow.getFinishedAmount() == (processFlow.getTotalAmount() - 1)) {
			return false;
		}
		AbstractProcessStep stepFirst = processFlow.getProcessSteps().get(stepIndexFirst);
		AbstractProcessStep stepSecond = processFlow.getProcessSteps().get(stepIndexSecond);
		if ((stepFirst instanceof InterventionStep) || (stepSecond instanceof InterventionStep)) {
			return false;
		}
		if (stepSecond instanceof RobotStep) {
			if (stepFirst instanceof RobotStep) {
				return false;	// both robot steps
			} else {			// first step is not a robot step
				if (willNeedGripperHead(stepIndexFirst, ((RobotStep) stepSecond).getRobotSettings().getGripperHead())) {
					return false;
				}
				if (stepSecond instanceof DeviceStep) {
					if (!(((DeviceStep) stepSecond).getDevice() instanceof AbstractStackingDevice)) {
						if (willNeedWorkArea(stepIndexFirst, ((DeviceStep) stepSecond).getDeviceSettings().getWorkArea())) {
							return false;
						}
					}
				}
			}
		} else {
			if (stepSecond instanceof DeviceStep) {
				if (willNeedWorkArea(stepIndexFirst, ((DeviceStep) stepSecond).getDeviceSettings().getWorkArea())) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean willNeedGripperHead(final int stepIndexFirst, final GripperHead gripperHead) {
		for (int i = stepIndexFirst; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			if (step instanceof RobotStep) {
				if (((RobotStep) step).getRobotSettings().getGripperHead().equals(gripperHead)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean willNeedWorkArea(final int stepIndexFirst, final WorkArea workArea) {
		for (int i = stepIndexFirst; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			if (step instanceof DeviceStep) {
				if (((DeviceStep) step).getDeviceSettings().getWorkArea().equals(workArea)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "AutomateOptimizedThread: " + processFlow.toString();
	}
	
	public void stopRunning() {
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(final boolean running) {
		this.running = running;
	}
	
	@Override
	public void interrupt() {
		if (running) {
			running = false;
			for (AbstractRobot robot : processFlow.getRobots()) {
				robot.interruptCurrentAction();
			}
			for (AbstractDevice device :processFlow.getDevices()) {
				device.interruptCurrentAction();
			}
		}
		synchronized (syncObject) {
			syncObject.notifyAll();
		}
		reset();
	}

	@Override
	public void notifyException(final Exception e) {
		getProcessFlow().processProcessFlowEvent(new ExceptionOccuredEvent(getProcessFlow(), e));
		processFlow.initialize();
		getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
		getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
		interrupt();
		e.printStackTrace();
		logger.error(e);
		processFlow.setMode(Mode.STOPPED);
	}
	
	@Override
	public void notifyInterruptedException(final InterruptedException e) {
		if ((!isRunning()) || ThreadManager.isShuttingDown()) {
			running = false;
			logger.info("Execution of one or more steps got interrupted, so let't just stop");
		} else {
			getProcessFlow().processProcessFlowEvent(new ExceptionOccuredEvent(getProcessFlow(), new Exception(Translator.getTranslation(OTHER_EXCEPTION))));
			e.printStackTrace();
			logger.error(e);
		}
		getProcessFlow().initialize();
		getProcessFlow().setMode(Mode.STOPPED);
		getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
		getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
	}

	public ProcessFlow getProcessFlow() {
		return processFlow;
	}

	@Override
	public synchronized void stepExecutionFinished(final int stepProcessId) {
		isRunning.put(stepProcessId, false);
		int currentStepIndex = getProcessFlow().getCurrentIndex(stepProcessId);
		currentStepIndex++;
		getProcessFlow().setCurrentIndex(stepProcessId, currentStepIndex);
		if (currentStepIndex == processFlow.getProcessSteps().size()) {
			if (stepProcessId == mainProcessIndex) {
				// main flow has finished, switch
				processFlow.incrementFinishedAmount();
				getProcessFlow().setCurrentIndex(stepProcessId, 0);
				this.mainProcessIndex = secondProcessIndex;
				this.secondProcessIndex = stepProcessId;
				if (processFlow.getFinishedAmount() == processFlow.getTotalAmount()) {
					running = false;
					finished = true;
				}
			} else {
				throw new IllegalStateException("Secondary process finished before primary?");
			}
		}
		synchronized (syncObject) {
			syncObject.notifyAll();
		}
	}
	
	public int getMainProcessFlowId() {
		return mainProcessIndex;
	}

}
