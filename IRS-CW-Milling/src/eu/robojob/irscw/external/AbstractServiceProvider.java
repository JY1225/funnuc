package eu.robojob.irscw.external;

import eu.robojob.irscw.process.Process;

public abstract class AbstractServiceProvider {
	
	private boolean isLocked;
	private Process ownerProcess;
	protected String id;
	
	public AbstractServiceProvider(String id) {
		isLocked = false;
		ownerProcess = null;
		this.id = id;
	}
	
	//TODO: add timer to auto-expire the lock after a certain delay
	public synchronized boolean lock(Process ownerProcess) {
		if (isLocked) {
			if (this.ownerProcess.equals(ownerProcess)) {
				return true;
			} else {
				return false;
			}
		} else {
			isLocked = true;
			this.ownerProcess = ownerProcess;
			return true;
		}
	}
	
	public synchronized boolean release(Process ownerProcess) {
		if (this.ownerProcess.equals(ownerProcess)) {
			isLocked = false;
			ownerProcess = null;
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean isLocked() {
		return isLocked;
	}
	
	public abstract String getStatus();
	
	public synchronized boolean hasLock(Process parentProcess) {
		if (this.ownerProcess.equals(parentProcess)) {
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized Process getLockingProcess() {
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
