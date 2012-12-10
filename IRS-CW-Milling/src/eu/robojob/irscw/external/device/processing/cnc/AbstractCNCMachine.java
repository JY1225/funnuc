package eu.robojob.irscw.external.device.processing.cnc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.threading.ThreadManager;

public abstract class AbstractCNCMachine extends AbstractProcessingDevice {

	private Set<CNCMachineListener> listeners;
	
	protected Set<CNCMachineAlarm> alarms;
	protected CNCMachineStatus status;
	
	private boolean statusChanged;
	private Object syncObject;
	
	private boolean stopAction;
	
	private static final Logger logger = Logger.getLogger(AbstractCNCMachine.class);
	
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "AbstractCNCMachine.disconnectedWhileWaiting";
	
	public AbstractCNCMachine(String id) {
		super(id, true);
		this.statusChanged = false;
		syncObject = new Object();
		this.alarms = new HashSet<CNCMachineAlarm>();
		this.status = new CNCMachineStatus(0);
		this.listeners = new HashSet<CNCMachineListener>();
		CNCMachineMonitoringThread cncMachineMonitoringThread = new CNCMachineMonitoringThread(this);
		ThreadManager.getInstance().submit(cncMachineMonitoringThread);
		this.stopAction = false;
	}
	
	public AbstractCNCMachine(String id, List<Zone> zones) {
		super(id, zones, true);
		this.statusChanged = false;
		syncObject = new Object();
		this.alarms = new HashSet<CNCMachineAlarm>();
		this.status = new CNCMachineStatus(0);
		this.listeners = new HashSet<CNCMachineListener>();
		CNCMachineMonitoringThread cncMachineMonitoringThread = new CNCMachineMonitoringThread(this);
		ThreadManager.getInstance().submit(cncMachineMonitoringThread);
		this.stopAction = false;
	}
	
	@Override
	public void interruptCurrentAction() {
		logger.info("stopping current machine action");
		stopAction = true;
		synchronized(syncObject) {
			syncObject.notifyAll();
		}
	}

	public void addListener(CNCMachineListener listener) {
		logger.info("added listener: " + listener);
		listeners.add(listener);
	}
	
	public void removeListener(CNCMachineListener listener) {
		logger.info("removed listener: " + listener);
		listeners.remove(listener);
	}
	
	public CNCMachineStatus getStatus() {
		return status;
	}
	
	public abstract void updateStatusAndAlarms() throws AbstractCommunicationException;
	public abstract void disconnect();
	
	public Set<CNCMachineAlarm> getAlarms() {
		return alarms;
	}
	
	private void statusChanged() {
		synchronized(syncObject) {
			statusChanged = true;
			syncObject.notifyAll();
		}
	}
	
	public void processCNCMachineEvent(CNCMachineEvent event) {
		switch(event.getId()) {
			case CNCMachineEvent.CNC_MACHINE_CONNECTED : 
				for (CNCMachineListener listener : listeners) {
					listener.cNCMachineConnected(event);
				}
				break;
			case CNCMachineEvent.CNC_MACHINE_DISCONNECTED : 
				statusChanged();
				for (CNCMachineListener listener : listeners) {
					listener.cNCMachineDisconnected(event);
				}
				break;
			case CNCMachineEvent.ALARM_OCCURED : 
				for (CNCMachineListener listener : listeners) {
					// TODO get list of alarms!
					listener.cNCMachineAlarmsOccured((CNCMachineAlarmsOccuredEvent) event);
				}
				break;
			case CNCMachineEvent.STATUS_CHANGED : 
				statusChanged();
				for (CNCMachineListener listener : listeners) {
					// TODO get status!
					listener.cNCMachineStatusChanged((CNCMachineStatusChangedEvent) event);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown event type");
		}
	}
	
	public abstract void reset() throws AbstractCommunicationException, InterruptedException;
	public abstract void nCReset() throws AbstractCommunicationException, InterruptedException;
	public abstract void powerOff() throws AbstractCommunicationException, InterruptedException;
	public abstract void indicateAllProcessed() throws AbstractCommunicationException, InterruptedException;
	public abstract void operatorRequested(boolean requested) throws AbstractCommunicationException, InterruptedException;
	public abstract void stopIndications() throws AbstractCommunicationException, InterruptedException;
	
	protected boolean waitForStatus(int status, long timeout) throws InterruptedException, DeviceActionException {
		long waitedTime = 0;
		stopAction = false;
		do {
			long lastTime = System.currentTimeMillis();
			if ((getStatus().getStatus() & status) > 0) {
				return true;
			} else {
				if (!isConnected()) {
					throw new DeviceActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				try {
					statusChanged = false;
					if (timeout > waitedTime) {
						synchronized(syncObject) {
							syncObject.wait(timeout - waitedTime);
						}
					}
				} catch (InterruptedException e) {
					if (!statusChanged) {
						if (stopAction) {
							stopAction = false;
							throw e;
						} else {
							e.printStackTrace();
						}
					}
				} 
				if (stopAction) {
					stopAction = false;
					throw new InterruptedException();
				}
				if (!isConnected()) {
					throw new DeviceActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				waitedTime += System.currentTimeMillis() - lastTime;
				if (statusChanged == true) {
					if ((getStatus().getStatus() & status) > 0) {
						return true;
					}
				}
			}
		} while (waitedTime < timeout);
		return false;
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.CNC_MACHINE;
	}

}
