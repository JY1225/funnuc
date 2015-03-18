package eu.robojob.millassist.external.device.stacking.conveyor.eaton;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorEvent;
import eu.robojob.millassist.threading.MonitoringThread;

public class ConveyorMonitoringThread implements Runnable, MonitoringThread {

	public static final int REFRESH_TIME = 25;
	
	private ConveyorEaton conveyor;
	
	private boolean alive;
	private int previousStatus;
	private Set<ConveyorAlarm> previousAlarms;
		
	private static Logger logger = LogManager.getLogger(ConveyorMonitoringThread.class.getName());
	
	public ConveyorMonitoringThread(final ConveyorEaton conveyor) {
		this.conveyor = conveyor;
		this.alive = true;
		this.previousAlarms = new HashSet<ConveyorAlarm>();
	}
	
	@Override
	public void run() {
		try {
			while (alive) {
				if (conveyor.isConnected()) {
					try {
						conveyor.upateStatusAndAlarms();
						int status = conveyor.getStatus();
						if (status != previousStatus) {
							conveyor.processConveyorEvent(new ConveyorEvent(conveyor, ConveyorEvent.STATUS_CHANGED));
						}
						this.previousStatus = status;
						Set<ConveyorAlarm> alarms = conveyor.getAlarms();
						if ((!previousAlarms.containsAll(alarms)) || (!alarms.containsAll(previousAlarms))) {
							conveyor.processConveyorEvent(new ConveyorAlarmsOccuredEvent(conveyor, alarms));
						}
						this.previousAlarms = alarms;
					} catch (InterruptedException e) {
						interrupted();
					} catch (AbstractCommunicationException e) {
						if (conveyor.isConnected()) {
							logger.error(e);
							conveyor.disconnect();
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
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
		if (conveyor.isConnected()) {
			conveyor.disconnect();
		}
	}
	
	@Override
	public String toString() {
		return "ConveyorMonitoringThread: " + conveyor.toString();
	}
	
	@Override
	public void stopExecution() {
		this.alive = false;
	}
	
}