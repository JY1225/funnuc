package eu.robojob.irscw.process.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;

public class TeachThread extends Thread {

	private ProcessFlow processFlow;
	private boolean running;
	
	private static Logger logger = LogManager.getLogger(TeachThread.class.getName());
	private static final int WORKPIECE_ID = 1;
	
	public TeachThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.running = false;
	}
	
	@Override
	public void run() {
		// A basic teach thread which will iterate all process steps and request teaching whenever needed.
		logger.debug("Started execution, processflow [" + getProcessFlow() + "].");
		getProcessFlow().initialize();
		getProcessFlow().setMode(Mode.TEACH);
		setRunning(true);
		resetOffsets();
		try {
			for (AbstractRobot robot : getProcessFlow().getRobots()) {
				robot.recalculateTCPs();
			}
			for (AbstractDevice device: getProcessFlow().getDevices()) {
				device.prepareForProcess(getProcessFlow());
			}
			int currentStepIndex = 0;
			while ((currentStepIndex < getProcessFlow().getProcessSteps().size()) && isRunning()) {
				AbstractProcessStep step = getProcessFlow().getProcessSteps().get(currentStepIndex);
				AbstractProcessStep nextStep = null;				
				if (step instanceof AbstractTransportStep) {
					((AbstractTransportStep) step).getRobotSettings().setFreeAfter(true);
				}
				if (currentStepIndex < (processFlow.getProcessSteps().size() - 1)) {
					nextStep = processFlow.getProcessSteps().get(1 + currentStepIndex);
				}
				if (step instanceof AbstractTransportStep) {
					((AbstractTransportStep) step).getRobotSettings().setFreeAfter(true);
					if ((nextStep != null) && (nextStep instanceof AbstractTransportStep) && (step instanceof AbstractTransportStep)) {
						AbstractTransportStep trStep = (AbstractTransportStep) step;
						AbstractTransportStep trNextStep = (AbstractTransportStep) nextStep;
						if (trStep.getRobotSettings().getWorkArea().getUserFrame().equals(trNextStep.getRobotSettings().getWorkArea().getUserFrame())) {
							trStep.getRobotSettings().setFreeAfter(false);
						} else {
							trStep.getRobotSettings().setFreeAfter(true);
						}
					}
				}
				if (!(step instanceof InterventionStep)) {
					if (step instanceof AbstractTransportStep) {
						((AbstractTransportStep) step).executeStep(WORKPIECE_ID);
						((AbstractTransportStep) step).finalizeStep();
					} else {
						step.executeStep(WORKPIECE_ID);
					}
				}
				currentStepIndex++;
			}
			if (running) {
				processFlow.setMode(Mode.READY);
				this.running = false;
			} else {
				processFlow.setMode(Mode.STOPPED);
			}
		} catch (AbstractCommunicationException | RobotActionException | DeviceActionException e) {
			e.printStackTrace();
			logger.error(e);
			processFlow.setMode(Mode.STOPPED);
		} catch (InterruptedException e) {
			if (!isRunning()) {
				logger.info("Execution of one or more steps got interrupted, so let't just stop");
			} else {
				e.printStackTrace();
				logger.error(e);
			}
			getProcessFlow().setMode(Mode.STOPPED);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			processFlow.setMode(Mode.STOPPED);
		}
		logger.info(toString() + " ended...");
		this.running = false;
	}
	
	@Override
	public void interrupt() {
		if (running) {
			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.interruptCurrentAction();
			}
			for (AbstractDevice device :processFlow.getDevices()) {
				device.interruptCurrentAction();
			}
			running = false;
		}
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
}
