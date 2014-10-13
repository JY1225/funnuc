package eu.robojob.millassist.process.execution.fixed;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.execution.fixed.ProcessFlowExecutionThread.ExecutionThreadStatus;

//TODO - herschrijven (niet zeker of deze klasse wel nodig is)
public class DualLoadAutomateControllingThreadReversal extends AutomateControllingThread {

	public DualLoadAutomateControllingThreadReversal(ProcessFlow processFlow,int nbProcesses) {
		super(processFlow, nbProcesses);
	}
	
	@Override
	synchronized void notifyWaitingBeforePickFromStacker(final ProcessFlowExecutionThread processFlowExecutor) {
		processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.WAITING_BEFORE_PICK_FROM_STACKER);
		for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
			//Only allow other process to continue if we are at the second CNC processing cycle
			System.out.println("NB IN FLOW = " + getNbInFlow());
			if (isInReversalCycle(processExecutor) && (getNbInFlow() + 1 < getNbConcurrentInMachine()))
				return;
		}
		super.notifyWaitingBeforePickFromStacker(processFlowExecutor);
		
		
		//Hier mogen we enkel starten als er 2 processen aan het processen zijn in de machine of in het begin
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
		logger.info("Put in machine finished.");
		if (processFlowExecutor.needsReversal()) {
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_BEFORE_REVERSAL);
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (processExecutor.getExecutionStatus().equals(ExecutionThreadStatus.WAITING_AFTER_PICK_FROM_MACHINE)) { 
					processExecutor.setExecutionStatus(ExecutionThreadStatus.WORKING_WITH_ROBOT);
					processExecutor.continueExecution();
					return;
				}
			}
			System.out.println("NB IN FLOW = " + getNbInFlow());
			if (getNbInFlow() < getNbConcurrentInMachine()) {
				startNewProcess();
			}
			try {
				processFlow.getRobots().iterator().next().moveToHome();
			} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
			}
		} else {
			// TODO - als we een put doen mogen we niet direct verder gaan, want er kan nog iemand anders in de 
			// machine zitten
			processFlowExecutor.setExecutionStatus(ExecutionThreadStatus.PROCESSING_IN_MACHINE);
			for (ProcessFlowExecutionThread processExecutor: processFlowExecutors) {
				if (isInReversalCycle(processExecutor)) {
					return;
				}
			}
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
