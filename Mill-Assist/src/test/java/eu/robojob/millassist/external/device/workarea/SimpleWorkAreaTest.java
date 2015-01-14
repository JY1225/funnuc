package eu.robojob.millassist.external.device.workarea;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.EFixtureType;
import eu.robojob.millassist.external.device.NoFreeClampingInWorkareaException;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.WorkAreaManager;
import eu.robojob.millassist.external.device.Clamping.Type;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.UserFrame;

public class SimpleWorkAreaTest {

	private Clamping clamping1, clamping2, clamping3, clamping4;
	private WorkAreaManager workAreaManager1;
	private SimpleWorkArea workAreaClone1, workAreaClone2, workAreaClone3, workAreaClone4, workAreaClone5;
	private DeviceSettings CNCDeviceSettings;
	
	@Before
	public void setUp() throws Exception {
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
		setUp_clampings();
	}
	
	private void setUp_clampings() {
		List<SimpleWorkArea> workAreas = new ArrayList<SimpleWorkArea>();
		workAreas.add(workAreaClone1);
		workAreas.add(workAreaClone2);
		workAreas.add(workAreaClone3);
		workAreas.add(workAreaClone4);
		workAreas.add(workAreaClone5);
		// We create a new process with at first only 1 workArea active (default clamping = CL1; related clamping = CL2)
		CNCDeviceSettings = new DeviceSettings(workAreas);
		CNCDeviceSettings.setDefaultClamping(workAreaClone1, clamping3);
		loadDeviceSettings(CNCDeviceSettings);
		workAreaClone1.setInUse(true);
	}

	@Test
	public void test_reserveClamping() throws NoFreeClampingInWorkareaException {
		workAreaClone1.getFreeActiveClamping(1);
		assertEquals(workAreaClone1.getDefaultClamping(), workAreaManager1.getActiveClamping(true, 1));
		assertEquals(workAreaClone1.getDefaultClamping(), workAreaManager1.getActiveClamping(false, 1));
		assertEquals(workAreaClone1.getDefaultClamping(), clamping3);
		assertEquals(workAreaClone1.getWorkAreaManager().getActiveClamping(true, 1), clamping3);
		assertEquals(workAreaClone1.getWorkAreaManager().getActiveClamping(false, 1), clamping3);
		assertTrue(clamping3.isInUse(1));
		assertTrue(clamping3.getProcessIdUsingClamping().contains(1));
		assertEquals(clamping3.getProcessIdUsingClamping().size(), 1);
	}
	
	@Test(expected = NoFreeClampingInWorkareaException.class)
	public void test_reserveClamping_fail() throws NoFreeClampingInWorkareaException {
		workAreaClone1.getFreeActiveClamping(1);
		assertEquals(workAreaClone1.getDefaultClamping(), workAreaManager1.getActiveClamping(true, 1));
		assertEquals(workAreaClone1.getDefaultClamping(), workAreaManager1.getActiveClamping(false, 1));
		assertEquals(workAreaClone1.getDefaultClamping(), clamping3);
		assertEquals(workAreaClone1.getWorkAreaManager().getActiveClamping(true, 1), clamping3);
		assertEquals(workAreaClone1.getWorkAreaManager().getActiveClamping(false, 1), clamping3);
		assertTrue(clamping3.isInUse(1));
		assertTrue(clamping3.getProcessIdUsingClamping().contains(1));
		assertEquals(clamping3.getProcessIdUsingClamping().size(), 1);
		workAreaClone1.getFreeActiveClamping(1);
	}
	
	@Test
	public void test_reserveRelatedClamping() throws NoFreeClampingInWorkareaException {
		workAreaClone1.getWorkAreaManager().setMaxClampingsToUse(2);
		clamping3.addRelatedClamping(clamping2);
		workAreaClone1.getFreeActiveClamping(1);
		assertTrue(clamping3.isInUse(1));
		assertFalse(clamping2.isInUse(1));
		workAreaClone1.getFreeActiveClamping(1);
		assertTrue(clamping2.isInUse(1));
		assertTrue(clamping3.isInUse(1));
		assertEquals(workAreaClone1.getNbClampingsPerProcessThread(1), 2);
		assertEquals(workAreaManager1.getMaxNbClampingOtherProcessThread(1), 0);
		assertEquals(clamping3, workAreaManager1.getActiveClamping(true, 1));
		assertEquals(clamping2, workAreaManager1.getActiveClamping(false, 1));
	}
	
	@Test(expected = NoFreeClampingInWorkareaException.class)
	public void test_reserveRelatedClamping_fail() throws NoFreeClampingInWorkareaException {
		clamping3.addRelatedClamping(clamping2);
		workAreaClone1.getFreeActiveClamping(1);
		assertTrue(clamping3.isInUse(1));
		assertFalse(clamping2.isInUse(1));
		workAreaClone1.getFreeActiveClamping(1);
	}
	
	@Test
	public void test_freeClampings() throws NoFreeClampingInWorkareaException {
		workAreaClone1.getWorkAreaManager().setMaxClampingsToUse(2);
		clamping3.addRelatedClamping(clamping2);
		workAreaClone1.getFreeActiveClamping(1);
		workAreaClone1.getFreeActiveClamping(1);
		workAreaManager1.freeClamping(1);
		assertFalse(clamping3.getProcessIdUsingClamping().contains(1));
		assertTrue(clamping2.getProcessIdUsingClamping().contains(1));
		workAreaManager1.freeClamping(1);
		assertFalse(clamping3.getProcessIdUsingClamping().contains(1));
		assertFalse(clamping2.getProcessIdUsingClamping().contains(1));

	}
	
	private void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setDefaultClamping(entry.getValue());
		}
	}

}
