package eu.robojob.millassist.external.device.workarea;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.EFixtureType;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.WorkAreaManager;
import eu.robojob.millassist.external.device.Clamping.Type;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.UserFrame;

public class WorkAreaManagerDeviceTest {
	
	private Clamping clamping1, clamping2, clamping3, clamping4;
	private WorkAreaManager workAreaManager1;
	private SimpleWorkArea workAreaClone1, workAreaClone2, workAreaClone3, workAreaClone4, workAreaClone5;

	@Before
	public void setUp() {
		// This is the setup like it would be if it was read from the database
		clamping1 = new Clamping(Type.CENTRUM, "CLAMPING FIX1", 10.0f, new Coordinates(), new Coordinates(), null, EFixtureType.FIXTURE_1);
		clamping1.setId(1);
		clamping2 = new Clamping(Type.CENTRUM, "CLAMPING FIX2", 20.0f, new Coordinates(), new Coordinates(), null, EFixtureType.FIXTURE_2);
		clamping2.setId(2);
		clamping3 = new Clamping(Type.CENTRUM, "CLAMPING FIX3", 30.0f, new Coordinates(), new Coordinates(), null, EFixtureType.FIXTURE_3);
		clamping3.setId(3);
		clamping4 = new Clamping(Type.CENTRUM, "CLAMPING FIX4", 40.0f, new Coordinates(), new Coordinates(), null, EFixtureType.FIXTURE_4);
		clamping4.setId(4);
		Set<Clamping> possibleClampings = new HashSet<Clamping>();
		possibleClampings.add(clamping1);
		possibleClampings.add(clamping2);
		possibleClampings.add(clamping3);
		possibleClampings.add(clamping4);
		UserFrame userFrame = new UserFrame(3, "CNC Machine", 20.0f, new Coordinates());
		workAreaManager1 = new WorkAreaManager(userFrame, possibleClampings);
		workAreaClone1 = new SimpleWorkArea(workAreaManager1, "WA1", 1);
		workAreaClone2 = new SimpleWorkArea(workAreaManager1, "WA1 REV1", 2);
		workAreaClone3 = new SimpleWorkArea(workAreaManager1, "WA1 REV2", 3);
		workAreaClone4 = new SimpleWorkArea(workAreaManager1, "WA1 REV3", 4);
		workAreaClone5 = new SimpleWorkArea(workAreaManager1, "WA1 REV4", 5);
	}

	@Test
	public void test_setUp() {
		assertTrue(workAreaManager1.getWorkAreas().containsValue(workAreaClone1));
		assertTrue(workAreaManager1.getWorkAreas().containsValue(workAreaClone2));
		assertTrue(workAreaManager1.getWorkAreas().containsValue(workAreaClone3));
		assertTrue(workAreaManager1.getWorkAreas().containsValue(workAreaClone4));
		assertTrue(workAreaManager1.getWorkAreas().containsValue(workAreaClone5));
		assertEquals(workAreaClone1.getWorkAreaManager(), workAreaManager1);
		assertEquals(workAreaClone2.getWorkAreaManager(), workAreaManager1);
		assertEquals(workAreaClone3.getWorkAreaManager(), workAreaManager1);
		assertEquals(workAreaClone4.getWorkAreaManager(), workAreaManager1);
		assertEquals(workAreaClone5.getWorkAreaManager(), workAreaManager1);
		assertFalse(workAreaManager1.isInUse());
		assertEquals(workAreaManager1.getUserFrame().getNumber(), 3);
		assertEquals(workAreaManager1.getWorkAreaNr(), 1);
	}
	
	@Test
	public void test_WorkAreaManager_getClampingById() {
		Clamping testClamping = workAreaManager1.getClampingById(2);
		assertEquals(testClamping, clamping2);
		Clamping testClamping2 = workAreaManager1.getClampingById(4);
		assertEquals(testClamping2, clamping4);
		Clamping testClamping3 = workAreaManager1.getClampingById(1);
		assertEquals(testClamping3, clamping1);
		Clamping testClamping4 = workAreaManager1.getClampingById(3);
		assertEquals(testClamping4, clamping3);
		assertEquals(workAreaManager1.getClampings().size(), 4);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_WorkAreaManager_getClampingById_fail() {
		workAreaManager1.getClampingById(5);
	}
	
	@Test
	public void test_WorkAreaManager_getClampingByName() {
		Clamping testClamping = workAreaManager1.getClampingByName("CLAMPING FIX2");
		assertEquals(testClamping, clamping2);
		assertEquals("CLAMPING FIX2", testClamping.getName());
		Clamping testClamping2 = workAreaManager1.getClampingByName("CLAMPING FIX4");
		assertEquals(testClamping2, clamping4);
		assertEquals("CLAMPING FIX4", testClamping2.getName());
		Clamping testClamping3 = workAreaManager1.getClampingByName("CLAMPING FIX1");
		assertEquals(testClamping3, clamping1);
		assertEquals("CLAMPING FIX1", testClamping3.getName());
		Clamping testClamping4 = workAreaManager1.getClampingByName("CLAMPING FIX3");
		assertEquals(testClamping4, clamping3);
		assertEquals("CLAMPING FIX3", testClamping4.getName());
		assertEquals(workAreaManager1.getClampings().size(), 4);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_WorkAreaManager_getClampingByName_fail() {
		workAreaManager1.getClampingByName("Test clamping");
	}
	
	@Test
	public void test_WorkAreaManager_getClampingNames() {
		assertTrue(workAreaManager1.getClampingNames().contains(clamping1.getName()));
		assertTrue(workAreaManager1.getClampingNames().contains(clamping2.getName()));
		assertTrue(workAreaManager1.getClampingNames().contains(clamping3.getName()));
		assertTrue(workAreaManager1.getClampingNames().contains(clamping4.getName()));
		assertEquals(workAreaManager1.getClampingNames().size(), 4);
	}
	
	@Test
	public void test_WorkAreaManager_getWorkAreaWithSequence() {
		assertEquals(workAreaClone1, workAreaManager1.getWorkAreaWithSequence(1));
		assertEquals(workAreaClone2, workAreaManager1.getWorkAreaWithSequence(2));
		assertEquals(workAreaClone3, workAreaManager1.getWorkAreaWithSequence(3));
		assertEquals(workAreaClone4, workAreaManager1.getWorkAreaWithSequence(4));
		assertEquals(workAreaClone5, workAreaManager1.getWorkAreaWithSequence(5));
		assertEquals(workAreaManager1.getWorkAreas().size(), 5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_WorkAreaManager_getWorkAreaWithSequence_fail() {
		workAreaManager1.getWorkAreaWithSequence(10);
	}
}
