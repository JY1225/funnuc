package eu.robojob.millassist.external.device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Zone {
	
	private int id;
	private int zoneNr;
	private String name;
	private Set<WorkAreaManager> workAreaManagers;
	private AbstractDevice device;

	public Zone(final String name, final Set<WorkAreaManager> workAreaManagers, final int zoneNr) {
		this.name = name;
		this.workAreaManagers =  new HashSet<WorkAreaManager>();
		for (WorkAreaManager workAreaManager : workAreaManagers) {
			addWorkArea(workAreaManager);
		}
		this.zoneNr = zoneNr;
	}
	
	public Zone(final String name) {
		this(name, new HashSet<WorkAreaManager>(), 0);
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

	public Set<WorkAreaManager> getWorkAreaManagers() {
		return workAreaManagers;
	}
	
	public List<String> getWorkAreaNames() {
		List<String> workAreaNames = new ArrayList<String>();
		for (WorkAreaManager workAreaManager : workAreaManagers) {
			workAreaNames.addAll(workAreaManager.getWorkAreaNames());
		}
		return workAreaNames;
	}

	public void setWorkAreas(final Set<WorkAreaManager> workAreas) {
		this.workAreaManagers = workAreas;
	}
	
	public void addWorkArea(final WorkAreaManager workArea) {
		if (getWorkAreaByName(workArea.getName()) != null) {
			throw new IllegalArgumentException("A workArea with the same id already exists within this zone.");
		} else {
			this.workAreaManagers.add(workArea);
			workArea.setZone(this);
		}
	}
	
	public void removeWorkArea(final WorkAreaManager workArea) {
		this.workAreaManagers.remove(workArea);
	}
	
	public WorkAreaManager getWorkAreaByName(final String name) {
		for (WorkAreaManager workArea : workAreaManagers) {
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
		for (WorkAreaManager workAreaManager: workAreaManagers) {
			for (SimpleWorkArea workArea: workAreaManager.getWorkAreas().values()) {
				if (workArea.isInUse()) {
					if (nbClampingsChosen == -1) {
						nbClampingsChosen = workArea.getAllActiveClampings().size();
					} else if (workArea.getAllActiveClampings().size() != nbClampingsChosen) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
}