package eu.robojob.irscw.ui.automate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.process.ProcessFlow;

public class AutomateThread extends Thread{

	protected ProcessFlow processFlow;
	
	private static final Logger logger = LogManager.getLogger(AutomateThread.class.getName());
	
	protected boolean running;
	
	public AutomateThread(ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	@Override
	public void run() {
		/*processFlow.setMode(Mode.AUTO);
		logger.info("started automate thread!");
		
		this.running = true;
		try {
			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.recalculateTCPs();
			}
			for (AbstractDevice device: processFlow.getDevices()) {
				device.prepareForProcess(processFlow);
			}
			while(processFlow.getFinishedAmount() < processFlow.getTotalAmount() && running) {
				while (processFlow.hasStep() && running) {
					AbstractProcessStep step = processFlow.getCurrentStep();
					
					AbstractProcessStep nextStep = processFlow.getNextStep();
					
					if (step instanceof AbstractTransportStep) {
						((AbstractTransportStep) step).getRobotSettings().setFreeAfter(true);
					}
					
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
						if ((processFlow.getFinishedAmount()+1) % ((InterventionStep) step).getFrequency() == 0) {
							step.executeStep();
							logger.info("Waiting because intervention!");
							running = false;
						}
					}
					processFlow.nextStep();
				}
				if (running) {
					processFlow.restart();
					for (AbstractDevice device: processFlow.getDevices()) {
						device.prepareForProcess(processFlow);
					}
				}
			}
			if (running) {
				processFlow.setMode(Mode.FINISHED);
				logger.info("finished");
				for (AbstractDevice device: processFlow.getDevices()) {
					if (device instanceof AbstractCNCMachine) {
						AbstractCNCMachine cncMachine = (AbstractCNCMachine) device;
						cncMachine.indicateAllProcessed();
					}
				}
			} else {
				processFlow.setMode(Mode.PAUSED);
			}
		} catch(AbstractCommunicationException | RobotActionException | DeviceActionException e) {
			notifyException(e);
			processFlow.setMode(Mode.STOPPED);
		} catch(InterruptedException e) {
			if (!running) {
				logger.info("Execution of one or more steps got interrupted, so let't just stop");
			} else {
				e.printStackTrace();
				//notifyException(e);
				logger.error(e);
			}
			processFlow.setMode(Mode.STOPPED);
		} catch(Exception e) {
			e.printStackTrace();
			processFlow.setMode(Mode.STOPPED);
		}
		processFlow.processProcessFlowEvent(new StatusChangedEvent(processFlow, null, StatusChangedEvent.NONE_ACTIVE));
		logger.info("Automate Thread ended: " + toString());
		this.running = false;*/
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
		logger.info("about to interrupt automate thread");
		if (running) {
			running = false;
			for (AbstractRobot robot :processFlow.getRobots()) {
				logger.info("stopping robot: " + robot.getId());
				robot.interruptCurrentAction();
				try {
					robot.abort();
				} catch (AbstractCommunicationException | InterruptedException e) {
				}
			}
			for (AbstractDevice device :processFlow.getDevices()) {
				device.interruptCurrentAction();
			}
		}
	}
	
}
