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
	
	private boolean running;
	
	public AutomateThread(ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.running = true;
	}
	
	@Override
	public void run() {
		processFlow.setMode(Mode.AUTO);
		try {
			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.restartProgram();
				//robot.moveToHome();
			}
			while(processFlow.getFinishedAmount() < processFlow.getTotalAmount() && running) {
				while (processFlow.hasStep() && running) {
					AbstractProcessStep step = processFlow.getCurrentStep();
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
				if (running) {
					processFlow.restart();
				}
			}
			if (running) {
				processFlow.setMode(Mode.FINISHED);
			} else {
				processFlow.setMode(Mode.PAUSED);
			}
		} catch(Exception e) {
			running = false;
			notifyException(e);
			e.printStackTrace();
			processFlow.setMode(Mode.STOPPED);
			logger.error(e);
		}
	}
	
	private void handlePick(final PickStep pickStep) throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		pickStep.executeStep();
	}
	
	private void handlePut(final PutStep putStep) throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		putStep.executeStep();
	}

	private void handleProcessing(final ProcessingStep step) throws CommunicationException, DeviceActionException, InterruptedException {
		step.executeStep();
	}
	
	private void notifyException(final Exception e) {
		processFlow.processProcessFlowEvent(new ExceptionOccuredEvent(processFlow, e));
	}
	
	@Override
	public String toString() {
		return "AutomateThread: " + processFlow.toString();
	}
	
	public void stopRunning() {
		running = false;
	}
}
