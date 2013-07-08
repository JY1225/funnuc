package eu.robojob.millassist.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.external.device.stacking.BasicStackPlate;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.ProcessingStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.threading.ThreadManager;

public class AutomateFixedControllingThread extends Thread {

	private ProcessFlowExecutionThread threadWaitingForPutInMachine;
	private ProcessFlowExecutionThread threadWaitingForPickFromMachine;
	
	private ProcessFlow processFlow;
	private ProcessFlowExecutionThread processFlowExecutor1;
	private ProcessFlowExecutionThread processFlowExecutor2;
	
	private static final int WORKPIECE_0_ID = 0;
	private static final int WORKPIECE_1_ID = 1;
	private static Logger logger = LogManager.getLogger(AutomateFixedControllingThread.class.getName());
	
	private Object finishedSyncObject;
	private boolean running = false;
	
	private boolean finished;
	
	private boolean firstPiece;
	private boolean lastPiece;
	private boolean isConcurrentExecutionPossible;
	private int mainProcessFlowId;
	
	public AutomateFixedControllingThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.processFlowExecutor1 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_0_ID);
		this.processFlowExecutor2 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_1_ID);
		reset();
	}
	
	private void checkStatus() throws InterruptedException {
		if (!running) {
			throw new InterruptedException("Got interrupted during execution of processflow");
		}
	}
	
	private void checkIfConcurrentExecutionIsPossible() {
		// this is possible if the CNC machine is used only once, and the gripper used to put the piece in the 
		// CNC machine is not the same as the gripper used to pick the piece from the CNC machine
		PickStep pickFromStacker = null;
		PickStep pickFromMachine = null;
		PutStep putToMachine = null;
		for (AbstractProcessStep step : processFlow.getProcessSteps()) {
			if ((step instanceof PickStep) && ((PickStep) step).getDevice() instanceof BasicStackPlate) {
				pickFromStacker = (PickStep) step;
			}
			if ((step instanceof PickStep) && ((PickStep) step).getDevice() instanceof AbstractCNCMachine) {
				pickFromMachine = (PickStep) step;
			}
			if ((step instanceof PutStep) && ((PutStep) step).getDevice() instanceof AbstractCNCMachine) {
				putToMachine = (PutStep) step;
			}
		}
		float totalWorkPieceWeight = pickFromStacker.getRobotSettings().getWorkPiece().getWeight() + pickFromMachine.getRobotSettings().getWorkPiece().getWeight();
		if (totalWorkPieceWeight < pickFromMachine.getRobot().getMaxWorkPieceWeight()) {
			if (pickFromMachine.getRobotSettings().getGripperHead().equals(putToMachine.getRobotSettings().getGripperHead()) && 
				processFlow.getFinishedAmount() < processFlow.getTotalAmount() - 1) {
				isConcurrentExecutionPossible = false; 
			} else {
				isConcurrentExecutionPossible = true;
			}
		} else {
			isConcurrentExecutionPossible = false;
		}
		logger.info("Concurrent execution possible: " + isConcurrentExecutionPossible);
	}
	
	public boolean isConcurrentExecutionPossible() {
		return isConcurrentExecutionPossible;
	}

	public void reset() {
		processFlow.setCurrentIndex(WORKPIECE_0_ID, -1);
		processFlow.setCurrentIndex(WORKPIECE_1_ID, -1);
		this.firstPiece = true;
		this.lastPiece = false;
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
			if (processFlow.getCurrentIndex(WORKPIECE_0_ID) == -1) {
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
			if (processFlow.getCurrentIndex(WORKPIECE_0_ID) > 0) {
				// process has already passed some steps, check if current step is processing in machine
				AbstractProcessStep step = processFlow.getStep(processFlow.getCurrentIndex(WORKPIECE_0_ID));
				if (step instanceof ProcessingStep) {
					if (((ProcessingStep) step).getDevice() instanceof AbstractCNCMachine) {
						if (processFlow.getCurrentIndex(WORKPIECE_1_ID) == 0) {
							if (isConcurrentExecutionPossible()) {
								processFlowExecutor2 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_1_ID);
								firstPiece = false;
								ThreadManager.submit(processFlowExecutor2);
							}
						}
					}
				}
			}
			ThreadManager.submit(processFlowExecutor1);
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
			} /*else {
				stopRunning();
			}*/
		} catch (AbstractCommunicationException e) {
			stopRunning();
			notifyException(e);
		} catch (RobotActionException e) {
			stopRunning();
			notifyException(e);
		}
	}
	
	@Override
	public void interrupt() {
		running = false;
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
	
	public synchronized void notifyWaitingForPutInMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		if (firstPiece || !isConcurrentExecutionPossible) {
			processFlowExecutor.continueExecution();
		} else	{
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				threadWaitingForPutInMachine = processFlowExecutor1;
			} else {
				threadWaitingForPutInMachine = processFlowExecutor2;
			}
			if (threadWaitingForPickFromMachine != null) {
				waitingForPutAndPick();
			}
		}
	}
	
	public synchronized void notifyWaitingForPickFromMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		if (lastPiece || !isConcurrentExecutionPossible) {
			processFlowExecutor.continueExecution();
		} else {
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				threadWaitingForPickFromMachine = processFlowExecutor1;
			} else {
				threadWaitingForPickFromMachine = processFlowExecutor2;
			}
			if (threadWaitingForPutInMachine != null) {
				waitingForPutAndPick();
			}
		}
	}
	
	private void waitingForPutAndPick() {
		logger.info("First take care of picking");
		threadWaitingForPickFromMachine.continueExecution();
		threadWaitingForPickFromMachine = null;
	}
	
	public synchronized void notifyPutInMachineFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		// continue both flows
		if (isConcurrentExecutionPossible) {
			processFlowExecutor1.continueExecution();
			if (firstPiece) {
				logger.info("Initiating second flow");
				processFlowExecutor2 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_1_ID);
				firstPiece = false;
				ThreadManager.submit(processFlowExecutor2);
			} else {
				processFlowExecutor2.continueExecution();
			}
		} else {
			processFlowExecutor.continueExecution();
		}
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
	
	public synchronized void notifyPickFromMachineFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		if (isConcurrentExecutionPossible && !lastPiece) {
			threadWaitingForPutInMachine.continueExecution();
			threadWaitingForPutInMachine = null;
		} else {
			processFlowExecutor.continueExecution();
		}
	}
	
	public int getMainProcessFlowId() {
		return mainProcessFlowId;
	}
	
	//TODO: also review for flows with 2 - 1 pieces?
	public synchronized void notifyProcessFlowFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 1) {
			if (isConcurrentExecutionPossible) {
				this.lastPiece = true;
				processFlowExecutor.stopRunning();
				// last piece just finished, if another thread is waiting for picking from the machine it can continue
				if (threadWaitingForPickFromMachine != null) {
					threadWaitingForPickFromMachine.continueExecution();
				}
			} else {
				this.lastPiece = true;
				logger.info("LAST PIECE!");
			}
		} 
		if (processFlow.getFinishedAmount() == processFlow.getTotalAmount()) {
			processFlowExecutor.stopRunning();
			finished = true;
			synchronized(finishedSyncObject) {
				finishedSyncObject.notify();
			}
		}
		if (isConcurrentExecutionPossible) {
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				mainProcessFlowId = WORKPIECE_1_ID;
			} else {
				mainProcessFlowId = WORKPIECE_0_ID;
			}
		}
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
		if (processFlowExecutor1 != null) {
			processFlowExecutor1.interrupt();
		}
		if (processFlowExecutor2 != null) {
			processFlowExecutor2.interrupt();
		}
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
