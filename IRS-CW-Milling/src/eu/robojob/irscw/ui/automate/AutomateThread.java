package eu.robojob.irscw.ui.automate;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;

public class AutomateThread extends Thread{

	private ProcessFlow processFlow;
	
	private static final Logger logger = Logger.getLogger(AutomateThread.class);
	
	public AutomateThread(ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	@Override
	public void run() {
		processFlow.setMode(Mode.AUTO);
		try {
			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.restartProgram();
				//robot.moveToHome();
			}
			while(processFlow.getFinishedAmount() < processFlow.getTotalAmount()) {
				while (processFlow.hasStep()) {
					AbstractProcessStep step = processFlow.getCurrentStep();
					logger.info("current step: " + step);
					// intervention steps can be skipped
					if (!(step instanceof InterventionStep)) {
						if (step instanceof PickStep) {
							handlePick((PickStep) step);
						} else if (step instanceof PutStep) {
							handlePut((PutStep) step);
						} else if (step instanceof ProcessingStep) {
							handleProcessing((ProcessingStep) step);
						}
					}
					processFlow.nextStep();
				}
				processFlow.restart();
			}
			processFlow.setMode(Mode.FINISHED);
		} catch(Exception e) {
			notifyException(e);
			logger.error(e);
		}
		logger.info(toString() + " has ended");
	}
	
	private void handlePick(final PickStep pickStep) throws CommunicationException, RobotActionException, DeviceActionException {
		pickStep.executeStep();
	}
	
	private void handlePut(final PutStep putStep) throws CommunicationException, RobotActionException, DeviceActionException {
		putStep.executeStep();
	}

	private void handleProcessing(final ProcessingStep step) throws CommunicationException, DeviceActionException {
		step.executeStep();
	}
	
	private void notifyException(final Exception e) {
		processFlow.processProcessFlowEvent(new ExceptionOccuredEvent(processFlow, e));
	}
	
	@Override
	public String toString() {
		return "AutomateThread: " + processFlow.toString();
	}
}
