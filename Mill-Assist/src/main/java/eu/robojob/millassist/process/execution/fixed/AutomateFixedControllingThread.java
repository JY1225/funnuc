package eu.robojob.millassist.process.execution.fixed;

import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.ProcessFlow.Type;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.threading.ThreadManager;

public class AutomateFixedControllingThread extends Thread {
	
	protected ProcessFlow processFlow;
	protected ProcessFlowExecutionThread processFlowExecutor1;
	protected Future<?> processFlowExecutor1Future;
	protected ProcessFlowExecutionThread processFlowExecutor2;
	protected Future<?> processFlowExecutor2Future;
	
	protected enum ExecutionThreadStatus {
		IDLE,
		WAITING_BEFORE_PICK_FROM_STACKER,
		WAITING_FOR_WORKPIECES_STACKER,
		WAITING_FOR_PICK_FROM_STACKER,
		WORKING_WITH_ROBOT,
		WORKING_WITHOUT_ROBOT,
		WAITING_BEFORE_PUT_IN_MACHINE,
		WAITING_BEFORE_PICK_FROM_MACHINE,
		WAITING_AFTER_PICK_FROM_MACHINE,
		WAITING_AFTER_PUT_IN_STACKER,
		FINISHED
	}
	
	protected ExecutionThreadStatus statusExecutor1;
	protected ExecutionThreadStatus statusExecutor2;
	
	protected static final int WORKPIECE_0_ID = 0;
	protected static final int WORKPIECE_1_ID = 1;
	private static Logger logger = LogManager.getLogger(AutomateFixedControllingThread.class.getName());
	
	protected Object finishedSyncObject;
	protected boolean running = false;
	protected boolean finished;
	protected boolean firstPiece;
	protected boolean isConcurrentExecutionPossible;
	protected int mainProcessFlowId;
	
