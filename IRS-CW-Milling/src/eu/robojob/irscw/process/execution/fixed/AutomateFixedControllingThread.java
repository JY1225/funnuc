package eu.robojob.irscw.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.threading.ThreadManager;

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
	
	private void checkIfConcurrentExecutionIsPossible() {
		// this is possible if the CNC machine is used only once, and the gripper used to put the piece in the 
		// CNC machine is not the same as the gripper used to pick the piece from the CNC machine
		PickStep pickFromMachine = null;
		PutStep putToMachine = null;
		for (AbstractProcessStep step : processFlow.getProcessSteps()) {
			if ((step instanceof PickStep) && ((PickStep) step).getDevice() instanceof AbstractCNCMachine) {
				pickFromMachine = (PickStep) step;
			}
			if ((step instanceof PutStep) && ((PutStep) step).getDevice() instanceof AbstractCNCMachine) {
				putToMachine = (PutStep) step;
			}
		}
		if (pickFromMachine.getRobotSettings().getGripperHead().equals(putToMachine.getRobotSettings().getGripperHead()) && 
				processFlow.getFinishedAmount() < processFlow.getTotalAmount() - 1) {
			isConcurrentExecutionPossible = false; 
		} else {
			isConcurrentExecutionPossible = true;
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
		this.finishedSyncObject = new Object();
		this.mainProcessFlowId = WORKPIECE_0_ID;
	}
	
	@Override
	public void run() {
		try {
			running = true;
			checkIfConcurrentExecutionIsPossible();
			if (processFlow.getCurrentIndex(WORKPIECE_0_ID) == -1) {
				processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.PREPARE, WORKPIECE_0_ID));
				for (AbstractRobot robot :processFlow.getRobots()) {	// first recalculate TCPs
					robot.recalculateTCPs();
				}
				for (AbstractDevice device: processFlow.getDevices()) {	// prepare devices for this processflow
					device.prepareForProcess(processFlow);
				}
				processFlow.setCurrentIndex(WORKPIECE_0_ID, 0);
				processFlow.setCurrentIndex(WORKPIECE_1_ID, 0);
			}
			if (processFlow.getCurrentIndex(WORKPIECE_1_ID) == -1) {
				processFlow.setCurrentIndex(WORKPIECE_1_ID, 0);
			}
			processFlowExecutor1 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_0_ID);
			ThreadManager.submit(processFlowExecutor1);
			synchronized(finishedSyncObject) {
				finishedSyncObject.wait();
			}
			processFlow.setMode(Mode.FINISHED);
			processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
			processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
			for (AbstractDevice device : processFlow.getDevices()) {
				if (device instanceof CNCMillingMachine) {
					((CNCMillingMachine) device).indicateAllProcessed();
				}
			}
			for (AbstractRobot robot : processFlow.getRobots()) {
				robot.moveToHome();
			}
			logger.info(toString() + " ended...");
		} catch(InterruptedException e) {
			if (running) {
				stopRunning();
			} else {
				stopRunning();
				notifyException(e);
			}
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
		if (running) {
			running = false;
			for (AbstractRobot robot : processFlow.getRobots()) {
				robot.interruptCurrentAction();
			}
			for (AbstractDevice device :processFlow.getDevices()) {
				device.interruptCurrentAction();
			}
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
			} else {
				this.lastPiece = true;
				logger.info("LAST PIECE!");
			}
		} 
		if (processFlow.getFinishedAmount() == processFlow.getTotalAmount()) {
			processFlowExecutor.stopRunning();
			synchronized(finishedSyncObject) {
				finishedSyncObject.notify();
			}
		}
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			mainProcessFlowId = WORKPIECE_1_ID;
		} else {
			mainProcessFlowId = WORKPIECE_0_ID;
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
		stopExecution();
	}
	
	public void stopExecution() {
		processFlow.setMode(Mode.STOPPED);
		processFlow.initialize();
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
