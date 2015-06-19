package eu.robojob.millassist.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.NoFreeClampingInWorkareaException;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.robot.Gripper;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Type;
import eu.robojob.millassist.process.ProcessingStep;
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
		WAITING_FOR_PICK_STACKER_VACUUM,
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
	private int nbWPInFlow = 0;
	private int nbWPInMachine = 0;
	private int nbWPReversed = 0;
	private boolean vacuumContinue = false;
	private Object syncObject2;
	// Is the reversal still to come?
	private boolean needsReversal;
	private int nbCNCPassed = 0;
	private boolean canStartProcessing = false;
	
	private boolean isTIMPossible = false;
	
	private int pickFromStackerStepIndex;
	private int pickFromMachineStepIndex;
	private int pickFromMachineBeforeReversalStepIndex = -1;
	
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
				else if (currentStep instanceof PickStep && (((PickStep) currentStep).getDevice() instanceof ReversalUnit)) {
					if (processFlow.getNbCNCInFlow() - 1 == nbCNCPassed) {
						needsReversal = false;
					} else {
						needsReversal = true;
					}
					checkStatus();
					currentStep.executeStep(processId, this);
					nbWPReversed++;
					checkStatus();
					((PickStep) currentStep).finalizeStep(this);
				}
				else if ((currentStep instanceof PutStep) && (((PutStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					checkStatus();
					executePutInMachineStep((PutStep) currentStep);
					vacuumContinue = false;
					//More workpieces to put (multiple fixtures present - jump back a few steps)
					//TODO - review (nbCNCPassed to use?)
					if (morePutsToPerform((PutStep) currentStep) && processFlow.hasReversalUnit() && nbWPReversed < nbWPInMachine && nbWPReversed > 0) {
						needsReversal = true;
						processFlow.setCurrentIndex(processId, pickFromMachineBeforeReversalStepIndex-1);
					} else if (morePutsToPerform((PutStep) currentStep)) {
						vacuumContinue = true;
						processFlow.setCurrentIndex(processId, pickFromStackerStepIndex-1);
					}
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof AbstractCNCMachine)) {
					checkStatus();
					executePickFromMachineStep((PickStep) currentStep);
					if (needsReversal) {
						pickFromMachineBeforeReversalStepIndex = processFlow.getCurrentIndex(processId);
					} else {
						pickFromMachineStepIndex = processFlow.getCurrentIndex(processId);
					}
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof AbstractStackingDevice)) {
					// we can always return to home, as home is typically the same location as the stacker's IP
					// if no pre-processing is needed, we will than be waiting in home before putting in machine
					((PickStep) currentStep).getRobotSettings().setFreeAfter(false);
					executePickFromStackerStep((PickStep) currentStep);
					pickFromStackerStepIndex = processFlow.getCurrentIndex(processId);
				} else if (currentStep instanceof ProcessingStep) {
					checkStatus();
					executeProcessingStep((ProcessingStep) currentStep);
					if (((ProcessingStep) currentStep).getDevice() instanceof AbstractCNCMachine) {
						nbWPReversed = 0;
						nbCNCPassed++;
					}
				} else {
					if ((currentStep instanceof PutStep) && (((PutStep) currentStep).getDevice() instanceof AbstractStackingDevice)) {
						// check if after this step no more pick is needed, so we can return to home, only do this if the process is not continuous
						//TODO - review for multiple fixtures
					    if(((PutStep)currentStep).mustExecuteInterventionStep()) {
					        executeInterventionStep(((PutStep)currentStep).getInterventionStep());
					    }
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
					nbWPInFlow--;
					if (nbWPInMachine > 0) {
						processFlow.setCurrentIndex(processId, pickFromMachineStepIndex);
					} else {
						processFlow.setCurrentIndex(processId, 0);
						nbCNCPassed = 0;
						canContinue = false;
						if (processFlow.hasReversalUnit()) {
							needsReversal = true;
						}	
						controllingThread.notifyProcessFlowFinished(this);
						if (waitingForIntervention) {
							synchronized(syncObject2) {
								logger.info("Waiting after processflow finished.");
								syncObject2.wait();
								logger.info("Can continue new processflow.");
							}
						}
					}	
				} else {
					processFlow.setCurrentIndex(processId, processFlow.getCurrentIndex(processId) + 1);
				}
			}
			logger.info(toString() + " ended...");
		} catch (InterruptedException e) {
			logger.debug("Exception received, we will stop running thread for PRC["+ processId + "]");
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
		nbWPInFlow++;
		checkStatus();
		pickStep.finalizeStep(this);
	}
	
	public boolean isWaitForVacuum() {
		if (!processFlow.isConcurrentExecutionPossible() || controllingThread.getNbProcessesWithStatus(ExecutionThreadStatus.IDLE) >= 1) {
			return false;
		}
		if (vacuumContinue) {
			return false;
		}
		PickStep pickStep = (PickStep) processFlow.getStep(processFlow.getCurrentIndex(processId));
		if (pickStep.getRobotSettings().getGripperHead().getGripper().getType().equals(Gripper.Type.VACUUM)) {
			return true;
		}
		return false;
	}
	
	private void executePutInMachineStep(final PutStep putStep) throws InterruptedException, AbstractCommunicationException, RobotActionException, DeviceActionException, NoFreeClampingInWorkareaException {
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
		while (!putStep.getDevice().canPut(putStep.getDeviceSettings())) {
			Thread.sleep(POLLING_INTERVAL);
			checkStatus();
		}
		checkStatus();
		putStep.getRobotSettings().setFreeAfter(false);	// we can always go back to home after putting a wp in the machine
		checkStatus();
		// Because there are multiple possible clampings in one workarea, we need to ask for a clamping that is free at the moment.
		// This one can then be used to put the workpiece in the machine. The clamping will be freed after the pickStep of this
		// processExecutor is finished
		putStep.getDeviceSettings().getWorkArea().getFreeActiveClamping(processId);
		putStep.executeStep(processId, this);
		setTIMPossible(false);
		nbWPInMachine++;
		checkStatus();		
		putStep.finalizeStep(ProcessFlowExecutionThread.this);
		canStartProcessing = false;
		ThreadManager.submit(new Thread() {
			@Override
			public void run() {
				try {
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
				controllingThread.notifyPutInMachineFinished(ProcessFlowExecutionThread.this, morePutsToPerform(putStep),
						putStep.getDeviceSettings().getWorkArea().getWorkAreaManager().getNbActiveClampingsEachSide(), 
						putStep.getDeviceSettings().getWorkArea().getNbClampingsPerProcessThread(processId));
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
		setTIMPossible(false);
		checkStatus();
		pickStep.finalizeStep(this);
		// Free the clamping that was used by the process so that it can be re-used by the next one
		pickStep.getDeviceSettings().getWorkArea().getWorkAreaManager().freeClamping(processId);
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
		nbWPInMachine--;
	}
	
	public void executeProcessingStep(final ProcessingStep processingStep) throws InterruptedException, AbstractCommunicationException, DeviceActionException {
		checkStatus();
//		int nbOfClampingsInUse = processingStep.getDeviceSettings().getWorkArea().getWorkAreaManager().getNbActiveClampingsEachSide();
		
		//In case we have more than one clamping in use, it could be that we have to block the execution thread. Consider the following
		//scenario with 2 clampings: we still have 1 piece to do and 2 pieces are done, so ready to be taken out the clampings. 
		//The executor waiting for the pick from the machine has the priority (otherwise no clamping would be free). The piece gets
		//taken and thus a clamp is free for put. This executor does the putInMachine step and continues. At this point, there is one raw
		//and one finished workpiece in the machine. The next step of this executor is processing in machine (because we only have one piece
		//to do in the entire process), but first we have to get the finished piece from the flow - so this executor has to be blocked.
		//Side note - it is possible to remove nbOfClampingsInUse test, but it is a little bit more efficient since this is the most frequent case.
		
//		if(nbOfClampingsInUse > 1) {
			
			//Due to timing of threads it could be that we are notified before the wait was started (see call of notifyPutInMachineFinished
			//in method executePutInMachineStep). Therefore, we check the canStartProcessing flag that is only activated in case the
			//startProcessing method is called by one of the ControllingThread classes.
			if(!canStartProcessing && processingStep.getDevice() instanceof AbstractCNCMachine) {
				synchronized(syncObject) {
					logger.info("Waiting before processing can start.");
					syncObject.wait();
					logger.info("Can continue.");
				}
			}
//		}
		processingStep.executeStep(processId, this);
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
	
	/**
	 * Continue the execution of this executor. 
	 */
	public void continueExecution() {
		logger.info("Continue!");
		this.canContinue = true;
		synchronized(syncObject) {
			syncObject.notify();
		}
	}
	
	/**
	 * Special method similar to continueExecution. This method is called before we continue 
	 * with processing in the machine.
	 * 
	 * @see #continueExecution()
	 */
	public void startProcessing() {
		logger.info("Start processing");
		this.canContinue = true;
		this.canStartProcessing = true;
		synchronized(syncObject) {
			syncObject.notify();
		}
	}
	
	synchronized int getNbInFlow() {
		return this.nbWPInFlow;
	}
	
	int getNbWPInMachine() {
		return this.nbWPInMachine;
	}
	
	void incrementNbInMachine() {
		this.nbWPInMachine++;
	}
	
	void incrementNbInFlow() {
		this.nbWPInFlow++;
	}
	
	private synchronized boolean morePutsToPerform(PutStep step) {
		int nbActiveClamping = step.getDeviceSettings().getWorkArea().getWorkAreaManager().getNbActiveClampingsEachSide();
		if (processFlow.hasReversalUnit() && nbWPReversed < nbWPInFlow && nbWPReversed > 0) {
			return true;
		}
		if (nbWPInMachine >= nbActiveClamping) {
			return false;
		}
		return controllingThread.stillPieceToDo();
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
	
	ProcessFlow getProcessFlow() {
		return processFlow;
	}
	
	@Override
	public String toString() {
		return "ProcessFlowExecutionThread - PRC[" + processId + "]";
	}

	public boolean isTIMPossible() {
		return isTIMPossible;
	}

	public void setTIMPossible(boolean isTIMPossible) {
		this.isTIMPossible = isTIMPossible;
	}
}
