package eu.robojob.millassist.external.device.workarea;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.EFixtureType;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.WorkAreaManager;
import eu.robojob.millassist.external.device.Clamping.Type;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.UserFrame;

public class WorkAreaManagerProcessTest {

	//TODO - add test for 1 processflow / multiple processflows (using same workAreas, but different settings)
	//mss ook meerdere workAreaManagers (ufNr 4?)
	private Clamping clamping1, clamping2, clamping3, clamping4;
	private WorkAreaManager workAreaManager1;
	private SimpleWorkArea workAreaClone1, workAreaClone2, workAreaClone3, workAreaClone4, workAreaClone5;
	private DeviceSettings CNCDeviceSettings;

	@Before
	public void setUp() {
		deviceSetUp();
	}
	
	private void deviceSetUp() {
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
	
	private void setUp_OneWorkAreaInUse_OneClampInUse() {
		//deviceSettings
		Map<SimpleWorkArea, Clamping> defaultClampings = new HashMap<SimpleWorkArea, Clamping>();
		try {
			Clamping clonedClamp1 = clamping1.clone();
			// No related clampings
			defaultClampings.put(workAreaClone1, clonedClamp1);
			CNCDeviceSettings = new DeviceSettings(defaultClampings);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_OneWorkAreaInUse_OneClampInUse() {
		setUp_OneWorkAreaInUse_OneClampInUse();
		// TODO - test nog aan te vullen
	}
	
	@Test
	public void test_deviceSettings() {
		List<SimpleWorkArea> workAreas = new ArrayList<SimpleWorkArea>();
		workAreas.add(workAreaClone1);
		workAreas.add(workAreaClone2);
		workAreas.add(workAreaClone3);
		workAreas.add(workAreaClone4);
		workAreas.add(workAreaClone5);
		DeviceSettings deviceSettings = new DeviceSettings(workAreas);
		assertEquals(deviceSettings.getClampings().values().size(), 5);
		assertEquals(deviceSettings.getDefaultClamping(workAreaClone1), workAreaManager1.getWorkAreaWithSequence(1).getDefaultClamping());
	}
	
	@Test
	public void test_selectionClampings() {
		List<SimpleWorkArea> workAreas = new ArrayList<SimpleWorkArea>();
		workAreas.add(workAreaClone1);
		workAreas.add(workAreaClone2);
		workAreas.add(workAreaClone3);
		workAreas.add(workAreaClone4);
		workAreas.add(workAreaClone5);
		// We create a new process with at first only 1 workArea active (default clamping = CL1; related clamping = CL2)
		CNCDeviceSettings = new DeviceSettings(workAreas);
		CNCDeviceSettings.setDefaultClamping(workAreaClone1, clamping1);
		loadDeviceSettings(CNCDeviceSettings);
		assertEquals(CNCDeviceSettings.getDefaultClamping(workAreaClone1), clamping1);
		assertEquals(CNCDeviceSettings.getClampings().get(workAreaClone1), clamping1);
		assertEquals(workAreaClone1.getDefaultClamping(), clamping1);
		// Voeg clamping2 toe als related
		clamping1.addRelatedClamping(clamping2);
		assertEquals(clamping1.getRelatedClampings().size(), 1);
		assertTrue(clamping1.getRelatedClampings().contains(clamping2));
		// Als we een stap toevoegen wordt er een clone gemaakt van de clamping & als default gezet
		CNCDeviceSettings = new DeviceSettings(workAreas);
		assertEquals(CNCDeviceSettings.getDefaultClamping(workAreaClone1), clamping1);
		assertEquals(CNCDeviceSettings.getClampings().get(workAreaClone1), clamping1);
		assertEquals(workAreaClone1.getDefaultClamping(), clamping1);
		try {
			CNCDeviceSettings.setDefaultClamping(workAreaClone2, clamping1.clone());
			loadDeviceSettings(CNCDeviceSettings);
			assertEquals(CNCDeviceSettings.getDefaultClamping(workAreaClone2), clamping1);
			assertEquals(CNCDeviceSettings.getClampings().get(workAreaClone2), clamping1);
			assertEquals(workAreaClone2.getDefaultClamping(), clamping1);
			workAreaClone2.getDefaultClamping().addRelatedClamping(clamping3);
			workAreaClone2.getDefaultClamping().addRelatedClamping(clamping4);
			assertEquals(workAreaClone2.getDefaultClamping().getRelatedClampings().size(), 2);
			assertEquals(workAreaClone1.getDefaultClamping().getRelatedClampings().size(), 1);
			assertTrue(workAreaClone2.getDefaultClamping().getRelatedClampings().contains(clamping3));
			assertTrue(workAreaClone2.getDefaultClamping().getRelatedClampings().contains(clamping4));
			assertFalse(workAreaClone2.getDefaultClamping().getRelatedClampings().contains(clamping2));
			assertFalse(workAreaClone1.getDefaultClamping().getRelatedClampings().contains(clamping3));
			assertFalse(workAreaClone1.getDefaultClamping().getRelatedClampings().contains(clamping4));
			assertTrue(workAreaClone1.getDefaultClamping().getRelatedClampings().contains(clamping2));
			assertTrue(workAreaClone1.getAllActiveClampings().contains(clamping1));
			assertTrue(workAreaClone1.getAllActiveClampings().contains(clamping2));
			assertEquals(workAreaClone1.getAllActiveClampings().size(), 2);
			assertTrue(workAreaClone2.getAllActiveClampings().contains(clamping3));
			assertTrue(workAreaClone2.getAllActiveClampings().contains(clamping4));
			assertEquals(workAreaClone2.getAllActiveClampings().size(), 3);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setDefaultClamping(entry.getValue());
		}
	}
	
	@Test
	public void test_getActiveClamping() {
		assertEquals(workAreaClone1.getDefaultClamping(), workAreaManager1.getActiveClamping(true, 1));
		assertEquals(workAreaClone1.getDefaultClamping(), workAreaManager1.getActiveClamping(false, 1));

	}
}
