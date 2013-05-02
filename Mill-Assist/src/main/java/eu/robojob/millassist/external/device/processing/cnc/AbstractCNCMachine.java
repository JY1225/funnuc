package eu.robojob.millassist.external.device.processing.cnc;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.DeviceType;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;

public abstract class AbstractCNCMachine extends AbstractProcessingDevice {

	private Set<CNCMachineListener> listeners;
	
	private Set<CNCMachineAlarm> alarms;
	private int currentStatus;
	
	private boolean statusChanged;
	private Object syncObject;
	
	private boolean stopAction;
	
	private CNCMachineAlarm cncMachineTimeout;

	private float clampingWidthR;

	private float clampingLengthR;
	
	private static Logger logger = LogManager.getLogger(AbstractCNCMachine.class.getName());
	
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "AbstractCNCMachine.disconnectedWhileWaiting";
	
	public AbstractCNCMachine(final String name, final Set<Zone> zones, final float clampingLengthR, final float clampingWidthR) {
		super(name, zones, true);
		this.statusChanged = false;
		syncObject = new Object();
		this.alarms = new HashSet<CNCMachineAlarm>();
		this.currentStatus = 0;
		this.listeners = new HashSet<CNCMachineListener>();
		this.stopAction = false;
		this.clampingLengthR = clampingLengthR;
		this.clampingWidthR = clampingWidthR;
	}
	
	public AbstractCNCMachine(final String name, final float clampingLengthR, final float clampingWidthR) {
		this(name, new HashSet<Zone>(), clampingLengthR, clampingWidthR);
	}
	
	public CNCMachineAlarm getCncMachineTimeout() {
		return cncMachineTimeout;
	}

	public void setCncMachineTimeout(final CNCMachineAlarm cncMachineTimeout) {
		this.cncMachineTimeout = cncMachineTimeout;
	}

	@Override
	public void interruptCurrentAction() {
		logger.debug("Interrupting current action of: " + getName());
		stopAction = true;
		synchronized (syncObject) {
			syncObject.notifyAll();
		}
	}

	public void addListener(final CNCMachineListener listener) {
		listeners.add(listener);
		logger.debug("Now listening to [" + toString() + "]: " + listener.toString());
	}
	
	public void removeListener(final CNCMachineListener listener) {
		listeners.remove(listener);
		logger.debug("Stopped listening to [" + toString() + "]: " + listener.toString());
	}
	
	public int getStatus() {
		return currentStatus;
	}
	
	public void setStatus(final int status) {
		this.currentStatus = status;
	}
	
	public void setAlarms(final Set<CNCMachineAlarm> alarms) {
		this.alarms = alarms;
	}
	
	public abstract void updateStatusAndAlarms() throws AbstractCommunicationException, InterruptedException;
	public abstract void disconnect();
	
	public Set<CNCMachineAlarm> getAlarms() {
		return alarms;
	}
	
	public void processCNCMachineEvent(final CNCMachineEvent event) {
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
		synchronized (syncObject) {
			statusChanged = true;
			syncObject.notifyAll();
		}
	}
	
	public abstract void reset() throws AbstractCommunicationException, InterruptedException;
	public abstract void nCReset() throws AbstractCommunicationException, InterruptedException;
	public abstract void powerOff() throws AbstractCommunicationException, InterruptedException;
	public abstract void indicateAllProcessed() throws AbstractCommunicationException, InterruptedException;
	public abstract void indicateOperatorRequested(boolean requested) throws AbstractCommunicationException, InterruptedException;
	public abstract void clearIndications() throws AbstractCommunicationException, InterruptedException;
	
	protected boolean waitForStatus(final int status, final long timeout) throws InterruptedException, DeviceActionException {
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
		while ((timeout == 0) || ((timeout > 0) && (waitedTime < timeout))) {
			// start waiting
			statusChanged = false;
			if ((timeout == 0) || ((timeout > 0) && (timeout > waitedTime))) {
				long timeBeforeWait = System.currentTimeMillis();
				synchronized (syncObject) {
					if (timeout > 0) {
						syncObject.wait(timeout - waitedTime);
					} else {
						syncObject.wait();
					}
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
				if ((statusChanged) && ((currentStatus & status) > 0)) {
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
	
	protected void waitForStatus(final int status) throws DeviceActionException, InterruptedException {
		waitForStatus(status, 0);
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.CNC_MACHINE;
	}

	@Override
	public String toString() {
		return "AbstractCNCMachine: " + getName();
	}
	
	@Override
	public void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			getWorkAreaByName(entry.getKey().getName()).setActiveClamping(entry.getValue());
		}
	}

	@Override
	public DeviceSettings getDeviceSettings() {
		return new DeviceSettings(getWorkAreas());
	}

	public float getClampingWidthR() {
		return clampingWidthR;
	}

	public void setClampingWidthR(final float clampingWidthR) {
		this.clampingWidthR = clampingWidthR;
	}

	public float getClampingLengthR() {
		return clampingLengthR;
	}

	public void setClampingLengthR(final float clampingLengthR) {
		this.clampingLengthR = clampingLengthR;
	}
}
