package eu.robojob.millassist.process.execution.fixed;

import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.fixed.ProcessFlowExecutionThread.ExecutionThreadStatus;

public abstract class AbstractFixedControllingThread implements Runnable {

	protected static final int PROCESS_0_ID = 0;
	protected static final int PROCESS_1_ID = 1;
	
	protected ProcessFlow processFlow;
	protected ProcessFlowExecutionThread[] processFlowExecutors;
	protected Future<?>[] processFlowExecutorFutures;
	protected int mainProcessFlowId;
	
	protected boolean running = false;
	protected boolean finished;
	protected boolean firstPiece;
	protected boolean isConcurrentExecutionPossible;
	protected int nbProcesses;
	
	protected Object finishedSyncObject;
	
	protected static Logger logger = LogManager.getLogger(AbstractFixedControllingThread.class.getName());
	
	protected AbstractFixedControllingThread(final ProcessFlow processFlow, final int nbProcesses) {
		this.processFlow = processFlow;
		this.nbProcesses = nbProcesses;
		reset();
	}
	
	protected void initExecutors() {
		processFlowExecutors = new ProcessFlowExecutionThread[nbProcesses];
		processFlowExecutorFutures = new Future<?>[nbProcesses];
		for(int i = 0; i < nbProcesses; i++) {
			processFlowExecutors[i] = new ProcessFlowExecutionThread(this, processFlow, i);
		}
	}
	
	protected void initRun() {
		mainProcessFlowId = PROCESS_0_ID;
		processFlow.setMode(ProcessFlow.Mode.AUTO);
		running = true;
		checkIfConcurrentExecutionIsPossible();
		if (isConcurrentExecutionPossible) {
			nbProcesses++;
		}
		initExecutors();
	}
	
	protected void checkStatus() throws InterruptedException {
		if (!running) {
			throw new InterruptedException("Got interrupted during execution of processflow");
		}
	}
	
	void interrupted() {
		if (running) {
			stopRunning();
		}
		reset();
	}
	
	boolean isFirstPiece() {
		return firstPiece;
	}
	
	protected final void checkIfConcurrentExecutionIsPossible() {
		isConcurrentExecutionPossible = processFlow.isConcurrentExecutionPossible();
	}
	
	public boolean isConcurrentExecutionPossible() {
		return isConcurrentExecutionPossible;
	}
	
	public final int getNbConcurrentInMachine() {
		if (isConcurrentExecutionPossible) {
			return (nbProcesses - 1);
		} 
		return nbProcesses;
	}
	
	protected final int getNbInFlow() {
		int result = 0;
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			result += processExecutor.getNbInFlow();
		}
		return result;
	}
	
	protected final boolean stillPieceToDo() {
		return (processFlow.getTotalAmount() - processFlow.getFinishedAmount() - getNbInFlow() > 0);
	}
	
	protected abstract boolean isFreePlaceInMachine();
	
	protected boolean isRobotFree() {
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				return false;
			}
		}
		return true;
	}
	
	public void reset() {
		for (int processId = 0; processId < nbProcesses; processId++) {
			processFlow.setCurrentIndex(processId, -1);

		}
		this.firstPiece = true;
		this.finished = false;
		this.finishedSyncObject = new Object();	
	}

	synchronized void notifyWaitingOnIntervention() {
		for (ProcessFlowExecutionThread processFlowExecutor: processFlowExecutors) {
			if ((processFlowExecutor != null) && (processFlowExecutor.isRunning())) {
				processFlowExecutor.waitForIntervention();
			}
		}
	}
	
	public synchronized void interventionFinished() {
		for (ProcessFlowExecutionThread processFlowExecutor: processFlowExecutors) {
			if ((processFlowExecutor != null) && (processFlowExecutor.isRunning())) {
				processFlowExecutor.interventionFinished();
			}
		}
		processFlow.setMode(Mode.AUTO);
	}
	
	abstract void notifyWaitingBeforePickFromStacker(final ProcessFlowExecutionThread processFlowExecutor);
	
	abstract void notifyWorkPiecesPresent(final ProcessFlowExecutionThread processFlowExecutor);
	
	/**
	 * The robot has grabbed a workpiece and wants to have access to the machine
	 */
	abstract void notifyWaitingBeforePutInMachine(final ProcessFlowExecutionThread processFlowExecutor);
	
	abstract void notifyPutInMachineFinished(final ProcessFlowExecutionThread processFlowExecutor);
	
	abstract void notifyNoWorkPiecesPresent(final ProcessFlowExecutionThread processFlowExecutor);
	
	abstract void notifyWaitingBeforePickFromMachine(final ProcessFlowExecutionThread processFlowExecutor);
	
	abstract void notifyWaitingAfterPickFromMachine(final ProcessFlowExecutionThread processFlowExecutor);
	
	abstract void notifyProcessFlowFinished(final ProcessFlowExecutionThread processFlowExecutor);
	
	abstract protected ExecutionThreadStatus getFirstPutState();
	
	void notifyException(final Exception e) {
		processFlow.processProcessFlowEvent(new ExceptionOccuredEvent(processFlow, e));
		processFlow.initialize();
		interrupted();
		e.printStackTrace();
		logger.error(e);
	}
	
	public final void stopRunning() {
		logger.info("Called stop running");
		running = false;	
		for (AbstractRobot robot : processFlow.getRobots()) {
			robot.interruptCurrentAction();
		}
		for (AbstractDevice device :processFlow.getDevices()) {
			device.interruptCurrentAction();
		}
		synchronized(finishedSyncObject) {
			finishedSyncObject.notifyAll();
		}
		for (Future<?> processFlowExecutorFuture: processFlowExecutorFutures) {
			if (processFlowExecutorFuture != null) {
				processFlowExecutorFuture.cancel(true);
			}
		}
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			processExecutor.setExecutionStatus(ExecutionThreadStatus.IDLE);
		}
		for (AbstractRobot robot: processFlow.getRobots()) {
			robot.setCurrentActionSettings(null);
		}
		stopExecution();
		reset();
	}
	
	protected final void stopExecution() {
		processFlow.initialize();
		processFlow.setMode(Mode.STOPPED);
		for(int i = 0; i <= nbProcesses; i++) {
			processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, i));
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public int getMainProcessFlowId() {
		return mainProcessFlowId;
	}
	
	@Override
	public abstract String toString();
}
