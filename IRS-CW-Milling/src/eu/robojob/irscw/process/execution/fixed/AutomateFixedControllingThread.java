package eu.robojob.irscw.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
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
	
	private int mainProcessFlowId;
	
	public AutomateFixedControllingThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.processFlowExecutor1 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_0_ID);
		this.processFlowExecutor2 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_1_ID);
		reset();
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
			processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.PREPARE, WORKPIECE_0_ID));
			for (AbstractRobot robot :processFlow.getRobots()) {	// first recalculate TCPs
				robot.recalculateTCPs();
			}
			for (AbstractDevice device: processFlow.getDevices()) {	// prepare devices for this processflow
				device.prepareForProcess(processFlow);
			}
			processFlow.setCurrentIndex(WORKPIECE_0_ID, 0);
			processFlow.setCurrentIndex(WORKPIECE_1_ID, 0);
			ThreadManager.submit(processFlowExecutor1);
			synchronized(finishedSyncObject) {
				finishedSyncObject.wait();
			}
			logger.info(toString() + " ended...");
		} catch(InterruptedException e) {
			if (running) {
				stopExecution();
			} else {
				stopExecution();
				notifyException(e);
			}
		} catch (AbstractCommunicationException e) {
			stopExecution();
			notifyException(e);
		}
	}
	
	public synchronized void notifyWaitingForPutInMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		if (firstPiece) {
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
		if (lastPiece) {
			logger.info("First piece so can continue");
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
		logger.info("Continue both flows");
		processFlowExecutor1.continueExecution();
		if (firstPiece) {
			processFlowExecutor2 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_1_ID);
			firstPiece = false;
			ThreadManager.submit(processFlowExecutor2);
		} else {
			processFlowExecutor2.continueExecution();
		}
	}
	
	public synchronized void notifyPickFromMachineFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		threadWaitingForPutInMachine.continueExecution();
		threadWaitingForPutInMachine = null;
	}
	
	public int getMainProcessFlowId() {
		return mainProcessFlowId;
	}
	
	//TODO: also review for flows with 2 - 1 pieces?
	public synchronized void notifyProcessFlowFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 2) {
			this.lastPiece = true;
		}
		if (processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 1) {
			processFlowExecutor.stopRunning();
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
		if (processFlowExecutor1 != null) {
			processFlowExecutor1.stopRunning();
		}
		if (processFlowExecutor2 != null) {
			processFlowExecutor2.stopRunning();
		}
		stopExecution();
	}
	
	public void stopExecution() {
		processFlow.setMode(Mode.STOPPED);
		processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_0_ID));
		processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_1_ID));
	}
	
	public boolean isRunning() {
		return running;
	}
}
