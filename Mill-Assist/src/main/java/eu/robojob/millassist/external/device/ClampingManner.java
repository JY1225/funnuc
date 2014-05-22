package eu.robojob.millassist.external.device;


public class ClampingManner {

	private boolean changed;
	//private static final Logger logger = LogManager.getLogger(ClampingManner.class.getName());
	
	public enum Type {
		LENGTH, WIDTH
	}
	
	private Type type;
	
	public ClampingManner() {
		this.type = Type.LENGTH;
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
	
}
