package eu.robojob.millassist.external.device;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.millassist.external.device.Clamping.Type;
import eu.robojob.millassist.positioning.Coordinates;

public class ClampingTest {

	private Clamping clamping1, clamping2;
	
	@Before
	public void setUp() throws Exception {
		clamping1 = new Clamping(Type.CENTRUM, "CLAMPING 1", 20.0f, new Coordinates(), new Coordinates(), null, EFixtureType.FIXTURE_1);
		clamping1.setId(20);
		clamping2 = new Clamping(Type.CENTRUM, "CLAMPING 2", 20.0f, new Coordinates(), new Coordinates(), null, EFixtureType.FIXTURE_2);
		clamping2.setId(39);
	}

	@Test
	public void test_gettersSetters() {
		assertEquals(clamping1.getType(), Type.CENTRUM);
		assertEquals(clamping1.getName(), "CLAMPING 1");
		assertEquals(clamping1.getDefaultHeight(), 20.0f, 0);
		assertEquals(clamping1.getImageUrl(), null);
		assertEquals(clamping1.getRelatedClampings().size(), 0);
		assertTrue(clamping1.getRelatedClampings().isEmpty());
		assertEquals(clamping1.getProcessIdUsingClamping().size(), 0);
		assertTrue(clamping1.getProcessIdUsingClamping().isEmpty());
	}
	
	@Test
	public void test_clampingClone_init() {
		try {
			Clamping clonedClamping = clamping1.clone();
			assertEquals(clamping1, clonedClamping);
			assertEquals(clonedClamping.getId(), clonedClamping.getId());
			assertEquals(clonedClamping.getId(), 20);
			assertEquals(clonedClamping.getName(), "CLAMPING 1");
			assertEquals(clonedClamping.getType(), Type.CENTRUM);
			assertEquals(clonedClamping.getFixtureType(), EFixtureType.FIXTURE_1);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_clampingClone_relatedClamp() {
		try {
			clamping1.addRelatedClamping(clamping2);
			Clamping clonedClamping = clamping1.clone();
			assertTrue(clonedClamping.getRelatedClampings().isEmpty());
			assertFalse(clamping1.getRelatedClampings().isEmpty());
			assertEquals(clamping1.getRelatedClampings().size(), 1);
			assertEquals(clonedClamping.getRelatedClampings().size(), 0);
			clamping1.addProcessIdUsingClamping(0);
			assertFalse(clonedClamping.getProcessIdUsingClamping().contains(0));
			assertTrue(clonedClamping.getProcessIdUsingClamping().isEmpty());
			assertTrue(clamping1.getProcessIdUsingClamping().contains(0));
			assertFalse(clamping1.getProcessIdUsingClamping().isEmpty());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

}
