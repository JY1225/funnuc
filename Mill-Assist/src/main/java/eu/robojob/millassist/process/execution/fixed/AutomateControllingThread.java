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
				// than second process can start!
				AbstractProcessStep step = processFlow.getStep(processFlow.getCurrentIndex(PROCESS_0_ID) - 1);
				if (step instanceof PutStep) {
					if (((PutStep) step).getDevice() instanceof AbstractCNCMachine) {
						processFlowExecutors[PROCESS_0_ID].setExecutionStatus(ExecutionThreadStatus.PROCESSING_IN_MACHINE);
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
	synchronized void notifyPutInMachineFinished(ProcessFlowExecutionThread processFlowExecutor) {
		logger.info("Put in machine finished.");
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_IN_MACHINE);
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) { 
				processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processExecutor.continueExecution();
				return;
			}
		}
		//start a new process - we do not have to check for concurrentProcessing since we have done this at init
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
		//All piece are currently in the flow, so finish this processExecutor
		if (processFlow.getType().equals(Type.FIXED_AMOUNT) && !stillPieceToDo()) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.FINISHED);
			//Pick the next one to execute
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE)) { 
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processExecutor.continueExecution();
					return;
				}
			}			
			processFlowExecutor.stopRunning();
			//All workpieces are done, so also stop this thread
			if (processFlow.getFinishedAmount() == processFlow.getTotalAmount()) {
				finished = true;
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
	synchronized protected boolean isFreePlaceInMachine() {
		int nbInMachine = 0;
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.PROCESSING_IN_MACHINE)) {
				nbInMachine++;
			}
		}
		if (nbInMachine >= getNbConcurrentInMachine()) {
			return false;
		}
		return true;
	}

	@Override
	protected ExecutionThreadStatus getFirstPutState() {
		return ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE;
	}

}
