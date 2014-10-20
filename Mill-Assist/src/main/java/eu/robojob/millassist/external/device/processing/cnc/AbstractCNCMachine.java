package eu.robojob.millassist.external.device.processing.cnc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

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
import eu.robojob.millassist.external.device.processing.cnc.mcode.MCodeAdapter;

public abstract class AbstractCNCMachine extends AbstractProcessingDevice {

	private Set<CNCMachineListener> listeners;
	private Set<CNCMachineAlarm> alarms;
	private int currentStatus;
	private boolean statusChanged;
	private static Object syncObject;
	private boolean stopAction;
	private CNCMachineAlarm cncMachineTimeout;
	private int clampingWidthR;
	private WayOfOperating wayOfOperating;
	private MCodeAdapter mCodeAdapter;
	private Map<Integer, Integer> statusMap;
	
	public enum WayOfOperating {
		START_STOP(1), M_CODES(1), M_CODES_DUAL_LOAD(2);
		
		private int nbProcesses;
		
		private WayOfOperating(int nbProcesses) {
			this.nbProcesses = nbProcesses;
		}
		
		public int getNbProcesses() {
			return this.nbProcesses;
		}
	};
	
	private static Logger logger = LogManager.getLogger(AbstractCNCMachine.class.getName());
	
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "AbstractCNCMachine.disconnectedWhileWaiting";
	private static final String EXCEPTION_WHILE_WAITING = "AbstractCNCMachine.exceptionWhileWaiting";
	
	public AbstractCNCMachine(final String name, final WayOfOperating wayOfOperating, final MCodeAdapter mCodeAdapter, final Set<Zone> zones, final int clampingWidthR) {
		super(name, zones, true);
		this.mCodeAdapter = mCodeAdapter;
		this.wayOfOperating = wayOfOperating;
		this.statusChanged = false;
		syncObject = new Object();
		this.alarms = new HashSet<CNCMachineAlarm>();
		this.currentStatus = 0;
		this.listeners = new HashSet<CNCMachineListener>();
		this.stopAction = false;
		this.clampingWidthR = clampingWidthR;
		this.statusMap = new HashMap<Integer, Integer>();
	}
	
	public AbstractCNCMachine(final String name, final WayOfOperating wayOfOperating, final MCodeAdapter mCodeAdapter, final int clampingWidthR) {
		this(name, wayOfOperating, mCodeAdapter, new HashSet<Zone>(), clampingWidthR);
	}
	
	public WayOfOperating getWayOfOperating() {
		return wayOfOperating;
	}

	public void setWayOfOperating(final WayOfOperating wayOfOperating) {
		this.wayOfOperating = wayOfOperating;
	}
	
	public MCodeAdapter getMCodeAdapter() {
		return mCodeAdapter;
	}
	
	public void setMCodeAdapter(final MCodeAdapter mCodeAdapter) {
		this.mCodeAdapter = mCodeAdapter;
	}

	public CNCMachineAlarm getCncMachineTimeout() {
		return cncMachineTimeout;
	}

	public void setCncMachineTimeout(final CNCMachineAlarm cncMachineTimeout) {
		this.cncMachineTimeout = cncMachineTimeout;
	}

	@Override
	public void interruptCurrentAction() {
		setCncMachineTimeout(null);
		stopAction = true;
		synchronized (syncObject) {
			syncObject.notifyAll();
		}
	}

	public synchronized void addListener(final CNCMachineListener listener) {
		listeners.add(listener);
		logger.debug("Now listening to [" + toString() + "]: " + listener.toString());
	}
	
	public synchronized void removeListener(final CNCMachineListener listener) {
		listeners.remove(listener);
		logger.debug("Stopped listening to [" + toString() + "]: " + listener.toString());
	}
	
	protected Set<CNCMachineListener> getCNCMachineListeners() {
		return this.listeners;
	}
	
	protected void setCNCMachineListeners(final Set<CNCMachineListener> listeners) {
		this.listeners = listeners;
	}
	
	public int getStatus() {
		return currentStatus;
	}
	
	public void setStatus(final int status) {
		this.currentStatus = status;
	}
	
	public Map<Integer,Integer> getStatusMap() {
		return statusMap;
	}
	
