package eu.robojob.millassist.external.device;
//TODO - check the possibility to add a defaultName 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.positioning.UserFrame;

/**
 * This class represents a workarea manager. The workareamanager controls the usage of the clampings in a 
 * workarea taking all its clones into account.
 */
public class WorkAreaManager {
	
	private int id;
	private int workAreaNr;
	private Zone zone;
	private UserFrame userFrame;
	private WorkAreaBoundary boundaries;
	
	// maxClampsToFill is the amount of clampings chosen in the CNC configure screen * nbSides
	private int maxClampsToFill;
	private Map<Integer, SimpleWorkArea> workAreas;
	// All the possible clampings for the managed workarea
	private Set<Clamping> clampings;
	private List<Clamping> clampingsInUse;
	
	private static Logger logger = LogManager.getLogger(WorkAreaManager.class.getName());

	public WorkAreaManager(final UserFrame userFrame, final Set<Clamping> clampings) {
		this.userFrame = userFrame;
		this.clampings = clampings;
		workAreas = new HashMap<Integer, SimpleWorkArea>();
		clampingsInUse = new ArrayList<Clamping>();
		this.maxClampsToFill = 1;
		setWorkAreaNr();
	}
	
	public int getWorkAreaNr() {
		return this.workAreaNr;
	}
	
	private void setWorkAreaNr() {
		switch (getUserFrame().getNumber()) {
		case 3:
			this.workAreaNr = 1;
			break;
		case 4:
			this.workAreaNr = 2;
			break;
		default:
			this.workAreaNr = 0;
			break;
		}
	}
	
	public String getName() {
		return workAreas.get(1).getName();
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}
	
	public Zone getZone() {
		return zone;
	}

	public void setZone(final Zone zone) {
		this.zone = zone;
	}
	
	public UserFrame getUserFrame() {
		return userFrame;
	}
	
	public void setUserFrame(final UserFrame userFrame) {
		this.userFrame = userFrame;
	}
	
	public WorkAreaBoundary getBoundaries() {
		return this.boundaries;
	}

	public void setWorkAreaBoundary(final WorkAreaBoundary boundaries) {
		this.boundaries = boundaries;
	}
	
	public Set<Clamping> getClampings() {
		return clampings;
	}

	public void setClampings(final Set<Clamping> clampings) {
		this.clampings = new HashSet<Clamping>();
		for (Clamping clamping : clampings) {
			addClamping(clamping);
		}
	}
	
	public void addClamping(final Clamping clamping) {
		clampings.add(clamping);
	}
	
	public void removeClamping(final Clamping clamping) {
		clampings.remove(clamping);
	}
	
	public List<String> getClampingNames() {
		List<String> clampingNames = new ArrayList<String>();
		for (Clamping clamping: clampings) {
			clampingNames.add(clamping.getName());
		}
		return clampingNames;
	}
	
	public Clamping getClampingByName(final String name) {
		for (Clamping clamping : clampings) {
			if (clamping.getName().equals(name)) {
				return clamping;
			}
		}
		return null;
	}
	
	public Clamping getClampingById(final int id) {
		for (Clamping clamping : clampings) {
			if (clamping.getId() == id) {
				return clamping;
			}
		}
		return null;
	}
	
	public void addWorkArea(SimpleWorkArea workArea) {
		workAreas.put(workArea.getSequenceNb(), workArea);
	}	
	
	public List<String> getWorkAreaNames() {
		List<String> names = new ArrayList<String>();
		for (SimpleWorkArea workarea: workAreas.values()) {
			names.add(workarea.getName());
		}
		return names;
	}
	
	public SimpleWorkArea getWorkAreaWithSequence(int sequenceNb) {
		return workAreas.get(sequenceNb);
	}
	
	public Map<Integer, SimpleWorkArea> getWorkAreas() {
		return workAreas;
	}
	
	/**
	 * Indicate that the workarea with the given sequence number is currently in use or not in use anymore. 
	 * 
	 * @param sequenceNb
	 * @param inUseFlag
	 * @throws IllegalArgumentException - in case there is no workarea found with the given sequence number.
	 */
	public void setWorkAreaInUse(int sequenceNb, boolean inUseFlag) throws IllegalArgumentException {
		SimpleWorkArea workArea = workAreas.get(sequenceNb);
		if (workArea == null) {
			throw new IllegalArgumentException("No workarea with " + sequenceNb + " found.");
		}
		workArea.setInUse(inUseFlag);
	}
	
