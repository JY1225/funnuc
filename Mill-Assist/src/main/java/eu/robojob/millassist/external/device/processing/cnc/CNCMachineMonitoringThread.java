package eu.robojob.irscw.external.device.processing.cnc;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.threading.MonitoringThread;

public class CNCMachineMonitoringThread extends Thread implements MonitoringThread {

	private static final int REFRESH_TIME = 500;
	
	private AbstractCNCMachine cncMachine;
	private boolean alive;
	private int previousStatus;
	private Set<CNCMachineAlarm> previousAlarms;
	
	private static Logger logger = LogManager.getLogger(CNCMachineMonitoringThread.class.getName());
	
	public CNCMachineMonitoringThread(final AbstractCNCMachine cncMachine) {
		this.cncMachine = cncMachine;
		this.alive = true;
		this.previousAlarms = new HashSet<CNCMachineAlarm>();
	}
	
	@Override
	public void run() {
		try {
			while (alive) {
				if (cncMachine.isConnected()) {
					try {
						cncMachine.updateStatusAndAlarms();
						int status = cncMachine.getStatus();
						if (status != previousStatus) {
							cncMachine.processCNCMachineEvent(new CNCMachineEvent(cncMachine, CNCMachineEvent.STATUS_CHANGED));
						}
						this.previousStatus = status;
						Set<CNCMachineAlarm> alarms = cncMachine.getAlarms();
						if ((!previousAlarms.containsAll(alarms)) || (!alarms.containsAll(previousAlarms))) {
							cncMachine.processCNCMachineEvent(new CNCMachineAlarmsOccuredEvent(cncMachine, alarms));
						}
						this.previousAlarms = alarms;
					} catch (AbstractCommunicationException | InterruptedException e) {
						//TODO do something with this exception
						if (cncMachine.isConnected()) {
							logger.error(e);
							cncMachine.disconnect();
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
				try {
					Thread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					// interrupted, so let's just stop, the external communication thread takes care of disconnecting if needed at this point
					alive = false;
				}
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
	public void interrupt() {
		alive = false;
		super.interrupt();
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
