package eu.robojob.irscw.external.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceSettings {

	//TODO add ProcessFlow
	private Map<WorkArea, Clamping> clampings;
	
	public DeviceSettings() {
		clampings = new HashMap<WorkArea, Clamping>();
	}
	
	public DeviceSettings(final List<WorkArea> workAreas) {
		this();
		for (WorkArea workArea : workAreas) {
			clampings.put(workArea, workArea.getActiveClamping());
		}
	}
	
	public void setClamping(final WorkArea workArea, final Clamping clamping) {
		clampings.put(workArea, clamping);
	}

	public Map<WorkArea, Clamping> getClampings() {
		return clampings;
	}

	public void setClampings(final Map<WorkArea, Clamping> clampings) {
		this.clampings = clampings;
	}
	
	public Clamping getClamping(final WorkArea workArea) {
		return clampings.get(workArea);
	}

}
