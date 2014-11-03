package eu.robojob.millassist.process.execution.fixed;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.ProcessFlow.Type;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.fixed.ProcessFlowExecutionThread.ExecutionThreadStatus;
import eu.robojob.millassist.threading.ThreadManager;

public class AutomateControllingThread extends AbstractFixedControllingThread {
	
	public AutomateControllingThread(final ProcessFlow processFlow, final int nbProcesses) {
		super(processFlow, nbProcesses);
	}

	@Override
	public void run() {
		try {
			initRun();			
			if ((processFlow.getCurrentIndex(PROCESS_0_ID) == -1) || (processFlow.getCurrentIndex(PROCESS_0_ID) == 0)) {
				processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.PREPARE, PROCESS_0_ID));
				for (AbstractRobot robot :processFlow.getRobots()) {	// first recalculate TCPs
					checkStatus();
					robot.recalculateTCPs();
					robot.setCurrentActionSettings(null);
				}
				for (AbstractDevice device: processFlow.getDevices()) {	// prepare devices for this processflow
					checkStatus();
					device.prepareForProcess(processFlow);
				}
				for (int i = PROCESS_0_ID; i < nbProcesses; i++) {
					processFlow.setCurrentIndex(i, 0);
				}
			}
			if (processFlow.getCurrentIndex(PROCESS_1_ID) == -1) {
				for (int i = PROCESS_1_ID; i < nbProcesses; i++) {
					processFlow.setCurrentIndex(i, 0);
				}
			}
			checkStatus();
			processFlowExecutors[PROCESS_0_ID] = new ProcessFlowExecutionThread(this, processFlow, PROCESS_0_ID);
			boolean startSecond = false;
			if (processFlow.getCurrentIndex(PROCESS_0_ID) > 0) {
				// process has already passed some steps, check if current step is processing in machine
				// then second process can start!
				AbstractProcessStep step = processFlow.getStep(processFlow.getCurrentIndex(PROCESS_0_ID) - 1);
				if (step instanceof PutStep) {
					if (((PutStep) step).getDevice() instanceof AbstractCNCMachine) {
						int nbActiveClamping = ((PutStep) step).getDeviceSettings().getWorkArea().getNbActiveClampingsEachSide();
						processFlow.setCurrentIndex(PROCESS_0_ID, processFlow.getCurrentIndex(PROCESS_0_ID) - 1);
						//Show correct status after teaching
						processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, (PutStep) step, StatusChangedEvent.ENDED, PROCESS_0_ID));
						if(nbActiveClamping > 1 && processFlow.getTotalAmount() > 1) {
							processFlowExecutors[PROCESS_0_ID].setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER);
							processFlow.setCurrentIndex(PROCESS_0_ID, 0);
						} else {
							processFlow.setCurrentIndex(PROCESS_0_ID, processFlow.getCurrentIndex(PROCESS_0_ID) + 1);
							processFlowExecutors[PROCESS_0_ID].setExecutionStatus(ExecutionThreadStatus.PROCESSING_IN_MACHINE);	
						}
						processFlowExecutors[PROCESS_0_ID].incrementNbInMachine();
						processFlowExecutors[PROCESS_0_ID].incrementNbInFlow();
						if (processFlow.getCurrentIndex(PROCESS_1_ID) == 0) {
							if (isConcurrentExecutionPossible()) {
								startSecond = true;
							}
						}
					}
				} 
			}
			processFlowExecutorFutures[PROCESS_0_ID] = ThreadManager.submit(processFlowExecutors[PROCESS_0_ID]);
			if (startSecond) {
				firstPiece = false;
				processFlowExecutorFutures[PROCESS_1_ID] = ThreadManager.submit(processFlowExecutors[PROCESS_1_ID]);
			}	
			synchronized(finishedSyncObject) {
				finishedSyncObject.wait();
			}
			checkStatus();
			if (finished) {
				processFlow.setMode(Mode.FINISHED);
				for (AbstractDevice device : processFlow.getDevices()) {
					if (device instanceof AbstractCNCMachine) {
						checkStatus();
						((AbstractCNCMachine) device).indicateAllProcessed();
					}
				}
				for (AbstractRobot robot : processFlow.getRobots()) {
					checkStatus();
					robot.moveToHome();
				}
			}
			for (int i = 0; i < nbProcesses; i++) {
				processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, i));
			}
			logger.info(toString() + " ended...");
		} catch(InterruptedException e) {
			interrupted();
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
	synchronized void notifyWaitingBeforePickFromStacker(ProcessFlowExecutionThread processFlowExecutor) {
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER);
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITH_ROBOT)
					|| processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER)) {
				return;
			}
		}
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processExecutor.continueExecution();
				return;
			}
		}
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
		processFlowExecutor.continueExecution();
	}

	@Override
	synchronized void notifyWorkPiecesPresent(ProcessFlowExecutionThread processFlowExecutor) {
		// work pieces present, can continue if no other executor is working with robot
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER);
		if (isRobotFree()) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
			processFlowExecutor.continueExecution();	
		}
	}

	@Override
	synchronized void notifyWaitingBeforePutInMachine(ProcessFlowExecutionThread processFlowExecutor) {
		if (!processFlowExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PUT_MACHINE_BEFORE_REVERSAL)) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE);
		}
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE)) { 
				processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processExecutor.continueExecution();
				return;
			}
		}
		if (isFreePlaceInMachine()) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
			processFlowExecutor.continueExecution();
		} else {
			try {
				processFlow.getRobots().iterator().next().moveToHome();
			} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
	}

	@Override
	synchronized void notifyPutInMachineFinished(final ProcessFlowExecutionThread processFlowExecutor, final boolean moreToPut,
			final int nbActiveClampings, final int nbFilled) {
		// This processFlow has no more pieces to put in the machine - processing can start once the other processflow has cleared the clampings
		if(!moreToPut) {
			logger.info("Put in machine finished.");
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_IN_MACHINE);
			// Test whether all are filled by this process before we can start processing. If this is false, it could be that this side is still
			// occupied by (finished) workpieces from another executor
			boolean isContinuing = false;
			if(nbActiveClampings == nbFilled) {
				//No need to return, because processing will start
				processFlowExecutor.continueExecution();
				isContinuing = true;
			}
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) { 
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processExecutor.continueExecution();
					return;
				}
			}
			//start a new process - we do not have to check for concurrentProcessing because we have only created the max nb of processes possible at init (taking into account concurrentExecution)
			if (startNewProcess()) {
				return;
			}
			//If all processes are already running, check which one can execute next
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER)) { 
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
					processExecutor.continueExecution();
					return;
				}
			}
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER)) { 
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processExecutor.continueExecution();
					return;
				}
			}
			if (!isContinuing) {
				processFlowExecutor.continueExecution();
			}
			try {
				processFlow.getRobots().iterator().next().moveToHome();
			} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
	}
	
	protected synchronized boolean startNewProcess() {
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (!processExecutor.isRunning() && stillPieceToDo()) { 
				logger.info("Process " + processExecutor.getProcessId() + " not yet started");
				processFlowExecutorFutures[processExecutor.getProcessId() - 1] = ThreadManager.submit(processExecutor);
				return true;
			} else if (!stillPieceToDo()) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		}
		return false;
	}

	@Override
	synchronized void notifyNoWorkPiecesPresent(ProcessFlowExecutionThread processFlowExecutor) {
		//There are no workpieces left. Pick another process to continue its execution
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE)) { 
				processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processExecutor.continueExecution();
				return;
			}
		}
		if (isRobotFree()) {
			try {
				processFlow.getRobots().iterator().next().moveToHome();
			} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
	}

	@Override
	synchronized void notifyWaitingBeforePickFromMachine(ProcessFlowExecutionThread processFlowExecutor) {
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE);
		if (isRobotFree()) {
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER)) { 
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processExecutor.continueExecution();
					return;
				}
			}
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
			processFlowExecutor.continueExecution();
		}
	}

	/**
	 * This method is called whenever the robot has picked a finished piece from the machine. In case another process
	 * is currently waiting to put a piece in the machine, we give it the priority.
	 *
	 * @param processFlowExecutor
	 */
	@Override
	synchronized void notifyWaitingAfterPickFromMachine(ProcessFlowExecutionThread processFlowExecutor) {
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE);
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(getFirstPutState())) { 
				processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				//TODO - turnInMachine here
				processExecutor.continueExecution();
				return;
			}
		}
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
		processFlowExecutor.continueExecution();
	}

	@Override
	synchronized void notifyProcessFlowFinished(ProcessFlowExecutionThread processFlowExecutor) {
		mainProcessFlowId = (processFlowExecutor.getProcessId() + 1) % (nbProcesses);
		//All pieces are currently in the flow, so finish this processExecutor
		if (processFlow.getType().equals(Type.FIXED_AMOUNT) && !stillPieceToDo()) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.FINISHED);
			boolean isFinished = true;
			//Pick the next one to execute
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER)) {
					isFinished = false;
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
					processExecutor.continueExecution();
					break;
				}
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE)) { 
					isFinished = false;
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processExecutor.continueExecution();
					break;
				}
			}	
			if (isFinished) {
				for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
					if (processExecutor.getNbWPInMachine() > 0) {
						processExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_IN_MACHINE);
						processExecutor.continueExecution();
					}
				}
			}
			processFlowExecutor.stopRunning();
			//All workpieces are done, so also stop this thread
			if (processFlow.getFinishedAmount() == processFlow.getTotalAmount()) {
				finished = true;
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
				synchronized(finishedSyncObject) {
					finishedSyncObject.notify();
				}
			}
		} else {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.FINISHED);
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.IDLE);
			processFlowExecutor.continueExecution();
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (!processExecutor.equals(processFlowExecutor)) {
					if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER)) {
						processExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
						processExecutor.continueExecution();
						return;
					}
					if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER)) {
						processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
						processExecutor.continueExecution();
						return;
					}
				}
			}	
		}
	}

	@Override
	public String toString() {
		return "AutomateControllingThread - processflow [" + processFlow + "]";
	}

	@Override
	protected boolean isFreePlaceInMachine() {
		return (getNbProcessesInMachine() < getNbConcurrentProcessesInMachine());
	}
	
	protected synchronized int getNbProcessesInMachine() {
		int nbInMachine = 0;
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.PROCESSING_IN_MACHINE)) {
				nbInMachine++;
			}
		}
		return nbInMachine;
	}

	@Override
	protected ExecutionThreadStatus getFirstPutState() {
		return ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE;
	}

}
