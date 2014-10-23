package eu.robojob.millassist.external.device.processing.cnc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.threading.MonitoringThread;

public class CNCMachineMonitoringThreadDevIntv2 implements Runnable, MonitoringThread {

	private static final int REFRESH_TIME = 150;
	
	private AbstractCNCMachine cncMachine;
	private boolean alive;
	private Map<Integer, Integer> previousStatus;
	private Set<CNCMachineAlarm> previousAlarms;
	private Set<Integer> previousActiveMCodes;
	
	private static Logger logger = LogManager.getLogger(CNCMachineMonitoringThreadDevIntv2.class.getName());
	
	public CNCMachineMonitoringThreadDevIntv2(final AbstractCNCMachine cncMachine) {
		this.cncMachine = cncMachine;
		this.alive = true;
		this.previousActiveMCodes = new HashSet<Integer>();
		this.previousAlarms = new HashSet<CNCMachineAlarm>();
		this.previousStatus = new HashMap<Integer, Integer>();
	}
	
	@Override
	public void run() {
		try {
			while (alive) {
				if (cncMachine.isConnected()) {
					try {
						cncMachine.updateStatusAndAlarms();
						boolean statusChanged = false;
						for(int statusRegister: previousStatus.keySet()) {
							if (cncMachine.getStatus(statusRegister) != previousStatus.get(statusRegister)) {
								statusChanged = true;
							}
						}
						if (cncMachine.getWayOfOperating() == EWayOfOperating.START_STOP) {
							if (statusChanged) {
								cncMachine.processCNCMachineEvent(new CNCMachineEvent(cncMachine, CNCMachineEvent.STATUS_CHANGED));
							}
							this.previousStatus = new HashMap<Integer, Integer>(cncMachine.getStatusMap());
						} else if ((cncMachine.getWayOfOperating() == EWayOfOperating.M_CODES) || (cncMachine.getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
							Set<Integer> activeMCodes = new HashSet<Integer>();
							activeMCodes = cncMachine.getMCodeAdapter().getActiveMCodes();
							if ((statusChanged) || (!previousActiveMCodes.containsAll(activeMCodes)) 
									|| (!activeMCodes.containsAll(previousActiveMCodes))) {
								logger.info("STATUS CHANGED! : " + activeMCodes);
								cncMachine.processCNCMachineEvent(new CNCMachineEvent(cncMachine, CNCMachineEvent.STATUS_CHANGED));
							}
							this.previousStatus = new HashMap<Integer, Integer>(cncMachine.getStatusMap());
							this.previousActiveMCodes = new HashSet<Integer>(activeMCodes);
						}
						Set<CNCMachineAlarm> alarms = cncMachine.getAlarms();
						if ((!previousAlarms.containsAll(alarms)) || (!alarms.containsAll(previousAlarms))) {
							cncMachine.processCNCMachineEvent(new CNCMachineAlarmsOccuredEvent(cncMachine, alarms));
						}
						this.previousAlarms = alarms;
					} catch (InterruptedException e) {
						interrupted();
					} catch (AbstractCommunicationException e) {
						//TODO do something with this exception
						if (cncMachine.isConnected()) {
							logger.error(e);
							cncMachine.disconnect();
						}
					} catch (Exception e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					interrupted();
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
	
	public void interrupted() {
		alive = false;
		if (cncMachine.isConnected()) {
			cncMachine.disconnect();
		}
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
