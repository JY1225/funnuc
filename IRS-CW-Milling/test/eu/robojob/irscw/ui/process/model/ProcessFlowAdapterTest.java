package eu.robojob.irscw.ui.process.model;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.irscw.external.device.processing.cnc.CNCMillingMachine;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;

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
		processFlow = new ProcessFlow("Test");
		/*Conveyor conveyor = new Conveyor("conveyor", null);
		EmbossingDevice embossing = new EmbossingDevice("embossing", null);*/
		CNCMillingMachine cncMilling = new CNCMillingMachine("cnc milling", null);
		FanucRobot robot = new FanucRobot("robot", null);
		put2 = new PutStep(robot, cncMilling, null, null);
		processing2 = new ProcessingStep(cncMilling, null);
		intervention = new InterventionStep(cncMilling, robot, null, 10);
		pick3 = new PickStep(robot, cncMilling, null, null);
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
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(0).getPutStep(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(0).getInterventionStepAfterPut(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(0).getInterventionStepBeforePick(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(0).getProcessingStep(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(1).getPickStep(), pick2);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(1).getPutStep(), put1);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(1).getInterventionStepAfterPut(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(1).getInterventionStepBeforePick(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(1).getProcessingStep(), processing1);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(2).getPickStep(), pick3);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(2).getPutStep(), put2);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(2).getInterventionStepAfterPut(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(2).getInterventionStepBeforePick(), intervention);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(2).getProcessingStep(), processing2);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(3).getPickStep(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(3).getPutStep(), put3);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(3).getInterventionStepAfterPut(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(3).getInterventionStepBeforePick(), null);
		Assert.assertEquals(processFlowAdapter.getDeviceInformation(3).getProcessingStep(), null);
	}
	
	@Test
	public void testGetTransportInformation() {
		Assert.assertEquals(processFlowAdapter.getTransportInformation(0).getPickStep(), pick1);
		Assert.assertEquals(processFlowAdapter.getTransportInformation(0).getPutStep(), put1);
		Assert.assertEquals(processFlowAdapter.getTransportInformation(1).getPickStep(), pick2);
		Assert.assertEquals(processFlowAdapter.getTransportInformation(1).getPutStep(), put2);
		Assert.assertEquals(processFlowAdapter.getTransportInformation(2).getPickStep(), pick3);
		Assert.assertEquals(processFlowAdapter.getTransportInformation(2).getPutStep(), put3);
	}

}
