package eu.robojob.irscw.ui.automate;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.cnc.AbstractCNCMachine;
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

public class AutomateThread extends Thread{

	private ProcessFlow processFlow;
	
	private static final Logger logger = Logger.getLogger(AutomateThread.class);
	
	private boolean running;
	
	public AutomateThread(ProcessFlow processFlow) {
		this.processFlow = processFlow;
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
				while (processFlow.hasStep() && running) {
					AbstractProcessStep step = processFlow.getCurrentStep();
					
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
					
					// intervention steps can be skipped
					if (step instanceof PickStep) {
						handlePick((PickStep) step);
					} else if (step instanceof PutStep) {
						handlePut((PutStep) step);
					} else if (step instanceof ProcessingStep) {
						handleProcessing((ProcessingStep) step);
					} else if (step instanceof InterventionStep) {
						if (processFlow.getFinishedAmount() % ((InterventionStep) step).getFrequency() == 0) {
							step.executeStep();
							logger.info("Waiting because intervention!");
							running = false;
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
				for (AbstractDevice device: processFlow.getDevices()) {
					if (device instanceof AbstractCNCMachine) {
						AbstractCNCMachine cncMachine = (AbstractCNCMachine) device;
						cncMachine.indicateAllProcessed();
					}
				}
			} else {
				processFlow.setMode(Mode.PAUSED);
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
		logger.info("Thread ended: " + toString());
		this.running = false;
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
	
	public boolean isRunning() {
		return running;
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
	
}
