package eu.robojob.irscw.external.device.processing.cnc;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.threading.MonitoringThread;

public class CNCMachineMonitoringThread extends Thread implements MonitoringThread {

	private static final int REFRESH_TIME = 500;
	
	private AbstractCNCMachine cncMachine;
	private boolean alive;
	private int previousStatus;
	private Set<CNCMachineAlarm> previousAlarms;
	
	private static Logger logger = Logger.getLogger(CNCMachineMonitoringThread.class);
	
	public CNCMachineMonitoringThread(AbstractCNCMachine cncMachine) {
		this.cncMachine = cncMachine;
		this.alive = true;
		this.previousAlarms = new HashSet<CNCMachineAlarm>();
	}
	
	@Override
	public void run() {
		while (alive) {
			if (cncMachine.isConnected()) {
				try {
					cncMachine.updateStatusAndAlarms();
					CNCMachineStatus status = cncMachine.getStatus();
					if (status.getStatus() != previousStatus) {
						cncMachine.processCNCMachineEvent(new CNCMachineStatusChangedEvent(cncMachine, status));
					}
					this.previousStatus = status.getStatus();
					Set<CNCMachineAlarm> alarms = cncMachine.getAlarms();
					if ((!previousAlarms.containsAll(alarms)) || (!alarms.containsAll(previousAlarms))) {
						cncMachine.processCNCMachineEvent(new CNCMachineAlarmsOccuredEvent(cncMachine, alarms));
					}
					this.previousAlarms = alarms;
				} catch (AbstractCommunicationException e) {
					cncMachine.disconnect();
					logger.error(e);
				}
			}
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException e) {
				// interrupted, so let's just stop
				alive = false;
			}
		}
		logger.info(toString() + " ended...");
	}
	
	@Override
	public void interrupt() {
		alive = false;
	}
	
	@Override
	public String toString() {
		return "CNCMachineMonitoringThread: " + cncMachine.toString();
	}

	@Override
	public void stopExecution() {
		alive = false;
	}
}