	/**
	 * Get the clamping first or last reserved depending on the boolean value given. In case of put,
	 * the parameter has to be false. Otherwise - in case of pick action - the parameter has to be
	 * true. When we do not find a result in the clampingsInUse list, the defaultClamping will be
	 * returned
	 * 
	 * @param fifo - boolean indicating FIFO or LIFO operation
	 * @return clampingsInUse.get(first) || clampingsInUse.get(last) depending on the boolean value
	 */
	public Clamping getActiveClamping(boolean fifo, int sequenceNb) {
		if (clampingsInUse.size() == 0) {
			return getWorkAreaWithSequence(sequenceNb).getDefaultClamping();
		} else {
			if (fifo) {
				return clampingsInUse.get(0);
			} else  {
				return clampingsInUse.get(clampingsInUse.size() - 1);
			}
		}
	}
	
	void reserveClamping(Clamping freeClamping) throws NoFreeClampingInWorkareaException {
		if (clampingsInUse.size() >= maxClampsToFill) {
			throw new NoFreeClampingInWorkareaException();
		}
		clampingsInUse.add(freeClamping);
	}
	
	/**
	 * This method will make the first clamping that was reserved back available for use
	 */
	public synchronized void freeClamping(int processId) {
		Clamping clamping = clampingsInUse.get(0);
		logger.debug("Clamping " + clamping.getName() + " in " + this.toString() +  " used by PRC[" + processId + "] freed up.");
		clamping.getProcessIdUsingClamping().remove(processId);
		clampingsInUse.remove(0);
	}
	
	/**
	 * Returns the number of clampings chosen in the CNC machine configure screen. The result takes
	 * only 1 side into account. This means that if e.g. 2 clampings are chosen, this method will 
	 * return 2 disregarding the number of load sides (e.g. dual load).
	 * 
	 * @return number of clampings chosen at configure time
	 */
	public int getNbActiveClampingsEachSide() {
		for (SimpleWorkArea workArea: workAreas.values()) {
			if (workArea.isInUse()) {
				if (workArea.getDefaultClamping() != null) {
					return ((workArea.getDefaultClamping().getRelatedClampings().size() + 1));
				}
			}
		}
		return 0;
	}
	
	/**
	 * Calculate the maximum number of clampings in use taken into account all the processExecutors
	 * that are currently waiting for actions.
	 * 
	 * @param processId - processId to exclude
	 * @return
	 */
	public int getMaxNbClampingOtherProcessThread(final int processId) {
		//is only being used for dualLoad ending check
		int maxClampings = clampingsInUse.size();
		for (SimpleWorkArea workArea: getWorkAreas().values()) {
			if (workArea.isInUse()) {
				maxClampings -= workArea.getNbClampingsPerProcessThread(processId);
			}
		}
		return maxClampings;
	}
	
	/**
	 * Check whether one of the cloned workareas managed by this WorkAreaManager is currently
	 * being used by the process.
	 * 
	 * @return	true/false
	 */
	public boolean isInUse() {
		for (SimpleWorkArea workArea: workAreas.values()) {
			if (workArea.isInUse()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sets the inUse variable of all simpleWorkArea managed by this WorkAreaManager to false.
	 */
	public void resetUse() {
		for (SimpleWorkArea workArea: workAreas.values()) {
			workArea.setInUse(false);
		}
	}
	
	/**
	 * Set the number of max clampings to use during processing. This takes the different sides into
	 * account (if their are multiple side e.g. dualLoad). 
	 * 
	 * @param nbUsedClampings = nbSides * getNbActiveClampingsEachSide
	 * @see #getNbActiveClampingsEachSide()
	 */
	public void setMaxClampingsToUse(final int nbUsedClampings) {
		this.maxClampsToFill = nbUsedClampings;
	}
	
	//this method is used every time we will start the processing. This is a reset because we do not know
	//whether we have aborted the previous run or not - so number can contain left-overs from the previous runs
	public void resetNbPossibleWPPerClamping(int nbSides) {
		clampingsInUse.clear();
		for (SimpleWorkArea workArea: getWorkAreas().values()) {
			if (workArea.isInUse()) {
				workArea.resetNbPossibleWPPerClamping(nbSides);
			}
		}
	}
	
	@Override
	public String toString() {
		return "WorkArea " + getName();
	}
	
}
