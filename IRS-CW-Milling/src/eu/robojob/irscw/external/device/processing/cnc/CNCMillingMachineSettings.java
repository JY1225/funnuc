package eu.robojob.irscw.external.device.processing.cnc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.WorkArea;

public class CNCMillingMachineSettings extends DeviceSettings {

	private Map<WorkArea, Clamping> clampings;
	
	public CNCMillingMachineSettings() {
		clampings = new HashMap<WorkArea, Clamping>();
	}
	
	public CNCMillingMachineSettings(List<WorkArea> workAreas) {
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