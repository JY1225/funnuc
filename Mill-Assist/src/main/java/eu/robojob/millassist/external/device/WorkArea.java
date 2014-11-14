package eu.robojob.millassist.external.device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.positioning.UserFrame;

public class WorkArea {
	
	private int id;
	// Default - WA 0
	private int workAreaNr = 0;
	private String name;
	private Zone zone;
	private UserFrame userFrame;
	private Clamping defaultClamping;
	private Set<Clamping> clampings;
	// This list contains the clampings that are currently being used by the process
	private List<Clamping> clampingsInUse;
	private int clampingsInUseCount;
	private boolean inUse;
	// Boundary of the workarea - square (lower left corner/upper right corner)
	private AirblowSquare boundaries;
	
	private static Logger logger = LogManager.getLogger(WorkArea.class.getName());
	
	public WorkArea(final String name, final UserFrame userFrame, final Clamping defaultClamping, final Set<Clamping> clampings) {
		this.name = name;
		this.userFrame = userFrame;
		this.defaultClamping = defaultClamping;
		this.clampings = clampings;
		this.clampingsInUse = new ArrayList<Clamping>();
		if(userFrame.getNumber() == 3) {
			this.workAreaNr = 1;
		} else if (userFrame.getNumber() == 4) {
			this.workAreaNr = 2;
		}
		this.clampingsInUseCount = 1;
	}
	
	public WorkArea(final String name, final UserFrame userFrame, final Set<Clamping> clampings) {
		this(name, userFrame, null, clampings);
	}
	
	public WorkArea(final String name, final UserFrame userFrame) {
		this(name, userFrame, null, new HashSet<Clamping>());
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
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
	
	public void setNbUsedClampings(final int nbUsedClampings) {
		this.clampingsInUseCount = nbUsedClampings;
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
	
	/**
	 * Calculate the maximum number of clampings in use taken into account all the processExecutors
	 * that are currently waiting for actions.
	 * 
	 * @param processId - processId to exclude
	 * @return
	 */
	public int getMaxNbClampingOtherProcessThread(final int processId) {
		//is only being used for dualLoad ending check
		int maxClampings = 0;
		for (int i = 0; i <= 2; i++) {
			if (i != processId) {
				int tmp = getNbClampingsPerProcessThread(i);
				if (tmp > maxClampings) {
					maxClampings = tmp;
				}
			}
		}
		return maxClampings;
	}
	
	public Clamping getDefaultClamping() {
		return defaultClamping;
	}
	
	public Set<Clamping> getAllActiveClampings() {
		Set<Clamping> resultSet = new HashSet<Clamping>();
		resultSet.add(getDefaultClamping());
		resultSet.addAll(getDefaultClamping().getRelatedClampings());
		return resultSet;
	}
	
	public void setDefaultClamping(final Clamping defaultClamping) {
		this.defaultClamping = defaultClamping;
	}
	
	public boolean inUse() {
		return this.inUse;
	}
	
	public void inUse(boolean inUse) {
		this.inUse = inUse;
	}
	
	/**
	 * Search for a clamping that is still available for use. In case a candidate is found, the processId
	 * is added to the clamping. On top of that, the clamping will be added to the inUseFIFO list. 
	 * 
	 * @param processId
	 * @throws NoFreeClampingInWorkareaException
	 */
	public synchronized void getFreeActiveClamping(int processId) throws NoFreeClampingInWorkareaException{
		Clamping freeClamping = reserveFreeActiveClampingForProcess(processId);
		if (clampingsInUse.size() >= clampingsInUseCount) {
			throw new NoFreeClampingInWorkareaException();
		}
		clampingsInUse.add(freeClamping);
	}
	
	private Clamping reserveFreeActiveClampingForProcess(int processId) throws NoFreeClampingInWorkareaException {
		if (!zone.clampingInUse(defaultClamping, processId)) {
			reserveActiveClamping(defaultClamping, processId);
			return defaultClamping; 
		}
		for(Clamping clamping: getDefaultClamping().getRelatedClampings()) {
			if (!zone.clampingInUse(clamping, processId)) {
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
		if(getDefaultClamping() != null) {
			return ((getDefaultClamping().getRelatedClampings().size() + 1));
		}
		return 0;
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
	public Clamping getActiveClamping(boolean fifo) {
		if (clampingsInUse.size() == 0) {
			return getDefaultClamping();
		} else {
			if (fifo) {
				return clampingsInUse.get(0);
			} else  {
				return clampingsInUse.get(clampingsInUse.size() - 1);
			}
		}
	}
	
	// TODO - review - moet niet gebeuren na teaching
	public void resetNbPossibleWPPerClamping(int nbSides) {
		clampingsInUse.clear();
		defaultClamping.setNbPossibleWPToStore(nbSides);
		defaultClamping.getProcessIdUsingClamping().clear();
		for (Clamping relClamping: defaultClamping.getRelatedClampings()) {
			relClamping.setNbPossibleWPToStore(nbSides);
			relClamping.getProcessIdUsingClamping().clear();
		}
	}
	
	public String toString() {
		return "WorkArea " + name;
	}
	
	public int getWorkAreaNr() {
		return this.workAreaNr;
	}
	
	public AirblowSquare getBoundaries() {
		return this.boundaries;
	}

	public void setBoundary(AirblowSquare boundaries) {
		this.boundaries = boundaries;
	}
}