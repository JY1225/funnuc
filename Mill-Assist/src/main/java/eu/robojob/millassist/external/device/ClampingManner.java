package eu.robojob.millassist.external.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
		this.changed = false;
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
	}
	
	public void setClampingMannerAllowed(ClampingMannerAllowed allowed) {
		this.clampingMannerAllowed = allowed;
	}
	
	public ClampingMannerAllowed getClampingMannerAllowed() {
		return this.clampingMannerAllowed;
	}
	
	private void checkClampingMannerAllowed() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("settings.properties")));
			if ((properties.get("clamping-manner-allowed") != null) && (properties.get("clamping-manner-allowed").equals("length"))) {
				setClampingMannerAllowed(ClampingMannerAllowed.LENGTH);
			} else if ((properties.get("clamping-manner-allowed") != null) && (properties.get("clamping-manner-allowed").equals("width"))) {
				setClampingMannerAllowed(ClampingMannerAllowed.WIDTH);
			} else {
				setClampingMannerAllowed(ClampingMannerAllowed.FREE);
			}
		} catch (IOException e) {
		}
	}
	
}
