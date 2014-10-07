package eu.robojob.millassist.workpiece;


public class WorkPiece {

	public enum Type {
		RAW(1), FINISHED(2), HALF_FINISHED(3);
		
		private int id;
		
		private Type(int id) {
			this.id = id;
		}
		
		public int getTypeId() {
			return this.id;
		}
		
		public static Type getTypeById(int id) throws IllegalStateException {
			for (Type workPieceType: Type.values()) {
				if (workPieceType.getTypeId() == id) {
					return workPieceType;
				}
			}
			throw new IllegalStateException("Unknown workpiece type: [" + id + "].");
		}
	}
	
	public enum Material {
		AL, CU, FE, OTHER
	}
	
	private static final float AL_DENSITY = 0.000002702f; 
	private static final float CU_DENSITY = 0.00000896f;
	private static final float FE_DENSITY = 0.00000786f;
	
	private int id;
	
	private Type type;
	private WorkPieceDimensions dimensions;
	private Material material;
	private float weight;
	
	public WorkPiece(final Type type, final WorkPieceDimensions dimensions, final Material material, final float weight) {
		this.type = type;
		this.dimensions = dimensions;
		this.material = material;
		this.weight = weight;
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public WorkPiece(final WorkPiece wp) {
		this.type = wp.getType();
		this.dimensions = new WorkPieceDimensions(wp.getDimensions().getLength(), wp.getDimensions().getWidth(), wp.getDimensions().getHeight());
		this.material = wp.getMaterial();
		this.weight = wp.getWeight();
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public WorkPieceDimensions getDimensions() {
		return dimensions;
	}

	public void setDimensions(final WorkPieceDimensions dimensions) {
		this.dimensions = dimensions;
	}
	
	public Material getMaterial() {
		return material;
	}

	public void setMaterial(final Material material) {
		this.material = material;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(final float weight) {
		this.weight = weight;
	}

	public void calculateWeight() {
		if (material.equals(Material.OTHER)) {
			throw new IllegalStateException("Can't calculate weight: unknown material type.");
		} else if (material.equals(Material.AL)) {
			setWeight(getDimensions().getVolume() * AL_DENSITY);
		} else if (material.equals(Material.CU)) {
			setWeight(getDimensions().getVolume() * CU_DENSITY);
		} else if (material.equals(Material.FE)) {
			setWeight(getDimensions().getVolume() * FE_DENSITY);
		}
	}
	
	public String toString() {
		return "WorkPiece: " + type + " - " + dimensions;
	}
	
	@Override
	public WorkPiece clone() {
		return new WorkPiece(this);
	}
}
