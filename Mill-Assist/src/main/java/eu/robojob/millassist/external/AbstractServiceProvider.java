package eu.robojob.millassist.external;

import eu.robojob.millassist.process.ProcessFlow;

public abstract class AbstractServiceProvider {
	
	private boolean isLocked;
	private ProcessFlow ownerProcess;
	private String name;
	private int id;
	
	public AbstractServiceProvider(final String name) {
		isLocked = false;
		ownerProcess = null;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
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
	
	public synchronized boolean release() {
		isLocked = false;
		this.ownerProcess = null;
		return true;
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

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	public abstract String toString();
}
