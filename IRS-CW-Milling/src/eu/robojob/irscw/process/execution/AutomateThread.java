package eu.robojob.irscw.process.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;

/**
 * This simple Automate-thread will execute the ProcessSteps sequentially
 */
public class AutomateThread extends Thread {

	private static Logger logger = LogManager.getLogger(AutomateThread.class.getName());
	private static final int WORKPIECE_ID = 1;
	
	private ProcessFlow processFlow;
	private boolean running;
	
	private int currentStepIndex;
	
	public AutomateThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.currentStepIndex = -1;
	}
	
	@Override
	public void run() {
		logger.debug("Started execution, processflow [" + processFlow + "].");
		try {
			processFlow.setMode(ProcessFlow.Mode.AUTO);
			this.running = true;
			try {
				for (AbstractRobot robot :processFlow.getRobots()) {	// first recalculate TCPs
					robot.recalculateTCPs();
				}
				for (AbstractDevice device: processFlow.getDevices()) {	// prepare devices for this processflow
					device.prepareForProcess(processFlow);
				}
				currentStepIndex = 0;
				while (processFlow.getFinishedAmount() < processFlow.getTotalAmount() && running) {
					while ((currentStepIndex < processFlow.getProcessSteps().size()) && running) {
						AbstractProcessStep step = processFlow.getProcessSteps().get(currentStepIndex);
						AbstractProcessStep nextStep = null;
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
						if (step instanceof InterventionStep) {
							if (((InterventionStep) step).isInterventionNeeded(processFlow.getFinishedAmount())) {
								for (AbstractRobot robot :processFlow.getRobots()) {	// first recalculate TCPs
									robot.moveToHome();	// send robots to home
								}
								step.executeStep(WORKPIECE_ID);
								running = false;
							}
						} else {
							step.executeStep(WORKPIECE_ID);
							if (step instanceof AbstractTransportStep) {
								((AbstractTransportStep) step).finalizeStep();
							}
						}
						currentStepIndex++;
					}
					currentStepIndex = 0;
					for (AbstractDevice device: processFlow.getDevices()) {
						device.prepareForProcess(processFlow);
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
			} catch (AbstractCommunicationException | RobotActionException | DeviceActionException e) {
				e.printStackTrace();
				logger.error(e);
				processFlow.setMode(Mode.STOPPED);
			} catch (InterruptedException e) {
				if (!running) {
					logger.info("Execution of one or more steps got interrupted, so let't just stop");
				} else {
					e.printStackTrace();
					logger.error(e);
				}
				processFlow.setMode(Mode.STOPPED);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
				processFlow.setMode(Mode.STOPPED);
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
		if (running) {
			running = false;
			for (AbstractRobot robot : processFlow.getRobots()) {
				robot.interruptCurrentAction();
			}
			for (AbstractDevice device :processFlow.getDevices()) {
				device.interruptCurrentAction();
			}
		}
	}
	
}
