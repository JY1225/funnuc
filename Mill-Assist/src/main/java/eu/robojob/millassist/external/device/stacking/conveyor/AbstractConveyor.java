package eu.robojob.millassist.external.device.stacking.conveyor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public abstract class AbstractConveyor extends AbstractStackingDevice {

	private static Logger logger = LogManager.getLogger(AbstractConveyor.class.getName());
	private List<ConveyorListener> listeners;
	private boolean statusChanged;
	private Object syncObject;
	private boolean stopAction;
	private int currentStatus;
	private ConveyorAlarm conveyorTimeout;
	private ConveyorSocketCommunication socketCommunication;

	private Set<ConveyorAlarm> currentAlarms;
	
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "Conveyor.disconnectedWhileWaiting";
	private static final String EXCEPTION_WHILE_WAITING = "Conveyor.exceptionWhileWaiting";

	public AbstractConveyor(final String name, final Set<Zone> zones, final SocketConnection socketConnection) {
		super(name, zones);
		syncObject = new Object();
		this.currentStatus = 0;
		this.stopAction = false;
		this.statusChanged = false;
		this.listeners = new ArrayList<ConveyorListener>();
		this.currentAlarms = new HashSet<ConveyorAlarm>();
		this.socketCommunication = new ConveyorSocketCommunication(socketConnection, this);
	}
	
	public ConveyorSocketCommunication getSocketCommunication() {
		return socketCommunication;
	}
	
	public void setAlarms(final Set<ConveyorAlarm> alarms) {
		this.currentAlarms = alarms;
	}
	
	public Set<ConveyorAlarm> getAlarms() {
		return currentAlarms;
	}
	
	public ConveyorAlarm getConveyorTimeout() {
		return conveyorTimeout;
	}
	
	public void setConveyorTimeout(final ConveyorAlarm conveyorTimeout) {
		this.conveyorTimeout = conveyorTimeout;
	}
	
	@Override
	public float getZSafePlane(final WorkPieceDimensions dimensions, final SimpleWorkArea workArea, final ApproachType approachType) throws IllegalArgumentException {
		float zSafePlane = workArea.getDefaultClamping().getRelativePosition().getZ(); 
		float wpHeight = dimensions.getHeight();
		if (wpHeight > workArea.getDefaultClamping().getHeight()) {
			zSafePlane += wpHeight;
		} else {
			zSafePlane += workArea.getDefaultClamping().getHeight();
		}
		zSafePlane += dimensions.getHeight();
		return zSafePlane;
	}

	public void addListener(final ConveyorListener listener) {
		listeners.add(listener);
		logger.debug("Now listening to [" + toString() + "]: " + listener.toString());
	}
	
	public void removeListener(final ConveyorListener listener) {
		listeners.remove(listener);
		logger.debug("Stopped listening to [" + toString() + "]: " + listener.toString());
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	public List<ConveyorListener> getListeners() {
		return listeners;
	}
	
	public void notifyLayoutChanged() {
		for (ConveyorListener listener : getListeners()) {
			listener.layoutChanged();
		}
	}
	
	public void setStatus(final int status) {
		this.currentStatus = status;
	}
	
	public int getStatus() {
		return currentStatus;
	}
	
	@Override
	public boolean isConnected() {
		return getSocketCommunication().isConnected();
	}
	
	public void disconnect() {
		getSocketCommunication().disconnect();
	}
	
	public abstract void notifyFinishedShifted();
	
	public abstract void indicateAllProcessed() throws AbstractCommunicationException, InterruptedException, DeviceActionException;
	public abstract void indicateOperatorRequested(boolean requested) throws AbstractCommunicationException, InterruptedException;
	public abstract void clearIndications() throws AbstractCommunicationException, InterruptedException;
	
	protected boolean waitForStatusCondition(final Callable<Boolean> condition, final long timeout) throws InterruptedException, DeviceActionException {
		long waitedTime = 0;
		stopAction = false;
		// check status before we start
		try {
			if (condition.call()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DeviceActionException(this, EXCEPTION_WHILE_WAITING);
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
					try {
						if (condition.call()) {
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new DeviceActionException(this, EXCEPTION_WHILE_WAITING);
					}
					if (timeout > 0) {
						syncObject.wait(timeout - waitedTime);
					} else {
						syncObject.wait();
					}
				}
				// at this point the wait is finished, either by a notify (status changed, or request to stop), or by a timeout
				if (stopAction) {
					stopAction = false;
					throw new InterruptedException("Waiting for status got interrupted");
				}
				// just to be sure, check connection
				if (!isConnected()) {
					throw new DeviceActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				// check if status has changed
				try {
					if ((statusChanged) && (condition.call())) {
						statusChanged = false;
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new DeviceActionException(this, EXCEPTION_WHILE_WAITING);
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
	
	protected boolean waitForStatus(final int status, final long timeout) throws InterruptedException, DeviceActionException {
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ((currentStatus & status) == status);
			}
		}, timeout);
	}
	
	protected void waitForFirstStatus(final int status1, final int status2) throws DeviceActionException, InterruptedException {
		waitForFirstStatus(status1, status2, 0);
	}
	
	protected boolean waitForFirstStatus(final int status1, final int status2, final long timeout) throws InterruptedException, DeviceActionException {
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return (((currentStatus & status1) == status1) || ((currentStatus & status2) == status2));
			}
		}, timeout);
	}
	
	protected void waitForStatusNot(final int statusNot) throws DeviceActionException, InterruptedException {
		waitForStatusNot(statusNot, 0);
	}
	
	protected boolean waitForStatusNot(final int statusNot, final long timeout) throws InterruptedException, DeviceActionException {
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ((currentStatus & statusNot) == 0);
			}
		}, timeout);
	}
	
	/**
	 * This method will be called after processing a STATUS_CHANGED event, so if the waitForStatus method
	 * is waiting, it will be notified
	 */
	protected void statusChanged() {
		synchronized (syncObject) {
			statusChanged = true;
			syncObject.notifyAll();
		}
	}
	
	public void processConveyorEvent(final ConveyorEvent event) {
		switch(event.getId()) {
			case ConveyorEvent.ALARM_OCCURED:
				for (ConveyorListener listener : getListeners()) {
					listener.conveyorAlarmsOccured((ConveyorAlarmsOccuredEvent) event);
				}
				break;
			case ConveyorEvent.CONVEYOR_CONNECTED:
				for (ConveyorListener listener : getListeners()) {
					listener.conveyorConnected(event);
				}
				break;
			case ConveyorEvent.CONVEYOR_DISCONNECTED:
				statusChanged();
				for (ConveyorListener listener : getListeners()) {
					listener.conveyorDisconnected(event);
				}
				break;
			case ConveyorEvent.STATUS_CHANGED:
				statusChanged();
				for (ConveyorListener listener : getListeners()) {
					listener.conveyorStatusChanged(event);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown event type: " + event.getId());
		}
	}

}
