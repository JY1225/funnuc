package eu.robojob.millassist.external.device.processing.reversal;

import eu.robojob.millassist.external.device.DeviceSettings;

public class ReversalUnitSettings extends DeviceSettings {
	
	private float configWidth;
	private boolean isShiftedOrigin;
	
	public ReversalUnitSettings() {
		this(0.0f, false);
	}

	public ReversalUnitSettings(final float configWidth, final boolean isShiftedOrigin) {
		this.configWidth = configWidth;
		this.isShiftedOrigin= isShiftedOrigin;
	}
	
	public float getConfigWidth() {
		return this.configWidth;
	}
	
	public void setConfigWidth(final float configWidth) {
		this.configWidth = configWidth;
	}
	
	public boolean isShiftedOrigin() {
		return this.isShiftedOrigin;
	}
	
	public void setShiftedOrigin(final boolean isShiftedOrigin) {
		this.isShiftedOrigin = isShiftedOrigin;
	}

}