	public int getStatus(final int registerIndex) {
		return statusMap.get(registerIndex);
	}
	
	public void setStatus(final int status, final int registerIndex) {
		statusMap.put(registerIndex, status);
	}
	
	public void setAlarms(final Set<CNCMachineAlarm> alarms) {
		this.alarms = alarms;
	}
	
	public abstract void updateStatusAndAlarms() throws AbstractCommunicationException, InterruptedException;
	public abstract void disconnect();
	
	public Set<CNCMachineAlarm> getAlarms() {
		return alarms;
	}
	
	public synchronized void processCNCMachineEvent(final CNCMachineEvent event) {
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
					for (CNCMachineAlarm alarm : getAlarms()) {
						logger.error(alarm);
					}
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
	public abstract void nCReset() throws AbstractCommunicationException, InterruptedException, DeviceActionException;
	public abstract void powerOff() throws AbstractCommunicationException, InterruptedException;
	public abstract void indicateAllProcessed() throws AbstractCommunicationException, InterruptedException, DeviceActionException;
	public abstract void indicateOperatorRequested(boolean requested) throws AbstractCommunicationException, InterruptedException;
	public abstract void clearIndications() throws AbstractCommunicationException, InterruptedException;
	
	protected boolean waitForStatus(final int status, final long timeout) throws InterruptedException, DeviceActionException {
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ((currentStatus & status) == status);
			}
		}, timeout);
	}
	
	protected boolean waitForStatusDevIntv2(final int registerIndex, final int status, final long timeout) throws InterruptedException, DeviceActionException {
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ((statusMap.get(registerIndex) & status) == status);
			}
		}, timeout);
	}
	
	protected boolean waitForStatusGoneDevIntv2(final int registerIndex, final int status, final long timeout) throws InterruptedException, DeviceActionException {
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ((statusMap.get(registerIndex) | status) == (statusMap.get(registerIndex) ^ status));
			}
		}, timeout);
	}
	
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
					// at this point the wait is finished, either by a notify (status changed, or request to stop), or by a timeout
					if (stopAction) {
						//stopAction = false;
						throw new InterruptedException("Waiting for status got interrupted");
					}
					if (timeout > 0) {
						syncObject.wait(timeout - waitedTime);
					} else {
						syncObject.wait();
					}
				}
				// at this point the wait is finished, either by a notify (status changed, or request to stop), or by a timeout
				if (stopAction) {
					//stopAction = false;
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
	
	protected void waitForStatusDevIntv2(final int registerIndex, final int status) throws DeviceActionException, InterruptedException {
		waitForStatusDevIntv2(registerIndex, status, 0);
	}
	
	protected void waitForStatusGoneDevIntv2(final int registerIndex, final int status) throws DeviceActionException, InterruptedException {
		waitForStatusGoneDevIntv2(registerIndex, status, 0);
	}
	
	protected boolean waitForMCodes(final int... indexList) throws InterruptedException, DeviceActionException {
		String loggerString = "Waiting for M CODE: " + indexList[0];
		for (int i = 1; i < indexList.length; i++) {
			loggerString += " OR " + indexList[i];
		}
		logger.info(loggerString);
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				for (int index: indexList) {
					if (mCodeAdapter.isMCodeActive(index))
						return true;
				}
				return false;
			}
		}, 0);
	}
	
	protected boolean waitForNoMCode(final int... indexList) throws InterruptedException, DeviceActionException {
		String loggerString = "Waiting for M CODE gone: " + indexList[0];
		for (int i = 1; i < indexList.length; i++) {
			loggerString += " OR " + indexList[i];
		}
		logger.info(loggerString);
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				for (int index: indexList) {
					if (mCodeAdapter.isMCodeActive(index)) {
						return false;
					}
				}
				return true;
			}
		}, 0);
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

	public int getClampingWidthR() {
		return clampingWidthR;
	}

	public void setClampingWidthR(final int clampingWidthR) {
		this.clampingWidthR = clampingWidthR;
	}
	
	public abstract boolean isUsingNewDevInt();
	
	public abstract CNCMachineSocketCommunication getCNCMachineSocketCommunication();
	
}