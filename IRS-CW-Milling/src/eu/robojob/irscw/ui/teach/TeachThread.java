package eu.robojob.irscw.ui.teach;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
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

public class TeachThread extends Thread {

	private ProcessFlow processFlow;
			
	private boolean running;
	
	private static final Logger logger = Logger.getLogger(TeachThread.class);
	
	public TeachThread(ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.running = false;
	}
	
	@Override
	public void run() {
		this.running = true;
		logger.info("started teach thread!");
		processFlow.setMode(Mode.TEACH);
		processFlow.initialize();
		try {
			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.restartProgram();
				robot.setSpeed(10);
				//robot.moveToHome();
			}
			while (processFlow.hasStep()) {
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
			processFlow.restart();
			processFlow.setMode(Mode.READY);
			this.running = false;
		} catch(CommunicationException | RobotActionException | DeviceActionException e) {
			notifyException(e);
		} catch(InterruptedException e) {
			logger.info("Execution of one or more steps got interrupted, so let't just stop");
			processFlow.initialize();
			processFlow.setMode(Mode.STOPPED);
		} catch(Exception e) {
			e.printStackTrace();
		}
		processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, null, ActiveStepChangedEvent.NONE_ACTIVE));
		logger.info("ended teach thread!");
		this.running = false;
	}
	
	@Override
	public void interrupt() {
		logger.info("about to interrupt teach thread");
		if (running) {
			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.stopCurrentAction();
				try {
					robot.abort();
				} catch (CommunicationException e) {
					notifyException(e);
				}
			}
			for (AbstractDevice device :processFlow.getDevices()) {
				device.stopCurrentAction();
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	private void handlePick(final PickStep pickStep) throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		if (pickStep.needsTeaching()) {
			pickStep.prepareForTeaching();
			pickStep.teachingFinished();
		} else {
			pickStep.executeStep();
		}
	}
	
	private void handlePut(final PutStep putStep) throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		if (putStep.needsTeaching()) {
			putStep.prepareForTeaching();
			putStep.teachingFinished();
		} else {
			putStep.executeStep();
		}
	}

	private void handleProcessing(final ProcessingStep step) throws CommunicationException, DeviceActionException, InterruptedException {
		step.executeStep();
	}
	
	private void notifyException(final Exception e) {
		processFlow.processProcessFlowEvent(new ExceptionOccuredEvent(processFlow, e));
	}
}
