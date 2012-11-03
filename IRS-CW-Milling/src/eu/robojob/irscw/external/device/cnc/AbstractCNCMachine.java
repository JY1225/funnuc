package eu.robojob.irscw.external.device.cnc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.threading.ThreadManager;


public abstract class AbstractCNCMachine extends AbstractProcessingDevice {

	private Set<CNCMachineListener> listeners;
	
	protected Set<CNCMachineAlarm> alarms;
	protected CNCMachineStatus status;
	
	private boolean statusChanged;
	private Object syncObject;
	
	public AbstractCNCMachine(String id) {
		super(id, true);
		this.statusChanged = false;
		syncObject = new Object();
		this.alarms = new HashSet<CNCMachineAlarm>();
		this.status = new CNCMachineStatus(0);
		this.listeners = new HashSet<CNCMachineListener>();
		CNCMachineMonitoringThread cncMachineMonitoringThread = new CNCMachineMonitoringThread(this);
		ThreadManager.getInstance().submit(cncMachineMonitoringThread);
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
	}

	public void addListener(CNCMachineListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(CNCMachineListener listener) {
		listeners.remove(listener);
	}
	
	public CNCMachineStatus getStatus() {
		return status;
	}
	
	public abstract void updateStatusAndAlarms() throws CommunicationException;
	
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
	
	protected boolean waitForStatus(int status, long timeout) {
		long waitedTime = 0;
		do {
			long lastTime = System.currentTimeMillis();
			if ((getStatus().getStatus() & status) > 0) {
				return true;
			} else {
				try {
					statusChanged = false;
					if (timeout > waitedTime) {
						synchronized(syncObject) {
							syncObject.wait(timeout - waitedTime);
						}
					}
				} catch (InterruptedException e) {
					if (!statusChanged) {
						break;
					}
				} 
				if (statusChanged == true) {
					waitedTime += System.currentTimeMillis() - lastTime;
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

	public abstract static class AbstractCNCMachinePutSettings extends AbstractProcessingDevicePutSettings{
		public AbstractCNCMachinePutSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public abstract static class AbstractCNCMachinePickSettings extends AbstractProcessingDevicePickSettings{
		public AbstractCNCMachinePickSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public abstract static class AbstractCNCMachineInterventionSettings extends AbstractProcessingDeviceInterventionSettings{
		public AbstractCNCMachineInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public abstract static class AbstractCNCMachineStartCyclusSettings extends AbstractProcessingDeviceStartCyclusSettings {
		public AbstractCNCMachineStartCyclusSettings(WorkArea workArea) {
			super(workArea);
		}
	}
}
