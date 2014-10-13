package eu.robojob.millassist.process.execution.fixed;

import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.ProcessFlow.Type;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.fixed.ProcessFlowExecutionThread.ExecutionThreadStatus;
import eu.robojob.millassist.threading.ThreadManager;

//TODO refactor, same logic for all 3 executors currently duplicated
public class DualLoadAutomateFixedControllingThread extends AutomateFixedControllingThread {

	protected ProcessFlowExecutionThread processFlowExecutor3;
	protected Future<?> processFlowExecutor3Future;
	
	private static final Logger logger = LogManager.getLogger(DualLoadAutomateFixedControllingThread.class.getName());
	
	protected static final int WORKPIECE_2_ID = 2;
	
	public DualLoadAutomateFixedControllingThread(final ProcessFlow processFlow) {
		super(processFlow);
		this.processFlowExecutor3 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_2_ID);
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		processFlow.setCurrentIndex(WORKPIECE_2_ID, -1);
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
			boolean startSecond = false;
			if (processFlow.getCurrentIndex(WORKPIECE_0_ID) > 0) {
				// process has already passed some steps, check if current step is processing in machine
				// than second process can start!
				AbstractProcessStep step = processFlow.getStep(processFlow.getCurrentIndex(WORKPIECE_0_ID) - 1);
				if (step instanceof PutStep) {
					if (((PutStep) step).getDevice() instanceof AbstractCNCMachine) {
						processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT);
						if (processFlow.getCurrentIndex(WORKPIECE_1_ID) == 0) {
							if (isConcurrentExecutionPossible()) {
								startSecond = true;
							}
						}
					}
				} 
			}
			processFlowExecutor1Future = ThreadManager.submit(processFlowExecutor1);
			if (startSecond) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.IDLE);
				processFlowExecutor2 = new ProcessFlowExecutionThread(this, processFlow, WORKPIECE_1_ID);
				firstPiece = false;
				processFlowExecutor2Future = ThreadManager.submit(processFlowExecutor2);
			}
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
	public synchronized void notifyWaitingOnIntervention() {
		super.notifyWaitingOnIntervention();
		if ((processFlowExecutor3 != null) && (processFlowExecutor3.isRunning())) {
			processFlowExecutor3.waitForIntervention();
		}
	}
	
	@Override
	public void interventionFinished() {
		if ((processFlowExecutor3 != null) && (processFlowExecutor3.isRunning())) {
			processFlowExecutor3.interventionFinished();
		}
		super.interventionFinished();
	}
	
	@Override
	public synchronized void notifyWaitingBeforePickFromStacker(final ProcessFlowExecutionThread processFlowExecutor) {
		// continue if no other executor is working with robot or waiting for work piece at the stacker
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER);
			if ((processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER) &&
					(processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER)) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor1.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER);
			if ((processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER) &&
					(processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER)) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor2.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor3)) {
			processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER);
			if ((processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER) &&
					(processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER)) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor3.continueExecution();
			}
		}
	}
	
	@Override
	public synchronized void notifyWorkPiecesPresent(final ProcessFlowExecutionThread processFlowExecutor) {
		// work pieces present, can continue if no other executor is working with robot
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER);
			if ((processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER);
			if ((processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor3)) {
			processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER);
			if ((processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			}
		}
	}
	
	@Override
	public synchronized void notifyWaitingBeforePutInMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		// if one of other executor is waiting for pick: continue this first
		// if one (or none) of other executors is working without robot, continue
		// otherwise go to home
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE);
			if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if ((((processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)  || (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) 
					&& (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) ||
					(((processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)  || (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) 
							&& (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.IDLE))) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
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
			processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE);
			if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else if ((((processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)  || (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) 
					&& (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) ||
					(((processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)  || (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) 
							&& (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.IDLE))) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
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
			processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE);
			if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if ((((processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)  || (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) 
					&& (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) ||
					(((processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)  || (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.IDLE)) 
							&& (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.IDLE))) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
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
	
	//TODO - duplicate code...
	public synchronized void notifyWaitingBeforePutInMachineBeforeReversal(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE_BEFORE_REVERSAL);
			if (!processFlowExecutor2.isInReversalCycle()
					&& !processFlowExecutor2.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)
					&& !processFlowExecutor2.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor2.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE)) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor2.isInReversalCycle() || processFlowExecutor2.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} else {
			processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE_BEFORE_REVERSAL);
			if (!processFlowExecutor1.isInReversalCycle()
					&& !processFlowExecutor1.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)
					&& !processFlowExecutor1.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE)) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor1.isInReversalCycle() || processFlowExecutor1.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT)) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		}
	}
	
	public synchronized void notifyPutInMachineBeforeReversalFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		waitingBeforeReversal(processFlowExecutor);
	}
	
	@Override
	public synchronized void notifyPutInMachineFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		logger.info("Put in machine finished.");
		// if other executor is running and waiting after pick from machine, continue that one
		// else if next executor is waiting for pick from stacker, continue this one, if it hasn't started yet, start (if possible)
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT);
			// no continue needed
			if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (!processFlowExecutor2.isRunning() && (processFlow.getTotalAmount() - processFlow.getFinishedAmount() > 1)) {
				// always start second process if not running and more than one wp needed after current
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.IDLE);
				processFlowExecutor2Future = ThreadManager.submit(processFlowExecutor2);
			} else if ((!processFlowExecutor2.isRunning()) && (processFlow.getTotalAmount() - processFlow.getFinishedAmount() <= 1)) {
				logger.info("No second process, move to home");
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			} else if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor2.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT);
			// no continue needed
			if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else if (!processFlowExecutor3.isRunning() && (processFlow.getTotalAmount() - processFlow.getFinishedAmount() > 2) && isConcurrentExecutionPossible) {
				// always start second process if not running and more than one wp needed after current
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.IDLE);
				processFlowExecutor3Future = ThreadManager.submit(processFlowExecutor3);
			} else if (!processFlowExecutor3.isRunning()) {
				logger.info("No third process, move to home");
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor3.continueExecution();
			}
		}  else {
			processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITHOUT_ROBOT);
			// no continue needed
			if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			}
		}
	}
	
	@Override
	public synchronized void notifyNoWorkPiecesPresent(final ProcessFlowExecutionThread processFlowExecutor) {
		// if previous executor is waiting for pick from machine continue this one, else go to home
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if ((processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else if ((processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} else if (processFlowExecutor.equals(processFlowExecutor3)) {
			if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if ((processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} 
	}
	
	@Override
	public synchronized void notifyWaitingBeforePickFromMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		// if other executors are not working with robot, continue
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE);
			if ((processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} 
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE);
			if ((processFlowExecutor3.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} 
		} else if (processFlowExecutor.equals(processFlowExecutor3)) {
			processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE);
			if ((processFlowExecutor1.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT) && (processFlowExecutor2.getExecutionStatus() !=ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} 
		}
	}
	
	@Override
	public synchronized void notifyWaitingAfterPickFromMachine(final ProcessFlowExecutionThread processFlowExecutor) {
		// if one of the other executors is waiting for put in machine continue that one, otherwise continue 
		if (processFlowExecutor.equals(processFlowExecutor1)) {
			processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE);
			if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			}
		} else if (processFlowExecutor.equals(processFlowExecutor2)) {
			processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE);
			if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			}
		} if (processFlowExecutor.equals(processFlowExecutor3)) {
			processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE);
			if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
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
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.FINISHED);
				if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processFlowExecutor2.continueExecution();
				} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processFlowExecutor3.continueExecution();
				}
			} else if (processFlowExecutor == processFlowExecutor2){
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.FINISHED);
				if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processFlowExecutor3.continueExecution();
				} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processFlowExecutor1.continueExecution();
				}
			} else if (processFlowExecutor == processFlowExecutor3){
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.FINISHED);
				if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processFlowExecutor1.continueExecution();
				} else if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_MACHINE) {
					processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
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
			processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.IDLE);
			processFlowExecutor1.continueExecution();
			if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor3.continueExecution();
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} 
		}  else if (processFlowExecutor == processFlowExecutor2) {
			// continue
			processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.IDLE);
			processFlowExecutor2.continueExecution();
			if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor3.continueExecution();
			} else if (processFlowExecutor3.getExecutionStatus() ==ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor3.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} 
		} else if (processFlowExecutor == processFlowExecutor3) {
			// continue
			processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.IDLE);
			processFlowExecutor3.continueExecution();
			if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor1.getExecutionStatus() ==ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor1.continueExecution();
			} else if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
				processFlowExecutor2.continueExecution();
			} else if (processFlowExecutor2.getExecutionStatus() ==ExecutionThreadStatus.WAITING_FOR_PICK_FROM_STACKER) {
				processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
				processFlowExecutor2.continueExecution();
			} 
		} 
	}
	
	@Override
	public void stopRunning() {
		logger.info("Called stop running");
		running = false;
		for (AbstractRobot robot : processFlow.getRobots()) {
			robot.interruptCurrentAction();
		}
		for (AbstractDevice device :processFlow.getDevices()) {
			device.interruptCurrentAction();
		}
		synchronized(finishedSyncObject) {
			finishedSyncObject.notifyAll();
		}
		if (processFlowExecutor1Future != null) {
			processFlowExecutor1Future.cancel(true);
		}
		if (processFlowExecutor2Future != null) {
			processFlowExecutor2Future.cancel(true);
		}
		if (processFlowExecutor3Future != null) {
			processFlowExecutor3Future.cancel(true);
		}
		processFlowExecutor1.setExecutionStatus(ExecutionThreadStatus.IDLE);
		processFlowExecutor2.setExecutionStatus(ExecutionThreadStatus.IDLE);
		processFlowExecutor3.setExecutionStatus(ExecutionThreadStatus.IDLE);
		// just to be sure: 
		for (AbstractRobot robot : processFlow.getRobots()) {
			robot.setCurrentActionSettings(null);
		}
		stopExecution();
		reset();
	}
	
	@Override
	public void stopExecution() {
		super.stopExecution();
		processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.INACTIVE, WORKPIECE_2_ID));
	}
}