	public AutomateFixedControllingThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.processFlowExecutor1 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_0_ID);
		this.processFlowExecutor2 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_1_ID);
		this.statusExecutor1 = ExecutionThreadStatus.IDLE;
		this.statusExecutor2 = ExecutionThreadStatus.IDLE;
		reset();
	}
	
	protected void checkStatus() throws InterruptedException {
		if (!running) {
			throw new InterruptedException("Got interrupted during execution of processflow");
		}
	}
	
	protected void checkIfConcurrentExecutionIsPossible() {
		isConcurrentExecutionPossible = processFlow.isConcurrentExecutionPossible();
	}
	
	public boolean isConcurrentExecutionPossible() {
		return isConcurrentExecutionPossible;
	}

	public void reset() {
		processFlow.setCurrentIndex(WORKPIECE_0_ID, -1);
		processFlow.setCurrentIndex(WORKPIECE_1_ID, -1);
		this.firstPiece = true;
		this.finished = false;
		this.finishedSyncObject = new Object();
		this.mainProcessFlowId = WORKPIECE_0_ID;
	}
	
	@Override
	public void run() {
		try {
			processFlow.setMode(ProcessFlow.Mode.AUTO);
			running = true;
			checkIfConcurrentExecutionIsPossible();
			if ((processFlow.getCurrentIndex(WORKPIECE_0_ID) == -1) || (processFlow.getCurrentIndex(WORKPIECE_0_ID) == 0)) {
				processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.PREPARE, WORKPIECE_0_ID));
				for (AbstractRobot robot :processFlow.getRobots()) {	// first recalculate TCPs
					checkStatus();
					robot.recalculateTCPs();
					robot.setCurrentActionSettings(null);
				}
				for (AbstractDevice device: processFlow.getDevices()) {	// prepare devices for this processflow
					checkStatus();
					device.prepareForProcess(processFlow);
				}
				processFlow.setCurrentIndex(WORKPIECE_0_ID, 0);
				processFlow.setCurrentIndex(WORKPIECE_1_ID, 0);
			}
			if (processFlow.getCurrentIndex(WORKPIECE_1_ID) == -1) {
				processFlow.setCurrentIndex(WORKPIECE_1_ID, 0);
			}
			checkStatus();
			processFlowExecutor1 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_0_ID);
			boolean startSecond = false;
			if (processFlow.getCurrentIndex(WORKPIECE_0_ID) > 0) {
				// process has already passed some steps, check if current step is processing in machine
				// than second process can start!
				AbstractProcessStep step = processFlow.getStep(processFlow.getCurrentIndex(WORKPIECE_0_ID) - 1);
				if (step instanceof PutStep) {
					if (((PutStep) step).getDevice() instanceof AbstractCNCMachine) {
						statusExecutor1 = ExecutionThreadStatus.WORKING_WITHOUT_ROBOT;
						if (processFlow.getCurrentIndex(WORKPIECE_1_ID) == 0) {
							if (isConcurrentExecutionPossible()) {
								startSecond = true;
							}
						}
					}
				} 
			}
			processFlowExecutor1Future = ThreadManager.submit(processFlowExecutor1);
			if (startSecond) {
				statusExecutor2 = ExecutionThreadStatus.IDLE;
				processFlowExecutor2 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_1_ID);
				firstPiece = false;
				processFlowExecutor2Future = ThreadManager.submit(processFlowExecutor2);
			}
			// wait until all processes have finished
			synchronized(finishedSyncObject) {
				finishedSyncObject.wait();
			}
			checkStatus();
			if (finished) {
				processFlow.setMode(Mode.FINISHED);
				for (AbstractDevice device : processFlow.getDevices()) {
					if (device instanceof CNCMillingMachine) {
						checkStatus();
						((AbstractCNCMachine) device).indicateAllProcessed();
					}
				}
				for (AbstractRobot robot : processFlow.getRobots()) {
					checkStatus();
					robot.moveToHome();
				}
			}
			processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
			processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
			logger.info(toString() + " ended...");
		} catch(InterruptedException e) {
			if (running) {
				stopRunning();
				notifyException(e);
			}
		} catch (AbstractCommunicationException e) {
			stopRunning();
			notifyException(e);
		} catch (RobotActionException e) {
			stopRunning();
			notifyException(e);
		} catch (Exception e) {
			notifyException(e);
		}
	}
	
	@Override
	public void interrupt() {
		logger.info("Called interrupt on AutomateFixedControllingThread");
		running = false;
		super.interrupt();
		for (AbstractRobot robot : processFlow.getRobots()) {
			robot.interruptCurrentAction();
		}
		for (AbstractDevice device :processFlow.getDevices()) {
			device.interruptCurrentAction();
		}
		stopRunning();
		reset();
	}
	
	public boolean isFirstPiece() {
		return firstPiece;
	}
	
	public synchronized void notifyWaitingOnIntervention() {
		if ((processFlowExecutor1 != null) && (processFlowExecutor1.isRunning())) {
			processFlowExecutor1.waitForIntervention();
		}
		if ((processFlowExecutor2 != null) && (processFlowExecutor2.isRunning())) {
			processFlowExecutor2.waitForIntervention();
		}
	}
	
	public void interventionFinished() {
		if ((processFlowExecutor1 != null) && (processFlowExecutor1.isRunning())) {
			processFlowExecutor1.interventionFinished();
		}
		if ((processFlowExecutor2 != null) && (processFlowExecutor2.isRunning())) {
			processFlowExecutor2.interventionFinished();
		}
		processFlow.setMode(Mode.AUTO);
	}
	
	public synchronized void notifyWaitingBeforePickFromStacker(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			statusExecutor1 = ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER;
			if ((statusExecutor2 != ExecutionThreadStatus.WORKING_WITH_ROBOT) && (statusExecutor2 != ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER)) {
				statusExecutor1 = ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER;
				processFlowExecutor1.continueExecution();
			}
		} else {
			statusExecutor2 = ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER;
			if ((statusExecutor1 != ExecutionThreadStatus.WORKING_WITH_ROBOT) && (statusExecutor1 != ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER)) {
				statusExecutor2 = ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER;
				processFlowExecutor2.continueExecution();
			}
		}
	}
	
	public synchronized void notifyWorkPiecesPresent(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			statusExecutor1 = ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER;
			if (statusExecutor2 != ExecutionThreadStatus.WORKING_WITH_ROBOT) {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			}
		} else {
			statusExecutor2 = ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER;
			if (statusExecutor1 != ExecutionThreadStatus.WORKING_WITH_ROBOT) {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			}
		}
	}
	
	public synchronized void notifyWaitingBeforePutInMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			statusExecutor1 = ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE;
			if ((statusExecutor2 != ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) && (statusExecutor2 != ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) &&
					(statusExecutor2 != ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor2 == ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} else {
			statusExecutor2 = ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE;
			if ((statusExecutor1 != ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) && (statusExecutor1 != ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) &&
					(statusExecutor1 != ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor1 == ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		}
	}
	
	public synchronized void notifyPutInMachineFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		logger.info("Put in machine finished.");
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			statusExecutor1 = ExecutionThreadStatus.WORKING_WITHOUT_ROBOT;
			// no continue needed
			if ((!processFlowExecutor2.isRunning()) && isConcurrentExecutionPossible) {
				logger.info("Second process not yet started");
				statusExecutor2 = ExecutionThreadStatus.IDLE;
				processFlowExecutor2Future = ThreadManager.submit(processFlowExecutor2);
			} else if ((!processFlowExecutor2.isRunning()) && !isConcurrentExecutionPossible) {
				logger.info("No second process, move to home");
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			} else if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				statusExecutor2 = ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER;
				processFlowExecutor2.continueExecution();
			} else if ((statusExecutor2 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) ||
					(statusExecutor2 == ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			} 
		} else {
			statusExecutor2 = ExecutionThreadStatus.WORKING_WITHOUT_ROBOT;
			// no continue needed
			if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				statusExecutor1 = ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER;
				processFlowExecutor1.continueExecution();
			} else if ((statusExecutor1 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) ||
					(statusExecutor1 == ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			}
		}
	}
	
	public synchronized void notifyNoWorkPiecesPresent(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor2 != ExecutionThreadStatus.WORKING_WITH_ROBOT) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} else {
			if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor1 != ExecutionThreadStatus.WORKING_WITH_ROBOT) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		}
	}
	
	public synchronized void notifyWaitingBeforePickFromMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			statusExecutor1 = ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE;
			if (statusExecutor2 != ExecutionThreadStatus.WORKING_WITH_ROBOT) {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			} 
		} else {
			statusExecutor2 = ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE;
			if (statusExecutor1 != ExecutionThreadStatus.WORKING_WITH_ROBOT) {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			} 
		}
	}
	
	public synchronized void notifyWaitingAfterPickFromMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			statusExecutor1 = ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE;
			if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			} else {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			}
		} else {
			statusExecutor2 = ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE;
			if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			} else {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			}
		}
	}
	
	//TODO: also review for flows with 2 - 1 pieces?
	public synchronized void notifyProcessFlowFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		// update main process flow
		if (isConcurrentExecutionPossible) {
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				mainProcessFlowId = WORKPIECE_1_ID;
			} else {
				mainProcessFlowId = WORKPIECE_0_ID;
			}
		}
		// check if this was the last run for this executor
		if ((processFlow.getType() == Type.FIXED_AMOUNT) && 
				((processFlow.getFinishedAmount() == processFlow.getTotalAmount()) || 
						((processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 1) 
								&& isConcurrentExecutionPossible))) {
			// this was the last work piece for this executor, so let's stop it
			if (processFlowExecutor == processFlowExecutor1) {
				statusExecutor1 = ExecutionThreadStatus.FINISHED;
				if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
					processFlowExecutor2.continueExecution();
				}
			} else {
				statusExecutor2 = ExecutionThreadStatus.FINISHED;
				if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
					processFlowExecutor1.continueExecution();
				}
			}
			processFlowExecutor.stopRunning();
			if (processFlow.getFinishedAmount() == processFlow.getTotalAmount()) {
				// finished all pieces so also stop this thread
				finished = true;
				synchronized(finishedSyncObject) {
					finishedSyncObject.notify();
				}
			}
		} else if (processFlowExecutor == processFlowExecutor1) {
			// continue
			statusExecutor1 = ExecutionThreadStatus.IDLE;
			processFlowExecutor1.continueExecution();
			if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				statusExecutor2 = ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER;
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor2 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				statusExecutor2 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor2.continueExecution();
			} 
		} else {
			// continue
			statusExecutor2 = ExecutionThreadStatus.IDLE;
			processFlowExecutor2.continueExecution();
			if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				statusExecutor1 = ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER;
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor1 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				statusExecutor1 = ExecutionThreadStatus.WORKING_WITH_ROBOT;
				processFlowExecutor1.continueExecution();
			} 
		}
	}
	
	public int getMainProcessFlowId() {
		return mainProcessFlowId;
	}
	
	public void notifyException(final Exception e) {
		processFlow.processProcessFlowEvent(new ExceptionOccuredEvent(processFlow, e));
		processFlow.initialize();
		interrupt();
		e.printStackTrace();
		logger.error(e);
	}
	
	public void stopRunning() {
		logger.info("Called stop running");
		running = false;
		synchronized(finishedSyncObject) {
			finishedSyncObject.notifyAll();
		}
		if (processFlowExecutor1Future != null) {
			processFlowExecutor1Future.cancel(true);
		}
		if (processFlowExecutor2Future != null) {
			processFlowExecutor2Future.cancel(true);
		}
		this.statusExecutor1 = ExecutionThreadStatus.IDLE;
		this.statusExecutor2 = ExecutionThreadStatus.IDLE;
		// just to be sure: 
		for (AbstractRobot robot : processFlow.getRobots()) {
			robot.setCurrentActionSettings(null);
		}
		stopExecution();
	}
	
	public void stopExecution() {
		processFlow.initialize();
		processFlow.setMode(Mode.STOPPED);
		processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
		processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
	}
	
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public String toString() {
		return "AutomateFixedControllingThread - processflow [" + processFlow + "]";
	}
}
