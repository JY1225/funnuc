package eu.robojob.millassist.external.device;

import java.util.HashMap;

public enum EFixtureType {	
	DEFAULT(0) {
		public String toShortString() {
			return "";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 0;
		}
	}, 
	FIXTURE_1(1) {
		@Override
		public String toString() {
			return "Fixture 1";
		}
		public String toShortString() {
			return "Fix 1";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 1;
		}
	}, 
	FIXTURE_2(2) {
		@Override
		public String toString() {
			return "Fixture 2";
		}
		public String toShortString() {
			return "Fix 2";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 2;
		}
	},
	FIXTURE_3(3) {
		@Override
		public String toString() {
			return "Fixture 3";
		}
		public String toShortString() {
			return "Fix 3";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 3;
		}
	},
	FIXTURE_4(4) {
		@Override
		public String toString() {
			return "Fixture 4";
		}
		public String toShortString() {
			return "Fix 4";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 4;
		}
	},
	FIXTURE_1_2(12) {
		@Override
		public String toString() {
			return "Fixture 1 + 2";
		}
		public String toShortString() {
			return "Fix 1 + 2";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 2;
		}
	}, 
	FIXTURE_1_3(13) {
		@Override
		public String toString() {
			return "Fixture 1 + 3";
		}
		public String toShortString() {
			return "Fix 1 + 3";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 3;
		}
	}, 
	FIXTURE_1_4(14) {
		@Override
		public String toString() {
			return "Fixture 1 + 4";
		}
		public String toShortString() {
			return "Fix 1 + 4";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 4;
		}
	}, 
	FIXTURE_2_3(23) {
		@Override
		public String toString() {
			return "Fixture 2 + 3";
		}
		public String toShortString() {
			return "Fix 2 + 3";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 3;
		}
	}, 
	FIXTURE_2_4(24) {
		@Override
		public String toString() {
			return "Fixture 2 + 4";
		}
		public String toShortString() {
			return "Fix 2 + 4";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 4;
		}
	},  
	FIXTURE_3_4(34) {
		@Override
		public String toString() {
			return "Fixture 3 + 4";
		}
		public String toShortString() {
			return "Fix 3 + 4";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 4;
		}
	}, 
	FIXTURE_1_2_3(123) {
		@Override
		public String toString() {
			return "Fixture 1 + 2 + 3";
		}
		public String toShortString() {
			return "Fix 1 + 2 + 3";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 3;
		}
	}, 
	FIXTURE_1_2_3_4(1234) {
		@Override
		public String toString() {
			return "Fixture 1 + 2 + 3 + 4";
		}
		public String toShortString() {
			return "Fix 1 + 2 + 3 + 4";
		}
		@Override
		public int getHighestNbOfFixtureUsed() {
			return 4;
		}
	};
	
	private int code;
	private static HashMap<Integer, EFixtureType> codeValueMap = new HashMap<Integer, EFixtureType>();
	
	private EFixtureType(int c) {
		code = c;
	}
	
	public int getCode() {
		return code;
	}
	
	public abstract String toShortString();
	
	static
	{
		for (EFixtureType type : EFixtureType.values())
		{
			codeValueMap.put(type.getCode(), type);
		}
	}
	
	public static EFixtureType getFixtureTypeFromStringValue(String name) {
		for (EFixtureType type : EFixtureType.values()) {
			if(name.equals(type.toString()))
				return type;
		}
		throw new IllegalArgumentException("No fixtureType found with name " + name + ". ");
	}
	
	public static EFixtureType getFixtureTypeFromCodeValue(int codeValue)
	{
		return codeValueMap.get(codeValue);
	}
	
	public int nbFixtures() {
		return String.valueOf(getCode()).length();
	}
	
	/**
	 * Get the highest number of fixture that is used in the configuration. E.g. FIXTURE_1_2 has 2 as highest fixture while FIX_4 has 4.
	 *
	 * @return The highest fixture number used in the given configuration
	 */
	public abstract int getHighestNbOfFixtureUsed();
}