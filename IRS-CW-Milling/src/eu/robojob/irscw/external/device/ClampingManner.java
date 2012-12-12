package eu.robojob.irscw.external.device;

public class ClampingManner {

	public enum Type {
		LENGTH, WIDTH
	}
	
	private Type type;
	
	public ClampingManner() {
		this.type = Type.LENGTH;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}
	
}
