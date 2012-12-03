package eu.robojob.irscw.external;

import eu.robojob.irscw.process.ProcessFlow;

public abstract class AbstractServiceProvider {
	
	public static final String DISCONNECTED = "DISCONNECTED";
	public static final String CONNECTED = "CONNECTED";
	
	private boolean isLocked;
	private ProcessFlow ownerProcess;
	protected String id;
	
	public AbstractServiceProvider(String id) {
		isLocked = false;
		ownerProcess = null;
		this.id = id;
	}
	
	//TODO: add timer to auto-expire the lock after a certain delay
	public synchronized boolean lock(ProcessFlow ownerProcess) {
		/*if (isLocked) {
			if (this.ownerProcess.equals(ownerProcess)) {
				return true;
			} else {
				return false;
			}
		} else {
			isLocked = true;
			this.ownerProcess = ownerProcess;
			return true;
		}*/
		return true;
	}
	
	public synchronized boolean release(ProcessFlow ownerProcess) {
		/*if (this.ownerProcess.equals(ownerProcess)) {
			isLocked = false;
			ownerProcess = null;
			return true;
		} else {
			return false;
		}*/
		return true;
	}
	
	public synchronized boolean isLocked() {
		return isLocked;
	}
		
	public synchronized boolean hasLock(ProcessFlow parentProcess) {
		/*if (this.ownerProcess.equals(parentProcess)) {
			return true;
		} else {
			return false;
		}*/
		return false;
	}
	
	public synchronized ProcessFlow getLockingProcess() {
		return ownerProcess;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public abstract String toString();
}
