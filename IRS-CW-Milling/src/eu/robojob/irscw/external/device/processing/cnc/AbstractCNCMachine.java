package eu.robojob.irscw.external.device.processing.cnc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.threading.ThreadManager;

public abstract class AbstractCNCMachine extends AbstractProcessingDevice {

	private Set<CNCMachineListener> listeners;
	
	protected Set<CNCMachineAlarm> alarms;
	protected int currentStatus;
	
	private boolean statusChanged;
	private Object syncObject;
	
	private boolean stopAction;
	
	private static final Logger logger = LogManager.getLogger(AbstractCNCMachine.class.getName());
	
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "AbstractCNCMachine.disconnectedWhileWaiting";
	
	public AbstractCNCMachine(String id, List<Zone> zones) {
		super(id, zones, true);
		this.statusChanged = false;
		syncObject = new Object();
		this.alarms = new HashSet<CNCMachineAlarm>();
		this.currentStatus = 0;
		this.listeners = new HashSet<CNCMachineListener>();
		CNCMachineMonitoringThread cncMachineMonitoringThread = new CNCMachineMonitoringThread(this);
		// start monitoring thread at creation of this object
		ThreadManager.getInstance().submit(cncMachineMonitoringThread);
		this.stopAction = false;
	}
	
	public AbstractCNCMachine(String id) {
		this(id, new ArrayList<Zone>());
	}
	
	@Override
	public void interruptCurrentAction() {
		logger.debug("Interrupting current action of: " + id);
		stopAction = true;
		synchronized(syncObject) {
			syncObject.notifyAll();
		}
	}

	public void addListener(CNCMachineListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(CNCMachineListener listener) {
		listeners.remove(listener);
	}
	
	public int getStatus() {
		return currentStatus;
	}
	
	public abstract void updateStatusAndAlarms() throws DisconnectedException, ResponseTimedOutException, InterruptedException;
	public abstract void disconnect();
	
	public Set<CNCMachineAlarm> getAlarms() {
		return alarms;
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
					listener.cNCMachineAlarmsOccured((CNCMachineAlarmsOccuredEvent) event);
				}
				break;
			case CNCMachineEvent.STATUS_CHANGED : 
				statusChanged();
				for (CNCMachineListener listener : listeners) {
					listener.cNCMachineStatusChanged(event);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown event type.");
		}
	}
	
	/**
	 * This method will be called after processing a STATUS_CHANGED event, so if the waitForStatus method
	 * is waiting, it will be notified
	 */
	private void statusChanged() {
		synchronized(syncObject) {
			statusChanged = true;
			syncObject.notifyAll();
		}
	}
	
	public abstract void reset() throws ResponseTimedOutException, DisconnectedException, InterruptedException;
	public abstract void nCReset() throws ResponseTimedOutException, DisconnectedException, InterruptedException;
	public abstract void powerOff() throws ResponseTimedOutException, DisconnectedException, InterruptedException;
	public abstract void indicateAllProcessed() throws ResponseTimedOutException, DisconnectedException, InterruptedException;
	public abstract void indicateOperatorRequested(boolean requested) throws ResponseTimedOutException, DisconnectedException, InterruptedException;
	public abstract void clearIndications() throws ResponseTimedOutException, DisconnectedException, InterruptedException;
	
	protected boolean waitForStatus(int status, long timeout) throws InterruptedException, DeviceActionException {
		long waitedTime = 0;
		stopAction = false;
		// check status before we start
		if ((currentStatus & status) > 0) {
			return true;
		}
		// also check connection status
		if (!isConnected()) {
			throw new DeviceActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
		}
		while(waitedTime < timeout) {
			// start waiting
			statusChanged = false;
			if (timeout > waitedTime) {
				long timeBeforeWait = System.currentTimeMillis();
				synchronized(syncObject) {
					syncObject.wait(timeout - waitedTime);
				}
				// at this point the wait is finished, either by a notify (status changed, or request to stop), or by a timeout
				if (stopAction) {
					stopAction = false;
					throw new InterruptedException("Waiting for status: " + status + " got interrupted");
				}
				// just to be sure, check connection
				if (!isConnected()) {
					throw new DeviceActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				// check if status has changed
				if ((statusChanged == true) && ((currentStatus & status) > 0)) {
					statusChanged = false;
					return true;
				}
				// update waited time
				waitedTime += System.currentTimeMillis() - timeBeforeWait;
			} else {
				return false;
			}
		} 
		return false;
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.CNC_MACHINE;
	}

	@Override
	public String toString() {
		return "AbstractCNCMachine: " + id;
	}
}
