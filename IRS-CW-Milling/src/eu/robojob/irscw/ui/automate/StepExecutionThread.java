package eu.robojob.irscw.ui.automate;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class StepExecutionThread extends Thread {
	
	private AbstractProcessStep step;
	private AutomateThread automateThread;
	
	public StepExecutionThread(AbstractProcessStep step, AutomateThread automateThread) {
		this.step = step;
		this.automateThread = automateThread;
	}
	
	@Override
	public void run() {
		try {
			step.executeStep();
		} catch (CommunicationException | RobotActionException | DeviceActionException e) {
			e.printStackTrace();
			automateThread.notifyException(e);
			step.getProcessFlow().setMode(Mode.STOPPED);
		} catch(InterruptedException e) {
			e.printStackTrace();
			step.getProcessFlow().setMode(Mode.STOPPED);
		} catch(Exception e) {
			e.printStackTrace();
			step.getProcessFlow().setMode(Mode.STOPPED);
		}
		step.getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(step.getProcessFlow(), null, ActiveStepChangedEvent.NONE_ACTIVE));
	}
	
}
