package eu.robojob.millassist.external.device;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleWorkArea {
	
	private int id;
	private String name;
	private boolean inUse;
	private int sequenceNb;
	private WorkAreaManager workAreaManager;
	private Clamping defaultClamping;
	
	private static Logger logger = LogManager.getLogger(SimpleWorkArea.class.getName());
	
	public SimpleWorkArea(final WorkAreaManager workAreaManager, final String name, final int sequenceNb) {
		this.name = name;
		this.workAreaManager = workAreaManager;
		this.sequenceNb = sequenceNb;
		this.inUse = false;
		workAreaManager.addWorkArea(this);
	}
	
	/**
	 * Get the unique id. This id matches the one from the database.
	 * 
	 * @return	integer value with the unique database id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the unique id. The id will be used for storage and retrieval in database queries
	 * 
	 * @param id - database id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Flag indicating whether this simpleWorkArea is currently being used by the active process.
	 * 
	 * @return	true/false
	 */
	public boolean isInUse() {
		return inUse;
	}
	
	/**
	 * Set the flag to indicate whether this simpleWorkArea is being used by the active process or not
	 * 
	 * @param inUse - flag 
	 */
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
	/**
	 * Gives the sequence number of the workarea. In case the workarea is the only one for a specific device, 
	 * this method will always return 1. In other cases, e.g. where we have multiple CNC actions, cloned
	 * workarea's for the CNC machine exist. The sequence then gives the number of CNC action in the flow. 
	 * This means that in case an action is required for the first CNC action, the sequence number of the matching
	 * workarea will be 1. In case an action is required for the 2nd CNC action, the sequence number is 2; and so on.
	 * 
	 * @return sequence number starting from 1.
	 */
	public int getSequenceNb() {
		return sequenceNb;
	}
	
	/**
	 * Gives the manager of the workareas. There is exactly one manager per type of workarea. The manager
	 * can have multiple SimpleWorkAreas (all with a unique sequenceNumber).
	 * 
	 * @return
	 */
	public WorkAreaManager getWorkAreaManager() {
		return workAreaManager;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Get the default clamping for this workArea. The default clamping is set when the deviceSettings are loaded. 
	 * This occurs everytime a processflow is made or opened.
	 * 
	 * @return defaultClamping
	 */
	public Clamping getDefaultClamping() {
		if (workAreaManager.getClampings().size() == 1) {
			this.defaultClamping = workAreaManager.getClampings().iterator().next();
		}
		return defaultClamping;
	}
	
	public void setDefaultClamping(final Clamping defaultClamping) {
		this.defaultClamping = defaultClamping;
	}
	
	@Override
	public String toString() {
		return "WorkArea " + name;
	}
	
	/**
	 * Search for a clamping that is still available for use. In case a candidate is found, the processId
	 * is added to the clamping. On top of that, the clamping will be added to the inUseFIFO list. 
	 * 
	 * @param processId
	 * @throws NoFreeClampingInWorkareaException
	 */
	public synchronized void getFreeActiveClamping(int processId) throws NoFreeClampingInWorkareaException {
		Clamping freeClamping = reserveFreeActiveClampingForProcess(processId);
		getWorkAreaManager().reserveClamping(freeClamping);
	}
	
	private Clamping reserveFreeActiveClampingForProcess(int processId) throws NoFreeClampingInWorkareaException {
		if (!defaultClamping.isInUse(processId)) {
			reserveActiveClamping(defaultClamping, processId);
			return defaultClamping; 
		}
		for(Clamping clamping: getDefaultClamping().getRelatedClampings()) {
			if (!clamping.isInUse(processId)) {
				reserveActiveClamping(clamping, processId);
				return clamping;
			}
		}
		throw new NoFreeClampingInWorkareaException();
	}
	
	private synchronized void reserveActiveClamping(Clamping clamping, int processId) {
		logger.debug("Clamping "+ clamping.getName() + " in " + this.toString() +  " blocked for PRC[" + processId + "]");
		clamping.addProcessIdUsingClamping(processId);
	}
	
	/**
	 * Get all selected clampings for this specific workarea. In case of a workarea related to a CNC step, 
	 * this is the set of clampings that was selected in the CNC machine configure step.
	 * 
	 * @return set of selected clampings to use in the process
	 */
	public Set<Clamping> getAllActiveClampings() {
		Set<Clamping> resultSet = new HashSet<Clamping>();
		resultSet.add(getDefaultClamping());
		resultSet.addAll(getDefaultClamping().getRelatedClampings());
		return resultSet;
	}
	
	/**
	 * Get the number of clampings that are currently being used by the given process.
	 * 
	 * @param processId
	 * @return
	 */
	public synchronized int getNbClampingsPerProcessThread(final int processId) {
		int result = 0;
		if (defaultClamping.getProcessIdUsingClamping().contains(processId)) {
			result++;
		}
		for (Clamping relClamping: defaultClamping.getRelatedClampings()) {
			if(relClamping.getProcessIdUsingClamping().contains(processId)) {
				result++;
			}
		}
		return result;
	}
	
	//this method is used every time we will start the processing. This is a reset because we do not know
	//whether we have aborted the previous run or not - so number can contain left-overs from the previous runs
	void resetNbPossibleWPPerClamping(int nbSides) {
		defaultClamping.setNbPossibleWPToStore(nbSides);
		defaultClamping.getProcessIdUsingClamping().clear();
		for (Clamping relClamping: defaultClamping.getRelatedClampings()) {
			relClamping.setNbPossibleWPToStore(nbSides);
			relClamping.getProcessIdUsingClamping().clear();
		}
	}
}
