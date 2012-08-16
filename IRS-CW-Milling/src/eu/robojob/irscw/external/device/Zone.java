package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

public class Zone {
	
	private String id;
	private List<WorkArea> workAreas;
	private AbstractDevice device;

	public Zone(String id, List<WorkArea> workAreas, AbstractDevice device) {
		this.id = id;
		this.workAreas = workAreas;
		this.device = device;
		device.addZone(this);
	}
	
	public Zone(String id, AbstractDevice device) {
		this(id, new ArrayList<WorkArea>(), device);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<WorkArea> getWorkAreas() {
		return workAreas;
	}

	public void setWorkAreas(List<WorkArea> workAreas) {
		this.workAreas = workAreas;
	}
	
	public void addWorkArea(WorkArea workArea) throws IllegalArgumentException {
		if (getWorkAreaById(workArea.getId())!=null) {
			throw new IllegalArgumentException("A workArea with the same id already exists within this zone.");
		} else {
			this.workAreas.add(workArea);
		}
	}
	
	public void removeWorkArea(WorkArea workArea) {
		this.workAreas.remove(workArea);
	}
	
	public WorkArea getWorkAreaById(String id) {
		for (WorkArea workArea : workAreas) {
			if (workArea.getId().equals(id)) {
				return workArea;
			}
		}
		return null;
	}

	public AbstractDevice getDevice() {
		return device;
	}
}
