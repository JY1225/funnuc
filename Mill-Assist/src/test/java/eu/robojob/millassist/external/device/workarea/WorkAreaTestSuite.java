package eu.robojob.millassist.external.device.workarea;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.robojob.millassist.external.device.ClampingTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	   WorkAreaManagerDeviceTest.class,
	   WorkAreaManagerProcessTest.class,
	   SimpleWorkAreaTest.class,
	   ClampingTest.class
	})

public class WorkAreaTestSuite {

}
