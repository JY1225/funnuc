package eu.robojob.millassist.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.ProcessFlow.Type;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.threading.ThreadManager;

//TODO refactor, same logic for all 3 executors currently duplicated
public class DualLoadReversalAutomateFixedControllingThread extends DualLoadAutomateFixedControllingThread {
	
	private static final Logger logger = LogManager.getLogger(DualLoadReversalAutomateFixedControllingThread.class.getName());
	
	public DualLoadReversalAutomateFixedControllingThread(final ProcessFlow processFlow) {
		super(processFlow);
	}
	
	@Override
	public void run() {
		try {
			processFlow.setMode(ProcessFlow.Mode.AUTO);
			running = true;
			checkIfConcurrentExecutionIsPossible();
			if ((processFlow.getCurrentIndex(WORKPIECE_0_ID) == -1) || (processFlow.getCurrentIndex(WORKPIECE_0_ID) == 0)) {
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
				processFlow.setCurrentIndex(WORKPIECE_2_ID, 0);
			}
			if (processFlow.getCurrentIndex(WORKPIECE_1_ID) == -1) {
				processFlow.setCurrentIndex(WORKPIECE_1_ID, 0);
				processFlow.setCurrentIndex(WORKPIECE_2_ID, 0);
			}
			checkStatus();
			processFlowExecutor1 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_0_ID);
			processFlowExecutor1Future = ThreadManager.submit(processFlowExecutor1);
			// Normally here we have a check for start after optimal teaching, but since no optimal teaching is allowed in case of a reversalUnit, the check is not needed
			// wait until all processes have finished
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
			processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_2_ID));
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
	public synchronized void notifyWaitingBeforePickFromStacker(final ProcessFlowExecutionThread processFlowExecutor) {
		// continue if no other executor is working with robot or waiting for work piece at the stacker
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			setStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER ,WORKPIECE_0_ID);
			if (notifyWaitingBeforePickFromStackerCondition(statusExecutor2, statusExecutor3)) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			setStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER ,WORKPIECE_1_ID);
			if (notifyWaitingBeforePickFromStackerCondition(statusExecutor1, statusExecutor3)) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor3)) {
			setStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER ,WORKPIECE_2_ID);
			if (notifyWaitingBeforePickFromStackerCondition(statusExecutor1, statusExecutor2)) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			}
		}
	}
	
	private static boolean notifyWaitingBeforePickFromStackerCondition(final ExecutionThreadStatus executionThreadStatus1, 
			final ExecutionThreadStatus executionThreadStatus2) {
		if (executionThreadStatus1.equals(ExecutionThreadStatus.WORKING_WITH_ROBOT) || executionThreadStatus2.equals(ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
			return false;
		}
		if (executionThreadStatus1.equals(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER) || executionThreadStatus2.equals(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER)) {
			return false;
		}
		// Two processes are in reversalCycle
		if (executionThreadStatus1.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL) && executionThreadStatus2.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)) {
			return false;
		}
		if (executionThreadStatus1.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL) && executionThreadStatus2.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)) {
			return false;
		}
		if (executionThreadStatus1.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) && executionThreadStatus2.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)) {
			return false;
		}
		return true;
	}
	
	//TODO - andere status toevoegen, want het wordt te ingewikkeld - extra checks toevoegen 
	@Override
	public synchronized void notifyWaitingBeforePutInMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		// if one of other executor is waiting for pick: continue this first
		// if one (or none) of other executors is working without robot, continue
		// otherwise go to home
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			setStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE ,WORKPIECE_0_ID);
			if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE  && !processFlowExecutor3.needsReversal()) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE && !processFlowExecutor2.needsReversal()) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else if (isFreeSpaceInMachine(statusExecutor2, processFlowExecutor2.needsReversal(), statusExecutor3, processFlowExecutor3.needsReversal())) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			setStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE ,WORKPIECE_1_ID);
			if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE  && !processFlowExecutor1.needsReversal()) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE && !processFlowExecutor3.needsReversal()) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else if (isFreeSpaceInMachine(statusExecutor3, processFlowExecutor3.needsReversal(), statusExecutor1, processFlowExecutor1.needsReversal())) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		}  else if (processFlowExecutor.equals(processFlowExecutor3)) {
			setStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE ,WORKPIECE_2_ID);
			if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE  && !processFlowExecutor2.needsReversal()) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE && !processFlowExecutor1.needsReversal()) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else if (isFreeSpaceInMachine(statusExecutor1, processFlowExecutor1.needsReversal(), statusExecutor2, processFlowExecutor2.needsReversal())) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} 
	}
	
	//FIXME dit is te ingewikkeld - eigenlijk is dit "zit tussen state en state" - state nummer toevoegen? - probleem want WORKING_WITH_ROBOT komt meermaals voor - tenzij we de stepNummer gebruiken
	//==> processFlowExectuion van multiThread 
	private static boolean isFreeSpaceInMachine(final ExecutionThreadStatus executionThreadStatus1, final boolean needsReversalThread1,
			final ExecutionThreadStatus executionThreadStatus2, final boolean needsReversalThread2) {
		//Als eerste en tweede proces beiden in reversal cyclus zitten dan is het niet goed
		if (executionThreadStatus1.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) 
				|| executionThreadStatus1.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)
				|| (executionThreadStatus1.equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) && needsReversalThread1)
				|| executionThreadStatus1.equals(ExecutionThreadStatus.FINISHED)
				|| executionThreadStatus1.equals(ExecutionThreadStatus.IDLE)) {
			if (executionThreadStatus2.equals(ExecutionThreadStatus.IDLE) || executionThreadStatus2.equals(ExecutionThreadStatus.FINISHED)) {
				return true;
			} else if (!(executionThreadStatus2.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) 
					|| executionThreadStatus2.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)
					|| executionThreadStatus2.equals(ExecutionThreadStatus.WORKING_WITH_ROBOT))) {
				return true;
			}
		}
		if (executionThreadStatus2.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) 
				|| executionThreadStatus2.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)
				|| (executionThreadStatus2.equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) && needsReversalThread1)
				|| executionThreadStatus2.equals(ExecutionThreadStatus.FINISHED)
				|| executionThreadStatus2.equals(ExecutionThreadStatus.IDLE)) {
			if (executionThreadStatus1.equals(ExecutionThreadStatus.IDLE) || executionThreadStatus1.equals(ExecutionThreadStatus.FINISHED)) {
				return true;
			} else if (!(executionThreadStatus1.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) 
					|| executionThreadStatus1.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)
					|| executionThreadStatus1.equals(ExecutionThreadStatus.WORKING_WITH_ROBOT))) {
				return true;
			}
		}
		return false;
	}
	
	//TODO opsplitsen!
	@Override
	public synchronized void notifyPutInMachineFinished(final ProcessFlowExecutionThread processFlowExecutor, final boolean needsReversal) {
		logger.info("Put in machine finished.");
		if (needsReversal) {
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_REVERSAL ,WORKPIECE_0_ID);
			} else if (processFlowExecutor.equals(processFlowExecutor2)) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_REVERSAL ,WORKPIECE_1_ID);
			} else {
				setStatus(ExecutionThreadStatus.WAITING_FOR_REVERSAL ,WORKPIECE_2_ID);
			}
			putBeforeReversalFinished(processFlowExecutor);
		} else {
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				setStatus(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT, WORKPIECE_0_ID);
			} else if (processFlowExecutor.equals(processFlowExecutor2)) {
				setStatus(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT, WORKPIECE_1_ID);
			} else {
				setStatus(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT, WORKPIECE_2_ID);
			}
			putAfterReversalFinished(processFlowExecutor);
		}
	}
	
	private void putBeforeReversalFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			if (statusExecutor2.equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor3.equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else {
				startNewProcess(processFlowExecutor);
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			if (statusExecutor1.equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor3.equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else {
				startNewProcess(processFlowExecutor);
			}
		} else {
			if (statusExecutor1.equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor2.equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} //no need to start new process - check whether we can do something else
		}
	}
	
	private boolean startNewProcess(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			if (isNewProcessAllowedToStart(statusExecutor1, statusExecutor2, statusExecutor3)) {
				if (!processFlowExecutor2.isRunning()) {	
					setStatus(ExecutionThreadStatus.IDLE ,WORKPIECE_1_ID);
					processFlowExecutor2Future = ThreadManager.submit(processFlowExecutor2);
					return true;
				} else if ((!processFlowExecutor2.isRunning()) && (processFlow.getTotalAmount() - processFlow.getFinishedAmount() <= 1)) {
					logger.info("No second process, move to home");
					try {
						processFlow.getRobots().iterator().next().moveToHome();
						return true;
					} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
						e.printStackTrace();
						logger.error(e);
					}
				}
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			if (isNewProcessAllowedToStart(statusExecutor2, statusExecutor1, statusExecutor3)) {
				if (!processFlowExecutor3.isRunning() && (processFlow.getTotalAmount() - processFlow.getFinishedAmount() > 2) && isConcurrentExecutionPossible) {
					setStatus(ExecutionThreadStatus.IDLE ,WORKPIECE_2_ID);
					processFlowExecutor3Future = ThreadManager.submit(processFlowExecutor3);
					return true;
				} else if (!processFlowExecutor3.isRunning()) {
					logger.info("No third process, move to home");
					try {
						processFlow.getRobots().iterator().next().moveToHome();
						return true;
					} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
						e.printStackTrace();
						logger.error(e);
					}
				} 
			} 
		} 
		return false;
	}
	
	//FIXME - hier ook check of in reversalCycle
	private static boolean isNewProcessAllowedToStart(final ExecutionThreadStatus activeExecutionThreadStatus, final ExecutionThreadStatus executionThreadStatus1, 
			final ExecutionThreadStatus executionThreadStatus2) {
		if (activeExecutionThreadStatus.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)) {
			// At this point we have 2 processes waiting_for_reversal
			if (executionThreadStatus1.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL) || executionThreadStatus2.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)) {
				return false;
			}
			// At this point we have 1 process WAITING_FOR_REVERSAL and 1 WORKING_WITHOUT_ROBOT (after reversal)
			if (executionThreadStatus1.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT) || executionThreadStatus2.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)) {
				return false;
			}
		} else if (activeExecutionThreadStatus.equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)) {
			if (executionThreadStatus1.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL) || executionThreadStatus2.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL)) {
				return false;
			}
		}
		return true;
	}
	
	private void putAfterReversalFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		if (!startNewProcess(processFlowExecutor)) {
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
					setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_1_ID);
					processFlowExecutor2.continueExecution();
				}
			}
			else if (processFlowExecutor.equals(processFlowExecutor2)) {
				if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
					setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_2_ID);
					processFlowExecutor3.continueExecution();
				}
			}  else {
				if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
					setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_0_ID);
					processFlowExecutor1.continueExecution();
				}
			}
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
					processFlowExecutor2.continueExecution();
				}
			}
		}
	}
	
	@Override
	public synchronized void notifyWaitingAfterPickFromMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		// if one of the other executors is waiting for put in machine continue that one, otherwise continue 
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			setStatus(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE ,WORKPIECE_0_ID);
			if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			setStatus(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE ,WORKPIECE_1_ID);
			if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			}
		} if (processFlowExecutor.equals(processFlowExecutor3)) {
			setStatus(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE ,WORKPIECE_2_ID);
			if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			}
		}
	}
	
	@Override
	public synchronized void notifyProcessFlowFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		// update main process flow
		if (isConcurrentExecutionPossible) {
			if (processFlowExecutor.equals(processFlowExecutor1)) {
				mainProcessFlowId = WORKPIECE_1_ID;
			} else if (processFlowExecutor.equals(processFlowExecutor2)) {
				if (processFlowExecutor3.isRunning()) {
					mainProcessFlowId = WORKPIECE_2_ID;
				} else {
					mainProcessFlowId = WORKPIECE_0_ID;
				}
			} else if (processFlowExecutor.equals(processFlowExecutor3)) {
				mainProcessFlowId = WORKPIECE_0_ID;
			}
		}
		// check if this was the last run for this executor
		if ((processFlow.getType() == Type.FIXED_AMOUNT) && 
				((processFlow.getFinishedAmount() == processFlow.getTotalAmount()) || 
						(processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 1) || 
						((processFlow.getFinishedAmount() == processFlow.getTotalAmount() - 2) 
								&& isConcurrentExecutionPossible))) {
			// this was the last work piece for this executor, so let's stop it
			// continue other executors that are waiting for pick from machine
			if (processFlowExecutor == processFlowExecutor1) {
				setStatus(ExecutionThreadStatus.FINISHED ,WORKPIECE_0_ID);
				if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
					processFlowExecutor2.continueExecution();
				} else if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
					processFlowExecutor3.continueExecution();
				}
			} else if (processFlowExecutor == processFlowExecutor2){
				setStatus(ExecutionThreadStatus.FINISHED ,WORKPIECE_1_ID);
				if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
					processFlowExecutor3.continueExecution();
				} else if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
					processFlowExecutor1.continueExecution();
				}
			} else if (processFlowExecutor == processFlowExecutor3){
				setStatus(ExecutionThreadStatus.FINISHED ,WORKPIECE_2_ID);
				if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
					processFlowExecutor1.continueExecution();
				} else if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
					processFlowExecutor2.continueExecution();
				}
			}
			processFlowExecutor.stopRunning();
			if (processFlow.getFinishedAmount() == processFlow.getTotalAmount()) {
				// finished all pieces so also stop this thread
				finished = true;
				synchronized(finishedSyncObject) {
					finishedSyncObject.notify();
				}
			}
		// when not last work piece: continue, if other process is waiting for pick from stacker, continue this
		} else if (processFlowExecutor == processFlowExecutor1) {
			// continue
			setStatus(ExecutionThreadStatus.IDLE ,WORKPIECE_0_ID);
			processFlowExecutor1.continueExecution();
			if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor2 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else if (statusExecutor3 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} 
		}  else if (processFlowExecutor == processFlowExecutor2) {
			// continue
			setStatus(ExecutionThreadStatus.IDLE ,WORKPIECE_1_ID);
			if (!(statusExecutor1.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL) &&
					statusExecutor3.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL))) {
				processFlowExecutor2.continueExecution();
			}
			if (statusExecutor3 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else if (statusExecutor3 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_2_ID);
				processFlowExecutor3.continueExecution();
			} else if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor1 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} 
		} else if (processFlowExecutor == processFlowExecutor3) {
			// continue
			setStatus(ExecutionThreadStatus.IDLE ,WORKPIECE_2_ID);
			if (!(statusExecutor1.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL) &&
					statusExecutor2.equals(ExecutionThreadStatus.WAITING_FOR_REVERSAL))) {
				processFlowExecutor3.continueExecution();
			}
			if (statusExecutor1 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor1 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_0_ID);
				processFlowExecutor1.continueExecution();
			} else if (statusExecutor2 == ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER ,WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} else if (statusExecutor2 == ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				setStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT, WORKPIECE_1_ID);
				processFlowExecutor2.continueExecution();
			} 
		} 
	}
	
}
