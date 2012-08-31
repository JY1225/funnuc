package eu.robojob.irscw.ui.process.model;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.irscw.external.device.CNCMillingMachine;
import eu.robojob.irscw.external.device.Conveyor;
import eu.robojob.irscw.external.device.EmbossingDevice;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;

public class ProcessFlowAdapterTest {

	private ProcessFlow processFlow;
	private ProcessFlowAdapter processFlowAdapter;
	PickStep pick1;
	PickStep pick2;
	PickStep pick3;
	PutStep put1;
	PutStep put2;
	PutStep put3;
	ProcessingStep processing1;
	ProcessingStep processing2;
	InterventionStep intervention;
	
	@Before
	public void setUp() throws Exception {
		processFlow = new ProcessFlow();
		Conveyor conveyor = new Conveyor("conveyor", null);
		EmbossingDevice embossing = new EmbossingDevice("embossing", null);
		CNCMillingMachine cncMilling = new CNCMillingMachine("cnc milling", null);
		FanucRobot robot = new FanucRobot("robot", null);
		pick1 = new PickStep(robot, null, conveyor, null, null);
		put1 = new PutStep(robot, null, embossing, null, null);
		processing1 = new ProcessingStep(embossing, null);
		pick2 = new PickStep(robot, null, embossing, null, null);
		put2 = new PutStep(robot, null, cncMilling, null, null);
		processing2 = new ProcessingStep(cncMilling, null);
		intervention = new InterventionStep(cncMilling, null, 10);
		pick3 = new PickStep(robot, null, cncMilling, null, null);
		put3 = new PutStep(robot, null, conveyor, null, null);
		processFlow.addStep(pick1);
		processFlow.addStep(put1);
		processFlow.addStep(processing1);
		processFlow.addStep(pick2);
		processFlow.addStep(put2);
		processFlow.addStep(processing2);
		processFlow.addStep(intervention);
		processFlow.addStep(pick3);
		processFlow.addStep(put3);
		processFlowAdapter = new ProcessFlowAdapter(processFlow); 
	}

	@Test
	public void testGetDeviceStepCount() {
		Assert.assertEquals(processFlowAdapter.getDeviceStepCount(), 4);
	}
	
	@Test
	public void testGetTransportStepCount() {
		Assert.assertEquals(processFlowAdapter.getTransportStepCount(), 3);
	}
	
	@Test
	public void testGetDeviceInformation() {
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(0).getPickStep(), pick1);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(0).getPutStep(), put1);
	}

}
