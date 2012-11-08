package eu.robojob.irscw.ui.teach;

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

public class TeachThread extends Thread {

	private ProcessFlow processFlow;
			
	public TeachThread(ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	@Override
	public void run() {
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
		} catch(Exception e) {
			notifyException(e);
		}
	}
	
	private void handlePick(final PickStep pickStep) throws CommunicationException, RobotActionException, DeviceActionException {
		if (pickStep.needsTeaching()) {
			pickStep.prepareForTeaching();
			pickStep.teachingFinished();
		} else {
			pickStep.executeStep();
		}
	}
	
	private void handlePut(final PutStep putStep) throws CommunicationException, RobotActionException, DeviceActionException {
		if (putStep.needsTeaching()) {
			putStep.prepareForTeaching();
			putStep.teachingFinished();
		} else {
			putStep.executeStep();
		}
	}

	private void handleProcessing(final ProcessingStep step) throws CommunicationException, DeviceActionException {
		step.executeStep();
	}
	
	private void notifyException(final Exception e) {
		processFlow.processProcessFlowEvent(new ExceptionOccuredEvent(processFlow, e));
	}
}
