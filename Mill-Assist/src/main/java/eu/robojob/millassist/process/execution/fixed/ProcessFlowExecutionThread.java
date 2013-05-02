package eu.robojob.millassist.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.stacking.BasicStackPlate;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.PutAndWaitStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.RobotStep;
import eu.robojob.millassist.process.execution.ProcessExecutor;

public class ProcessFlowExecutionThread extends Thread implements ProcessExecutor {

	private ProcessFlow processFlow;
	private int workpieceId;
	private AutomateFixedControllingThread controllingThread;
	private Object syncObject;
	private boolean running;
	private boolean canContinue;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowExecutionThread.class.getName());
	
	public ProcessFlowExecutionThread(final AutomateFixedControllingThread controllingThread,
			final ProcessFlow processFlow, final int workpieceId) {
		this.controllingThread = controllingThread;
		this.processFlow = processFlow;
		this.workpieceId = workpieceId;
		this.syncObject = new Object();
		this.canContinue = false;
	}
	
	private void checkStatus() throws InterruptedException {
		if (!running) {
			throw new InterruptedException("Got interrupted during execution of processflow");
		}
	}
	
	@Override 
	public void run() {
		this.running = true;
		try {
			while (running) {
				
				AbstractProcessStep currentStep = processFlow.getStep(processFlow.getCurrentIndex(workpieceId));
				
				checkStatus();
				
				if (currentStep instanceof RobotStep) {
					((RobotStep) currentStep).getRobotSettings().setFreeAfter(false);
				}
				
				if ((currentStep instanceof PutStep) && (((PutStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					checkStatus();
					executePutInMachineStep((PutStep) currentStep);
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					checkStatus();
					executePickFromMachineStep((PickStep) currentStep);
				} else {
					if ((currentStep instanceof PickStep) && ((PickStep) currentStep).getDevice() instanceof BasicStackPlate) {
						((PickStep) currentStep).getRobotSettings().setFreeAfter(true);
					}
					if ((currentStep instanceof PutStep) && ((PutStep) currentStep).getDevice() instanceof BasicStackPlate) {
						if ((processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 1) || 
								((processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 2)) && controllingThread.isConcurrentExecutionPossible()) {
							((PutStep) currentStep).getRobotSettings().setFreeAfter(true);
						}
					}
					if ((currentStep instanceof PutAndWaitStep) && controllingThread.isConcurrentExecutionPossible() && 
							!controllingThread.isFirstPiece()) {
						((PutAndWaitStep) currentStep).getRobotSettings().setFreeAfter(true);
					}
					checkStatus();
					currentStep.executeStep(workpieceId, this);
					if (currentStep instanceof AbstractTransportStep) {
						checkStatus();
						((AbstractTransportStep) currentStep).finalizeStep(this);
					} 
				}
				
				if (processFlow.getCurrentIndex(workpieceId) == processFlow.getProcessSteps().size() - 1) {
					processFlow.setCurrentIndex(workpieceId, 0);
					processFlow.incrementFinishedAmount();
					controllingThread.notifyProcessFlowFinished(this);
				} else {
					processFlow.setCurrentIndex(workpieceId, processFlow.getCurrentIndex(workpieceId) + 1);
				}
			}
			logger.info(toString() + " ended...");
		} catch (InterruptedException e) {
			if (controllingThread.isRunning()) {
				controllingThread.notifyException(e);
				controllingThread.stopRunning();
			} /*else {
				controllingThread.stopRunning();
			}*/
		} catch (Exception e) {
			controllingThread.notifyException(e);
			//controllingThread.stopExecution();
			controllingThread.stopRunning();
		}
	}
	
	public void executePutInMachineStep(final PutStep putStep) throws InterruptedException, AbstractCommunicationException, RobotActionException, DeviceActionException {
		canContinue = false;
		checkStatus();
		controllingThread.notifyWaitingForPutInMachine(this);
		checkStatus();
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting before put in machine");
				syncObject.wait();
				logger.info("Can continue");
			}
		}
		if (!running) {
			return;
		}
		putStep.getRobotSettings().setFreeAfter(true);
		checkStatus();
		putStep.executeStep(workpieceId, this);
		checkStatus();
		putStep.finalizeStep(this);
		canContinue = false;
		checkStatus();
		controllingThread.notifyPutInMachineFinished(this);
		checkStatus();
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting after put in machine");
				syncObject.wait();
				logger.info("Can continue");
			}
		}
	}
	
	public void executePickFromMachineStep(final PickStep pickStep) throws AbstractCommunicationException, RobotActionException, InterruptedException, DeviceActionException {
		canContinue = false;
		checkStatus();
		controllingThread.notifyWaitingForPickFromMachine(this);
		checkStatus();
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting before pick from machine");
				syncObject.wait();
				logger.info("Can continue");
			}
		}
		if (!running) {
			return;
		}
		if (controllingThread.isConcurrentExecutionPossible() && (processFlow.getFinishedAmount() < (processFlow.getTotalAmount() - 1))) {
			pickStep.getRobotSettings().setFreeAfter(false);
		} else {
			pickStep.getRobotSettings().setFreeAfter(true);
		}
		checkStatus();
		pickStep.executeStep(workpieceId, this);
		checkStatus();
		pickStep.finalizeStep(this);
		canContinue = false;
		checkStatus();
		controllingThread.notifyPickFromMachineFinished(this);
		checkStatus();
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting after pick from machine");
				syncObject.wait();
				logger.info("Can continue");
			}
		}
	}
	
	@Override
	public void interrupt() {
		stopRunning();
	}
	
	public void stopRunning() {
		this.running = false;
		synchronized(syncObject) {
			syncObject.notifyAll();
		}
	}
	
	public void setRunning(final boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void continueExecution() {
		this.canContinue = true;
		synchronized(syncObject) {
			syncObject.notify();
		}
	}
	
	@Override
	public String toString() {
		return "ProcessFlowExecutionThread - WP[" + workpieceId + "]";
	}
}
