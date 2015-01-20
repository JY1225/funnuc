package eu.robojob.millassist.workpiece;


public class WorkPiece {

	public enum Type {
		RAW(1), HALF_FINISHED(2), FINISHED(3);
		
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
		AL(0.000002702f), CU(0.00000896f), FE(0.00000786f), OTHER(Float.NaN);
		
		private float density;
		
		private Material(float density) {
			this.density = density;
		}
		
		public float getDensity() {
			return this.density;
		}
	}
	
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
	
	public WorkPiece(final WorkPiece wp) {
		this.type = wp.getType();
		this.dimensions = new WorkPieceDimensions(wp.getDimensions().getLength(), wp.getDimensions().getWidth(), wp.getDimensions().getHeight());
		this.material = wp.getMaterial();
		this.weight = wp.getWeight();
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
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
		} else {
			setWeight(getDimensions().getVolume() * material.getDensity());
		}
	}
	
	//Occurs when reversalUnit.approachType = LEFT (X-as ligt naar voren)
	public void rotateDimensionsAroundX() {
		WorkPieceDimensions bckUpDimensions = new WorkPieceDimensions(dimensions.getLength(), dimensions.getWidth(), dimensions.getHeight());
		dimensions.setWidth(bckUpDimensions.getHeight());
		dimensions.setHeight(bckUpDimensions.getWidth());
	}
	
	//Occurs when reversalUnit.approachType = FRONT (Y-as ligt naar rechts)
	public void rotateDimensionsAroundY() {
		WorkPieceDimensions bckUpDimensions = new WorkPieceDimensions(dimensions.getLength(), dimensions.getWidth(), dimensions.getHeight());
		dimensions.setLength(bckUpDimensions.getHeight());
		dimensions.setHeight(bckUpDimensions.getLength());
	}
	
	public String toString() {
		return "WorkPiece: " + type + " - " + dimensions;
	}
}
