package eu.robojob.millassist.external.device;

import eu.robojob.millassist.util.PropertyManager;
import eu.robojob.millassist.util.PropertyManager.Setting;

public class ClampingManner {

	private ClampingMannerAllowed clampingMannerAllowed;
	private boolean changed;
	//private static final Logger logger = LogManager.getLogger(ClampingManner.class.getName());
	
	public enum Type {
		LENGTH, WIDTH
	}
	
	public enum ClampingMannerAllowed {
		LENGTH, WIDTH, FREE;
	}
	
	private Type type;
	
	public ClampingManner() {
		checkClampingMannerAllowed();
		if (clampingMannerAllowed.equals(ClampingMannerAllowed.FREE) || clampingMannerAllowed.equals(ClampingMannerAllowed.LENGTH)) {
			this.type = Type.LENGTH;
		} else {
			this.type = Type.WIDTH;
		}
		setChanged(false);
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {

		this.changed = changed;
		if (clampingMannerAllowed.equals(ClampingMannerAllowed.LENGTH)) {
			if (!changed) {
				setType(Type.LENGTH);
			} else{
				setType(Type.WIDTH);
			}
		} else if (clampingMannerAllowed.equals(ClampingMannerAllowed.WIDTH)) {
			if (!changed) {
				setType(Type.WIDTH);
			} else{
				setType(Type.LENGTH);
			}
		}

	}
	
	public void setClampingMannerAllowed(ClampingMannerAllowed allowed) {
		this.clampingMannerAllowed = allowed;
	}
	
	public ClampingMannerAllowed getClampingMannerAllowed() {
		return this.clampingMannerAllowed;
	}
	
	private void checkClampingMannerAllowed() {	
		if (PropertyManager.hasSettingValue(Setting.CLAMPING_MANNER_ALLOWED, "length")) {
			setClampingMannerAllowed(ClampingMannerAllowed.LENGTH);
		} else if(PropertyManager.hasSettingValue(Setting.CLAMPING_MANNER_ALLOWED, "width")) {
			setClampingMannerAllowed(ClampingMannerAllowed.WIDTH);
		} else {
			setClampingMannerAllowed(ClampingMannerAllowed.FREE);
		}
	}
	
}
