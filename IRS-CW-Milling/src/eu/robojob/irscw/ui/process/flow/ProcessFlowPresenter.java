package eu.robojob.irscw.ui.process.flow;

import eu.robojob.irscw.external.device.CNCMillingMachine;
import eu.robojob.irscw.external.device.Conveyor;
import eu.robojob.irscw.external.device.EmbossingDevice;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.process.configure.ConfigurePresenter;

public class ProcessFlowPresenter {

	private ProcessFlowView view;
	private ConfigurePresenter parent;
	
	public ProcessFlowPresenter(ProcessFlowView view) {
		this.view = view;
		view.setPresenter(this);
		createFakeTest();
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}
	
	public ProcessFlowView getView() {
		return view;
	}
	
	public void deviceClicked(String id) {
		
	}
	
	public void transportClicked(String id) {
		
	}
	
	private void createFakeTest() {
		ProcessFlow processFlow = new ProcessFlow();
		Conveyor conveyor = new Conveyor("conveyor", null);
		EmbossingDevice embossing = new EmbossingDevice("embossing", null);
		CNCMillingMachine cncMilling = new CNCMillingMachine("cnc milling", null);
		FanucRobot robot = new FanucRobot("robot", null);
		PickStep pick1 = new PickStep(robot, null, conveyor, null, null);
		PutStep put1 = new PutStep(robot, null, embossing, null, null);
		ProcessingStep processing1 = new ProcessingStep(embossing, null);
		PickStep pick2 = new PickStep(robot, null, embossing, null, null);
		PutStep put2 = new PutStep(robot, null, cncMilling, null, null);
		ProcessingStep processing2 = new ProcessingStep(cncMilling, null);
		InterventionStep intervention = new InterventionStep(cncMilling, null, 10);
		PickStep pick3 = new PickStep(robot, null, cncMilling, null, null);
		PutStep put3 = new PutStep(robot, null, conveyor, null, null);
		processFlow.addStep(pick1);
		processFlow.addStep(put1);
		processFlow.addStep(processing1);
		processFlow.addStep(pick2);
		processFlow.addStep(put2);
		processFlow.addStep(processing2);
		processFlow.addStep(intervention);
		processFlow.addStep(pick3);
		processFlow.addStep(put3);
		view.setProcessFlow(processFlow);
	}
}
