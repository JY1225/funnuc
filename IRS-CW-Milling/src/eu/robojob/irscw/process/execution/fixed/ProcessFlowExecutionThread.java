package eu.robojob.irscw.process.execution.fixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.PutStep;

public class ProcessFlowExecutionThread extends Thread {

	private ProcessFlow processFlow;
	private int workpieceId;
	private AutomateFixedControllingThread controllingThread;
	private Object syncObject;
	private boolean running;
	private boolean canContinue;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowExecutionThread.class.getName());
	
	public ProcessFlowExecutionThread(final AutomateFixedControllingThread controllingThread,
			final ProcessFlow processFlow, final int workpieceId) {
		this.controllingThread = controllingThread;
		this.processFlow = processFlow;
		this.workpieceId = workpieceId;
		this.syncObject = new Object();
		this.canContinue = false;
	}
	
	@Override 
	public void run() {
		this.running = true;
		try {
			while (running) {
				
				AbstractProcessStep currentStep = processFlow.getStep(processFlow.getCurrentIndex(workpieceId));
				if ((currentStep instanceof PutStep) && (((PutStep) currentStep).getDevice() instanceof CNCMillingMachine)) {
					canContinue = false;
					controllingThread.notifyWaitingForPutInMachine(this);
					if (!canContinue) {
						synchronized(syncObject) {
							logger.info("Waiting before put in machine");
							syncObject.wait();
							logger.info("Can continue");
						}
					}
					currentStep.executeStep(workpieceId);
					if (currentStep instanceof AbstractTransportStep) {
						((AbstractTransportStep) currentStep).finalizeStep();
					} 
					canContinue = false;
					controllingThread.notifyPutInMachineFinished(this);
					if (!canContinue) {
						synchronized(syncObject) {
							logger.info("Waiting after put in machine");
							syncObject.wait();
							logger.info("Can continue");
						}
					}
				} else if ((currentStep instanceof PickStep) && (((PickStep) currentStep).getDevice() instanceof CNCMillingMachine)) {
					canContinue = false;
					controllingThread.notifyWaitingForPickFromMachine(this);
					if (!canContinue) {
						synchronized(syncObject) {
							logger.info("Waiting before pick from machine");
							syncObject.wait();
							logger.info("Can continue");
						}
					}
					currentStep.executeStep(workpieceId);
					if (currentStep instanceof AbstractTransportStep) {
						((AbstractTransportStep) currentStep).finalizeStep();
					} 
					canContinue = false;
					controllingThread.notifyPickFromMachineFinished(this);
					if (!canContinue) {
						synchronized(syncObject) {
							logger.info("Waiting after pick from machine");
							syncObject.wait();
							logger.info("Can continue");
						}
					}
				} else {
					currentStep.executeStep(workpieceId);
					if (currentStep instanceof AbstractTransportStep) {
						((AbstractTransportStep) currentStep).finalizeStep();
					} 
				}
				
				if (processFlow.getCurrentIndex(workpieceId) == processFlow.getProcessSteps().size() - 1) {
					processFlow.setCurrentIndex(workpieceId, 0);
					processFlow.incrementFinishedAmount();
					controllingThread.notifyProcessFlowFinished(this);
				} else {
					processFlow.setCurrentIndex(workpieceId, processFlow.getCurrentIndex(workpieceId) + 1);
				}
			}
			logger.info(toString() + " ended...");
		} catch (InterruptedException e) {
			if (controllingThread.isRunning()) {
				controllingThread.stopExecution();
			} else {
				controllingThread.notifyException(e);
				controllingThread.stopExecution();
			}
		} catch (Exception e) {
			controllingThread.notifyException(e);
			controllingThread.stopExecution();
		}
	}
	
	public void stopRunning() {
		this.running = false;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void continueExecution() {
		this.canContinue = true;
		synchronized(syncObject) {
			syncObject.notify();
		}
	}
}
