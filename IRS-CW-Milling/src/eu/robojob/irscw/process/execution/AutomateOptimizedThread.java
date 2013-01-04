package eu.robojob.irscw.process.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.stacking.AbstractStackingDevice;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.DeviceStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.RobotStep;
import eu.robojob.irscw.threading.ThreadManager;

/**
 * This optimized Automate-thread will be able to (sometimes) execute 2 steps in parallel
 */
public class AutomateOptimizedThread extends Thread implements ProcessExecutor {

	private static Logger logger = LogManager.getLogger(AutomateThread.class.getName());
	private static final int WORKPIECE_0_ID = 1;
	private static final int WORKPIECE_1_ID = 0;
	
	private ProcessFlow processFlow;
	private boolean running;
	
	private Map<Integer, Integer> currentIndices;
	private Map<Integer, Boolean> isRunning;
	private int mainProcessIndex;
	private int secondProcessIndex;

	private Object syncObject;
	
	public AutomateOptimizedThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.syncObject = new Object();
		reset();
	}
	
	public void reset() {
		this.currentIndices = new HashMap<Integer, Integer>();
		this.mainProcessIndex = WORKPIECE_0_ID;
		this.secondProcessIndex = WORKPIECE_1_ID;
		this.isRunning = new HashMap<Integer, Boolean>();
		isRunning.put(WORKPIECE_0_ID, false);
		isRunning.put(WORKPIECE_1_ID, false);	
		currentIndices.put(WORKPIECE_0_ID, 0);
		currentIndices.put(WORKPIECE_1_ID, 0);
	}
	
	//TODO add intervention steps execution
	@Override
	public void run() {
		logger.debug("Started execution, processflow [" + processFlow + "].");
		try {
			processFlow.setMode(ProcessFlow.Mode.AUTO);
			this.running = true;
			try {
				for (AbstractRobot robot :processFlow.getRobots()) {	// first recalculate TCPs
					robot.recalculateTCPs();
				}
				for (AbstractDevice device: processFlow.getDevices()) {	// prepare devices for this processflow
					device.prepareForProcess(processFlow);
				}
				while (running) {
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
			} catch (AbstractCommunicationException e) {
				e.printStackTrace();
				logger.error(e);
				processFlow.setMode(Mode.STOPPED);
			} catch (InterruptedException e) {
				if (!running) {
					logger.info("Execution of one or more steps got interrupted, so let't just stop.");
				} else {
					e.printStackTrace();
					logger.error(e);
				}
				processFlow.setMode(Mode.STOPPED);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
				processFlow.setMode(Mode.STOPPED);
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
	
	private synchronized void executeProcess(final int mainProcessId, final int secondProcessId) throws InterruptedException, ExecutionException {
		int currentStepIndexMain = currentIndices.get(mainProcessId);
		int currentStepIndexSecondary = currentIndices.get(secondProcessId);
		
		// if the first process is processing (and not as part of put and wait) the second process can use the robot
		// if the second process is processing (and not as part of pick and wait) the first process can use the robot
		// the first process has priority so it can finish first
		// sometimes, a special optimization is possible: when the first process does a pick and the second process's next step is 
		// a put in the same WorkArea, this should be allowed, before the first process does it's put
		
		if (!isRunning.get(mainProcessId)) {
			AbstractProcessStep step = processFlow.getStep(currentStepIndexMain);
			if ((step instanceof PutStep) && (currentStepIndexMain > 0)) {
				PickStep previousStep = (PickStep) processFlow.getStep(currentStepIndexMain - 1);	// before put step is always a pick step
				AbstractProcessStep stepSecondProcess = processFlow.getStep(currentStepIndexSecondary);
				if (stepSecondProcess instanceof PutStep) {
					PutStep secondProcessPutStep = (PutStep) stepSecondProcess;
					if (secondProcessPutStep.getRobotSettings().getWorkArea().equals(previousStep.getDeviceSettings().getWorkArea())) {
						AbstractProcessStep stepSecond = processFlow.getStep(currentStepIndexSecondary);
						ProcessStepExecutionThread exThread2 = new ProcessStepExecutionThread(stepSecond, secondProcessId, this);
						isRunning.put(secondProcessId, true);
						ThreadManager.submit(exThread2);
						return;
					}
				}
			}
			ProcessStepExecutionThread exThread = new ProcessStepExecutionThread(step, mainProcessId, this);
			isRunning.put(mainProcessId, true);
			ThreadManager.submit(exThread);
		}
		if (!isRunning.get(secondProcessId)) {
			if (isConcurrentExecutionPossible(currentStepIndexMain, currentStepIndexSecondary) && (processFlow.getFinishedAmount() < processFlow.getTotalAmount() - 1)) {
				AbstractProcessStep stepSecond = processFlow.getStep(currentStepIndexSecondary);
				ProcessStepExecutionThread exThread2 = new ProcessStepExecutionThread(stepSecond, secondProcessId, this);
				isRunning.put(secondProcessId, true);
				ThreadManager.submit(exThread2);
			}
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
	}

	@Override
	public void notifyException(final Exception e) {
		e.printStackTrace();
		logger.error(e);
		processFlow.setMode(Mode.STOPPED);
	}

	@Override
	public void notifyThrowable(final Throwable t) {
		t.printStackTrace();
		logger.error(t);
		processFlow.setMode(Mode.STOPPED);
	}

	@Override
	public synchronized void stepExecutionFinished(final int stepProcessId) {
		isRunning.put(stepProcessId, false);
		int currentStepIndex = currentIndices.get(stepProcessId);
		currentStepIndex++;
		currentIndices.put(stepProcessId, currentStepIndex);
		if (currentStepIndex == processFlow.getProcessSteps().size()) {
			if (stepProcessId == mainProcessIndex) {
				// main flow has finished, switch
				processFlow.incrementFinishedAmount();
				currentIndices.put(stepProcessId, 0);
				this.mainProcessIndex = secondProcessIndex;
				this.secondProcessIndex = stepProcessId;
				if (processFlow.getFinishedAmount() == processFlow.getTotalAmount()) {
					running = false;
					processFlow.setMode(Mode.FINISHED);
				}
			} else {
				throw new IllegalStateException("Secondary process finished before primary?.");
			}
		}
		synchronized (syncObject) {
			syncObject.notifyAll();
		}
	}
}
