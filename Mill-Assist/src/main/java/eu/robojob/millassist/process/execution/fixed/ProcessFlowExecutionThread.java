package eu.robojob.millassist.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Type;
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
	private boolean waitingForIntervention;
	private Object syncObject2;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowExecutionThread.class.getName());
	
	private static final int POLLING_INTERVAL = 500;
	
	public ProcessFlowExecutionThread(final AutomateFixedControllingThread controllingThread,
			final ProcessFlow processFlow, final int workpieceId) {
		this.controllingThread = controllingThread;
		this.processFlow = processFlow;
		this.workpieceId = workpieceId;
		this.syncObject = new Object();
		this.canContinue = false;
		this.waitingForIntervention = false;
		this.syncObject2 = new Object();
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
				
				if (waitingForIntervention) {
					synchronized(syncObject2) {
						logger.info("Waiting for intervention");
						syncObject2.wait();
						logger.info("Can continue after intervention");
					}
				}
				
				AbstractProcessStep currentStep = processFlow.getStep(processFlow.getCurrentIndex(workpieceId));
				
				logger.info("ProcessFlowExecution, current step = " + currentStep);
				
				checkStatus();
				
				if (currentStep instanceof RobotStep) {
					((RobotStep) currentStep).getRobotSettings().setFreeAfter(false);
				}
				if (currentStep instanceof InterventionStep) {
					executeInterventionStep((InterventionStep) currentStep);
				} else if ((currentStep instanceof PutStep) && (((PutStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					checkStatus();
					executePutInMachineStep((PutStep) currentStep);
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					checkStatus();
					executePickFromMachineStep((PickStep) currentStep);
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof AbstractStackingDevice)) {
						// we can always return to home, as home is typically the same location as the stacker's IP
						// if no pre-processing is needed, we will than be waiting in home before putting in machine
						((PickStep) currentStep).getRobotSettings().setFreeAfter(true);
						executePickFromStackerStep((PickStep) currentStep);
				} else {
					if ((currentStep instanceof PutStep) && (((PutStep) currentStep).getDevice() instanceof AbstractStackingDevice)) {
						// check if after this step no more pick is needed, so we can return to home, only do this if the process is not continuous
						if ((processFlow.getType() == Type.FIXED_AMOUNT) && ((processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 1) || 
								((processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 2)) && controllingThread.isConcurrentExecutionPossible())) {
							((PutStep) currentStep).getRobotSettings().setFreeAfter(true);
						}
					}
					if ((currentStep instanceof PutAndWaitStep) && controllingThread.isConcurrentExecutionPossible() && 
							!controllingThread.isFirstPiece()) {
						// if not the first: go to home after pre-processing step to wait before putting in machine
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
					canContinue = false;
					controllingThread.notifyProcessFlowFinished(this);
					if (waitingForIntervention) {
						synchronized(syncObject2) {
							logger.info("Waiter after processflow finished.");
							syncObject2.wait();
							logger.info("Can continue new processflow.");
						}
					}
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
	
	public void executeInterventionStep(final InterventionStep interventionStep) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException {
		if (interventionStep.isInterventionNeeded()) {
			interventionStep.executeStep(workpieceId, this);
			canContinue = false;
			checkStatus();
			// notify master of intervention, it will, in turn notify other running executor-threads
			controllingThread.notifyWaitingOnIntervention();
			checkStatus();
			if (waitingForIntervention) {
				synchronized(syncObject2) {
					logger.info("Waiting for intervention");
					syncObject2.wait();
					logger.info("Can continue after intervention");
				}
			}
			checkStatus();
			interventionStep.interventionFinished();
		}
	}
	
	public void waitForIntervention() {
		this.waitingForIntervention = true;
	}
	
	public void interventionFinished() {
		this.waitingForIntervention = false;
		synchronized(syncObject2) {
			syncObject2.notify();
		}
	}
	
	public void executePickFromStackerStep(final PickStep pickStep) throws InterruptedException, AbstractCommunicationException, RobotActionException, DeviceActionException {
		checkStatus();
		canContinue = false;
		controllingThread.notifyWaitingBeforePickFromStacker(this);
		checkStatus();
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting before pick from stacker.");
				syncObject.wait();
				logger.info("Can continue.");
			}
		}
		if (!running) {
			return;
		}
		while (!pickStep.getDevice().canPick(pickStep.getDeviceSettings())) {
			Thread.sleep(POLLING_INTERVAL);
		}
		checkStatus();
		canContinue = false;
		controllingThread.notifyWorkPiecesPresent(this);
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting for pick from stacker (work pieces present).");
				syncObject.wait();
				logger.info("Can continue.");
			}
		}
		if (!running) {
			return;
		}
		checkStatus();
		pickStep.executeStep(workpieceId, this);
		checkStatus();
		pickStep.finalizeStep(this);
	}
	
	public void executePutInMachineStep(final PutStep putStep) throws InterruptedException, AbstractCommunicationException, RobotActionException, DeviceActionException {
		checkStatus();
		canContinue = false;
		controllingThread.notifyWaitingBeforePutInMachine(this);
		checkStatus();
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting before put in machine");
				syncObject.wait();
				logger.info("Can continue.");
			}
		}
		if (!running) {
			return;
		}
		putStep.getRobotSettings().setFreeAfter(true);	// we can always go back to home after putting a wp in the machine
		checkStatus();
		putStep.executeStep(workpieceId, this);
		checkStatus();
		putStep.finalizeStep(this);
		checkStatus();
		controllingThread.notifyPutInMachineFinished(this);
	}
	
	public void executePickFromMachineStep(final PickStep pickStep) throws AbstractCommunicationException, RobotActionException, InterruptedException, DeviceActionException {
		checkStatus();
		canContinue = false;
		controllingThread.notifyWaitingBeforePickFromMachine(this);
		checkStatus();
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting before pick from machine.");
				syncObject.wait();
				logger.info("Can continue.");
			}
		}
		if (!running) {
			return;
		}
		pickStep.getRobotSettings().setFreeAfter(false);
		checkStatus();
		pickStep.executeStep(workpieceId, this);
		checkStatus();
		pickStep.finalizeStep(this);
		checkStatus();
		canContinue = false;
		controllingThread.notifyWaitingAfterPickFromMachine(this);
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
		logger.info("Continue!");
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
