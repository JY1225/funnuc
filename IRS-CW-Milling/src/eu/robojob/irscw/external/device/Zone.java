package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

public class Zone {
	
	private String id;
	private List<WorkArea> workAreas;
	private AbstractDevice device;

	public Zone(String id, List<WorkArea> workAreas) {
		this.id = id;
		this.workAreas =  new ArrayList<WorkArea>();
		for (WorkArea workArea : workAreas) {
			addWorkArea(workArea);
		}
	}
	
	public Zone(String id) {
		this(id, new ArrayList<WorkArea>());
	}
	
	public void setDevice(AbstractDevice device) {
		this.device = device;
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
	
	public List<String> getWorkAreaIds() {
		List<String> workAreaIds = new ArrayList<String>();
		for (WorkArea workArea : workAreas) {
			workAreaIds.add(workArea.getId());
		}
		return workAreaIds;
	}

	public void setWorkAreas(List<WorkArea> workAreas) {
		this.workAreas = workAreas;
	}
	
	public void addWorkArea(WorkArea workArea) throws IllegalArgumentException {
		if (getWorkAreaById(workArea.getId())!=null) {
			throw new IllegalArgumentException("A workArea with the same id already exists within this zone.");
		} else {
			this.workAreas.add(workArea);
			workArea.setZone(this);
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
