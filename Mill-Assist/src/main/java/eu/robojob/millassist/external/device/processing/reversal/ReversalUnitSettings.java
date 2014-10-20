package eu.robojob.millassist.external.device.processing.reversal;

import eu.robojob.millassist.external.device.DeviceSettings;

public class ReversalUnitSettings extends DeviceSettings {
	
	private float configWidth;
	
	public ReversalUnitSettings() {
		this(0.0f);
	}

	public ReversalUnitSettings(final float configWidth) {
		this.configWidth = configWidth;
	}
	
	public float getConfigWidth() {
		return this.configWidth;
	}
	
	public void setConfigWidth(final float configWidth) {
		this.configWidth = configWidth;
	}

}
