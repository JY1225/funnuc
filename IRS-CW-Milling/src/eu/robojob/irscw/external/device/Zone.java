package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

public class Zone {
	
	private int id; 
	private String name;
	private List<WorkArea> workAreas;
	private AbstractDevice device;

	public Zone(final String name, final List<WorkArea> workAreas) {
		this.name = name;
		this.workAreas =  new ArrayList<WorkArea>();
		for (WorkArea workArea : workAreas) {
			addWorkArea(workArea);
		}
	}
	
	public Zone(final String name) {
		this(name, new ArrayList<WorkArea>());
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

	public List<WorkArea> getWorkAreas() {
		return workAreas;
	}
	
	public List<String> getWorkAreaNames() {
		List<String> workAreaNames = new ArrayList<String>();
		for (WorkArea workArea : workAreas) {
			workAreaNames.add(workArea.getName());
		}
		return workAreaNames;
	}

	public void setWorkAreas(final List<WorkArea> workAreas) {
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
}
