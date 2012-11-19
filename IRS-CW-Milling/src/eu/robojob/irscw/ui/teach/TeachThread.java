package eu.robojob.irscw.ui.teach;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
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
		
		
		// if the device, corresponding to the first PICK step has a fixed Pick position (which for now, is the case) 
		// then the relationship between the grippers and the pieces they take can be teached on this device, providing the dimensions (width / height) 
		// of the workpiece don't change during the process (they can be placed on the same location on this first device, their center's are on the same location (except the height))
		// (which for now, is the case)
		
		// when this has been done, all that's left to teach is the position of the put location of clampings where the put location is not fixed
		// and when this is done for a certain clamping, related clampings are updated automatically. 
		
		// this last part is done by going through the process and when no teaching is needed in the future, the used should be able to switch to the automate view
		
		
		
		try {
			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.restartProgram();
				robot.setSpeed(10);
				//robot.moveToHome();
			}
			while (processFlow.hasStep()) {
				AbstractProcessStep step = processFlow.getCurrentStep();
				// intervention steps can be skipped
				
				AbstractProcessStep nextStep = processFlow.getNextStep();
				if ((nextStep != null) && (nextStep instanceof AbstractTransportStep) && (step instanceof AbstractTransportStep)) {
					AbstractTransportStep trStep = (AbstractTransportStep) step;
					AbstractTransportStep trNextStep = (AbstractTransportStep) nextStep;
					if (trStep.getRobotSettings().getWorkArea().getUserFrame().equals(trNextStep.getRobotSettings().getWorkArea().getUserFrame())) {
						trStep.getRobotSettings().setFreeAfter(false);
					} else {
						trStep.getRobotSettings().setFreeAfter(true);
					}
				}
				
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
			if (running) {
				processFlow.setMode(Mode.READY);
				this.running = false;
			} else {
				processFlow.setMode(Mode.STOPPED);
			}
		} catch(CommunicationException | RobotActionException | DeviceActionException e) {
			notifyException(e);
			processFlow.setMode(Mode.STOPPED);
		} catch(InterruptedException e) {
			logger.info("Execution of one or more steps got interrupted, so let't just stop");
			e.printStackTrace();
			processFlow.setMode(Mode.STOPPED);
		} catch(Exception e) {
			e.printStackTrace();
			processFlow.setMode(Mode.STOPPED);
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
			running = false;
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
