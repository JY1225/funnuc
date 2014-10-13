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
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			//Only allow other process to continue if we are at the second CNC processing cycle
			System.out.println("NB IN FLOW = " + getNbInFlow());
			if (isInReversalCycle(processExecutor) && (getNbInFlow() < (getNbConcurrentInMachine()+1)))
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
	synchronized void notifyPutInMachineFinished(final ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.needsReversal()) {
			logger.info("Put in machine finished.");
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_BEFORE_REVERSAL);
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) { 
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processExecutor.continueExecution();
					return;
				}
			}
			//CODE for dualLoad
			System.out.println("NB IN FLOW = " + getNbInFlow());
			if (getNbInFlow() < getNbConcurrentInMachine()) {
				startNewProcess();
			}
			//END
			try {
				processFlow.getRobots().iterator().next().moveToHome();
			} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
			}
		} else {
			//CODE FOR DUAL LOAD
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (isInReversalCycle(processExecutor)) {
					return;
				}
			}
			//END DUAL LOAD
			super.notifyPutInMachineFinished(processFlowExecutor);
		}
	}
	
	@Override
	synchronized void notifyWaitingBeforePickFromMachine(ProcessFlowExecutionThread processFlowExecutor) {
		if (processFlowExecutor.needsReversal()) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_FOR_PICK_MACHINE_BEFORE_REVERSAL);
			if (isRobotFree())
				processFlowExecutor.continueExecution();
		} else {
			super.notifyWaitingBeforePickFromMachine(processFlowExecutor);
		}
	}
	
	@Override
	synchronized protected boolean isFreePlaceInMachine() {
		return (getNbInMachine() < getNbConcurrentInMachine());
	}
	
	@Override
	protected ExecutionThreadStatus getFirstPutState() {
		return ExecutionThreadStatus.WAITING_BEFORE_PUT_MACHINE_BEFORE_REVERSAL;
	}
	
	private static boolean isInReversalCycle(ProcessFlowExecutionThread processExecutor) {
		if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.PROCESSING_BEFORE_REVERSAL)
				|| processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_FOR_PICK_MACHINE_BEFORE_REVERSAL)
				|| processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_BEFORE_PUT_IN_MACHINE)) {
			return true;
		}
		return false;
	}
	
	private synchronized int getNbInMachine() {
		int nbInMachine = 0;
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.PROCESSING_IN_MACHINE) || isInReversalCycle(processExecutor)) {
				nbInMachine++;
			}
		}
		return nbInMachine;
	}
	
	@Override
	public String toString() {
		return "AutomateControllingThreadReversal - processflow [" + processFlow + "]";
	}

}
