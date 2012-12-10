package eu.robojob.irscw.external.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceSettings {

	private Map<WorkArea, Clamping> clampings;
	
	public DeviceSettings() {
		clampings = new HashMap<WorkArea, Clamping>();
	}
	
	public DeviceSettings(List<WorkArea> workAreas) {
		this();
		for (WorkArea workArea : workAreas) {
			clampings.put(workArea, workArea.getActiveClamping());
		}
	}
	
	public void setClamping(WorkArea workArea, Clamping clamping) {
		clampings.put(workArea, clamping);
	}

	public Map<WorkArea, Clamping> getClampings() {
		return clampings;
	}

	public void setClampings(Map<WorkArea, Clamping> clampings) {
		this.clampings = clampings;
	}
	
	public Clamping getClamping(WorkArea workArea) {
		return clampings.get(workArea);
	}

}
