package eu.robojob.millassist.external.device;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.positioning.Coordinates;

public class Clamping {
	
	public static enum Type {
		CENTRUM {
			@Override
			public String toString() {
				return "Centrum";
			}
		}, 
		FIXED_XP {
			@Override
			public String toString() {
				return "Fix X +";
			}
		}, 
		FIXED_XM {
			@Override
			public String toString() {
				return "Fix X -";
			}
		}, 
		FIXED_YP {
			@Override
			public String toString() {
				return "Fix Y +";
			}
		}, 
		FIXED_YM {
			@Override
			public String toString() {
				return "Fix Y -";
			}
		}, 
		NONE
	} 
	
	private int id;
	private String name;
	private Coordinates relativePosition;
	private Coordinates smoothToPoint;
	private Coordinates smoothFromPoint;
	private EFixtureType fixtureType; 
	private float height;
	private float defaultHeight;
	private String imageURL;
	private Type type;
	// Process ID that is currently located in the clamping - default value = -1
	// In case of dualLoad, we can have 'two' workpieces in 'one' clamping
	private Set<Integer> prcIdUsingClamping;
	// Related clampings that are currently active for use
	private Set<Clamping> relatedClampings;
	// Default
	private int nbOfPossibleWPToStore = 1;
	
	public Clamping(final Type type, final String name, final float defaultHeight, final Coordinates relativePosition, final Coordinates smoothToPoint,
			final Coordinates smoothFromPoint, final String imageURL, final EFixtureType fixtureType) {
		this.name = name;
		this.height = defaultHeight;
		this.defaultHeight = defaultHeight;
		this.relativePosition = relativePosition;
		this.smoothToPoint = smoothToPoint;
		this.smoothFromPoint = smoothFromPoint;
		this.imageURL = imageURL;
		this.prcIdUsingClamping = new HashSet<Integer>();
		this.relatedClampings = new HashSet<Clamping>();
		this.type = type;
		this.fixtureType = fixtureType;
	}

	public Clamping(final Type type, final String name, final float defaultHeight, final Coordinates relativePosition, final Coordinates smoothPoint, final String imageURL, final EFixtureType fixtureType) {
		this(type, name, defaultHeight, relativePosition, smoothPoint, smoothPoint, imageURL, fixtureType);
	}
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public void addRelatedClamping(final Clamping clamping) {
		relatedClampings.add(clamping);
	}
	
	public void removeRelatedClamping(final Clamping clamping) {
		relatedClampings.remove(clamping);
	}
	
	public Set<Clamping> getRelatedClampings() {
		return relatedClampings;
	}
	
	public void setRelatedClampings(final Set<Clamping> tobeRelatedClampings) {
		this.relatedClampings = tobeRelatedClampings;
	}
	
	public EFixtureType getFixtureType() {
		return fixtureType;
	}

	public void setFixtureType(EFixtureType fixtureType) {
		this.fixtureType = fixtureType;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public Coordinates getRelativePosition() {
		return relativePosition;
	}

	public void setRelativePosition(final Coordinates relativePosition) {
		this.relativePosition = relativePosition;
	}

	public String getImageUrl() {
		return imageURL;
	}

	public void setImageUrl(final String imageURL) {
		this.imageURL = imageURL;
	}

	public Coordinates getSmoothToPoint() {
		return smoothToPoint;
	}

	public void setSmoothToPoint(final Coordinates smoothToPoint) {
		this.smoothToPoint = smoothToPoint;
	}

	public Coordinates getSmoothFromPoint() {
		return smoothFromPoint;
	}

	public void setSmoothFromPoint(final Coordinates smoothFromPoint) {
		this.smoothFromPoint = smoothFromPoint;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}
	
	public float getDefaultHeight() {
		return defaultHeight;
	}

	public void setDefaultHeight(final float defaultHeight) {
		this.defaultHeight = defaultHeight;
	}
	
	public void resetHeightToDefault() {
		this.height = defaultHeight;
	}
	
	public int getNbPossibleWPToStore() {
		return this.nbOfPossibleWPToStore;
	}
	
	public void setNbPossibleWPToStore(final int nbWPToStore) {
		this.nbOfPossibleWPToStore = nbWPToStore;
	}
	
	public synchronized Set<Integer> getProcessIdUsingClamping() {
		return this.prcIdUsingClamping;
	}
	
	public synchronized void addProcessIdUsingClamping(int id) {
		this.prcIdUsingClamping.add(id);
	}
	
	public synchronized boolean isInUse(int processId) {
		if(prcIdUsingClamping.contains(processId)) {
			return true;
		}
		return (prcIdUsingClamping.size() >= nbOfPossibleWPToStore);
	}
}
