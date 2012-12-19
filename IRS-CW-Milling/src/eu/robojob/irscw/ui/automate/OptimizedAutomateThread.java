package eu.robojob.irscw.ui.automate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.process.execution.AutomateThread;

public class OptimizedAutomateThread extends AutomateThread {
	
	private static final Logger logger = LogManager.getLogger(OptimizedAutomateThread.class.getName());
	
	private int pickFromStackerIndex;
	private int putAndWaitPrageIndex;
	private int pickAfterWaitPrageIndex;
	private int putInMachineIndex;
	private int machineProcessIndex;
	private int pickFromMachineIndex;
	private int putOnStackerIndex;
	
	public OptimizedAutomateThread(ProcessFlow processFlow) {
		super(processFlow);
		this.pickFromStackerIndex = -1;
		this.putAndWaitPrageIndex = -1;
		this.pickAfterWaitPrageIndex = -1;
		this.putInMachineIndex = -1;
		this.machineProcessIndex = -1;
		this.pickFromMachineIndex = -1;
		this.putOnStackerIndex = -1;
		for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep currentStep = processFlow.getProcessSteps().get(i);
			if (currentStep instanceof PickStep) {
				if (((PickStep) currentStep).getDevice().getId() == DeviceManager.IRS_M_BASIC) {
					pickFromStackerIndex = i;
				} else if (((PickStep) currentStep).getDevice().getId() == DeviceManager.MAZAK_VRX) {
					pickFromMachineIndex = i;
				} else if (((PutStep) currentStep).getDevice().getId() == DeviceManager.PRAGE_DEVICE) {
					pickAfterWaitPrageIndex = i;
				}
			} else if (currentStep instanceof PutStep) {
				if (((PutStep) currentStep).getDevice().getId() == DeviceManager.PRAGE_DEVICE) {
					putAndWaitPrageIndex = i;
				} else if (((PutStep) currentStep).getDevice().getId() == DeviceManager.MAZAK_VRX) {
					putInMachineIndex = i;
				} else if (((PutStep) currentStep).getDevice().getId() == DeviceManager.IRS_M_BASIC) {
					putOnStackerIndex = i;
				}
			} else if (currentStep instanceof ProcessingStep) {
				machineProcessIndex = i;
			}
		}
	}
	
	@Override
	public void run() {
		processFlow.setMode(Mode.AUTO);
		logger.info("started automate thread!");
		this.running = true;
		try {
			for (AbstractDevice device: processFlow.getDevices()) {
				device.prepareForProcess(processFlow);
			}
			while(processFlow.getFinishedAmount() < processFlow.getTotalAmount() && running) {
				//TODO: INTERVENTIES!!
				if (processFlow.getFinishedAmount() == 0) {
					// first piece 
					// do pick - [put(wait) - pick(after wait)] - put - processing (PROCESSING IS BUSY)
				} else if (processFlow.getFinishedAmount() == (processFlow.getTotalAmount() - 1)){
					// last piece (should already be in the machine)
					// (WHEN PROCESSING FINISHED)
					// do pick and put
				} else {
					// other piece (there already is a piece in the machine (which is not the last)
					// do pick - [put(wait) - pick(after wait)] *parallel with processing* (DURING PROCESSING)
					// (WHEN PROCESSING FINISHED)
					// then pick (first piece) - put (second piece) - processing second piece (PROCESSING IS BUSY)
					// then put (first piece) * parallel with processing* (DURING PROCESSING)
				}
			}
		//} catch(CommunicationException | RobotActionException | DeviceActionException e) {
		} catch(AbstractCommunicationException e) {
			//notifyException(e);
			processFlow.setMode(Mode.STOPPED);
		} catch(InterruptedException e) {
			logger.info("Execution of one or more steps got interrupted, so let't just stop");
			e.printStackTrace();
			processFlow.setMode(Mode.STOPPED);
		} catch(Exception e) {
			e.printStackTrace();
			processFlow.setMode(Mode.STOPPED);
		}
		//processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.NONE_ACTIVE));
		logger.info("Thread ended: " + toString());
		this.running = false;
	}

}
