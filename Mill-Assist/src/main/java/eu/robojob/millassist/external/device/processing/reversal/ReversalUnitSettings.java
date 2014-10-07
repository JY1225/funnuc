package eu.robojob.millassist.external.device.processing.reversal;

import eu.robojob.millassist.external.device.DeviceSettings;

public class ReversalUnitSettings extends DeviceSettings {
	
	private float configWidth;
	private LoadType putType;
	private LoadType pickType;
	
	public enum LoadType {
		FRONT(0), TOP(1), BOTTOM(2);
		
		private int id;
		
		private LoadType(final int id) {
			this.id = id;
		}
		
		public int getId() {
			return this.id;
		}
		
		public static LoadType getById(int id) {
		    for(LoadType type : values()) {
		        if(type.id == id) 
		        	return type;
		    }
		    return null;
		 }	
	}
	
	public ReversalUnitSettings() {
		this(LoadType.TOP, LoadType.BOTTOM, 0.0f);
	}

	public ReversalUnitSettings(final LoadType putType, final LoadType pickType, final float configWidth) {
		this.configWidth = configWidth;
		this.putType = putType;
		this.pickType = pickType;
	}
	
	public LoadType getPutType() {
		return this.putType;
	}
	
	public void setPutType(LoadType loadType) {
		this.putType = loadType;
	}
	
	public LoadType getPickType() {
		return this.pickType;
	}
	
	public void setPickType(LoadType loadType) {
		this.pickType = loadType;
	}
	
	public float getConfigWidth() {
		return this.configWidth;
	}
	
	public void setConfigWidth(final float configWidth) {
		this.configWidth = configWidth;
	}

}
