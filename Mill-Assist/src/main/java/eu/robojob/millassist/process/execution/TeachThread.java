package eu.robojob.millassist.process.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.util.Translator;

public class TeachThread implements Runnable, ProcessExecutor {

	private ProcessFlow processFlow;
	private boolean running;
	
	private static Logger logger = LogManager.getLogger(TeachThread.class.getName());
	private static final int WORKPIECE_ID = 0;
	
	protected static final String OTHER_EXCEPTION = "Exception.otherException";
	
	public TeachThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.running = false;
	}
	
	@Override
	public void run() {
		// A basic teach thread which will iterate all process steps and request teaching whenever needed.
		logger.debug("Started execution, processflow [" + getProcessFlow() + "].");
		setRunning(true);
		try {
			getProcessFlow().initialize();
			getProcessFlow().setMode(Mode.TEACH);
			//resetOffsets();
			try {
				// process-initialization
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.PREPARE, WORKPIECE_ID));
				for (AbstractRobot robot : getProcessFlow().getRobots()) {
					robot.recalculateTCPs();
					robot.moveToHome();
				}
				for (AbstractDevice device: getProcessFlow().getDevices()) {
					device.prepareForProcess(getProcessFlow());
				}
				getProcessFlow().setCurrentIndex(WORKPIECE_ID, 0);
				while ((getProcessFlow().getCurrentIndex(WORKPIECE_ID) < getProcessFlow().getProcessSteps().size()) && isRunning()) {
					AbstractProcessStep step = getProcessFlow().getProcessSteps().get(getProcessFlow().getCurrentIndex(WORKPIECE_ID));
					AbstractProcessStep nextStep = null;				
					if (getProcessFlow().getCurrentIndex(WORKPIECE_ID) < (processFlow.getProcessSteps().size() - 1)) {
						nextStep = processFlow.getProcessSteps().get(1 + getProcessFlow().getCurrentIndex(WORKPIECE_ID));
					}
					// check if next step would be an intervention step
					if ((nextStep != null) && (nextStep instanceof InterventionStep) && (getProcessFlow().getCurrentIndex(WORKPIECE_ID) < (processFlow.getProcessSteps().size() - 2))) {
						nextStep = processFlow.getProcessSteps().get(2 + getProcessFlow().getCurrentIndex(WORKPIECE_ID));
					}
					if (step instanceof AbstractTransportStep) {
						((AbstractTransportStep) step).getRobotSettings().setFreeAfter(true);
						if (step instanceof PickStep) {
							((PickStep) step).getRobotSettings().setFreeAfter(false);
						}
						/*if ((nextStep != null) && (nextStep instanceof AbstractTransportStep) && (step instanceof AbstractTransportStep)) {
							AbstractTransportStep trStep = (AbstractTransportStep) step;
							AbstractTransportStep trNextStep = (AbstractTransportStep) nextStep;
							if (trStep.getRobotSettings().getWorkArea().getUserFrame().equals(trNextStep.getRobotSettings().getWorkArea().getUserFrame())) {
								trStep.getRobotSettings().setFreeAfter(false);
							} else {
								trStep.getRobotSettings().setFreeAfter(true);
							}
						}*/
					}
					if (!(step instanceof InterventionStep)) {
						if (step instanceof AbstractTransportStep) {
							((AbstractTransportStep) step).executeStepTeached(WORKPIECE_ID, this);
							((AbstractTransportStep) step).finalizeStep(this);
						} else {
							step.executeStep(WORKPIECE_ID, this);
						}
					}
					getProcessFlow().setCurrentIndex(WORKPIECE_ID, getProcessFlow().getCurrentIndex(WORKPIECE_ID) + 1);
					if (Thread.currentThread().isInterrupted()) {
						interrupted();
					}					
				}
				if (running) {
					// everything went as it should
					processFlow.incrementFinishedAmount();
					processFlow.setCurrentIndex(WORKPIECE_ID, 0);
					processFlow.setMode(Mode.READY);
					setRunning(false);
				} else {
					// flow got interrupted
					processFlow.setMode(Mode.STOPPED);
				}
			} catch (AbstractCommunicationException | RobotActionException | DeviceActionException e) {
				handleException(e);
			} catch (InterruptedException e) {
				interrupted();
			} catch (Exception e) {
				handleException(new Exception(Translator.getTranslation(OTHER_EXCEPTION)));
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Throwable t) {
			logger.error(t);
			t.printStackTrace();
		}
		logger.info(toString() + " ended...");
	}
	
	protected void handleException(final Exception e) {
		getProcessFlow().processProcessFlowEvent(new ExceptionOccuredEvent(getProcessFlow(), e));
		e.printStackTrace();
		logger.error(e);
		indicateStopped();
	}
	
	protected void indicateStopped() {
		getProcessFlow().initialize();
		getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_ID));
		getProcessFlow().setMode(Mode.STOPPED);
	}
	
	public void interrupted() {
		if (running) {
			stopRunning();
		}
	}
	
	public void stopRunning() {
		running = false;
		logger.info("running to false");
		for (AbstractRobot robot :processFlow.getRobots()) {
			robot.interruptCurrentAction();
		}
		for (AbstractDevice device :processFlow.getDevices()) {
			device.interruptCurrentAction();
		}
		indicateStopped();
	}

	protected void resetOffsets() {
		for (AbstractProcessStep step: getProcessFlow().getProcessSteps()) {
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).setRelativeTeachedOffset(null);
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	protected void setRunning(final boolean running) {
		this.running = running;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}
	
	@Override
	public String toString() {
		return "TeachThread [" + processFlow + "].";
	}
}
