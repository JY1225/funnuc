package eu.robojob.millassist.process.execution.fixed;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.execution.fixed.ProcessFlowExecutionThread.ExecutionThreadStatus;

public class AutomateControllingThreadReversal extends AutomateControllingThread {

	public AutomateControllingThreadReversal(ProcessFlow processFlow,int nbProcesses) {
		super(processFlow, nbProcesses);
	}
	
	@Override
	synchronized void notifyWaitingBeforePickFromStacker(final ProcessFlowExecutionThread processFlowExecutor) {
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER);
		//Test for dualLoad whether we can continue with pick from stacker - if first processExecutor already went through the whole cycle	
		boolean processingBeforeRev = false;
		boolean processingAfterRev = false;
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.PROCESSING_BEFORE_REVERSAL)) {
				processingBeforeRev = true;
			} else if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.PROCESSING_IN_MACHINE)) {
				processingAfterRev = true;
			}
		}
		if (processingBeforeRev && processingAfterRev) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_WORKPIECES_STACKER);
			processFlowExecutor.continueExecution();
			return;
		}
		if (getTotalNbWPInFlow() > getMaxNbInFlow()) {
			return;
		}
		int reversalCount = 0;
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			//Only allow other process to continue if we are at the second CNC processing cycle
			if (isInReversalCycle(processExecutor))
				reversalCount++;
		}
		if (reversalCount >= getNbConcurrentProcessesInMachine()) {
			return;
		}
		super.notifyWaitingBeforePickFromStacker(processFlowExecutor);
	}
	
	@Override
	synchronized void notifyWaitingBeforePutInMachine(ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.needsReversal()) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_MACHINE_BEFORE_REVERSAL);
			super.notifyWaitingBeforePutInMachine(processFlowExecutor);
		} else {
			//Because a piece was just removed from the machine, we know the space is still there, so we can continue.
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE);
			processFlowExecutor.continueExecution();
		}
	}
	
	@Override
	synchronized void notifyPutInMachineFinished(final ProcessFlowExecutionThread processFlowExecutor, final boolean moreToPut,
			final int nbActiveClampings, final int nbFilled) {
		if (!moreToPut) {
			logger.info("Put in machine finished.");
			if (processFlowExecutor.needsReversal()) {
				processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_BEFORE_REVERSAL);
				// Test whether all are filled by this process before we can start processing. If this is false, it could be that this side is still
				// occupied by (finished) workpieces from another executor
				boolean isContinuing = false;
				if(nbActiveClampings == nbFilled) {
					processFlowExecutor.startProcessing();
					isContinuing = true;
				}
				for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
					if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) { 
						processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
						processExecutor.continueExecution();
						return;
					}
				}
				if (getTotalNbWPInFlow() < (getNbConcurrentProcessesInMachine()*processFlow.getNbClampingsChosen())) {
					startNewProcess();
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
			} else {
				processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_IN_MACHINE);
				processFlowExecutor.continueExecution();
				for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
					if (isInReversalCycle(processExecutor))
						return;
				}
				super.notifyPutInMachineFinished(processFlowExecutor, moreToPut, nbActiveClampings, nbFilled);
			}
		}
	}
	
	@Override
	synchronized void notifyWaitingBeforePickFromMachine(ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.needsReversal()) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_PICK_MACHINE_BEFORE_REVERSAL);
			if (isRobotFree()) {
				processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.REVERSING_WITH_ROBOT);
				processFlowExecutor.continueExecution();
			}
		} else {
			super.notifyWaitingBeforePickFromMachine(processFlowExecutor);
		}
	}
	
	@Override
	protected ExecutionThreadStatus getFirstPutState() {
		return ExecutionThreadStatus.WAITING_BEFORE_PUT_MACHINE_BEFORE_REVERSAL;
	}
	
	private static boolean isInReversalCycle(ProcessFlowExecutionThread processExecutor) {
		if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.PROCESSING_BEFORE_REVERSAL)
				|| processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_FOR_PICK_MACHINE_BEFORE_REVERSAL)
				|| processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE)
				|| processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.REVERSING_WITH_ROBOT)) {
			return true;
		}
		return false;
	}
	
	@Override
	protected synchronized int getNbProcessesInMachine() {
		int nbInMachine = 0;
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.PROCESSING_IN_MACHINE) || isInReversalCycle(processExecutor)) {
				nbInMachine++;
			}
		}
		return nbInMachine;
	}
	
	@Override
	protected boolean isRobotFree() {
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.REVERSING_WITH_ROBOT)
					|| processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WORKING_WITH_ROBOT)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "AutomateControllingThreadReversal - processflow [" + processFlow + "]";
	}
}
