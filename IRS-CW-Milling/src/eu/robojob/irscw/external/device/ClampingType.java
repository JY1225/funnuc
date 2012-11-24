package eu.robojob.irscw.external.device;

public class ClampingType {

	public enum Type {
		LENGTH, WIDTH
	}
	
	private Type type;
	
	public ClampingType() {
		this.type = Type.LENGTH;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
}
