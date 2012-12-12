package eu.robojob.irscw.external;

import eu.robojob.irscw.process.ProcessFlow;

public abstract class AbstractServiceProvider {
	
	private boolean isLocked;
	private ProcessFlow ownerProcess;
	private String id;
	
	public AbstractServiceProvider(final String id) {
		isLocked = false;
		ownerProcess = null;
		this.id = id;
	}
	
	//TODO: add timer to auto-expire the lock after a certain delay
	public synchronized boolean lock(final ProcessFlow ownerProcess) {
		if (isLocked) {
			if (this.ownerProcess.equals(ownerProcess)) {
				return true;
			}
			return false;
		} else {
			isLocked = true;
			this.ownerProcess = ownerProcess;
			return true;
		}
	}
	
	public synchronized boolean release(final ProcessFlow ownerProcess) {
		if (this.ownerProcess.equals(ownerProcess)) {
			isLocked = false;
			this.ownerProcess = null;
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean isLocked() {
		return isLocked;
	}
		
	public synchronized boolean hasLock(final ProcessFlow parentProcess) {
		if (this.ownerProcess.equals(parentProcess)) {
			return true;
		} 
		return false;
	}
	
	public synchronized ProcessFlow getLockingProcess() {
		return ownerProcess;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}
	
	public abstract String toString();
}
