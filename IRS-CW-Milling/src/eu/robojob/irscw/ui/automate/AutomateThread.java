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
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
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
			logger.info("1");
			while(processFlow.getFinishedAmount() < processFlow.getTotalAmount() && running) {
				logger.info("2");
				while (processFlow.hasStep() && running) {
					logger.info("3");
					AbstractProcessStep step = processFlow.getCurrentStep();
					logger.info("running...: " + step);
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
		processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, null, ActiveStepChangedEvent.NONE_ACTIVE));
		logger.info("Thread ended: " + toString());
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
