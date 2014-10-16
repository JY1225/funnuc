package eu.robojob.millassist.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.robot.AbstractRobot;
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
import eu.robojob.millassist.threading.ThreadManager;

public class ProcessFlowExecutionThread implements Runnable, ProcessExecutor {

	enum ExecutionThreadStatus {
		WORKING_WITH_ROBOT,
		IDLE,
		WAITING_BEFORE_PICK_FROM_STACKER,
		WAITING_FOR_WORKPIECES_STACKER,
		WAITING_FOR_PICK_FROM_STACKER,
		WAITING_BEFORE_PUT_IN_MACHINE,
		PROCESSING_IN_MACHINE,
		WAITING_BEFORE_PICK_FROM_MACHINE,
		WAITING_AFTER_PICK_FROM_MACHINE,
		FINISHED,
		
		//States in case of presence of reversal unit - all states related to first CNC machine
		WAITING_FOR_PICK_MACHINE_BEFORE_REVERSAL,
		REVERSING_WITH_ROBOT,
		PROCESSING_BEFORE_REVERSAL,
		WAITING_BEFORE_PUT_MACHINE_BEFORE_REVERSAL;
	}
	
	private ExecutionThreadStatus executionStatus;
	
	private ProcessFlow processFlow;
	private int processId;
	private AbstractFixedControllingThread controllingThread;
	private Object syncObject;
	private boolean running;
	private boolean canContinue;
	private boolean waitingForIntervention;
	private int nbInFlow;
	private Object syncObject2;
	// Is the reversal still to come?
	private boolean needsReversal;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowExecutionThread.class.getName());
	
	private static final int POLLING_INTERVAL = 500;
	
	public ProcessFlowExecutionThread(final AbstractFixedControllingThread controllingThread,
			final ProcessFlow processFlow, final int processId) {
		this.controllingThread = controllingThread;
		this.processFlow = processFlow;
		this.processId = processId;
		this.syncObject = new Object();
		this.canContinue = false;
		this.waitingForIntervention = false;
		this.needsReversal = false;
		this.syncObject2 = new Object();
		this.executionStatus = ExecutionThreadStatus.IDLE;
		this.nbInFlow = 0;
	}
	
	private void checkStatus() throws InterruptedException {
		if (!running) {
			throw new InterruptedException("Got interrupted during execution of processflow");
		}
	}
	
	@Override 
	public void run() {
		this.running = true;
		if (processFlow.hasReversalUnit()) {
			needsReversal = true;
		}
		try {
			while (running) {
				
				if (waitingForIntervention) {
					synchronized(syncObject2) {
						logger.info("Waiting for intervention");
						syncObject2.wait();
						logger.info("Can continue after intervention");
					}
				}
				
				AbstractProcessStep currentStep = processFlow.getStep(processFlow.getCurrentIndex(processId));
				
				logger.info("ProcessFlowExecution for PRC[" + processId +"], current step = " + currentStep);
				
				checkStatus();
				
				if (currentStep instanceof RobotStep) {
					((RobotStep) currentStep).getRobotSettings().setFreeAfter(false);
				}
				if (currentStep instanceof InterventionStep) {
					executeInterventionStep((InterventionStep) currentStep);
				} 
				if (currentStep instanceof PickStep && (((PickStep) currentStep).getDevice() instanceof ReversalUnit)) {
					needsReversal = false;
					checkStatus();
					currentStep.executeStep(processId, this);
					checkStatus();
					((PickStep) currentStep).finalizeStep(this);
				}
				else if ((currentStep instanceof PutStep) && (((PutStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					checkStatus();
					executePutInMachineStep((PutStep) currentStep);
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					checkStatus();
					executePickFromMachineStep((PickStep) currentStep);
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof AbstractStackingDevice)) {
					// we can always return to home, as home is typically the same location as the stacker's IP
					// if no pre-processing is needed, we will than be waiting in home before putting in machine
					nbInFlow++;
					((PickStep) currentStep).getRobotSettings().setFreeAfter(false);
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
					currentStep.executeStep(processId, this);
					if (currentStep instanceof AbstractTransportStep) {
						checkStatus();
						((AbstractTransportStep) currentStep).finalizeStep(this);
					}
					checkStatus();
				}
				
				if (processFlow.getCurrentIndex(processId) == processFlow.getProcessSteps().size() - 1) {
					processFlow.setCurrentIndex(processId, 0);
					processFlow.incrementFinishedAmount();
					if (processFlow.hasReversalUnit()) {
						needsReversal = true;
					}
					canContinue = false;
					nbInFlow = 0;
					controllingThread.notifyProcessFlowFinished(this);
					if (waitingForIntervention) {
						synchronized(syncObject2) {
							logger.info("Waiter after processflow finished.");
							syncObject2.wait();
							logger.info("Can continue new processflow.");
						}
					}
				} else {
					processFlow.setCurrentIndex(processId, processFlow.getCurrentIndex(processId) + 1);
				}
			}
			logger.info(toString() + " ended...");
		} catch (InterruptedException e) {
			interrupted();
		} catch (Exception e) {
			controllingThread.notifyException(e);
			//controllingThread.stopExecution();
			controllingThread.stopRunning();
		} finally {
			stopRunning();
		}
	}
	
	public void executeInterventionStep(final InterventionStep interventionStep) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException {
		if (interventionStep.isInterventionNeeded()) {
			interventionStep.executeStep(processId, this);
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
	
	private void executePickFromStackerStep(final PickStep pickStep) throws InterruptedException, AbstractCommunicationException, RobotActionException, DeviceActionException {
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
		if (!pickStep.getDevice().canPick(pickStep.getDeviceSettings())) {
			controllingThread.notifyNoWorkPiecesPresent(this);
			Thread.sleep(POLLING_INTERVAL);
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
		pickStep.executeStep(processId, this);
		checkStatus();
		pickStep.finalizeStep(this);
	}
	
	private void executePutInMachineStep(final PutStep putStep) throws InterruptedException, AbstractCommunicationException, RobotActionException, DeviceActionException {
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
		if (!putStep.getDevice().canPut(putStep.getDeviceSettings())) {
			// send robot to home
			for (AbstractRobot robot : processFlow.getRobots()) {
				checkStatus();
				robot.moveToHome();
			}
			checkStatus();
			Thread.sleep(POLLING_INTERVAL);
		}
		while (!putStep.getDevice().canPut(putStep.getDeviceSettings())) {
			Thread.sleep(POLLING_INTERVAL);
			checkStatus();
		}
		checkStatus();
		putStep.getRobotSettings().setFreeAfter(false);	// we can always go back to home after putting a wp in the machine
		checkStatus();
		putStep.executeStep(processId, this);
		checkStatus();		
		ThreadManager.submit(new Thread() {
			@Override
			public void run() {
				try {
					putStep.finalizeStep(ProcessFlowExecutionThread.this);
					checkStatus();
				} catch (InterruptedException e) {
					if (controllingThread.isRunning()) {
						controllingThread.notifyException(e);
						controllingThread.stopRunning();
					}
				} catch (Exception e) {
					controllingThread.notifyException(e);
					controllingThread.stopRunning();
				}
				controllingThread.notifyPutInMachineFinished(ProcessFlowExecutionThread.this);
			}
		});		
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
		pickStep.executeStep(processId, this);
		checkStatus();
		pickStep.finalizeStep(this);
		checkStatus();
		if (!needsReversal) {
			canContinue = false;
			controllingThread.notifyWaitingAfterPickFromMachine(this);
		} else {
			canContinue = true;
			logger.info("Going to reversal unit");
		}
		checkStatus();
		if (!canContinue) {
			synchronized(syncObject) {
				logger.info("Waiting after pick from machine");
				syncObject.wait();
				logger.info("Can continue");
			}
		}
	}
	
	public void interrupted() {
		stopRunning();
		if (controllingThread.isRunning()) {
			controllingThread.stopRunning();
		}
	}
	
	public void stopRunning() {
		logger.info("Stop running processFlowExecutionThread");
		this.needsReversal = false;
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
	
	boolean needsReversal() {
		return this.needsReversal;
	}
	
	public void continueExecution() {
		logger.info("Continue!");
		this.canContinue = true;
		synchronized(syncObject) {
			syncObject.notify();
		}
	}
	
	int getNbInFlow() {
		return this.nbInFlow;
	}
	
	int getProcessId() {
		return this.processId;
	}
	
	ExecutionThreadStatus getExecutionStatus() {
		return this.executionStatus;
	}
	
	void setExecutionStatus(ExecutionThreadStatus executionStatus) {
		if (!this.executionStatus.equals(executionStatus)) {
			logger.debug("STATUS PRC[" + processId + "] = " + executionStatus);
			this.executionStatus = executionStatus;
		}
	}
	
	@Override
	public String toString() {
		return "ProcessFlowExecutionThread - PRC[" + processId + "]";
	}
}
