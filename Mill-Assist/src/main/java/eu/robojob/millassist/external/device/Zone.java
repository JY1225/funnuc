package eu.robojob.millassist.external.device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.robojob.millassist.external.robot.AirblowSquare;

public class Zone {
	
	private int id;
	private int zoneNr;
	private String name;
	private Set<WorkArea> workAreas;
	private AbstractDevice device;
	// Boundary of the zone - square (lower left corner/upper right corner)
	private AirblowSquare boundaries;

	public Zone(final String name, final Set<WorkArea> workAreas, final int zoneNr) {
		this.name = name;
		this.workAreas =  new HashSet<WorkArea>();
		for (WorkArea workArea : workAreas) {
			addWorkArea(workArea);
		}
		this.zoneNr = zoneNr;
	}
	
	public Zone(final String name) {
		this(name, new HashSet<WorkArea>(), 0);
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setDevice(final AbstractDevice device) {
		this.device = device;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Set<WorkArea> getWorkAreas() {
		return workAreas;
	}
	
	public List<String> getWorkAreaNames() {
		List<String> workAreaNames = new ArrayList<String>();
		for (WorkArea workArea : workAreas) {
			workAreaNames.add(workArea.getName());
		}
		return workAreaNames;
	}

	public void setWorkAreas(final Set<WorkArea> workAreas) {
		this.workAreas = workAreas;
	}
	
	public void addWorkArea(final WorkArea workArea) {
		if (getWorkAreaByName(workArea.getName()) != null) {
			throw new IllegalArgumentException("A workArea with the same id already exists within this zone.");
		} else {
			this.workAreas.add(workArea);
			workArea.setZone(this);
		}
	}
	
	public void removeWorkArea(final WorkArea workArea) {
		this.workAreas.remove(workArea);
	}
	
	public WorkArea getWorkAreaByName(final String name) {
		for (WorkArea workArea : workAreas) {
			if (workArea.getName().equals(name)) {
				return workArea;
			}
		}
		return null;
	}

	public AbstractDevice getDevice() {
		return device;
	}
	
	public int getZoneNr() {
		return this.zoneNr;
	}
	
	/**
	 * Check whether all workarea's in the zone have the same number of clampings selected
	 * 
	 * @return true in case all workarea's have the same amount of clampings selected. False otherwise
	 */
	public boolean clampingSelectionCorrect() {
		int nbClampingsChosen = -1;
		for (WorkArea workArea: workAreas) {
			if (workArea.inUse()) {
				if (nbClampingsChosen == -1) {
					nbClampingsChosen = workArea.getNbActiveClampingsEachSide();
				} else if (workArea.getNbActiveClampingsEachSide() != nbClampingsChosen) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Check whether the clamping that we want to use is not reserved by another workArea. This can be 
	 * the case if we work with a reversalUnit. Because 2 CNC machines are created which are physically
	 * the same machine, the clampings of the two machines needs to be considered as one.
	 * 
	 * @param currentWorkArea
	 * @param clamping
	 * @param processId
	 * @return
	 */
	public boolean clampingInUse(Clamping clamping, int processId) {
		for (WorkArea workArea: workAreas) {
			for (Clamping tmpClamp: workArea.getAllActiveClampings()) {
				if (tmpClamp.getId() == clamping.getId() && tmpClamp.isInUse(processId)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public AirblowSquare getBoundaries() {
		return this.boundaries;
	}

	public void setBoundary(AirblowSquare boundaries) {
		this.boundaries = boundaries;
	}
}