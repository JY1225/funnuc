package eu.robojob.irscw.ui.teach;

import eu.robojob.irscw.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.irscw.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.RobotEvent;
import eu.robojob.irscw.external.robot.RobotListener;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class StatusPresenter implements ProcessFlowListener, RobotListener, CNCMachineListener {

	private StatusView view;
	private TeachPresenter parent;
	
	public StatusPresenter(final StatusView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(final TeachPresenter parent) {
		this.parent = parent;
	}
	
	public void stopTeaching() {
		parent.stopTeaching();
	}
	
	public StatusView getView() {
		return view;
	}

	//TODO: also look at alarms! Weergeven tijdens flow! mogelijkheid tot continue
	
	@Override public void modeChanged(final ModeChangedEvent e) { }
	@Override public void statusChanged(final StatusChangedEvent e) { }
	@Override public void dataChanged(final ProcessFlowEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }
	@Override public void cNCMachineConnected(final CNCMachineEvent event) { }
	@Override public void cNCMachineDisconnected(final CNCMachineEvent event) { }
	@Override public void cNCMachineStatusChanged(final CNCMachineEvent event) { }
	@Override public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) { }
	@Override public void robotConnected(final RobotEvent event) { }
	@Override public void robotDisconnected(final RobotEvent event) { }
	@Override public void robotStatusChanged(final RobotEvent event) { }
	@Override public void robotZRestChanged(final RobotEvent event) { }
	@Override public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) { }
	@Override public void robotSpeedChanged(final RobotEvent event) { }
}
