package eu.robojob.irscw.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.PutAndWaitStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.RobotStep;

public class ProcessFlowExecutionThread extends Thread {

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
	
	@Override 
	public void run() {
		this.running = true;
		try {
			while (running) {
				
				AbstractProcessStep currentStep = processFlow.getStep(processFlow.getCurrentIndex(workpieceId));
				
				if (currentStep instanceof RobotStep) {
					((RobotStep) currentStep).getRobotSettings().setFreeAfter(false);
				}
				
				if ((currentStep instanceof PutStep) && (((PutStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					executePutInMachineStep((PutStep) currentStep);
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
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
						logger.info("* SET FREE AFTER PRAGE");
						((PutAndWaitStep) currentStep).getRobotSettings().setFreeAfter(true);
					}
					currentStep.executeStep(workpieceId);
					if (currentStep instanceof AbstractTransportStep) {
						((AbstractTransportStep) currentStep).finalizeStep();
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
			if (!controllingThread.isRunning()) {
				controllingThread.stopExecution();
			} else {
				controllingThread.notifyException(e);
				controllingThread.stopExecution();
			}
		} catch (Exception e) {
			controllingThread.notifyException(e);
			controllingThread.stopExecution();
		}
	}
	
	public void executePutInMachineStep(final PutStep putStep) throws InterruptedException, AbstractCommunicationException, RobotActionException, DeviceActionException {
		canContinue = false;
		controllingThread.notifyWaitingForPutInMachine(this);
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting before put in machine");
				syncObject.wait();
				logger.info("Can continue");
			}
		}
		putStep.getRobotSettings().setFreeAfter(true);
		putStep.executeStep(workpieceId);
		putStep.finalizeStep();
		canContinue = false;
		controllingThread.notifyPutInMachineFinished(this);
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
		controllingThread.notifyWaitingForPickFromMachine(this);
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting before pick from machine");
				syncObject.wait();
				logger.info("Can continue");
			}
		}
		if (controllingThread.isConcurrentExecutionPossible() && (processFlow.getFinishedAmount() < (processFlow.getTotalAmount() -2))) {
			pickStep.getRobotSettings().setFreeAfter(false);
		} else {
			pickStep.getRobotSettings().setFreeAfter(true);
		}
		pickStep.executeStep(workpieceId);
		pickStep.finalizeStep();
		canContinue = false;
		controllingThread.notifyPickFromMachineFinished(this);
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting after pick from machine");
				syncObject.wait();
				logger.info("Can continue");
			}
		}
	}
	
	public void stopRunning() {
		this.running = false;
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
}
