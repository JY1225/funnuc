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
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_FOR_PICK_MACHINE_BEFORE_REVERSAL)) {
					processExecutor.setExecutionStatus(ExecutionThreadStatus.REVERSING_WITH_ROBOT);
					processExecutor.continueExecution();
				}
			}
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
				// At this point we know that there are no more pieces to put by this executor, so we check whether the max is filled by this exector.
				// It could very well be that there are still pieces from another executor in the clampings, that are waiting before pick. This is
				// the case when there are multiple clampings to use and we are at the final piece, where we only use 1 clamp. In that case
				// there is no more to put (only 1 put action to do - because final piece), but there are 2 pick actions to perform.
				if(nbActiveClampings == nbFilled) {
					processFlowExecutor.startProcessing();
					isContinuing = true;
				} 
				for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
					// One of the executors is currently waiting for its action to perform after the pick from the machine
					// (e.g. put to stacker) after that a new piece has been put in the machine and there are no more pieces
					// for the given executor to put
					if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) { 
						processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
						processExecutor.continueExecution();
						return;
					}
				}
				// There can be more pieces in the flow, so start a new process to make use of the max capacity
				// (e.g. already pick a piece to put in the machine whenever the processing of another executor is finished)
				if (getTotalNbWPInFlow() < (getNbConcurrentProcessesInMachine()*processFlow.getNbClampingsChosen())) {
					startNewProcess();
				}
				// There is no executor found that matches the above requirements, so start the processing in the given executor
				if (!isContinuing) {
					processFlowExecutor.startProcessing();
				}
				// Machine is processing, so robot is idle - sent him to home
				try {
					processFlow.getRobots().iterator().next().moveToHome();
				} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
					e.printStackTrace();
					logger.error(e);
				}
			} else {
				// At this point, processing was done and all the pieces have been reversed and put back. In fact the processing 
				// was stopped for a while to make it possible to reverse the pieces. So, we can immediately continue with the 
				// processing
				processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_IN_MACHINE);
				processFlowExecutor.startProcessing();
				// Look for another process - the given one is currently processing, so the robot is free to use.
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
