package eu.robojob.millassist.external.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the settings of one specific device in a processflow. 
 */
public class DeviceSettings {
	
	private int id;
	private Map<SimpleWorkArea, Clamping> clampings;
	
	public DeviceSettings() {
		clampings = new HashMap<SimpleWorkArea, Clamping>();
	}
	
	public DeviceSettings(final Map<SimpleWorkArea, Clamping> clampings) {
		this.clampings = clampings;
	}
	
	public DeviceSettings(final List<SimpleWorkArea> workAreas) {
		this();
		for (SimpleWorkArea workArea : workAreas) {
			clampings.put(workArea, workArea.getDefaultClamping());
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setDefaultClamping(final SimpleWorkArea workArea, final Clamping clamping) {
		clampings.put(workArea, clamping);
	}

	public Map<SimpleWorkArea, Clamping> getClampings() {
		return clampings;
	}

	public void setClampings(final Map<SimpleWorkArea, Clamping> clampings) {
		this.clampings = clampings;
	}
	
	/**
	 * Get the default clamping for the given workareaManager.
	 * 
	 * @param 	workArea
	 * @return
	 */
	public Clamping getDefaultClamping(final SimpleWorkArea workArea) {
		return clampings.get(workArea);
	}

}
